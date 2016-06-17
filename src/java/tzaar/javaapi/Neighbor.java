package tzaar.javaapi;

public class Neighbor {
    public final Position position;
    public final Slot slot;

    public Neighbor(Position position, Slot slot) {
        this.position = position;
        this.slot = slot;
    }

    @Override
    public String toString() {
        return String.format("[Position: %s, Slot: %s]", position, slot);
    }
}
