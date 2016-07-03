package tzaar.java;

import java.util.function.Consumer;

public class ExamplePlayer implements Player {
    @Override
    public void play(final GameState gameState, final Consumer<Turn> playTurn) {
        final Color playerColor = gameState.whosTurn();
        final Board board = gameState.board;

        final Move.Attack attackMove = (Move.Attack) board.allAttackMoves(playerColor)
                .findFirst()
                .get();
        final Turn turn;
        if(gameState.isFirstTurn()) {
            turn = Turn.firstTurn(attackMove);
        } else {
            Move secondMove = board.applyMove(attackMove)
                    .allMoves(playerColor)
                    .findFirst()
                    .orElse(Move.Pass);
            turn = new Turn(attackMove, secondMove);
        }
        playTurn.accept(turn);
    }
}
