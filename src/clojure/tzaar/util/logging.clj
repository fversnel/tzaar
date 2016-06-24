(ns tzaar.util.logging)

(defprotocol Logger
  (-write [logger more])
  (-enabled? [logger]))

(defrecord SystemOutLogger []
  Logger
  (-write [_ more] (apply print more) (flush))
  (-enabled? [_] true))

(defrecord NoOpLogger []
  Logger
  (-write [_ _])
  (-enabled? [_] false))

(def system-out-logger (->SystemOutLogger))
(def no-op-logger (->NoOpLogger))

(defmacro write [logger & more]
  `(when (-enabled? ~logger)
     (-write ~logger [~@more])))

(defmacro writeln [logger & more]
  `(write ~logger ~@(conj (vec more) \newline)))