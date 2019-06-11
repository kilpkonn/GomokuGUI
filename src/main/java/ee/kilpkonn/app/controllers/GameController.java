package ee.kilpkonn.app.controllers;

import ee.kilpkonn.app.board.Board;
import ee.kilpkonn.app.controllers.components.Banner;
import ee.kilpkonn.app.controllers.components.BoardCell;
import ee.kilpkonn.app.player.Player;
import ee.kilpkonn.app.player.statistics.Statistics;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameController extends Controller {

    @FXML private GridPane board;
    @FXML private StackPane pane;

    @FXML private Label player1_name;
    @FXML private Label player1_games;
    @FXML private Label player1_wins;
    @FXML private Label player1_losses;
    @FXML private Label player1_draws;
    @FXML private Label player1_total_moves;
    @FXML private Label player1_current_moves;


    @FXML private Label player2_name;
    @FXML private Label player2_games;
    @FXML private Label player2_wins;
    @FXML private Label player2_losses;
    @FXML private Label player2_draws;
    @FXML private Label player2_total_moves;
    @FXML private Label player2_current_moves;

    private Banner banner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


    public void initializeBoard(Board board) {
        this.board.getChildren().clear();

        if (banner != null) {
            pane.getChildren().remove(banner);
            banner = null;
        }

        double cellSize = game.getWindowHeight()
                / (board.getHeight() < board.getWidth() ? board.getWidth() : board.getHeight());

        IntStream.range(0, board.getHeight())
                .forEach(y -> IntStream.range(0, board.getWidth())
                        .forEach(x -> this.board.add(new BoardCell(new Board.Location(y, x), cellSize), x, y)));
    }

    public void makeMove(Board.Location location, Player player) {
        Platform.runLater(() -> {
            var a = board.getChildren().stream()
                    .filter(c -> ((BoardCell) c).isAt(location))
                    .collect(Collectors.toList());
            System.out.println(a);

            board.getChildren().stream()
                    .filter(c -> ((BoardCell) c).isAt(location))
                    .forEach(c -> ((BoardCell) c).placeStone(player));  //Do it just once, no need for optional
        });
    }

    public void showBanner(String text) {
        Platform.runLater(() -> {
            if (banner != null) {
                pane.getChildren().remove(banner);
            }
            this.banner = new Banner(text, game.getWindowWidth(), game.getWindowHeight());
            banner.setOnMouseClicked(e -> game.end());
            pane.getChildren().add(banner);
        });
    }

    public void updateStats(Player player1, Player player2) {
        Platform.runLater(() -> {
            updatePlayerStats(player1, player1_name, player1.getName(), player1_games, player1_wins, player1_losses, player1_draws, player1_total_moves, player1_current_moves);
            updatePlayerStats(player2, player2_name, player2.getName(), player2_games, player2_wins, player2_losses,
                    player2_draws, player2_total_moves, player2_current_moves);
        });
    }

    private void updatePlayerStats(Player player1, Label player1_name, String name, Label player1_games, Label player1_wins, Label player1_losses, Label player1_draws, Label player1_total_moves, Label player1_current_moves) {
        Statistics player1Stats = player1.getStats();
        player1_name.setText(name + ":");
        player1_games.setText(String.format("Games: %d", player1Stats.getTotalGamesCount()));
        player1_wins.setText(String.format("Wins: %d (%d%%)", player1Stats.getWins(),
                Math.round(player1Stats.getWinRate() * 100)));
        player1_losses.setText(String.format("Losses: %d (%d%%)", player1Stats.getLosses(),
                Math.round(player1Stats.getLossRate() * 100)));
        player1_draws.setText(String.format("Draws: %d (%d%%)", player1Stats.getDraws(),
                Math.round(player1Stats.getDrawRate() * 100)));
        player1_total_moves.setText(String.format("Total moves: %d (%.2fs per move)", player1Stats.getTotalMoves(),
                player1Stats.getAverageTimePerMove()));
        player1_current_moves.setText(String.format("This game: %d (%.2fs per move)",
                player1Stats.getMovesCurrentGame(), player1Stats.getAverageTimePerMoveCurrentGame()));
    }
}
