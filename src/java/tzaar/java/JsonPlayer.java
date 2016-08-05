package tzaar.java;

import clojure.lang.IFn;
import java.util.function.Consumer;

public abstract class JsonPlayer implements tzaar.player.Player {

    private static final IFn GameStateToJson = ClojureLayer.JSON_API.function("game-state->json");
    private static final IFn JsonToTurn = ClojureLayer.JSON_API.function("json->turn");

    @Override
    public final Object _play(Object gameState, Object playTurn) {
        play((String)GameStateToJson.invoke(gameState),
            turnJson -> {
                final Object turn = JsonToTurn.invoke(turnJson);
                ((IFn)playTurn).invoke(turn);
            });
        return null;
    }

    protected abstract void play(String gameState, Consumer<String> playTurn);
}
