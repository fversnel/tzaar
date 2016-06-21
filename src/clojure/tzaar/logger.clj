(ns tzaar.logger)

(defprotocol Logger
  (-logln [logger more])
  (-log [logger more]))

(def system-out-logger
  (reify Logger
    (-logln [logger more] (apply println more))
    (-log [logger more] (apply print more) (flush))))

(def no-op-logger
  (reify Logger
    (-logln [logger more])
    (-log [logger more])))

(defn logln [logger & more]
  (-logln logger more))

(defn log [logger & more]
  (-log logger more))