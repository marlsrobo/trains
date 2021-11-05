DATE: October 13, 2021  
TO:  CS4500 Staff, Northeastern University  
FROM: Laura Calderon and Marley Robinson, Northeastern University  
RE: Design for Trains Game State(s)

### Game State, The Plan

For our plan for the game state, we decided that we should include information about game components (like rails and cards) and information about acquired connections and reached destinations. The important thing is that 1) the referee should have enough information to validate legal moves and that 2) the players should have enough information to make strategic moves. In our plan for the game state, we decided that players and referees should have different access levels of information about the game. 

We also decided that a game state is not "editable" by either a player or a referee. The Game State only keeps track of the pertinent game information and will only include "getters", depending on whether the user is a player or a referee. Players only have access to information about already acquired connections from other players, scores, and their own components (their hand of cards, their number of rails, etc.), while the referee has access to all of the players information (all the statuses of destinations, rails, and cards per player). Players should NOT have access to the information of other players. 

We've included our wishlist for the game state(s) below (with information about signatures and purpose statements) and a list of possible fields we'd need in our Game State class(es).

### GameState

_Interpretation_: a class representing the current state of the game. It contains fields and methods that are 
common to both players and referees

Fields
* gameOver (boolean) : whether the game is over or not
* scores (hashmap of player ids (integer) to score (integer)) : the scores of all the players
* acquiredConnections (hashmap of player id (integer) to Set of Connection) : which players have acquired which connections

Wishlist of Methods
* **isGameOver()** -> boolean : returns gameOver to determine if the game is over
* **getScores()** -> hashmap of player ids (integer) to score (integer): returns scores to get the scores of all the players
* **getAcquiredConnections()** -> hashmap of player id (integer) to Set of Connection: returns acquiredConnections to determine which Connections have been acquired by which players

### PlayerGameState

_Interpretation_: a class representing the current state of the game specifically for players that extends the GameState class

Fields
* cardsInHand (Hashmap of ConnectionColor to integer) : the cards in their hand (number of cards and color of each)
* railsInHand (integer) : the number of rails that a player currently has left
* destinationsStatus (Hashmap of (Set of City) to boolean) : keeps track of the player's destinations and if they have connected them yet or not

Wishlist of Methods
* **getCardsInHand()** -> Hashmap of ConnectionColor (card) to integer : returns cardsInHand
* **getRailsInHand()** -> integer : returns railsInHand
* **getDestinationsStatus()** -> Hashmap of (Set of City) to boolean : returns the status of each destination for this player (i.e. whether each destination has been connected by this player)

### RefereeGameState

_Interpretation_: a class representing the current state of the game specifically for referees that extends the GameState class

Fields
* currentTurn (integer) : the player id of whose turn it currently is in the game
* deck (Hashmap of ConnectionColor to integer) : the deck of cards available to be distributed to players (the number of cards and the color of each card)
* railsPerPlayer (Hashmap of integer (player id) to integer) : the current number of rails left per player (to determine when to start to end the game)
* cardsPerPlayer (Hashmap of integer (player id) to Hashmap of ConnectionColor (card) to integer) : the number and color of cards that each player currently has in their hand
* destinationsStatuses (Hashmap of integer (player id) to hashmap of (Set of City) to boolean) : the destinations' statuses per player (whether they have been connected or not)

Wishlist of Methods
* **getCurrentTurn()** -> integer : returns the player id whose turn it is
* **getDeck()** -> Hashmap of ConnectionColor to integer : returns the current cards left in the deck
* **getRailsPerPlayer()** -> Hashmap of integer (player id) to integer : returns the number of rails left for each player
* **getRails(integer playerID)** -> integer : returns the number of rails left for the given player
* **getCards(integer playerID)** -> Hashmap of ConnectionColor to integer : returns the given player's hand of cards
* **getDestinationsStatuses()** -> Hashmap of integer (player id) to hashmap of (Set of City) to boolean : returns the destinations and their statuses per player
* **getPlayerDestinationsStatuses(integer playerID)** -> Hashmap of (Set of City) to boolean : returns the destinations' statuses of the given player

*ConnectionColor, City, Connection have all been defined previously (see map-design.md)

