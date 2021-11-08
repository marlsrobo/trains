## Self-Evaluation Form for Milestone 4

Indicate below each bullet which file/unit takes care of each task:

1. the method/function header for determining all connections available for
   acquisition plus at least one unit test 
   - Determining all occupied connections is done via 
   https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Common/game_state/RefereeGameState.java#:~:text=public%20Map%3CIRailConnection,%7D
   - Determining all possible connections is done via the train map, which can be retrieved via 
   https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Common/game_state/RefereeGameState.java#:~:text=public%20ITrainMap%20getGameMap,%7D
   - Once the train map is retrieved, you can get all connections from it via 
   https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Common/map/TrainMap.java#:~:text=public%20Set%3CIRailConnection,%7D
   - With knowledge of the occupied connections and possible connections, the available connections can be calculated via set difference.

2. the method/function header for deciding the legality of an acquisition
   request from a player to the referee,  plus at least two unit harnesses
   - The only legality that the referee state checks is that the connection exists and is unoccupied: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Common/game_state/RefereeGameState.java#:~:text=%40Override-,public%20void%20acquireConnection(String%20playerID%2C%20IRailConnection%20railConnection)%20%7B,%7D,-%40Override
   - We expect the referee to check that the player has enough cards and enough rails in the bank. We also expect the referee to remove the cards from hand and rails from bank, because we did not want the referee state to impose rules of valid _actions_, only valid _states_ in this case.
   - Unit test to ensure that connection is unoccupied: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/Tests/UnitTestClasses/TestRefereeGameState.java#:~:text=%40Test-,public%20void%20TestAcquireConnectionAlreadyOccupied()%20%7B,%7D,-%40Test
   - Unit test to ensure that requested connection exists: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/Tests/UnitTestClasses/TestRefereeGameState.java#:~:text=%40Test-,public%20void%20TestAcquireConnectionRailDoesntExist()%20%7B,%7D,-%40Test

3. the method/function header for producing a player-game state from a
   referee-game state,  plus a unit test 
   - Method: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Common/game_state/RefereeGameState.java#:~:text=public%20IPlayerGameState%20createPlayerGameState,%7D
   - Unit Test: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/Tests/UnitTestClasses/TestRefereeGameState.java#:~:text=public%20void%20TestCreatePlayerGameState,%7D

The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.
