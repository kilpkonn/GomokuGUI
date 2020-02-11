package ee.taltech.iti0202.gomoku.app.player.strategy;

import ee.taltech.iti0202.gomoku.app.board.IBoard;
import ee.taltech.iti0202.gomoku.app.board.ILocation;
import ee.taltech.iti0202.gomoku.app.board.IStone;
import ee.taltech.iti0202.gomoku.app.board.Location;
import ee.taltech.iti0202.gomoku.app.board.Stone;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.Set;


/**
 * Important!
 * This is an example strategy class.
 * You should not overwrite this.
 * Instead make your own class, for example:
 * public class AgoStrategy implements ComputerStrategy
 * <p>
 * and add all the logic there. The created strategy
 * should be visible under player selection automatically.
 * <p>
 * This file here might be overwritten in future versions.
 */
public class DummyStrategy extends Strategy {
    private IStone[][] currentBoard;
    private short[][] currentWeightedBoard;
    private Set<Pair<Integer, Integer>> checkedRows = new HashSet<>();
    private Set<Pair<Integer, Integer>> checkedCols = new HashSet<>();
    private Set<Pair<Integer, Integer>> checkedDiagonals1 = new HashSet<>();
    private Set<Pair<Integer, Integer>> checkedDiagonals2 = new HashSet<>();

    private int RECURSION_DEPTH = 5;
    private int CHECK_RADIUS = 3;
    private int MAX_SHAPE_LENGTH = 5;
    private int boardWidth;
    private int boardHeight;
    private Stone player;
    private boolean won;

    private static final short FIVE = 25;
    private static final short OPEN_FOUR = 24;
    private static final short FOUR = 17;
    private static final short OPEN_THREE = 12;
    private static final short THREE = 7;
    private static final short OPEN_TWO = 4;
    private static final short TWO = 3;
    private static final short ONE = 1;


    @Override
    public ILocation getMove(IBoard board, boolean isWhite) {
        return getMove(board, isWhite, RECURSION_DEPTH);
    }

