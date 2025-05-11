package com.example.viewpagertry2;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import files.XpPointsTracker;

public class XpPointsAnimations {
    //this class can be also relevant to coins later
    private static void animateXpPoint(Context context, ConstraintLayout layout, float xStartPosition, float yStart, float xEnd, float yEnd,int duration)
    {
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


        imageView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        imageView.animate()
                                .x(xEnd) // target x position
                                .y(yEnd) // target y position
                                .setDuration(duration)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageView.setVisibility(View.GONE);

                                    }
                                })
                                .start();

                    }
                }
        );

    }
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
