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

/**
 * A custom {@link ProgressBar} that allows displaying text directly on top of the progress bar.
 * This can be used to show numerical progress, percentages, or any relevant text alongside
 * the visual progress.
 */
public class TextProgressBar extends ProgressBar {
    private String text = ""; // The text to be displayed on the progress bar
    private Paint textPaint; // Paint object for drawing the text

    /**
     * Constructor for creating a TextProgressBar from XML layout.
     * Initializes the progress bar's vertical scale and calls {@link #init()}.
     *
     * @param context The Context the view is running in.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setScaleY(4f); // Increase the vertical scale of the progress bar
        init();
    }

    /**
     * Constructor for creating a TextProgressBar programmatically.
     * Initializes the progress bar's vertical scale and calls {@link #init()}.
     *
     * @param context The Context the view is running in.
     */
    public TextProgressBar(Context context) {
        super(context);
        this.setScaleY(4f); // Increase the vertical scale of the progress bar
        init();
    }

    /**
     * Initializes the {@link Paint} object used for drawing the text.
     * Sets the text color, size, typeface, anti-aliasing, and horizontal scale.
     */
    private void init() {
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK); // Set the default text color to black
        textPaint.setTextSize(17); // Set the default text size

        // Load a custom font from resources and apply it to the text paint
        Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.font_for_progress_text);
        textPaint.setTypeface(typeface);
        textPaint.setAntiAlias(true); // Enable anti-aliasing for smoother text edges
        textPaint.setTextScaleX(3f); // Increase for wider text, effectively stretching it horizontally
    }

    /**
     * Sets the text to be displayed on the progress bar.
     * After setting the text, {@link #invalidate()} is called to request a redraw
     * of the view, ensuring the new text is displayed.
     *
     * @param text The string text to display.
     */
    public void setText(String text) {
        this.text = text;
        invalidate(); // Request a redraw to update the text on the canvas
    }

    /**
     * Overrides the {@link ProgressBar#onDraw(Canvas)} method to draw the custom text
     * centered on the progress bar. This method is synchronized to prevent issues
     * with concurrent drawing operations.
     *
     * @param canvas The {@link Canvas} on which the view will be drawn.
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas); // Draw the default progress bar first

        // Calculate the X-coordinate to center the text horizontally.
        // It's half of the width minus half of the text's measured width.
        int x = (getWidth() / 2) - (int) (textPaint.measureText(text) / 2);

        // Calculate the Y-coordinate to center the text vertically.
        // It's half of the height minus half of the text's ascent and descent (to account for baseline).
        int y = (getHeight() / 2) - ((int) textPaint.ascent() + (int) textPaint.descent()) / 2;

        // Draw the text on the canvas at the calculated centered position.
        canvas.drawText(text, x, y, textPaint);
    }
}
