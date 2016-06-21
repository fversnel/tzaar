package tzaar.java;

public class Neighbor {
    public final Position position;
    public final Slot slot;

    public Neighbor(final Position position, final Slot slot) {
        this.position = position;
        this.slot = slot;
    }

    @Override
    public String toString() {
        return String.format("[Position: %s, Slot: %s]", position, slot);
    }
}
