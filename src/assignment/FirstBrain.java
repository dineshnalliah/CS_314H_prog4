package assignment;

import java.util.ArrayList;

import assignment.Board.Action;

public class FirstBrain implements Brain {

    private ArrayList<Board> options;
    private ArrayList<Board.Action> firstMoves;

    /**
     * Decide what the next move should be based on the state of the board.
     */
    public Board.Action nextMove(Board currentBoard) {
        // Fill the our options array with versions of the new Board
        options = new ArrayList<>();
        firstMoves = new ArrayList<>();
        enumerateOptions(currentBoard);

        int best = 0;
        int bestIndex = 0;

        // Check all of the options and get the one with the highest score
        for (int i = 0; i < options.size(); i++) {
            int score = scoreBoard(options.get(i));
            if (score > best) {
                best = score;
                bestIndex = i;
            }
        }

        // We want to return the first move on the way to the best Board
        return firstMoves.get(bestIndex);
    }

    /**
     * Test all of the places we can put the current Piece.
     * Since this is just a Lame Brain, we aren't going to do smart
     * things like rotating pieces.
     */
        /**
     * Test all of the places we can put the current Piece, in every rotation.
     */
    private void enumerateOptions(Board currentBoard) {
        Board rotated = currentBoard;

        for (int r = 0; r < 4; r++) {
            Board.Action rotationMove = (r == 0) ? null : Board.Action.CLOCKWISE;
            addColumnOptions(rotated, rotationMove);

            // Rotate once more for the next pass; stop if the rotation is blocked
            rotated = rotated.testMove(Board.Action.CLOCKWISE);
            if (rotated.getLastResult() != Board.Result.SUCCESS) break;
        }
    }

    private void addColumnOptions(Board start, Board.Action rotationMove) {
        // We can always drop where we are
        options.add(start.testMove(Board.Action.DROP));
        firstMoves.add(rotationMove != null ? rotationMove : Board.Action.DROP);

        // All the places to the left we can DROP
        Board left = start.testMove(Board.Action.LEFT);
        while (left.getLastResult() == Board.Result.SUCCESS) {
            options.add(left.testMove(Board.Action.DROP));
            firstMoves.add(rotationMove != null ? rotationMove : Board.Action.LEFT);
            left.move(Board.Action.LEFT);
        }

        // And then the same thing to the right
        Board right = start.testMove(Board.Action.RIGHT);
        while (right.getLastResult() == Board.Result.SUCCESS) {
            options.add(right.testMove(Board.Action.DROP));
            firstMoves.add(rotationMove != null ? rotationMove : Board.Action.RIGHT);
            right.move(Board.Action.RIGHT);
        }
    }

    /**
     * Since we're trying to avoid building too high,
     * we're going to give higher scores to Boards with
     * MaxHeights close to 0.
     */
    private int scoreBoard(Board newBoard) {
        return 100 - (newBoard.getMaxHeight() * 5);
    }
    
}
