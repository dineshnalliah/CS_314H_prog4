package assignment;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import assignment.Piece.PieceType;

/*
 * Any comments and methods here are purely descriptions or suggestions.
 * This is your test file. Feel free to change this as much as you want.
 */

public class BlackBoxTetrisPieceTest {

    // helper to convert the Point[] body into a set so the order of points is
    // irrelevant in comparison
    private static HashSet<Point> bodySet(Piece piece) {
        return new HashSet<>(Arrays.asList(piece.getBody()));
    }

    // new piece should have the type it was built width and start in default
    // orientation (rotationIndex == 0)
    @Test
    void testNewPieceTypeAndRotation() {
        Piece t = new TetrisPiece(PieceType.T);
        assertEquals(PieceType.T, t.getType());
        assertEquals(0, t.getRotationIndex());
    }

    // clockwise rotation should set rotationIndex to 1 when starting from new, and
    // wrap around to 3 counterclockwise
    @Test
    void testRotationIndexWraps() {
        Piece t = new TetrisPiece(PieceType.T);
        assertEquals(1, t.clockwisePiece().getRotationIndex());
        assertEquals(3, t.counterclockwisePiece().getRotationIndex());
    }

    // four rotations in some same direction should return the orientation to
    // default
    @Test
    void testFourRotationsReturnToStart() {
        for (PieceType type : PieceType.values()) {
            Piece p = new TetrisPiece(type);

            Piece rotated = p.clockwisePiece().clockwisePiece().clockwisePiece().clockwisePiece();
            assertEquals(p, rotated, "Failed for " + type);
            assertEquals(bodySet(p), bodySet(rotated), "Body mismatch for " + type);

            Piece rotatedAgain = p.counterclockwisePiece().counterclockwisePiece().counterclockwisePiece()
                    .counterclockwisePiece();
            assertEquals(p, rotatedAgain, "Failed for " + type);
            assertEquals(bodySet(p), bodySet(rotatedAgain), "Body mismatch for " + type);
        }
    }

    // a clockwise rotation followed by counterclockwise should return back to the
    // start
    @Test
    void testCWThenCCW() {
        for (PieceType type : PieceType.values()) {
            Piece p = new TetrisPiece(type);

            Piece rotated = p.clockwisePiece().counterclockwisePiece();
            assertEquals(p, rotated, "Failed for " + type);
            assertEquals(bodySet(p), bodySet(rotated), "Body mismatch for " + type);
        }
    }

    // making sure the second and third clockwise rotations are as expected for the
    // T shape
    @Test
    void testCWRotations2and3() {
        Piece t0 = new TetrisPiece(PieceType.T);
        Piece t1 = t0.clockwisePiece();
        Piece t2 = t1.clockwisePiece();
        Piece t3 = t2.clockwisePiece();

        HashSet<Point> expected2 = new HashSet<>(Arrays.asList(
                new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 0)));
        HashSet<Point> expected3 = new HashSet<>(Arrays.asList(
                new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 1)));

        assertEquals(expected2, bodySet(t2));
        assertEquals(expected3, bodySet(t3));
    }

    // width and height should be equal to the width and height of bounding box, and
    // should remain the same after rotation
    @Test
    void testWidthAndHeight() {
        Piece stick = new TetrisPiece(PieceType.STICK);
        assertEquals(4, stick.getWidth());
        assertEquals(4, stick.getHeight());
        assertEquals(4, stick.clockwisePiece().getWidth());

        Piece square = new TetrisPiece(PieceType.SQUARE);
        assertEquals(2, square.getWidth());
        assertEquals(2, square.getHeight());
    }

    // testing the specific T tetronimo to ensure the clockwise operation works
    @Test
    void testBodyAfterClockwiseRotation() {
        Piece t = new TetrisPiece(PieceType.T).clockwisePiece();
        HashSet<Point> expected = new HashSet<>(
                Arrays.asList(new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 1)));
        assertEquals(expected, bodySet(t));
    }

    // ensure the skirt computation is correct for the T tetronimo's rotations at 0
    // and 1
    @Test
    void testSkirt() {
        Piece t = new TetrisPiece(PieceType.T);
        assertArrayEquals(new int[] { 1, 1, 1 }, t.getSkirt());
        // Rotated T has an empty left column
        assertArrayEquals(new int[] { Integer.MAX_VALUE, 0, 1 }, t.clockwisePiece().getSkirt());
        assertArrayEquals(new int[] { 1, 0, 1 }, t.clockwisePiece().clockwisePiece().getSkirt());
        assertArrayEquals(new int[] { 1, 0, Integer.MAX_VALUE },
                t.clockwisePiece().clockwisePiece().clockwisePiece().getSkirt());
    }

    // ensure tetris pieces with the same type and orientation are equal
    @Test
    void testEquals() {
        Piece a = new TetrisPiece(PieceType.T);
        Piece b = new TetrisPiece(PieceType.T);
        assertTrue(a.equals(b));
        assertNotEquals(a, a.clockwisePiece());
        assertNotEquals(a, new TetrisPiece(PieceType.SQUARE));
    }
}
