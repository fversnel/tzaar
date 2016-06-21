(ns tzaar.logger)

(defprotocol Logger
  (-log [logger more]))

(def system-out-logger
  (reify Logger
    (-log [_ more] (apply print more) (flush))))

(def no-op-logger
  (reify Logger
    (-log [_ _])))

(defn logln [logger & more]
  (-log logger (conj (vec more) \newline)))

(defn log [logger & more]
  (-log logger more))