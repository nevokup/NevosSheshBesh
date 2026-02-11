package com.example.nevos_shesh_besh.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    boolean DeTests = false;

    int[] board;
    int[] dice;
    List<Integer> availableMoves;
    private Random random;

    int moveFrom;
    boolean isP1Turn;
    int movesMade;
    int movesToDo;

    int p1EatenCount;
    int p2EatenCount;
    int p1OffBoard;
    int p2OffBoard;

    private static final String TAG = "Game";

    // --- Win Logic: Types and Listener ---
    public enum WinType {
        REGULAR,        // ניצחון רגיל (1 נקודה)
        MARS,           // מארס (2 נקודות)
        TURKISH_MARS    // מארס טורקי (3 נקודות - יש חייל בבית המנצח או אכול)
    }

    public interface GameOverListener {
        void onGameOver(String winnerName, WinType winType, int score);
    }

    private GameOverListener gameOverListener;

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }
    // -------------------------------------

    public Game() {
        board = new int[24];
        dice = new int[2];
        random = new Random();
        initBoard();
        isP1Turn = true;
        rollDice();
        moveFrom = -1;
        movesMade = 0;
        p1EatenCount = 0;
        p2EatenCount = 0;
        p1OffBoard = 0;
        p2OffBoard = 0;
    }

    public int[] getBoard() { return board; }
    public int[] getDice() { return dice; }
    public int getP1EatenCount() { return p1EatenCount; }
    public int getP2EatenCount() { return p2EatenCount; }
    public int getP1OffBoard() { return p1OffBoard; }
    public int getP2OffBoard() { return p2OffBoard; }

    public void rollDice() {
        dice[0] = random.nextInt(6) + 1;
        dice[1] = random.nextInt(6) + 1;

        availableMoves = new ArrayList<>();
        if (dice[0] == dice[1]) {
            movesToDo = 4;
            for (int i = 0; i < 4; i++) {
                availableMoves.add(dice[0]);
            }
        } else {
            movesToDo = 2;
            availableMoves.add(dice[0]);
            availableMoves.add(dice[1]);
        }
        checkIfPlayerIsStuck();
    }

    private void checkIfPlayerIsStuck() {
        if ((isP1Turn && p1EatenCount == 0) || (!isP1Turn && p2EatenCount == 0)) {
            if (canBearOff(isP1Turn)) {
                return;
            }
            return;
        }

        boolean hasValidReEntryMove = false;
        for (int diceValue : new ArrayList<>(availableMoves)) {
            int targetIndex;
            if (isP1Turn) {
                targetIndex = diceValue - 1;
                if (board[targetIndex] < 102) {
                    hasValidReEntryMove = true;
                    break;
                }
            } else {
                targetIndex = 24 - diceValue;
                boolean isBlockedByP1 = board[targetIndex] >= 2 && board[targetIndex] < 100;
                if (!isBlockedByP1) {
                    hasValidReEntryMove = true;
                    break;
                }
            }
        }

        if (!hasValidReEntryMove) {
            Log.d(TAG, "Player is stuck on re-entry. Ending turn.");
            endTurn();
        }
    }

    public boolean move(int index) {
        // Case 1: Deselect
        if (moveFrom == index) {
            board[moveFrom] -= 1000;
            moveFrom = -1;
            Log.d(TAG, "move: Deselected checker at index: " + index);
            return true;
        }

        // --- Bearing off logic ---
        if (canBearOff(isP1Turn) && moveFrom == -1) {
            boolean isMyChecker = isP1Turn ? (board[index] > 0 && board[index] < 100) : (board[index] >= 100);

            if (isMyChecker) {
                Integer roll = getBestDiceForBearOff(index);

                if (roll != null) {
                    board[index]--;
                    if (!isP1Turn && board[index] % 100 == 0) board[index] = 0;

                    if (isP1Turn) p1OffBoard++;
                    else p2OffBoard++;

                    availableMoves.remove(roll);
                    movesMade++;

                    // --- CHECK WIN AFTER BEARING OFF ---
                    checkWinCondition();
                    if (p1OffBoard == 15 || p2OffBoard == 15) return true; // Game Over
                    // -----------------------------------

                    if (movesMade >= movesToDo || availableMoves.isEmpty()) {
                        endTurn();
                    }
                    return true;
                }
            }
        }

        // Check legality
        if (!isLegalMove(index)) {
            if (moveFrom != -1) {
                boolean isMyNewChecker = (isP1Turn && board[index] > 0 && board[index] < 100) ||
                        (!isP1Turn && board[index] >= 100 && board[index] < 1000);
                if (isMyNewChecker) {
                    board[moveFrom] -= 1000;
                    moveFrom = index;
                    board[moveFrom] += 1000;
                    return true;
                }
            }
            return false;
        }

        // Execute Move
        boolean isReEntering = (isP1Turn && p1EatenCount > 0) || (!isP1Turn && p2EatenCount > 0);

        if (isReEntering) {
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

            // --- CHECK WIN AFTER RE-ENTRY (Rare but possible if opponent forfeits) ---
            checkWinCondition();
            if (p1OffBoard == 15 || p2OffBoard == 15) return true;

            if (movesMade >= movesToDo) endTurn();

        } else if (moveFrom == -1) {
            moveFrom = index;
            board[moveFrom] += 1000;
        } else {
            // Standard Move
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

            if (board[moveFrom] % 100 == 0 && board[moveFrom] != 0) {
                if (board[moveFrom] < 1000) board[moveFrom] = 0;
            }

            int distance = isP1Turn ? (index - moveFrom) : (moveFrom - index);
            availableMoves.remove(Integer.valueOf(distance));

            moveFrom = -1;
            movesMade++;

            // --- CHECK WIN ---
            checkWinCondition();
            if (p1OffBoard == 15 || p2OffBoard == 15) return true;

            if (movesMade >= movesToDo) endTurn();
        }
        return true;
    }

    private void checkWinCondition() {
        if (p1OffBoard == 15) {
            WinType type = calculateWinType(true);
            if (gameOverListener != null) {
                gameOverListener.onGameOver("Player 1", type, getScore(type));
            }
        } else if (p2OffBoard == 15) {
            WinType type = calculateWinType(false);
            if (gameOverListener != null) {
                gameOverListener.onGameOver("Player 2", type, getScore(type));
            }
        }
    }

    private WinType calculateWinType(boolean p1Won) {
        if (p1Won) {
            // P1 wins, check P2 status
            if (p2OffBoard > 0) return WinType.REGULAR;

            // Mars check. Turkish if P2 has checker on bar or in P1's home (18-23)
            boolean isTurkish = p2EatenCount > 0;
            if (!isTurkish) {
                for (int i = 18; i < 24; i++) {
                    if (board[i] >= 100) {
                        isTurkish = true;
                        break;
                    }
                }
            }
            return isTurkish ? WinType.TURKISH_MARS : WinType.MARS;

        } else {
            // P2 wins, check P1 status
            if (p1OffBoard > 0) return WinType.REGULAR;

            // Mars check. Turkish if P1 has checker on bar or in P2's home (0-5)
            boolean isTurkish = p1EatenCount > 0;
            if (!isTurkish) {
                for (int i = 0; i < 6; i++) {
                    if (board[i] > 0 && board[i] < 100) {
                        isTurkish = true;
                        break;
                    }
                }
            }
            return isTurkish ? WinType.TURKISH_MARS : WinType.MARS;
        }
    }

    private int getScore(WinType type) {
        switch (type) {
            case MARS: return 2;
            case TURKISH_MARS: return 3;
            default: return 1;
        }
    }

    private boolean canBearOff(boolean isP1) {
        if (isP1) {
            if (p1EatenCount > 0) return false;
            for (int i = 0; i < 18; i++) {
                if (board[i] > 0 && board[i] < 100) return false;
            }
        } else {
            if (p2EatenCount > 0) return false;
            for (int i = 6; i < 24; i++) {
                if (board[i] >= 100) return false;
            }
        }
        return true;
    }

    private Integer getBestDiceForBearOff(int checkerIndex) {
        int dist = isP1Turn ? (24 - checkerIndex) : (checkerIndex + 1);

        if (availableMoves.contains(dist)) {
            return dist;
        }

        int furthestDistance = -1;
        if (isP1Turn) {
            for (int i = 18; i < 24; i++) {
                if (board[i] > 0 && board[i] < 100) {
                    furthestDistance = 24 - i;
                    break;
                }
            }
        } else {
            for (int i = 5; i >= 0; i--) {
                if (board[i] >= 100) {
                    furthestDistance = i + 1;
                    break;
                }
            }
        }

        if (dist != furthestDistance) {
            return null;
        }

        for (Integer roll : availableMoves) {
            if (roll > dist) return roll;
        }

        return null;
    }

    private void endTurn() {
        isP1Turn = !isP1Turn;
        movesMade = 0;
        moveFrom = -1;
        rollDice();
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

        if (moveFrom == -1) {
            if (board[index] == 0) return false;
            if (isP1Turn) return board[index] < 100;
            else return board[index] >= 100;
        }

        if (isP1Turn) {
            if (board[index] >= 102) return false;
            if (index < moveFrom) return false;
        } else {
            if (board[index] > 1 && board[index] < 100) return false;
            if (index > moveFrom) return false;
        }

        int distance = isP1Turn ? index - moveFrom : moveFrom - index;
        return availableMoves.contains(distance);
    }

    private void initBoard() {
        int[] initPositionsP1;
        int[] initPositionsP2;

        if (DeTests == false) {
            initPositionsP1 = new int[]{2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, 0};
            initPositionsP2 = new int[]{0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2};
        } else {
            // Setup for testing Bearing Off logic
            initPositionsP1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 3, 2, 3, 2};
            initPositionsP2 = new int[]{2, 3, 2, 3, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        }

        for (int i = 0; i < board.length; i++) {
            if (initPositionsP1[i] > 0) {
                board[i] = initPositionsP1[i];
            } else if (initPositionsP2[i] > 0) {
                board[i] = initPositionsP2[i] + 100;
            }
        }
    }
}