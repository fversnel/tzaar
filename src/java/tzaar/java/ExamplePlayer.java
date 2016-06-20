package tzaar.java;

import java.util.function.Consumer;

public class ExamplePlayer implements Player {
    @Override
    public void play(Color playerColor, Board board,
                     boolean isFirstTurn, Consumer<Turn> playTurn) {
        final Move.Attack attackMove = board.allMoves(playerColor)
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
                    .allMoves(playerColor)
                    .stream()
                    .findFirst()
                    .orElse(Move.Pass);
        }
        playTurn.accept(new Turn(attackMove, secondMove));
    }
}
