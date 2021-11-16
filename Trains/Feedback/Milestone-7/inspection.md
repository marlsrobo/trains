Pair: mark-twain

Commit: [`f10d76`](https://github.ccs.neu.edu/CS4500-F21/mark-twain/tree/f10d768adb25a25de0f976ad44c1f8aa18ffd03e) *(Multiple hashes found: `f10d76`, `241b70`, `088f72`. Using `f10d76` instead.)*

Score: 205/290

Grader: Sindhu

## Self Evaluation 20/20

## git log inspection 60/60

You got full points if the answer to "Did you copy code?" appeared to be "no".

## Improved Project Code 125/210

For each of the sections below, criteria aliases and descriptions:

| Criteria Alias         | Description  | Points |                                                    
| :--------------------- | :-------------------- | :----------------------------------------------------------------------------: | 
| A                      | the item in your `todo` file or an explanation that about why it was not in your `todo` file (you already had the functionality prior to this milestone, you fixed an issue "immediately", etc. But you can't get these points if you didn't implement the functionality.)  | 5 | 
| B                      | a link to a git commit (or set of) and/or git diffs or files that contain the functionality | 5 | 
| C                      | quick-check accuracy (1. matches 2. date or fix (2 is relevant for git commits)) | 5 | 
| D                      | quality (see below) | 15 | 

### **Scores**


| Category                                  | A  | B | C | D | Total |                                                   
| :--------------------- | :-------------------- | :----------------------------------------------------------------------------: | :--------: | :---------: | :---------: | 
| Game Map                                   | 5 | 5 | 5 | 15 | 30 |
| Game States                                | 5 | 5 | 5 | 15 | 30 | 
| Strategy                                   | 5 | 5 | 5 | 15 | 30 |
| Referee's Scoring (Connection Points)      | 0 | 0 | 0 |  5 | 5 |
| Referee's Scoring (Destinations Connected) | 0 | 0 | 0 |  5 | 5 |
| Referee's Scoring (Longest Path)           | 0 | 0 | 0 |  5 | 5 |
| Referee's Scoring (Ranking)                | 0 | 0 | 0 |  0 | 0 |

### Game Map Notes & Quality Criteria
- Quality
  - how are connections between two cities represented?
    - example: if you constructed a map with BOS and NYC on it, connected with 4 red segments, how would the data representation express this?
  - is every connection between two cities represented once or twice?
    - example: if you have a 3-blue BOS--NYC connection, does it show up once or twice?
  - does the data representation say how to translate it into a graphic layout?
    - example: how large is the map? Where would NYC show up (in the above example)?

### Game States Notes & Quality Criteria
- Quality
  - a proper data definition including an interpretation for the player game state
  - a purpose statement for the "legality" functionality on player game states and connections
  - two unit tests for the "legality" functionality on states and connections

### Strategy Notes & Quality Criteria
- Quality
  - do the purpose statements of the strategies' methods/functions express how it makes decisions?
  - are common pieces abstracted now?

### Referee Notes & Quality Criteria
- Quality
  - separate method; clear naming and/or good purpose statement; unit tests
    - For each, 5 pts
- Grading notes:
  - You did not mention any TODO reference in the self eval. We at least expect you to say "our code was alright" if you don't have anything to change, same way mentioned for other points above. (-15 * 3) Giving you 5 * 3 for purpose statement of assign points. 
  - You have functions only to calculate total number of segments, destinations and longest path, but their calculation of score is not abstracted out.  (-10 * 3) 
  - ScorePlayers is not a ranking function. (-30)
    
### Bonus 20/20

- 10/10 for quick-check accuracy (1. matches 2: date or fix)
- 10/10 for quality if it is convincing that your "favorite debt removal action" removed a critical technical debt


