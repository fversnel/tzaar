package tzaar.javaapi;

import java.util.function.Consumer;

public interface Player {
    void play(Color color, Board board, Consumer<Turn> playTurn);
}