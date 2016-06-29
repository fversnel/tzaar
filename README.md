# tzaar

Clojure implementation of the abstract strategy game Tzaar by Kris Burm

##Usage

You can play tzaar from Clojure and from Java.

You can create your own AI by implementing the `Player` interface.
Your implementation should not have to keep track of the game state.
When the `play` method on your AI
gets called all essential game state gets passed into it.
The game engine should be able to re-use your AI instance for other games as well.
You are of course allowed to use state for purposes of machine learning or anything
else but be wary of these requirements. Also the implementation of `play` needs
to be thread-safe.

### From Clojure

With Leiningen/Boot:

```clojure
[org.fversnel/tzaar "0.1.0"]
```

Implementing a new player (human or AI):

```clojure
(defrecord YourAI []
  tzaar.player/Player
  (-play [this game-state play-turn]
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

Add tzaar to your Gradle build file:
```groovy
repositories {
    maven { url "http://clojars.org/repo" }
    mavenCentral()
}

dependencies {
    compile "org.clojure:clojure:1.9.0-alpha8"
    compile "org.fversnel:tzaar:0.1.0"
}
```

Implementing a new player:

```java
public class YourAI implements tzaar.java.Player {
    @Override
    public void play(final GameState gameState,
                     final Consumer<Turn> playTurn) {
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
> lein uberjar
```

Then command it to run a few games:
```shell
> java -cp "your-ai.jar;target/tzaar-0.1.0-SNAPSHOT-standalone.jar" \
 tzaar.runner \
 -white tzaar.players.ai.provided.RandomButLegalAI \
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

- Keep track of time taken by an AI and report it at the end of the match
- Allow for optional use of chess-like clocks
- Allow players to resign
- Add Java Stream API?
- Add GUI
- Add support for Clojurescript
- Write a clojure.spec and use it to write tests

## License

Copyright © 2016 Frank Versnel

Distributed under the Eclipse Public License version 1.0
