(ns tzaar.logger)

(defprotocol Logger
  (-logln [logger more])
  (-log [logger more]))

(def system-out-logger
  (reify Logger
    (-logln [_ more] (apply println more))
    (-log [_ more] (apply print more) (flush))))

(def no-op-logger
  (reify Logger
    (-logln [_ _])
    (-log [_ _])))

(defn logln [logger & more]
  (-logln logger more))

(defn log [logger & more]
  (-log logger more))