package com.tictactoe;

import java.util.Stack;

public class GameEngine {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;

    private final Stack<Board> boardHistory = new Stack<>();
    private final Stack<Player> playerHistory = new Stack<>();

    public GameEngine(Board board, Player player1, Player player2, Player startingPlayer) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = startingPlayer;
    }

    public Player play() {
        System.out.println("\n" + ConsoleColor.PURPLE_BOLD_BRIGHT + "⚔  Game started between " + ConsoleColor.RESET 
                + ConsoleColor.WHITE_BOLD + player1.getName() + " (" + player1.getSymbol() + ")" + ConsoleColor.RESET 
                + ConsoleColor.PURPLE_BOLD_BRIGHT + " and " + ConsoleColor.RESET 
                + ConsoleColor.WHITE_BOLD + player2.getName() + " (" + player2.getSymbol() + ")." + ConsoleColor.RESET);
        System.out.println(ConsoleColor.BLACK_BRIGHT + "Grid size: " + board.getSize() + "x" + board.getSize() + ConsoleColor.RESET);
        
        while (true) {
            board.display();

            try {
                Move move = currentPlayer.getNextMove(board);
                
                // Save current state to history before executing move
                boardHistory.push(board.copy());
                playerHistory.push(currentPlayer);

                // Place the symbol
                board.place(move.row(), move.col(), currentPlayer.getSymbol());
                System.out.println("\n✔ " + ConsoleColor.WHITE_BOLD + currentPlayer.getName() + ConsoleColor.RESET 
                        + " placed " + currentPlayer.getSymbol() 
                        + " at " + ConsoleColor.YELLOW_BOLD + move + ConsoleColor.RESET);

                // Check for win
                if (board.checkWin(currentPlayer.getSymbol())) {
                    board.display();
                    System.out.println(ConsoleColor.GREEN_BOLD_BRIGHT + "🎉 Congratulations! " 
                            + currentPlayer.getName() + " wins the match! 🎉" + ConsoleColor.RESET);
                    return currentPlayer;
                }

                // Check for draw
                if (board.isDraw() || board.isFull()) {
                    board.display();
                    if (board.isFull()) {
                        System.out.println(ConsoleColor.YELLOW_BOLD_BRIGHT + "🤝 The match is a draw! 🤝" + ConsoleColor.RESET);
                    } else {
                        System.out.println(ConsoleColor.YELLOW_BOLD_BRIGHT + "🤝 The match is a draw! (Determined early as no winning moves are possible) 🤝" + ConsoleColor.RESET);
                    }
                    return null;
                }

                // Switch turn
                currentPlayer = (currentPlayer == player1) ? player2 : player1;

            } catch (UndoRequestedException e) {
                if (undo()) {
                    System.out.println("\n" + ConsoleColor.YELLOW_BOLD_BRIGHT + "↩ [UNDO] The last turn has been undone." + ConsoleColor.RESET);
                } else {
                    System.out.println("\n" + ConsoleColor.RED_BOLD + "⚠ [UNDO] Nothing to undo! We are at the start of the game." + ConsoleColor.RESET);
                }
            }
        }
    }

    public boolean undo() {
        boolean isSinglePlayer = (player2 instanceof ComputerPlayer);

        if (isSinglePlayer) {
            // In single player, we need to revert the computer's move AND the player's move.
            // This requires at least 2 steps in history.
            if (boardHistory.size() < 2) {
                return false;
            }

            // Pop computer's move
            boardHistory.pop();
            playerHistory.pop();

            // Pop human's move
            Board targetBoard = boardHistory.pop();
            Player targetPlayer = playerHistory.pop();

            // Restore
            board.copyStateFrom(targetBoard);
            currentPlayer = targetPlayer;
            return true;
        } else {
            // In two-player mode, we revert just the single last move.
            if (boardHistory.isEmpty()) {
                return false;
            }

            Board targetBoard = boardHistory.pop();
            Player targetPlayer = playerHistory.pop();

            // Restore
            board.copyStateFrom(targetBoard);
            currentPlayer = targetPlayer;
            return true;
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Board getBoard() {
        return board;
    }
}
