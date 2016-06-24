package tzaar.java;

import java.util.function.Consumer;

public interface Player {
    void play(final Color color, final Board board, final boolean isFirstTurn,
              final Consumer<Turn> playTurn);
}