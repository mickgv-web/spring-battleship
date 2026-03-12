package dev.mg.battleships.dto;

import dev.mg.battleships.game.Cell;

public class FireResponse {

    private int x;
    private int y;

    private Cell result;

    private Long shooterId;

    private Long nextTurnPlayerId;

    private boolean gameOver;

    private Long winnerId;

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public Cell getResult() { return result; }
    public void setResult(Cell result) { this.result = result; }

    public Long getShooterId() { return shooterId; }
    public void setShooterId(Long shooterId) { this.shooterId = shooterId; }

    public Long getNextTurnPlayerId() { return nextTurnPlayerId; }
    public void setNextTurnPlayerId(Long nextTurnPlayerId) { this.nextTurnPlayerId = nextTurnPlayerId; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }

    public Long getWinnerId() { return winnerId; }
    public void setWinnerId(Long winnerId) { this.winnerId = winnerId; }

}