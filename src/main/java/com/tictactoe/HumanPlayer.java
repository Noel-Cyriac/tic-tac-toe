package com.tictactoe;

import java.util.Scanner;

public class HumanPlayer extends Player {
    private final Scanner scanner;

    public HumanPlayer(String name, Symbol symbol, Scanner scanner) {
        super(name, symbol);
        this.scanner = scanner;
    }

    @Override
    public Move getNextMove(Board board) throws UndoRequestedException {
        int size = board.getSize();
        while (true) {
            System.out.print(ConsoleColor.CYAN_BOLD + "👉 " + name + " (" + symbol + ")" + ConsoleColor.RESET + ", enter move (row col), 'undo', or 'exit': ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("undo")) {
                throw new UndoRequestedException();
            }

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                throw new MatchExitRequestedException();
            }

            String[] parts = input.split("[,\\s]+");
            if (parts.length != 2) {
                System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid input format. Enter two numbers separated by a space (e.g. '2 2')." + ConsoleColor.RESET);
                continue;
            }

            try {
                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;

                if (!board.isWithinBounds(row, col)) {
                    System.out.println(ConsoleColor.RED_BOLD + "⚠ Move is out of bounds. Must be between 1 and " + size + "." + ConsoleColor.RESET);
                    continue;
                }

                if (!board.isValidMove(row, col)) {
                    System.out.println(ConsoleColor.RED_BOLD + "⚠ That cell is already occupied! Try again." + ConsoleColor.RESET);
                    continue;
                }

                return new Move(row, col);
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid input. Please enter valid integer coordinates." + ConsoleColor.RESET);
            }
        }
    }
}
