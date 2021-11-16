## Self-Evaluation Form for Milestone 7

Please respond to the following items with

1. the item in your `todo` file that addresses the points below.

2. a link to a git commit (or set of commits) and/or git diffs the resolve
   bugs/implement rewrites: 

It is possible that you had "perfect" data definitions/interpretations
(purpose statement, unit tests, etc) and/or responded to feedback in a
timely manner. In that case, explain why you didn't have to add this
to your `todo` list.

These questions are taken from the rubric and represent some of 
critical elements of the project, though by no means all of them.

If there is anything special about any of these aspects below, you may also point to your `reworked.md` and/or `bugs.md` files. 

### Game Map 

- a proper data definition with an _interpretation_ for the game _map_

We did not add anything related to the game map to our todo list because we did not receive any negative feedback from the TAs, and we also liked our design.

Our data definition is located here:
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Common/map/ITrainMap.java#L9-L15

### Game States 

- a proper data definition and an _interpretation_ for the player game state  

Item 2 under functionality in our ToDo list related to the player game state, and was fixed in Milestone 5.  
"add a method to determine all connections that can still be acquired for a specific Player"  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Common/game_state/IPlayerGameState.java#L32-L38  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Common/game_state/PlayerGameState.java#L35-L42  

Our data definition for player game state is located here:  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Common/game_state/IPlayerGameState.java#L9-L21

- a purpose statement for the "legality" functionality on states and connections 

We did not add anything related to the legality function to our todo list because we did not receive any negative feedback from the TAs. We updated our legality checking to be located in the ActionChecker class during Milestone 6.

The purpose statement for the legality function is located here:
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Admin/referee/IActionChecker.java

- at least _two_ unit tests for the "legality" functionality on states and connections 

https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Other/UnitTests/UnitTestClasses/TestActionChecker.java#L77-L83
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Other/UnitTests/UnitTestClasses/TestActionChecker.java#L93-L99
The file in both of these links contains several more unit tests for legality.

### Referee and Scoring a Game

The functionality for computing scores consists of 4 distinct pieces of functionality:

  - awarding players for the connections they connected
Function:
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Admin/referee/ScoreCalculator.java#L81-L89

  - awarding players for destinations connected
Function:
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Admin/referee/ScoreGraphUtils.java#L112-L141
Unit Tests:
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Other/UnitTests/UnitTestClasses/TestScoreGraphUtils.java#L71-L124

  - awarding players for constructing the longest path(s)
Function calculating the path for one player:
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Admin/referee/ScoreGraphUtils.java#L49-L69
Function calculating which players had the longest paths:
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Admin/referee/ScoreGraphUtils.java#L22-L47
Unit Tests:
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Other/UnitTests/UnitTestClasses/TestScoreGraphUtils.java#L23-L57

  - ranking the players based on their scores 
Function: 
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Admin/referee/ScoreCalculator.java#L12-L54
Unit Tests:
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Other/UnitTests/UnitTestClasses/TestScoreCalculator.java#L41-L59

Point to the following for each of the above: 

  - piece of functionality separated out as a method/function:
  - a unit test per functionality

### Bonus

Explain your favorite "debt removal" action via a paragraph with
supporting evidence (i.e. citations to git commit links, todo, `bug.md`
and/or `reworked.md`).  

Our favorite "debt removal" activity was reworking our return type for a player's action on their turn to use the visitor pattern. It removed two places in our code where we performed a switch statement on the type of the action, both of which were very uncomfortable to look at when we wrote them the first time. After we were reminded of the visitor pattern in class, it was a relief to fix both of those areas and make our design more extensible at the same time.   
The switches were located at:  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Other/TestHarnesses/harnesses/XStrategy.java#L48-L56  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Admin/referee/TrainsReferee.java#L229-L240  
Our visitor to apply the action to the game state is now located at:  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/Trains/Player/action/ActionVisitor.java
We mentioned this change in item 1 in our `reworked.md` here:  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/f10d768adb25a25de0f976ad44c1f8aa18ffd03e/7/reworked.md





