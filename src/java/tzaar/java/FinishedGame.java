package tzaar.java;

import java.util.List;

public class FinishedGame {
    public final Board initialBoard;
    public final List<Turn> turns;
    public final Color winner;
    public final Stats whitePlayerStats;
    public final Stats blackPlayerStats;

    public FinishedGame(final Board initialBoard, final List<Turn> turns, final Color winner,
                        final Stats whitePlayerStats, final Stats blackPlayerStats) {
        this.initialBoard = initialBoard;
        this.turns = turns;
        this.winner = winner;
        this.whitePlayerStats = whitePlayerStats;
        this.blackPlayerStats = blackPlayerStats;
    }

    @Override
    public String toString() {
        return String.format("%s wins in %d turns" +
                "\nWhite stats: %s" +
                "\nBlack stats: %s", winner, turns.size(), whitePlayerStats, blackPlayerStats);
    }
}
