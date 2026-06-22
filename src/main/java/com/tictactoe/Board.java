package com.tictactoe;

import java.util.Arrays;

public class Board {
    private final int size;
    private final Symbol[][] grid;

    public Board(int size) {
        if (size < 3) {
            throw new IllegalArgumentException("Board size must be at least 3");
        }
        this.size = size;
        this.grid = new Symbol[size][size];
        clear();
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            Arrays.fill(grid[i], Symbol.EMPTY);
        }
    }

    public int getSize() {
        return size;
    }

    public Symbol getCell(int row, int col) {
        if (!isWithinBounds(row, col)) {
            throw new IllegalArgumentException("Coordinates out of bounds: " + row + ", " + col);
        }
        return grid[row][col];
    }

    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public boolean isValidMove(int row, int col) {
        return isWithinBounds(row, col) && grid[row][col] == Symbol.EMPTY;
    }

    public boolean place(int row, int col, Symbol symbol) {
        if (!isValidMove(row, col)) {
            return false;
        }
        grid[row][col] = symbol;
        return true;
    }

    public boolean isFull() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c] == Symbol.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkWin(Symbol symbol) {
        // Check rows
        for (int r = 0; r < size; r++) {
            boolean win = true;
            for (int c = 0; c < size; c++) {
                if (grid[r][c] != symbol) {
                    win = false;
                    break;
                }
            }
            if (win) return true;
        }

        // Check columns
        for (int c = 0; c < size; c++) {
            boolean win = true;
            for (int r = 0; r < size; r++) {
                if (grid[r][c] != symbol) {
                    win = false;
                    break;
                }
            }
            if (win) return true;
        }

        // Check main diagonal
        boolean winDiag1 = true;
        for (int i = 0; i < size; i++) {
            if (grid[i][i] != symbol) {
                winDiag1 = false;
                break;
            }
        }
        if (winDiag1) return true;

        // Check anti-diagonal
        boolean winDiag2 = true;
        for (int i = 0; i < size; i++) {
            if (grid[i][size - 1 - i] != symbol) {
                winDiag2 = false;
                break;
            }
        }
        return winDiag2;
    }

    public boolean isDraw() {
        int countX = 0;
        int countO = 0;
        int emptyCount = 0;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (grid[r][c] == Symbol.X) countX++;
                else if (grid[r][c] == Symbol.O) countO++;
                else emptyCount++;
            }
        }

        int xTurns;
        int oTurns;
        if (countX == countO) {
            xTurns = (emptyCount + 1) / 2;
            oTurns = emptyCount / 2;
        } else {
            oTurns = (emptyCount + 1) / 2;
            xTurns = emptyCount / 2;
        }

        boolean xCanWin = false;
        boolean oCanWin = false;

        // Check rows
        for (int r = 0; r < size; r++) {
            int rowEmpty = 0;
            boolean rowHasX = false;
            boolean rowHasO = false;
            for (int c = 0; c < size; c++) {
                if (grid[r][c] == Symbol.X) rowHasX = true;
                else if (grid[r][c] == Symbol.O) rowHasO = true;
                else rowEmpty++;
            }
            if (!rowHasO && rowEmpty <= xTurns) xCanWin = true;
            if (!rowHasX && rowEmpty <= oTurns) oCanWin = true;
        }

        // Check columns
        for (int c = 0; c < size; c++) {
            int colEmpty = 0;
            boolean colHasX = false;
            boolean colHasO = false;
            for (int r = 0; r < size; r++) {
                if (grid[r][c] == Symbol.X) colHasX = true;
                else if (grid[r][c] == Symbol.O) colHasO = true;
                else colEmpty++;
            }
            if (!colHasO && colEmpty <= xTurns) xCanWin = true;
            if (!colHasX && colEmpty <= oTurns) oCanWin = true;
        }

        // Check main diagonal
        int diag1Empty = 0;
        boolean diag1HasX = false;
        boolean diag1HasO = false;
        for (int i = 0; i < size; i++) {
            if (grid[i][i] == Symbol.X) diag1HasX = true;
            else if (grid[i][i] == Symbol.O) diag1HasO = true;
            else diag1Empty++;
        }
        if (!diag1HasO && diag1Empty <= xTurns) xCanWin = true;
        if (!diag1HasX && diag1Empty <= oTurns) oCanWin = true;

        // Check anti-diagonal
        int diag2Empty = 0;
        boolean diag2HasX = false;
        boolean diag2HasO = false;
        for (int i = 0; i < size; i++) {
            if (grid[i][size - 1 - i] == Symbol.X) diag2HasX = true;
            else if (grid[i][size - 1 - i] == Symbol.O) diag2HasO = true;
            else diag2Empty++;
        }
        if (!diag2HasO && diag2Empty <= xTurns) xCanWin = true;
        if (!diag2HasX && diag2Empty <= oTurns) oCanWin = true;

        // If neither player can win, it's a draw
        return !xCanWin && !oCanWin;
    }

    public Board copy() {
        Board copyBoard = new Board(this.size);
        for (int r = 0; r < size; r++) {
            System.arraycopy(this.grid[r], 0, copyBoard.grid[r], 0, size);
        }
        return copyBoard;
    }

    public void copyStateFrom(Board source) {
        if (source.getSize() != this.size) {
            throw new IllegalArgumentException("Cannot copy state from a board of different size");
        }
        for (int r = 0; r < size; r++) {
            System.arraycopy(source.grid[r], 0, this.grid[r], 0, size);
        }
    }

    public void display() {
        // Print column headers
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        for (int c = 0; c < size; c++) {
            sb.append(" ").append(ConsoleColor.YELLOW_BOLD).append(c + 1).append(ConsoleColor.RESET).append("  ");
        }
        System.out.println(sb);

        // Print top border
        sb = new StringBuilder();
        sb.append("   ").append(ConsoleColor.WHITE).append("┌");
        for (int c = 0; c < size; c++) {
            sb.append("───");
            if (c < size - 1) {
                sb.append("┬");
            }
        }
        sb.append("┐").append(ConsoleColor.RESET);
        System.out.println(sb);

        for (int r = 0; r < size; r++) {
            // Print row data line
            sb = new StringBuilder();
            sb.append(" ").append(ConsoleColor.YELLOW_BOLD).append(r + 1).append(ConsoleColor.RESET).append(" ");
            sb.append(ConsoleColor.WHITE).append("│").append(ConsoleColor.RESET);
            for (int c = 0; c < size; c++) {
                sb.append(" ").append(grid[r][c]).append(" ").append(ConsoleColor.WHITE).append("│").append(ConsoleColor.RESET);
            }
            System.out.println(sb);

            // Print divider line (except last row)
            if (r < size - 1) {
                sb = new StringBuilder();
                sb.append("   ").append(ConsoleColor.WHITE).append("├");
                for (int c = 0; c < size; c++) {
                    sb.append("───");
                    if (c < size - 1) {
                        sb.append("┼");
                    }
                }
                sb.append("┤").append(ConsoleColor.RESET);
                System.out.println(sb);
            }
        }

        // Print bottom border
        sb = new StringBuilder();
        sb.append("   ").append(ConsoleColor.WHITE).append("└");
        for (int c = 0; c < size; c++) {
            sb.append("───");
            if (c < size - 1) {
                sb.append("┴");
            }
        }
        sb.append("┘").append(ConsoleColor.RESET);
        System.out.println(sb);
        System.out.println();
    }
}
