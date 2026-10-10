package assignment;

import java.awt.Point;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a Tetris board -- essentially a 2-d grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2-d board.
 */
public final class TetrisBoard implements Board {

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

    // copy constructor used for testMove() 
    private TetrisBoard(TetrisBoard other) {
        this.width = other.width;
        this.height = other.height;

        this.grid = new Piece.PieceType[height][];

        // have to clone by row so references aren't shared between the copies
        for (int y = 0; y < height; y++) {
            this.grid[y] = other.grid[y].clone();
        }
        this.rowWidth = other.rowWidth.clone();
        this.colHeights = other.colHeights.clone();
        this.maxHeight = other.maxHeight;

        this.currentPiece = other.currentPiece; // immutable so can directly assign
        this.currentPosition = other.currentPosition == null ? null : new Point(other.currentPosition); // point is mutable so need to create new object

        this.lastAction = other.lastAction;
        this.lastResult = other.lastResult;
        this.rowsCleared = other.rowsCleared;
    }

    @Override
    public Result move(Action act) { 

        lastAction = act;
        rowsCleared = 0; // upon a new action rowsCleared needs to be reset

        if (currentPiece == null) { // should trigger nextPiece()
            lastResult = Result.NO_PIECE;
            return lastResult;
        }

        switch (act) {
            case LEFT: 
                lastResult = tryHorizontalShift(-1, currentPosition.x, currentPosition.y);
                break;
            case RIGHT: 
                lastResult = tryHorizontalShift(1, currentPosition.x, currentPosition.y);
                break;
            case DOWN: 
                lastResult = tryVerticalShift(-1, currentPosition.x, currentPosition.y);
                break;
            case DROP:
                lastResult = drop();
                break;
            case CLOCKWISE:
                lastResult = tryRotate(true);
                break;
            case COUNTERCLOCKWISE:
                lastResult = tryRotate(false);
                break;
            case NOTHING:
                lastResult = Result.SUCCESS;
                break;
            default:
                lastResult = Result.SUCCESS; 
                break;
        }

        return lastResult;
    }

    // helper to check if all points in the body of a piece are in valid positions of the grid
    private boolean isValid(Piece piece, int x, int y) {

        Point[] temp = piece.getBody();

        for (Point p : temp) {
            // body is relative to bounding box position
            int x2 = x + p.x;
            int y2 = y + p.y;

            if (x2 < 0 || x2 >= width || y2 < 0 || y2 >= height) { // out of bounds
                return false;
            }

            if (grid[y2][x2] != null) return false;  // already taken
        }

        return true;
    }

    // helper that returns the array of valid wall kicks a piece can perform given its type and rotation direction
    private static Point[] getPossibleKicks(Piece piece, boolean clockwise) {
        int from = piece.getRotationIndex(); 
        switch (piece.getType()) {
            case SQUARE:
                return new Point[] {new Point(0, 0)}; // no kicks
            case STICK:
                return clockwise ? Piece.I_CLOCKWISE_WALL_KICKS[from] : Piece.I_COUNTERCLOCKWISE_WALL_KICKS[from];
            default:
                return clockwise ? Piece.NORMAL_CLOCKWISE_WALL_KICKS[from] : Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[from];
        }
    }

    // helper for lateral movement
    private Result tryHorizontalShift(int dx, int x, int y) {
        if (isValid(currentPiece, x + dx, y)) {
            currentPosition = new Point(x + dx, y);
            return Result.SUCCESS;
        }

        return Result.OUT_BOUNDS;
    }

    // helper for downwards movement
    private Result tryVerticalShift(int dy, int x, int y) {
        if (isValid(currentPiece, x, y + dy)) {
            currentPosition = new Point(x, y + dy);
            return Result.SUCCESS;
        }

        placePiece();
        return Result.PLACE;
    }


