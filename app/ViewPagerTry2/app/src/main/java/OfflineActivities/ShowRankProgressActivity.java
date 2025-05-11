package OfflineActivities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
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
import android.widget.ImageView;
public class ShowRankProgressActivity extends AppCompatActivity {

    private ScrollView scrollView;
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
        createScreen();
        int shieldCounter = getIntent().getIntExtra("shield",-1);
        if(shieldCounter != -1)
        {

            animateShieldImg(shieldCounter);
            MakeViewPlayAudio.playRecording(this,"compose_rank_up.mp3",false);
            //animate the shield view and then take it of

        }
    }
    public void exitButtonShowRankClicked(View view)
    {
        this.finish();
    }
    private void setTextViewParams(TextView rankedUpTextView)
    {
        rankedUpTextView.setTextSize(40);
       //rankedUpTextView.setTextColor(R.color.red_orange);
        ConstraintLayout.LayoutParams textParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, // Width wraps content
                ConstraintLayout.LayoutParams.WRAP_CONTENT  // Height wraps content
        );

// Set constraints to center the rankedUpTextView on the shieldView
        textParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID; // Align start with parent's start
        textParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;     // Align end with parent's end
        textParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;     // Align top with parent's top
        textParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

// Set the layout params for rankedUpTextView
        rankedUpTextView.setLayoutParams(textParams);
    }
    private void animateShieldImg(int shieldCounter)
    {
        int imgId = XpPointsTracker.ranksImagesIds[shieldCounter];
        ImageView shieldView = new ImageView(this);
        TextView rankedUpTextView = new TextView(this);
        rankedUpTextView.setText("Ranked Up!");
        shieldView.setImageResource(imgId);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                800,  // Width in pixels
                800   // Height in pixels
        );


        ConstraintLayout layout = findViewById(R.id.constraintLayoutShowRank);
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID; // Align start with parent's start
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;     // Align end with parent's end
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;     // Align top with parent's top
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID; // Align bottom with parent's bottom
        shieldView.setLayoutParams(params);
        layout.addView(shieldView);
        setTextViewParams(rankedUpTextView);

        layout.addView(rankedUpTextView);

        // Animate shieldView to shrink and then grow back
        // Use post to ensure that the addition happens after the layout is ready
        shieldView.post(new Runnable() {
            @Override
            public void run() {

                shieldView.animate()
                        .scaleX(0.5f)    // Shrink to 50%
                        .scaleY(0.5f)    // Shrink to 50%
                        .setDuration(2000) // Duration for shrinking
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                shieldView.animate()
                                        .scaleX(1f) // Grow back to original size
                                        .scaleY(1f) // Grow back to original size
                                        .setDuration(2000) // Duration for growing
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Set the view to gone after the grow animation
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
    private void createScreen()
    {
        // layout.addView(view,0); - adding at the buttom of the layout
        // get levels and shields and curr amount
        int rank = XpPointsTracker.getCurrentRank();
        int level = XpPointsTracker.getCurrentLevel();
        int progressAmount = XpPointsTracker.getAmountForProgressBarNextLevel();
        boolean isMaxShieldUserCome = false;
        boolean isMaxLevel = false;
        boolean isSetScrollView = false;
        //needs array of imgs
        int[] imgsIds = XpPointsTracker.ranksImagesIds;

        LinearLayout layout = findViewById(R.id.linearLayoutShowRank);
        LinearLayout.LayoutParams paramters = new LinearLayout.LayoutParams(
                75,  // Width in pixels
                75   // Height in pixels
        );
        for(int i =0; i < imgsIds.length-1;i++)
        {
            if(i == rank)
            {
                isMaxShieldUserCome = true;
            }
            ImageView shieldView = new ImageView(this);
            shieldView.setImageResource(imgsIds[i]); // Set the drawable resource
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    200,  // Width in pixels
                    200   // Height in pixels
            );

            params.gravity = Gravity.CENTER; // Set the gravity for centering
            shieldView.setLayoutParams(params);

            //i will be each shield
            //View shieldView = findViewById(imgsIds[i]);
            layout.addView(shieldView,0);
            if(isMaxLevel == true&&isSetScrollView == false)
            {
                scrollView.post(() -> scrollView.scrollTo(0, shieldView.getTop()-20));
                isSetScrollView = true;
            }
            for(int j =0; j < XpPointsTracker.NUMBER_OF_LEVELS_IN_EACH_SHIELD; j++)
            {
                //j will be each level

                TextView currentLevelTextView = new TextView(this);
                LinearLayout.LayoutParams textViewparams = new LinearLayout.LayoutParams(
                        75,  // Width in pixels
                        75   // Height in pixels
                );
                textViewparams.gravity = Gravity.CENTER;
                currentLevelTextView.setLayoutParams(textViewparams);
                currentLevelTextView.setText(Integer.toString(j));
                currentLevelTextView.setGravity(Gravity.CENTER);
                currentLevelTextView.setBackgroundResource(R.drawable.circle_background);


                DividerView dividerView = new DividerView(this);
                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                        10,  // Width in pixels
                        150   // Height in pixels
                );
                params3.gravity = Gravity.CENTER;
                if(!isMaxLevel)
                {
                    textViewparams.height = 90;
                    textViewparams.weight = 150;
                    currentLevelTextView.setTextSize(20);
                    currentLevelTextView.setLayoutParams(textViewparams);
                    currentLevelTextView.setBackgroundResource(R.drawable.green_circle_backround);
                }
                if(isMaxShieldUserCome)
                {
                    if(j == level)
                    {
                        if(isMaxLevel != true)
                        {
                            //if the max level already has - skip this
                            isMaxLevel = true;
                            dividerView.setRedPercentage(progressAmount);

                            //scroll to this place


                            //scrollView.post(() -> scrollView.scrollTo(0, currentLevelTextView.getTop()-600));
                            //scrollView.post(() -> scrollView.fullScroll(currentHieght));
                        }

                    }
                }
                if(!isMaxLevel)
                {
                    dividerView.setRedPercentage(100);
                }
                dividerView.setLayoutParams(params3);


                layout.addView(currentLevelTextView,0);
                layout.addView(dividerView,0);
            }
        }
        DividerView dividerView = new DividerView(this);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                10,  // Width in pixels
                250   // Height in pixels
        );
        params3.gravity = Gravity.CENTER;
        if(isMaxShieldUserCome == false)
        {
            dividerView.setRedPercentage(100);
        }
        dividerView.setLayoutParams(params3);
        layout.addView(dividerView,0);


        ImageView shieldView = new ImageView(this);
        shieldView.setImageResource(imgsIds[imgsIds.length-1]); // Set the drawable resource
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                330,  // Width in pixels
                330   // Height in pixels
        );
        params.gravity = Gravity.CENTER; // Set the gravity for centering
        shieldView.setLayoutParams(params);

        layout.addView(shieldView,0);

        //scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}