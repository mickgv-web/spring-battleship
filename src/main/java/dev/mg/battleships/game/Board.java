package dev.mg.battleships.game;

public class Board {

    private static final int SIZE = 10;

    private Cell[][] grid;

    public Board() {

        grid = new Cell[SIZE][SIZE];

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                grid[x][y] = Cell.EMPTY;
            }
        }
    }

    public boolean placeShip(int x, int y, int size, Direction direction) {

        if (direction == Direction.HORIZONTAL) {

            if (y + size > SIZE) return false;

            for (int i = 0; i < size; i++) {
                if (grid[x][y + i] != Cell.EMPTY) return false;
            }

            for (int i = 0; i < size; i++) {
                grid[x][y + i] = Cell.SHIP;
            }

        } else {

            if (x + size > SIZE) return false;

            for (int i = 0; i < size; i++) {
                if (grid[x + i][y] != Cell.EMPTY) return false;
            }

            for (int i = 0; i < size; i++) {
                grid[x + i][y] = Cell.SHIP;
            }

        }

        return true;
    }

    public Cell fire(int x, int y) {

        Cell cell = grid[x][y];

        if (cell == Cell.SHIP) {
            grid[x][y] = Cell.HIT;
            return Cell.HIT;
        }

        if (cell == Cell.EMPTY) {
            grid[x][y] = Cell.MISS;
            return Cell.MISS;
        }

        return cell;
    }

    public boolean allShipsSunk() {

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {

                if (grid[x][y] == Cell.SHIP) {
                    return false;
                }

            }
        }

        return true;
    }

}