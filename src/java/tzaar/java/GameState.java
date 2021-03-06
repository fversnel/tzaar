package tzaar.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static tzaar.java.ClojureLayer.callClojure;

public class GameState {
    public final UUID gameId;
    public final Board initialBoard;
    public final List<Turn> turns;
    public final Board board;

    public GameState(final UUID gameId,
                     final Board initialBoard,
                     final List<Turn> turns,
                     final Board currentBoard) {
        this.gameId = gameId;
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

//    public GameState applyTurn(final Turn turn) {
//        final List<Turn> newTurns = new ArrayList<>(turns);
//        newTurns.add(turn);
//        return new GameState(
//                gameId,
//                initialBoard,
//                newTurns,
//                board.applyTurn(turn));
//    }

    public Optional<Winner> isGameOver() {
        return Optional.ofNullable(ClojureLayer.<Winner>callClojure("game-over?", this));
    }
}
