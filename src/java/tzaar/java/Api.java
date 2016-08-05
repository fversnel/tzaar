package tzaar.java;

import tzaar.players.ai.provided.AttackThenStackAI;
import tzaar.players.commandline.CommandlinePlayer;
import tzaar.players.ai.provided.RandomButLegalAI;
import tzaar.util.logging.Logger;
import tzaar.util.logging.NoOpLogger;
import tzaar.util.logging.SystemOutLogger;

import static tzaar.java.ClojureLayer.*;

public class Api {
    private Api() { }

    public static final Logger SYSTEM_OUT_LOGGER = new SystemOutLogger();
    public static final Logger NO_OP_LOGGER = new NoOpLogger();

    public static Board defaultBoard() {
        return Board.standard();
    }
    public static Board randomBoard() {
        return Board.random();
    }

    public static final tzaar.player.Player COMMAND_LINE_PLAYER = new CommandlinePlayer();
    public static final tzaar.player.Player RANDOM_BUT_LEGAL_AI = new RandomButLegalAI();
    public static final tzaar.player.Player ATTACK_THEN_STACK_AI = new AttackThenStackAI();
    public static final tzaar.player.Player FRANK_AI_2 = new tzaar.players.ai.frank2.FrankAI();

    public static FinishedGame playGame(final Object whitePlayer,
                                 final Object blackPlayer,
                                 final Board board) {
        return playGame(whitePlayer, blackPlayer, board, SYSTEM_OUT_LOGGER);
    }

    public static FinishedGame playGame(final Object whitePlayer,
                                 final Object blackPlayer,
                                 final Board board,
                                 final Logger logger) {
        final Object winner = GAME.function("play-game")
                .invoke(whitePlayer,
                        blackPlayer,
                        FROM_JAVA.invoke(board),
                        logger);
        return (FinishedGame) TO_JAVA.invoke(FinishedGame.class, winner);
    }


}
