package com.example.viewpagertry2;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import files.XpPointsTracker;

/**
 * This class provides functionality for animating XP point icons moving across the screen.
 * It includes methods to initiate a single XP point animation and to trigger multiple
 * animations with staggered delays, while also updating the XP points tracker.
 */
public class XpPointsAnimations {

    /**
     * Animates a single XP point icon moving from a starting position to an ending position
     * within a specified duration.
     *
     * @param context         The context of the application.
     * @param layout          The {@link ConstraintLayout} where the animation will occur.
     * @param xStartPosition The starting X-coordinate of the XP point icon.
     * @param yStart          The starting Y-coordinate of the XP point icon.
     * @param xEnd            The ending X-coordinate of the XP point icon.
     * @param yEnd            The ending Y-coordinate of the XP point icon.
     * @param duration        The duration of the animation in milliseconds.
     */
    private static void animateXpPoint(Context context, ConstraintLayout layout, float xStartPosition, float yStart, float xEnd, float yEnd, int duration) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.xp_point_coin);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                80,  // Width in pixels
                80   // Height in pixels
        );

        imageView.setLayoutParams(params);
        imageView.setTranslationX(xStartPosition);
        imageView.setTranslationY(yStart);
        layout.addView(imageView);

        imageView.post(() -> imageView.animate()
                .x(xEnd) // target x position
                .y(yEnd) // target y position
                .setDuration(duration)
                .withEndAction(() -> imageView.setVisibility(View.GONE))
                .start());
    }

    /**
     * Animates multiple XP point icons moving from a starting position to an ending position
     * with a staggered delay between each animation. It also updates the XP points tracker
     * by the specified amount.
     *
     * @param amount         The number of XP points to animate and add.
     * @param context         The context of the application.
     * @param layout          The {@link ConstraintLayout} where the animations will occur.
     * @param xStartPosition The starting X-coordinate for all XP point icons.
     * @param yStart          The starting Y-coordinate for all XP point icons.
     * @param xEnd            The ending X-coordinate for all XP point icons.
     * @param yEnd            The ending Y-coordinate for all XP point icons.
     */
    public static void animateAndAddXpPoints(int amount, Context context, ConstraintLayout layout, float xStartPosition, float yStart, float xEnd, float yEnd) {
        int initialDelay = 200; // Starting delay between animations
        int delayDecrease = 10; // Amount to decrease delay after each iteration
        int cumulativeDelay = 0; // Track the total delay for each animation call

        XpPointsTracker.addOrSubToProgressBar2(amount, context);

        for (int i = 0; i < amount; i++) {
            int delay = initialDelay - (delayDecrease * i); // Calculate the current delay
            if (delay < 20) delay = 20; // Ensure delay doesn’t go below 20 ms

            layout.postDelayed(() -> {
                animateXpPoint(context, layout, xStartPosition, yStart, xEnd, yEnd, 1500);
            }, cumulativeDelay); // Use the cumulative delay for each animation

            cumulativeDelay += delay; // Increase cumulative delay by the current delay
        }
    }
}