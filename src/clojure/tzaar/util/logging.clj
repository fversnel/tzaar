(ns tzaar.util.logging)

(defprotocol Logger
  (-write [logger more])
  (-enabled? [logger]))

(def system-out-logger
  (reify Logger
    (-write [_ more] (apply print more) (flush))
    (-enabled? [_] true)))

(def no-op-logger
  (reify Logger
    (-write [_ _])
    (-enabled? [_] false)))

(defmacro write [logger & more]
  `(if (-enabled? ~logger)
     (-write ~logger [~@more])))

(defmacro writeln [logger & more]
  `(write ~logger ~@(conj (vec more) \newline)))