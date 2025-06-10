
package OfflineActivities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.MakeViewPlayAudio;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;
import com.example.viewpagertry2.XpPointsAnimations;
import com.google.gson.Gson;

import API.API_Handler;
import API.DailyWord;
import API.ReqCallback;
import NewViews.TextProgressBar;
import files.HistoryOfUnitAndCategoryPrefs;
import files.UnitAndCaetogryHistoryHelper;
import files.XpPointsTracker;

import ExercisesPages.WordQuestionsPageMultipleAnswers;
import ExercisesPages.WordQuestionsPageWriteAnswer;

/**
 * The main menu for offline activities. Provides access to various features
 * such as starting games, sorting words, searching the database, playing writing games,
 * viewing marked words, and a shop for in-app currency (XP points). It also displays
 * the user's current rank and level progress and fetches a daily word from an API.
 */
public class MenuOfflinePage extends AppCompatActivity {

    private DBManager dbManager;
    private ImageView shieldImageView;
    private final int BUCKET_AMOUNT = 20;
    private final int HANDFUL_AMOUNT = 10;

    /**
     * Called when the activity is first created. Sets up the layout, initializes
     * the database manager, sets up listeners for shop buttons, updates the UI
     * with progress and a daily word, and starts background music.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_offline_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbManager = new DBManager(this);
        shieldImageView = findViewById(R.id.progressRankImageView);
        shieldImageView.post(this::setListernsToShopButtons); // Set listeners after view is laid out
        onStartPage(); // Update UI elements
        get_daily_word(); // Fetch and display the daily word
        MakeViewPlayAudio.playRecording(this, "composeapptry2melody.mp3", true); // Start background music
        // XpPointsTracker.resetAmount(this); // Uncomment to reset XP points for testing
    }

    /**
     * Fetches a daily word and its meaning from a remote API and displays it
     * in the {@link TextView} with the ID {@code R.id.daily_word_textView}.
     * Handles potential errors during the API request or JSON parsing.
     */
    private void get_daily_word() {
        TextView daily_text = findViewById(R.id.daily_word_textView);
        API_Handler.sendGetRequest("http://13.51.79.222:15555/daily_word", new ReqCallback() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                try {
                    DailyWord word_meaning = gson.fromJson(response, DailyWord.class);
                    daily_text.setText(word_meaning.word + ": " + word_meaning.meaning);
                } catch (Exception e) {

            }
            }

            @Override
            public void onFailure(Exception e) {
                //delays the dialog until the layout is ready
                findViewById(android.R.id.content).post(() -> {
                    new AlertDialog.Builder(MenuOfflinePage.this)
                            .setMessage("No internet connection. no daily word for you :(")
                            .setPositiveButton("OK", null)
                            .show();
                });
            }
        });
    }

    /**
     * Handles the click event of the rank image, navigating the user to the
     * {@link ShowRankProgressActivity} to view their full rank and level progress.
     *
     * @param view The clicked rank {@link ImageView}.
     */
    public void RankImageClicked(View view) {
        Intent intent = new Intent(MenuOfflinePage.this, ShowRankProgressActivity.class);
        startActivity(intent);
    }

    /**
     * Updates the progress bar and level text view based on the user's current
     * XP points. It also sets the image of the current rank.
     */
    private void setProgressBar() {
        TextProgressBar progressBar = findViewById(R.id.progressBar);
        TextView levelTextView = findViewById(R.id.currentLevelTextView);
        ImageView imageView = findViewById(R.id.progressRankImageView);

        XpPointsTracker.setCountersAtStart(this);
        int currentAmount = XpPointsTracker.getCurrentAmount();
        int progressBarValue = XpPointsTracker.getAmountForProgressBarNextLevel();
        progressBar.setProgress(progressBarValue);
        int nextLevelPoints = XpPointsTracker.getAmountForNextLevel();

        if (nextLevelPoints == -1) {
            progressBar.setProgress(100);
            progressBar.setText(Integer.toString(currentAmount));
            levelTextView.setVisibility(View.GONE); // Hide level text if max level reached
        } else {
            progressBar.setText(currentAmount + "/" + nextLevelPoints);
            levelTextView.setVisibility(View.VISIBLE);
            levelTextView.setText(String.valueOf(XpPointsTracker.getCurrentLevel()));
        }

        XpPointsTracker.getImgOfCurrentRank2(this, imageView);
    }

    /**
     * Updates UI elements on activity start, such as the progress bar and the
     * "Sort Words" button which displays the number of known words out of the total.
     */
    private void onStartPage() {
        setProgressBar();
        Button organizeBtn = findViewById(R.id.orgenizeWordsButtonMenu);
        int amountOfWordsKnow = dbManager.getCountOfWordsUserKnow();
        int amountOfAllWords = dbManager.getCountOfWords(true);

        organizeBtn.setSingleLine(false);
        organizeBtn.setText("Sort Words\n" + amountOfWordsKnow + "/" + amountOfAllWords);
    }

    /**
     * Handles the click event of the "Start Game" button, navigating the user
     * to the {@link WordQuestionsPageMultipleAnswers} activity.
     *
     * @param view The clicked "Start Game" {@link Button}.
     */
    public void gameButtonClicked(View view) {
        Intent intent = new Intent(MenuOfflinePage.this, WordQuestionsPageMultipleAnswers.class);
        startActivity(intent);
        // this.finish(); // Consider if you want to close the menu after starting the game
    }

    /**
     * Handles the click event of the "Sort Words" button, navigating the user
     * to the {@link SortingWordsPage}. It also passes the last viewed unit and
     * category to maintain user context.
     *
     * @param view The clicked "Sort Words" {@link Button}.
     */
    public void orgenizeButtonClicked(View view) {
        Intent intent = new Intent(MenuOfflinePage.this, SortingWordsPage.class);
        UnitAndCaetogryHistoryHelper categoryUnit = HistoryOfUnitAndCategoryPrefs.getUnitAndCategory(this);
        intent.putExtra("category", categoryUnit.getCategoryIndex());
        intent.putExtra("unit", categoryUnit.getUnitIndex());
        startActivity(intent);
        // this.finish(); // Consider if you want to close the menu after sorting words
    }

    /**
     * Handles the click event of the "Search Words" button, navigating the user
     * to the {@link SeacrhWordInDbActivity}.
     *
     * @param view The clicked "Search Words" {@link Button}.
     */
    public void searchWordsButtonClicked(View view) {
        Intent intent = new Intent(MenuOfflinePage.this, SeacrhWordInDbActivity.class);
        startActivity(intent);
        // this.finish(); // Consider if you want to close the menu after searching
    }

    /**
     * Handles the click event of the "Write Game" button, navigating the user
     * to the {@link WordQuestionsPageWriteAnswer} activity.
     *
     * @param view The clicked "Write Game" {@link Button}.
     */
    public void writeGameButtonClicked(View view) {
        Intent intent = new Intent(MenuOfflinePage.this, WordQuestionsPageWriteAnswer.class);
        startActivity(intent);
    }

    /**
     * Handles the click event of the "Marked Words" button, navigating the user
     * to the {@link SortingWordsPage} and filtering to show only marked words.
     *
     * @param view The clicked "Marked Words" {@link Button}.
     */
    public void markedWordButtonClicked(View view) {
        Intent intent = new Intent(MenuOfflinePage.this, SortingWordsPage.class);
        intent.putExtra("action", OperationsAndOtherUsefull.MARKED_WORDS_ACTION);
        startActivity(intent);
    }

    /**
     * Sets {@link android.view.View.OnClickListener}s to the shop buttons (single coin,
     * handful of coins, bucket of coins) to trigger the {@link #shopButtonClicked(View, int)}
     * method with the corresponding amount of XP points to add.
     */
    private void setListernsToShopButtons() {
        ImageView oneCoin = findViewById(R.id.imageView3);
        ImageView handfulCoins = findViewById(R.id.imageView2);
        ImageView bucketCoins = findViewById(R.id.imageView);

        oneCoin.setOnClickListener(view -> shopButtonClicked(view, 1));
        handfulCoins.setOnClickListener(view -> shopButtonClicked(view, HANDFUL_AMOUNT));
        bucketCoins.setOnClickListener(view -> shopButtonClicked(view, BUCKET_AMOUNT));
    }

    /**
     * Handles the click event of the "Reset Points" button, resetting the user's
     * XP points using {@link XpPointsTracker#resetAmount(android.content.Context)}
     * and updating the progress bar and level display.
     *
     * @param view The clicked "Reset Points" {@link Button}.
     */
    public void resetPointsBtnClicked(View view) {
        XpPointsTracker.resetAmount(this);
        TextView curr_level = findViewById(R.id.currentLevelTextView);
        curr_level.setVisibility(View.VISIBLE); // Ensure level text is visible after reset
        setProgressBar();
    }

    /**
     * Animates the addition of XP points when a shop button is clicked. It calls
     * {@link XpPointsAnimations#animateAndAddXpPoints(int, android.content.Context, android.view.ViewGroup, float, float, float, float)}
     * to create a visual animation of coins moving from the clicked button to the
     * rank shield, and updates the XP points accordingly. Finally, it updates the
     * progress bar.
     *
     * @param view   The clicked shop {@link ImageView}.
     * @param amount The amount of XP points to add.
     */
    private void shopButtonClicked(View view, int amount) {
        ConstraintLayout layout = findViewById(R.id.main);
        float endX = shieldImageView.getX() + shieldImageView.getWidth() / 2f;
        float endY = shieldImageView.getY() + shieldImageView.getHeight() / 2f;
        float xPosition = view.getX();
        float yPosition = view.getY();
        XpPointsAnimations.animateAndAddXpPoints(amount, view.getContext(), layout, xPosition, yPosition, endX, endY);
        setProgressBar();
    }
}
