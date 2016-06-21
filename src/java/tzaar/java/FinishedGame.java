package tzaar.java;

import java.util.List;

public class FinishedGame {
    public final Board initialBoard;
    public final List<Turn> turns;
    public final Color winner;

    public FinishedGame(final Board initialBoard, final List<Turn> turns, final Color winner) {
        this.initialBoard = initialBoard;
        this.turns = turns;
        this.winner = winner;
    }
}
