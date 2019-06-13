package ee.kilpkonn.app.player.strategy;

import ee.kilpkonn.app.board.Board;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public abstract class Strategy {

    public abstract Board.Location getMove(Board board, boolean isWhite);

    public Paint getColor(boolean white) {
        if (white) {
            System.out.println("White");
            return new RadialGradient(0, 0, 1, 0, 1, true,
                    CycleMethod.NO_CYCLE,
                    new Stop(1, Color.LIGHTGRAY),
                    new Stop(0, Color.WHITE));
        } else {
            System.out.println("Black");
            return new RadialGradient(0, 0, 1, 0, 1, true,
                    CycleMethod.NO_CYCLE,
                    new Stop(1, Color.BLACK),
                    new Stop(0, Color.GREY));
        }
    }

    public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }
}
