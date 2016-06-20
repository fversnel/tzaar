package tzaar.java;

import java.util.function.Consumer;

public class ExamplePlayer implements Player {
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
}
