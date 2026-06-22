package com.tictactoe;

public abstract class Player {
    protected final String name;
    protected final Symbol symbol;

    protected Player(String name, Symbol symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * Determines the next move.
     * @param board The current state of the board.
     * @return The chosen move.
     * @throws UndoRequestedException if the player requests to undo the last move.
     */
    public abstract Move getNextMove(Board board) throws UndoRequestedException;
}
