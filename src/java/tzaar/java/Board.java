package tzaar.java;

import clojure.lang.ArraySeq;
import clojure.lang.IFn;

import java.util.*;
import java.util.stream.Stream;

public class Board {
    /**
     * You probably don't need this in Java
     */
    public final Object clojureBoard;

    public Board(Object clojureBoard) {
        this.clojureBoard = clojureBoard;
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
        return Board.<Collection<Move>>callClojure("moves", this, position)
                .stream();
    }

    public Stream<Move> stackMoves(final Position position) {
        return moves(position).filter(Move::isStack);
    }

    public Stream<Move> attackMoves(final Position position) {
        return moves(position).filter(Move::isAttack);
    }

    public Stream<Move> allMoves(final Color color) {
        return Board.<Collection<Move>>callClojure("all-moves", this, color)
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

    public Board applyTurn(final Turn turn) {
        return callClojure("apply-turn", this, turn);
    }

    public boolean isStackTypeMissing(final Color playerColor) {
        return callClojure("stack-type-missing?", this, playerColor);
    }

    public boolean hasLost(final Color playerColor, final boolean isFirstMoveOfTurn) {
        return callClojure("lost?", this, playerColor, isFirstMoveOfTurn);
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

    private static <TReturn> TReturn callClojure(final String clojureFnName,
                                                 final Object... params) {
        final IFn clojureFn = ClojureLayer.JAVA_API.function(clojureFnName);
        return (TReturn) clojureFn.applyTo(ArraySeq.create(params));
    }
}
