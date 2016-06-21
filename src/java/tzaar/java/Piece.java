package tzaar.java;

public class Piece {
    public enum Type { Tzaar, Tzarra, Tott }

    public final Color color;
    public final Type type;

    public Piece(final Color color, final Piece.Type type) {
        this.color = color;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("[%s %s]", color, type);
    }
}
