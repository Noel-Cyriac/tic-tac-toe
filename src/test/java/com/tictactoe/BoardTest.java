package com.tictactoe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    public void testBoardInitialization() {
        Board board = new Board(3);
        assertEquals(3, board.getSize());
        assertFalse(board.isFull());
        
        // Assert all cells empty
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                assertEquals(Symbol.EMPTY, board.getCell(r, c));
            }
        }
    }

    @Test
    public void testInvalidBoardSize() {
        assertThrows(IllegalArgumentException.class, () -> new Board(2));
    }

    @Test
    public void testPlaceSymbolAndIsValidMove() {
        Board board = new Board(3);
        assertTrue(board.isValidMove(1, 1));
        assertTrue(board.place(1, 1, Symbol.X));
        assertEquals(Symbol.X, board.getCell(1, 1));
        assertFalse(board.isValidMove(1, 1)); // no longer valid
        assertFalse(board.place(1, 1, Symbol.O)); // should return false
    }

    @Test
    public void testCheckWinRows() {
        Board board = new Board(3);
        board.place(0, 0, Symbol.X);
        board.place(0, 1, Symbol.X);
        assertFalse(board.checkWin(Symbol.X));
        board.place(0, 2, Symbol.X);
        assertTrue(board.checkWin(Symbol.X));
    }

    @Test
    public void testCheckWinCols() {
        Board board = new Board(4);
        board.place(0, 2, Symbol.O);
        board.place(1, 2, Symbol.O);
        board.place(2, 2, Symbol.O);
        assertFalse(board.checkWin(Symbol.O));
        board.place(3, 2, Symbol.O);
        assertTrue(board.checkWin(Symbol.O));
    }

    @Test
    public void testCheckWinMainDiagonal() {
        Board board = new Board(3);
        board.place(0, 0, Symbol.X);
        board.place(1, 1, Symbol.X);
        assertFalse(board.checkWin(Symbol.X));
        board.place(2, 2, Symbol.X);
        assertTrue(board.checkWin(Symbol.X));
    }

    @Test
    public void testCheckWinAntiDiagonal() {
        Board board = new Board(5);
        // Anti-diagonal for 5x5: (0,4), (1,3), (2,2), (3,1), (4,0)
        board.place(0, 4, Symbol.O);
        board.place(1, 3, Symbol.O);
        board.place(2, 2, Symbol.O);
        board.place(3, 1, Symbol.O);
        assertFalse(board.checkWin(Symbol.O));
        board.place(4, 0, Symbol.O);
        assertTrue(board.checkWin(Symbol.O));
    }

    @Test
    public void testIsFull() {
        Board board = new Board(3);
        // Fill the board but no win
        // X O X
        // X X O
        // O X O
        board.place(0, 0, Symbol.X);
        board.place(0, 1, Symbol.O);
        board.place(0, 2, Symbol.X);
        board.place(1, 0, Symbol.X);
        board.place(1, 1, Symbol.X);
        board.place(1, 2, Symbol.O);
        board.place(2, 0, Symbol.O);
        board.place(2, 1, Symbol.X);
        assertFalse(board.isFull());
        board.place(2, 2, Symbol.O);
        assertTrue(board.isFull());
        assertFalse(board.checkWin(Symbol.X));
        assertFalse(board.checkWin(Symbol.O));
    }

    @Test
    public void testCopyStateAndCopyStateFrom() {
        Board original = new Board(3);
        original.place(0, 0, Symbol.X);
        original.place(1, 1, Symbol.O);

        Board copy = original.copy();
        assertEquals(original.getSize(), copy.getSize());
        assertEquals(Symbol.X, copy.getCell(0, 0));
        assertEquals(Symbol.O, copy.getCell(1, 1));
        assertEquals(Symbol.EMPTY, copy.getCell(2, 2));

        // Mutate original, copy shouldn't change
        original.place(2, 2, Symbol.X);
        assertEquals(Symbol.EMPTY, copy.getCell(2, 2));

        // Restore copy back to original
        Board target = new Board(3);
        target.copyStateFrom(copy);
        assertEquals(Symbol.X, target.getCell(0, 0));
        assertEquals(Symbol.O, target.getCell(1, 1));
        assertEquals(Symbol.EMPTY, target.getCell(2, 2));
    }

    @Test
    public void testEarlyDrawDetection() {
        Board board = new Board(3);
        // Set up an early draw board (cell at 1, 2 is still EMPTY):
        // X O X
        // O X .
        // O X O
        board.place(0, 0, Symbol.X);
        board.place(0, 1, Symbol.O);
        board.place(0, 2, Symbol.X);
        board.place(1, 0, Symbol.O);
        board.place(1, 1, Symbol.X);
        // (1, 2) left empty
        board.place(2, 0, Symbol.O);
        board.place(2, 1, Symbol.X);
        board.place(2, 2, Symbol.O);

        assertFalse(board.isFull());
        assertTrue(board.isDraw());
    }
}
