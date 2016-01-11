# Introduction

AI Battleground is competition in artificial intelligence, where AI player, aka bots, fight against each other in predefined game. By design game is played in turns, where each player make their action in parallel.

# Installation

* clone repository
* Run maven install: ```mvn install```
* Look over unit test how this server is used
* Look at other repositories implementing concrete game rules ```ai-server-sum``` or ```ai-server-conway```

# Game phases

### Phase 0

Current game state is sent to both players and to game state observers, aka spectators.

### Phase 1

* Players asynchronously and concurrently compute their action, that is game movement. To better understand action notion here are few example:

    * In chess action could be "move king to E4". Difference between chess and out game is players play turn-by-turn instead of
    * In Dota, LOL and similar games your action consist of casting spells, movement commands, attaching commands, etc. Similar to our system all players issue commands in parallel, without waiting for other player to complete their turn.

### Phase 2

In this phase we iterate one game iteration. Depending on specific game rules

# Further work

* Move internal server client handling from threaded model to Asynchronous IO (also known as non-blocking IO).
* Make Dockerfile and Docker container out of server components for easier deployment. It should be simple since only pre-requisits are java and maven, plus maven dependencies.
* Make server support multiple concurrent games at once
* Web-based interface for code submit, instead of hacky git one...I have couple ideas how to construct this whole thing.
