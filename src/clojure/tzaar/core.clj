(ns tzaar.core
  (require [tzaar.parser :as parser]
           [clojure.string :as string]
           [clojure.edn :as edn]))

(def empty-board (parser/read-board "empty-board"))
(def default-board (parser/read-board "default-board"))

; TODO Refactor such that the game state is passed around functions
; This makes it easier and clearer to determine which moves are valid

; {:board initial-board
;  :moves []}
;
;(defn initial-state [board]
;  {:ïnitial-board board
;   :turns []
;   :current-board board})
;
;(defn whos-turn? [{:keys [turns] :as game-state}]
;  (if (even? (count turns)) :white :black))

(def move-types #{:attack :stack :pass})
(defn attack-move? [move] (= :attack (:move-type move)))
(defn stack-move? [move] (= :stack (:move-type move)))
(def pass-move {:move-type :pass})
(defn pass-move? [move] (= move pass-move))

(def stack-types #{:tzaar :tzarra :tott})
(defn single-stack [color type] [[color type]])
(defn top-piece [stack] (first stack))
(defn stack? [slot] (sequential? slot))
(defn stack-color [stack]
  (when (stack? stack) (first (top-piece stack))))
(defn stack-color? [color stack] (= color (stack-color stack)))
(defn stack-type [stack] (second (top-piece stack)))
(defn stack-size [stack] (count stack))

(defn update-position [board [^int x ^int y] new-slot]
  (assoc-in board [y x] new-slot))

(defn lookup [board [^int x ^int y]]
  (or (get-in board [y x]) :nothing))

(defn iterate-slots [board]
  (for [y (range (count board))
        x (range (count (nth board y)))
        :let [position [x y]]]
    (array-map :slot (lookup board position)
               :position position)))

(defn iterate-stacks [color board]
  (->> board
       iterate-slots
       (filter (fn [{:keys [slot]}]
                 (and (stack? slot)
                      (stack-color? color slot))))))

(defn apply-move
  [board {:keys [from to move-type] :as move}]
  (if-not (pass-move? move)
    (let [from-stack (lookup board from)
          to-stack (lookup board to)
          new-stack (case move-type
                      :attack from-stack
                      :stack (concat from-stack to-stack))]
      (-> board
          (update-position from :empty)
          (update-position to new-stack)))
    board))

(defn stack-type-missing?
  [board color]
  (let [stacks (->> board
                    (iterate-stacks color)
                    (map :slot)
                    (map stack-type))]
    (not= stack-types (set stacks))))

(defn neighbors [board position]
  (letfn [(neighbor [Δx Δy]
            (->> position
                 (iterate (fn [[x y]] [(+ x Δx) (+ y Δy)]))
                 (drop 1) ; You're not your own neighbor
                 (map #(array-map :slot (lookup board %)
                                  :position %))
                 (remove #(= :empty (:slot %)))
                 first))]
    [; Horizontal
      (neighbor 1 0)
      (neighbor -1 0)
      ; Vertical
      (neighbor 0 1)
      (neighbor 0 -1)
      ; Diagonal
      (neighbor -1 -1)
      (neighbor 1 1)]))

(defn moves [board position]
  (if (stack? (lookup board position))
    (let [stack (lookup board position)
          color (stack-color stack)]
      (->> (neighbors board position)
           (remove #(= :nothing (:slot %)))
           (map #(array-map
                   :from position
                   :to (:position %)
                   :move-type (if (stack-color? color (:slot %))
                                 :stack
                                 :attack)))
           ; Remove stacks that cannot be attacked
           (remove (fn [move]
                     (and (attack-move? move)
                          (let [enemy-stack (lookup board (:to move))]
                            (< (stack-size stack) (stack-size enemy-stack))))))
           ; Remove moves that would kill yourself
           (remove (fn [move]
                     (and (stack-move? move)
                          (stack-type-missing?
                            (apply-move board move)
                            color))))
           set))
    #{}))

(defn attack-moves [board position]
  (-> (moves board position)
      (filter attack-move?)
      set))

(defn all-moves [board color]
  (->> board
       (iterate-stacks color)
       (map :position)
       (mapcat #(moves board %))))

(defn valid-move?
  [board color first-turn-move? move]
  (or (pass-move? move)
      (and ((moves board (:from move)) move)
           (or (not first-turn-move?) (attack-move? move))
           (= color (stack-color (lookup board (:from move)))))))

(defn apply-turn
  [board [first-move second-move]]
  (-> board
      (apply-move first-move)
      (apply-move second-move)))

(defn valid-turn?
  [board color first-turn? [first-move second-move]]
  (and
    (valid-move? board color true first-move)
    (or (not first-turn?) (pass-move? second-move))
    (valid-move? (apply-move board first-move) color false second-move)))

; Optionally add under which condition the player has lost
(defn lost?
  [board player-color first-turn-move?]
  (let [moves (all-moves board player-color)
        attack-moves (filter attack-move? moves)]
    (or (stack-type-missing? board player-color)
        (and first-turn-move? (empty? attack-moves)))))

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

(defn board-to-str [board]
  (letfn [(stack-to-str [stack]
            (str (if (< 1 (stack-size stack))
                   (stack-size stack)
                   \space)
                 (case (top-piece stack)
                       [:white :tott] "w1"
                       [:white :tzarra] "w2"
                       [:white :tzaar] "w3"
                       [:black :tott] "b1"
                       [:black :tzarra] "b2"
                       [:black :tzaar] "b3")))
          (slot-to-str [slot]
            (cond
              (stack? slot) (stack-to-str slot)
              (= :empty slot) " e "
              (= :nothing slot) " n "))]
    (let [column-indices (->> \a
                              int
                              (iterate inc)
                              (map (comp #(str " " % " ")
                                         string/capitalize
                                         char))
                              (take (count board)))
          row-strs (for [row-index (range (count board))]
                     (->> (get board row-index)
                       (map slot-to-str)
                       (string/join \space)
                       (str (+ row-index 1) "  ")))]
      (str
        (str "   " (string/join \space column-indices))
        \newline
        (string/join \newline row-strs)))))

(defn color-to-str [color]
  (string/capitalize (name color)))