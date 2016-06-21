(ns tzaar.logger)

(defprotocol Logger
  (-log [logger more])
  (-enabled? [logger]))

(def system-out-logger
  (reify Logger
    (-log [_ more] (apply print more) (flush))
    (-enabled? [_] true)))

(def no-op-logger
  (reify Logger
    (-log [_ _])
    (-enabled? [_] false)))

(defmacro log [logger & more]
  `(if (-enabled? ~logger)
     (-log ~logger ~(vec more))))

; TODO Describe logln in terms of log
(defmacro logln [logger & more]
  `(if (-enabled? ~logger)
     (-log ~logger ~(conj (vec more) \newline))))