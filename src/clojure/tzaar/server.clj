(ns tzaar.server
  (:require [tzaar.player :refer [Player]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(def config (-> "config.edn" io/resource slurp edn/read-string))

(defn websocket-player [socket]
  (reify Player
    (-play [_ {:keys [board] :as game-state} play-turn]
    ; TODO Use https://github.com/ptaoussanis/sente
    )))

; TODO Whenever someone posts to /create-game
;      We start a websocket and start a game against an ai.
;      We create a websocket player that will we feed into the
;      game and that will be periodically asked to play a move.
;
;      On the client we then render the board each time (play) is called.