package tzaar.java;

public class Turn {
    public final Move.Attack firstMove;
    public final Move secondMove;

    public Turn(Move.Attack firstMove, Move secondMove) {
        this.firstMove = firstMove;
        this.secondMove = secondMove;
    }
}
