package tzaar.java;

public class Main {

    public static void main(String[] args) {
        Color winner = Api.playGame(Api.RANDOM_BUT_LEGAL_AI,
                new ExamplePlayer().toClojure(),
                Board.random());
    }
}
