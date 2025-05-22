
package OfflineActivities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.MakeViewPlayAudio;
import com.example.viewpagertry2.R;

import NewViews.DividerView;
import files.XpPointsTracker;

/**
 * An {@link AppCompatActivity} that displays the user's progress through ranks and levels.
 * When a user ranks up, this activity is launched to provide a visual representation
 * of their journey, including all the ranks and levels they have progressed through.
 * It also animates the newly achieved rank's shield.
 */
public class ShowRankProgressActivity extends AppCompatActivity {

    private ScrollView scrollView;

    /**
     * Called when the activity is first created. Sets up the layout, enables edge-to-edge
     * display, finds the exit button and the scroll view, and initiates the screen creation.
     * It also checks if a new rank was achieved and animates the corresponding shield.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_rank_progress);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayoutShowRank), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton exitButton = findViewById(R.id.exitButtonShowRank);
        exitButton.bringToFront();
        scrollView = findViewById(R.id.scrollViewShowRank);
        createScreen(); // Create the visual representation of ranks and levels
        int shieldCounter = getIntent().getIntExtra("shield", -1);
        if (shieldCounter != -1) {
            animateShieldImg(shieldCounter); // Animate the newly achieved shield
            MakeViewPlayAudio.playRecording(this, "compose_rank_up.mp3", false); // Play rank up sound
        }
    }

    /**
     * Handles the click event of the exit button, finishing this activity and returning
     * the user to the previous screen.
     *
     * @param view The clicked exit {@link ImageButton}.
     */
    public void exitButtonShowRankClicked(View view) {
        this.finish();
    }

    /**
     * Sets the layout parameters for the "Ranked Up!" {@link TextView} to ensure it is
     * centered on the screen.
     *
     * @param rankedUpTextView The {@link TextView} to apply the parameters to.
     */
    private void setTextViewParams(TextView rankedUpTextView) {
        rankedUpTextView.setTextSize(40);
        ConstraintLayout.LayoutParams textParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        textParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        textParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        textParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        rankedUpTextView.setLayoutParams(textParams);
    }

    /**
     * Animates the shield image corresponding to the newly achieved rank. It scales
     * the image down and then back to its original size with a fade-in effect. After
     * the animation, the shield image and the "Ranked Up!" text are set to {@link View#GONE}.
     *
     * @param shieldCounter The index of the newly achieved shield/rank.
     */
    private void animateShieldImg(int shieldCounter) {
        int imgId = XpPointsTracker.ranksImagesIds[shieldCounter];
        ImageView shieldView = new ImageView(this);
        TextView rankedUpTextView = new TextView(this);
        rankedUpTextView.setText("Ranked Up!");
        shieldView.setImageResource(imgId);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                800,
                800
        );
        ConstraintLayout layout = findViewById(R.id.constraintLayoutShowRank);
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        shieldView.setLayoutParams(params);
        layout.addView(shieldView);
        setTextViewParams(rankedUpTextView);
        layout.addView(rankedUpTextView);

        shieldView.post(new Runnable() {
            @Override
            public void run() {
                shieldView.animate()
                        .scaleX(0.5f)
                        .scaleY(0.5f)
                        .alpha(0f) // Fade out
                        .setDuration(2000)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                shieldView.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .alpha(1f) // Fade in
                                        .setDuration(2000)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                shieldView.setVisibility(View.GONE);
                                                rankedUpTextView.setVisibility(View.GONE);
                                            }
                                        })
                                        .start();
                            }
                        })
                        .start();
            }
        });
    }

    /**
     * Creates the visual representation of the user's rank progress. It iterates through
     * all available ranks and levels, displaying the shield image for each rank and
     * circular indicators for each level. The current rank and level are highlighted,
     * and the progress towards the next level is indicated on the divider connecting
     * the current level to the next. The scroll view is adjusted to bring the current
     * progress into view.
     */
    private void createScreen() {
        int rank = XpPointsTracker.getCurrentRank();
        int level = XpPointsTracker.getCurrentLevel();
        int progressAmount = XpPointsTracker.getAmountForProgressBarNextLevel();
        boolean isMaxShieldUserCome = false;
        boolean isMaxLevel = false;
        boolean isSetScrollView = false;
        int[] imgsIds = XpPointsTracker.ranksImagesIds;

        LinearLayout layout = findViewById(R.id.linearLayoutShowRank);

        for (int i = 0; i < imgsIds.length - 1; i++) {
            if (i == rank) {
                isMaxShieldUserCome = true;
            }
            ImageView shieldView = new ImageView(this);
            shieldView.setImageResource(imgsIds[i]);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    200,
                    200
            );
            params.gravity = Gravity.CENTER;
            shieldView.setLayoutParams(params);
            layout.addView(shieldView, 0);

            if (isMaxLevel && !isSetScrollView) {
                scrollView.post(() -> scrollView.scrollTo(0, shieldView.getTop() - 20));
                isSetScrollView = true;
            }

            for (int j = 0; j < XpPointsTracker.NUMBER_OF_LEVELS_IN_EACH_SHIELD; j++) {
                TextView currentLevelTextView = new TextView(this);
                LinearLayout.LayoutParams textViewparams = new LinearLayout.LayoutParams(
                        75,
                        75
                );
                textViewparams.gravity = Gravity.CENTER;
                currentLevelTextView.setLayoutParams(textViewparams);
                currentLevelTextView.setText(Integer.toString(j + 1)); // Levels are 1-indexed for display
                currentLevelTextView.setGravity(Gravity.CENTER);
                currentLevelTextView.setBackgroundResource(R.drawable.circle_background);

                DividerView dividerView = new DividerView(this);
                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                        10,
                        150
                );
                params3.gravity = Gravity.CENTER;

                if (!isMaxLevel) {
                    textViewparams.height = 90;
                    textViewparams.width = 90;
                    currentLevelTextView.setTextSize(20);
                    currentLevelTextView.setLayoutParams(textViewparams);
                    currentLevelTextView.setBackgroundResource(R.drawable.green_circle_backround);
                }

                if (isMaxShieldUserCome) {
                    if (j == level) {
                        if (!isMaxLevel) {
                            isMaxLevel = true;
                            dividerView.setRedPercentage(progressAmount);
                        }
                    }
                }

                if (!isMaxLevel) {
                    dividerView.setRedPercentage(100);
                }
                dividerView.setLayoutParams(params3);

                layout.addView(currentLevelTextView, 0);
                layout.addView(dividerView, 0);
            }
        }

        DividerView finalDividerView = new DividerView(this);
        LinearLayout.LayoutParams finalParams3 = new LinearLayout.LayoutParams(
                10,
                250
        );
        finalParams3.gravity = Gravity.CENTER;
        if (!isMaxShieldUserCome) {
            finalDividerView.setRedPercentage(100);
        }
        finalDividerView.setLayoutParams(finalParams3);
        layout.addView(finalDividerView, 0);

        ImageView finalShieldView = new ImageView(this);
        finalShieldView.setImageResource(imgsIds[imgsIds.length - 1]);
        LinearLayout.LayoutParams finalParams = new LinearLayout.LayoutParams(
                330,
                330
        );
        finalParams.gravity = Gravity.CENTER;
        finalShieldView.setLayoutParams(finalParams);
        layout.addView(finalShieldView, 0);

        if (isMaxLevel && !isSetScrollView) {
            scrollView.post(() -> scrollView.scrollTo(0, finalShieldView.getTop() - 20));
        }
    }
}
