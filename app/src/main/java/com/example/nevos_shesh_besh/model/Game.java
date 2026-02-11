package com.example.nevos_shesh_besh.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {

    boolean DeTests = true;

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
            // Add more logic here if needed for normal moves
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

        // --- NEW & FIXED: Bearing off logic ---
        // We check bearing off ONLY if no checker is currently selected (moveFrom == -1)
        if (canBearOff(isP1Turn) && moveFrom == -1) {
            boolean isMyChecker = isP1Turn ? (board[index] > 0 && board[index] < 100) : (board[index] >= 100);

            if (isMyChecker) {
                // Try to get a valid die for bearing off
                Integer roll = getBestDiceForBearOff(index);

                if (roll != null) {
                    // Execute Bear Off
                    board[index]--;
                    if (!isP1Turn && board[index] % 100 == 0) board[index] = 0; // Clean up P2 marker

                    if (isP1Turn) p1OffBoard++;
                    else p2OffBoard++;

                    availableMoves.remove(roll);
                    movesMade++;

                    if (movesMade >= movesToDo || availableMoves.isEmpty()) {
                        endTurn();
                    }
                    return true;
                }
                // If roll is null, it means we can't bear off THIS checker,
                // but we might be able to move it internally. So we let the code continue to selection logic.
            }
        }

        // Check if the move is legal. If not, check for re-selection.
        if (!isLegalMove(index)) {
            if (moveFrom != -1) {
                boolean isMyNewChecker = (isP1Turn && board[index] > 0 && board[index] < 100) ||
                        (!isP1Turn && board[index] >= 100 && board[index] < 1000);
                if (isMyNewChecker) {
                    board[moveFrom] -= 1000; // Deselect old
                    moveFrom = index;        // Select new
                    board[moveFrom] += 1000; // Highlight new
                    return true;
                }
            }
            return false;
        }

        // Logic for executing a normal move or re-entry
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
            if (movesMade >= movesToDo) endTurn();

        } else if (moveFrom == -1) {
            // Select checker
            moveFrom = index;
            board[moveFrom] += 1000;
        } else {
            // Execute Move
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

            if (movesMade >= movesToDo) endTurn();
        }
        return true;
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

    // --- FIXED FUNCTION ---
    private Integer getBestDiceForBearOff(int checkerIndex) {
        // Calculate distance from exit
        // P1 exits at hypothetical 24 (Board indices 18-23). Distance = 24 - index.
        // P2 exits at hypothetical -1 (Board indices 0-5). Distance = index + 1.
        int dist = isP1Turn ? (24 - checkerIndex) : (checkerIndex + 1);

        // 1. Check for Exact Match (Best case)
        if (availableMoves.contains(dist)) {
            return dist;
        }

        // 2. Check for High Die Rule (If dice > dist, AND this is the furthest checker)
        // Find the furthest checker's distance
        int furthestDistance = -1;

        if (isP1Turn) {
            // Iterate from 18 upwards. The first index we find with a checker is the furthest from 24.
            // Example: checkers on 20 and 23. Loop checks 18, 19, 20(Found!).
            // Dist for 20 is 4. Dist for 23 is 1. 4 is the furthest distance.
            for (int i = 18; i < 24; i++) {
                if (board[i] > 0 && board[i] < 100) {
                    furthestDistance = 24 - i;
                    break;
                }
            }
        } else {
            // Iterate from 5 downwards. The first index we find is furthest from 0.
            for (int i = 5; i >= 0; i--) {
                if (board[i] >= 100) {
                    furthestDistance = i + 1;
                    break;
                }
            }
        }

        // If the clicked checker is NOT the furthest one, we cannot use a larger dice.
        if (dist != furthestDistance) {
            return null;
        }

        // If we are here, this IS the furthest checker.
        // Now check if there is ANY dice larger than the distance.
        // We look for the smallest die that is still larger than dist (standard logic),
        // or just any available one since the rule allows it.
        Integer bestHighDie = null;
        for (Integer roll : availableMoves) {
            if (roll > dist) {
                // We prefer the largest die if we want to get rid of big numbers,
                // OR just the first one found.
                // In your example: Roll 6,5. Checkers on 5. Dist is 5.
                // 6 > 5. Valid.
                return roll;
            }
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