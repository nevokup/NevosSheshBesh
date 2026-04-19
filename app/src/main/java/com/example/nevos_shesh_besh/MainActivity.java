package com.example.nevos_shesh_besh;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nevos_shesh_besh.UI.CustomSurfaceView;
import com.example.nevos_shesh_besh.model.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private Game game;
    private NetworkManager networkManager;
    private CustomSurfaceView gameView;
    private boolean isOnlineMode = false;
    private String currentUsername = "אני";
    private boolean isResultSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = new Game();
        networkManager = new NetworkManager();
        isResultSaved = false;

        loadCurrentUserName();

        game.setGameOverListener((winnerName, winTypeDesc) ->
                runOnUiThread(() -> {
                    if (!isResultSaved) {
                        saveGameResult(winnerName, winTypeDesc);
                        showWinnerDialog(winnerName, winTypeDesc);
                    }
                })
        );

        showLobbyDialog();
    }

    private void loadCurrentUserName() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            currentUsername = doc.getString("username");
                        }
                    });
        }
    }

    private void saveGameResult(String winnerName, String winTypeDesc) {
        if (isResultSaved || !isOnlineMode) return;
        
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        // קביעת שם היריב
        String opponentName = game.localPlayerIsP1 ? game.p2Name : game.p1Name;

        // דרישה: לא לשמור אם זה עדיין שם ברירת מחדל או לא שם אמיתי
        if (opponentName == null || opponentName.equals("שחקן 1") || opponentName.equals("שחקן 2") || opponentName.equals("מחכה ליריב...")) {
            return;
        }

        isResultSaved = true;

        String finalWinner;
        if (currentUsername.equals(winnerName) || "אני".equals(winnerName)) {
            finalWinner = "אני";
        } else {
            finalWinner = winnerName;
        }

        GameRecord record = new GameRecord(
                opponentName,
                finalWinner,
                winTypeDesc,
                System.currentTimeMillis(),
                uid
        );

        FirebaseFirestore.getInstance().collection("games")
                .add(record)
                .addOnFailureListener(e -> {
                    isResultSaved = false;
                    Toast.makeText(this, "שגיאה בשמירת תוצאה", Toast.LENGTH_SHORT).show();
                });
    }

    private void showLobbyDialog() {
        String[] options = {"צור משחק", "הצטרף למשחק", "משחק יחיד (מקומי)"};
        new AlertDialog.Builder(this)
                .setTitle("שש-בש")
                .setItems(options, (dialog, which) -> {
                    isResultSaved = false; 
                    if (which == 0) {
                        isOnlineMode = true;
                        game.isOnlineMode_Internal = true;
                        game.localPlayerIsP1 = true;
                        game.p1Name = currentUsername;
                        game.p2Name = "שחקן 2";
                        startGameAsHost();
                    } else if (which == 1) {
                        isOnlineMode = true;
                        game.isOnlineMode_Internal = true;
                        game.localPlayerIsP1 = false;
                        game.p1Name = "שחקן 1";
                        game.p2Name = currentUsername;
                        showJoinDialog();
                    } else {
                        isOnlineMode = false;
                        game.isOnlineMode_Internal = false;
                        game.localPlayerIsP1 = true;
                        game.p1Name = currentUsername;
                        game.p2Name = "מחשב";
                        startLocalSinglePlayer();
                    }
                })
                .setCancelable(false).show();
    }

    private void startLocalSinglePlayer() {
        startGameView();
        Toast.makeText(this, "משחק יחיד התחיל", Toast.LENGTH_SHORT).show();
    }

    private void startGameAsHost() {
        String code = String.valueOf((int)(Math.random() * 9000) + 1000);
        networkManager.createGame(code, game, data -> {
            runOnUiThread(() -> {
                game.updateFromMap(data);
                handleRemoteUpdate();
                if (gameView != null) gameView.invalidate();
            });
        });
        Toast.makeText(this, "קוד משחק: " + code, Toast.LENGTH_LONG).show();
        startGameView();
    }

    private void showJoinDialog() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("הכנס קוד")
                .setView(input)
                .setPositiveButton("הצטרף", (dialog, which) -> {
                    networkManager.joinGame(input.getText().toString(), data -> {
                        runOnUiThread(() -> {
                            game.updateFromMap(data);
                            if (currentUsername != null && !currentUsername.equals("אני")) {
                                game.p2Name = currentUsername;
                                networkManager.updateGameState(game);
                            }
                            handleRemoteUpdate();
                            if (gameView != null) gameView.invalidate();
                        });
                    });
                    startGameView();
                }).show();
    }

    private void handleRemoteUpdate() {
        if (game.isGameOver && !isResultSaved) {
            saveGameResult(game.winnerName, game.winTypeString);
            showWinnerDialog(game.winnerName, game.winTypeString);
        }
    }

    private void startGameView() {
        gameView = new CustomSurfaceView(this, game);
        setContentView(gameView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP && isOnlineMode && !game.isGameOver) {
            networkManager.updateGameState(game);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void showWinnerDialog(String winnerName, String winTypeDesc) {
        if (isFinishing()) return;
        new AlertDialog.Builder(this)
                .setTitle("המשחק נגמר!")
                .setMessage(winnerName + " ניצח!\nסוג ניצחון: " + winTypeDesc)
                .setCancelable(false)
                .setPositiveButton("חזרה לתפריט", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
}
