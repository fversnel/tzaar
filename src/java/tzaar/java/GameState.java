package tzaar.java;

import java.util.List;

public class GameState {
    public final Board initialBoard;
    public final List<Turn> turns;
    public final Board board;

    public GameState(final Board initialBoard,
                     final List<Turn> turns,
                     final Board currentBoard) {
        this.initialBoard = initialBoard;
        this.turns = turns;
        this.board = currentBoard;
    }

    public boolean isFirstTurn() {
        return turns.isEmpty();
    }

    public Color whosTurn() {
        return (turns.size() % 2) == 0 ? Color.White : Color.Black;
    }
}
