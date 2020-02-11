package ee.taltech.iti0202.gomoku.app.player.strategy;

import ee.taltech.iti0202.gomoku.app.board.IBoard;
import ee.taltech.iti0202.gomoku.app.board.ILocation;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public abstract class Strategy {

    public abstract ILocation getMove(IBoard board, boolean isWhite);

    public Paint getColor(boolean white) {
        if (white) {
            return new RadialGradient(0, 0, 1, 0, 1, true,
                    CycleMethod.NO_CYCLE,
                    new Stop(1, Color.LIGHTGRAY),
                    new Stop(0, Color.WHITE));
        } else {
            return new RadialGradient(0, 0, 1, 0, 1, true,
                    CycleMethod.NO_CYCLE,
                    new Stop(1, Color.BLACK),
                    new Stop(0, Color.GREY));
        }
    }

    public abstract void onGameOver();

    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }
}
