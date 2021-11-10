# Reworked Code During Milestone 7

1. instead of type checking the TurnAction by using enums, use the visitor pattern to apply the action  
We reworked the TurnAction class into an interface, and created two implementations for DrawCardsAction
and AcquireConnectionAction. We then created IActionVisitor to implement the visitor pattern over 
TurnActions. The concrete implementation ActionVisitor performs the given action on the game state
for the Referee.  
TODO: ADD links

2. remove playerID from various data representations that store it  
Our PlayerData class contained an ID field, that was unused. we removed it and its getter.
TODO: ADD LINKS

3. add a method to determine all connections that can still be acquired for a specific Player
This was feedback given to us in Milestone 4, and was fixed for Milestone 5.  
TODO: ADD LINKS.

4. need to check that the player has enough rails and cards to occupy the connection in the RefereeGameState  
This was feedback given to us in Milestone 4, and was fixed for Milestone 5.  
TODO: ADD LINKS

5. need to separate out ranking from the playing game method 
We made the method to calculate scores (calculateGameEndReport) public so that it can be called in a 
separate step from playing the game. We made the refereeGameState a parameter of the TrainsReferee 
class so that the calculateGameEndReport has access to it without taking in any arguments.
TODO: ADD LINKS.
note to us: line 184 in TrainsReferee