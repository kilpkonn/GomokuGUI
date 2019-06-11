package ee.kilpkonn;

import ee.kilpkonn.app.Game;
import ee.kilpkonn.app.controllers.GameController;
import ee.kilpkonn.app.controllers.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("ui/menu.fxml"));
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("ui/game.fxml"));

        MenuController menuController = new MenuController();
        GameController gameController = new GameController();
        menuLoader.setController(menuController);
        gameLoader.setController(gameController);

        Scene menuScene = new Scene(menuLoader.load());
        Scene gameScene = new Scene(gameLoader.load());
        Game game = new Game(primaryStage, menuScene, gameScene, menuController, gameController);
        menuController.registerGame(game);
        gameController.registerGame(game);

        primaryStage.setTitle("Gomoku by kilpkonn");

        primaryStage.setMaximized(false);
        primaryStage.show();
    }
}
