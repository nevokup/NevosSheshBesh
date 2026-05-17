package com.example.nevos_shesh_besh.model;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {
// מחלקת הליבה של המשחק (המודל הלוגי) המנהלת את חוקי השש-בש.

    private static final String TAG = "Game";

    public enum WinType { REGULAR, MARS, KOOCHI }
    // מבנה נתונים מסוג Enum המגדיר את סוגי הניצחונות האפשריים בשש-בש.

    private boolean DeTests = false;
    // משתנה דגל (Flag) המשמש למצב בדיקות (דיבאג) - מאפשר לאתחל לוח קרוב לסיום כדי לבדוק הוצאת חיילים בקלות.

    public boolean localPlayerIsP1 = true;
    // משתנה המסמן האם המשתמש במכשיר הנוכחי הוא שחקן מספר 1 (לבן) או שחקן 2 (שחור).

    public boolean isOnlineMode_Internal = false;
    // מסמן פנימית בתוך המודל האם מדובר במשחק רשת או מקומי.

    public boolean isP1Turn;
    // משתנה בוליאני הקובע של מי התור הנוכחי (True לשחקן 1, False לשחקן 2).

    public boolean isGameOver = false;
    // דגל המציין האם המשחק הנוכחי הסתיים.

    public String winnerName = "";
    public String winTypeString = "";
    // משתנים השומרים את שם המנצח ואת התיאור המילולי של סוג הניצחון (למשל "מרס").

    public String p1Name = "שחקן 1";
    public String p2Name = "שחקן 2";

    public int[] board;
    // מערך בגודל 24 המייצג את משולשי הלוח. כל תא מכיל מספר המייצג כמות וחברה: ערך מתחת ל-100 מייצג חיילי שחקן 1, ערך מעל 100 מייצג חיילי שחקן 2 (למשל: 103 משמעותו 3 חיילים של שחקן 2).

    public int[] dice;
    // מערך בגודל 2 המכיל את ערכי שתי הקוביות הנוכחיות.

    public List<Integer> availableMoves;
    // רשימה דינמית השומרת את הצעדים שנותרו לשחקן לבצע בתור הנוכחי (במקרה של דאבל יהיו בה 4 ערכים).

    private Random random;
    // אובייקט להגרלת מספרים אקראיים (עבור הטלת קוביות).

    public int moveFrom = -1;
    // שומר את אינדקס המשולש ממנו השחקן בחר להזיז חייל (-1 אומר שלא נבחר חייל עדיין).

    public int movesMade;
    public int movesToDo;
    // משתנים העוקבים אחרי כמות הצעדים שבוצעו בפועל לעומת כמות הצעדים שחובה לבצע בתור הנוכחי.

    public int p1EatenCount;
    public int p2EatenCount;
    // משתנים המונים כמה חיילים אכולים יש לכל שחקן על הבר (האמצע).

    public int p1OffBoard;
    public int p2OffBoard;
    // משתנים המונים כמה חיילים כל שחקן כבר הוציא מחוץ ללוח.

    public interface GameOverListener {
        void onGameOver(String winnerName, String winTypeDesc);
    }
    private GameOverListener gameOverListener;
    // הגדרת ממשק (Interface) פנימי המאפשר למחלקות אחרות (כמו MainActivity) להאזין ולקבל התראה ברגע שיש ניצחון.

    public Game() {
        // בנאי המחלקה המאתחל את מערך הלוח, הקוביות, הגרלת התור הראשון והחיילים למצב התחלתי.
        board = new int[24];
        dice = new int[2];
        random = new Random();
        availableMoves = new ArrayList<>();
        initBoard();
        isP1Turn = true;
        rollDice();
        p1EatenCount = 0; p2EatenCount = 0;
        p1OffBoard = 0; p2OffBoard = 0;
    }

    private void initBoard() {
        // פונקציה המציבה את 15 החיילים של כל שחקן במיקומים המסורתיים של לוח השש-בש (או במצב בדיקה מיוחד אם דגל DeTests פעיל).
        board = new int[24];
        int[] initPositionsP1;
        int[] initPositionsP2;

        if (!DeTests) {
            initPositionsP1 = new int[]{2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, 0};
            initPositionsP2 = new int[]{0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2};
        } else {
            initPositionsP1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 3, 2, 3, 2};
            initPositionsP2 = new int[]{2, 3, 2, 3, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        }

        for (int i = 0; i < 24; i++) {
            if (initPositionsP1[i] > 0) board[i] = initPositionsP1[i];
            else if (initPositionsP2[i] > 0) board[i] = initPositionsP2[i] + 100;
        }
    }

    public Map<String, Object> toMap() {
        // פונקציית סריאליזציה הממירה את כל מצב המשחק הנוכחי למבנה נתונים של Map (מפתח-ערך) כדי שניתן יהיה להעלות אותו בקלות ל-Firebase Firestore.
        Map<String, Object> map = new HashMap<>();
        List<Integer> boardList = new ArrayList<>();
        for (int i : board) boardList.add(i);
        map.put("board", boardList);
        map.put("dice0", dice[0]);
        map.put("dice1", dice[1]);
        map.put("isP1Turn", isP1Turn);
        map.put("isGameOver", isGameOver);
        map.put("winnerName", winnerName);
        map.put("winTypeString", winTypeString);
        map.put("p1Eaten", p1EatenCount);
        map.put("p2Eaten", p2EatenCount);
        map.put("p1Off", p1OffBoard);
        map.put("p2Off", p2OffBoard);
        map.put("availableMoves", availableMoves);
        map.put("movesToDo", movesToDo);
        map.put("movesMade", movesMade);
        map.put("p1Name", p1Name);
        map.put("p2Name", p2Name);
        return map;
    }

    public void updateFromMap(Map<String, Object> map) {
        // פונקציית דה-סריאליזציה המקבלת Map המגיע משרת ה-Firebase, ומעדכנת לפיו את משתני המשחק המקומיים כדי להציג את הצעדים של השחקן השני בזמן אמת.
        if (map == null) return;
        try {
            List<Long> boardList = (List<Long>) map.get("board");
            if (boardList != null) {
                for (int i = 0; i < boardList.size(); i++) board[i] = boardList.get(i).intValue();
            }
            dice[0] = ((Long) map.get("dice0")).intValue();
            dice[1] = ((Long) map.get("dice1")).intValue();
            isP1Turn = (boolean) map.get("isP1Turn");
            isGameOver = (boolean) map.get("isGameOver");
            winnerName = (String) map.get("winnerName");
            winTypeString = (String) map.get("winTypeString");
            p1EatenCount = ((Long) map.get("p1Eaten")).intValue();
            p2EatenCount = ((Long) map.get("p2Eaten")).intValue();
            p1OffBoard = ((Long) map.get("p1Off")).intValue();
            p2OffBoard = ((Long) map.get("p2Off")).intValue();
            movesToDo = ((Long) map.get("movesToDo")).intValue();
            movesMade = ((Long) map.get("movesMade")).intValue();

            if (map.containsKey("p1Name")) p1Name = (String) map.get("p1Name");
            if (map.containsKey("p2Name")) p2Name = (String) map.get("p2Name");

            List<Long> avail = (List<Long>) map.get("availableMoves");
            availableMoves = new ArrayList<>();
            if (avail != null) for (Long l : avail) availableMoves.add(l.intValue());
        } catch (Exception e) { Log.e(TAG, "Update Error", e); }
    }

    public void rollDice() {
        // פונקציה המגרילה שתי קוביות (מספרים 1-6). אם יש "דאבל" (מספרים שווים), השחקן מקבל 4 מהלכים זהים. אם לא, הוא מקבל 2 מהלכים לפי ערכי הקוביות.
        dice[0] = random.nextInt(6) + 1;
        dice[1] = random.nextInt(6) + 1;
        availableMoves.clear();
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
        // פונקציית הליבה לניהול מהלך נגיעה במשולש. היא בודקת אם נבחר חייל, האם המהלך חוקי, האם השחקן נמצא בשלב הוצאת חיילים ("Bear Off"), ומבצעת את השינויים או מעבירה תור במידת הצורך.
        if (isGameOver) return false;
        if (isOnlineMode_Internal && (isP1Turn != localPlayerIsP1)) return false; // מניעת מהלכים מחוץ לתור באונליין

        if (moveFrom == index) { // ביטול בחירה של חייל בנגיעה חוזרת
            if (board[moveFrom] >= 1000) board[moveFrom] -= 1000;
            moveFrom = -1;
            return true;
        }

        if (canBearOff(isP1Turn) && moveFrom == -1) { // טיפול בהוצאת חיילים ישירות מהלוח
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

        if (!isLegalMove(index)) { // אם המהלך לא חוקי, מסמנים את המשולש החדש כבחירה הנוכחית במידה והוא מכיל חייל שלנו
            if (moveFrom != -1 && board[moveFrom] >= 1000) board[moveFrom] -= 1000;
            boolean isMyNewChecker = (isP1Turn && board[index] > 0 && board[index] < 100) ||
                    (!isP1Turn && board[index] >= 100 && board[index] < 1000);
            if (isMyNewChecker) {
                moveFrom = index;
                board[moveFrom] += 1000; // הוספת 1000 היא טריק תכנותי לסמן חזותית שהחייל נבחר
                return true;
            }
            return false;
        }

        executeMove(index); // ביצוע המהלך בפועל
        return true;
    }

    private void executeMove(int index) {
        // פונקציה פרטית המבצעת את שינוי המיקומים בפועל על הלוח: מעדכנת את כמות החיילים במשולש המקור והיעד, מטפלת באכילת חייל יריב בודד (בלוט) אם קיים, ומסירה את הקובייה שנוצלה מרשימת המהלכים.
        boolean isReEntering = (isP1Turn && p1EatenCount > 0) || (!isP1Turn && p2EatenCount > 0);
        if (isReEntering) { // כניסה מחדש של חייל אכול מהבר
            if (isP1Turn) {
                if (board[index] == 101) { p2EatenCount++; board[index] = 1; } // אכילת חייל יריב
                else board[index]++;
                p1EatenCount--;
                availableMoves.remove(Integer.valueOf(index + 1));
            } else {
                if (board[index] == 1) { p1EatenCount++; board[index] = 101; } // אכילת חייל יריב
                else { if (board[index] == 0) board[index] = 100; board[index]++; }
                p2EatenCount--;
                availableMoves.remove(Integer.valueOf(24 - index));
            }
        } else { // תנועה רגילה על הלוח
            if (isP1Turn && board[index] == 101) { p2EatenCount++; board[index] = 0; }
            else if (!isP1Turn && board[index] == 1) { p1EatenCount++; board[index] = 0; }
            if (board[moveFrom] >= 1000) board[moveFrom] -= 1000;
            board[moveFrom]--;
            if (isP1Turn) board[index]++;
            else { if (board[index] == 0) board[index] = 100; board[index]++; }
            if (!isP1Turn && board[moveFrom] == 100) board[moveFrom] = 0;
            int distance = isP1Turn ? (index - moveFrom) : (moveFrom - index);
            availableMoves.remove(Integer.valueOf(distance));
        }
        movesMade++;
        moveFrom = -1;
        checkWinCondition();
        if (movesMade >= movesToDo || availableMoves.isEmpty()) endTurn();
    }

    private boolean isLegalMove(int index) {
        // פונקציית ולידציה הבודקת האם מהלך מסוים חוקי לפי חוקי השש-בש: בודקת חובה להכניס קודם חייל אכול, בודקת התאמה לערכי הקוביות, ומוודא שהמשולש המיועד אינו חסום על ידי שני חיילים או יותר של היריב.
        if (isP1Turn && p1EatenCount > 0) return (index + 1 <= 6) && availableMoves.contains(index + 1) && board[index] < 102;
        if (!isP1Turn && p2EatenCount > 0) return (24 - index <= 6) && availableMoves.contains(24 - index) && (board[index] < 1 || board[index] >= 100);
        if (moveFrom == -1) return false;
        int distance = isP1Turn ? index - moveFrom : moveFrom - index;
        if (!availableMoves.contains(distance)) return false;
        if (isP1Turn) return board[index] < 102 && index > moveFrom;
        return (board[index] < 2 || board[index] >= 100) && index < moveFrom;
    }

    private boolean canBearOff(boolean isP1) {
        // פונקציה הבודקת האם שחקן רשאי להתחיל להוציא חיילים מהלוח (תנאי: אין לו אף חייל אכול, וכל 15 חייליו נמצאים ברביע האחרון של הלוח - "בית").
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
        // פונקציה המחשבת ומחזירה מהי הקובייה המתאימה ביותר להוצאת החייל הנוכחי מהלוח, כולל התחשבות במצבים בהם ערך הקובייה גבוה מהמרחק הנדרש והחייל הוא הרחוק ביותר.
        int dist = isP1Turn ? (24 - index) : (index + 1);
        if (availableMoves.contains(dist)) return dist;
        int furthest = -1;
        if (isP1Turn) { for (int i=18; i<24; i++) if (board[i]>0 && board[i]<100) { furthest = 24-i; break; } }
        else { for (int i=5; i>=0; i--) if (board[i]>=100) { furthest = i+1; break; } }
        if (dist == furthest) { for (int move : availableMoves) if (move > dist) return move; }
        return null;
    }

    private void endTurn() {
        // פונקציה המעבירה את התור לשחקן השני, מאפסת מונים ומטילה את הקוביות מחדש עבור השחקן הבא.
        isP1Turn = !isP1Turn;
        movesMade = 0;
        moveFrom = -1;
        rollDice();
    }

    private void checkWinCondition() {
        // פונקציה הבודקת האם אחד השחקנים הגיע ל-15 חיילים מחוץ ללוח. במידה וכן, המשחק נעצר והיא מפעילה את חישוב סוג הניצחון ומפעילה את המאזין (Callback).
        if (p1OffBoard == 15) {
            isGameOver = true;
            winnerName = p1Name;
            calculateWinType(true);
            if (gameOverListener != null) gameOverListener.onGameOver(winnerName, winTypeString);
        } else if (p2OffBoard == 15) {
            isGameOver = true;
            winnerName = p2Name;
            calculateWinType(false);
            if (gameOverListener != null) gameOverListener.onGameOver(winnerName, winTypeString);
        }
    }

    private void calculateWinType(boolean p1Won) {
        // פונקציה המחשבת אלגוריתמית את סוג הניצחון: "רגיל" (אם היריב כבר הוציא לפחות חייל אחד), "מרס" (אם היריב לא הוציא אף חייל), או "מרס כוכבים" / "קוש" (אם היריב לא הוציא אף חייל ויש לו עדיין חייל אכול או חייל בתוך הבית של השחקן המנצח).
        int opponentOff = p1Won ? p2OffBoard : p1OffBoard;
        int opponentEaten = p1Won ? p2EatenCount : p1EatenCount;

        if (opponentOff > 0) {
            winTypeString = "רגיל";
        } else {
            boolean hasInOpponentHome = false;
            if (p1Won) {
                for (int i = 18; i < 24; i++) if (board[i] >= 100) hasInOpponentHome = true;
            } else {
                for (int i = 0; i < 6; i++) if (board[i] > 0 && board[i] < 100) hasInOpponentHome = true;
            }

            if (opponentEaten > 0 || hasInOpponentHome) {
                winTypeString = "מרס כוכבים";
            } else {
                winTypeString = "מרס";
            }
        }
    }

    public void setGameOverListener(GameOverListener l) { this.gameOverListener = l; }
    public int[] getDice() { return dice; }
    public int getP1EatenCount() { return p1EatenCount; }
    public int getP2EatenCount() { return p2EatenCount; }
}