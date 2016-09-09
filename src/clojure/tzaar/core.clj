(ns tzaar.core
  (:require [tzaar.parser :as parser]
            ;[taoensso.timbre.profiling :as profiling :refer [p defnp]]
            [clojure.string :as string]))

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

(defrecord Slot [slot position])
(defrecord Move [move-type from to])

(def player-colors #{:white :black})
(defn opponent-color [player-color]
  (if (= player-color :white) :black :white))

(def move-types #{:attack :stack :pass})
(defn attack-move? [move] (= :attack (:move-type move)))
(defn stack-move? [move] (= :stack (:move-type move)))
(def pass-move (->Move :pass nil nil))
(defn pass-move? [move] (= :pass (:move-type move)))

(def stack-types #{:tzaar :tzarra :tott})
(defn single-stack [color type] [[color type]])
(defn top-piece [[piece]] piece)
(defn stack? [slot] (not (#{:empty :nothing} slot)))
(defn stack-color [[[color _]]] color)
(defn stack-color? [color stack] (= color (stack-color stack)))
(defn stack-type [[[_ type]]] type)
(defn stack-size [stack] (count stack))

(defn update-position [board [^int x ^int y] new-slot]
  (assoc-in board [y x] new-slot))

(defn lookup [board [x y]]
  (-> board (nth y) (nth x)))

(defn iterate-slots [board]
  (for [y (range (count board))
        x (range (count (nth board y)))
        :let [position [x y]]]
    (->Slot (lookup board position) position)))

(defn iterate-stacks [color board]
  (eduction (filter (fn [{:keys [slot]}]
                      (and (stack? slot)
                           (stack-color? color slot))))
            (iterate-slots board)))

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

(defn first-turn? [{:keys [turns]}]
  (empty? turns))

(defn whos-turn [{:keys [turns]}]
  (if (even? (count turns)) :white :black))

(defn stack-type-missing?
  [board color]
  (let [stacks (into #{}
                     (map (comp stack-type :slot))
                     (iterate-stacks color board))]
    (not= stack-types stacks)))

(defn safe-lookup [board [^int x ^int y]]
  (or (get-in board [y x]) :nothing))

(defn neighbors [board position]
  (letfn [(neighbor [[Δx Δy]]
            (eduction
              (comp (drop 1)
                    (map #(->Slot (safe-lookup board %) %))
                    (remove #(= :empty (:slot %)))
                    (take 1))
              (iterate (fn [[x y]] [(+ x Δx) (+ y Δy)]) position)))]
    (sequence (comp (mapcat neighbor)
                    (remove #(= :nothing (:slot %))))
              [; horizontal
               [1 0] [-1 0]
               ; vertical
               [0 1] [0 -1]
               ; diagonal
               [-1 -1] [1 1]])))

(defn moves [board position]
  (let [slot (lookup board position)]
    (if (stack? slot)
      (let [stack slot
            color (stack-color stack)
            is-enemy? #(stack-color? color (:slot %))]
        (into #{}
              (comp (map #(->Move
                           (if (is-enemy? %) :attack :stack)
                           position
                           (:position %)))
                    (remove (fn [move]
                              (case (:move-type move)
                                ; Remove stacks that cannot be attacked
                                :attack (let [enemy-stack (lookup board (:to move))]
                                          (< (stack-size stack)
                                             (stack-size enemy-stack)))
                                ; Remove moves that would kill yourself
                                :stack (stack-type-missing?
                                         (apply-move board move)
                                         color)))))
              (neighbors board position)))
      #{})))

(defn all-moves
  [board color]
  (sequence (comp (map :position)
                  (mapcat #(moves board %)))
            (iterate-stacks color board)))

(defn valid-move?
  [board color first-turn-move? move]
  (or (pass-move? move)
      (and ((moves board (:from move)) move)
           (or (not first-turn-move?) (attack-move? move))
           (= color (stack-color (lookup board (:from move)))))))

(defn apply-turn [board turn]
  (reduce apply-move board turn))

(defn valid-turn?
  [{:keys [board] :as game-state} turn]
  (let [player-color (whos-turn game-state)
        [first-move second-move] turn]
    (and
      (valid-move? board player-color true first-move)
      (if-not (first-turn? game-state)
        (valid-move? (apply-move board first-move)
                     player-color
                     false
                     second-move)
        (nil? second-move)))))

; Optionally add under which condition the player has lost
(defn lost?
  ([game-state]
   (lost? (:board game-state)
          (whos-turn game-state)
          true))
  ([board player-color first-turn-move?]
    (let [moves (all-moves board player-color)
          attack-moves (filter attack-move? moves)]
      (or (stack-type-missing? board player-color)
          (and first-turn-move? (empty? attack-moves))))))

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

(defn board->str [board]
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

(defn color->str [color]
  (string/capitalize (name color)))

(defn- position->coordinate [[x y]]
  (let [column (string/upper-case (char (+ x (int \a))))
        row (+ y 1)]
    (str column row)))

(defn move->str [move]
  (case (:move-type move)
    :attack (str (position->coordinate (:from move))
                 " attacks "
                 (position->coordinate (:to move)))
    :stack (str (position->coordinate (:from move))
                " stacks "
                (position->coordinate (:to move)))
    :pass "passes"))

(defn turn->str [turn]
  (->> turn
       (map move->str)
       (map #(str "'" % "'"))
       (string/join " then ")))