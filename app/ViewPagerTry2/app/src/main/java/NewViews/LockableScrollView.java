package NewViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class LockableScrollView extends ScrollView {

    private boolean scrollable = true;

    public LockableScrollView(Context context) {
        super(context);
    }

    public LockableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't intercept touch events when scrolling is disabled
        return scrollable && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Don't handle touch events when scrolling is disabled
        return scrollable && super.onTouchEvent(ev);
    }

    // Method to enable or disable scrolling
    public void setScrollingEnabled(boolean enabled) {
        this.scrollable = enabled;
    }
}

