(ns tzaar.parser
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

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

(def read-board (comp parse-board slurp io/resource))