package tzaar.java;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import tzaar.java.util.ClojureNamespace;

import java.util.function.Consumer;

public class Main {

    private static final ClojureNamespace JAVA_API = new ClojureNamespace("clojure.java.data");
    private static final IFn FROM_JAVA = JAVA_API.function("from-java");
    private static final IFn TO_JAVA = JAVA_API.function("to-java");

    public static void main(String[] args) {
        final ClojureNamespace ns = new ClojureNamespace("tzaar.javaapi");
        Color c = (Color) TO_JAVA.invoke(Color.class, Keyword.intern(null, "white"));
        System.out.println(c);
//        JavaPlayer p = new JavaPlayer(new Player() {
//            @Override
//            public void play(Color color, Board board, Consumer<Turn> playTurn) {
//            }
//        });
//        p._play(Keyword.intern(null, "white"), null, null);




//        Api.playGame(Api.COMMAND_LINE_PLAYER,
//                Api.RANDOM_BUT_LEGAL_PLAYER,
//                Api.randomBoard());
    }
}
