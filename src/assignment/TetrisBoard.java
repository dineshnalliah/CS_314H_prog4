package assignment;

import java.awt.*;

/**
 * Represents a Tetris board -- essentially a 2-d grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2-d board.
 */
public final class TetrisBoard implements Board {

    // dimensions
    private int width;
    private int height;

    private Piece.PieceType[][] grid;

    private Piece currentPiece;
    private Point currentPosition;

    private Result lastResult;
    private Action lastAction;

    private int[] colHeights;
    private int[] rowWidth;
    private int maxHeight;

    private int rowsCleared;


    // JTetris will use this constructor
    public TetrisBoard(int width, int height) {
        this.width = width;
        this.height = height;

        grid = new Piece.PieceType[height][width];
    }

    @Override
    public Result move(Action act) { 

        lastAction = act;

        switch (act) {
            case LEFT: 
                return tryHorizontalShift(-1, currentPosition.x, currentPosition.y);
            case RIGHT: 
                return tryHorizontalShift(1, currentPosition.x, currentPosition.y);
            case DOWN: 
                return tryVerticalShift(-1, currentPosition.x, currentPosition.y);
            case DROP:
                return Result.SUCCESS;
            case CLOCKWISE:
                return Result.SUCCESS;
            case COUNTERCLOCKWISE:
                return Result.SUCCESS;
            case NOTHING:
                return Result.SUCCESS;
            default:
                return Result.NO_PIECE; 
        }
    }

    private boolean isValid(int x, int y) {

        Point[] temp = currentPiece.getBody();

        for (Point p : temp) {
            int x2 = x + p.x;
            int y2 = y + p.y;

            if (x2 < 0 || x2 >= width || y2 < 0 || y2 >= height) {
                return false;
            }

            if (grid[y2][x2] != null) return false;
        }

        return true;
    }

    private Result tryHorizontalShift(int dx, int x, int y) {
        if (isValid(x + dx, y)) {
            currentPosition.x = x + dx;
            currentPosition.y = y;
            return Result.SUCCESS;
        }

        return Result.OUT_BOUNDS;
    }

    private Result tryVerticalShift(int dy, int x, int y) {
        if (isValid(x, y + dy)) {
            currentPosition.x = x;
            currentPosition.y = y + dy;
            return Result.SUCCESS;
        }

        placePiece();
        return Result.PLACE;
    }

    private void placePiece() {
        for (Point p : currentPiece.getBody()) {
            int x = currentPosition.x + p.x;
            int y = currentPosition.y + p.y;
            grid[y][x] = currentPiece.getType();
        }
    }

    

    @Override
    public Board testMove(Action act) { return null; }

    @Override
    public Piece getCurrentPiece() { 
        return currentPiece; 
    }

    @Override
    public Point getCurrentPiecePosition() { 
        return currentPosition; 
    }

    @Override
    public void nextPiece(Piece p, Point spawnPosition) {

        // check if spawn pos is valid

        currentPiece = p;
        currentPosition = new Point(spawnPosition);
    }

    @Override
    public boolean equals(Object other) { return false; }

    @Override
    public Result getLastResult() { 
        return lastResult; 
    }

    @Override
    public Action getLastAction() { 
        return lastAction; 
    }

    @Override
    public int getRowsCleared() { 
        return rowsCleared; 
    }

    @Override
    public int getWidth() { 
        return width; 
    }

    @Override
    public int getHeight() { 
        return height; 
    }

    @Override
    public int getMaxHeight() { 
        return maxHeight; 
    }

    @Override
    public int dropHeight(Piece piece, int x) { 
        // calculate across the width of the bounding box for the max of getColumnHeight
        return -1; 
    }

    @Override
    public int getColumnHeight(int x) { 
        return colHeights[x]; 
    }

    @Override
    public int getRowWidth(int y) { 
        return rowWidth[y]; 
    }

    @Override
    public Piece.PieceType getGrid(int x, int y) { 
        // check bounds
        return grid[y][x]; 
    }
}
