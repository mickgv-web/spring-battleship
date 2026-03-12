# Battleships · Architecture Overview

## 1. High-Level Architecture

The application is a Spring Boot multiplayer game using:

- Spring MVC → HTTP endpoints (lobby, create/join game)
- Spring Security → authentication
- Spring WebSocket + STOMP → real-time gameplay
- Thymeleaf → server-rendered pages
- JavaScript → game UI
- MySQL + JPA → persistent game metadata

The game logic is split between:

Backend (authoritative game state)
Frontend (UI + user interaction)
WebSocket (synchronization layer)


---

# 2. System Flow

## Lobby Flow

User logs in
↓
GET /lobby
↓
GameController
↓
GameService.findOpenGames()
↓
Games displayed


---

## Create Game

POST /games/create
↓
GameController.createGame()
↓
GameService.createGame()
↓
Game saved in DB
↓
Redirect → /games/{code}


---

## Join Game

POST /games/join
↓
GameController.joinGame()
↓
GameService.joinGame()
↓
GameManager.createSession()
↓
GameSession created in memory
↓
WebSocket event → PLAYER_JOINED


---

# 3. Game Runtime Architecture

## Core Components

### GameManager

Responsible for managing active games in memory.

GameManager
Map<Long, GameSession>

Responsibilities:

createSession()
getSession()
removeSession()

Game sessions are NOT persisted — only stored in memory.


---

### GameSession

Represents one active game.

GameSession
├─ gameId
├─ player1Id
├─ player2Id
├─ player1Board
├─ player2Board
└─ currentTurnPlayerId

Responsibilities:

placeShip()
fire()
switchTurn()
isGameOver()
getWinner()
getBoardForPlayer()

GameSession is the core game engine.


---

### Board

Represents a 10x10 battleship grid.

Board
├─ Cell[][] grid

Cells can be:

EMPTY
SHIP
HIT
MISS
SUNK

Responsibilities:

placeShip()
fire()
allShipsSunk()
isShipSunk()


---

# 4. WebSocket Architecture

## Connection

Client
↓
SockJS
↓
STOMP
↓
/ws endpoint


---

## Topics

### Game events

/topic/game/{gameId}

Broadcast events:

PLAYER_JOINED
BOARD_STATE
FIRE events


---

## Message Flow

### Client → Server

/app/board
/app/fire


---

### Server → Client

/topic/game/{gameId}

Messages:

BoardStateEvent
FireResponse
GameEvent


---

# 5. WebSocket Controller

## GameSocketController

Handles real-time gameplay.

### Request Board

@MessageMapping("/board")

Flow:

Client sends /app/board
↓
GameSocketController
↓
GameSession.getBoardForPlayer()
↓
BoardStateEvent
↓
/topic/game/{gameId}


---

### Fire

@MessageMapping("/fire")

Flow:

Client sends /app/fire
↓
GameSession.fire()
↓
Board.fire()
↓
FireResponse
↓
Broadcast to /topic/game/{gameId}


---

# 6. Frontend Architecture

## Files

/static/js
├─ websocket.js
├─ board.js
└─ game.js


---

## websocket.js

Handles the WebSocket layer.

Responsibilities:

connectWebSocket()
requestBoard()
sendFire()
subscribe to topics

Flow:

connect
↓
subscribe /topic/game/{id}
↓
requestBoard()


---

## board.js

Responsible for rendering boards.

Functions:

createBoard(containerId)
renderBoard(containerId, board)
updateCell(containerId, x, y, result)


---

## game.js

Main client controller.

Responsibilities:

handleServerEvent()
handleCellClick()
startGame()
initGame()

Controls:

turn validation
board rendering
user interaction


---

# 7. Game Lifecycle

## WAITING

Game created
Host waiting for opponent

UI:

Waiting for opponent...


---

## SETUP

Second player joined
Boards generated

Events:

PLAYER_JOINED
BOARD_STATE


---

## PLAYING

Players fire shots
Turns alternate

Flow:

Player click
↓
sendFire()
↓
GameSession.fire()
↓
FireResponse
↓
updateCell()


---

## FINISHED

All ships sunk
Winner determined

Future improvement:

GAME_OVER event


---

# 8. Current Data Flow Example

### Player fires

click enemy cell
↓
sendFire()
↓
/app/fire
↓
GameSocketController
↓
GameSession.fire()
↓
Board.fire()
↓
FireResponse
↓
/topic/game/{gameId}
↓
handleServerEvent()
↓
updateCell()


---

# 9. Security Model

Authentication handled by:

Spring Security
UserDetailsService

Principal available in WebSocket controllers:

Principal principal
principal.getName()

Used to identify the player.


---

# 10. Current Limitations

The current implementation still lacks:

### Ship placement phase

Currently ships are hardcoded:

placeDefaultShips()

Future phase:

drag & drop ships
send placement via websocket


---

### Game persistence

Currently:

GameSession lives only in memory

Server restart → game lost.


---

### Spectators

Not supported yet.


---

### Reconnect recovery

Reloading the page may lose UI state.


---

# 11. Future Improvements

Recommended roadmap:

1. Ship placement phase
2. GameOver events
3. UI improvements
4. Reconnect support
5. Game persistence


---

# 12. Project Structure

src
├─ controller
│    ├─ GameController
│    └─ GameSocketController
│
├─ service
│    └─ GameService
│
├─ game
│    ├─ GameManager
│    ├─ GameSession
│    ├─ Board
│    └─ Cell
│
├─ entity
│    ├─ Game
│    └─ User
│
├─ repository
│    ├─ GameRepository
│    └─ UserRepository
│
└─ dto
├─ FireRequest
├─ FireResponse
├─ BoardStateEvent
└─ GameEvent


---

# 13. Mental Model

The system follows this pattern:

Database
↓
Lobby management

GameSession (memory)
↓
Game logic

WebSocket
↓
State synchronization

Frontend
↓
UI rendering