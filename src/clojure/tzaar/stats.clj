(ns tzaar.stats
  (:require [tzaar.core :as core]))

(defn- combine [v1 v2]
  (cond
    (number? v1) (+ v1 v2)
    (coll? v1) (concat v1 v2)))

(def initial-stats {:time-taken 0
                    :total-turns 0
                    :total-attack-moves 0
                    :total-stack-moves 0
                    :total-pass-moves 0})

(defn turn->move-stats [turn]
  (->> turn
       (map (fn [{:keys [move-type]}]
              (case move-type
                :attack {:total-attack-moves 1}
                :stack {:total-stack-moves 1}
                :pass {:total-pass-moves 1})))
       (apply merge-with +)))

(defn update-stats [current-stats turn]
  (let [time-taken (:tzaar/time-taken (meta turn))]
    (merge-with combine
                current-stats
                (turn->move-stats turn)
                {:time-taken time-taken
                 :total-turns 1})))

(defn stats [finished-game]
  (letfn [(player-stats [color]
            (let [turns (->> (:turns finished-game)
                             (filter core/resignation?)
                             (partition 2)
                             (map (if (= color :white) first second)))]
              (reduce update-stats initial-stats turns)))]
    {:white (player-stats :white)
     :black (player-stats :black)}))

;(defn average-stats [all-stats]
;  (apply merge-with average all-stats))

  ;; Sort all stacks from high to low, drop all stacks of 1 height
  ;(->> (core/iterate-stacks :white board)
  ;     (remove (fn [stack] (= (core/stack-size stack) 1)))
  ;     ; Order from high to low
  ;     (sort-by core/stack-size)
  ;     reverse
