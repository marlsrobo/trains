Pair: gallatin

Commit: 3f97f41b49d775cc6325d99a3c0f5f8a6e7aa431

Score: 135/145

Grader: Sindhu

Self Evaluation (30/30)
---------------------
- (30/30) Accuracy 

Please submit the links of the latest commit added before the deadline, because the github master link you have provided broke, since you had made new changes to the repo. 
Make sure to follow this, to avoid losing accuracy points. 

Code Inspection (90/100)
----------------------
- (20/20) for the organization of code with Interface <- Abstract <- 2 concrete implementations for hold-10 and buy-now stratergies.
  - (10/10) for the specific classes 
  - (10/10) for the common buy algorithm factored out 
  
 Note: Is it chooseDrawCards at line 65? It is TurnAction.createDrawCards(); now. Verify this. 
  
- (20/20) for signatures and purpose statements of
  - a `choose-destinations` method 
  - a `choose-action` method
 
Note: Since your chooseDrawCards always return false, What will happen if the player does not have enough colored card to buy a connection?
  
- (20/20) for unit tests of the `choose-destination` for `hold-10` (at least one) 
- (10/20) for unit tests of the `choose-action` for `buy-now`
  - at least one that checks "acquire a connection" 
  - at least one that checks "give me more cards" (missing)

- (20/20) for factoring out
  - lexical order of destinations: sig/purp/unit
  - lexical order of connections: sig/purp/unit 

(0/10) BONUS points for README or interface module/file contains a diagram for organization of code.

Design Inspection (15/15)
--------------------------------------------------
- (5/5) setup first
- (5/5) pick separate second, or explicitly included in setup
- (5/5) take-turn follows

