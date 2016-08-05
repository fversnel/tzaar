(ns tzaar.jsonapi
  (require [clojure.walk :as walk]
           [cheshire.core :as cheshire]))

(def ^:private ^:const parse-keywords true)

(defn keywordize [x]
  (walk/postwalk #(if (string? %) (keyword %) %) x))

(defn to-json [x]
  (cheshire/generate-string x))

(defn from-json [json]
  (cheshire/parse-string json parse-keywords))

(defn game-state->json [game-state]
  (to-json game-state))

(defn json->turn [json]
  (-> json
      from-json
      keywordize))

; Example turn:
;
;[{"move-type":"attack","from":[8,8],"to":[8,7]}
; {"move-type":"stack","from":[4,0],"to":[4,1]}]
;
; Example Game state:
;