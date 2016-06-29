(ns tzaar.util.timer)

(defprotocol Timer
  (nanos-elapsed [this]))

(defn- round2 ^double [n]
  (/ (double (Math/round (* (double n) 100.0))) 100.0))

(defn format-nanos [nanosecs]
  (let [ns (long nanosecs)] ; Truncate any fractionals
    (cond
      (>= ns 1000000000) (str (round2 (/ ns 1000000000))  "s") ; 1e9
      (>= ns    1000000) (str (round2 (/ ns    1000000)) "ms") ; 1e6
      (>= ns       1000) (str (round2 (/ ns       1000)) "μs") ; 1e3
      :else (str ns "ns"))))

(defn timer->str [timer]
  (format-nanos (nanos-elapsed timer)))

(defn current-time [] (System/nanoTime))

(defn start-timer []
  (let [start-time (current-time)]
    (reify Timer
      (nanos-elapsed [_] (- (current-time) start-time)))))
