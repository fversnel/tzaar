(ns tzaar.runner
  (:require [tzaar.game :as game]
            [tzaar.player]
            [tzaar.javaapi]
            [tzaar.jsonapi]
            [tzaar.players.ai.frank2]
            [tzaar.util.logging :as logging]
            [clojure.edn :as edn]
            [tzaar.core :as core]
            [tzaar.util.timer :as timer])
  (:gen-class))

(defn run-games
  [white-player
   black-player
   board-gen
   logger
   {:keys [n-games] :as opts}]
  (doall
    (for [_ (range n-games)]
      (game/play-game white-player black-player (board-gen) logger))))

(defn -main
  [& args]
  {:pre [(even? (count args))]}
  ; command-line args: -white tzaar.player.CommandlinePlayer
  ;                    -black tzaar.player.RandomButLegalAI
  ;                    -games 100
  ;                    -logging true
  (let [args (apply hash-map args)
        create-player (fn [class-name]
                        (.newInstance (Class/forName class-name)))
        players {:white (create-player (get args "-white"))
                 :black (create-player (get args "-black"))}
        logger (if (edn/read-string (or (get args "-logging") "false"))
                 logging/system-out-logger
                 logging/no-op-logger)
        n-games (Integer/parseInt (or (get args "-games") "1"))]
    ; TODO Don't keep accumulating finished games as we will eventually
    ; run out of memory, instead reduce the games into a final map
    ; that represents win count for each player and total turn count
    (println "Starting runner...")
    (let [finished-games (run-games (:white players)
                                    (:black players)
                                    core/random-board
                                    logger
                                    {:n-games n-games})
          games-by-winner (group-by :winner finished-games)
          total-turns (->> finished-games
                           (map (comp count :turns))
                           (reduce +))
          avg-turns (fn [games]
                      (if (< 0 (count games))
                        (int (/ total-turns (count games)))
                        "-"))
          avg-time (fn [player-color]
                     (let [total-time (->> finished-games
                                           (map #(get-in % [:stats player-color :time-taken]))
                                           (reduce +))]
                       (timer/format-nanos (/ total-time total-turns))))
          percentage-wins (fn [games]
                            (int (* (/ (count games) (count finished-games)) 100)))
          print-player (fn [player-color]
                         (println (core/color->str player-color)
                                  (str "(" (.getSimpleName (.getClass (player-color players))) ")")
                                  "wins"
                                  (str (percentage-wins (player-color games-by-winner)) "%")
                                  "of the games"
                                  "in average" (avg-turns (player-color games-by-winner)) "turns"
                                  "taking" (avg-time player-color) "per turn"))]
      (println "Played" (count finished-games) "games of Tzaar:")
      (print-player :white)
      (print-player :black))))
