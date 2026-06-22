package com.tictactoe;

public enum Symbol {
    X('X'),
    O('O'),
    EMPTY(' ');

    private final char character;

    Symbol(char character) {
        this.character = character;
    }

    public char getChar() {
        return character;
    }

    @Override
    public String toString() {
        if (this == X) {
            return ConsoleColor.CYAN_BOLD_BRIGHT + character + ConsoleColor.RESET;
        } else if (this == O) {
            return ConsoleColor.RED_BOLD_BRIGHT + character + ConsoleColor.RESET;
        }
        return String.valueOf(character);
    }
}
