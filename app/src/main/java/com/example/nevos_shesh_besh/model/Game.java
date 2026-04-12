package com.example.nevos_shesh_besh.model;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {
    private static final String TAG = "Game";

    public int[] board;
    public int[] dice;
    public List<Integer> availableMoves;
    private Random random;

    public int moveFrom = -1;
    public boolean isP1Turn;
    public int movesMade;
    public int movesToDo;

    public int p1EatenCount;
    public int p2EatenCount;
    public int p1OffBoard;
    public int p2OffBoard;

    // הגדרה מי השחקן במכשיר הזה - מעודכן על ידי ה-Lobby ב-MainActivity
    public boolean localPlayerIsP1 = true;

    public enum WinType { REGULAR, MARS, TURKISH_MARS }
    public interface GameOverListener {
        void onGameOver(String winnerName, WinType winType, int score);
    }
    private GameOverListener gameOverListener;

    public Game() {
        board = new int[24];
        dice = new int[2];
        random = new Random();
        initBoard();
        isP1Turn = true;
        rollDice();
        p1EatenCount = 0; p2EatenCount = 0;
        p1OffBoard = 0; p2OffBoard = 0;
    }

    // --- סנכרון Firebase ---
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        List<Integer> boardList = new ArrayList<>();
        for (int i : board) boardList.add(i);
        map.put("board", boardList);
        map.put("dice0", dice[0]);
        map.put("dice1", dice[1]);
        map.put("isP1Turn", isP1Turn);
        map.put("p1Eaten", p1EatenCount);
        map.put("p2Eaten", p2EatenCount);
        map.put("p1Off", p1OffBoard);
        map.put("p2Off", p2OffBoard);
        map.put("availableMoves", availableMoves);
        map.put("movesToDo", movesToDo);
        map.put("movesMade", movesMade);
        return map;
    }

    public void updateFromMap(Map<String, Object> map) {
        if (map == null) return;
        try {
            List<Long> boardList = (List<Long>) map.get("board");
            if (boardList != null) {
                for (int i = 0; i < boardList.size(); i++) board[i] = boardList.get(i).intValue();
            }
            dice[0] = ((Long) map.get("dice0")).intValue();
            dice[1] = ((Long) map.get("dice1")).intValue();
            isP1Turn = (boolean) map.get("isP1Turn");
            p1EatenCount = ((Long) map.get("p1Eaten")).intValue();
            p2EatenCount = ((Long) map.get("p2Eaten")).intValue();
            p1OffBoard = ((Long) map.get("p1Off")).intValue();
            p2OffBoard = ((Long) map.get("p2Off")).intValue();
            movesToDo = ((Long) map.get("movesToDo")).intValue();
            movesMade = ((Long) map.get("movesMade")).intValue();

            List<Long> avail = (List<Long>) map.get("availableMoves");
            availableMoves = new ArrayList<>();
            if (avail != null) for (Long l : avail) availableMoves.add(l.intValue());
        } catch (Exception e) { Log.e(TAG, "Update Error", e); }
    }

    // --- לוגיקת משחק ---
    public void rollDice() {
        dice[0] = random.nextInt(6) + 1;
        dice[1] = random.nextInt(6) + 1;
        availableMoves = new ArrayList<>();
        if (dice[0] == dice[1]) {
            movesToDo = 4;
            for (int i = 0; i < 4; i++) availableMoves.add(dice[0]);
        } else {
            movesToDo = 2;
            availableMoves.add(dice[0]);
            availableMoves.add(dice[1]);
        }
    }

    public boolean move(int index) {
        // הגנה: רק השחקן שתורו יכול להזיז
        if (isP1Turn != localPlayerIsP1) return false;

        // ביטול בחירה
        if (moveFrom == index) {
            if (board[moveFrom] >= 1000) board[moveFrom] -= 1000;
            moveFrom = -1;
            return true;
        }

        // לוגיקת הוצאת חיילים (Bearing Off)
        if (canBearOff(isP1Turn) && moveFrom == -1) {
            boolean isMyChecker = isP1Turn ? (board[index] > 0 && board[index] < 100) : (board[index] >= 100 && board[index] < 1000);
            if (isMyChecker) {
                Integer roll = getBestDiceForBearOff(index);
                if (roll != null) {
                    board[index]--;
                    if (!isP1Turn && board[index] % 100 == 0) board[index] = 0;
                    if (isP1Turn) p1OffBoard++; else p2OffBoard++;
                    availableMoves.remove(roll);
                    movesMade++;
                    checkWinCondition();
                    if (movesMade >= movesToDo || availableMoves.isEmpty()) endTurn();
                    return true;
                }
            }
        }

        if (!isLegalMove(index)) {
            // בחירת חייל חדש
            if (moveFrom != -1 && board[moveFrom] >= 1000) board[moveFrom] -= 1000;
            boolean isMyNewChecker = (isP1Turn && board[index] > 0 && board[index] < 100) ||
                    (!isP1Turn && board[index] >= 100 && board[index] < 1000);
            if (isMyNewChecker) {
                moveFrom = index;
                board[moveFrom] += 1000;
                return true;
            }
            return false;
        }

        // ביצוע המהלך
        boolean isReEntering = (isP1Turn && p1EatenCount > 0) || (!isP1Turn && p2EatenCount > 0);

        if (isReEntering) {
            executeReEntry(index);
        } else if (moveFrom == -1) {
            moveFrom = index;
            board[moveFrom] += 1000;
        } else {
            executeStandardMove(index);
        }

        return true;
    }

    private void executeReEntry(int index) {
        if (isP1Turn) {
            if (board[index] == 101) { p2EatenCount++; board[index] = 1; }
            else { board[index]++; }
            p1EatenCount--;
            availableMoves.remove(Integer.valueOf(index + 1));
        } else {
            if (board[index] == 1) { p1EatenCount++; board[index] = 101; }
            else { if (board[index] == 0) board[index] = 100; board[index]++; }
            p2EatenCount--;
            availableMoves.remove(Integer.valueOf(24 - index));
        }
        movesMade++;
        if (movesMade >= movesToDo || availableMoves.isEmpty()) endTurn();
    }

    private void executeStandardMove(int index) {
        if (isP1Turn) {
            if (board[index] == 101) { p2EatenCount++; board[index] = 0; }
        } else {
            if (board[index] == 1) { p1EatenCount++; board[index] = 0; }
        }

        if (board[moveFrom] >= 1000) board[moveFrom] -= 1000;
        board[moveFrom]--;
        if (isP1Turn) board[index]++;
        else {
            if (board[index] == 0) board[index] = 100;
            board[index]++;
        }

        // ניקוי משבצת ריקה של שחקן 2
        if (!isP1Turn && board[moveFrom] == 100) board[moveFrom] = 0;
        if (board[moveFrom] < 0) board[moveFrom] = 0;

        int distance = isP1Turn ? (index - moveFrom) : (moveFrom - index);
        availableMoves.remove(Integer.valueOf(distance));
        movesMade++;
        moveFrom = -1;
        checkWinCondition();
        if (movesMade >= movesToDo || availableMoves.isEmpty()) endTurn();
    }

    private boolean isLegalMove(int index) {
        if (isP1Turn && p1EatenCount > 0) {
            int diceValue = index + 1;
            return availableMoves.contains(diceValue) && board[index] < 102;
        }
        if (!isP1Turn && p2EatenCount > 0) {
            int diceValue = 24 - index;
            boolean isBlockedByP1 = board[index] >= 2 && board[index] < 100;
            return availableMoves.contains(diceValue) && !isBlockedByP1;
        }
        if (moveFrom == -1) return false;

        int distance = isP1Turn ? index - moveFrom : moveFrom - index;
        if (!availableMoves.contains(distance)) return false;

        if (isP1Turn) {
            if (board[index] >= 102 || index < moveFrom) return false;
        } else {
            if ((board[index] > 1 && board[index] < 100) || index > moveFrom) return false;
        }
        return true;
    }

    private boolean canBearOff(boolean isP1) {
        if (isP1) {
            if (p1EatenCount > 0) return false;
            for (int i = 0; i < 18; i++) if (board[i] > 0 && board[i] < 100) return false;
        } else {
            if (p2EatenCount > 0) return false;
            for (int i = 6; i < 24; i++) if (board[i] >= 100) return false;
        }
        return true;
    }

    private Integer getBestDiceForBearOff(int index) {
        int dist = isP1Turn ? (24 - index) : (index + 1);
        if (availableMoves.contains(dist)) return dist;

        // בדיקה אם זה החייל הכי רחוק כדי להשתמש בקוביה גבוהה יותר
        int furthest = -1;
        if (isP1Turn) {
            for (int i=18; i<24; i++) if (board[i]>0 && board[i]<100) { furthest = 24-i; break; }
        } else {
            for (int i=5; i>=0; i--) if (board[i]>=100) { furthest = i+1; break; }
        }

        if (dist == furthest) {
            for (int move : availableMoves) if (move > dist) return move;
        }
        return null;
    }

    private void endTurn() {
        isP1Turn = !isP1Turn;
        movesMade = 0;
        moveFrom = -1;
        rollDice();
    }

    private void checkWinCondition() {
        if (p1OffBoard == 15 && gameOverListener != null) gameOverListener.onGameOver("Player 1", WinType.REGULAR, 1);
        if (p2OffBoard == 15 && gameOverListener != null) gameOverListener.onGameOver("Player 2", WinType.REGULAR, 1);
    }

    public void setGameOverListener(GameOverListener l) { this.gameOverListener = l; }

    private void initBoard() {
        board = new int[24];
        int[] p1Pos = {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, 0};
        int[] p2Pos = {0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2};
        for (int i = 0; i < 24; i++) {
            if (p1Pos[i] > 0) board[i] = p1Pos[i];
            else if (p2Pos[i] > 0) board[i] = p2Pos[i] + 100;
        }
    }
}