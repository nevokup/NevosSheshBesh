package com.example.nevos_shesh_besh;

public class GameRecord {
    private String opponentName;
    private String winner;    // "אני" או שם היריב
    private String winType;   // "רגיל", "מרס", "מרס כוכבים"
    private long timestamp;   // למיון לפי זמן
    private String playerUid;

    public GameRecord() {} // חובה עבור Firestore

    public GameRecord(String opponentName, String winner, String winType, long timestamp, String playerUid) {
        this.opponentName = opponentName;
        this.winner = winner;
        this.winType = winType;
        this.timestamp = timestamp;
        this.playerUid = playerUid;
    }

    public String getOpponentName() { return opponentName; }
    public String getWinner() { return winner; }
    public String getWinType() { return winType; }
    public long getTimestamp() { return timestamp; }
    public String getPlayerUid() { return playerUid; }
}