package com.example.nevos_shesh_besh;
// שיוך החבילה של האפליקציה.

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
// ייבוא רכיבי תצוגה ומתאמים מובנים של אנדרואיד.

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
// הגדרת מחלקת מתאם המשחקים, היורשת מתוך מחלקת הבסיס המובנית של ה-RecyclerView. היא עובדת עם מחזיק תצוגה פנימי שנקרא ViewHolder.

    private List<GameRecord> games;
    // הגדרת משתנה פרטי מסוג רשימה (List) שיכיל את אובייקטי רשומות המשחק שיוצגו ברשימה.

    public GameAdapter(List<GameRecord> games) { this.games = games; }
    // בנאי (Constructor) של המתאם המקבל מבחוץ (מתוך ה-Activity) את רשימת המשחקים העדכנית ומציב אותה במשתנה הפרטי שלו.

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // פונקציה מובנית של ה-Adapter שתפקידה לייצר פיזית את אובייקט "מחזיק התצוגה" (ViewHolder) עבור שורה בודדת ברשימה כשהיא נוצרת לראשונה.

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_item, parent, false);
        // שימוש ברכיב LayoutInflater של אנדרואיד. תפקידו לקחת קובץ עיצוב XML עצמאי (R.layout.game_item המייצג שורה בודדת של משחק) ולהפוך אותו לאובייקט תצוגה חי (View) בתוך קוד הג'אווה. הפעולה הזו נקראת ניפוח (Inflate).

        return new ViewHolder(v);
        // יצירה והחזרה של אובייקט ViewHolder חדש, כאשר אנחנו שולחים לו בבנאי את ה-View המנופח שייצרנו זה עתה.
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // הפונקציה החשובה ביותר במתאם. היא רצה אוטומטית בכל פעם ששורה מסוימת נכנסת למסך של המכשיר תוך כדי גלילה. היא מקבלת את מחזיק התצוגה של אותה שורה (holder) ואת המיקום הסידורי שלה ברשימה (position).

        GameRecord g = games.get(position);
        // שליפת אובייקט ה-GameRecord הספציפי שנמצא בתוך רשימת המשחקים במיקום ה-position הנוכחי.

        holder.opponent.setText(g.getOpponentName());
        // לקיחת שם היריב מתוך אובייקט המשחק והזרקתו לתוך רכיב הטקסט של היריב שנמצא בתוך ה-ViewHolder של השורה הזו.

        holder.winner.setText(g.getWinner());
        // לקיחת זהות המנצח מתוך אובייקט המשחק והזרקתו ל-TextView של המנצח בשורה.

        holder.type.setText(g.getWinType());
        // לקיחת סוג הניצחון מתוך אובייקט המשחק והזרקתו ל-TextView של סוג הניצחון בשורה.
    }

    @Override
    public int getItemCount() { return games.size(); }
    // פונקציה מובנית שחובה לממש. היא מחזירה ל-RecyclerView את כמות הפריטים הכוללת שיש ברשימה (games.size()). לפיה ה-RecyclerView יודע כמה שורות הוא צריך לייצר בסך הכל.

    static class ViewHolder extends RecyclerView.ViewHolder {
        // הגדרת תת-מחלקה פנימית וסטטית בשם ViewHolder היורשת מהמחלקה המובנית של אנדרואיד. תפקידה הבלעדי הוא "להחזיק" את רכיבי התצוגה של שורה בודדת בזיכרון, כדי שלא נצטרך לבצע פעולות findViewById יקרות ובזבזניות בכל פעם ששורה נגללת על המסך.

        TextView opponent, winner, type;
        // הגדרת שלושה רכיבי טקסט פנימיים שיחזיקו את התצוגות של השורה: שם יריב, מנצח וסוג משחק.

        ViewHolder(View v) {
            // בנאי של מחזיק התצוגה, המקבל את אובייקט ה-View המנופח של השורה הבודדת.

            super(v);
            // קריאה חובה לבנאי של מחלקת האב הרישמית (RecyclerView.ViewHolder).

            opponent = v.findViewById(R.id.tv_row_opponent);
            // ביצוע קישור חד-פעמי בין משתנה הטקסט הפנימי לבין רכיב ה-TextView האמיתי שנמצא בתוך ה-XML של השורה הבודדת, באמצעות הפעלת findViewById על אובייקט השורה (v).

            winner = v.findViewById(R.id.tv_row_winner);
            // קישור רכיב טקסט המנצח של השורה.

            type = v.findViewById(R.id.tv_row_type);
            // קישור רכיב טקסט סוג הניצחון של השורה.
        }
    }
}