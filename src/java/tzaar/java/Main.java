package tzaar.java;

import clojure.lang.Keyword;
import java.util.function.Consumer;

import static tzaar.java.ClojureLayer.TO_JAVA;

public class Main {

    public static void main(String[] args) {
        System.out.println(Api.randomBoard()
            .allMoves(Color.White)
            .stream()
            .findFirst());
        final tzaar.player.Player p = new Player() {
            @Override
            public void play(Color color, Board board, boolean isFirstTurn, Consumer<Turn> playTurn) {
                final Move.Attack attackMove = board.allMoves(color)
                        .stream()
                        .filter(Move::isAttack)
                        .map(move -> (Move.Attack) move)
                        .findFirst()
                        .get();
                final Move secondMove;
                if(isFirstTurn) {
                    secondMove = Move.Pass;
                } else {
                    secondMove = board.applyMove(attackMove)
                            .allMoves(color)
                            .stream()
                            .findFirst()
                            .orElse(Move.Pass);
                }
                playTurn.accept(new Turn(attackMove, secondMove));
            }
        }.toClojure();

        Api.playGame(Api.RANDOM_BUT_LEGAL_AI, p, Board.random());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
