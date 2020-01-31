package ee.taltech.iti0202.gomoku.app.controllers;

import ee.taltech.gomoku.app.util.Util;
import ee.taltech.iti0202.gomoku.app.player.strategy.Strategy;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MenuController extends Controller {

    @FXML
    private ComboBox<Strategy> player1;
    @FXML
    private ComboBox<Strategy> player2;
    @FXML
    private TextField board_width;
    @FXML
    private TextField board_height;
    @FXML
    private TextField player1_timeout;
    @FXML
    private TextField player2_timeout;
    @FXML
    private TextField games_count;
    @FXML
    private CheckBox show_head_to_head;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<Strategy> strategies =
                Util.getClassesInPackage(Strategy.class.getPackageName()).stream()
                        .filter(c -> c.getSuperclass() == Strategy.class)
                        .map(c -> {
                            try {
                                return c.getConstructor();
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .map(o -> {
                            try {
                                return (Strategy) o.newInstance();
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        player1.getItems().setAll(strategies);
        player2.getItems().setAll(strategies);

        player1.getSelectionModel().selectFirst();
        player2.getSelectionModel().selectFirst();
    }

    public void onStart() {
        try {
            int boardWidth = Integer.parseInt(board_width.getText());
            int boardHeight = Integer.parseInt(board_height.getText());
            int gamesCount = Integer.parseInt(games_count.getText());
            long player1Timeout = Math.round(1000000000 * Double.parseDouble(player1_timeout.getText()));
            long player2Timeout = Math.round(1000000000 * Double.parseDouble(player2_timeout.getText()));
            boolean showHeadToHead = show_head_to_head.isSelected();

            game.start(player1.getValue(), player2.getValue(),
                    boardWidth > 2 ? boardWidth : 3,
                    boardHeight > 2 ? boardHeight : 3,
                    gamesCount,
                    player1Timeout,
                    player2Timeout,
                    showHeadToHead);
        } catch (NumberFormatException e) {
            System.out.println("Board width, height, games count & timeouts have to be numbers!");
        }
    }
}
