DATE: October 20, 2021  
TO:  CS4500 Staff, Northeastern University  
FROM: Laura Calderon and Marley Robinson, Northeastern University  
RE: Design for Player Interface 

For this design task, we focused on the 3 major "phases" of a game of Trains: set up, 
playing turns, and ending the game. During each phase, the players and referee must communicate
to update the game state. 

We decided on the player taking the initiative of making moves by asking the referee. Our "player interface" is thus the design 
of an API that communicates with the ref, requesting more cards, attempting to acquire connections, etc. The order of 
method calls will correspond with the order in which they appear in this memo. 

### Player Interface, The Plan

1. Set up
2. Playing Turns
3. Ending the Game

#### Setup 
*All the methods in the Setup phase must be called once, before any other API calls, and in the order in which they appear*
* boolean requestCardsAndRails();
  * Signature: -> boolean
  * Interpretation: returns true if the setup of cards and rails is successful.
  * Purpose: To request pieces from the referee (this happens during the setup phase of the game). Initializes
the rails and cards for the player.
  

* Set<Set\<String\>> getPreliminaryDestinations();
  * Signature: -> Set<Set\<String\>> 
  * Interpretation: Each Set<String> is a destination with 2 city names each. The method returns a Set of multiple
destinations (5 total)
  * Purpose: To get a set of destinations for this player to pick from. 
  

* boolean pickDestinations(Set\<String\> destination1, Set\<String\> destination2)
  * Signature: Set\<String\> Set\<String\> -> boolean 
  * Interpretation: Each Set\<String\> represents a destination with 2 city names each. The method will return true 
if destinations are successfully picked.
  * Purpose: To select the 2 destinations for this player. If successful, returns true.

#### Playing Turns
*All the methods in the "Playing Turns" phase must be called after the end of the "Setup" and before the "Ending" phase*
* boolean attemptAcquireConnection(String city1, String city2, String color);
  * Signature: string city1, string city2 and string color (of the connection to be acquired) 
-> boolean 
  * Interpretation: Returns true if successfully acquired, false otherwise
  * Purpose: To acquire a connection on the map. If the acquisition is legal, the referee will then update 
  the rails and cards of this player.


* boolean requestAdditionalCards();
  * Signature: -> boolean 
  * Interpretation: Returns true if the player gets additional cards
  * Purpose: To request additional cards from the referee. If successful, returns true. 

#### Ending 
*All the methods in the "Ending" phase must be called after the end of the "Playing Turns" phase*
* int getScore();
  * Signature: -> int
  * Interpretation: returns the score of the player
  * Purpose: To get the current players score from the referee. 


* int getRanking();
  * Signature: -> int
  * Interpretation: returns the ranking of the player, a positive integer
  * Purpose: To get the current players ranking from the referee. Returns an integer >= 1
