package ee.taltech.iti0202.gomoku.app.player.strategy;

import ee.taltech.iti0202.gomoku.app.board.ILocation;
import ee.taltech.iti0202.gomoku.app.board.IBoard;

public interface GenericStrategy<T extends IBoard> {

    /**
     * Takes the game state and return the best move
     * @param board Board state
     * @param isWhite Player indicator. Which player's
     * strategy it is. (white or black)
     * @return A location where to make computer's move.
     *
     * @see T
     * @see ILocation
     */
    ILocation getMove(T board, boolean isWhite);

    /**
     * Name will be shown during the play.
     * This method should be overridden to
     * show student's name.
     * @return Name of the player
     */
    String getName();
}
