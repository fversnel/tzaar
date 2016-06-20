package tzaar.java;

import clojure.lang.Keyword;
import java.util.function.Consumer;

import static tzaar.java.ClojureLayer.TO_JAVA;

public class Main {

    public static void main(String[] args) {
        System.out.println(Api.randomBoard()
            .allMoves(Color.White)
            .stream()
            .findFirst());

        Api.playGame(Api.RANDOM_BUT_LEGAL_AI, new ExamplePlayer().toClojure(), Board.random());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
