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
// מחלקת המסך הראשית של המשחק, היורשת מ-AppCompatActivity ומנהלת את מהלך משחק השש-בש.

    private Game game;
    // אובייקט הלוגיקה של המשחק (מכיל את מצב הלוח, התורות, האכילות וכו').

    private NetworkManager networkManager;
    // רכיב האחראי על ניהול התקשורת מול מסד הנתונים בזמן אמת (Firebase) עבור משחק אונליין.

    private CustomSurfaceView gameView;
    // רכיב תצוגה מותאם אישית (SurfaceView) האחראי על ציור הלוח והחיילים על המסך ברמת ביצועים גבוהה.

    private boolean isOnlineMode = false;
    // משתנה בוליאני המסמן האם המשחק הנוכחי מתנהל ברשת (אונליין) או מקומית.

    private String currentUsername = "אני";
    // משתנה השומר את שם המשתמש הנוכחי (נשלף מה-Firebase).

    private boolean isGameOverHandled = false;
    // משתנה הגנה שנועד לוודא שתהליך סיום המשחק (שמירת תוצאה, הצגת דיאלוג) יתבצע פעם אחת בלבד.

    private boolean isNameSynced = false;
    // משתנה המסמן האם השמות של השחקנים סונכרנו בהצלחה מול שרת האונליין.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // פונקציית מחזור החיים הראשונה שרצה בעת יצירת המסך. משמשת לאתחול רכיבים, מאזינים וטעינת נתונים ראשונית.
        super.onCreate(savedInstanceState);
        game = new Game();
        networkManager = new NetworkManager();
        isGameOverHandled = false;
        isNameSynced = false;

        loadCurrentUserName();

        game.setGameOverListener((winnerName, winTypeDesc) ->
                runOnUiThread(() -> {
                    if (!isGameOverHandled) {
                        isGameOverHandled = true;
                        if (isOnlineMode) {
                            networkManager.updateGameState(game);
                        }
                        saveGameResult(winnerName, winTypeDesc);
                        showWinnerDialog(winnerName, winTypeDesc);
                    }
                })
        );
        // הגדרת מאזין לסיום המשחק (Callback). ברגע שהלוגיקה מזהה מנצח, הפונקציה רצה על ה-UI Thread (שרשור התצוגה), מעדכנת את השרת, שומרת את התוצאה ומציגה הודעת ניצחון.

        // בדיקה אם הגענו מ-HomeActivity עם מצב משחק ספציפי
        String mode = getIntent().getStringExtra("mode");
        if (mode != null) {
            handleStartingMode(mode);
        } else {
            showLobbyDialog();
        }
        // שליפת מצב המשחק שנבחר במסך הקודם (יצירת חדר, הצטרפות או משחק מקומי). אם לא הועבר מצב, נפתח תפריט בחירה (Lobby).
    }

    private void handleStartingMode(String mode) {
        // פונקציה המקבלת את מצב המשחק ומאתחלת את חוקי המשחק בהתאם (למשל: מי שחקן 1, מי שחקן 2 והאם מדובר באונליין).
        switch (mode) {
            case "create":
                isOnlineMode = true;
                game.isOnlineMode_Internal = true;
                game.localPlayerIsP1 = true;
                game.p1Name = currentUsername;
                game.p2Name = "שחקן 2";
                startGameAsHost();
                break;
            case "join":
                isOnlineMode = true;
                game.isOnlineMode_Internal = true;
                game.localPlayerIsP1 = false;
                game.p1Name = "שחקן 1";
                game.p2Name = currentUsername;
                showJoinDialog();
                break;
            case "local":
                isOnlineMode = false;
                game.isOnlineMode_Internal = false;
                game.localPlayerIsP1 = true;
                game.p1Name = currentUsername;
                game.p2Name = "מחשב";
                startLocalSinglePlayer();
                break;
            default:
                showLobbyDialog();
                break;
        }
    }

    private void loadCurrentUserName() {
        // פונקציה השולפת את ה-UID של המשתמש המחובר מ-Firebase Auth, ולאחר מכן ניגשת ל-Firestore כדי להביא את שם המשתמש האמיתי שלו מה-Database.
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
        // פונקציה השומרת את היסטוריית המשחק (שם היריב, המנצח, סוג הניצחון וזמן המשחק) לתוך אוסף (Collection) בשם "games" במסד הנתונים Firestore - רלוונטי רק למשחקי אונליין.
        if (!isOnlineMode) return;

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        String opponentName = game.localPlayerIsP1 ? game.p2Name : game.p1Name;

        if (opponentName == null || opponentName.contains("שחקן") || opponentName.equals("מחכה ליריב...")) {
            return;
        }

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

        FirebaseFirestore.getInstance().collection("games").add(record);
    }

    private void showLobbyDialog() {
        // פונקציה המציגה תיבת דיאלוג (AlertDialog) לבחירת סוג המשחק במקרה שהמשתמש הגיע למסך ללא הגדרה מוקדמת.
        String[] options = {"צור משחק", "הצטרף למשחק", "משחק יחיד (מקומי)"};
        new AlertDialog.Builder(this)
                .setTitle("שש-בש")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) handleStartingMode("create");
                    else if (which == 1) handleStartingMode("join");
                    else handleStartingMode("local");
                })
                .setCancelable(false).show();
    }

    private void startLocalSinglePlayer() {
        // פונקציה המפעילה משחק מקומי על המכשיר ומציגה הודעת חיווי מהירה (Toast).
        startGameView();
        Toast.makeText(this, "משחק יחיד התחיל", Toast.LENGTH_SHORT).show();
    }

    private void startGameAsHost() {
        // פונקציה המייצרת קוד חדר אקראי בן 4 ספרות, פותחת חדר חדש ב-Firebase דרך ה-NetworkManager, ומגדירה מאזין לעדכונים מהשחקן השני שיצטרף.
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
        // פונקציה המציגה תיבת דיאלוג עם שדה טקסט שבה השחקן השני מקליד את קוד החדר כדי להתחבר למשחק קיים דרך ה-NetworkManager.
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("הכנס קוד")
                .setView(input)
                .setPositiveButton("הצטרף", (dialog, which) -> {
                    networkManager.joinGame(input.getText().toString(), data -> {
                        runOnUiThread(() -> {
                            game.updateFromMap(data);
                            if (!isNameSynced && isOnlineMode) {
                                isNameSynced = true;
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
        // פונקציה הבודקת בעת קבלת עדכון מהרשת האם המשחק הסתיים בצד השני, ואם כן מטפלת בסיום המשחק גם אצל השחקן הנוכחי.
        if (game.isGameOver && !isGameOverHandled) {
            isGameOverHandled = true;
            saveGameResult(game.winnerName, game.winTypeString);
            showWinnerDialog(game.winnerName, game.winTypeString);
        }
    }

    private void startGameView() {
        // פונקציה המאתחלת את רכיב התצוגה הגרפית (CustomSurfaceView) ומגדירה אותו כמסך הראשי של ה-Activity (במקום קובץ XML רגיל).
        gameView = new CustomSurfaceView(this, game);
        setContentView(gameView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // פונקציית מערכת שמירטת כל נגיעה במסך. אם מדובר במשחק אונליין והשחקן הרים את האצבע מהמסך (ACTION_UP), מצב המשחק העדכני נשלח מיד לענן כדי לסנכרן את היריב.
        if (isOnlineMode && !isGameOverHandled) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                networkManager.updateGameState(game);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void showWinnerDialog(String winnerName, String winTypeDesc) {
        // פונקציה המציגה תיבת דיאלוג חגיגית בסיום המשחק המפרטת מי המנצח ואיך הוא ניצח (רגיל/מרס), ומאפשרת חזרה לתפריט הראשי תוך סגירת המשחק הנוכחי מהזיכרון.
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