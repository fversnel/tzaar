(ns tzaar.core
  (require [tzaar.parser :as parser]))

(def empty-board (parser/read-board "empty-board"))
(def default-board (parser/read-board "default-board"))

(def move-types #{:attack :stack :pass})
(defn attack-move? [move] (= :attack (:move-type move)))
(defn stack-move? [move] (= :stack (:move-type move)))
(def pass-move {:move-type :pass})
(defn pass-move? [move] (= move pass-move))

(def stack-types #{:tzaar :tzarra :tott})
(defn single-stack [color type] [[color type]])
(defn stack-color [stack] (first (first stack)))
(defn stack-color? [color stack] (= color (stack-color stack)))
(defn stack-type [stack] (second (first stack)))
(defn stack-size [stack] (count stack))
(defn stack? [slot] (vector? slot))

(defn update-position [board [x y] new-slot]
  (assoc-in board [y x] new-slot))

(defn lookup-slot [board [x y]]
  (or (get-in board [y x]) :nothing))

(defn iterate-slots [board]
  (for [y (range 0 (count board))
        x (range 0 (count (nth board y)))
        :let [slot (lookup-slot board [x y])]]
    {:position [x y] :slot slot}))

(defn iterate-stacks [color board]
  (->> board
       iterate-slots
       (filter #(and (stack? (:slot %))
                     (stack-color? color (:slot %))))))

(defn apply-move
  [board {:keys [from to move-type] :as move}]
  (let [from-stack (lookup-slot board from)
        to-stack (lookup-slot board to)
        new-stack (case move-type
                    :attack from-stack
                    :stack (concat from-stack to-stack))]
    (-> board
        (update-position from :empty)
        (update-position to new-stack))))

(defn stack-type-missing?
  [board color]
  (let [stacks (->> board
                    (iterate-stacks color)
                    (map :slot)
                    (map stack-type))]
    (not= stack-types (set stacks))))

(defn possible-moves [board position]
  (let [stack (lookup-slot board position)
        player-color (stack-color stack)]
    (if (stack? stack)
      (letfn [(neighbors [{:keys [xfn yfn]}]
                (let [positions (iterate (fn [[x y]]
                                           [(xfn x) (yfn y)])
                                         position)]
                  (->> positions
                       (remove #(= position %))
                       (map #(assoc {} :slot (lookup-slot board %)
                                       :position %))
                       (remove #(= :empty (:slot %)))
                       (map #(if (stack? (:slot %))
                              {:from position
                               :to (:position %)
                               :move-type (if (= player-color (stack-color (:slot %)))
                                            :stack
                                            :attack)}
                              :nothing))
                       first)))]
        (let [neighbors [; Horizontal
                         (neighbors {:xfn inc :yfn identity})
                         (neighbors {:xfn dec :yfn identity})

                         ; Vertical
                         (neighbors {:xfn identity :yfn inc})
                         (neighbors {:xfn identity :yfn dec})

                         ; Diagonal
                         (neighbors {:xfn inc :yfn inc})
                         (neighbors {:xfn inc :yfn dec})
                         (neighbors {:xfn dec :yfn inc})
                         (neighbors {:xfn dec :yfn dec})]]
          (->> neighbors
               (remove #(= :nothing %))
               ; Remove stacks that cannot be attacked
               (remove (fn [move]
                         (and (attack-move? move)
                              (let [enemy-stack (lookup-slot board (:to move))]
                                (< (stack-size stack) (stack-size enemy-stack))))))
               ; Remove moves that would kill yourself
               (remove (fn [move]
                         (and (stack-move? move)
                              (stack-type-missing?
                                (apply-move board move)
                                player-color))))
               set)))
      #{})))

(defn all-moves [board color]
  (->> board
       (iterate-stacks color)
       (map :position)
       (mapcat #(possible-moves board %))))

; Optionally add under which condition the player has lost
(defn lost?
  [board player-color]
  (let [moves (all-moves board player-color)
        attack-moves (filter attack-move? moves)]
    (or (stack-type-missing? board player-color)
        (empty? attack-moves))))

(defn random-board []
  (let [color-stacks (fn [color] (map #(single-stack color %)
                                      (concat (repeat 6 :tzaar)
                                              (repeat 9 :tzarra)
                                              (repeat 15 :tott))))
        shuffled-stacks (shuffle (concat (color-stacks :white)
                                         (color-stacks :black)))
        empty-positions (->> empty-board
                             iterate-slots
                             (filter #(= :empty (:slot %)))
                             (map :position))]
    (loop [board empty-board
           stacks shuffled-stacks
           empty-positions empty-positions]
      (if (empty? stacks)
        board
        (recur (update-position board
                                (first empty-positions)
                                (first stacks))
               (rest stacks)
               (rest empty-positions))))))
