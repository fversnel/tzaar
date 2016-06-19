package tzaar.java;

public class Piece {
    public enum Type { Tzaar, Tzarra, Tott }

    public final Color color;
    public final Type type;

    public Piece(Color color, Piece.Type type) {
        this.color = color;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("[%s %s]", color, type);
    }
}
