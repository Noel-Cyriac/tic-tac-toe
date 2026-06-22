package com.tictactoe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PersistenceManager {
    private final String profilesPath;
    private final String historyPath;
    private final ObjectMapper mapper;

    public PersistenceManager(String profilesPath, String historyPath) {
        this.profilesPath = profilesPath;
        this.historyPath = historyPath;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public List<PlayerProfile> loadProfiles() {
        File file = new File(profilesPath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<PlayerProfile>>() {});
        } catch (IOException e) {
            System.err.println("Error loading player profiles from " + profilesPath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveProfiles(List<PlayerProfile> profiles) {
        try {
            mapper.writeValue(new File(profilesPath), profiles);
        } catch (IOException e) {
            System.err.println("Error saving player profiles to " + profilesPath + ": " + e.getMessage());
        }
    }

    public List<TournamentResult> loadHistory() {
        File file = new File(historyPath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<TournamentResult>>() {});
        } catch (IOException e) {
            System.err.println("Error loading tournament history from " + historyPath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveTournamentResult(TournamentResult result) {
        List<TournamentResult> history = loadHistory();
        history.add(result);
        try {
            mapper.writeValue(new File(historyPath), history);
        } catch (IOException e) {
            System.err.println("Error saving tournament history to " + historyPath + ": " + e.getMessage());
        }
    }
}
