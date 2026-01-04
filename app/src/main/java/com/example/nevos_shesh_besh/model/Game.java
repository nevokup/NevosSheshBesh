package com.example.nevos_shesh_besh.model;

import android.util.Log;

public class Game {
    int[] initPositionsP1 = {0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5};
    int[] initPositionsP2 = {5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0};

    int[] board;

    int moveFrom;

    private static final String TAG = "Game";


    public Game() {
        board = new int[24];
        initBoard();
        moveFrom = -1;
    }

    public int[] getBoard() {
        return board;
    }
    public boolean move(int index)
    {
        if(!isLegalMove(index))
        {
            Log.d(TAG, "move: move is not legal. index: " + index);
        }

        if(moveFrom == -1)
        {
            moveFrom = index;
        }
        else
        {
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
        }
        return true;
    }

    private boolean isLegalMove(int index)
    {
        if(moveFrom == -1)
        {
            if(board[index] == 0)
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
