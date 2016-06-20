(ns tzaar.player
  (require [clojure.spec :as s]
           [tzaar.spec :as spec]
           [tzaar.core :as core]))

(defprotocol Player
  (-play [player color board first-turn? play-turn]))

(defn play
  [player color board first-turn? play-turn]
  (-play player color board first-turn?
         (fn [turn]
           (if (and (s/valid? ::spec/turn turn)
                    (core/valid-turn? board color first-turn? turn))
             (play-turn turn)
             (throw (Exception. "Invalid play"))))))

(def random-but-legal-ai
  (reify tzaar.player/Player
    (-play [_ color board first-turn? play-turn]
      (let [attack-move (->> (core/all-moves board color)
                             (filter core/attack-move?)
                             shuffle
                             first)
            second-move (let [new-board (core/apply-move board attack-move)]
                          (->  (core/all-moves new-board color)
                               shuffle
                               first
                               (or core/pass-move)))]
        (play-turn [attack-move (if first-turn? core/pass-move second-move)])))))