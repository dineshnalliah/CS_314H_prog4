package assignment;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import assignment.Piece.PieceType;

import java.awt.Point;

import assignment.Board.Action;
import assignment.Board.Result;

/*
 * Any comments and methods here are purely descriptions or suggestions.
 * This is your test file. Feel free to change this as much as you want.
 */

public class BlackBoxTetrisBoardTest {

    // this is our helper method to make a 10x20 board
    private static Board createBoard() {
        return new TetrisBoard(10, 20);
    }

    // a new board should have the correct dimensions and start empty
    @Test
    void testBoardInit() {
        Board b = createBoard();
        assertEquals(10, b.getWidth());
        assertEquals(20, b.getHeight());
        assertEquals(0, b.getMaxHeight());
        assertEquals(0, b.getRowsCleared());
        assertEquals(Action.NOTHING, b.getLastAction());
        assertEquals(Result.NO_PIECE, b.getLastResult());
        assertNull(b.getCurrentPiece());
        assertNull(b.getCurrentPiecePosition());

        for (int x = 0; x < b.getWidth(); x++) {
            assertEquals(0, b.getColumnHeight(x));
        }
        for (int y = 0; y < b.getHeight(); y++) {
            assertEquals(0, b.getRowWidth(y));
        }

        // out of bounds should return null
        assertNull(b.getGrid(-1, 0));
    }

    // nextPiece should set the current piece and its starting position
    @Test
    void testNextPiece() {
        Board b = createBoard();
        Piece square = new TetrisPiece(PieceType.SQUARE);
        Point spawn = new Point(4, 15);

        b.nextPiece(square, spawn);
        assertEquals(square, b.getCurrentPiece());
        assertEquals(spawn, b.getCurrentPiecePosition());
    }

    // we originally had nextPiece() throwing exceptions
    // @Test
    // void testNextPieceExceptions() {
    //     Board b = createBoard();
    //     Piece square = new TetrisPiece(PieceType.SQUARE);

    //     try {
    //         b.nextPiece(null, new Point(0, 0));
    //         fail("Expected IllegalArgumentException for null piece");
    //     } catch (IllegalArgumentException e) {
    //     }

    //     try {
    //         b.nextPiece(square, null);
    //         fail("Expected IllegalArgumentException for null spawn position");
    //     } catch (IllegalArgumentException e) {
    //     }
    // }

    // testing that move when there is no active piece should return No_PIECE
    @Test
    void testMoveNoPiece() {
        Board b = createBoard();
        assertEquals(Result.NO_PIECE, b.move(Action.LEFT));
        assertEquals(Result.NO_PIECE, b.move(Action.RIGHT));
        assertEquals(Result.NO_PIECE, b.move(Action.DOWN));
        assertEquals(Result.NO_PIECE, b.move(Action.DROP));
        assertEquals(Result.NO_PIECE, b.move(Action.CLOCKWISE));
    }

    @Test
    void testMoveLeft() {
        Board b = createBoard();
        Piece square = new TetrisPiece(PieceType.SQUARE);
        b.nextPiece(square, new Point(4, 10));

        assertEquals(Result.SUCCESS, b.move(Action.LEFT));
        assertEquals(new Point(3, 10), b.getCurrentPiecePosition());
        assertEquals(Action.LEFT, b.getLastAction());
        assertEquals(Result.SUCCESS, b.getLastResult());
    }

    @Test
    void testMoveRight() {
        Board b = createBoard();
        Piece square = new TetrisPiece(PieceType.SQUARE);
        b.nextPiece(square, new Point(4, 10));

        assertEquals(Result.SUCCESS, b.move(Action.RIGHT));
        assertEquals(new Point(5, 10), b.getCurrentPiecePosition());
        assertEquals(Action.RIGHT, b.getLastAction());
        assertEquals(Result.SUCCESS, b.getLastResult());
    }

