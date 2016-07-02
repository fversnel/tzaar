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
               (if (core/first-turn? game-state)
                 (s/valid? ::spec/first-turn turn)
                 (s/valid? ::spec/turn turn))]}
        (let [turn (with-meta turn {:tzaar/time-taken (timer/nanos-elapsed timer)
                                    :tzaar/played-by (.getClass player)})]
          (if (core/valid-turn? game-state turn)
            (play-turn turn)
            (throw (Exception. (str (core/color->str (core/whos-turn game-state))
                                    " invalidly plays '"
                                    (core/turn->str turn)
                                    "' on board:"
                                    \newline
                                    (core/board->str (:board game-state)))))))))))