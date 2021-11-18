## Self-Evaluation Form for Milestone 8

Indicate below each bullet which file/unit takes care of each task.

The `manager` performs five completely distinct tasks, with one
closely related sub-task. Point to each of them:  

1. inform players of the beginning of the game, retrieve maps

https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Admin/tournament_manager/SingleElimTournamentManager.java#L200-L221

2. pick a map with enough destinations
	- including the predicate that decides "enough destinations"

The method to pick a map is called here:  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Admin/tournament_manager/SingleElimTournamentManager.java#L220  
We thought that how to pick a map was undefined, so we abstracted our tournament manager over this function. It can be provided in our builder for the tournament manager here:  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Admin/tournament_manager/SingleElimTournamentManager.java#L107-L120  
If no map selecter is provided to the builder, the default is here:
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Other/UnitTests/TestUtils/test_utils/TrainsMapUtils.java#L118-L133


3. allocating players to a bunch of games per round  

The method to allocate players is defined here:  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Admin/tournament_manager/PlayerAllocator.java#L15-L43
It is called in the tournament manager here:  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Admin/tournament_manager/SingleElimTournamentManager.java#L256-L257

4. run the tournament and its two major pieces of functionality:
   - run a round of games
   - run all rounds, discover termination conditions  

The method to run one round is here:    
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Admin/tournament_manager/SingleElimTournamentManager.java#L246-L272  
The method runTournament() runs all rounds, and checks termination consitions. It is located here:    
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Admin/tournament_manager/SingleElimTournamentManager.java#L139-L181

5. inform survining players at the very end whether they won the tournament  

https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Admin/tournament_manager/SingleElimTournamentManager.java#L274-L286

Next point to unit tests for:

- testing the `manager` on the same inputs as the `referee`, because
  you know the outcome  

This test runs a tournament with two players that always draw cards. From testing the Referee, we know that they will tie every game. This test tests that the tournament manager marks both of them as winners.  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Other/UnitTests/UnitTestClasses/TestSingleElimTournamentManager.java#L143-L157

- testing the allocation of players to the games of one round  

https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Other/UnitTests/UnitTestClasses/TestPlayerAllocator.java#L33-L49

Finally, the specification of the `cheat` strategy says "like BuyNow",
which suggests (F II) to derive (`extend`) the base class or re-use some
functionality:

- point to the cheat strategy and how it partially reusess existing code

The Cheat strategy extends BuyNow, and only overrides the one method where it behaves differently.  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Player/strategy/Cheat.java#L21

- point to a unit test that makes sure the requested acquisition is impossible

This test tests that both cheating players are in the cheaters part of the TournameneResult, which shows that they attempted to acquire invalid connections but couldn't.  
https://github.ccs.neu.edu/CS4500-F21/mark-twain/blob/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2/Trains/Other/UnitTests/UnitTestClasses/TestSingleElimTournamentManager.java#L127-L141

The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.
