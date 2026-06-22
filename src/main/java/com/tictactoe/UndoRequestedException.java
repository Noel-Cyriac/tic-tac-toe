package com.tictactoe;

public class UndoRequestedException extends RuntimeException {
    public UndoRequestedException() {
        super("Undo requested by player");
    }
}