    // helper 
    private Result drop() { // could be optimized maybe
        int y = currentPosition.y;
        while (isValid(currentPiece, currentPosition.x, y - 1)) {
            y--;
        }
        currentPosition = new Point(currentPosition.x, y);
        placePiece();
        return Result.PLACE;
    }

    // common helper for piece placement
    private void placePiece() {
        for (Point p : currentPiece.getBody()) {
            int x = currentPosition.x + p.x;
            int y = currentPosition.y + p.y;

            grid[y][x] = currentPiece.getType();
            colHeights[x] = Math.max(colHeights[x], y+1); // compare the previous columnheight to the current y
            rowWidth[y]++; // another block has been added to row y
            maxHeight = Math.max(maxHeight, colHeights[x]); // maxheight will be replaced if colheight has been updated and is larger
        }

        // see if the placing filled any rows
        rowsCleared = clearRows();

        // this piece is no longer playable after placing
        currentPiece = null; // will make next move() call return NO_PIECE
        currentPosition = null;
    }

    private int clearRows() {
        int cur = 0;

        for (int r = 0; r < height; r++) {
            if (rowWidth[r] == width) continue; // dont save rows that have been cleared
            grid[cur] = grid[r];
            rowWidth[cur] = rowWidth[r];
            cur++;
        }

        int clearedRows = height - cur;

        // fill the new rows
        for (; cur < height; cur++) {
            grid[cur] = new Piece.PieceType[width];
            rowWidth[cur] = 0;
        }

        if (clearedRows > 0) recalculateHeights();
        
        return clearedRows;
    }

    private void recalculateHeights() {
        int previousHighest = maxHeight - 1;
        maxHeight = 0;
        for (int x = 0; x < width; x++) {
            int h = 0;
            for (int y = previousHighest; y >= 0; y--) {
                if (grid[y][x] != null) {
                    h = y + 1;
                    break;
                }
            }
            colHeights[x] = h;
            maxHeight = Math.max(maxHeight, h);
        }
    }

    private Result tryRotate(boolean clockwise) {
        Piece rotated = clockwise ? currentPiece.clockwisePiece() : currentPiece.counterclockwisePiece();

        for (Point kick : getPossibleKicks(currentPiece, clockwise)) {
            int newX = currentPosition.x + kick.x;
            int newY = currentPosition.y + kick.y;
            if (isValid(rotated, newX, newY)) {
                currentPiece = rotated;
                currentPosition = new Point(newX, newY);
                return Result.SUCCESS;
            }
        }
        return Result.OUT_BOUNDS;
    }



    @Override
    public Board testMove(Action act) { 
        TetrisBoard copy = new TetrisBoard(this);
        copy.move(act);
        return copy; 
    }

    @Override
    public Piece getCurrentPiece() { 
        return currentPiece; 
    }

    @Override
    public Point getCurrentPiecePosition() { 
        return currentPosition == null ? null : new Point(currentPosition); 
    }

    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        // check if spawn pos is valid
        if (p == null || spawnPosition == null) {
            System.err.println("Piece and position must not be null");
            return;
        }
        if (!isValid(p, spawnPosition.x, spawnPosition.y)) {
            System.err.println("Piece does not fit at " + spawnPosition);
            return;
        }

        currentPiece = p;
        currentPosition = new Point(spawnPosition);
    }

    @Override
    public boolean equals(Object other) { 
        if (!(other instanceof TetrisBoard)) return false;
        TetrisBoard o = (TetrisBoard) other;
        // deepequals because 2D array
        return width == o.width && height == o.height && Objects.equals(currentPiece, o.currentPiece) && Objects.equals(currentPosition, o.currentPosition) && Arrays.deepEquals(grid, o.grid);
    }

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
        int[] skirt = piece.getSkirt();
        int result = Integer.MIN_VALUE;

        for (int i = 0; i < skirt.length; i++) {
            if (skirt[i] == Integer.MAX_VALUE) continue; // no blocks in this column
            int landing = colHeights[x + i] - skirt[i];
            result = Math.max(result, landing);
        }
        return result;
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
