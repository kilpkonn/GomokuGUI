package ee.taltech.iti0202.gomoku.app.board;

public interface IBoard<T> {

    T getWhite();

    T getBlack();

    T[][] getMatrix();

    int getHeight();

    int getWidth();
}
