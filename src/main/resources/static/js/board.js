function createBoard(containerId, onCellClick){

    const board = document.getElementById(containerId);

    board.innerHTML = "";

    for(let x=0;x<10;x++){

        for(let y=0;y<10;y++){

            const cell = document.createElement("div");

            cell.classList.add("cell");
            cell.dataset.x = x;
            cell.dataset.y = y;

            if(onCellClick){
                cell.onclick = function(){

                    // evitar disparar dos veces en la misma celda
                    if(cell.classList.contains("hit") || cell.classList.contains("miss")){
                        return;
                    }

                    onCellClick(x,y);

                };
            }

            board.appendChild(cell);

        }

    }

}

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

            if(value === "SHIP"){
                cell.classList.add("ship");
            }

        }

    }

}

function updateCell(containerId,x,y,result){

    const cell = document.querySelector(
        `#${containerId} .cell[data-x='${x}'][data-y='${y}']`
    );

    if(!cell){
        return;
    }

    if(result === "HIT"){
        cell.classList.add("hit");
    }

    if(result === "MISS"){
        cell.classList.add("miss");
    }

}