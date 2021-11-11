# Listed below are outstanding items that would improve the code base

## Data Representation
[X] instead of type checking the TurnAction by using enums, use the visitor pattern to apply the action  
[X] remove playerID from various data representations that store it  

## Functionality
[X] need to check that the player has enough rails and cards to occupy the connection in the RefereeGameState  
[X] add a method to determine all connections that can still be acquired for a specific Player    
[X] need to separate out ranking from the playing game method  
[ ] need to abstract out a single point of control to call methods on the Player  



