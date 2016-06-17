package tzaar.javaapi;

public abstract class Move {

    private Move() {}

    public boolean isPass() {
        return this == Pass;
    }

    public static class Attack extends Move {
        public Position from;
        public Position to;

        public Attack(Position from, Position to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return String.format("[Attack from: %s, to: %s]", from, to);
        }
    }

    public static class Stack extends Move {
        public Position from;
        public Position to;

        public Stack(Position from, Position to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return String.format("[Stack from: %s, to: %s]", from, to);
        }
    }

    public static final Move Pass = new Move() {
        @Override
        public String toString() {
            return "Pass";
        }
    };
}
