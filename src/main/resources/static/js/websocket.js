let stompClient = null;

function connectWebSocket(gameId, onMessage){

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(){

        console.log("WebSocket connected");

        // 🔹 eventos del juego (disparos)
        stompClient.subscribe('/topic/game/' + gameId, function(message){

            const data = JSON.parse(message.body);

            console.log("GAME EVENT:", data);

            onMessage(data);

        });

        // 🔹 estado del tablero del jugador
        stompClient.subscribe('/user/queue/board', function(message){

            const data = JSON.parse(message.body);

            console.log("BOARD STATE RECEIVED:", data);

            onMessage(data);

        });

        // 🔹 pedir el tablero al servidor cuando el socket está listo
        requestBoard(gameId);

    });

}

function requestBoard(gameId){

    if(!stompClient || !stompClient.connected){
        console.warn("WebSocket not ready yet");
        return;
    }

    stompClient.send(
        "/app/board",
        {},
        JSON.stringify({
            gameId: gameId
        })
    );

}

function sendFire(gameId, x, y){

    if(!stompClient || !stompClient.connected){
        console.warn("WebSocket not connected");
        return;
    }

    stompClient.send(
        "/app/fire",
        {},
        JSON.stringify({
            gameId: gameId,
            x: x,
            y: y
        })
    );

}