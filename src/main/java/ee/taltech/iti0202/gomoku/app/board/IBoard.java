package ee.taltech.iti0202.gomoku.app.board;

public interface IBoard {
    IStone[][] getMatrix();

    int getWidth();

    int getHeight();
}
