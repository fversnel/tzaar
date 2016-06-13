(ns tzaar.player
  (require [clojure.spec :as s]
           [tzaar.spec :as spec]
           [tzaar.core :as core]))

(defprotocol Player
  (-play [player color board]))

(defn play
  [player color board]
  {:post [(s/valid? ::spec/turn %)]}
  (-play player color board))

(def random-but-legal-ai
  (reify Player
    (-play [_ color board]
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
        [first-move second-move]))))