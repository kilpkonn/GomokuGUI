package ee.kilpkonn.app.controllers.components;

import ee.kilpkonn.app.board.Board;
import ee.kilpkonn.app.player.Player;
import ee.kilpkonn.app.player.strategy.HumanStrategy;
import javafx.scene.control.Button;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class BoardCell extends Button {

    private Board.Location location;

    public BoardCell (Board.Location location, double size) {
        this.location = location;
        this.getStyleClass().add("board-cell");
        this.setMinSize(size, size);
        this.setMaxSize(size, size);

        this.setOnAction(e -> HumanStrategy.setSelectedCell(location));
    }

    public boolean isAt(Board.Location location) {
        return this.location == location;
    }

    public void placeStone(Player player) {
        this.getChildren().add(new Stone(this.getHeight() / 2, player.getColor()));
        this.setDisable(true);
    }

    private class Stone extends Circle {

        public Stone(double offset, Paint fill) {
            super(offset, offset, offset / 1.2, fill);
        }
    }
}
