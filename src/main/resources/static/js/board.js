function createBoard(containerId, onCellClick){

    const board = document.getElementById(containerId);

    board.innerHTML = "";

    board.classList.add("game-board");


    /* ---------- column labels ---------- */

    const letters = ["A","B","C","D","E","F","G","H","I","J"];

    const corner = document.createElement("div");
    corner.classList.add("board-corner");
    board.appendChild(corner);

    for(let i=0;i<10;i++){

        const label = document.createElement("div");
        label.classList.add("board-label");
        label.textContent = letters[i];

        board.appendChild(label);

    }


    /* ---------- rows ---------- */

    for(let x=0;x<10;x++){

        const rowLabel = document.createElement("div");

        rowLabel.classList.add("board-label");
        rowLabel.textContent = x + 1;

        board.appendChild(rowLabel);

        for(let y=0;y<10;y++){

            const cell = document.createElement("div");

            cell.classList.add("cell");

            cell.dataset.x = x;
            cell.dataset.y = y;

            if(onCellClick){

                cell.onclick = function(){

                    if(cell.classList.contains("hit") ||
                       cell.classList.contains("miss") ||
                       cell.classList.contains("sunk")){
                        return;
                    }

                    onCellClick(x,y);

                };

            }

            board.appendChild(cell);

        }

    }

}



/* ===========================
   RENDER BOARD
=========================== */

function renderBoard(containerId, board){

    for(let x=0;x<10;x++){

        for(let y=0;y<10;y++){

            const value = board[x][y];

            const cell = document.querySelector(
                `#${containerId} .cell[data-x='${x}'][data-y='${y}']`
            );

            if(!cell){
                continue;
            }

            /* limpiar estado anterior */

            cell.classList.remove("ship","hit","miss","sunk",
                                  "ship-left","ship-right","ship-top","ship-bottom");

            if(value === "SHIP"){
                cell.classList.add("ship");
            }

            if(value === "HIT"){
                cell.classList.add("hit");
            }

            if(value === "MISS"){
                cell.classList.add("miss");
            }

            if(value === "SUNK"){
                cell.classList.add("sunk");
            }

        }

    }

    connectShips(containerId, board);

}



/* ===========================
   CONNECT SHIPS
=========================== */

function connectShips(containerId, board){

    for(let x=0;x<10;x++){

        for(let y=0;y<10;y++){

            if(board[x][y] !== "SHIP" &&
               board[x][y] !== "HIT" &&
               board[x][y] !== "SUNK"){
                continue;
            }

            const cell = document.querySelector(
                `#${containerId} .cell[data-x='${x}'][data-y='${y}']`
            );

            if(!cell) continue;

            if(y>0 && board[x][y-1] === "SHIP"){
                cell.classList.add("ship-left");
            }

            if(y<9 && board[x][y+1] === "SHIP"){
                cell.classList.add("ship-right");
            }

            if(x>0 && board[x-1][y] === "SHIP"){
                cell.classList.add("ship-top");
            }

            if(x<9 && board[x+1][y] === "SHIP"){
                cell.classList.add("ship-bottom");
            }

        }

    }

}



/* ===========================
   UPDATE CELL
=========================== */

function updateCell(containerId, x, y, result){

    const cell = document.querySelector(
        `#${containerId} .cell[data-x='${x}'][data-y='${y}']`
    );

    if(!cell){
        return;
    }

    if(result === "HIT"){

        cell.classList.add("hit");

        animateHit(cell);

    }

    if(result === "MISS"){

        cell.classList.add("miss");

        animateMiss(cell);

    }

    if(result === "SUNK"){

        cell.classList.remove("hit");

        cell.classList.add("sunk");

        animateHit(cell);

    }

}



/* ===========================
   HIT EFFECT
=========================== */

function animateHit(cell){

    cell.style.transform = "scale(1.25)";
    cell.style.transition = "transform 0.15s";

    setTimeout(()=>{

        cell.style.transform="scale(1)";

    },150);

}



/* ===========================
   MISS EFFECT
=========================== */

function animateMiss(cell){

    cell.style.opacity = "0.4";
    cell.style.transition = "opacity 0.2s";

    setTimeout(()=>{

        cell.style.opacity="1";

    },200);

}