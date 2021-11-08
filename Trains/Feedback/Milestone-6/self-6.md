## Self-Evaluation Form for Milestone 6

Indicate below each bullet which file/unit takes care of each task:

1. In the player component, identify the following pieces of functionality:

  - `setup`, the function/method for receiving the game map etc.
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/player/Player.java#:~:text=public%20void%20setup,%7D
  - `pick`, the function/method for picking destinations from given alternatives
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/player/Player.java#:~:text=public%20Set%3CDestination,%7D
  - `play`, the function/method for taking a turn 
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/player/Player.java#:~:text=public%20TurnAction%20takeTurn,%7D
  - `more_cards`, the function/method for receiving more cards (if available)
Currently does not do anything because it is not necessary for this minimal player implementation, but is available in the interface.
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/player/Player.java#:~:text=public%20void%20receiveCards(List%3CRailCard%3E%20drawnCards)%20%7B%7D
  - `win`, the function/method for receiving information about the outcome of the game
Currently does not do anything because it is not necessary for this minimal player implementation, but is available in the interface.
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/player/Player.java#:~:text=public%20void%20winNotification(boolean%20thisPlayerWon)%20%7B%7D
2. In the referee component, identify the following pieces of major functionality:

  - a start-up phase, i.e., for setting up players with maps and destinations
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Admin/referee/TrainsReferee.java#:~:text=%7D-,private%20Optional%3CIPlayerData%3E%20setupPlayer(,%7D,-private%20boolean%20validDestinationChoice
  - a play-turns phase, i.e., for running the game proper 
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Admin/referee/TrainsReferee.java#:~:text=turn%20was%20significant%20*/-,private%20boolean%20takePlayerTurn(Iterator%3CIPlayer%3E%20turnOrder%2C%20IRefereeGameState%20gameState)%20%7B,%7D,-private%20TurnResult%20applyActionToActivePlayer
  - a shut down phase, i.e., for informing players of the outcome 
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Admin/referee/TrainsReferee.java#:~:text=%7D-,private%20GameEndReport%20calculateGameEndReport(IRefereeGameState%20gameState)%20%7B,%7D,-private%20List%3CIPlayer

3. In the referee component, identify the following pieces of scoring functionality and their unit tests: 
All points are assigned here (https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Admin/referee/ScoreCalculator.java#:~:text=private%20static%20int%20assignPoints,%7D) based on calculated information (specified below).
  - the functionality for granting points for segments per connection
Uses this functionality: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Admin/referee/ScoreCalculator.java#:~:text=private%20static%20int%20calculateTotalNumSegments,%7D
Test with 0 segments: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/UnitTests/UnitTestClasses/TestScoreCalculator.java#:~:text=public%20void%20testScoreNoConnections,%7D
Test with nonzero segments: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/UnitTests/UnitTestClasses/TestScoreCalculator.java#:~:text=public%20void%20testScoreCompletedDestination,%7D
  - the functionality for granting points for longest path
Uses this functionality for calculating who has a longest path: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Admin/referee/ScoreGraphUtils.java#:~:text=*/-,public%20static%20Set%3CInteger%3E%20calculatePlayersWithLongestPath(,%7D,-private%20static%20int
Test with one player in game, receiving longest path: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/UnitTests/UnitTestClasses/TestScoreCalculator.java#:~:text=public%20void%20testScoreNoConnections,%7D
Test with multiple players, one player non-trivially wins longest path: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/UnitTests/UnitTestClasses/TestScoreCalculator.java#:~:text=public%20void%20testScoreMultiplePlayers,data1%2C%20data2%2C%20data3)))%3B
Test with multiple players, but not all, nontrivially tying for longest path (and other edge cases): https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/UnitTests/UnitTestClasses/TestScoreGraphUtils.java#:~:text=for%20longest%20path-,Assertions.assertEquals(,ScoreGraphUtils.occupiedConnectionsToGraph(p1))))%3B,-//%20Now%20the%200th
  - the functionality for granting points for destinations connected
Uses this functionality for determining how many destinations were completed: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Admin/referee/ScoreGraphUtils.java#:~:text=public%20static%20int%20calculateNumDestinationsConnected
Test with 0 total destinations: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/UnitTests/UnitTestClasses/TestScoreCalculator.java#:~:text=public%20void%20testScoreNoConnections,%7D
Test with some completed, some incomplete destinations:
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/UnitTests/UnitTestClasses/TestScoreCalculator.java#:~:text=public%20void%20testScoreMultiplePlayers,%7D
More tests for calculating number of complete destinations:
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/UnitTests/UnitTestClasses/TestScoreGraphUtils.java#:~:text=public%20void%20testCalculateNumDestinationsConnected,%7D
4. In the referee component, identify the functionality for ranking players 
For ranking players for calculating score report: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Admin/referee/TrainsReferee.java#:~:text=%7D-,private%20GameEndReport%20calculateGameEndReport(IRefereeGameState%20gameState)%20%7B,%7D,-private%20List%3CIPlayer
5. In the referee component, identify the functionality for eliminating misbehaving players 
Removing players for abnormal behavior during turns: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Admin/referee/TrainsReferee.java#:~:text=this.removedPlayersIndices.add,gameState.removeActivePlayer()%3B
Removing players for abnormal behavior during setup: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Admin/referee/TrainsReferee.java#:~:text=activeDestinationList%2C%20deck%2C%20map)%3B-,if%20(thisPlayersData.isPresent())%20%7B,%7D,-%7D

The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.
