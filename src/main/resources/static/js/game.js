const gameId = window.gameConfig.gameId;
const playerId = window.gameConfig.playerId;
let gameStatus = window.gameConfig.status;

let boardInitialized = false;
let currentTurnPlayerId = null;

// guardamos board si llega antes del DOM
let pendingBoardState = null;


/* ===========================
   EVENT LOG
=========================== */

function logEvent(message, type){

    const log = document.getElementById("event-log");

    if(!log){
        return;
    }

    const entry = document.createElement("div");

    entry.classList.add("event-entry");

    if(type){
        entry.classList.add("event-" + type.toLowerCase());
    }

    entry.textContent = message;

    log.prepend(entry);

}


/* ===========================
   TURN INDICATOR
=========================== */

function updateTurnIndicator(){

    const status = document.getElementById("game-status");

    if(!status){
        return;
    }

    if(currentTurnPlayerId === playerId){

        status.textContent = "🟢 YOUR TURN — Fire!";
        status.className = "turn-indicator your-turn";

    } else {

        status.textContent = "⏳ ENEMY TURN — Waiting...";
        status.className = "turn-indicator enemy-turn";

    }

}


/* ===========================
   CELL CLICK
=========================== */

function handleCellClick(x, y){

    if(gameStatus !== "SETUP" && gameStatus !== "PLAYING"){
        return;
    }

    if(currentTurnPlayerId !== playerId){
        console.log("Not your turn");
        return;
    }

    const cell = document.querySelector(
        `#enemy-board .cell[data-x='${x}'][data-y='${y}']`
    );

    if(cell.classList.contains("hit") || cell.classList.contains("miss")){
        return;
    }

    logEvent("Firing at (" + x + "," + y + ")...");

    sendFire(gameId, x, y);

}


/* ===========================
   SERVER EVENTS
=========================== */

function handleServerEvent(data){

    console.log("SERVER EVENT:", data);


    /* PLAYER JOINED */

    if(data.type === "PLAYER_JOINED"){

        gameStatus = "SETUP";

        logEvent("Opponent joined the game");

        startGame();

        return;

    }


    /* BOARD STATE */

    if(data.board){

        console.log("BOARD STATE RECEIVED");

        currentTurnPlayerId = data.currentTurnPlayerId;

        logEvent("Board synchronized");

        updateTurnIndicator();

        if(!boardInitialized){

            pendingBoardState = data.board;

        } else {

            renderBoard("player-board", data.board);

        }

        return;

    }


    /* TURN UPDATE */

    if(data.nextTurnPlayerId !== undefined){

        currentTurnPlayerId = data.nextTurnPlayerId;

        updateTurnIndicator();

    }


    /* FIRE EVENT */

    if(data.x !== undefined){

        const coords = "(" + data.x + "," + data.y + ")";

        if(data.shooterId === playerId){

            updateCell("enemy-board", data.x, data.y, data.result);

            logEvent("You fired at " + coords);

        } else {

            updateCell("player-board", data.x, data.y, data.result);

            logEvent("Enemy fired at " + coords);

        }

        logEvent("Result: " + data.result);

    }

}


/* ===========================
   WAITING SCREEN
=========================== */

function showWaiting(){

    const status = document.getElementById("game-status");

    if(status){

        status.textContent = "Waiting for opponent...";

    }

}


/* ===========================
   START GAME
=========================== */

function startGame(){

    if(boardInitialized){
        return;
    }

    const status = document.getElementById("game-status");

    if(status){
        status.textContent = "";
    }

    createBoard("player-board", null);

    createBoard("enemy-board", handleCellClick);

    boardInitialized = true;

    if(pendingBoardState){

        renderBoard("player-board", pendingBoardState);

        pendingBoardState = null;

    }

}


/* ===========================
   INIT
=========================== */

function initGame(){

    if(gameStatus === "WAITING"){

        showWaiting();

    } else {

        startGame();

    }

    connectWebSocket(gameId, handleServerEvent);

}

initGame();