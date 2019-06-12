package ee.kilpkonn.app;

import ee.kilpkonn.app.board.Board;
import ee.kilpkonn.app.controllers.GameController;
import ee.kilpkonn.app.exceptions.LocationOccupiedException;
import ee.kilpkonn.app.exceptions.ThinkingTimeoutException;
import ee.kilpkonn.app.player.Player;
import ee.kilpkonn.app.player.statistics.Statistics;

public class GameSession {

    private GameController gameController;
    private Player whitePlayer;
    private Player blackPlayer;
    private Board board;
    private GameState state;
    private boolean whiteToMove = true;
    private long timeout = 1000 * 5;

    public GameSession(GameController gameController, Player whitePlayer, Player blackPlayer, int boardWidth,
                       int boardHeight) {
        this.gameController = gameController;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        whitePlayer.setIsWhite(true);
        blackPlayer.setIsWhite(false);

        board = new Board(boardWidth, boardHeight);
        state = GameState.PLAYING;
    }

    public void nextMove() {
        Board.Location move;

        try {
            if (whiteToMove) {
                move = whitePlayer.getMove(board, timeout);
                board.makeMove(move, Board.Stone.WHITE);
            } else {
                move = blackPlayer.getMove(board, timeout);
                board.makeMove(move, Board.Stone.BLACK);
            }
            gameController.makeMove(move, whiteToMove ? whitePlayer : blackPlayer);
            gameController.updateStats(whitePlayer, blackPlayer);
        } catch (LocationOccupiedException | ThinkingTimeoutException e) {
            e.printStackTrace();
            // TODO: Penalty here?
        } finally {
            state = board.getGameState();
            whiteToMove = !whiteToMove;
        }
    }

    public void submitGame() {
        switch (state) {
            case WHITE_WON:
                whitePlayer.submitGame(Statistics.Result.WIN);
                blackPlayer.submitGame(Statistics.Result.LOSS);
                break;
            case BLACK_WON:
                whitePlayer.submitGame(Statistics.Result.LOSS);
                blackPlayer.submitGame(Statistics.Result.WIN);
                break;
            case DRAW:
                whitePlayer.submitGame(Statistics.Result.DRAW);
                blackPlayer.submitGame(Statistics.Result.DRAW);
                break;
        }
    }

    public GameState getState() {
        return state;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public enum GameState {
        PLAYING,
        WHITE_WON,
        BLACK_WON,
        DRAW
    }
}
