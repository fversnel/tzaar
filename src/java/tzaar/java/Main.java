package tzaar.java;

public class Main {

    public static void main(String[] args) {
        Board.standard().hasLost(Color.Black, true);
        Api.playGame(Api.RANDOM_BUT_LEGAL_AI,
                new ExamplePlayer().toClojure(),
                Board.random());
    }
}
