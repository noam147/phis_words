package NewViews;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DividerView extends View {

    private int color = 0xFF000000; // Default color black
    private int thickness = 4; // Thickness of the line
    private int redPercentage = 0; // Percentage of the line to be red
    private Paint paint;

    public DividerView(Context context) {
        super(context);
        init();
    }

    public DividerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DividerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate the height of the red part based on the percentage
        int height = getHeight();
        int redHeight = (int) (height * (redPercentage / 100.0));

        // Draw the red part starting from the bottom
        if (redHeight > 0) {
            paint.setColor(Color.RED);
            canvas.drawRect(0, height - redHeight, thickness, height, paint);
        }

        // Draw the remaining part of the line above the red part
        paint.setColor(color);
        canvas.drawRect(0, 0, thickness, height - redHeight, paint);
    }

    // Method to set the red percentage
    public void setRedPercentage(int percentage) {
        this.redPercentage = percentage;
        invalidate(); // Request a redraw
    }

    // Method to set the thickness of the line
    public void setThickness(int thickness) {
        this.thickness = thickness;
        requestLayout(); // Request a layout update
    }

    // Method to set the color of the line
    public void setColor(int color) {
        this.color = color;
        invalidate(); // Request a redraw
    }
}
