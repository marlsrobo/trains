Pair: gallatin

Commit: 651892a14d0eb353dfb19b91b4125f81e07b5de7

Score: 105/155

Grader: Mohan Shobana

### Self Evaluation (10/10)

- (10/10) Self Evaluation Accuracy

### Code Inspection (40/85)

#### "determine all connections that can still be acquired"

    You mentioned "the available connections can be calculated via set difference.". 
    Can you point it out in code where you did the same? 
- (0/5) "all connections" properly named
- (0/10) purpose statement 
- (0/10) unit harnesses

#### "decide whether it is legal to acquire"

    Good job checking if connection can be aquired.
    But, you need to point out where you check the player has enough rails and cards to occupy the connections.

- (5/5) "legal acquisition" properly named
- (5/5) "legal acquisition" with purpose statement 
- (0/10) comprehensibility/readability
- (10/20)  one TRUE unit test one FALSE unit test {Only has False Test}

#### "produce a player game state."

- (5/5) "projection" properly named
- (5/5) "projection" with purpose statement
- (10/10) one unit test

### Design Inspection (15/20)

- (5/5) a setup method for handing out the game pieces at the beginning
- (10/10) the play method must allow two distinct kinds of results: acquire-occupy and more-cards.
- (0/5) a separate more method for handing over additional cards

### Image Inspection (40/40)

- (10/10) student's test
- (30/30) Matthias's test

