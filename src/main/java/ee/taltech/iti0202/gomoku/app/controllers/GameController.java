package ee.taltech.iti0202.gomoku.app.controllers;

import ee.taltech.gomoku.app.controllers.components.Banner;
import ee.taltech.iti0202.gomoku.app.Game;
import ee.taltech.iti0202.gomoku.app.board.Board;
import ee.taltech.iti0202.gomoku.app.board.ILocation;
import ee.taltech.iti0202.gomoku.app.board.Location;
import ee.taltech.iti0202.gomoku.app.controllers.components.BoardCell;
import ee.taltech.iti0202.gomoku.app.player.Player;
import ee.taltech.iti0202.gomoku.app.player.statistics.Statistics;
import ee.taltech.iti0202.gomoku.app.util.Util;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class GameController extends Controller {

    @FXML
    private GridPane board;
    @FXML
    private StackPane pane;
    @FXML
    private Label player1_name;
    @FXML
    private Label player1_games;
    @FXML
    private Label player1_wins;
    @FXML
    private Label player1_losses;
    @FXML
    private Label player1_draws;
    @FXML
    private Label player1_total_moves;
    @FXML
    private Label player1_current_moves;

    @FXML
    private Label player2_name;
    @FXML
    private Label player2_games;
    @FXML
    private Label player2_wins;
    @FXML
    private Label player2_losses;
    @FXML
    private Label player2_draws;
    @FXML
    private Label player2_total_moves;
    @FXML
    private Label player2_current_moves;

    @FXML
    private Button rewind_start;
    @FXML
    private Button rewind;
    @FXML
    private Button play;
    @FXML
    private Button play_move;
    @FXML
    private Button play_end;

    private Banner banner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rewind_start.setOnAction(e -> game.setState(Game.State.REWIND_START));
        rewind.setOnAction(e -> game.setState(Game.State.REWIND_MOVE));
        play.setOnAction(e -> {
            if (game.getState() == Game.State.PLAY) {
                game.stop();
                play.setText("PLAY");
            } else {
                game.setState(Game.State.PLAY);
                play.setText("STOP");
            }
        });
        play_move.setOnAction(e -> game.setState(Game.State.PLAY_MOVE));
        play_end.setOnAction(e -> game.setState(Game.State.PLAY_END));
    }


    public void initializeBoard(Board board) {
        this.board.getChildren().clear();
        play.setText("STOP");

        if (banner != null) {
            pane.getChildren().remove(banner);
            banner = null;
        }

        double cellSize = game.getWindowHeight() / (Math.max(board.getHeight(), board.getWidth()));

        IntStream.range(0, board.getHeight())
                .forEach(y -> IntStream.range(0, board.getWidth())
                        .forEach(x -> this.board.add(new BoardCell(new Location(y, x), cellSize), x, y)));
    }

    public void makeMove(ILocation location, Player player) {
        Util.updateFX(() -> {
            board.getChildren().stream()
                    .filter(c -> ((BoardCell) c).isAt(location))
                    .forEach(c -> ((BoardCell) c).placeStone(player));  // Do it just once, no need for optional
        });
    }

    public void reverseMove(ILocation location) {
        Util.updateFX(() -> {
            board.getChildren().stream()
                    .filter(c -> ((BoardCell) c).isAt(location))
                    .forEach(c -> ((BoardCell) c).removeStone());  // Do it just once, no need for optional
        });
    }

    public void showBanner(String text, String smallText, boolean autoClose) {
        Util.updateFX(() -> {
            if (banner != null) {
                pane.getChildren().remove(banner);
            }
            this.banner = new Banner(text, smallText, game.getWindowWidth(), game.getWindowHeight());

            if (autoClose) {
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5),
                        e -> pane.getChildren().remove(banner)));
                timeline.setCycleCount(1);
                timeline.setOnFinished(e -> game.end());
                timeline.play();
            } else {
                banner.setOnMouseClicked(e -> game.end());
            }
            pane.getChildren().add(banner);
        });
    }

    public void updateStats(Player player1, Player player2, boolean headToHead) {
        Util.updateFX(() -> {
            updatePlayerStats(headToHead ? player1.getStats(player2) : player1.getStats(),
                    player1_name,
                    player1.getName(),
                    player1_games,
                    player1_wins,
                    player1_losses,
                    player1_draws, player1_total_moves, player1_current_moves);
            updatePlayerStats(headToHead ? player2.getStats(player1) : player2.getStats(),
                    player2_name,
                    player2.getName(),
                    player2_games,
                    player2_wins,
                    player2_losses,
                    player2_draws, player2_total_moves, player2_current_moves);
        });
    }

    private void updatePlayerStats(Statistics player1Stats, Label player1_name, String name, Label player1_games,
                                   Label player1_wins, Label player1_losses, Label player1_draws,
                                   Label player1_total_moves, Label player1_current_moves) {
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
