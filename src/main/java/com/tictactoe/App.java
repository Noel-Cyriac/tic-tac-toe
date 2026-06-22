package com.tictactoe;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private final ConfigManager configManager;
    private final PersistenceManager persistenceManager;

    // Active session configuration overrides
    private int currentBoardSize;
    private Difficulty currentDifficulty;
    private GameMode currentGameMode;
    private int currentTournamentLength;

    public App() {
        this.configManager = new ConfigManager();
        this.persistenceManager = new PersistenceManager(
                configManager.getProfilesFilePath(),
                configManager.getHistoryFilePath()
        );
        
        // Initialize overrides from config file
        this.currentBoardSize = configManager.getDefaultBoardSize();
        this.currentDifficulty = configManager.getDefaultDifficulty();
        this.currentGameMode = configManager.getDefaultGameMode();
        this.currentTournamentLength = configManager.getDefaultTournamentLength();
    }

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    startTournamentFlow(scanner);
                    break;
                case "2":
                    manageProfilesFlow(scanner);
                    break;
                case "3":
                    viewHistoryFlow();
                    promptEnterToContinue(scanner);
                    break;
                case "4":
                    configureSettingsFlow(scanner);
                    break;
                case "5":
                    clearScreen();
                    System.out.println("\n" + ConsoleColor.GREEN_BOLD + "👋 Thank you for playing Tic-Tac-Toe! Goodbye." + ConsoleColor.RESET + "\n");
                    running = false;
                    break;
                default:
                    System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid selection. Please choose an option from 1 to 5." + ConsoleColor.RESET);
                    promptEnterToContinue(scanner);
            }
        }
    }

    private void printMainMenu() {
        clearScreen();
        List<String> banner = List.of(
            "TIC-TAC-TOE TOURNAMENT CLI",
            "---",
            "1. Start Tic-Tac-Toe Tournament",
            "2. Manage Player Profiles",
            "3. View Tournament History & Statistics",
            "4. Configure Game Settings",
            "5. Exit"
        );
        printBox(banner, ConsoleColor.CYAN_BOLD, true);
        System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Select an option (1-5): " + ConsoleColor.RESET);
    }

    private void startTournamentFlow(Scanner scanner) {
        // 1. Select Game Mode
        GameMode mode = currentGameMode;
        while (true) {
            clearScreen();
            List<String> modeMenu = List.of(
                "SELECT GAME MODE",
                "---",
                "1. Single-player (against Computer)",
                "2. Two-player"
            );
            printBox(modeMenu, ConsoleColor.CYAN_BOLD, true);
            System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Choice [default: " + currentGameMode + "]: " + ConsoleColor.RESET);
            String modeInput = scanner.nextLine().trim();
            if (modeInput.isEmpty()) {
                break;
            }
            if (modeInput.equals("1")) {
                mode = GameMode.SINGLE_PLAYER;
                break;
            } else if (modeInput.equals("2")) {
                mode = GameMode.TWO_PLAYER;
                break;
            } else {
                System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid selection." + ConsoleColor.RESET);
                promptEnterToContinue(scanner);
            }
        }

        // 2. Select Player Profiles
        PlayerProfile p1Profile = selectOrCreateProfile(scanner, "Player 1");
        if (p1Profile == null) {
            return;
        }
        PlayerProfile p2Profile = null;
        if (mode == GameMode.TWO_PLAYER) {
            while (true) {
                p2Profile = selectOrCreateProfile(scanner, "Player 2");
                if (p2Profile == null) {
                    return;
                }
                if (p2Profile.getName().equalsIgnoreCase(p1Profile.getName())) {
                    System.out.println(ConsoleColor.RED_BOLD + "⚠ Player 2 cannot select the same profile as Player 1. Choose a different profile." + ConsoleColor.RESET);
                    promptEnterToContinue(scanner);
                } else {
                    break;
                }
            }
        }

        // 3. Confirm/Select Board Size
        int boardSize = currentBoardSize;
        while (true) {
            clearScreen();
            List<String> sizeBox = List.of(
                "SELECT BOARD SIZE",
                "---",
                "Available sizes: 3x3, 4x4, 5x5"
            );
            printBox(sizeBox, ConsoleColor.CYAN_BOLD, true);
            System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Enter Board Size (3, 4, or 5) [default: " + currentBoardSize + "]: " + ConsoleColor.RESET);
            String sizeInput = scanner.nextLine().trim();
            if (sizeInput.isEmpty()) {
                break;
            }
            try {
                int parsed = Integer.parseInt(sizeInput);
                if (parsed == 3 || parsed == 4 || parsed == 5) {
                    boardSize = parsed;
                    break;
                } else {
                    System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid board size." + ConsoleColor.RESET);
                    promptEnterToContinue(scanner);
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid input. Enter a number." + ConsoleColor.RESET);
                promptEnterToContinue(scanner);
            }
        }

        // 4. Confirm/Select Difficulty (if single player)
        Difficulty aiDiff = currentDifficulty;
        if (mode == GameMode.SINGLE_PLAYER) {
            while (true) {
                clearScreen();
                List<String> diffBox = List.of(
                    "SELECT COMPUTER DIFFICULTY",
                    "---",
                    "1. Easy (Random moves)",
                    "2. Medium (Blocks winning moves & plays center)"
                );
                printBox(diffBox, ConsoleColor.CYAN_BOLD, true);
                System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Choice [default: " + currentDifficulty + "]: " + ConsoleColor.RESET);
                String diffInput = scanner.nextLine().trim();
                if (diffInput.isEmpty()) {
                    break;
                }
                if (diffInput.equals("1")) {
                    aiDiff = Difficulty.EASY;
                    break;
                } else if (diffInput.equals("2")) {
                    aiDiff = Difficulty.MEDIUM;
                    break;
                } else {
                    System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid selection." + ConsoleColor.RESET);
                    promptEnterToContinue(scanner);
                }
            }
        }

        // 5. Select Tournament Length (Best of 1, 3, 5)
        int tournamentLength = currentTournamentLength;
        while (true) {
            clearScreen();
            List<String> lenBox = List.of(
                "SELECT TOURNAMENT TYPE",
                "---",
                "Options: Best of 1, 3, or 5 matches"
            );
            printBox(lenBox, ConsoleColor.CYAN_BOLD, true);
            System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Best of (1, 3, or 5) [default: " + currentTournamentLength + "]: " + ConsoleColor.RESET);
            String lengthInput = scanner.nextLine().trim();
            if (lengthInput.isEmpty()) {
                break;
            }
            try {
                int parsed = Integer.parseInt(lengthInput);
                if (parsed == 1 || parsed == 3 || parsed == 5) {
                    tournamentLength = parsed;
                    break;
                } else {
                    System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid length." + ConsoleColor.RESET);
                    promptEnterToContinue(scanner);
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid input. Enter a number." + ConsoleColor.RESET);
                promptEnterToContinue(scanner);
            }
        }

        // Instantiate Players
        Player p1 = new HumanPlayer(p1Profile.getName(), Symbol.X, scanner);
        Player p2;
        if (mode == GameMode.SINGLE_PLAYER) {
            p2 = new ComputerPlayer(Symbol.O, aiDiff);
        } else {
            p2 = new HumanPlayer(p2Profile.getName(), Symbol.O, scanner);
        }

        // Target wins required to secure tournament
        int targetWins = (tournamentLength / 2) + 1;
        List<String> initiationBoxLines = List.of(
            ConsoleColor.GREEN_BOLD_BRIGHT + "TOURNAMENT INITIATED" + ConsoleColor.RESET,
            p1.getName() + " (X)  vs  " + p2.getName() + " (O)",
            "Mode: Best of " + tournamentLength + " (First to " + targetWins + " wins)"
        );
        System.out.println();
        printBox(initiationBoxLines, ConsoleColor.CYAN_BOLD, true);

        int p1Wins = 0;
        int p2Wins = 0;
        int draws = 0;
        int matchCount = 0;

        try {
            while (p1Wins < targetWins && p2Wins < targetWins && matchCount < tournamentLength) {
                matchCount++;
                List<String> matchBoxLines = List.of(
                    String.format("MATCH %d of %d", matchCount, tournamentLength),
                    String.format("Standings: %s [%d] - %s [%d] (%d Draws)", p1.getName(), p1Wins, p2.getName(), p2Wins, draws)
                );
                System.out.println();
                printBox(matchBoxLines, ConsoleColor.YELLOW_BOLD, false);

                // Alternate starting player
                Player startingPlayer = (matchCount % 2 == 1) ? p1 : p2;
                System.out.println("\n📢 " + ConsoleColor.WHITE_BOLD + startingPlayer.getName() + ConsoleColor.RESET + " starts Match " + matchCount + ".");

                Board board = new Board(boardSize);
                GameEngine engine = new GameEngine(board, p1, p2, startingPlayer);
                Player matchWinner = engine.play();

                // Record match result
                if (matchWinner == p1) {
                    p1Wins++;
                    p1Profile.recordWin();
                    if (mode == GameMode.TWO_PLAYER) {
                        p2Profile.recordLoss();
                    }
                } else if (matchWinner == p2) {
                    p2Wins++;
                    if (mode == GameMode.TWO_PLAYER) {
                        p2Profile.recordWin();
                    }
                    p1Profile.recordLoss();
                } else {
                    draws++;
                    p1Profile.recordDraw();
                    if (mode == GameMode.TWO_PLAYER) {
                        p2Profile.recordDraw();
                    }
                }

                // Save player profiles stats immediately after each game
                saveProfileUpdates(p1Profile, p2Profile);
            }
        } catch (MatchExitRequestedException e) {
            System.out.println("\n" + ConsoleColor.RED_BOLD + "⚠ Match exited mid-game. Tournament cancelled." + ConsoleColor.RESET);
            promptEnterToContinue(scanner);
            return;
        }

        // Determine overall tournament winner
        String tournamentWinnerName;
        List<String> winnerBoxLines;
        if (p1Wins > p2Wins) {
            tournamentWinnerName = p1.getName();
            winnerBoxLines = List.of(
                ConsoleColor.GREEN_BOLD_BRIGHT + "TOURNAMENT CHAMPION" + ConsoleColor.RESET,
                p1.getName() + " wins the tournament (" + p1Wins + "-" + p2Wins + ")!"
            );
        } else if (p2Wins > p1Wins) {
            tournamentWinnerName = p2.getName();
            winnerBoxLines = List.of(
                ConsoleColor.GREEN_BOLD_BRIGHT + "TOURNAMENT CHAMPION" + ConsoleColor.RESET,
                p2.getName() + " wins the tournament (" + p2Wins + "-" + p1Wins + ")!"
            );
        } else {
            tournamentWinnerName = "Draw";
            winnerBoxLines = List.of(
                ConsoleColor.YELLOW_BOLD_BRIGHT + "TOURNAMENT RESULT" + ConsoleColor.RESET,
                "The tournament ended in a draw (" + p1Wins + "-" + p2Wins + ")!"
            );
        }
        System.out.println();
        if (!tournamentWinnerName.equals("Draw")) {
            System.out.println("🏆 " + ConsoleColor.GREEN_BOLD_BRIGHT + "CONGRATULATIONS!" + ConsoleColor.RESET + " 🏆");
        } else {
            System.out.println("🤝 " + ConsoleColor.YELLOW_BOLD_BRIGHT + "WELL PLAYED!" + ConsoleColor.RESET + " 🤝");
        }
        printBox(winnerBoxLines, ConsoleColor.CYAN_BOLD, true);

        // Save tournament result to history
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        TournamentResult result = new TournamentResult(
                timestamp,
                p1.getName(),
                p2.getName(),
                p1Wins,
                p2Wins,
                draws,
                tournamentWinnerName
        );
        persistenceManager.saveTournamentResult(result);
        System.out.println(ConsoleColor.BLACK_BRIGHT + "Tournament results saved to history." + ConsoleColor.RESET);
    }

    private PlayerProfile selectOrCreateProfile(Scanner scanner, String role) {
        while (true) {
            clearScreen();
            List<PlayerProfile> profiles = persistenceManager.loadProfiles();
            
            if (profiles.isEmpty()) {
                System.out.println("No player profiles found. Creating a new one.");
                promptEnterToContinue(scanner);
                return createProfile(scanner);
            }

            List<String> boxLines = new ArrayList<>();
            boxLines.add("SELECT PROFILE FOR " + role.toUpperCase());
            boxLines.add("---");
            for (int i = 0; i < profiles.size(); i++) {
                boxLines.add((i + 1) + ". " + profiles.get(i).getName());
            }
            boxLines.add((profiles.size() + 1) + ". [Create a new profile]");
            boxLines.add((profiles.size() + 2) + ". [Cancel / Return to Main Menu]");

            printBox(boxLines, ConsoleColor.CYAN_BOLD, true);
            System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Select profile (1-" + (profiles.size() + 2) + "): " + ConsoleColor.RESET);
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= profiles.size()) {
                    return profiles.get(choice - 1);
                } else if (choice == profiles.size() + 1) {
                    PlayerProfile newProfile = createProfile(scanner);
                    if (newProfile != null) {
                        return newProfile;
                    }
                } else if (choice == profiles.size() + 2) {
                    return null;
                } else {
                    System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid selection. Choose a valid index." + ConsoleColor.RESET);
                    promptEnterToContinue(scanner);
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid input. Please enter a number." + ConsoleColor.RESET);
                promptEnterToContinue(scanner);
            }
        }
    }

    private PlayerProfile createProfile(Scanner scanner) {
        while (true) {
            clearScreen();
            List<String> boxLines = List.of(
                "CREATE NEW PLAYER PROFILE",
                "---",
                "Please enter a unique name.",
                "Or enter 'exit' to cancel."
            );
            printBox(boxLines, ConsoleColor.CYAN_BOLD, true);
            System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Enter profile name: " + ConsoleColor.RESET);
            String name = scanner.nextLine().trim();
            if (name.equalsIgnoreCase("exit")) {
                System.out.println(ConsoleColor.YELLOW + "\nProfile creation cancelled." + ConsoleColor.RESET);
                promptEnterToContinue(scanner);
                return null;
            }
            if (name.isEmpty()) {
                System.out.println(ConsoleColor.RED_BOLD + "⚠ Profile name cannot be empty." + ConsoleColor.RESET);
                promptEnterToContinue(scanner);
                continue;
            }
            if (name.equalsIgnoreCase("computer") || name.startsWith("Computer (")) {
                System.out.println(ConsoleColor.RED_BOLD + "⚠ Name cannot be reserved for the Computer. Choose another name." + ConsoleColor.RESET);
                promptEnterToContinue(scanner);
                continue;
            }
            
            List<PlayerProfile> profiles = persistenceManager.loadProfiles();
            boolean exists = false;
            for (PlayerProfile p : profiles) {
                if (p.getName().equalsIgnoreCase(name)) {
                    exists = true;
                    break;
                }
            }
            
            if (exists) {
                System.out.println(ConsoleColor.RED_BOLD + "⚠ Profile with name '" + name + "' already exists. Choose a different name." + ConsoleColor.RESET);
                promptEnterToContinue(scanner);
                continue;
            }

            PlayerProfile newProfile = new PlayerProfile(name);
            profiles.add(newProfile);
            persistenceManager.saveProfiles(profiles);
            System.out.println(ConsoleColor.GREEN_BOLD + "\n✔ Profile '" + name + "' created successfully!" + ConsoleColor.RESET);
            promptEnterToContinue(scanner);
            return newProfile;
        }
    }

    private void saveProfileUpdates(PlayerProfile p1, PlayerProfile p2) {
        List<PlayerProfile> profiles = persistenceManager.loadProfiles();
        
        // Update player 1
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getName().equalsIgnoreCase(p1.getName())) {
                profiles.set(i, p1);
                break;
            }
        }

        // Update player 2 (if human)
        if (p2 != null) {
            for (int i = 0; i < profiles.size(); i++) {
                if (profiles.get(i).getName().equalsIgnoreCase(p2.getName())) {
                    profiles.set(i, p2);
                    break;
                }
            }
        }

        persistenceManager.saveProfiles(profiles);
    }

    private void manageProfilesFlow(Scanner scanner) {
        boolean back = false;
        while (!back) {
            clearScreen();
            List<String> menuLines = List.of(
                "PROFILE MANAGEMENT",
                "---",
                "1. List All Profiles",
                "2. Create New Profile",
                "3. View Profile Statistics",
                "4. Delete Player Profile",
                "5. Back to Main Menu"
            );
            printBox(menuLines, ConsoleColor.PURPLE_BOLD, true);
            System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Choice (1-5): " + ConsoleColor.RESET);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    clearScreen();
                    List<PlayerProfile> profiles = persistenceManager.loadProfiles();
                    List<String> listLines = new ArrayList<>();
                    listLines.add("ACTIVE PLAYER PROFILES");
                    listLines.add("---");
                    if (profiles.isEmpty()) {
                        listLines.add("No player profiles found.");
                    } else {
                        for (PlayerProfile p : profiles) {
                            listLines.add("• " + p.toString());
                        }
                    }
                    printBox(listLines, ConsoleColor.CYAN_BOLD, true);
                    promptEnterToContinue(scanner);
                    break;
                case "2":
                    createProfile(scanner);
                    break;
                case "3":
                    viewProfileStatsFlow(scanner);
                    break;
                case "4":
                    deleteProfileFlow(scanner);
                    break;
                case "5":
                    back = true;
                    break;
                default:
                    System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid choice. Enter 1-5." + ConsoleColor.RESET);
                    promptEnterToContinue(scanner);
            }
        }
    }

    private void viewProfileStatsFlow(Scanner scanner) {
        List<PlayerProfile> profiles = persistenceManager.loadProfiles();
        if (profiles.isEmpty()) {
            System.out.println(ConsoleColor.YELLOW + "No profiles available." + ConsoleColor.RESET);
            promptEnterToContinue(scanner);
            return;
        }
        
        clearScreen();
        List<String> selectLines = new ArrayList<>();
        selectLines.add("SELECT PROFILE TO VIEW DETAILS");
        selectLines.add("---");
        for (int i = 0; i < profiles.size(); i++) {
            selectLines.add((i + 1) + ". " + profiles.get(i).getName());
        }
        printBox(selectLines, ConsoleColor.CYAN_BOLD, true);
        System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Select index (1-" + profiles.size() + "): " + ConsoleColor.RESET);
        String input = scanner.nextLine().trim();
        try {
            int idx = Integer.parseInt(input) - 1;
            if (idx >= 0 && idx < profiles.size()) {
                PlayerProfile profile = profiles.get(idx);
                double winRate = 0.0;
                if (profile.getMatchesPlayed() > 0) {
                    winRate = (double) profile.getWins() / profile.getMatchesPlayed() * 100;
                }
                List<String> statsLines = List.of(
                    ConsoleColor.YELLOW_BOLD_BRIGHT + "STATISTICS FOR: " + profile.getName() + ConsoleColor.RESET,
                    "---",
                    "Matches Played : " + profile.getMatchesPlayed(),
                    ConsoleColor.GREEN_BOLD + "Wins           : " + profile.getWins() + ConsoleColor.RESET,
                    ConsoleColor.RED_BOLD + "Losses         : " + profile.getLosses() + ConsoleColor.RESET,
                    ConsoleColor.YELLOW_BOLD + "Draws          : " + profile.getDraws() + ConsoleColor.RESET,
                    String.format("Win Rate       : %.2f%%", winRate)
                );
                clearScreen();
                printBox(statsLines, ConsoleColor.CYAN_BOLD, true);
            } else {
                System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid choice." + ConsoleColor.RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid input." + ConsoleColor.RESET);
        }
        promptEnterToContinue(scanner);
    }

    private void deleteProfileFlow(Scanner scanner) {
        List<PlayerProfile> profiles = persistenceManager.loadProfiles();
        if (profiles.isEmpty()) {
            System.out.println(ConsoleColor.YELLOW + "No profiles available to delete." + ConsoleColor.RESET);
            promptEnterToContinue(scanner);
            return;
        }

        clearScreen();
        List<String> selectLines = new ArrayList<>();
        selectLines.add("DELETE PLAYER PROFILE");
        selectLines.add("---");
        for (int i = 0; i < profiles.size(); i++) {
            selectLines.add((i + 1) + ". " + profiles.get(i).getName());
        }
        selectLines.add((profiles.size() + 1) + ". [Cancel]");
        printBox(selectLines, ConsoleColor.RED_BOLD, true);
        System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Select profile to delete (1-" + (profiles.size() + 1) + "): " + ConsoleColor.RESET);
        String input = scanner.nextLine().trim();
        try {
            int idx = Integer.parseInt(input) - 1;
            if (idx >= 0 && idx < profiles.size()) {
                PlayerProfile profile = profiles.get(idx);
                System.out.println(ConsoleColor.RED_BOLD + "\n⚠ WARNING: Deleting '" + profile.getName() 
                    + "' will permanently erase all their matches, wins, losses, and draws stats!" + ConsoleColor.RESET);
                System.out.print(ConsoleColor.YELLOW_BOLD + "Are you sure you want to delete this profile? (y/n): " + ConsoleColor.RESET);
                String confirm = scanner.nextLine().trim();
                if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
                    profiles.remove(idx);
                    persistenceManager.saveProfiles(profiles);
                    System.out.println(ConsoleColor.GREEN_BOLD + "\n✔ Profile '" + profile.getName() + "' deleted successfully." + ConsoleColor.RESET);
                } else {
                    System.out.println(ConsoleColor.YELLOW + "\nDeletion cancelled." + ConsoleColor.RESET);
                }
                promptEnterToContinue(scanner);
            } else if (idx == profiles.size()) {
                System.out.println(ConsoleColor.YELLOW + "\nDeletion cancelled." + ConsoleColor.RESET);
                promptEnterToContinue(scanner);
            } else {
                System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid selection." + ConsoleColor.RESET);
                promptEnterToContinue(scanner);
            }
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid input. Please enter a number." + ConsoleColor.RESET);
            promptEnterToContinue(scanner);
        }
    }

    private void viewHistoryFlow() {
        clearScreen();
        List<TournamentResult> results = persistenceManager.loadHistory();
        List<String> historyLines = new ArrayList<>();
        historyLines.add("TOURNAMENT HISTORY");
        historyLines.add("---");
        if (results.isEmpty()) {
            historyLines.add("No history found. Play some matches first!");
        } else {
            for (TournamentResult r : results) {
                historyLines.add("• " + r.toString());
            }
        }
        printBox(historyLines, ConsoleColor.PURPLE_BOLD, true);
    }

    private void configureSettingsFlow(Scanner scanner) {
        boolean back = false;
        while (!back) {
            clearScreen();
            List<String> settingsLines = List.of(
                "CONFIGURE DEFAULT SESSION SETTINGS",
                "Current Defaults:",
                "  Board Size          : " + currentBoardSize + "x" + currentBoardSize,
                "  Game Mode           : " + currentGameMode,
                "  Computer Difficulty : " + currentDifficulty,
                "  Tournament Length   : Best of " + currentTournamentLength,
                "---",
                "1. Change Default Board Size",
                "2. Change Default Game Mode",
                "3. Change Default Computer Difficulty",
                "4. Change Default Tournament Length",
                "5. Back to Main Menu"
            );
            printBox(settingsLines, ConsoleColor.PURPLE_BOLD, true);
            System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Choice (1-5): " + ConsoleColor.RESET);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    clearScreen();
                    List<String> szBox = List.of(
                        "CHANGE DEFAULT BOARD SIZE",
                        "---",
                        "Options: 3, 4, or 5"
                    );
                    printBox(szBox, ConsoleColor.CYAN_BOLD, true);
                    System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Enter Board Size: " + ConsoleColor.RESET);
                    String sz = scanner.nextLine().trim();
                    if (sz.equals("3") || sz.equals("4") || sz.equals("5")) {
                        currentBoardSize = Integer.parseInt(sz);
                        System.out.println(ConsoleColor.GREEN_BOLD + "\n✔ Default Board Size updated to " + currentBoardSize + ConsoleColor.RESET);
                    } else {
                        System.out.println(ConsoleColor.RED_BOLD + "\n⚠ Invalid size." + ConsoleColor.RESET);
                    }
                    promptEnterToContinue(scanner);
                    break;
                case "2":
                    clearScreen();
                    List<String> gmBox = List.of(
                        "CHANGE DEFAULT GAME MODE",
                        "---",
                        "1. Single-player",
                        "2. Two-player"
                    );
                    printBox(gmBox, ConsoleColor.CYAN_BOLD, true);
                    System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Choice: " + ConsoleColor.RESET);
                    String gm = scanner.nextLine().trim();
                    if (gm.equals("1")) {
                        currentGameMode = GameMode.SINGLE_PLAYER;
                        System.out.println(ConsoleColor.GREEN_BOLD + "\n✔ Default Game Mode updated to SINGLE_PLAYER" + ConsoleColor.RESET);
                    } else if (gm.equals("2")) {
                        currentGameMode = GameMode.TWO_PLAYER;
                        System.out.println(ConsoleColor.GREEN_BOLD + "\n✔ Default Game Mode updated to TWO_PLAYER" + ConsoleColor.RESET);
                    } else {
                        System.out.println(ConsoleColor.RED_BOLD + "\n⚠ Invalid selection." + ConsoleColor.RESET);
                    }
                    promptEnterToContinue(scanner);
                    break;
                case "3":
                    clearScreen();
                    List<String> diffBox = List.of(
                        "CHANGE DEFAULT COMPUTER DIFFICULTY",
                        "---",
                        "1. Easy",
                        "2. Medium"
                    );
                    printBox(diffBox, ConsoleColor.CYAN_BOLD, true);
                    System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Choice: " + ConsoleColor.RESET);
                    String diff = scanner.nextLine().trim();
                    if (diff.equals("1")) {
                        currentDifficulty = Difficulty.EASY;
                        System.out.println(ConsoleColor.GREEN_BOLD + "\n✔ Default Computer Difficulty updated to EASY" + ConsoleColor.RESET);
                    } else if (diff.equals("2")) {
                        currentDifficulty = Difficulty.MEDIUM;
                        System.out.println(ConsoleColor.GREEN_BOLD + "\n✔ Default Computer Difficulty updated to MEDIUM" + ConsoleColor.RESET);
                    } else {
                        System.out.println(ConsoleColor.RED_BOLD + "\n⚠ Invalid selection." + ConsoleColor.RESET);
                    }
                    promptEnterToContinue(scanner);
                    break;
                case "4":
                    clearScreen();
                    List<String> lenBox = List.of(
                        "CHANGE DEFAULT TOURNAMENT LENGTH",
                        "---",
                        "Options: 1, 3, or 5"
                    );
                    printBox(lenBox, ConsoleColor.CYAN_BOLD, true);
                    System.out.print(ConsoleColor.CYAN_BOLD + "\n👉 Enter Tournament Length: " + ConsoleColor.RESET);
                    String len = scanner.nextLine().trim();
                    if (len.equals("1") || len.equals("3") || len.equals("5")) {
                        currentTournamentLength = Integer.parseInt(len);
                        System.out.println(ConsoleColor.GREEN_BOLD + "\n✔ Default Tournament Length updated to Best of " + currentTournamentLength + ConsoleColor.RESET);
                    } else {
                        System.out.println(ConsoleColor.RED_BOLD + "\n⚠ Invalid selection." + ConsoleColor.RESET);
                    }
                    promptEnterToContinue(scanner);
                    break;
                case "5":
                    back = true;
                    break;
                default:
                    System.out.println(ConsoleColor.RED_BOLD + "⚠ Invalid choice. Select 1-5." + ConsoleColor.RESET);
                    promptEnterToContinue(scanner);
            }
        }
    }

    private int getVisualLength(String s) {
        if (s == null) return 0;
        // Remove ANSI escape codes
        String stripped = s.replaceAll("\033\\[[;\\d]*m", "");
        int length = 0;
        int i = 0;
        while (i < stripped.length()) {
            int codePoint = stripped.codePointAt(i);
            if (codePoint > 0xFFFF || isWideEmojiOrSymbol(codePoint)) {
                length += 2;
            } else {
                length += 1;
            }
            i += Character.charCount(codePoint);
        }
        return length;
    }

    private boolean isWideEmojiOrSymbol(int cp) {
        // Dingbats, Miscellaneous Symbols, and other symbol ranges in BMP that render as 2 cells
        return (cp >= 0x2600 && cp <= 0x27BF);
    }

    private void clearScreen() {
        System.out.println();
        System.out.println();
    }

    private void promptEnterToContinue(Scanner scanner) {
        System.out.print(ConsoleColor.BLACK_BRIGHT + "\nPress [Enter] to continue..." + ConsoleColor.RESET);
        scanner.nextLine();
    }

    private void printBox(List<String> lines, String color, boolean useDoubleBorders) {
        char topLeft = useDoubleBorders ? '╔' : '┌';
        char topRight = useDoubleBorders ? '╗' : '┐';
        char bottomLeft = useDoubleBorders ? '╚' : '└';
        char bottomRight = useDoubleBorders ? '╝' : '┘';
        char horiz = useDoubleBorders ? '═' : '─';
        char vert = useDoubleBorders ? '║' : '│';
        
        int maxVal = 0;
        for (String line : lines) {
            if (line.equals("---")) continue;
            int len = getVisualLength(line);
            if (len > maxVal) {
                maxVal = len;
            }
        }
        // Pad to a reasonable minimum width to look good
        maxVal = Math.max(maxVal, 36);
        
        // Top border
        StringBuilder sb = new StringBuilder();
        sb.append(color).append(topLeft);
        for (int i = 0; i < maxVal + 4; i++) {
            sb.append(horiz);
        }
        sb.append(topRight).append(ConsoleColor.RESET);
        System.out.println(sb.toString());
        
        // Lines
        for (String line : lines) {
            if (line.equals("---")) {
                sb = new StringBuilder();
                char dividerLeft = useDoubleBorders ? '╠' : '├';
                char dividerRight = useDoubleBorders ? '╣' : '┤';
                char dividerHoriz = useDoubleBorders ? '═' : '─';
                sb.append(color).append(dividerLeft);
                for (int i = 0; i < maxVal + 4; i++) {
                    sb.append(dividerHoriz);
                }
                sb.append(dividerRight).append(ConsoleColor.RESET);
                System.out.println(sb.toString());
                continue;
            }
            int lineLen = getVisualLength(line);
            int spacesNeeded = maxVal - lineLen;
            sb = new StringBuilder();
            sb.append(color).append(vert).append(ConsoleColor.RESET).append("  ");
            sb.append(line);
            for (int i = 0; i < spacesNeeded; i++) {
                sb.append(" ");
            }
            sb.append("  ").append(color).append(vert).append(ConsoleColor.RESET);
            System.out.println(sb.toString());
        }
        
        // Bottom border
        sb = new StringBuilder();
        sb.append(color).append(bottomLeft);
        for (int i = 0; i < maxVal + 4; i++) {
            sb.append(horiz);
        }
        sb.append(bottomRight).append(ConsoleColor.RESET);
        System.out.println(sb.toString());
    }
}
