package tzaar.java;

import java.util.function.Consumer;

public class ExamplePlayer implements Player {
    @Override
    public void play(final GameState gameState, final Consumer<Turn> playTurn) {
        final Color playerColor = gameState.whosTurn();
        final Board board = gameState.board;

        final Move.Attack attackMove = board.allMoves(playerColor)
                .stream()
                .filter(Move::isAttack)
                .map(move -> (Move.Attack) move)
                .findFirst()
                .get();
        final Move secondMove;
        if(gameState.isFirstTurn()) {
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
