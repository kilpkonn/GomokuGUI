package ee.taltech.iti0202.gomoku.app.board;

public interface IBoard {
    Board.Stone[][] getMatrix();

    int getWidth();

    int getHeight();
}
