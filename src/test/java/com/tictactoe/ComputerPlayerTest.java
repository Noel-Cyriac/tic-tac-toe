package com.tictactoe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ComputerPlayerTest {

    @Test
    public void testEasyComputerPlayerValidMove() {
        Board board = new Board(3);
        ComputerPlayer computer = new ComputerPlayer(Symbol.O, Difficulty.EASY);

        // Fill all but 2 cells
        board.place(0, 0, Symbol.X);
        board.place(0, 1, Symbol.O);
        board.place(0, 2, Symbol.X);
        board.place(1, 0, Symbol.O);
        board.place(1, 1, Symbol.X);
        board.place(1, 2, Symbol.O);
        board.place(2, 0, Symbol.X);
        // (2,1) and (2,2) are left empty

        Move move = computer.getNextMove(board);
        assertTrue(board.isValidMove(move.row(), move.col()));
        assertTrue(move.equals(new Move(2, 1)) || move.equals(new Move(2, 2)));
    }

    @Test
    public void testMediumComputerPlayerTakesWinningMove() {
        Board board = new Board(3);
        ComputerPlayer computer = new ComputerPlayer(Symbol.O, Difficulty.MEDIUM);

        // Computer is O. O has two marks in the first row.
        board.place(0, 0, Symbol.O);
        board.place(0, 1, Symbol.O);
        // Opponent X is elsewhere
        board.place(1, 1, Symbol.X);

        Move move = computer.getNextMove(board);
        // Computer should win by choosing (0,2)
        assertEquals(new Move(0, 2), move);
    }

    @Test
    public void testMediumComputerPlayerBlocksOpponentWin() {
        Board board = new Board(3);
        ComputerPlayer computer = new ComputerPlayer(Symbol.O, Difficulty.MEDIUM);

        // Opponent X has two marks in second row (1,0) and (1,1)
        board.place(1, 0, Symbol.X);
        board.place(1, 1, Symbol.X);
        // Computer O is elsewhere
        board.place(0, 0, Symbol.O);

        Move move = computer.getNextMove(board);
        // Computer should block opponent by choosing (1,2)
        assertEquals(new Move(1, 2), move);
    }

    @Test
    public void testMediumComputerPlayerTakesCenter() {
        Board board = new Board(3);
        ComputerPlayer computer = new ComputerPlayer(Symbol.O, Difficulty.MEDIUM);

        // Empty board. Computer should take the center (1,1)
        Move move = computer.getNextMove(board);
        assertEquals(new Move(1, 1), move);
    }

    @Test
    public void testMediumComputerPlayerCenterOccupiedFallback() {
        Board board = new Board(3);
        ComputerPlayer computer = new ComputerPlayer(Symbol.O, Difficulty.MEDIUM);

        // Center (1,1) is occupied by X
        board.place(1, 1, Symbol.X);

        Move move = computer.getNextMove(board);
        // Center is occupied, no direct win or block, so computer should choose any other valid cell
        assertNotNull(move);
        assertTrue(board.isValidMove(move.row(), move.col()));
        assertNotEquals(new Move(1, 1), move);
    }
}
