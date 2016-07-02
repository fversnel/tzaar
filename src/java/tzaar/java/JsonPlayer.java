package tzaar.java;

import java.util.function.Consumer;

/*
Example Game state JSON:

{"id": "id63bb3131-2835-4d3a-a42a-e6c2aebe6ec9",
 "initialBoard": [],
 "board": [],
 "turns": []}

Example Turn:

[{"moveType": "attack",
  "from": [0,0],
  "to": [0,1]},
 {"moveType": "pass"}]

First turn is always an array with one attack move in it
*/

public interface JsonPlayer {
    void play(String gameState, Consumer<String> playTurn);
}
