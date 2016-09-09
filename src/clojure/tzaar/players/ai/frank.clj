;(ns tzaar.players.ai.frank
;  (:require [tzaar.core :as core]
;            [tzaar.player :refer [Player]]
;            ;[taoensso.timbre.profiling :as profiling :refer [p defnp]]
;            [clojure.walk :as walk]
;            [clojure.core.async :as a :refer [>! <! <!! go go-loop
;                                              chan put! alts! timeout]]))
;
;;(defn map-kv [m f]
;;  (reduce-kv #(assoc %1 %2 (f %3)) {} m))
;
;;(defn iterate-stacks [board color]
;;  (core/iterate-stacks color board))
;;
;;(def points {:tzaar (/ 1 6)
;;             :tzarra (/ 1 9)
;;             :tott (/ 15)})
;;
;;(defn score-board [player-color board]
;;  (letfn [(score-stack [stacks]
;;            (->> stacks
;;              ; Example: a stack of 2 with a tzaar at the top
;;              ; will count as 2 tzaars, however each stack-type
;;              ; is scored on availability a stack type with a lower
;;              ; availability with generate more points
;;              (map (fn [[type stacks]]
;;                     (let [availability (count stacks)
;;                           total-pieces (reduce + (map count stacks))]
;;                       (/ total-pieces availability))))
;;              (reduce +)))
;;          (score [color]
;;            (->> color
;;                 (iterate-stacks board)
;;                 (map :stack)
;;                 (group-by core/stack-type)
;;                 (sort-by val #(< (count %1) (count %2)))
;;                 score-stack))]
;;    (- (score player-color)
;;       (score (core/opponent player-color)))))
;;(->> (core/all-moves board player-color)
;;     (filter core/attack-move?)
;;     (map (fn [move]
;;            {:score (->> move
;;                         (core/apply-move board)
;;                         (score-board player-color))
;;             :move move}))
;;     (sort-by val #(> (:score %1) (:score %2)))
;;     first
;;     :move)
;
;(defn apply-turn [{:keys [board turns] :as game-state} turn]
;  (assoc game-state
;    :board (core/apply-turn board turn)
;    :turns (conj turns turn)))
;
;(defnp attack-moves [board color]
;  (->> (core/all-moves board color)
;       (filter core/attack-move?)))
;
;(defn rand-elem [coll]
;  (when-not (empty? coll) (rand-nth coll)))
;
;(defn random-positions [board]
;  (let [position (let [y (rand-int (count board))
;                       row (nth board y)
;                       x (rand-int (count row))]
;                   [x y])]
;    (lazy-seq (cons position (random-positions board)))))
;
;
;(defn random-stack-positions [board color]
; (->> (random-positions board)
;      (filter (fn [position]
;                (let [stack (core/lookup board position)]
;                  (and (core/stack? stack)
;                       (core/stack-color? color stack)))))))
;
;; TODO Generate random moves based on random stack positions
;(defn random-moves [board color]
;  (->> (random-stack-positions board color)
;       (map #(core/moves board %))
;       (mapcat shuffle)))
;
;; TODO Make this pretty at some point
;; TODO This has to become faster, choose random stack position
;;      enumerate moves if there are any
;(defnp random-turns
;  ([{:keys [board] :as game-state}]
;   (random-turns board (core/whos-turn game-state)))
;  ([board color]
;   (let [attack-moves (vec (attack-moves board color))]
;     (random-turns board color attack-moves)))
;  ; Don't call the 3-arity directly from other code
;  ([board color attack-moves]
;    (let [attack-move (rand-nth attack-moves)
;          board-after-attack (core/apply-move board attack-move)
;          second-move (-> (vec (core/all-moves board-after-attack color))
;                          rand-elem
;                          (or core/pass-move))
;          turn [attack-move second-move]]
;      (lazy-seq (cons turn (random-turns board color attack-moves))))))
;
;(defn possible-turns
;  [board color]
;  (for [attack-move (attack-moves board color)
;        second-move (cons
;                      core/pass-move
;                      (core/all-moves
;                        (core/apply-move board attack-move)
;                        color))]
;    [attack-move second-move]))
;
;(defn create-child [game-state]
;  (merge
;    {:game-state game-state}
;    (if (core/lost? game-state)
;      (let [winner (core/opponent-color (core/whos-turn game-state))]
;        {:terminal? true
;         :wins (update {:white 0 :black 0} winner inc)})
;      {:terminal? false
;       :visited-turns {}
;       :wins {:white 0 :black 0}})))
;
;;(defn update-node [node child]
;;  (let [turn (last (get-in child [:game-state :turns]))]
;;    (-> node
;;        (assoc :unvisited-turns (rest (:unvisited-turns node)))
;;        (update-in [:visited-turns] #(assoc % turn child)))))
;
;(defn combine-rewards [r1 r2]
;  {:white (+ (:white r1) (:white r2))
;   :black (+ (:black r1) (:black r2))})
;
;(defn expand-child [{:keys [game-state terminal?] :as node}]
;  (if terminal?
;    node
;    (let [turn (first (random-turns game-state))
;          child (if-let [child (get (:visited-turns node) turn)]
;                  child
;                  (create-child (apply-turn game-state turn)))
;          child (expand-child child)
;          visited-turns (assoc (:visited-turns node) turn child)]
;      ;(println "Turn" (count (get-in child [:game-state :turns])))
;      ;(println (map :wins (vals visited-turns)))
;      (assoc node
;          ;:unvisited-turns rest-turns
;          :visited-turns visited-turns
;          :wins (->> (vals visited-turns)
;                     (map :wins)
;                     (reduce combine-rewards))))))
;
;(defn select-best-turn [root color]
;  (let [best-turn (->> (:visited-turns root)
;                       (sort-by (comp #(get-in % [:wins color]) val) >)
;                       ffirst)]
;    [best-turn (get-in root [:visited-turns best-turn :wins])]))
;
;
;; Get all possible turns this board, remove the pass move if possible
;; Randomly sample an enemy turn, remove the pass move if possible
;
;; Monte carlo
;;  - Different versions:
;;     - Linear: Simple Monte Carlo, two-stage or N-stage Monte Carlo
;;     - Complex: Mixture Modelling, Markov Chain
;; https://randomcomputation.blogspot.nl/2013/01/monte-carlo-tree-search-in-clojure.html
;
;
;; https://stackoverflow.com/questions/9056571/monte-carlo-tree-searching-uct-implementation
;;
;; In the selection step the tree is traversed from the root node until we reach a node E,
;; where we select a position that is not added to the tree yet.
;;
;; Next, during the play-out step moves are played in self-play until the end of the game
;; is reached. The result R of this “simulated” game is +1 in case of a win for Black
;; (the first player in LOA), 0 in case of a draw, and −1 in case of a win for White.
;;
;; Subsequently, in the expansion step children of E are added to the tree.
;;
;; Finally, R is propagated back along the path from E to the root node in the
;; backpropagation step. When time is up, the move played by the program is the child
;; of the root with the highest value. (This example is taken from this paper - PDF
;;
;
;; Note: We can use parallelism for each root node (a next turn in the game)
;; Note: Since we receive the complete game state we can store it in an atom
;;       for future reference. This means the AI can become better over time
;;       not only during the game but also after many games have been played.
;;       There is a limit to this though as we can't keep accumulating memory
;
;
;(def budget 10)
;
;(def db (atom {}))
;
;;(defrecord FrankAI []
;;  Player
;;  (-play [_ game-state play-turn]
;;    (let [player-color (core/whos-turn game-state)
;;          evaluate-turn (fn [turn]
;;                          (let [turn-chan (chan 1)]
;;
;;                            (a/close! turn-chan)
;;                            turn-chan))]
;;      (go
;;        (let [turns (possible-turns board player-color)
;;              nodes (<! (merge-into [] (map evaluate-turn turns)))
;;              best-node nil]
;;          ; Select the turn of the best node
;;          (play-turn (:turn best-node)))))))
