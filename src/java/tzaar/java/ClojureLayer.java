package tzaar.java;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import tzaar.java.util.ClojureNamespace;

public class ClojureLayer {
    private ClojureLayer() {
    }

    private static final Object FRANK_AI2 = ClojureNamespace.REQUIRE.invoke(Clojure.read("tzaar.players.ai.frank2"));
    public static final ClojureNamespace TIMER = new ClojureNamespace("tzaar.util.timer");
    public static final ClojureNamespace JAVA_API = new ClojureNamespace("tzaar.javaapi");
    public static final ClojureNamespace JSON_API = new ClojureNamespace("tzaar.jsonapi");
    public static final ClojureNamespace GAME = new ClojureNamespace("tzaar.game");
    public static final ClojureNamespace LOGGING = new ClojureNamespace("tzaar.util.logging");

    private static final ClojureNamespace JAVA_CONVERSION = new ClojureNamespace("clojure.java.data");
    public static final IFn FROM_JAVA = JAVA_CONVERSION.function("from-java");
    public static final IFn TO_JAVA = JAVA_CONVERSION.function("to-java");
}
