Pair: mark-twain

Commit: [`e35cc5`](https://github.ccs.neu.edu/CS4500-F21/mark-twain/tree/e35cc5b2e34770610fcc2de7e89f7c1127cb32e2) 

Score: 145/155

Grader: Sindhu

Score Distribution:
-------------------------------------------------
### Self Evaluation (20/20)
- (/20) Accuracy
--------------------------------------------------
### Code Inspection (105/115)

#### Manager Inspection: Total(90/100)
- manager performs five totally distinct tasks:(existence of separate methods/functions and well-chosen names and/or purpose statements):
    - (10/10) inform players of the beginning of the tournament, retrieve maps
    - check that there are maps with enough destinations and pick one of those for the games
      - (10/10) picks the "good" map 
      - (0/10) the same predicate should be used in both the manager and the referee for checking enough destinations (not copied code)
    - (/10) allocating players to a bunch of games per round
    - running the tournament, separate two functions
       - (15/15) run a round of games
       - (15/15) run all rounds
    - (10/10) inform survining players at the very end whether they won the tournament
- Testing the manager:
    - (10/10) testing a single game 
    - (0/10) testing the allocation of players to games per round (no test case found for this)

#### Cheat Strategy Inspection : Total(15/15)
- (10/10) proper design (derive (extend) the BuyNow class and override the turn method) 
- (5/5)  new turn decision maker should come with a unit test that makes sure that the requested acquisition is not on the map

-------------------------------------------------

### Design Inspection (20/20)
 - (5/5) diagrams that mention for the exact same scenarios as in logical interactions and logical interactions 2 --> detailed logical diagrams as in logical          interactions 1 and 2 were expected
 - (5/5) JSON format definitions, for each call and return in the diagrams 
 - (10/10) a "helpful" English explanation 
