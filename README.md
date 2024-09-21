# Conway's Game of Life

## Overview
This project implements Conway's Game of Life, a cellular automaton devised by mathematician John Conway. The simulation runs in a graphical user interface (GUI) created using Java Swing. Users can edit the initial state, run the simulation, randomize the grid, and save/load the game state.

## Design
- Java Swing for GUI development
- Double buffering for smoothness of application

## Requirements
- Java Development Kit (JDK) 8 or higher
- An IDE or text editor for Java development (e.g., IntelliJ IDEA, Eclipse, or NetBeans)

## How to Run
1. Clone the Repository or download the java file:
   ```
   git clone https://github.com/yourusername/conways-game-of-life.git
   cd conways-game-of-life
   ```
2. Compile the Code, ensure you have the JDK installed and set up. Compile the Java file using:
``` javac Conways_Life.java ```
3. Run the Application: Execute the compiled class:
``` java Conways_Life ```

## Controls
- Edit: Toggle between editing mode (to modify cell states) and simulation mode.
- Random: Randomly populate the grid with live cells (25% chance).
- Run: Start/stop the simulation.
- Save: Save the current state to a file.
- Load: Load a game state from a file.

## File Format
The saved game state is stored as a binary string in a text file:
  - Each cell is represented as 1 (alive) or 0 (dead).
  - The format consists of 40x40 grid








