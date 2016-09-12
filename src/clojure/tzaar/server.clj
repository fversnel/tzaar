(ns tzaar.server
  (:require [tzaar.player :refer [Player]]
            [tzaar.jsonapi :as jsonapi]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.core.async :refer [>! <! <!! go go-loop
                                        chan put! alts! timeout
                                        pipeline promise-chan]]))

(def config (-> "config.edn" io/resource slurp edn/read-string))

(defn channel-player [{:keys [send-chan receive-chan]}]
  (reify Player
    (-play [_ game-state play-turn]
      (go (>! send-chan game-state)
          (play-turn (<! receive-chan))))))

(defn wrap-chans [send-xform
                  receive-xform
                  {:keys [send-chan receive-chan]}]
  {:send-chan (let [from (chan)]
                (pipeline 1
                          send-chan
                          send-xform
                          from)
                from)
   :receive-chan (let [to (chan)]
                   (pipeline 1
                             to
                             receive-xform
                             receive-chan)
                   to)})

(def json-chans (partial wrap-chans
                         (map jsonapi/game-state->json)
                         (map jsonapi/json->turn)))


(defn create-game [request]


  )

; TODO Whenever someone posts to /create-game
;      We start a websocket and start a game against an ai.
;      We create a websocket player that will we feed into the
;      game and that will be periodically asked to play a move.
;
;      On the client we then render the board each time (play) is called.