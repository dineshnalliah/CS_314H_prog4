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
    private Point[][] rotations;
    // private TetrisPiece clockWisePiece;
    // private TetrisPiece counterClockWisePiece;



    

    public TetrisPiece(PieceType type) {
        switch (type) {
            case T:
                rotationIndex = 0;
                body = type.getSpawnBody();

            case SQUARE:

            
            default:
        }
    }

    public TetrisPiece(PieceType type, int rotationIndex, Point[] body) {
        this.type = type;
        this.rotationIndex = rotationIndex;
        this.body = body;
        createOrientations();
    }

    private void createOrientations() {
        rotations = new Point[4][body.length];
        rotations[rotationIndex] = Arrays.copyOf(body, body.length);
        Point[] temp = Arrays.copyOf(body, body.length);

        for (int i = (rotationIndex + 1 % 4); i != rotationIndex; i = (i + 1 % 4)) {
            rotations[i] = performClockWiseRotation(body, (int)(type.getBoundingBox().getWidth()));
            temp = Arrays.copyOf(rotations[i], body.length);
        }
    }

    private Point[] performClockWiseRotation(Point[] cur, int dimension) {
        Point[] transformed = new Point[cur.length];
        for (int i = 0 ; i < cur.length; i++) {
            transformed[i] = new Point((int)cur[i].getY(),(int)(dimension - 1 - cur[i].getX()));
        }
        return transformed;
    }

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
        return new TetrisPiece(type, (rotationIndex + 1) % 4, rotations[(rotationIndex + 1) % 4]);
    }

    @Override
    public Piece counterclockwisePiece() {
        return new TetrisPiece(type, (rotationIndex - 1) % 4, rotations[(rotationIndex - 1) % 4]);
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

