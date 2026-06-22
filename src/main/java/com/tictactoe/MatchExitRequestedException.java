package com.tictactoe;

public class MatchExitRequestedException extends RuntimeException {
    public MatchExitRequestedException() {
        super("Match exit requested by player");
    }
}
