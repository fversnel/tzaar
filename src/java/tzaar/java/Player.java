package tzaar.java;

import java.util.function.Consumer;

public interface Player {
    void play(final GameState gameState, final Consumer<Turn> playTurn);
}