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
    private Player player1;
    private Player player2;
    private Board board;
    private GameState state;
    private boolean whiteToMove;
    private long player1Timeout;
    private long player2Timeout;
    private List<Board.Location> moves;
    private Board.Location currentMove;
    private static final Board.Location EMPTY_MOVE = new Board.Location(-1, -1);

    public GameSession(GameController gameController, Player player1, Player player2, int boardWidth,
                       int boardHeight, long player1Timeout, long player2Timeout) {
        this.gameController = gameController;
        this.player1 = player1;
        this.player2 = player2;
        this.player1Timeout = player1Timeout;
        this.player2Timeout = player2Timeout;

        moves = new ArrayList<>();
        board = new Board(boardWidth, boardHeight);
        state = GameState.PLAYING;
        whiteToMove = true;
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
                player1.setIsWhite(true);
                currentMove = player1.getMove(board, player1Timeout);
                board.makeMove(currentMove, Board.Stone.WHITE);
            } else {
                player2.setIsWhite(false);
                currentMove = player2.getMove(board, player2Timeout);
                board.makeMove(currentMove, Board.Stone.BLACK);
            }
            gameController.makeMove(currentMove, whiteToMove ? player1 : player2);
            gameController.updateStats(player1, player2);

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
            gameController.makeMove(currentMove, whiteToMove ? player1 : player2);
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
            player1.cancelThinking();
        } else {
            player2.cancelThinking();
        }
    }

    public void submitGame() {
        switch (state) {
            case WHITE_WON:
                player1.submitGame(Statistics.Result.WIN);
                player2.submitGame(Statistics.Result.LOSS);
                break;
            case BLACK_WON:
                player1.submitGame(Statistics.Result.LOSS);
                player2.submitGame(Statistics.Result.WIN);
                break;
            case DRAW:
                player1.submitGame(Statistics.Result.DRAW);
                player2.submitGame(Statistics.Result.DRAW);
                break;
        }
    }

    public GameState getState() {
        return state;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public long getPlayer1Timeout() {
        return player1Timeout;
    }

    public long getPlayer2Timeout() {
        return player2Timeout;
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
