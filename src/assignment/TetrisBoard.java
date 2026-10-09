package assignment;

import java.awt.Point;

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

        lastAction = Action.NOTHING;
        lastResult = Result.NO_PIECE;

        colHeights = new int[width];
        rowWidth = new int[height];

        rowsCleared = 0;
    }

    @Override
    public Result move(Action act) { 

        lastAction = act;
        rowsCleared = 0;

        // change so lastResult is also updated
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

    private boolean isValid(Piece piece, int x, int y) {

        Point[] temp = piece.getBody();

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
        if (isValid(currentPiece, x + dx, y)) {
            currentPosition = new Point(x + dx, y);
            return Result.SUCCESS;
        }

        return Result.OUT_BOUNDS;
    }

    private Result tryVerticalShift(int dy, int x, int y) {
        if (isValid(currentPiece, x, y + dy)) {
            currentPosition = new Point(x, y + dy);
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
            colHeights[x] = Math.max(colHeights[x], y+1);
            rowWidth[y]++;
            maxHeight = Math.max(maxHeight, colHeights[x]);
        }

        // need to call some clearRows() method here, update rowsCleared

        // this piece is no longer playable after placing
        currentPiece = null; // will make next move() call return NO_PIECE
        currentPosition = null;
    }

    // private int clearRows() {}
    // this method will also need another private method to recalculate the heights, since clearing rows shifts heights

    // private void recalculateHeights() {}

    



    @Override
    public Board testMove(Action act) { return null; }

    @Override
    public Piece getCurrentPiece() { 
        return currentPiece; 
    }

    @Override
    public Point getCurrentPiecePosition() { 
        return new Point(currentPosition); 
    }

    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        // check if spawn pos is valid
        if (p == null || spawnPosition == null) {
            throw new IllegalArgumentException("Piece and position must not be null");
        }
        if (isValid(p, spawnPosition.x, spawnPosition.y)) {
            throw new IllegalArgumentException("Piece does not fit at " + spawnPosition);
        }

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
        if (x < 0 || x >= width || y < 0 || y >= height) return null;
        return grid[y][x]; 
    }
}
