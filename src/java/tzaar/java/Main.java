package tzaar.java;

public class Main {

    public static void main(String[] args) {
        Color winner = Api.playGame(
                Api.COMMAND_LINE_PLAYER,
                new ExamplePlayer().toClojure(),
                Board.random());
    }
}
