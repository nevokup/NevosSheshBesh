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
    }

    public int[] getBoard() {
        return board;
    }

    public int[] getDice() {
        return dice;
    }

    public int getP1EatenCount() {
        return p1EatenCount;
    }

    public int getP2EatenCount() {
        return p2EatenCount;
    }

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
            // If not re-entering, we assume there's a move. A more complex check for general stuck positions can be added later.
            return;
        }

        boolean hasValidReEntryMove = false;
        for (int diceValue : new ArrayList<>(availableMoves)) { // Iterate over a copy
            int targetIndex;
            if (isP1Turn) {
                targetIndex = diceValue - 1;
                if (board[targetIndex] < 102) { // Is the spot open for P1?
                    hasValidReEntryMove = true;
                    break;
                }
            } else { // Player 2's turn
                targetIndex = 24 - diceValue;
                boolean isBlockedByP1 = board[targetIndex] >= 2 && board[targetIndex] < 100;
                if (!isBlockedByP1) { // Is the spot open for P2?
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


    public boolean move(int index)
    {
        // Case 1: A checker is selected and user clicks it again to deselect.
        if (moveFrom == index) {
            board[moveFrom] -= 1000; // Remove selection marker
            moveFrom = -1;
            Log.d(TAG, "move: Deselected checker at index: " + index);
            return true;
        }

        // Check if the move is legal. If not, check for re-selection.
        if(!isLegalMove(index))
        {
            // If a checker is selected and the user clicks another of their own checkers, switch selection.
            if (moveFrom != -1) {
                boolean isMyNewChecker = (isP1Turn && board[index] > 0 && board[index] < 100) ||
                                         (!isP1Turn && board[index] >= 100 && board[index] < 1000);
                if (isMyNewChecker) {
                    board[moveFrom] -= 1000; // Deselect old
                    moveFrom = index;        // Select new
                    board[moveFrom] += 1000;  // Highlight new
                    Log.d(TAG, "move: Switched selection to index: " + index);
                    return true;
                }
            }

            Log.d(TAG, "move: move is not legal. index: " + index);
            return false;
        }

        // If we are here, the move is legal.
        boolean isReEntering = (isP1Turn && p1EatenCount > 0) || (!isP1Turn && p2EatenCount > 0);

        if (isReEntering) {
            if (isP1Turn) {
                // Player 1 re-entering
                if (board[index] == 101) { // Eating a P2 checker
                    p2EatenCount++;
                    board[index] = 1; // Becomes one P1 checker
                } else {
                    board[index]++; // Add a P1 checker
                }
                p1EatenCount--;
                availableMoves.remove(Integer.valueOf(index + 1));
            } else {
                // Player 2 re-entering
                if (board[index] == 1) { // Eating a P1 checker
                    p1EatenCount++;
                    board[index] = 101; // Becomes one P2 checker
                } else {
                    if (board[index] == 0) board[index] = 100;
                    board[index]++; // Add a P2 checker
                }
                p2EatenCount--;
                availableMoves.remove(Integer.valueOf(24 - index));
            }

            movesMade++;
            if (movesMade >= movesToDo) {
                endTurn();
            }
        } else if (moveFrom == -1) {
            // This is a normal move, starting with selecting a checker
            moveFrom = index;
            board[moveFrom] += 1000;
        } else {
            // This is the second part of a normal move
            // Eating logic
            if (isP1Turn) {
                if (board[index] == 101) { // P1 eats a single P2 checker
                    p2EatenCount++;
                    board[index] = 0;
                }
            } else {
                if (board[index] == 1) { // P2 eats a single P1 checker
                    p1EatenCount++;
                    board[index] = 0;
                }
            }

            // Move checker from 'moveFrom' to 'index'
            if (board[moveFrom] >= 1000)
                board[moveFrom] -= 1000;

            board[moveFrom]--;

            if (isP1Turn) {
                 board[index]++;
            } else {
                if (board[index] == 0) board[index] = 100;
                board[index]++;
            }

            if (board[moveFrom] % 100 == 0 && board[moveFrom] != 0) { // check if it was a P2 spot and now its empty
                if(board[moveFrom] < 1000) // dont reset selected checkers
                    board[moveFrom] = 0;
            }


            int distance = isP1Turn ? (index - moveFrom) : (moveFrom - index);
            availableMoves.remove(Integer.valueOf(distance));

            moveFrom = -1;
            movesMade++;

            if (movesMade >= movesToDo) {
                endTurn();
            }
        }
        return true;
    }

    private void endTurn() {
        isP1Turn = !isP1Turn;
        movesMade = 0;
        moveFrom = -1;
        rollDice();
    }

    private boolean isLegalMove(int index) {
        // Player must re-enter eaten checkers first.
        if (isP1Turn && p1EatenCount > 0) {
            int diceValue = index + 1;
            if (!availableMoves.contains(diceValue)) return false;
            // P1 can enter on a free spot, their own spot, or a spot with a single P2 checker.
            return board[index] < 102;
        }

        if (!isP1Turn && p2EatenCount > 0) {
            int diceValue = 24 - index;
            if (!availableMoves.contains(diceValue)) return false;
            // P2 can enter on an empty spot, a spot with one P1 checker, or their own spot.
            // i.e., not a spot blocked by P1 (2 or more P1 checkers).
            boolean isBlockedByP1 = board[index] >= 2 && board[index] < 100;
            return !isBlockedByP1;
        }

        // If a checker is not selected yet, check if the selected checker is valid.
        if (moveFrom == -1) {
            if (board[index] == 0) return false;
            if (isP1Turn) {
                return board[index] < 100; // P1 can only select P1 checkers
            } else {
                return board[index] >= 100; // P2 can only select P2 checkers
            }
        }

        // If a checker is already selected, check if the destination is valid.
        // Check for blocked destination
        if (isP1Turn) {
            if (board[index] >= 102) return false; // P1 cannot move to a spot with 2 or more P2 checkers
        } else {
            if (board[index] > 1 && board[index] < 100) return false; // P2 cannot move to a spot with 2 or more P1 checkers
        }

        // Check for correct direction
        if (isP1Turn) {
            if (index < moveFrom) return false;
        } else {
            if (index > moveFrom) return false;
        }

        // Check if the move distance matches a dice roll
        int distance = isP1Turn ? index - moveFrom : moveFrom - index;
        if (!availableMoves.contains(distance)) {
            Log.d(TAG, "isLegalMove: Invalid move distance: " + distance + ". Available moves: " + availableMoves.toString());
            return false;
        }

        return true;
    }

    private void initBoard() {
        int[] initPositionsP1;
        int[] initPositionsP2;

        if (DeTests == false) {
            initPositionsP1 = new int[]{2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, 0};
            initPositionsP2 = new int[]{0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2};
        } else {
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
