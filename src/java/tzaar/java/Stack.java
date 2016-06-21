package tzaar.java;

import java.util.List;

public class Stack {
    public final List<Piece> pieces;

    public Stack(final List<Piece> pieces) {
        this.pieces = pieces;
    }

    public Piece topPiece() {
        return pieces.get(0);
    }

    public int size() {
        return pieces.size();
    }

    @Override
    public String toString() {
        return String.format("[%s, %d]", topPiece(), size());
    }
}
