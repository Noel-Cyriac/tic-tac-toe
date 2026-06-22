package com.tictactoe;

public class TournamentResult {
    private String dateTime;
    private String player1Name;
    private String player2Name;
    private int player1Wins;
    private int player2Wins;
    private int draws;
    private String winnerName;

    // Default constructor for Jackson
    public TournamentResult() {}

    public TournamentResult(String dateTime, String player1Name, String player2Name, int player1Wins, int player2Wins, int draws, String winnerName) {
        this.dateTime = dateTime;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1Wins = player1Wins;
        this.player2Wins = player2Wins;
        this.draws = draws;
        this.winnerName = winnerName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public int getPlayer1Wins() {
        return player1Wins;
    }

    public void setPlayer1Wins(int player1Wins) {
        this.player1Wins = player1Wins;
    }

    public int getPlayer2Wins() {
        return player2Wins;
    }

    public void setPlayer2Wins(int player2Wins) {
        this.player2Wins = player2Wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s vs %s -> Score: %d-%d (%d Draws) | Winner: %s",
                dateTime, player1Name, player2Name, player1Wins, player2Wins, draws, winnerName);
    }
}
