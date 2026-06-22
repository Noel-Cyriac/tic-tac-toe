# ❌ Tic-Tac-Toe Tournament CLI ⭕

A modern, feature-rich, and colorful command-line interface (CLI) Tic-Tac-Toe game written in **Java 21** using **Maven**. This isn't just your standard Tic-Tac-Toe; it features an advanced tournament engine, player profile management with persistent statistics, custom board sizes, rule-based AI opponents, move undo capabilities, and smart early draw detection.

---

## 🌟 Key Features

*   **🎮 Interactive Game Modes:**
    *   **Single-player:** Face off against a computer opponent.
    *   **Two-player:** Play locally with a friend on the same terminal.
*   **🤖 Heuristic-Based AI Opponents:**
    *   **Easy:** Makes random, unpredictable moves.
    *   **Medium:** Implements a rule-based algorithm that automatically claims winning moves, blocks the opponent's winning opportunities, prioritizes the center cell, and falls back to random moves.
*   **📏 Custom Board Sizes:** Play on standard **3x3**, or larger **4x4** and **5x5** grids.
*   **🏆 Tournament System:** Configurable **Best of 1, 3, or 5** matches. Tracks standings and alternates starting players each match to ensure fairness.
*   **↩️ Turn Undo System:** Made a mistake? Type `undo` during your turn to revert. In single-player mode, this rolls back both the computer's and your last moves.
*   **🧠 Early Draw Detection:** Smart evaluation algorithm detects if a match is a logical draw *before* the board is fully filled, saving time when neither player can possibly win.
*   **👤 Player Profiles & Statistics:** Create, view, and delete player profiles. Keep track of:
    *   Matches played
    *   Wins / Losses / Draws
    *   Win Rate (%)
*   **💾 JSON Persistence:** All player statistics (`profiles.json`) and tournament match history (`history.json`) are persistently saved to JSON files using Jackson.
*   **⚙️ Configurable Properties:** Fine-tune default session settings (default game mode, board size, difficulty, tournament length, and database paths) via `src/main/resources/config.properties`.
*   **🎨 Rich Terminal UI:** Double-bordered boxes, color-coded indicators, ANSI-styled output, and emojis bring the game to life in your shell.

---

## 📋 Prerequisites

To run this project, make sure you have the following installed:
- **Java Development Kit (JDK) 21** or higher
- **Apache Maven 3.8+**

---

## 🚀 Quick Start & How to Run

The easiest way to start the game is using the helper bash script provided (which already has executable permissions):

```bash
./play.sh
```

Alternatively, you can compile and execute it using standard Maven commands:

### 1. Compile the Project
```bash
mvn compile
```

### 2. Run the CLI Application
```bash
mvn exec:java -Dexec.mainClass="com.tictactoe.App"
```

---

## 🕹️ How to Play

Upon running the game, you will see a main menu. Navigate the menus by typing the number corresponding to your choice and pressing `[Enter]`.

### Making Moves
During a match, when it is your turn:
*   Enter your coordinates as `row col` (e.g. `2 2` to place your symbol in the center of a 3x3 board).
*   Coordinates are 1-indexed (top-left is `1 1`, bottom-right of a 3x3 is `3 3`).

### Match Controls
You can type special commands at the move prompt:
*   `undo` - Undo the last move.
*   `exit` or `quit` - Abandon the current match and return to the main menu.

---

## ⚙️ Configuration

The default game settings are loaded from `src/main/resources/config.properties`. You can modify this file to adjust the startup defaults:

```properties
# Tic-Tac-Toe Game Configuration Settings
default.board.size=3
default.difficulty=MEDIUM
default.game.mode=SINGLE_PLAYER
default.tournament.length=3
profiles.file.path=profiles.json
history.file.path=history.json
```

*   `default.board.size`: Standard size of the grid (`3`, `4`, or `5`).
*   `default.difficulty`: Computer opponent behavior (`EASY` or `MEDIUM`).
*   `default.game.mode`: Game mode on startup (`SINGLE_PLAYER` or `TWO_PLAYER`).
*   `default.tournament.length`: Default match structure (`1`, `3`, or `5`).
*   `profiles.file.path` & `history.file.path`: JSON database file destinations.

---

## 🧪 Running Unit Tests

The codebase includes an automated JUnit 5 test suite to verify board states, AI logic, and game engine flows. To run all tests, execute:

```bash
mvn test
```

---

## 📂 Project Structure

```text
├── play.sh                   # Script to build and run the app
├── pom.xml                   # Maven project configuration file
├── profiles.json             # Persistent storage for player profiles
├── history.json              # Persistent storage for tournament history
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── tictactoe
    │   │           ├── App.java                      # Main CLI controller flow
    │   │           ├── Board.java                    # Grid state, win logic, draw checks
    │   │           ├── ComputerPlayer.java           # AI logic (Easy/Medium algorithms)
    │   │           ├── ConfigManager.java            # Loader for configuration properties
    │   │           ├── ConsoleColor.java             # Terminal color formatting constants
    │   │           ├── Difficulty.java               # Enum for AI Difficulty levels
    │   │           ├── GameEngine.java               # Active game loop and turn history
    │   │           ├── GameMode.java                 # Enum for Game Modes
    │   │           ├── HumanPlayer.java              # Interactive player move capture
    │   │           ├── MatchExitRequestedException.java
    │   │           ├── Move.java                     # Record holding grid coordinates
    │   │           ├── PersistenceManager.java       # Read/Write JSON databases
    │   │           ├── Player.java                   # Abstract Player class
    │   │           ├── PlayerProfile.java            # Holds player statistics
    │   │           ├── Symbol.java                   # Grid symbols (X, O, EMPTY)
    │   │           ├── TournamentResult.java         # Data model for tournament logs
    │   │           └── UndoRequestedException.java
    │   └── resources
    │       └── config.properties                     # Default game properties
    └── test
        └── java
            └── com
                └── tictactoe
                    ├── BoardTest.java                # Board unit tests
                    ├── ComputerPlayerTest.java       # AI behavior validation
                    └── GameEngineTest.java           # Game controller & Undo tests
```
