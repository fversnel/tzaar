package tzaar.java;

import java.util.function.Consumer;

public class ExamplePlayer implements Player {
    @Override
    public void play(final GameState gameState, final Consumer<Turn> playTurn) {
        final Color playerColor = gameState.whosTurn();
        final Board board = gameState.board;

        final Move.Attack attackMove = board.allAttackMoves(playerColor)
                .stream()
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
        Board updatedBoard;
        updatedBoard = gameState.board.applyMove(attackMove);
        updatedBoard = updatedBoard.applyMove(secondMove);
        playTurn.accept(new Turn(attackMove, secondMove));
    }
}
