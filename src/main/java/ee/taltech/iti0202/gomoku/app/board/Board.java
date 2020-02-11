package ee.taltech.iti0202.gomoku.app.board;

import ee.taltech.iti0202.gomoku.app.GameSession;
import ee.taltech.iti0202.gomoku.app.exceptions.LocationOccupiedException;

import java.util.Objects;

public class Board<T> implements IBoard {

    private static final int SQUARES_IN_LINE_FOR_WIN = 5;

    private int width;
    private int height;

    private Stone[][] matrix;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;

        matrix = new Stone[height][width];
    }

    public void makeMove(ILocation location, Stone stone) throws LocationOccupiedException {
        if (!isFree(location)) throw new LocationOccupiedException("Location already occupied");
        matrix[location.getY()][location.getX()] = stone;
    }

    public void reverseMove(ILocation location) {
        if (!isFree(location) && location.getY() >= 0 && location.getX() >= 0) {
            matrix[location.getY()][location.getX()] = null;
        }
    }

    public GameSession.GameState getGameState() {
        boolean hasFree = false;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (matrix[i][j] != null) {
                    if (downward(i, j) || rightward(i, j) || lDiagonal(i, j) || rDiagonal(i, j)) {
                        return matrix[i][j] == Stone.WHITE ? GameSession.GameState.WHITE_WON :
                                GameSession.GameState.BLACK_WON;
                    }
                } else {
                    hasFree = true;
                }
            }
        }

        return hasFree ? GameSession.GameState.PLAYING : GameSession.GameState.DRAW;
    }

    public boolean isFree(ILocation location) {
        return location.getY() >= 0 && location.getX() >= 0 && matrix[location.getY()][location.getX()] == null;
    }

    public Stone[][] getMatrix() {
        return matrix;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    private boolean rDiagonal(int r, int c) {
        Stone sq = matrix[r][c];
        int w = 0;

        for (int x = c, y = r; x < width && y < height; x++, y++) {
            if (sq == matrix[y][x]) {
                w++;
            } else {
                break;
            }
        }

        return (w >= SQUARES_IN_LINE_FOR_WIN);
    }

    private boolean lDiagonal(int r, int c) {
        Stone sq = matrix[r][c];
        int w = 0;

        for (int x = c, y = r; x >= 0 && y < height; x--, y++) {
            if (sq == matrix[y][x]) {
                w++;
            } else {
                break;
            }
        }

        return (w >= SQUARES_IN_LINE_FOR_WIN);
    }

    private boolean rightward(int r, int c) {
        Stone sq = matrix[r][c];
        int w = 0;

        for (int i = c; i < width; i++) {
            if (sq == matrix[r][i]) {
                w++;
            } else {
                break;
            }
        }

        return (w >= SQUARES_IN_LINE_FOR_WIN);
    }

    private boolean downward(int r, int c) {
        Stone sq = matrix[r][c];
        int w = 0;

        for (int i = r; i < height; i++) {
            if (sq == matrix[i][c]) {
                w++;
            } else {
                break;
            }
        }

        return (w >= SQUARES_IN_LINE_FOR_WIN);
    }

}
