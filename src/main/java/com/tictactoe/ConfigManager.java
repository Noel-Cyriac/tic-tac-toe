package com.tictactoe;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private final Properties properties = new Properties();

    public ConfigManager() {
        loadConfig();
    }

    private void loadConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.out.println("Warning: " + CONFIG_FILE + " not found on classpath. Using default settings.");
                setDefaults();
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("Error reading " + CONFIG_FILE + ". Using default settings.");
            setDefaults();
        }
    }

    private void setDefaults() {
        properties.setProperty("default.board.size", "3");
        properties.setProperty("default.difficulty", "MEDIUM");
        properties.setProperty("default.game.mode", "SINGLE_PLAYER");
        properties.setProperty("default.tournament.length", "3");
        properties.setProperty("profiles.file.path", "profiles.json");
        properties.setProperty("history.file.path", "history.json");
    }

    public int getDefaultBoardSize() {
        try {
            return Integer.parseInt(properties.getProperty("default.board.size", "3"));
        } catch (NumberFormatException e) {
            return 3;
        }
    }

    public Difficulty getDefaultDifficulty() {
        try {
            return Difficulty.valueOf(properties.getProperty("default.difficulty", "MEDIUM").toUpperCase());
        } catch (IllegalArgumentException e) {
            return Difficulty.MEDIUM;
        }
    }

    public GameMode getDefaultGameMode() {
        try {
            return GameMode.valueOf(properties.getProperty("default.game.mode", "SINGLE_PLAYER").toUpperCase());
        } catch (IllegalArgumentException e) {
            return GameMode.SINGLE_PLAYER;
        }
    }

    public int getDefaultTournamentLength() {
        try {
            return Integer.parseInt(properties.getProperty("default.tournament.length", "3"));
        } catch (NumberFormatException e) {
            return 3;
        }
    }

    public String getProfilesFilePath() {
        return properties.getProperty("profiles.file.path", "profiles.json");
    }

    public String getHistoryFilePath() {
        return properties.getProperty("history.file.path", "history.json");
    }
}
