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
        this.setScaleY(4f);
        init();
    }

    public TextProgressBar(Context context) {
        super(context);
        this.setScaleY(4f);
        init();
    }

    private void init() {
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK); // Set the text color
        textPaint.setTextSize(17); // Set the text size
        //textPaint.setTextScaleX(2);

        Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.font_for_progress_text);
         textPaint.setTypeface(typeface); // Sets default font in bold
        textPaint.setAntiAlias(true); // Smooths edges
        textPaint.setTextScaleX(3f); // Increase for wider text
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
