package tzaar.java;

import clojure.lang.ArraySeq;
import clojure.lang.IFn;

import java.util.*;
import java.util.stream.Collectors;

public class Board {
    public final List<List<Slot>> slots;

    public Board(final List<List<Slot>> slots) {
        this.slots = slots;
    }

    public Slot lookup(final int x, final int y) {
        return lookup(new Position(x, y));
    }

    public Slot lookup(final Position position) {
        final Slot s;
        if(0 <= position.y && position.y < slots.size()) {
            final List<Slot> row = slots.get(position.y);
            if(0 <= position.x && position.x < row.size()) {
                s = row.get(position.x);
            } else {
                s = Slot.Nothing;
            }
        } else {
            s = Slot.Nothing;
        }
        return s;
    }

    public static Board standard() {
        return callClojure("default-board");
    }

    public static Board random() {
        return callClojure("random-board");
    }

    public Collection<Move> moves(final Position position) {
        return callClojure("moves", this, position);
    }

    public Collection<Move.Stack> stackMoves(final Position position) {
        return filterStackMoves(moves(position));
    }

    public Collection<Move.Attack> attackMoves(final Position position) {
        return filterAttackMoves(moves(position));
    }

    public Collection<Move> allMoves(final Color color) {
        return callClojure("all-moves", this, color);
    }

    public Collection<Move.Attack> allAttackMoves(final Color color) {
        return filterAttackMoves(allMoves(color));
    }

    public Collection<Move.Stack> allStackMoves(final Color color) {
        return filterStackMoves(allMoves(color));
    }

    private Collection<Move.Attack> filterAttackMoves(Collection<Move> moves) {
        return moves
                .stream()
                .filter(Move::isAttack)
                .map(move -> (Move.Attack)move)
                .collect(Collectors.toList());
    }

    private Collection<Move.Stack> filterStackMoves(Collection<Move> moves) {
        return moves
                .stream()
                .filter(Move::isStack)
                .map(move -> (Move.Stack)move)
                .collect(Collectors.toList());
    }

    public Collection<Neighbor> neighbors(final Position position) {
        return callClojure("neighbors", this, position);
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
