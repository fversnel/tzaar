package tzaar.java;

public class Turn {
    public final Move.Attack firstMove;
    public final Move secondMove;

    public Turn(final Move.Attack firstMove, final Move secondMove) {
        this.firstMove = firstMove;
        this.secondMove = secondMove;
    }

    public static Turn firstTurn(final Move.Attack attackMove) {
        return new Turn(attackMove, Move.Pass);
    }
}
