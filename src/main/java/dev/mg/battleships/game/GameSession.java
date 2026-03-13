package dev.mg.battleships.game;

import lombok.Getter;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class GameSession {

    private Long gameId;

    private Long player1Id;
    private Long player2Id;

    private Board player1Board;
    private Board player2Board;

    private Long currentTurnPlayerId;


    public GameSession(Long gameId, Long player1Id, Long player2Id) {

        this.gameId = gameId;

        this.player1Id = player1Id;
        this.player2Id = player2Id;

        this.player1Board = new Board();
        this.player2Board = new Board();

        int layout1 = ThreadLocalRandom.current().nextInt(6);
        int layout2;

        do {
            layout2 = ThreadLocalRandom.current().nextInt(6);
        } while(layout2 == layout1);

        placeDefaultShips(player1Board, layout1);
        placeDefaultShips(player2Board, layout2);

        this.currentTurnPlayerId = player1Id;
    }

    public boolean placeShip(Long playerId, int x, int y, int size, Direction direction) {

        Board board = getPlayerBoard(playerId);

        if (board == null) return false;

        return board.placeShip(x, y, size, direction);
    }

    public Board getBoardForPlayer(Long playerId) {

        if (playerId.equals(player1Id)) {
            return player1Board;
        }

        if (playerId.equals(player2Id)) {
            return player2Board;
        }

        return null;
    }

    public Cell fire(Long playerId, int x, int y) {

        if (!playerId.equals(currentTurnPlayerId)) {
            throw new RuntimeException("Not your turn");
        }

        Board opponentBoard = getOpponentBoard(playerId);

        Cell result = opponentBoard.fire(x, y);

        // evitar disparar dos veces
        if (result == Cell.HIT || result == Cell.MISS) {

            if (result == Cell.HIT && opponentBoard.isShipSunk(x, y)) {
                result = Cell.SUNK;
            }

            // solo cambiar turno si el juego no terminó
            if (!isGameOver()) {
                switchTurn();
            }

            return result;
        }

        return result;
    }

    public boolean isGameOver() {

        return player1Board.allShipsSunk() || player2Board.allShipsSunk();
    }

    public Long getWinner() {

        if (!isGameOver()) return null;

        if (player1Board.allShipsSunk()) {
            return player2Id;
        }

        return player1Id;
    }

    private Board getPlayerBoard(Long playerId) {

        if (playerId.equals(player1Id)) return player1Board;

        if (playerId.equals(player2Id)) return player2Board;

        return null;
    }

    private Board getOpponentBoard(Long playerId) {

        if (playerId.equals(player1Id)) return player2Board;

        if (playerId.equals(player2Id)) return player1Board;

        return null;
    }

    private void switchTurn() {

        if (currentTurnPlayerId.equals(player1Id)) {
            currentTurnPlayerId = player2Id;
        } else {
            currentTurnPlayerId = player1Id;
        }

    }

    private void placeDefaultShips(Board board, int layout) {

        switch (layout) {

            case 0 -> {
                board.placeShip(0,0,5,Direction.HORIZONTAL);
                board.placeShip(2,2,4,Direction.VERTICAL);
                board.placeShip(5,5,3,Direction.HORIZONTAL);
                board.placeShip(7,3,3,Direction.VERTICAL);
                board.placeShip(9,0,2,Direction.HORIZONTAL);
            }

            case 1 -> {
                board.placeShip(1,1,5,Direction.VERTICAL);
                board.placeShip(3,5,4,Direction.HORIZONTAL);
                board.placeShip(6,2,3,Direction.VERTICAL);
                board.placeShip(8,6,3,Direction.HORIZONTAL);
                board.placeShip(0,8,2,Direction.VERTICAL);
            }

            case 2 -> {
                board.placeShip(0,4,5,Direction.VERTICAL);
                board.placeShip(4,0,4,Direction.HORIZONTAL);
                board.placeShip(6,6,3,Direction.HORIZONTAL);
                board.placeShip(8,2,3,Direction.VERTICAL);
                board.placeShip(9,7,2,Direction.HORIZONTAL);
            }

            case 3 -> {
                board.placeShip(2,0,5,Direction.HORIZONTAL);
                board.placeShip(5,4,4,Direction.VERTICAL);
                board.placeShip(7,1,3,Direction.HORIZONTAL);
                board.placeShip(0,7,3,Direction.VERTICAL);
                board.placeShip(9,5,2,Direction.HORIZONTAL);
            }

            case 4 -> {
                board.placeShip(0,9,5,Direction.VERTICAL);
                board.placeShip(3,3,4,Direction.HORIZONTAL);
                board.placeShip(5,0,3,Direction.VERTICAL);
                board.placeShip(8,5,3,Direction.HORIZONTAL);
                board.placeShip(6,8,2,Direction.VERTICAL);
            }

            case 5 -> {
                board.placeShip(4,0,5,Direction.HORIZONTAL);
                board.placeShip(0,2,4,Direction.VERTICAL);
                board.placeShip(7,6,3,Direction.HORIZONTAL);
                board.placeShip(3,8,3,Direction.VERTICAL);
                board.placeShip(9,3,2,Direction.HORIZONTAL);
            }

        }

    }
}