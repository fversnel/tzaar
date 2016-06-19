package tzaar.java;

import tzaar.java.util.ClojureNamespace;

public class ClojureNamespaces {
    private ClojureNamespaces() {}

    public static final ClojureNamespace JAVA_API = new ClojureNamespace("tzaar.javaapi");
    public static final ClojureNamespace COMMAND_LINE = new ClojureNamespace("tzaar.command-line");
    public static final ClojureNamespace JAVA_CONVERSION = new ClojureNamespace("clojure.java.data");
}
