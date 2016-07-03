package tzaar.java;

public class StackWithPosition {
    public final Position position;
    public final Stack stack;

    public StackWithPosition(final Position position, final Stack slot) {
        this.position = position;
        this.stack = slot;
    }

    @Override
    public String toString() {
        return String.format("[Position: %s, Stack: %s]", position, stack);
    }
}
