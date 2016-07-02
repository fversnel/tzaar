package tzaar.java;

import java.util.List;
import java.util.UUID;

public class GameState {
    public final UUID id;
    public final Board initialBoard;
    public final List<Turn> turns;
    public final Board board;

    public GameState(final UUID id,
                     final Board initialBoard,
                     final List<Turn> turns,
                     final Board currentBoard) {
        this.id = id;
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
