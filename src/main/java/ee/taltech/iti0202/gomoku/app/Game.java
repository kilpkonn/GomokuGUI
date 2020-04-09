package ee.taltech.iti0202.gomoku.app;

import ee.taltech.iti0202.gomoku.app.controllers.GameController;
import ee.taltech.iti0202.gomoku.app.controllers.MenuController;
import ee.taltech.iti0202.gomoku.app.player.Player;
import ee.taltech.iti0202.gomoku.app.player.strategy.Strategy;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class Game {

    private GameController gameController;
    private MenuController menuController;
    private GameSession session;
    private Stage primaryStage;
    private Scene menuScene;
    private Scene gameScene;
    private Thread gameThread;
    private int gamesCount;
    private Map<Strategy, Player> players;
    private State state;

    public Game(Stage primaryStage, Scene menuScene, Scene gameScene, MenuController menuController,
                GameController gameController) {
        this.primaryStage = primaryStage;
        this.menuScene = menuScene;
        this.gameScene = gameScene;
        this.menuController = menuController;
        this.gameController = gameController;

        players = new HashMap<>();

        primaryStage.setScene(menuScene);
    }

    public void start(Strategy strategy1, Strategy strategy2, int boardWidth, int boardHeight, int gamesCount,
                      long player1Timeout, long player2Timeout, boolean showHeadToHead) {
        this.gamesCount = gamesCount - 1;  //Game 1 already started

        Player player1 = players.containsKey(strategy1) ? players.get(strategy1) : new Player(strategy1);
        players.putIfAbsent(strategy1, player1);

        Player player2 = players.containsKey(strategy2) ? players.get(strategy2) : new Player(strategy2);
        players.putIfAbsent(strategy2, player2);

        boolean lastGame = gamesCount == 0;

        this.session = new GameSession(gameController, player1, player2, boardWidth, boardHeight, player1Timeout,
                player2Timeout, showHeadToHead);
        gameController.initializeBoard(session.getBoard());
        gameController.updateStats(session.getPlayer1(), session.getPlayer2(), showHeadToHead);
        primaryStage.setScene(gameScene);
        state = State.PLAY;

        if (gameThread != null && !gameThread.isInterrupted()) gameThread.interrupt();

        gameThread = new Thread(() -> {
            while (session.getState() == GameSession.GameState.PLAYING) {
                switch (state) {
                    case PLAY:
                        session.playMove();
                        break;
                    case PLAY_MOVE:
                        if (!session.nextMove()) session.playMove();
                        setState(State.STOPPED);
                        break;
                    case PLAY_END:
                        while (session.nextMove()) ;
                        setState(State.STOPPED);
                        break;
                    case REWIND_MOVE:
                        session.previousMove();
                        setState(State.STOPPED);
                        break;
                    case REWIND_START:
                        while (session.previousMove()) ;
                        setState(State.STOPPED);
                        break;
                    default:
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }
            }

            session.submitGame();

            String bannerSmallText = String.format("(%s %d - %d %s)",
                    player1.getName(),
                    player1.getStats(player2).getWins(),
                    player2.getStats(player1).getWins(),
                    player2.getName());

            switch (session.getState()) {
                case WHITE_WON:
                    gameController.showBanner("White Won!", bannerSmallText, !lastGame);
                    break;
                case BLACK_WON:
                    gameController.showBanner("Black Won!", bannerSmallText, !lastGame);
                    break;
                case DRAW:
                    gameController.showBanner("Draw.", bannerSmallText, !lastGame);
                    break;
                default:
                    System.out.println("Illegal game state, SumTingWong!");
                    break;
            }
        });
        gameThread.start();
    }

    public void end() {
        primaryStage.setScene(menuScene);  //needed for banner fix?
        session.terminateGame();
        if (gamesCount > 0) {
            start(session.getPlayer2().getStrategy(),
                    session.getPlayer1().getStrategy(),
                    session.getBoard().getWidth(),
                    session.getBoard().getHeight(),
                    gamesCount,
                    session.getPlayer2Timeout(),
                    session.getPlayer1Timeout(),
                    session.isShowHeadToHeadStats());
        }
    }

    public double getWindowHeight() {
        return primaryStage.getHeight();
    }

    public double getWindowWidth() {
        return primaryStage.getWidth();
    }

    public void close() {
        if (session != null) {
            session.terminateGame();
        }

        if (gameThread != null) {
            gameThread.interrupt();
        }
    }

    public void stop() {
        session.cancelMove();
        this.state = State.STOPPED;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public enum State {
        REWIND_START, REWIND_MOVE, PLAY, PLAY_MOVE, PLAY_END, STOPPED
    }
}
