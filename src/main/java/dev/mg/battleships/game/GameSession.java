package dev.mg.battleships.game;

import lombok.Getter;

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

        placeDefaultShips(player1Board);
        placeDefaultShips(player2Board);

        this.currentTurnPlayerId = player1Id;
    }

    public boolean placeShip(Long playerId, int x, int y, int size, Direction direction) {

        Board board = getPlayerBoard(playerId);

        if (board == null) return false;

        return board.placeShip(x, y, size, direction);
    }

    public Cell fire(Long playerId, int x, int y) {

        if (!playerId.equals(currentTurnPlayerId)) {
            throw new RuntimeException("Not your turn");
        }

        Board opponentBoard = getOpponentBoard(playerId);

        Cell result = opponentBoard.fire(x, y);

        switchTurn();

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

    private void placeDefaultShips(Board board) {

        board.placeShip(0,0,5,Direction.HORIZONTAL);
        board.placeShip(2,2,4,Direction.VERTICAL);
        board.placeShip(5,5,3,Direction.HORIZONTAL);
        board.placeShip(7,3,3,Direction.VERTICAL);
        board.placeShip(9,0,2,Direction.HORIZONTAL);

    }
}