Chris McCarty
Ben Blumenthal

This is Project Obelisk, our final project for Intro to Unix. The game can be played by either running she bash script named "start.sh", or entering the following command in your terminal:

	$ java -jar project_obelisk.jar

If this causes errors, then your version of java is not 
current or you are in the wrong directory.


The game itself is a 2D top-down hack and slash style game, with the player's goal being the collection of points. We used the Slick2D library, which is an open source 2D game library for java. (http://www.slick2d.org/) With it, we developed everything about our game. Everything from the sprites to the mechanics to the animations was done by us for this class. 

We had thought initially that this project was due later than it really was, so we were forced to scrap several features we wanted to implement like puzzles, additional weapons, and an online mode. Also, as we found out, the complexity and workload of this type of game increase nearly exponentially. The fact that we had to rush to polish off the game and neatly axe partially implemented features made for some messy source code. We sacrificed style for functionality in this case, especially because the majority of people who ever see this game will just run the jar and be done with it.

We put quite a bit of time into this game, and hope that you'll like it. The source code is in the src directory. Let us know if you have any questions or comments.

Chris McCarty: mccarty.exe@gmail.com 

*NOTE* any resources have static paths relative to the jar's layout, not a file tree in an IDE. If you plan on running it from an IDE, be sure to update the paths accordingly.
