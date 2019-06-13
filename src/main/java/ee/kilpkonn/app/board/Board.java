package ee.kilpkonn.app.board;

import ee.kilpkonn.app.GameSession;
import ee.kilpkonn.app.exceptions.LocationOccupiedException;

import java.util.Objects;

public class Board {

    private static final int SQUARES_IN_LINE_FOR_WIN = 5;

    private int width;
    private int height;

    private Stone[][] matrix;

    public enum Stone {
        BLACK, WHITE
    }

    public Board(int width, int height) {
        this.width = width;
        this.height = height;

        matrix = new Stone[height][width];
    }

    public void makeMove(Location location, Stone stone) throws LocationOccupiedException {
        if (!isFree(location)) throw new LocationOccupiedException("Location already occupied");
        matrix[location.y][location.x] = stone;
    }

    public void reverseMove(Location location) {
        if (!isFree(location) && location.y >= 0 && location.x >= 0) {
            matrix[location.y][location.x] = null;
        }
    }

    public GameSession.GameState getGameState(){
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

    public boolean isFree(Location location) {
        return location.y >= 0 && location.x >= 0 && matrix[location.y][location.x] == null;
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


    private boolean rDiagonal(int r, int c){
        Stone sq = matrix[r][c];
        int w = 0;

        for (int x = c, y = r; x < width && y < height; x++,y++) {
            if (sq == matrix[y][x]) {
                w++;
            } else {
                break;
            }
        }

        return (w >= SQUARES_IN_LINE_FOR_WIN);
    }

    private boolean lDiagonal(int r, int c){
        Stone sq = matrix[r][c];
        int w = 0;

        for (int x = c, y = r; x >= 0 && y < height; x--,y++) {
            if (sq == matrix[y][x]) {
                w++;
            } else {
                break;
            }
        }

        return (w >= SQUARES_IN_LINE_FOR_WIN);
    }

    private boolean rightward(int r, int c){
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

    private boolean downward(int r, int c){
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


    public static class Location {
        private int x, y;

        public Location(int y, int x) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return x == location.x &&
                    y == location.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        public Location clone() {
            return new Location(y, x);
        }
    }
}
