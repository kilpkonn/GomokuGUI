package ee.taltech.iti0202.gomoku.app.player.statistics;

public class Statistics {
    private int wins = 0;
    private int losses = 0;
    private int draws = 0;
    private int totalMoves = 0;
    private double totalTimeThinking = 0;
    private double timeThinkingCurrentGame = 0;
    private int movesCurrentGame = 0;

    public void addMove(double dt) {
        totalTimeThinking += dt;
        timeThinkingCurrentGame += dt;
        totalMoves++;
        movesCurrentGame++;
    }

    public void submitGame(Result result) {
        switch (result) {
            case WIN:
                wins++;
                break;
            case LOSS:
                losses++;
                break;
            case DRAW:
                draws++;
                break;
        }
        timeThinkingCurrentGame = 0;
        movesCurrentGame = 0;
    }

    public int getTotalGamesCount() {
        return wins + losses + draws;
    }

    public double getAverageTimePerMove() {
        return totalTimeThinking / totalMoves;
    }

    public double getAverageTimePerMoveCurrentGame() {
        return timeThinkingCurrentGame / movesCurrentGame;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getDraws() {
        return draws;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public double getTotalTimeThinking() {
        return totalTimeThinking;
    }

    public double getTimeThinkingCurrentGame() {
        return timeThinkingCurrentGame;
    }

    public int getMovesCurrentGame() {
        return movesCurrentGame;
    }

    public float getWinRate() {
        return (float) wins / (wins + losses + draws);
    }

    public float getLossRate() {
        return (float) losses / (wins + losses + draws);
    }

    public float getDrawRate() {
        return (float) draws / (wins + losses + draws);
    }

    public enum Result {
        WIN, LOSS, DRAW
    }
}
