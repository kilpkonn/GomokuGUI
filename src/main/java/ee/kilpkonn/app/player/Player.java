package ee.kilpkonn.app.player;

import ee.kilpkonn.app.board.Board;
import ee.kilpkonn.app.exceptions.ThinkingTimeoutException;
import ee.kilpkonn.app.player.statistics.Statistics;
import ee.kilpkonn.app.player.strategy.Strategy;
import javafx.scene.paint.Paint;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Player {

    private Strategy strategy;
    private Statistics stats;
    private Map<Player, Statistics> headToHeadStats = new HashMap<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<Board.Location> future;
    private Paint color;
    private boolean isWhite;
    private Player opponent;

    public Player(Strategy strategy) {
        this.strategy = strategy;
        this.stats = new Statistics();
    }

    public Board.Location getMove(Board board, long timeout) throws ThinkingTimeoutException, CancellationException {
        Task thinkingTask = new Task(board, isWhite);
        future = executor.submit(thinkingTask);
        Board.Location move = null;

        try {
            long start = System.nanoTime();
            move = future.get(timeout, TimeUnit.NANOSECONDS);
            long end = System.nanoTime();
            double dt = (end - start) / Math.pow(10, 9);
            stats.addMove(dt);
            headToHeadStats.get(opponent).addMove(dt);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            // e.printStackTrace();
            throw new ThinkingTimeoutException("Thinking took too long :(");
        }
        return move;  //TODO: handle error somehow
    }

    public void cancelThinking() {
        future.cancel(true);
    }

    public void submitGame(Statistics.Result result) {
        stats.submitGame(result);
        headToHeadStats.get(opponent).submitGame(result);
    }

    public void setOpponent(Player opponent) {
        headToHeadStats.putIfAbsent(opponent, new Statistics());
        this.opponent = opponent;
    }

    public void setIsWhite(boolean isWhite) {
        this.isWhite = isWhite;
        this.color = strategy.getColor(isWhite);
    }

    public Paint getColor() {
        return color;
    }

    private class Task implements Callable<Board.Location> {

        private Board board;
        private boolean isWhite;

        Task(Board board, boolean isWhite) {
            this.board = board;
            this.isWhite = isWhite;
        }

        @Override
        public Board.Location call() {
            return strategy.getMove(board, isWhite);
        }
    }

    public Statistics getStats(Player opponent) {
        return headToHeadStats.get(opponent);
    }

    public Statistics getStats() {
        return stats;
    }

    public String getName() {
        return strategy.getName();
    }

    public Strategy getStrategy() {
        return strategy;
    }
}
