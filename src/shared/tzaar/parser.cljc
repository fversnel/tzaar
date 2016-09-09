(ns tzaar.parser
  (:require [clojure.string :as string]))

(defn piece [color type] [[color type]])

(defn parse-board [board-str]
  (letfn [(parse-slot [slot-str]
            (case slot-str
              "w1" (piece :white :tott)
              "w2" (piece :white :tzarra)
              "w3" (piece :white :tzaar)
              "b1" (piece :black :tott)
              "b2" (piece :black :tzarra)
              "b3" (piece :black :tzaar)
              "e" :empty
              "n" :nothing))]
    (let [rows (->> board-str
                    string/split-lines
                    (map string/trim))]
      (vec
        (for [row rows
              :let [slots (string/split row #"\s+")]]
          (->> slots (map parse-slot) vec))))))

(def default-board
  (parse-board
    "w1 w1 w1 w1 b1 n  n  n  n
     b1 w2 w2 w2 b2 b1 n  n  n
     b1 b2 w3 w3 b3 b2 b1 n  n
     b1 b2 b3 w1 b1 b3 b2 b1 n
     b1 b2 b3 b1 n  w1 w3 w2 w1
     n  w1 w2 w3 w1 b1 w3 w2 w1
     n  n  w1 w2 w3 b3 b3 w2 w1
     n  n  n  w1 w2 b2 b2 b2 w1
     n  n  n  n  w1 b1 b1 b1 b1"))

(def empty-board
  (parse-board
    "e e e e e n n n n
     e e e e e e n n n
     e e e e e e e n n
     e e e e e e e e n
     e e e e n e e e e
     n e e e e e e e e
     n n e e e e e e e
     n n n e e e e e e
     n n n n e e e e e"))