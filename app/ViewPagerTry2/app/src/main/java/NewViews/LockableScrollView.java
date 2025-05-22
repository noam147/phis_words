
package NewViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * A custom {@link ScrollView} that allows enabling or disabling scrolling programmatically.
 * This can be useful in scenarios where the scrolling behavior needs to be controlled
 * based on certain conditions or user interactions.
 */
public class LockableScrollView extends ScrollView {

    private boolean scrollable = true; // By default, scrolling is enabled

    /**
     * Constructor for creating a LockableScrollView programmatically.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     */
    public LockableScrollView(Context context) {
        super(context);
    }

    /**
     * Constructor for creating a LockableScrollView from XML layout.
     *
     * @param context The Context the view is running in.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    public LockableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Overrides the onInterceptTouchEvent method to control whether touch events
     * should be intercepted by this ScrollView. When scrolling is disabled
     * ({@code scrollable} is false), this method returns false, preventing the
     * ScrollView from intercepting touch events, which then allows child views
     * to handle them.
     *
     * @param ev The motion event.
     * @return {@code true} if the ScrollView should intercept the touch event,
     * {@code false} otherwise.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't intercept touch events when scrolling is disabled
        return scrollable && super.onInterceptTouchEvent(ev);
    }

    /**
     * Overrides the onTouchEvent method to control whether touch events
     * should be handled by this ScrollView. When scrolling is disabled
     * ({@code scrollable} is false), this method returns false, effectively
     * preventing the ScrollView from scrolling in response to touch events.
     *
     * @param ev The motion event.
     * @return {@code true} if the ScrollView handled the touch event,
     * {@code false} otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Don't handle touch events when scrolling is disabled
        return scrollable && super.onTouchEvent(ev);
    }

    /**
     * Enables or disables the scrolling functionality of this LockableScrollView.
     * When set to false, the ScrollView will not respond to touch events for scrolling.
     *
     * @param enabled {@code true} to enable scrolling, {@code false} to disable it.
     */
    public void setScrollingEnabled(boolean enabled) {
        this.scrollable = enabled;
    }
}
