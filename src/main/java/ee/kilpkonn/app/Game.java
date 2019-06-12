package ee.kilpkonn.app;

import ee.kilpkonn.app.board.Board;
import ee.kilpkonn.app.controllers.GameController;
import ee.kilpkonn.app.controllers.MenuController;
import ee.kilpkonn.app.player.Player;
import ee.kilpkonn.app.player.strategy.Strategy;
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

    public void start(Strategy strategy1, Strategy strategy2, int boardWidth, int boardHeight, int gamesCount) {
        this.gamesCount = gamesCount - 1;  //Game 1 already started
        Player player1 = players.containsKey(strategy1) ? players.get(strategy1) : new Player(strategy1);
        Player player2 = players.containsKey(strategy2) && strategy1 != strategy2 ? players.get(strategy2) :
                new Player(strategy2);

        //TODO: Handle multiple same strategies better;

        players.putIfAbsent(strategy1, player1);
        players.putIfAbsent(strategy2, player2);

        boolean lastGame = gamesCount == 0;

        this.session = new GameSession(gameController, player1, player2, boardWidth, boardHeight);
        gameController.initializeBoard(session.getBoard());
        gameController.updateStats(session.getWhitePlayer(), session.getBlackPlayer());
        primaryStage.setScene(gameScene);

        gameThread = new Thread(() -> {
            while (session.getState() == GameSession.GameState.PLAYING) {
                session.nextMove();
            }

            switch (session.getState()) {
                case WHITE_WON:
                    gameController.showBanner("White Won!", !lastGame);
                    break;
                case BLACK_WON:
                    gameController.showBanner("Black Won!", !lastGame);
                    break;
                case DRAW:
                    gameController.showBanner("Draw.", !lastGame);
                    break;
                default:
                    System.out.println("Illegal game state, SumTingWong!");
                    break;
            }
            session.submitGame();
        });
        gameThread.start();
    }

    public void end() {
        primaryStage.setScene(menuScene);  //needed for banner fix?
        if (gamesCount > 0) {
            start(session.getBlackPlayer().getStrategy(),
                    session.getWhitePlayer().getStrategy(),
                    session.getBoard().getWidth(),
                    session.getBoard().getHeight(),
                    gamesCount);
        }
    }

    public double getWindowHeight() {
        return primaryStage.getHeight();
    }

    public double getWindowWidth() {
        return primaryStage.getWidth();
    }

    public void close() {
        if (gameThread != null) {
            gameThread.interrupt();
        }
    }
}
