package com.example.nevos_shesh_besh.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    int[] initPositionsP1 = {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, 0};
    int[] initPositionsP2 = {0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2};

    int[] board;
    int[] dice;
    List<Integer> availableMoves;
    private Random random;

    int moveFrom;
    boolean isP1Turn;
    int movesMade;
    int movesToDo;

    private static final String TAG = "Game";


    public Game() {
        board = new int[24];
        dice = new int[2];
        random = new Random();
        initBoard();
        rollDice();
        moveFrom = -1;
        isP1Turn = true;
        movesMade = 0;
    }

    public int[] getBoard() {
        return board;
    }

    public int[] getDice() {
        return dice;
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
        if(moveFrom == -1)
        {
            moveFrom = index;
            board[moveFrom] += 1000;
        }
        else
        {
            if (board[moveFrom] >= 1000)
                board[moveFrom] -= 1000;

            board[moveFrom]--;

            if(board[moveFrom] >= 100)
            {
                if(board[index] == 0)
                    board[index]+=100;

                if(board[moveFrom] == 100)
                    board[moveFrom] = 0;
            }

            board[index]++;
            
            int distance = isP1Turn ? (index - moveFrom) : (moveFrom - index);
            availableMoves.remove(Integer.valueOf(distance));

            moveFrom = -1;
            movesMade++;

            if (movesMade >= movesToDo) {
                isP1Turn = !isP1Turn;
                movesMade = 0;
                rollDice();
            }
        }
        return true;
    }

    private boolean isLegalMove(int index) {

        //move from phase
        if (moveFrom == -1) {
            if (board[index] == 0)
                return false;

            //בודק שכל שחקן משחק בתור שלו ולא בתור של היריב
            if (isP1Turn) {
                if (board[index] >= 100) return false;
            }
            else {
                if (board[index] < 100) return false;
            }

            return true;
        }

        //move to phase

        //בודק ששחקן כחול לא יכול לעלות על שחקן לבן וההפך
        if (!isP1Turn) {
            if (board[index] > 1 && board[index] < 100) {
                return false;
            }
        }
        else {
            if (board[index] >= 102) {
                return false;
            }
        }


        //בודק שהשחקן הולך בכיוון הנכון ולא בכיוון הנגדי
        if (isP1Turn) {
            if (index < moveFrom)
                return false;
        }
        else {
            if (index > moveFrom)
                return false;
        }
        
        
        //בודק שהשחקן הולך למשולש שמותר לו לפי הקוביות
        int distance = isP1Turn ? index - moveFrom : moveFrom - index;
        if (!availableMoves.contains(distance)) {
            Log.d(TAG, "isLegalMove: Invalid move distance: " + distance + ". Available moves: " + availableMoves.toString());
            return false;
        }

        return true;
    }


    private void initBoard() {
        for (int i = 0; i < board.length; i++) {

            if (initPositionsP1[i] > 0) {
                board[i] = initPositionsP1[i];
            }

            else if (initPositionsP2[i] > 0) {
                board[i] = initPositionsP2[i] + 100;
            }
        }

    }

}
