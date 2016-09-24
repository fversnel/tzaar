(ns tzaar.players.rabbitmq
  (:require [tzaar.player :refer [Player]]
            [tzaar.jsonapi :as json]
            [langohr.core :as rmq]
            [langohr.queue :as lq]
            [langohr.consumers :as lc]
            [langohr.channel :as lch]
            [langohr.basic :as lb]
            [clojure.core.async :refer [>! <! <!! go go-loop
                                        chan put! alts! timeout
                                        promise-chan]]
            [cheshire.core :as cheshire]
            [tzaar.core :as core]))

(def rabbit-mq-settings
  {:username "frank", :password "frank_2016", :vhost "/", :host "192.168.88.251", :port 5672})

(def ^:private ^:const parse-keywords true)

;(def conn (langohr.core/connect
; {:username "frank", :password "frank_2016", :vhost "/", :host "192.168.88.251", :port 5672}))
;(def channel (langohr.channel/open conn))

(defn rabbitmq-player []
  (let [conn (rmq/connect rabbit-mq-settings)
        channel (langohr.channel/open conn)
        turn-chan (chan 1)
        submit-turn (fn [_ {:keys [content-type delivery-tag type]} ^bytes payload]
                      (let [json-turn (String. payload "UTF-8")]
                        (println (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s, type: %s"
                                         json-turn delivery-tag content-type type))
                        (put! turn-chan
                              (-> json-turn
                                  (cheshire/parse-string parse-keywords)
                                  :turn))))]
    (lc/subscribe channel "tzaar_turns" submit-turn {:auto-ack true})
    (reify
      tzaar.player/Player
      (-play [_ {:keys [board] :as game-state} play-turn]
        (println
          "Json game state"
          (json/game-state->json game-state))
        (lb/publish channel
                    ""
                    "tzaar_turn_requests"
                    (json/game-state->json game-state)
                    {:content-type "application/json"})
        (go
          (let [player-color (core/whos-turn game-state)
                turn (reduce (fn [turn move]
                               (let [board (core/apply-turn board turn)
                                     to-color (core/stack-color (core/lookup board (:to move)))]
                                 (conj turn (core/->Move
                                              (if (= player-color to-color)
                                                :stack
                                                :attack)
                                              (:from move)
                                              (:to move)))))
                             []
                             (<! turn-chan))]
                (play-turn turn)))))))