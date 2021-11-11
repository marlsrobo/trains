# Reworked Code During Milestone 7
We did not fix each issue in its own commit, so the github links point to the place in the code where
we reworked each item.  

1. instead of type checking the TurnAction by using enums, use the visitor pattern to apply the action  
We reworked the TurnAction class into an interface, and created two implementations for DrawCardsAction
and AcquireConnectionAction. We then created IActionVisitor to implement the visitor pattern over 
TurnActions. The concrete implementation ActionVisitor performs the given action on the game state
for the Referee.  
Directory containing our Action classes and visitors:  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/tree/241b7024e6a05f2269044283804963bae3dee77b/Trains/Player/action  
Using the visitor in TrainsReferee:  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/241b7024e6a05f2269044283804963bae3dee77b/Trains/Admin/referee/TrainsReferee.java#L236-L239  

2. remove playerID from various data representations that store it  
Our PlayerData class contained an ID field, that was unused. We removed it and its getter.  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/241b7024e6a05f2269044283804963bae3dee77b/Trains/Admin/referee/game_state/PlayerData.java  

3. add a method to determine all connections that can still be acquired for a specific Player
This was feedback given to us in Milestone 4, and was fixed for Milestone 5.  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/241b7024e6a05f2269044283804963bae3dee77b/Trains/Common/game_state/IPlayerGameState.java#L32-L38  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/241b7024e6a05f2269044283804963bae3dee77b/Trains/Common/game_state/PlayerGameState.java#L35-L42  

4. need to check that the player has enough rails and cards to occupy the connection in the RefereeGameState  
This was feedback given to us in Milestone 4, and was fixed for Milestone 5. We created an ActionChecker
class that performs all validation on player's actions.
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/241b7024e6a05f2269044283804963bae3dee77b/Trains/Admin/referee/ActionChecker.java  

5. need to separate out ranking from the playing game method 
We made the existing method to calculate scores (calculateGameEndReport) public so that it can be 
called in a separate step from playing the game. We made the refereeGameState a parameter of the 
TrainsReferee class so that the calculateGameEndReport has access to it without taking in any 
arguments.  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/241b7024e6a05f2269044283804963bae3dee77b/Trains/Admin/referee/IReferee.java#L20-L25
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/241b7024e6a05f2269044283804963bae3dee77b/Trains/Admin/referee/TrainsReferee.java#L191-L201