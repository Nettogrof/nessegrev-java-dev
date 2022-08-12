![GitHub](https://img.shields.io/github/license/nettogrof/nessegrev-java-dev) ![GitHub issues](https://img.shields.io/github/issues/nettogrof/nessegrev-java-dev) ![GitHub last commit](https://img.shields.io/github/last-commit/nettogrof/nessegrev-java-dev) [![DeepSource](https://deepsource.io/gh/Nettogrof/nessegrev-java-dev.svg/?label=active+issues&show_trend=true)](https://deepsource.io/gh/Nettogrof/nessegrev-java-dev/?ref=repository-badge) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/3f96bc6226a74ad8997fa34c45dbf965)](https://www.codacy.com/gh/Nettogrof/nessegrev-java-dev/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Nettogrof/nessegrev-java-dev&amp;utm_campaign=Badge_Grade)

# Battlesnake Nessegrev snakes

This is the source code  of my current snakes.

Code
----

If you're brave enough and you want to loose your sanity, you may look at the code. Code will be update often to improve comments, and some refactoring. 

Few thing to know to understand this code:
1. Square formula, it get track for the square as an int. Formula square int =  X * 1000 + Y. Example if a food is at ("x" : 3 , "y" : 5)  it's square value is 3005.
2. Part of my code is still based on API v0, so something UP and DOWN are inverted.
3. My code don't use "real" algorithm, example: my MCTS search, does something similar to Monte-Carlo Tree Search, but not exactly.

link to the [JavaDoc](https://nettogrof.github.io/nessegrev-java-dev/)

Release
-------


The goal of the release is to let developers to use them to test their snakes via the [BattleSnake-CLI](https://github.com/BattlesnakeOfficial/rules/tree/master/cli) or with [Mojave](https://github.com/smallsco/mojave).
You won't be able to use those snakes to run on [BattleSnake](https://play.battlesnake.com) because my snakes are "Author" protected. 

## Snake description

(In order of the stupidest snake to the slightly less stupid snakes)

* Basic : A clueless snake that tries to go to the nearest food, only able to avoid wall and snakes' bodies
* FloodFill : This snake use flood fill algorithm to find the best move. It tries to target food and shorter snakes, and avoids bigger snakes.
* Expert : Decent snake using minimax/payoff matrix algorithm only with a MCTS . This snake can play standard, squad, royale, and wrapped.


## Properties files

Each snake has a properties file. You can edit it with any text editor. If you change the file, you need to restart the snake.
Most of the basic properties are self-explanatory, but the `minusbuffer` property is a millisecond property to compensate for the latency. For example, if the timeout is 500ms and the minusbuffer is 150ms, then the snake will use 350ms.
Please take note that sometimes (maybe cause by Java's garbage collection) my snakes may timeout.

## How-to

To run a snake, you need Java installed  (any recent version).
In the command line, type `java -Djava.util.logging.config.file=logging.properties -jar snake.jar <SnakeName>` e.g. `java -Djava.util.logging.config.file=logging.properties -jar snake.jar Expert`

For Expert, you may add memory config, e.g. `java -Djava.util.logging.config.file=logging.properties -Xmx1024m -Xms1024m -jar snake.jar Expert`

The snake will listen on the port number from the `.properties` file.
