const gameId = window.gameConfig.gameId;
const playerId = window.gameConfig.playerId;
let gameStatus = window.gameConfig.status;

let boardInitialized = false;
let currentTurnPlayerId = null;

// 🔹 guardamos el board si llega antes de crear el DOM
let pendingBoardState = null;

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

    sendFire(gameId, x, y);

}

function handleServerEvent(data){

    console.log("SERVER EVENT:", data);

    // 🔹 jugador se unió
    if(data.type === "PLAYER_JOINED"){

        gameStatus = "SETUP";

        startGame();

        return;

    }

    // 🔹 estado inicial del tablero
    if(data.board){

        console.log("BOARD STATE RECEIVED");

        currentTurnPlayerId = data.currentTurnPlayerId;

        // si el tablero aún no existe lo guardamos
        if(!boardInitialized){

            pendingBoardState = data.board;

        } else {

            renderBoard("player-board", data.board);

        }

        return;

    }

    // 🔹 actualización de turno
    if(data.nextTurnPlayerId !== undefined){

        currentTurnPlayerId = data.nextTurnPlayerId;

    }

    // 🔹 evento de disparo
    if(data.x !== undefined){

        if(data.shooterId === playerId){

            updateCell("enemy-board", data.x, data.y, data.result);

        } else {

            updateCell("player-board", data.x, data.y, data.result);

        }

    }

}

function showWaiting(){

    const status = document.getElementById("game-status");

    if(status){
        status.innerHTML = "Waiting for opponent...";
    }

}

function startGame(){

    if(boardInitialized){
        return;
    }

    const status = document.getElementById("game-status");

    if(status){
        status.innerHTML = "";
    }

    createBoard("player-board", null);

    createBoard("enemy-board", handleCellClick);

    boardInitialized = true;

    // 🔹 si el estado del tablero llegó antes lo pintamos ahora
    if(pendingBoardState){

        renderBoard("player-board", pendingBoardState);

        pendingBoardState = null;

    }

}

function initGame(){

    if(gameStatus === "WAITING"){

        showWaiting();

    } else {

        startGame();

    }

    // conectar websocket después de tener el DOM listo
    connectWebSocket(gameId, handleServerEvent);

}

initGame();