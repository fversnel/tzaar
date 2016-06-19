package tzaar.java.util;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

import java.util.concurrent.ConcurrentHashMap;

public class ClojureNamespace {
    public static final IFn DEREF = Clojure.var("clojure.core", "deref");
    public static final IFn REQUIRE = Clojure.var("clojure.core", "require");

    private final String namespace;
    private final ConcurrentHashMap<String, IFn> functionCache;

    public ClojureNamespace(String namespace) {
        REQUIRE.invoke(Clojure.read(namespace));

        this.namespace = namespace;
        this.functionCache = new ConcurrentHashMap<>();
    }

    public IFn function(String name) {
        return functionCache.computeIfAbsent(name, key -> Clojure.var(namespace, key));
    }

    public Object deref(String name) {
        return DEREF.invoke(function(name));
    }
}
