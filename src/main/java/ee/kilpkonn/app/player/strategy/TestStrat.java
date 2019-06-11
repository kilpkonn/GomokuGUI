package ee.kilpkonn.app.player.strategy;

import ee.kilpkonn.app.board.Board;

public class TestStrat extends Strategy {

    @Override
    public Board.Location getMove(Board board) {
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return "TestStrat";
    }
}
