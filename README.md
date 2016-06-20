# tzaar

Clojure implementation of the abstract strategy game Tzaar by Kris Burm

##Usage

You can play tzaar from Clojure and from Java.

The player implementation should not keep track of the game state.
The game should be able to re-use your AI instance for other games as well.
When the `play` method on your AI
gets called all necessary game state gets passed into it.
You are of course allowed to use state for purposes of machine learning or anything
else other than tracking individual game progress.

### From Clojure

Implementing a new player (human or AI):

```clojure
(def your-ai
  (reify tzaar.player/Player
    (-play [this color board first-turn?
            play-turn]
    ...)
```

Starting a game:

```clojure
(require [tzaar.player :as player]
         [tzaar.command-line :refer :all])
(command-line-game player/random-but-legal-ai
                   player/random-but-legal-ai
                   core/default-board)
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
        ...
    }
}
```
*(Also check out tzaar.java.ExamplePlayer to see typical API usage)*

Starting a game:
```java
Api.playGame(Api.RANDOM_BUT_LEGAL_AI,
        new YourAI().toClojure(),
        Board.random());
```

## License

Copyright © 2016 Frank Versnel

Distributed under the Eclipse Public License version 1.0
