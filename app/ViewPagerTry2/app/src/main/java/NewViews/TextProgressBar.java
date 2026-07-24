package NewViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import androidx.core.content.res.ResourcesCompat;

import com.example.viewpagertry2.R;

public class TextProgressBar extends ProgressBar {
    private String text = "";
    private Paint textPaint;

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextProgressBar(Context context) {
        super(context);
        init();
    }

    private void init() {
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK); // Set the text color
        textPaint.setTextSize(14 * getResources().getDisplayMetrics().density); // Set the text size
        
        Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.font_for_progress_text);
        if (typeface != null) {
            textPaint.setTypeface(typeface);
        }
        textPaint.setAntiAlias(true); // Smooths edges
        textPaint.setTextScaleX(1.5f); // Reduced from 3f for better readability
    }

    public void setText(String text) {
        this.text = text;
        invalidate(); // Redraw to update the text
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the text in the center
        int x = (getWidth() / 2) - (int) (textPaint.measureText(text) / 2);
        int y = (getHeight() / 2) - ((int) textPaint.ascent() + (int) textPaint.descent()) / 2;
        //textPaint.setTextSize(20);
        canvas.drawText(text, x, y, textPaint);
    }
}
