package tzaar.java;

import clojure.lang.IFn;

import java.util.function.Consumer;

public interface Player {
    void play(Color color, Board board, Consumer<Turn> playTurn);

    /**
     * Wraps a typed Java Player implementation such that it
     * can be used in the Clojure implementation of Tzaar.
     */
    default tzaar.player.Player toClojure() {
        final IFn fromJava = ClojureNamespaces.JAVA_CONVERSION.function("from-java");
        final IFn toJava = ClojureNamespaces.JAVA_CONVERSION.function("to-java");

        return new tzaar.player.Player() {
            @Override
            public Object _play(final Object color, final Object board, final Object playTurn) {
                Player.this.play(
                        (Color) toJava.invoke(Color.class, color),
                        (Board) toJava.invoke(Board.class, board),
                        turn -> ((IFn) playTurn).invoke(fromJava.invoke(turn)));
                return null;
            }

            @Override
            public String toString() {
                return Player.this.toString();
            }
        };
    }
}