package assignment;

import java.awt.Point;
import java.util.Arrays;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 * 
 * All operations on a TetrisPiece should be constant time, except for it's
 * initial construction. This means that rotations should also be fast - calling
 * clockwisePiece() and counterclockwisePiece() should be constant time! You may
 * need to do precomputation in the constructor to make this possible.
 */
public final class TetrisPiece implements Piece {

    private PieceType type;
    private Point[] body;
    private int rotationIndex;
    private int[] skirt;
    private TetrisPiece clockwise;
    private TetrisPiece counterclockwise;

    /**
     * Construct a tetris piece of the given type. The piece should be in it's spawn orientation,
     * i.e., a rotation index of 0.
     * 
     * You may freely add additional constructors, but please leave this one - it is used both in
     * the runner code and testing code.
     */

    public TetrisPiece(PieceType type) {

        // don't even have to do a switch case since all pieces have 4 rotations

        // first one will be the default and thus index 0
        this(type, 0, type.getSpawnBody());

        int dimension = type.getBoundingBox().width; // bounding box side length
        TetrisPiece before = this; // start with the original

        for (int i = 1; i < 4; i++) {
            // create a clockwise rotation
            TetrisPiece next = new TetrisPiece(type, i, performClockWiseRotation(body, dimension));

            // link the two pieces immediately
            before.clockwise = next;
            next.counterclockwise = before;

            // shift to the next rotation
            before = next;
        }


        // closing the circle
        before.clockwise = this;
        this.counterclockwise = before;
    }

    public TetrisPiece(PieceType type, int rotationIndex, Point[] body) {
        this.type = type;
        this.rotationIndex = rotationIndex;
        this.body = body;
        this.skirt = calculateSkirt();
    }

    private Point[] performClockWiseRotation(Point[] cur, int dimension) {
        Point[] transformed = new Point[cur.length];
        for (int i = 0 ; i < cur.length; i++) {
            transformed[i] = new Point((int)cur[i].getY(),(int)(dimension - 1 - cur[i].getX()));
        }
        return transformed;
    }

    private int[] calculateSkirt() {

        int[] temp = new int[type.getBoundingBox().width];
        Arrays.fill(temp, Integer.MAX_VALUE);

        for (Point p : body) {
            temp[p.x] = Math.min(p.y, temp[p.x]);
        }

        return skirt;
    }

    @Override
    public PieceType getType() {
        return type;
    }

    @Override
    public int getRotationIndex() {
        return rotationIndex;
    }

    @Override
    public Piece clockwisePiece() {
        return clockwise;
    }

    @Override
    public Piece counterclockwisePiece() {
        return counterclockwise;
    }

    @Override
    public int getWidth() {
        return type.getBoundingBox().width;
    }

    @Override
    public int getHeight() {
        return type.getBoundingBox().height;
    }

    @Override
    public Point[] getBody() {
        return body;
    }

    @Override
    public int[] getSkirt() {
        return skirt;
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;

        TetrisPiece otherPiece = (TetrisPiece) other;
        
        // check for piece type and orientation equality
        return this.type.equals(otherPiece.type) && this.rotationIndex == otherPiece.rotationIndex ? true : false;
    }
}

