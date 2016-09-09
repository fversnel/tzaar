package tzaar.java;

import java.util.Collection;
import java.util.List;

/**
 * Created by frank on 27-6-2016.
 */
public class Main {


    public static void main(String... args) {
        FinishedGame finishedGame = Api.playGame(Api.ATTACK_THEN_STACK_AI, Api.FRANK_AI_2,
                Board.random(), Api.SYSTEM_OUT_LOGGER);

        System.out.println(finishedGame);
    }
}
