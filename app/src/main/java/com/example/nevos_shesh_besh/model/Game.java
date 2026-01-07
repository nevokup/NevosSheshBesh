package com.example.nevos_shesh_besh.model;

import android.util.Log;

public class Game {
    int[] initPositionsP1 = {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, 0};
    int[] initPositionsP2 = {0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2};

    int[] board;

    int moveFrom;
    boolean isP1Turn;
    int movesMade;

    private static final String TAG = "Game";


    public Game() {
        board = new int[24];
        initBoard();
        moveFrom = -1;
        isP1Turn = true;
        movesMade = 0;
    }

    public int[] getBoard() {
        return board;
    }
    public boolean move(int index)
    {
        if(!isLegalMove(index))
        {
            Log.d(TAG, "move: move is not legal. index: " + index);
            return false;
        }

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
            moveFrom = -1;

            movesMade++;

            int movesToDo = 2;


            if (movesMade >= movesToDo) {
                isP1Turn = !isP1Turn;
                movesMade = 0;
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
