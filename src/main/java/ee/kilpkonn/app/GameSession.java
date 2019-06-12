package ee.kilpkonn.app;

import ee.kilpkonn.app.board.Board;
import ee.kilpkonn.app.controllers.GameController;
import ee.kilpkonn.app.exceptions.LocationOccupiedException;
import ee.kilpkonn.app.exceptions.ThinkingTimeoutException;
import ee.kilpkonn.app.player.Player;
import ee.kilpkonn.app.player.statistics.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

public class GameSession {

    private GameController gameController;
    private Player whitePlayer;
    private Player blackPlayer;
    private Board board;
    private GameState state;
    private boolean whiteToMove = true;
    private long timeout = 1000 * 5;
    private List<Board.Location> moves;
    private Board.Location currentMove;
    private static final Board.Location EMPTY_MOVE = new Board.Location(-1, -1);

    public GameSession(GameController gameController, Player whitePlayer, Player blackPlayer, int boardWidth,
                       int boardHeight) {
        this.gameController = gameController;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        whitePlayer.setIsWhite(true);
        blackPlayer.setIsWhite(false);

        moves = new ArrayList<>();
        board = new Board(boardWidth, boardHeight);
        state = GameState.PLAYING;
    }

    public void playMove() {
        int index = moves.indexOf(currentMove) + 1;
        if (index < moves.size()) {
            moves = moves.stream()
                    .limit(index)
                    .collect(Collectors.toList());
        }

        try {
            if (whiteToMove) {
                currentMove = whitePlayer.getMove(board, timeout);
                board.makeMove(currentMove, Board.Stone.WHITE);
            } else {
                currentMove = blackPlayer.getMove(board, timeout);
                board.makeMove(currentMove, Board.Stone.BLACK);
            }
            gameController.makeMove(currentMove, whiteToMove ? whitePlayer : blackPlayer);
            gameController.updateStats(whitePlayer, blackPlayer);

            moves.add(currentMove);

        } catch (LocationOccupiedException | ThinkingTimeoutException | CancellationException e) {
            e.printStackTrace();
            currentMove = EMPTY_MOVE.clone();
            moves.add(currentMove);
            // TODO: Penalty here?
        } finally {
            state = board.getGameState();
            whiteToMove = !whiteToMove;
        }
    }

    public boolean nextMove() {

        int index = moves.indexOf(currentMove) + 1;

        if (index >= moves.size()) return false;

        currentMove = moves.get(index);
        try {
            board.makeMove(currentMove, whiteToMove ? Board.Stone.WHITE : Board.Stone.BLACK);
            gameController.makeMove(currentMove, whiteToMove ? whitePlayer : blackPlayer);
        } catch (LocationOccupiedException e) {
            e.printStackTrace();
        } finally {
            state = board.getGameState();
            whiteToMove = !whiteToMove;
        }
        return true;
    }

    public boolean previousMove() {
        board.reverseMove(currentMove);
        gameController.reverseMove(currentMove);

        state = board.getGameState();
        whiteToMove = !whiteToMove;

        int index = moves.indexOf(currentMove) - 1;

        if (index < 0) {
            currentMove = null;
            return false;
        }

        currentMove = moves.get(index);
        return true;
    }

    public void cancelMove() {
        if (whiteToMove) {
            whitePlayer.cancelThinking();
        } else {
            blackPlayer.cancelThinking();
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
