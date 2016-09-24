package tzaar.java;

import java.util.*;
import java.util.stream.Stream;
import static tzaar.java.ClojureLayer.callClojure;

public class Board {
    /**
     * You probably don't need this in Java
     */
    public final Object _clojureBoard;

    public Board(final Object clojureBoard) {
        this._clojureBoard = clojureBoard;
    }

    public static Board standard() {
        return callClojure("default-board");
    }

    public static Board random() {
        return callClojure("random-board");
    }

    public Slot lookup(final Position position) {
        return callClojure("lookup", this, position);
    }

    public Stream<Move> moves(final Position position) {
        return ClojureLayer.<Collection<Move>>callClojure("moves", this, position)
                .stream();
    }

    public Stream<Move> stackMoves(final Position position) {
        return moves(position).filter(Move::isStack);
    }

    public Stream<Move> attackMoves(final Position position) {
        return moves(position).filter(Move::isAttack);
    }

    public Stream<Move> allMoves(final Color color) {
        return ClojureLayer.<Collection<Move>>callClojure("all-moves", this, color)
                .stream();
    }

    public Stream<Move> allAttackMoves(final Color color) {
        return allMoves(color).filter(Move::isAttack);
    }

    public Stream<Move> allStackMoves(final Color color) {
        return allMoves(color).filter(Move::isStack);
    }

    public Collection<StackWithPosition> neighbors(final Position position) {
        return callClojure("neighbors", this, position);
    }

    public Collection<StackWithPosition> stacks(final Color color) {
        return callClojure("iterate-stacks", color, this);
    }

    public Board applyMove(final Move move) {
        return callClojure("apply-move", this, move);
    }

    public boolean isStackTypeMissing(final Color playerColor) {
        return callClojure("stack-type-missing?", this, playerColor);
    }

    /**
     * WARNING: Only call this if you want to have low-level access to the board.
     * Otherwise use any of the other methods like allMoves(), neighbors() or stacks()
     * they are much faster since slots() has to completely copy a clojure data
     * structure into a java collection.
     *
     * @return A 2D list of positions on the board
     */
    public List<List<Slot>> slots() {
        return callClojure("to-slots", this);
    }

    @Override
    public String toString() {
        return callClojure("board->str", this);
    }

}
