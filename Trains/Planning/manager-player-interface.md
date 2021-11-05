The tournament manager component is meant to interact with all the players that sign up for a tournament, 
the referee, and tournament observers. We will go through all the tournament phases to spell out what needs 
to be added to our Player API so that a tournament manager may interact with it, as well as to our 
Referee Player Protocol to also include any tournament Manager-Player interactions.

In the future, we expect there to be additional methods that will be called on the Referee by the Tournament 
Manager to initiate games within a tournament and to receive stats about each game once they end.

The following memo specifies which API methods need to be added, and in what order our protocol 
will call on them. The ordering of interactions between the tournament manager and the player, as well
as between the player and referee, are outlined in this document: [Player Protocol](https://github.ccs.neu.edu/CS4500-F21/bighorn/blob/master/Trains/Planning/player-protocol.md)

### Manager-Player Interface, The Plan

1. Setting up the tournament
2. Ending the tournament

#### Setting up the tournament
*This phase is for selecting the Players that will be participating in the tournament*

* <code>void beginTournament(Boolean chosen)</code>
  * Signature: Boolean ->
  * Interpretation: Tells this Player that they have been selected to participate in the tournament and that it is about to begin.
  * Purpose: To indicate to the player that they will be participating in a tournament shortly. It will only ever been called on players who have been selected and <code>chosen</code> will always be true.

#### Ending the tournament
*This phase is for telling each Player that participated in the tournament whether they won the tournament or not. Once each Player has been given this information, the tournament is effectively over.*
* <code>void endTournament(Boolean won)</code>
  * Signature: Boolean -> 
  * Interpretation: tells this Player whether they have won the tournament. <code>won</code> is true if the Player won, false otherwise.
  * Purpose: To give the result of the tournament to each Player who participated.

*In between the setup and ending tournament phases, there are a series of method calls between the Player and Referee, as described in this document: [Player Interface](https://github.ccs.neu.edu/CS4500-F21/bighorn/blob/master/Trains/Planning/player-interface.md)
  
