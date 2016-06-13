(ns tzaar.core
  (require [tzaar.parser :as parser]))

(def empty-board (parser/read-board "empty-board"))
(def default-board (parser/read-board "default-board"))

(def move-types #{:attack :stack})

(def piece-types #{:tzaar :tzarra :tott})
(defn single-piece [color type] [[color type]])
(defn stack-color [piece] (first (peek piece)))
(defn stack-color? [color]
  (fn [piece] (= color (stack-color piece))))
(defn stack-type [piece] (second (peek piece)))
(defn stack-size [piece] (count piece))
(defn stack? [slot] (vector? slot))

(defn update-position [board [x y] new-slot]
  (assoc-in board [y x] new-slot))

(defn lookup-slot [board [x y]]
  (if (contains? board y)
    (let [row (nth board y)]
      (if (contains? row x)
        (nth row x)
        :nothing))
    :nothing))

(defn iterate-stacks [board]
  (for [y (range 0 (count board))
        x (range 0 (count (nth board y)))
        :let [slot (lookup-slot board [x y])]
        :when (stack? slot)]
    {:position [x y] :stack slot}))

; Returns: [{:move :attack :position {:x 1 :y 2}]
(defn possible-moves [board position]
  (let [stack (lookup-slot board position)
        color (stack-color stack)]
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
                               :move-type (if (= color (stack-color (:slot %)))
                                            :stack
                                            :attack)}
                              :nothing))
                       first)))]
        (let [moves [; Horizontal
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
          (->> moves
               (remove #(= :nothing %))
               ; Remove pieces that cannot be attacked
               (remove (fn [{:keys [to move-type]}]
                         (and (= move-type :attack)
                              (let [enemy-stack (lookup-slot board to)]
                                (< (stack-size stack) (stack-size enemy-stack))))))
               set))))))

(defn attack-move? [move] (= :attack (:move-type move)))
(defn stack-move? [move] (= :stack (:move-type move)))

(defn all-possible-moves [board color]
  (->> board
       iterate-stacks
       (filter #(= color (stack-color (:stack %))))
       (map :position)
       (map #(possible-moves board %))
       flatten))

; Return: :none, :white :black
; Optionally add under which condition the player has won
(defn lost? [board player-color]
  (let [board-pieces (->> board
                          iterate-stacks
                          (map :stack)
                          (filter (stack-color? player-color))
                          (map stack-type))
        moves (all-possible-moves board player-color)
        attack-moves (filter attack-move? moves)]
    (or (not= piece-types (set board-pieces))
        (empty? attack-moves))))

(defn apply-move [board {:keys [from to move-type] :as move}]
  (let [from-stack (lookup-slot board from)
        to-stack (lookup-slot board to)
        new-stack (case move-type
                    :attack from-stack
                    :stack (concat from-stack to-stack))]
  (-> board
      (update-position from :empty)
      (update-position to new-stack))))


; TODO Finish random board generator
(defn random-board []
  (let [color-pieces (fn [color] (map #(single-piece color %)
                                      (concat (repeat 6 :tzaar)
                                              (repeat 9 :tzarra)
                                              (repeat 15 :tott))))
        shuffled-pieces (shuffle (concat (color-pieces :white)
                                         (color-pieces :black)))]
    (loop [pieces shuffled-pieces
           board empty-board]
      (if (empty? pieces)
        board
        ; Find empty space, put piece in it
        (let [new-board ()]
          (recur (rest pieces) new-board)))

      )))