    // testing that attempt to move past the wall returns OUT_BOUNDS and keeps the
    // piece at x=0
    @Test
    void testMoveWall() {
        Board b = createBoard();
        Piece square = new TetrisPiece(PieceType.SQUARE);
        b.nextPiece(square, new Point(0, 10));

        assertEquals(Result.OUT_BOUNDS, b.move(Action.LEFT));
        assertEquals(new Point(0, 10), b.getCurrentPiecePosition());
    }

    // testing that DOWN should move the piece by 1, and place it at the
    // bottom
    @Test
    void testMoveDownAndPlace() {
        Board b = createBoard();
        Piece square = new TetrisPiece(PieceType.SQUARE);
        b.nextPiece(square, new Point(0, 1));

        assertEquals(Result.SUCCESS, b.move(Action.DOWN));
        assertEquals(new Point(0, 0), b.getCurrentPiecePosition());

        assertEquals(Result.PLACE, b.move(Action.DOWN));
        assertNull(b.getCurrentPiece());
        assertNull(b.getCurrentPiecePosition());
        assertEquals(2, b.getMaxHeight());
        assertEquals(2, b.getColumnHeight(0));
        assertEquals(2, b.getColumnHeight(1));
        assertEquals(PieceType.SQUARE, b.getGrid(1, 1));
    }

    @Test
    void testDrop() {
        Board b = createBoard();
        Piece square = new TetrisPiece(PieceType.SQUARE);
        b.nextPiece(square, new Point(2, 10));

        assertEquals(Result.PLACE, b.move(Action.DROP));
        assertNull(b.getCurrentPiece());
        assertEquals(2, b.getMaxHeight());
        assertEquals(2, b.getColumnHeight(2));
        assertEquals(2, b.getColumnHeight(3));
        assertEquals(PieceType.SQUARE, b.getGrid(2, 0));
    }

    // testing rotations with space and at wall(should apply wallkick)
    @Test
    void testRotationsAndWallKick() {
        Board b = createBoard();
        Piece t = new TetrisPiece(PieceType.T);
        b.nextPiece(t, new Point(4, 10));

        // clockwise rotation
        assertEquals(Result.SUCCESS, b.move(Action.CLOCKWISE));
        assertEquals(1, b.getCurrentPiece().getRotationIndex());
        assertEquals(Action.CLOCKWISE, b.getLastAction());

        // counterclockwise rotation returns to 0
        assertEquals(Result.SUCCESS, b.move(Action.COUNTERCLOCKWISE));
        assertEquals(0, b.getCurrentPiece().getRotationIndex());

        // wall kick
        Piece stick = new TetrisPiece(PieceType.STICK);
        b.nextPiece(stick, new Point(0, 10));
        b.move(Action.CLOCKWISE);
        while (b.move(Action.LEFT) == Result.SUCCESS)
            ;
        assertEquals(Result.SUCCESS, b.move(Action.COUNTERCLOCKWISE));
        assertEquals(0, b.getCurrentPiece().getRotationIndex());
    }

    // clearRows() should update hight and rowsCleared
    @Test
    void testClearRows() {
        Board b = createBoard();
        Piece square = new TetrisPiece(PieceType.SQUARE);

        // fill bottom 2 rows using 5 square pieces across 10 columns
        for (int x = 0; x < 10; x += 2) {
            b.nextPiece(square, new Point(x, 10));
            b.move(Action.DROP);
        }

        assertEquals(2, b.getRowsCleared());
        assertEquals(0, b.getMaxHeight());
        for (int x = 0; x < 10; x++) {
            assertEquals(0, b.getColumnHeight(x));
            assertNull(b.getGrid(x, 0));
        }
    }

    // test the equals method between two of the same boards
    @Test
    void testEquals() {
        Board b1 = createBoard();
        Board b2 = createBoard();
        Piece square = new TetrisPiece(PieceType.SQUARE);

        b1.nextPiece(square, new Point(4, 10));
        b2.nextPiece(square, new Point(4, 10));
        assertTrue(b1.equals(b2));
    }
}
