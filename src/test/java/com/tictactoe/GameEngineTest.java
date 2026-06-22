package com.tictactoe;

import org.junit.jupiter.api.Test;
import java.util.Stack;
import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {

    // Simple test double for Player to feed pre-determined moves or throw undo requests
    private static class TestPlayer extends Player {
        private final Object[] movesAndActions;
        private int index = 0;

        public TestPlayer(String name, Symbol symbol, Object... movesAndActions) {
            super(name, symbol);
            this.movesAndActions = movesAndActions;
        }

        @Override
        public Move getNextMove(Board board) throws UndoRequestedException {
            if (index >= movesAndActions.length) {
                throw new IllegalStateException("No more inputs for " + getName());
            }
            Object action = movesAndActions[index++];
            if (action instanceof String && ((String) action).equalsIgnoreCase("undo")) {
                throw new UndoRequestedException();
            }
            return (Move) action;
        }
    }

    @Test
    public void testTwoPlayerGameProgression() {
        Board board = new Board(3);
        // Player 1 wins via top row
        Player p1 = new TestPlayer("Alice", Symbol.X, new Move(0, 0), new Move(0, 1), new Move(0, 2));
        Player p2 = new TestPlayer("Bob", Symbol.O, new Move(1, 0), new Move(1, 1));

        GameEngine engine = new GameEngine(board, p1, p2, p1);
        Player winner = engine.play();

        assertEquals(p1, winner);
        assertEquals(Symbol.X, board.getCell(0, 0));
        assertEquals(Symbol.X, board.getCell(0, 1));
        assertEquals(Symbol.X, board.getCell(0, 2));
        assertEquals(Symbol.O, board.getCell(1, 0));
        assertEquals(Symbol.O, board.getCell(1, 1));
    }

    @Test
    public void testTwoPlayerUndo() {
        Board board = new Board(3);
        // P1 moves -> P2 moves -> P1 moves -> P2 undos -> P2 moves elsewhere -> P1 wins
        Player p1 = new TestPlayer("Alice", Symbol.X, new Move(0, 0), "undo", new Move(0, 1), new Move(0, 2));
        Player p2 = new TestPlayer("Bob", Symbol.O, new Move(1, 0), new Move(2, 0), new Move(1, 1));

        GameEngine engine = new GameEngine(board, p1, p2, p1);
        Player winner = engine.play();

        assertEquals(p1, winner);
        // Verify Bob's undone move (1,0) is EMPTY, and new move (2,0) has O
        assertEquals(Symbol.EMPTY, board.getCell(1, 0));
        assertEquals(Symbol.O, board.getCell(2, 0));
    }

    @Test
    public void testSinglePlayerUndo() {
        Board board = new Board(3);
        // Human P1: plays (0,0) -> undos -> plays (1,1) -> plays (0,0) -> plays (2,2)
        // Computer P2 (Easy): just plays random valid moves.
        // We will make sure P1 can successfully undo and reset the board.
        // Since P2 is computer, P1 undoing should revert BOTH P1's move and P2's computer move.
        
        Player p1 = new TestPlayer("Alice", Symbol.X, 
                new Move(0, 0),    // Move 1
                "undo",            // Undo both Move 1 and Computer's response
                new Move(1, 1),    // Move 2 (starts new sequence)
                new Move(0, 2),    // Move 3
                new Move(2, 0)     // Move 4 (could trigger win or continue)
        );
        Player computer = new ComputerPlayer(Symbol.O, Difficulty.EASY);

        // Since the game runs continuously, we don't necessarily play to win here, but we can verify the undo behavior.
        // Let's manually trigger actions using the engine undo method directly to assert state logic
        GameEngine engine = new GameEngine(board, p1, computer, p1);
        
        // P1 plays (0,0)
        board.place(0, 0, Symbol.X);
        engine.undo(); // Undo when history is empty -> returns false
        assertFalse(engine.undo());

        // Fill histories manually to simulate game progression
        Board state0 = board.copy(); // empty
        board.place(0, 0, Symbol.X);
        engine.getBoard().copyStateFrom(board);
        
        // Push human move
        // Board state before human move was state0
        // But in our engine, we push the board *before* the move is placed.
        // So:
        // History size 0
        // Human about to play (0,0):
        // Pushes copy of empty board (state0) and human player to stack.
        // Board gets (0,0) filled with X.
        // Switch turn to computer.
        // Computer about to play (1,1):
        // Pushes copy of board with X (state1) and computer player to stack.
        // Board gets (1,1) filled with O.
        // Switch turn to human.
        
        Board emptyBoard = new Board(3);
        Stack<Board> bh = getPrivateHistory(engine);
        Stack<Player> ph = getPrivatePlayerHistory(engine);

        // Simulate Human Move
        bh.push(emptyBoard.copy());
        ph.push(p1);
        board.place(0, 0, Symbol.X);

        // Simulate Computer Move
        Board afterHuman = emptyBoard.copy();
        afterHuman.place(0, 0, Symbol.X);
        bh.push(afterHuman);
        ph.push(computer);
        board.place(1, 1, Symbol.O);

        // Verify state is occupied
        assertEquals(Symbol.X, board.getCell(0, 0));
        assertEquals(Symbol.O, board.getCell(1, 1));

        // Trigger undo
        boolean undone = engine.undo();
        assertTrue(undone);

        // Verify board is fully restored to emptyBoard (before human's move)
        assertEquals(Symbol.EMPTY, board.getCell(0, 0));
        assertEquals(Symbol.EMPTY, board.getCell(1, 1));
        // Verify current player is restored to Human (p1)
        assertEquals(p1, engine.getCurrentPlayer());
    }

    @SuppressWarnings("unchecked")
    private Stack<Board> getPrivateHistory(GameEngine engine) {
        try {
            java.lang.reflect.Field field = GameEngine.class.getDeclaredField("boardHistory");
            field.setAccessible(true);
            return (Stack<Board>) field.get(engine);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private Stack<Player> getPrivatePlayerHistory(GameEngine engine) {
        try {
            java.lang.reflect.Field field = GameEngine.class.getDeclaredField("playerHistory");
            field.setAccessible(true);
            return (Stack<Player>) field.get(engine);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
