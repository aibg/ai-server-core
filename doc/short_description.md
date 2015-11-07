AI Battleground - the variation of Conway's Game of Life
--------------------------------------------------------

AI Battleground is a multiplayer artificial intelligence game that uses Conway's
Game of Life rules and some additional restrictions for multiplayer game.

Classic Game of Life ruleset specifies:
	- any live cell with fewer than two live neighbours dies, as if caused
	by under-population
	- any live cell with two or three live neighbours lives on to the next
	generation
	- any live cell with more than three live neighbours dies, as if by
	over-population
	- any dead cell with exactly three live neighbours becomes a live
	cell, as if by reproduction

The game depends only on the initial pattern of the system, on the other hand,
in AI Battleground it is possible (and is the main problem) to migrate, e.g.
to colonise cells.

The winner of the game is the one that covers greater number of cells after
game HALTs (for example, HALT can be reasonable time limit).


Communicating with the system
-----------------------------

In the current version of AI Battleground, the user communicates with the 
system using JSON (1*). All of the JSON attributes must be provided as an
answer to avoid exceptions or illegal states.

The answer is commited via stream (stdout). For C++, Python and Java
languages, additional utility function are provided for parsing JSON and
automatically committing actions.


(1*) Details about JSON attributes can be found in src/main/java/hr/best/...
/ai/games/conway/ConwayGameState.java, ConwayGameState class.


AI Battleground graphics engine
-------------------------------

The current AI Battleground implementation provides a simple GUI for battle
simulations (PvP). The GUI includes:
	- a finite number of cells representing battleground
	- small images representing each player's colony
	- players' names in the up left and right corner of the screen
	- players' overall points below the names
	- illustrative domination chart at the top of the screen
	- AI Battleground background :)


Some word about game logic and strategies
-----------------------------------------


