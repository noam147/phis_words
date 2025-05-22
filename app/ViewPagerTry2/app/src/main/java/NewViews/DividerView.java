
package NewViews;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * A custom {@link View} that draws a vertical divider line with a customizable
 * color, thickness, and a percentage of its length that can be colored red.
 * This can be used as a progress indicator or a visual separator.
 */
public class DividerView extends View {

    private int color = 0xFF000000; // Default color black
    private int thickness = 4; // Thickness of the line in pixels
    private int redPercentage = 0; // Percentage of the line to be red (0-100)
    private Paint paint; // Paint object for drawing

    /**
     * Constructor for creating a DividerView programmatically.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     */
    public DividerView(Context context) {
        super(context);
        init();
    }

    /**
     * Constructor for creating a DividerView from XML layout.
     *
     * @param context The Context the view is running in.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public DividerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor for creating a DividerView from XML layout with a default style.
     *
     * @param context The Context the view is running in.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view.
     */
    public DividerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initializes the Paint object used for drawing the divider.
     * This method is called by all constructors.
     */
    private void init() {
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Called when the view should render its content. This method draws the divider line.
     * It first draws a red portion from the bottom based on {@code redPercentage},
     * and then draws the main color for the remaining part of the line.
     *
     * @param canvas The canvas on which the view will be drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate the height of the red part based on the percentage
        int height = getHeight();
        int redHeight = (int) (height * (redPercentage / 100.0));

        // Draw the red part starting from the bottom
        if (redHeight > 0) {
            paint.setColor(Color.RED);
            // Draws a rectangle from (0, height - redHeight) to (thickness, height)
            canvas.drawRect(0, height - redHeight, thickness, height, paint);
        }

        // Draw the remaining part of the line above the red part
        paint.setColor(color);
        // Draws a rectangle from (0, 0) to (thickness, height - redHeight)
        canvas.drawRect(0, 0, thickness, height - redHeight, paint);
    }

    /**
     * Sets the percentage of the divider line that should be colored red.
     * A value of 0 means no red, 100 means the entire line is red.
     * Calling this method triggers a redraw of the view.
     *
     * @param percentage The percentage (0-100) to set for the red portion.
     */
    public void setRedPercentage(int percentage) {
        this.redPercentage = percentage;
        invalidate(); // Request a redraw because the visual appearance has changed.
    }

    /**
     * Sets the thickness of the divider line.
     * Calling this method triggers a layout update as the size of the view might change.
     *
     * @param thickness The desired thickness of the line in pixels.
     */
    public void setThickness(int thickness) {
        this.thickness = thickness;
        requestLayout(); // Request a layout update because the view's dimensions might have changed.
    }

    /**
     * Sets the main color of the divider line.
     * Calling this method triggers a redraw of the view.
     *
     * @param color The ARGB color integer to set for the line (e.g., {@link Color#BLACK}).
     */
    public void setColor(int color) {
        this.color = color;
        invalidate(); // Request a redraw because the visual appearance has changed.
    }
}
