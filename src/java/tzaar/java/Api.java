package tzaar.java;

import tzaar.player.CommandlinePlayer;
import tzaar.player.RandomButLegalAI;
import tzaar.util.logging.Logger;

import static tzaar.java.ClojureLayer.*;

public class Api {
    private Api() { }

    public static final Logger SYSTEM_OUT_LOGGER = (Logger) LOGGING.deref("system-out-logger");
    public static final Logger NO_OP_LOGGER = (Logger) LOGGING.deref("no-op-logger");

    public static Board defaultBoard() {
        return Board.standard();
    }
    public static Board randomBoard() {
        return Board.random();
    }

    public static final tzaar.player.Player COMMAND_LINE_PLAYER = new CommandlinePlayer();
    public static final tzaar.player.Player RANDOM_BUT_LEGAL_AI = new RandomButLegalAI();

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
