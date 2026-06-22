package com.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerPlayer extends Player {
    private final Difficulty difficulty;
    private final Random random;

    public ComputerPlayer(Symbol symbol, Difficulty difficulty) {
        super("Computer (" + difficulty + ")", symbol);
        this.difficulty = difficulty;
        this.random = new Random();
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public Move getNextMove(Board board) {
        System.out.println(ConsoleColor.BLACK_BRIGHT + getName() + " is thinking..." + ConsoleColor.RESET);
        // Add a slight delay for better user experience
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (difficulty == Difficulty.EASY) {
            return makeRandomMove(board);
        } else {
            return makeMediumMove(board);
        }
    }

    private Move makeRandomMove(Board board) {
        List<Move> available = getAvailableMoves(board);
        if (available.isEmpty()) {
            throw new IllegalStateException("No available moves on the board");
        }
        return available.get(random.nextInt(available.size()));
    }

    private Move makeMediumMove(Board board) {
        // 1. Can I win in this move?
        Move winMove = findWinningMove(board, this.symbol);
        if (winMove != null) {
            return winMove;
        }

        // 2. Can the opponent win in their next move? Block them.
        Symbol opponentSymbol = (this.symbol == Symbol.X) ? Symbol.O : Symbol.X;
        Move blockMove = findWinningMove(board, opponentSymbol);
        if (blockMove != null) {
            return blockMove;
        }

        // 3. Take the center if it's available
        int center = board.getSize() / 2;
        if (board.isValidMove(center, center)) {
            return new Move(center, center);
        }

        // 4. Fallback to random move
        return makeRandomMove(board);
    }

    private Move findWinningMove(Board board, Symbol sym) {
        int size = board.getSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board.isValidMove(r, c)) {
                    Board copy = board.copy();
                    copy.place(r, c, sym);
                    if (copy.checkWin(sym)) {
                        return new Move(r, c);
                    }
                }
            }
        }
        return null;
    }

    private List<Move> getAvailableMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        int size = board.getSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board.isValidMove(r, c)) {
                    moves.add(new Move(r, c));
                }
            }
        }
        return moves;
    }
}
