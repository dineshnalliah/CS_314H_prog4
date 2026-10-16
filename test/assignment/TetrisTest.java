package assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Point;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import assignment.Board.Action;
import assignment.Board.Result;
import assignment.Piece.PieceType;

public class TetrisTest {

    private static final int PLAY_HEIGHT = 20;
    private static final int BOARD_HEIGHT = 24;

    private static final int MAX_MOVES = 60;

    private static PieceType[] types;

    private Brain brain;

    @BeforeAll
    static void setupAll() {
        types = PieceType.values();
    }

    @BeforeEach
    void setupEach() {
        brain = new FirstBrain();
    }

    // making sure board works as expected
    @Test
    void testTetrisBoard() {
        Board board = new TetrisBoard(10, BOARD_HEIGHT);
        assertEquals(10, board.getWidth());
        assertEquals(BOARD_HEIGHT, board.getHeight());
        assertEquals(0, board.getMaxHeight());
        assertNull(board.getCurrentPiece());

        // a square dropped on an empty board lands in the bottom-left corner
        board.nextPiece(new TetrisPiece(PieceType.SQUARE), new Point(0, PLAY_HEIGHT));
        assertEquals(Result.PLACE, board.move(Action.DROP));
        assertEquals(PieceType.SQUARE, board.getGrid(0, 0));
        assertEquals(2, board.getColumnHeight(0));
    }

    // BRAIN TESTING

    // helpers to make moves

    // helps make custom boards
    private void spawn(Board board, PieceType type) {
        Piece p = new TetrisPiece(type);
        board.nextPiece(p, new Point(board.getWidth() / 2 - p.getWidth() / 2, PLAY_HEIGHT));
    }

    // keeps making moves until the piece is placed, maxed at 60
    private Result playOut(Board board) {
        Result result = Result.SUCCESS;
        for (int i = 0; i < MAX_MOVES && result != Result.PLACE; i++) {
            result = board.move(brain.nextMove(board));
        }
        return result;
    }

    // brain shouldn't affect the true board while calculating
    @Test
    void testBrainDoesNotChangeBoard() {
        Board board = new TetrisBoard(10, BOARD_HEIGHT);
        Board reference = new TetrisBoard(10, BOARD_HEIGHT);
        spawn(board, PieceType.T);
        spawn(reference, PieceType.T);

        assertNotNull(brain.nextMove(board));
        assertEquals(reference, board);
    }

    // makes sure the brain is able to place each type of piece
    @Test
    void testBrainPlacesEveryPieceType() {
        for (PieceType type : types) {
            Board board = new TetrisBoard(10, BOARD_HEIGHT);
            spawn(board, type);
            assertEquals(Result.PLACE, playOut(board), "Never placed " + type);
        }
    }

    @Test
    void testBrainSurvivesManyPieces() {
        final int pieceCount = 100; // arbitrary
        Random random = new Random(3); // put a seed so that the piece order is the same 
        Board board = new TetrisBoard(10, BOARD_HEIGHT);

        for (int i = 0; i < pieceCount; i++) {
            try {
                spawn(board, types[random.nextInt(types.length)]);
            } catch (IllegalArgumentException e) {
                fail("Game over: could not spawn piece number " + (i + 1));
            }

            if (playOut(board) != Result.PLACE) {
                fail("Brain never placed piece number " + (i + 1));
            }
            if (board.getMaxHeight() > PLAY_HEIGHT) {
                fail("Stack reached the top after piece number " + (i + 1));
            }
        }
    }

}