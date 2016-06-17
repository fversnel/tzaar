(ns tzaar.player
  (require [clojure.spec :as s]
           [tzaar.spec :as spec]
           [tzaar.core :as core]))

(defprotocol Player
  (-play [player color board play-turn]))

(defn play
  [player color board play-turn]
  (-play player color board
         (fn [turn]
           (if (and (s/valid? ::spec/turn turn)
                    (core/valid-turn? board color turn))
             (play-turn turn)
             (throw (Exception. "Invalid play"))))))

(def random-but-legal-ai
  (reify Player
    (-play [_ color board play-turn]
      (let [first-move (->> (core/all-moves board color)
                            (filter core/attack-move?)
                            shuffle
                            first)
            second-move (let [new-board (core/apply-move board first-move)]
                          (-> new-board
                              (core/all-moves color)
                              shuffle
                              first
                              (or core/pass-move)))]
        (play-turn [first-move second-move])))))