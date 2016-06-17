package tzaar.javaapi;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Board {
    public final Slot[][] slots;

    public Board(Slot[][] slots) {
        this.slots = slots;
    }

    public Slot lookup(int x, int y) {
        return lookup(new Position(x, y));
    }

    public Slot lookup(Position position) {
        final Slot s;
        if(0 <= position.y && position.y < slots.length) {
            final Slot[] row = slots[position.y];
            if(0 <= position.x && position.x < row.length) {
                s = row[position.x];
            } else {
                s = Slot.Nothing;
            }
        } else {
            s = Slot.Nothing;
        }
        return s;
    }

    public static Board standard() {
        return callClojure("default-board", Board.class);
    }

    public static Board random() {
        return callClojure("random-board", Board.class);
    }

    public Move[] moves(Position position) {
        return callClojure("moves", Move[].class, "board", this, "position", position);
    }

    public Move[] allMoves(Color color) {
        return callClojure("all-moves", Move[].class, "board", this, "color", color);
    }

    public Neighbor[] neighbors(Position position) {
        return callClojure("neighbors", Neighbor[].class, "board", this, "position", position);
    }

    public Board applyMove(Move move) {
        return callClojure("apply-move", Board.class, "board", this, "move", move);
    }

    public boolean hasLost(Color color) {
        return callClojure("lost?", boolean.class, "board", this, "color", color);
    }

    @Override
    public String toString() {
        return callClojure("board-to-str", String.class, "board", this);
    }

    private static final Gson JSON = Json.SERIALIZER;

    private static <TReturn> TReturn callClojure(String clojureFnName, Class<TReturn> returnType, Object... params) {
        // TODO Run this only once
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("tzaar.jsonapi"));

        final Map<String, Object> arguments = new HashMap<>();
        for (int i = 0; i < params.length; i = i + 2) {
            arguments.put((String)params[i], params[i + 1]);
        }

        final IFn clojureFn = Clojure.var("tzaar.jsonapi", clojureFnName);
        final String result;
        if(arguments.isEmpty()) {
            result = (String)clojureFn.invoke();
        } else {
            result = (String)clojureFn.invoke(JSON.toJson(arguments));
        }
        return JSON.fromJson(result, returnType);
    }
}
