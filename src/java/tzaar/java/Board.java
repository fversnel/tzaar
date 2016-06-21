package tzaar.java;

import clojure.lang.ArraySeq;
import clojure.lang.IFn;

import java.util.*;

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

    public Collection<Move> attackMoves(final Position position) {
        return callClojure("attack-moves", this, position);
    }

    public Collection<Move> allMoves(final Color color) {
        return callClojure("all-moves", this, color);
    }

    public Collection<Neighbor> neighbors(final Position position) {
        return callClojure("neighbors", this, position);
    }

    public Board applyMove(final Move move) {
        return callClojure("apply-move", this, move);
    }

    public boolean hasLost(final Color playerColor, final boolean isFirstMoveOfTurn) {
        return callClojure("lost?", this, playerColor, isFirstMoveOfTurn);
    }

    @Override
    public String toString() {
        return callClojure("board-to-str", this);
    }

    private static <TReturn> TReturn callClojure(final String clojureFnName,
                                                 final Object... params) {
        final IFn clojureFn = ClojureLayer.JAVA_API.function(clojureFnName);
        return (TReturn) clojureFn.applyTo(ArraySeq.create(params));
    }
}
