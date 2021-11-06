# ToDo List
[ ] Fix Scoring to work
    [ ] Add calculation to determine the longest path
[ ] Refactor IStrategy and AStrategy  
    [ ] Initialize game state after setup
    [ ] Remove requestToAcquireAConnection and pickConnection from the interface  
    [ ] Improve AStrategy to be more abstracted  
    [ ] Use comparators instead of comparables  
[ ] Refactor Action to use the visitor pattern  
[ ] Make a more clear separation between Referee and RefereeGameState  
    [ ] RefereeGameState should expose fewer setters, and be in charge of handling all game state modifications  
    [ ] abstract over setting up the deck
        [ ] make referee take in a function to setup the deck
        [ ] dont shuffle the deck anywhere outside of this function
    [ ] End the game when no players take actions and not when they cant take actions 
    [ ] Store PlayerInventories as a list in turn order
    [ ] Add acquired connections to PlayerInventory
    [ ] Remove the concept of phases of the game
[ ] Refactor Player
    [ ] Change how strategy classes are loaded from files in Player to not require any specific file path or name
    [ ] Dont store the gameState in the PLayer class
        [ ] remove contents of more that update the game state
    [ ] Store information from setup to use in pick
[ ] Remove references to PlayerID