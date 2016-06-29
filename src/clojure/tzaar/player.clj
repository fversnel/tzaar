(ns tzaar.player
  (require [tzaar.spec :as spec]
           [tzaar.util.timer :as timer]
           [tzaar.core :as core :refer [color->str]]
           [tzaar.util.macros :refer [try-repeatedly]]
           [clojure.spec :as s]))

(defprotocol Player
  (-play [player game-state play-turn]))

(defn play
  [player game-state play-turn]
  (let [timer (timer/start-timer)]
    (-play
      player
      game-state
      (fn [turn]
        {:pre [(not-any? nil? turn)
               (s/valid? ::spec/turn turn)]}
        (let [time-taken (timer/nanos-elapsed timer)]
          (if (core/valid-turn? game-state turn)
            (play-turn [turn time-taken])
            (throw (Exception. (str (core/color->str (core/whos-turn game-state))
                                    " invalidly plays '"
                                    (core/turn->str turn)
                                    "' on board:"
                                    \newline
                                    (core/board->str (:board game-state)))))))))))