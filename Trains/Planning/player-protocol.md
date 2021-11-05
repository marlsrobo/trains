DATE: November 3, 2021  
TO:  CS4500 Staff, Northeastern University  
FROM: Laura Calderon and Marley Robinson, Northeastern University  
RE: Design for Tournament Manager-Player-Referee Protocol 

For this design task, we are again focusing on the major "phases" of a game of Trains: set up, 
playing turns, last turns, and ending the game, which happen in that order. During each phase, 
the Player(s) and Referee must communicate to update the game state.

We have now added additional phases for beginning and ending a tournament in which the Tournament
Manager will communicate with the Player(s).

While for the previous design task we had thought the protocol would involve the Player taking the 
initiative of making moves by asking the Referee, we have decided to make our "Player Interface" 
API to be one where the Referee communicates with the Player, requesting it to make moves, pick destinations, etc.

In this memo we will describe the protocol between the Manager, Referee, and the Player(s), the ordering of 
the method/function calls, and we will describe the sequence of these orderings with a [UML diagram](#uml).

### Starting the Tournament

In this phase, the Tournament manager calls the <code>beginTournament</code> method on each Player
that the Manager has chosen to take part in the tournament. This tells the Players that the tournament
is about to begin.

### Game Phases
- Setup
- Playing turns
- Last turn
- End

Each of these phases happens in sequence, meaning that once the phase ends, it goes to the next phase 
and never goes back. During the “Setup” phase, all the methods/functions are called only once per player. 
Same thing during the “End” phase.. However, while in the “Playing Turns” phase, the method/function 
from Referee to Player will happen in sequence for each active player, and then loop/repeat until we 
move on to the “Last Turn” phase. During this phase there is one more turn for each player to make a move.

#### Game Setup   

During this phase, a series of method calls will occur in order between the Referee and the Player. 
First, the Referee will call a method on the Player called <code>setup</code>, which gives the Player 
their initial game pieces including their rails and colored cards, as well as the map to be used in the game.
The Referee then calls <code>pick</code> on the Player to offer them a set of destinations from which the
Player can pick a certain number to have for the entirety of the game. The Player then returns the
destinations that they did not pick to the Referee. These series of method calls are repeated for each Player in the game exactly once, 
in order of the order of turns for the game.

#### Game Playing  

The next phase of method calling between the Referee and the Player is during the middle of the 
game when Players are taking turns, but the game has not yet reached the last round of turns. 
First, the Referee asks the current Player (whose turn it is) to pick what kind of turn they want 
to make by calling a method <code>play(state)</code> which also provides the Player with their updated state
of the game. If the Player decides to ask for more cards, they will return "more_cards", and the Referee
will then call <code>more(cards[])</code> on the Player to provide them with the cards, if possible.
Otherwise, if the Player wants to acquire a connection, they will just return the Connection they want to acquire.
These series of method calls are repeated for every Player of the game in the specific ordering of 
turns until the condition for the final round of turns has been met.

#### Game Last Turn 

After the “Playing” phase, the “Last Turn” phase is initiated. This phase is exactly the same as the 
“Playing” phase, except that the method calls are only looped through once for every Player in the 
game, starting with the Player after the Player who initiated the last round of turns and ending with the Player 
whose turn is directly before the initiating Player.

#### Game End 

The “End” phase for the protocol will happen once every player is done with their last round of 
turns (previous phase). The Referee will loop over every Player exactly once and call <code>win(Boolean)</code>. 
This call tells the Player if they won or lost the game and signifies the end of the game.

### Ending the Tournament

In this final phase in the protocol, the Tournament manager calls the <code>endTournament</code> method on each Player
that participated in the tournament. This informs each Player whether they had won or lost the tournament and 
effectively ends the tournament.

### <a name="uml"></a>UML Diagram

![](https://github.ccs.neu.edu/CS4500-F21/bighorn/blob/master/Trains/Planning/Manager-Referee-Player%20Protocol.jpeg )
