package tzaar.java;

import clojure.lang.Keyword;
import java.util.function.Consumer;

import static tzaar.java.ClojureLayer.TO_JAVA;

public class Main {

    public static void main(String[] args) {
        Color c = (Color) TO_JAVA.invoke(Color.class, Keyword.intern(null, "white"));
        System.out.println(c);
        System.out.println(Api.randomBoard()
            .allMoves(Color.White)
            .stream()
            .findFirst());
        tzaar.player.Player p = new Player() {
            @Override
            public void play(Color color, Board board, Consumer<Turn> playTurn) {
                final Move.Attack attackMove = board.allMoves(color)
                        .stream()
                        .filter(Move::isAttack)
                        .map(move -> (Move.Attack) move)
                        .findFirst()
                        .get();

                final Move secondMove = board.applyMove(attackMove)
                        .allMoves(color)
                        .stream()
                        .findFirst()
                        .orElse(Move.Pass);

                playTurn.accept(new Turn(attackMove, secondMove));
            }
        }.toClojure();
    }
}
