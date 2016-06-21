package tzaar.java;

public abstract class Slot {

    private Slot() {}

    public boolean isEmpty() {
        return this == Empty;
    }

    public boolean isNothing() {
        return this == Nothing;
    }

    public boolean isStack() {return this != Empty && this != Nothing; }

    public static class Stack extends Slot {
        public final tzaar.java.Stack stack;

        public Stack(final tzaar.java.Stack stack) {
            this.stack = stack;
        }

        @Override
        public String toString() {
            return stack.toString();
        }
    }

    public static final Slot Nothing = new Slot() {
        @Override
        public String toString() {
            return "Nothing";
        }
    };
    public static final Slot Empty = new Slot() {
        @Override
        public String toString() {
            return "Empty";
        }
    };
}
