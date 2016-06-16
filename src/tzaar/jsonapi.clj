(ns tzaar.jsonapi
  (require [tzaar.core :as core]
           [cheshire.core :as cheshire]))

(def ^:private parse-keywords true)

(defmacro ^:private def-json
  "Takes a name, a vector of arguments, and a function
   and generates a function that takes json as input
   parses the json as a map, then threads the keys from the
   map specified in the args-list into the given function in
   the order they're specified:

   (def-json example [key1 key2] test) becomes:
   (defn example [json]
     (letfn [(apply-f [{:keys [key1 key2]}]
               (f key1 key2))
       (-> json
         parse-string
         apply-f
         generate-string)))"
  [name args-list f]
  `(defn ~name
     ~(if (empty? args-list) '[] '[json])
      (letfn [(apply-f# [{:keys [~@args-list]}]
                (~f ~@args-list))]
        (-> ~(if (empty? args-list)
               '{}
               '(cheshire/parse-string json parse-keywords))
            apply-f#
            cheshire/generate-string))))

(def-json neighbors [board position] core/neighbors)
(def-json moves [board position] core/moves)
(def-json all-moves [board color] core/all-moves)
(def-json apply-move [board move] core/apply-move)
(def-json random-board [] core/random-board)
(def-json board-to-str [board] core/board-to-str)
(def-json lost? [board] core/lost?)