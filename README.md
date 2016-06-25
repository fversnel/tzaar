# tzaar

Clojure implementation of the abstract strategy game Tzaar by Kris Burm

##Usage

You can play tzaar from Clojure and from Java.

You can create your own AI by implementing the `Player` interface.
Your implementation should not keep track of the game state.
The game should be able to re-use your AI instance for other games as well.
When the `play` method on your AI
gets called all essential game state gets passed into it.
You are of course allowed to use state for purposes of machine learning or anything
else other than tracking individual game progress.
Note that the thread calling `play` on the `Player` does not have to be the same
thread each time.

### From Clojure

Implementing a new player (human or AI):

```clojure
(defrecord YourAI []
  tzaar.player/Player
  (-play [this color board first-turn? play-turn]
    ; AI logic here

    ; Submit the turn asynchronously
    (play-turn turn)))
```

Starting a game:

```clojure
(require [tzaar.game :as game])
(game/play-game game/random-but-legal-ai
                (YourAI.)
                game/default-board)
```

### From Java

Implementing a new player:

```java
public class YourAI implements tzaar.java.Player {
    @Override
    public void play(Color playerColor,
                     Board board,
                     boolean isFirstTurn,
                     Consumer<Turn> playTurn) {
        // AI logic here

        // Submit the turn asynchronously
        playTurn.accept(turn);
    }
}
```
*(Also check out tzaar.java.ExamplePlayer to see typical API usage)*

Starting a game:
```java
final Object whitePlayer = Api.RANDOM_BUT_LEGAL_AI;
final Object blackPlayer = new YourAI();
Api.playGame(whitePlayer, blackPlayer, Api.randomBoard());
```

### Using the runner

Once you got your AI ready to go you can pack it into a jar and run it with the tzaar runner.
The runner is a command-line program that works as follows:

First build the runner with Leiningen
```shell
lein uberjar
```

Then command it to run a few games
```shell
> java -cp "your-ai.jar;target/tzaar-0.1.0-SNAPSHOT-standalone.jar" \
 tzaar.runner \
 -white tzaar.player.RandomButLegalAI \
 -black package.name.YourAI \
 -games 10 \
 -logging true
```
Outputs:
```shell
Played 10 games of Tzaar:
White (RandomButLegalAI) wins 30% of the games in average 25 turns
Black (YourAI) wins 70% of the games in average 24 turns
```

## To be done

- Allow players to resign
- Add Java Stream API?
- Add GUI
- Add support for Clojurescript

## License

Copyright © 2016 Frank Versnel

Distributed under the Eclipse Public License version 1.0
