package com.tictactoe;

public class PlayerProfile {
    private String name;
    private int matchesPlayed;
    private int wins;
    private int losses;
    private int draws;

    // Default constructor for Jackson
    public PlayerProfile() {}

    public PlayerProfile(String name) {
        this.name = name;
        this.matchesPlayed = 0;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public void recordWin() {
        this.matchesPlayed++;
        this.wins++;
    }

    public void recordLoss() {
        this.matchesPlayed++;
        this.losses++;
    }

    public void recordDraw() {
        this.matchesPlayed++;
        this.draws++;
    }

    @Override
    public String toString() {
        return String.format("Profile: %-15s | Played: %3d | Wins: %3d | Losses: %3d | Draws: %3d", 
                name, matchesPlayed, wins, losses, draws);
    }
}
