## Self-Evaluation Form for Milestone 5

Indicate below each bullet which file/unit takes care of each task:

1. the general interface/type/signatures for strategies

https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/IStrategy.java#:~:text=public%20interface%20IStrategy,%7D

2. the common container/abstract class (see Fundamentals II)  for the buy algorithm; in an FP approach, the common algoritnm

https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/AStrategy.java#:~:text=public%20abstract%20class%20AStrategy%20implements%20IStrategy%20%7B

3. the method/function for setting-up decisions, plus unit tests 
- Method in abstract class
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/AStrategy.java#:~:text=%40Override-,public%20Set%3CDestination%3E%20chooseDestinations(,%7D,-/**
- BuyNow strategy implementation of protected method
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/BuyNow.java#:~:text=protected%20List%3CDestination,%7D
- Hold10 strategy implementation of protected method
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/Hold10.java#:~:text=protected%20List%3CDestination,%7D

- Unit tests:
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/Tests/UnitTestClasses/TestStrategy.java#:~:text=public%20void%20testChooseDestinations,%7D

4. the method/function for take-turn decisions, plus unit tests 

- Method in abstract class
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/AStrategy.java#:~:text=%40Override-,public%20TurnAction%20takeTurn(IPlayerGameState%20currentPlayerGameState)%20%7B,%7D,-/**

- BuyNow strategy implementation of protected methods
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/BuyNow.java#:~:text=protected%20IRailConnection%20getPreferredConnection,%7D
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/BuyNow.java#:~:text=protected%20boolean%20chooseDrawCards,%7D

- Hold10 strategy implementation of protected method
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/Hold10.java#:~:text=protected%20IRailConnection%20getPreferredConnection,%7D
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Player/Hold10.java#:~:text=protected%20boolean%20chooseDrawCards,%7D

- Unit tests
For choosing connections: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/Tests/UnitTestClasses/TestStrategy.java#:~:text=public%20void%20testChooseRailConnection(AStrategy,%7D
For choosing to draw cards: https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/Tests/UnitTestClasses/TestStrategy.java#:~:text=public%20void%20testChooseCards,%7D

5. the methods/functions for lexical-order comparisions of destinations, plus unit tests
- Common utility method used:
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Common/utils/ComparatorUtils.java#:~:text=public%20static%20int%20LexicographicCompare,%7D
- Specific call in Destination data definition:
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Common/map/Destination.java#:~:text=public%20int%20compareTo,%7D

- Unit tests (this method and a few surrounding it):
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/Tests/UnitTestClasses/TestComparatorUtils.java#:~:text=public%20void%20TestLexicographicCompareSecondDifferent,%7D
- Unit test explicitly for Destination comparison:
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/Tests/UnitTestClasses/TestDestination.java#:~:text=public%20void%20TestCompare,%7D

6. the methods/functions for lexical-order comparisions of connections, plus unit tests 
- Common utility method used:
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Common/utils/ComparatorUtils.java#:~:text=*/-,public%20static%20int%20railComparator(,%7D,-%7D

- Unit tests (this method and a few surrounding it):
https://github.ccs.neu.edu/CS4500-F21/gallatin/blob/master/Trains/Other/Tests/UnitTestClasses/TestComparatorUtils.java#:~:text=public%20void%20TestRailComparatorSecondDifferent,%7D


The ideal feedback for each of these three points is a GitHub
perma-link to the range of lines in a specific file or a collection of
files.

A lesser alternative is to specify paths to files and, if files are
longer than a laptop screen, positions within files are appropriate
responses.

You may wish to add a sentence that explains how you think the
specified code snippets answer the request.

If you did *not* realize these pieces of functionality, say so.
