package tzaar.javaapi;

public class Stack {
    public final Piece[] pieces;

    public Stack(Piece[] pieces) {
        this.pieces = pieces;
    }

    public Piece topPiece() {
        return pieces[0];
    }

    public int size() {
        return pieces.length;
    }

    @Override
    public String toString() {
        return String.format("[%s, %d]", topPiece(), size());
    }
}
