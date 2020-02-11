package ee.taltech.iti0202.gomoku.app.player.strategy;

import ee.taltech.iti0202.gomoku.app.board.IBoard;
import ee.taltech.iti0202.gomoku.app.board.ILocation;
import ee.taltech.iti0202.gomoku.app.board.Location;

public class HumanStrategy extends Strategy {
    private static Location selectedCell;
    private static boolean playing = false;

    @Override
    public ILocation getMove(IBoard board, boolean isWhite) {
        selectedCell = null;
        playing = true;
        while (selectedCell == null) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        playing = false;
        return selectedCell;
    }

    @Override
    public String getName() {
        return "Human";
    }

    public static void setSelectedCell(Location selectedCell) {
        if (playing) HumanStrategy.selectedCell = selectedCell;
    }
}
