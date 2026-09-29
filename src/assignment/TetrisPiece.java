package assignment;

import java.awt.Point;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 * 
 * All operations on a TetrisPiece should be constant time, except for it's
 * initial construction. This means that rotations should also be fast - calling
 * clockwisePiece() and counterclockwisePiece() should be constant time! You may
 * need to do precomputation in the constructor to make this possible.
 */
public final class TetrisPiece implements Piece {

    /**
     * Construct a tetris piece of the given type. The piece should be in it's spawn orientation,
     * i.e., a rotation index of 0.
     * 
     * You may freely add additional constructors, but please leave this one - it is used both in
     * the runner code and testing code.
     */
    
    private PieceType type;
    private Point[] body;
    private int rotationIndex;
    private TetrisPiece clockwise;
    private TetrisPiece counterclockwise;

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


        // should close the circle, 'this' is still the initial piece, and before is now the last rotation
        before.clockwise = this;
        this.counterclockwise = before;
    }

    public TetrisPiece(PieceType type, int rotationIndex, Point[] body) {
        this.type = type;
        this.rotationIndex = rotationIndex;
        this.body = body;
    }

    private Point[] performClockWiseRotation(Point[] cur, int dimension) {
        Point[] transformed = new Point[cur.length];
        for (int i = 0 ; i < cur.length; i++) {
            transformed[i] = new Point((int)cur[i].getY(),(int)(dimension - 1 - cur[i].getX()));
        }
        return transformed;
    }


    // dont think this is relevant anymore
    private Point[] performCounterClockWiseRotation(Point[] cur, int dimension) {
        Point[] transformed = new Point[cur.length];
        for (int i = 0 ; i < cur.length; i++) {
            transformed[i] = new Point((int)(dimension - 1 - cur[i].getY()),(int)(cur[i].getX()));
        }
        return transformed;
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
        // TODO: Implement me.
        return -1;
    }

    @Override
    public int getHeight() {
        // TODO: Implement me.
        return -1;
    }

    @Override
    public Point[] getBody() {
        return body;
    }

    @Override
    public int[] getSkirt() {
        // TODO: Implement me.
        return null;
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;

        // TODO: Implement me.
        return false;
    }
}

