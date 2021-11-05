For this design task, we focused on the 3 major "phases" of a game of Trains: set up,
playing turns, and ending the game. During each phase, the players and referee must communicate
to update the game state.

We decided on the referee taking the initiative of asking each player to make moves. Our "player interface" is thus the design
of an API that communicates with the ref, requesting more cards, attempting to acquire connections, etc. The order of
method calls will correspond with the order in which they appear in this memo.

### Player Interface, The Plan

1. Set up
2. Playing Turns
3. Ending the Game

#### Setup
* <code>void setup(TrainsMap map, int rails, Map<ConnectionColor, Integer> cards)</code>
  * Signature: TrainsMap, int, Map<ConnectionColor, Integer> ->
  * Interpretation: Gives the Player their initial components for the game including the map of the game, their initial number of rails, and initial colored cards
  * Purpose: Sets the Player up with their game components to allow them to make decisions on which destinations to chose later

* <code>Set\<Desination\> pick(Set\<Desination\> destinations)</code>
  * Signature: Set\<Desination\> -> Set\<Desination\>
  * Interpretation: Given some Destinations in a set, where each Destination represents a destination between 2 cities each, 
the method will return the destinations not picked by the player.
  * Purpose: To offer the Player with a selection of Destinations to chose from, and then know which Destinations they did not choose.
  
#### Playing Turns
* <code>Action play(PlayerGameState state)</code>
  * Signature: PlayerGameState -> Action
  * Interpretation: Gives the Player their current state of the game and returns the specific Action that the Player wants to take.
  * Purpose: To tell a Player that it is their turn and receive the type of turn that the Player wants to make so that the Referee can decide if the move is legal and how to update the state of the game.

* <code>void more(Map<ConnectionColor, Integer> cards)</code>
  * Signature: Map<ConnectionColor, Integer> ->
  * Interpretation: Gives a Player a randomly selected amount of colored cards
  * Purpose: To give the Player more colored cards after they had specified that they would like to request the cards as a move.

#### Ending
* <code>void win(boolean winner)</code>
  * Signature: boolean -> 
  * Interpretation: Gives the player true if they won the game, false if they lost.
  * Purpose: To tell the player if they won or lost the game and indicate the end of the game.

*Before and after the game, there are additional methods that will specifically be called by the Tournament Manager on the Player, which are outlined in this document: [Manager-Player Interface](https://github.ccs.neu.edu/CS4500-F21/bighorn/blob/master/Trains/Planning/manager-player-interface.md)
