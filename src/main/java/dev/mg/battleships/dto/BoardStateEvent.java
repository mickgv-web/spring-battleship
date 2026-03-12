package dev.mg.battleships.dto;
import dev.mg.battleships.game.Cell;

public class BoardStateEvent {

    private String type = "BOARD_STATE";

    private Cell[][] board;

    private Long currentTurnPlayerId;

    public BoardStateEvent(Cell[][] board, Long currentTurnPlayerId) {
        this.board = board;
        this.currentTurnPlayerId = currentTurnPlayerId;
    }

    public String getType() {
        return type;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public Long getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

}