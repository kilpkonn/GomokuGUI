package ee.taltech.iti0202.gomoku.app.controllers.components;

import ee.taltech.iti0202.gomoku.app.board.ILocation;
import ee.taltech.iti0202.gomoku.app.board.Location;
import ee.taltech.iti0202.gomoku.app.player.Player;
import ee.taltech.iti0202.gomoku.app.player.strategy.HumanStrategy;
import javafx.scene.control.Button;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class BoardCell extends Button {

    private Location location;
    private Stone stone;

    public BoardCell(Location location, double size) {
        this.location = location;
        this.getStyleClass().add("board-cell");
        this.setMinSize(size, size);
        this.setMaxSize(size, size);

        this.setOnAction(e -> HumanStrategy.setSelectedCell(location));
    }

    public boolean isAt(ILocation location) {
        return this.location.equals(location);
    }

    public void placeStone(Player player) {
        stone = new Stone(this.getHeight() / 2, player.getColor());
        this.getChildren().add(stone);
        this.setDisable(true);
    }

    public void removeStone() {
        this.getChildren().remove(stone);
        this.setDisable(false);
    }

    private class Stone extends Circle {

        public Stone(double offset, Paint fill) {
            super(offset, offset, offset / 1.2, fill);
        }
    }
}
