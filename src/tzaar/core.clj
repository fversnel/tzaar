(ns tzaar.core
  (require [tzaar.parser :as parser]))

(def empty-board (parser/read-board "empty-board"))
(def default-board (parser/read-board "default-board"))

(def piece-types #{:tzaar :tzarra :tott})
(def move-types #{:attack :stack})
(defn piece-color [piece] (first piece))
(defn piece-type [piece] (second piece))
(defn piece? [slot] (and (vector? slot)
                         (piece-types (piece-type slot))))

(defn in-range? [coll index]
  (and (< index (count coll)) (<= 0 index)))

(defn lookup-slot [board [x y]]
  (if (in-range? board y)
    (let [row (nth board y)]
      (if (in-range? row x)
        (nth row x)
        :none))
    :none))

; Returns: [{:move :attack :position {:x 1 :y 2}]
(defn possible-moves [board position]
  (let [piece (lookup-slot board position)
        color (piece-color piece)]
    (if (piece? piece)
      (letfn [(walk-board [{:keys [xfn yfn]}]
                (let [positions (iterate (fn [[x y]]
                                           [(xfn x) (yfn y)])
                                         position)]
                  (->> positions
                       (remove #(= position %))
                       (map (fn [pos] {:slot (lookup-slot board pos) :position pos}))
                       (remove #(= :empty (:slot %)))
                       (map #(if (piece? (:slot %))
                                (if (= color (piece-color (:slot %)))
                                  {:move :stack :position (:position %)}
                                  {:move :attack :position (:position %)})
                              :none))
                       first)))]
        (let [moves [; Horizontal moves
                     (walk-board {:xfn inc :yfn identity})
                     (walk-board {:xfn dec :yfn identity})

                     ; Vertical moves
                     (walk-board {:xfn identity :yfn inc})
                     (walk-board {:xfn identity :yfn dec})

                     ; Diagonal moves
                     (walk-board {:xfn inc :yfn inc})
                     (walk-board {:xfn inc :yfn dec})
                     (walk-board {:xfn dec :yfn inc})
                     (walk-board {:xfn dec :yfn dec})
                     ]]
          (->> moves (remove (partial = :none)) set))))))

; Return: :none, :white :black
; Optionally add under which condition the player has won
(defn winner [board])

(defn attack-moves [moves]
  (filter #(= :attack (:move %)) moves))

(defn stack-moves [moves]
  (filter #(= :stack (:move %)) moves))

; TODO Finish random board generator
(defn random-board []
  (let [color-pieces (fn [color] (map #([color %]) (concat (repeat 6 :tzaar)
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