    private ILocation getMove(IBoard board, boolean player, int depth) {
        currentBoard = board.getMatrix();
        boardWidth = board.getWidth();
        boardHeight = board.getHeight();
        this.player = player ? Stone.WHITE : Stone.BLACK;

        currentWeightedBoard = new short[board.getHeight()][board.getWidth()];

        checkedRows.clear();
        checkedCols.clear();
        checkedDiagonals1.clear();
        checkedDiagonals2.clear();

        boolean empty = true;

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (currentBoard[y][x] != null) {
                    weightPaintBoard(y, x);
                    empty = false;
                }
            }
        }
        if (depth == RECURSION_DEPTH) {
            for (int y = 0; y < board.getHeight(); y++) {
                StringBuilder line = new StringBuilder();
                for (int x = 0; x < board.getWidth(); x++) {
                    line.append("\t|\t").append((float) currentWeightedBoard[y][x] / 10);
                }
                System.out.println(line);
            }
        }

        // Start...
        if (empty) {
            return new Location(boardHeight / 2, boardWidth / 2);
        }

        return findMostWanted(currentWeightedBoard);
    }


    private Location findMostWanted(short[][] board) {
        int maxX = 0, maxY = 0, max = -1;
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                if (board[y][x] > max) {
                    max = board[y][x];
                    maxX = x;
                    maxY = y;
                }
            }
        }
        return new Location(maxY, maxX);
    }

    private void weightPaintBoard(int y, int x) {
        Pair<Integer, Integer> loc = new Pair<>(y, x);
        currentWeightedBoard[y][x] = Short.MIN_VALUE;

        if (!checkedRows.contains(loc)) {
            findRows(y, x);
        }
        if (!checkedCols.contains(loc)) {
            findCols(y, x);
        }
        if (!checkedDiagonals1.contains(loc)) {
            findDiagonals1(y, x);
        }
        if (!checkedDiagonals2.contains(loc)) {
            findDiagonals2(y, x);
        }
    }

    private void findRows(int y, int x) {
        IStone start = currentBoard[y][x];
        Stone enemy = start == Stone.WHITE ? Stone.BLACK : Stone.WHITE;
        int newX = x;
        int hotX = -1;

        while (newX + 1 < boardWidth && currentBoard[y][newX + 1] == start) {
            newX++;
            checkedRows.add(new Pair<>(y, newX));
        }

        short len = (short) (newX - x + 1);
        boolean open = x - 1 > 0 && newX + 1 < boardWidth
                && currentBoard[y][x - 1] == null
                && currentBoard[y][newX + 1] == null;

        int capLength = len;
        boolean startClosed = false;
        boolean endClosed = false;

        for (int i = 0; i < MAX_SHAPE_LENGTH; ++i) {
            if (!startClosed && x - i >= 0 && currentBoard[y][x - i] != enemy) {
                capLength++;
            } else if (x - i >= 0 && currentBoard[y][x - i] == enemy) {
                startClosed = true;
            }

            if (!endClosed && x + i < boardWidth && currentBoard[y][x + i] != enemy) {
                capLength++;
            } else if (x + i < boardWidth && currentBoard[y][x + i] == enemy) {
                endClosed = true;
            }
        }
        boolean boxed = capLength < MAX_SHAPE_LENGTH;
        // TODO: Explosive stuff only works if white and black have same abs values

        startClosed = false;
        endClosed = false;

        if (!boxed) {
            /*if (hotX >=  0) {
                currentWeightedBoard[y][hotX] = getComboScore(len, (short) CHECK_RADIUS, open, start, player);
            }*/
            for (int i = 0; i <= CHECK_RADIUS; ++i) {
                float multiplier = (float) 1 + (float) (CHECK_RADIUS - i) * 2;
                if (x - i >= 0) {
                    if (currentBoard[y][x - i] == null && !startClosed) {
                        int comboScore = getComboScore(len, multiplier, open, start, player);
                        currentWeightedBoard[y][x - i] += comboScore;
                    } else {
                        if (!startClosed && currentBoard[y][x - i] != start) {
                            startClosed = true;
                        }
                    }
                }
                if (newX + i < boardWidth) {
                    if (currentBoard[y][newX + i] == null && !endClosed) {
                        int comboScore = getComboScore(len, multiplier, open, start, player);
                        currentWeightedBoard[y][newX + i] += comboScore;
                    } else {
                        if (!endClosed && currentBoard[y][newX + i] != start) {
                            endClosed = true;
                        }
                    }
                }

            }
        }
    }

    private void findCols(int y, int x) {
        IStone start = currentBoard[y][x];
        Stone enemy = start == Stone.WHITE ? Stone.BLACK : Stone.WHITE;

        int newY = y;
        while (newY + 1 < boardHeight && currentBoard[newY + 1][x] == start) {
            newY++;
            checkedCols.add(new Pair<>(newY, x));
        }

        short len = (short) (newY - y + 1);
        boolean open = y - 1 >= 0 && newY + 1 < boardHeight
                && currentBoard[y - 1][x] == null
                && currentBoard[newY + 1][x] == null;

        int capLength = len;
        boolean startClosed = false;
        boolean endClosed = false;

        for (int i = 0; i < MAX_SHAPE_LENGTH; ++i) {
            if (!startClosed && y - i >= 0 && currentBoard[y - i][x] != enemy) {
                capLength++;
            } else if (y - i >= 0 && currentBoard[y - i][x] == enemy) {
                startClosed = true;
            }

            if (!endClosed && y + i < boardHeight && currentBoard[y + i][x] != enemy) {
                capLength++;
            } else if (y + i < boardHeight && currentBoard[y + i][x] == enemy) {
                endClosed = true;
            }
        }
        boolean boxed = capLength < MAX_SHAPE_LENGTH;
        // TODO: Explosive stuff only works if white and black have same abs values

        startClosed = false;
        endClosed = false;

        if (open || !boxed) {
            for (int i = 0; i <= CHECK_RADIUS; ++i) {
                float multiplier = (float) 1 + (float) (CHECK_RADIUS - i) * 2;
                if (y - i >= 0) {
                    if (currentBoard[y - i][x] == null && !startClosed) {
                        int comboScore = getComboScore(len, multiplier, open, start, player);
                        currentWeightedBoard[y - i][x] += comboScore;
                    } else {
                        if (!startClosed && currentBoard[y - i][x] == enemy) {
                            startClosed = true;
                        }
                    }
                }
                if (newY + i < boardHeight) {
                    if (currentBoard[newY + i][x] == null && !endClosed) {
                        int comboScore = getComboScore(len, multiplier, open, start, player);
                        currentWeightedBoard[newY + i][x] += comboScore;
                    } else {
                        if (!endClosed && currentBoard[newY + i][x] == enemy) {
                            endClosed = true;
                        }
                    }
                }
            }
        }
    }

    private void findDiagonals1(int y, int x) {
        IStone start = currentBoard[y][x];
        Stone enemy = start == Stone.WHITE ? Stone.BLACK : Stone.WHITE;
        int newX = x;
        int newY = y;
        while (newX + 1 < boardWidth && newY + 1 < boardHeight && currentBoard[newY + 1][newX + 1] == start) {
            newY++;
            newX++;
            checkedDiagonals1.add(new Pair<>(newY, newX));
        }

        short len = (short) (newX - x + 1);
        boolean open = y > 0 && x > 0 && newX + 1 < boardWidth && newY + 1 < boardHeight
                && currentBoard[y - 1][x - 1] == null
                && currentBoard[newY + 1][newX + 1] == null;

        int capLength = len;
        boolean startClosed = false;
        boolean endClosed = false;

        for (int i = 0; i < MAX_SHAPE_LENGTH; ++i) {
            if (!startClosed && x - i >= 0 && y - i >= 0 && currentBoard[y - i][x - i] != enemy) {
                capLength++;
            } else if (x - i >= 0 && y - i >= 0 && currentBoard[y - i][x - i] == enemy) {
                startClosed = true;
            }

            if (!endClosed && x + i < boardWidth && y + i < boardHeight && currentBoard[y + i][x + i] != enemy) {
                capLength++;
            } else if (x + i < boardWidth && y + i < boardHeight && currentBoard[y + i][x + i] == enemy) {
                endClosed = true;
            }
        }
        boolean boxed = capLength < MAX_SHAPE_LENGTH;

        if (!boxed) {
            for (int i = 0; i <= CHECK_RADIUS; ++i) {
                float multiplier = (float) 1 + (float) (CHECK_RADIUS - i) * 2;
                if (x - i >= 0 && y - i >= 0) {
                    if (currentBoard[y - i][x - i] == null && !startClosed) {
                        int comboScore = getComboScore(len, multiplier, open, start, player);
                        currentWeightedBoard[y - i][x - i] += comboScore;
                    } else {
                        if (!startClosed && currentBoard[y - i][x - i] == enemy) {
                            startClosed = true;
                        }
                    }
                }
                if (newX + i < boardWidth && newY + i < boardHeight) {
                    if (currentBoard[newY + i][newX + i] == null && !endClosed) {
                        int comboScore = getComboScore(len, multiplier, open, start, player);
                        currentWeightedBoard[newY + i][newX + i] += comboScore;
                    } else {
                        if (!endClosed && currentBoard[newY + i][newX + i] == enemy) {
                            endClosed = true;
                        }
                    }
                }
            }
        }
    }

    private void findDiagonals2(int y, int x) {
        IStone start = currentBoard[y][x];
        Stone enemy = start == Stone.WHITE ? Stone.BLACK : Stone.WHITE;
        int newX = x;
        int newY = y;
        while (newY > 0 && newX + 1 < boardWidth && currentBoard[newY - 1][newX + 1] == start) {
            newY--;
            newX++;
            checkedDiagonals2.add(new Pair<>(newY, newX));
        }

        short len = (short) (newX - x + 1);
        boolean open = y + 1 < boardHeight && x > 0 && newY > 0 && newX + 1 < boardWidth
                && currentBoard[y + 1][x - 1] == null
                && currentBoard[newY - 1][newX + 1] == null;

        int capLength = len;
        boolean startClosed = false;
        boolean endClosed = false;

        for (int i = 0; i < MAX_SHAPE_LENGTH; ++i) {
            if (!startClosed && x - i >= 0 && y + i < boardHeight && currentBoard[y + i][x - i] != enemy) {
                capLength++;
            } else if (x - i >= 0 && y + i < boardHeight && currentBoard[y + i][x - i] == enemy) {
                startClosed = true;
            }

            if (!endClosed && x + i < boardWidth && y - i >= 0 && currentBoard[y - i][x + i] != enemy) {
                capLength++;
            } else if (x + i < boardWidth && y - i >= 0 && currentBoard[y - i][x + i] == enemy) {
                endClosed = true;
            }
        }
        boolean boxed = capLength < MAX_SHAPE_LENGTH;

        if (!boxed) {
            for (int i = 0; i <= CHECK_RADIUS; ++i) {
                float multiplier = (float) 1 + (float) (CHECK_RADIUS - i) * 2;
                if (x - i >= 0 && y + i < boardHeight) {
                    if (currentBoard[y + i][x - i] == null && !startClosed) {
                        int comboScore = getComboScore(len, multiplier, open, start, player);
                        currentWeightedBoard[y + i][x - i] += comboScore;
                    } else {
                        if (!startClosed && currentBoard[y + i][x - i] == enemy) {
                            startClosed = true;
                        }
                    }
                }
                if (newX + i < boardWidth && newY - i >= 0) {
                    if (currentBoard[newY - i][newX + i] == null && !endClosed) {
                        int comboScore = getComboScore(len, multiplier, open, start, player);
                        currentWeightedBoard[newY - i][newX + i] += comboScore;
                    } else {
                        if (!endClosed && currentBoard[newY - i][newX + i] == enemy) {
                            endClosed = true;
                        }
                    }
                }
            }
        }
    }

    private short getComboScore(int len, float multiplier, boolean open, IStone start, IStone player) {
        float comboScore = 0;
        won = won || len >= 5 || (len == 4 && open);
        switch (len) {
            case 5:
                comboScore = (FIVE) * multiplier;
                break;
            case 4:
                comboScore = (open ? OPEN_FOUR : FOUR) * multiplier;
                break;
            case 3:
                comboScore = (open ? OPEN_THREE : THREE) * multiplier;
                break;
            case 2:
                comboScore = (open ? OPEN_TWO : TWO) * multiplier;
                break;
            case 1:
                comboScore = (ONE) * multiplier;
                break;
        }
        return (short) comboScore;
    }

    @Override
    public String getName() {
        return "No minmax";
    }
}
