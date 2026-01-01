package com.example.nevos_shesh_besh.model;

public class Game {
    int[] initPositionsP1 = {0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5};
    int[] initPositionsP2 = {5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0};

    int[] board;

    public Game() {
        board = new int[24];
        initBoard();

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
