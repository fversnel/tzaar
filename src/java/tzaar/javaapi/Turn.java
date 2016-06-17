package tzaar.javaapi;

public class Turn {
    public final Move.Attack FirstMove;
    public final Move SecondMove;

    public Turn(Move.Attack firstMove, Move secondMove) {
        FirstMove = firstMove;
        SecondMove = secondMove;
    }
}
