(ns tzaar.jsonapi
  (require [tzaar.core :as core]
           [cheshire.core :as cheshire]
           [clojure.walk :refer [postwalk]]
           [camel-snake-kebab.core :refer [->camelCase
                                           ->kebab-case]]))

(defn- strings-to-keywords [form]
  (postwalk #(if (string? %) (keyword %) %)
        form))

(defmacro ^:private defn-json
  "Takes a name, a vector of arguments, and a function
   and generates a named function that takes json as input
   parses the json as a map, then threads the keys from the
   map specified in the args-list into the given function in
   the order they're specified:

   (defn-json example [key1 key2] test) becomes:
   (defn example [json {:keys [pretty?]}]
     (letfn [(apply-f [{:keys [key1 key2]}] (f key1 key2))]
       (-> json
         (parse-string (comp keyword ->kebab-case))
         string-to-keyword
         apply-f
         (generate-string {:key-fn (comp name ->camelCase)
                           :pretty pretty?}))))"
  [name args-list f]
  (if (empty? args-list)
    `(defn ~name [{:keys [~'pretty?]}]
       (cheshire/generate-string (~f) {:pretty ~'pretty?}))
    `(defn ~name [~'json {:keys [~'pretty?]}]
       (letfn [(apply-f# [{:keys [~@args-list]}]
                 (~f ~@args-list))]
         (-> ~'json
             (cheshire/parse-string (comp keyword ->kebab-case)))
             strings-to-keywords
             apply-f#
             (cheshire/generate-string {:key-fn (comp name ->camelCase)
                                        :pretty ~'pretty?})))))

(defn-json neighbors [board position] core/neighbors)
(defn-json moves [board position] core/moves)
(defn-json all-moves [board color] core/all-moves)
(defn-json apply-move [board move] core/apply-move)
(defn-json board-to-str [board] core/board-to-str)
(defn-json lost? [board color] core/lost?)

(defn-json random-board [] core/random-board)
(def default-board (cheshire/generate-string core/default-board))