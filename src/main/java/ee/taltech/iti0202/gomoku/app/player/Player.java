package ee.taltech.iti0202.gomoku.app.player;

import ee.taltech.iti0202.gomoku.app.board.Board;
import ee.taltech.iti0202.gomoku.app.board.ILocation;
import ee.taltech.iti0202.gomoku.app.exceptions.ThinkingTimeoutException;
import ee.taltech.iti0202.gomoku.app.player.statistics.Statistics;
import ee.taltech.iti0202.gomoku.app.player.strategy.Strategy;
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
    private Future<ILocation> future;
    private Paint color;
    private boolean isWhite;
    private Player opponent;

    public Player(Strategy strategy) {
        this.strategy = strategy;
        this.stats = new Statistics();
    }

    public ILocation getMove(Board board, long timeout) throws ThinkingTimeoutException, CancellationException {
        System.out.println(String.format("Player to move: %s", strategy.getName()));
        if (executor.isShutdown())
            executor = Executors.newSingleThreadExecutor();

        Task thinkingTask = new Task(board, isWhite);
        future = executor.submit(thinkingTask);
        ILocation move = null;

        try {
            long start = System.nanoTime();
            move = future.get(timeout, TimeUnit.NANOSECONDS);
            long end = System.nanoTime();
            double dt = (end - start) / Math.pow(10, 9);
            stats.addMove(dt);
            headToHeadStats.get(opponent).addMove(dt);
            // Thread.sleep(100); // TODO: allow setting in UI

        } catch (NullPointerException | InterruptedException | ExecutionException e) {
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

    public void terminateThinking() {
        executor.shutdown();
    }

    public void submitGame(Statistics.Result result) {
        strategy.onGameOver();
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

    private class Task implements Callable<ILocation> {

        private Board board;
        private boolean isWhite;

        Task(Board board, boolean isWhite) {
            this.board = board;
            this.isWhite = isWhite;
        }

        @Override
        public ILocation call() {
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
