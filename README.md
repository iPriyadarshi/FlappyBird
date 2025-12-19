# Flappy Bird (Java Swing)

Small Java Swing recreation of Flappy Bird. Tap space to keep the bird aloft while threading between randomly placed pipes. The game uses a simple timer loop for animation and collision checks.

## How to run
- Requirements: JDK 8+ (tested with desktop Java), a terminal, and `javac`/`java` on `PATH`.
- Compile (from the project root):
	- Windows PowerShell: `javac -d bin -sourcepath src src/*.java`
- Copy assets to the compiled output (needed once after compiling):
	- `Copy-Item src/resources bin/resources -Recurse -Force`
- Run:
	- Windows PowerShell: `java -cp "bin;bin/resources" App`

> If you run from an IDE, ensure `src/resources` (or `bin/resources`) is on the classpath so the image lookups succeed.

## Controls
- Space: flap / jump
- While game over: press Space to restart

## Gameplay details
- Gravity pulls the bird down each frame; space gives an upward impulse.
- Pipes scroll left at a constant speed; score increments when you pass a lower pipe.
- Collisions with pipes or the ground end the run and show the final score.

## Project layout
- `src/App.java` — sets up the main window and starts the panel.
- `src/FlappyBird.java` — game loop, rendering, input, and collision logic.
- `src/resources/` — background, bird, and pipe sprites (copied to `bin/resources` for runtime).
