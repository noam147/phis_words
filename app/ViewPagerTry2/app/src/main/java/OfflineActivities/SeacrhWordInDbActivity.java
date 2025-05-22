
        package OfflineActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;
import com.example.viewpagertry2.UnitAndCategoryOfWord;

import NewViews.WordButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * An {@link AppCompatActivity} that enables users to search for words within the
 * local database. As the user types in the search field, a list of matching
 * words is dynamically displayed as clickable buttons. Clicking a word button
 * navigates the user to the {@link SortingWordsPage} for that specific word's
 * unit and category.
 */
public class SeacrhWordInDbActivity extends AppCompatActivity {

    private DBManager dbManager;
    private FinalWordProperties[] words;
    private String currRegexToSearch = "";
    private int currAmount = 0; // Keeps track of the number of words currently loaded

    /**
     * Called when the activity is first created. Sets up the layout, initializes
     * the database manager, adds a text watcher to the search input, and performs
     * an initial search to display any matching words.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seacrh_word_in_db);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setVeryTopOfPhoneColor();
        dbManager = new DBManager(this);
        dbManager.openDb();
        addTextWatcher();
        afterTextChanges(); // Initial search
    }

    /**
     * Adds a {@link TextWatcher} to the {@link TextInputEditText} with the ID
     * {@code R.id.SearchWordEditText}. This listener triggers the {@link #afterTextChanges()}
     * method whenever the text in the search field changes, allowing for real-time search updates.
     */
    private void addTextWatcher() {
        TextInputEditText searchEditText = findViewById(R.id.SearchWordEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed for this implementation
            }

            @Override
            public void afterTextChanged(Editable s) {
                afterTextChanges(); // Perform search after text changes
            }
        });
    }

    /**
     * Retrieves the text from the search input field and uses it to query the
     * local database for matching words using {@link DBManager#searchWordsBasedOnStart(String, int)}.
     * The results are then passed to {@link #createButtons(FinalWordProperties[])} to be displayed.
     */
    private void afterTextChanges() {
        TextInputEditText searchEditText = findViewById(R.id.SearchWordEditText);
        currRegexToSearch = searchEditText.getText().toString();
        words = dbManager.searchWordsBasedOnStart(currRegexToSearch, OperationsAndOtherUsefull.AMOUNT_OF_WORDS_EACH_TIME_SEARCHING);
        currAmount = words.length;
        createButtons(words);
    }

    /**
     * Creates a list of {@link WordButton}s based on the provided array of
     * {@link FinalWordProperties}. Each button displays the word and its meaning.
     * An {@link android.view.View.OnClickListener} is attached to each button via
     * {@link #setListenerToWordButton(WordButton)} to handle clicks. If more
     * results are potentially available, a "click to see more results" button is added
     * with a listener set by {@link #setListenerToMoreResultsButton(Button)}.
     *
     * @param words An array of {@link FinalWordProperties} representing the search results.
     */
    private void createButtons(FinalWordProperties[] words) {
        LinearLayout buttonsContainer = findViewById(R.id.linearLayoutButtonContainer2);
        buttonsContainer.removeAllViews(); // Clear previous buttons

        for (int i = 0; i < words.length; i++) {
            WordButton btn = new WordButton(this, words[i], this.dbManager);
            btn.setText(words[i].getWordProperties().getWord() + ": " + words[i].getWordProperties().getMeaning());
            btn.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 20, 10, 0);
            btn.setLayoutParams(params);
            btn.setAllCaps(false);
            setListenerToWordButton(btn);
            buttonsContainer.addView(btn);
        }

        // Add "load more" button if there are more potential results
        if (words.length >= OperationsAndOtherUsefull.AMOUNT_OF_WORDS_EACH_TIME_SEARCHING) {
            Button loadMoreButton = new Button(this);
            loadMoreButton.setText("click to see more results");
            loadMoreButton.setBackgroundColor(Color.RED);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 40, 10, 0);
            loadMoreButton.setLayoutParams(params);
            loadMoreButton.setAllCaps(false);
            buttonsContainer.addView(loadMoreButton);
            setListenerToMoreResultsButton(loadMoreButton);
        }
    }

    /**
     * Sets an {@link android.view.View.OnClickListener} for the "click to see more results"
     * button. When clicked, it increases the number of words to fetch in the next search
     * and calls {@link #afterTextChanges()} to update the displayed list.
     *
     * @param button The "click to see more results" {@link Button}.
     */
    private void setListenerToMoreResultsButton(Button button) {
        button.setOnClickListener(v -> {
            currAmount += OperationsAndOtherUsefull.AMOUNT_OF_WORDS_EACH_TIME_SEARCHING;
            words = dbManager.searchWordsBasedOnStart(currRegexToSearch, currAmount);
            createButtons(words);
        });
    }

    /**
     * Sets an {@link android.view.View.OnClickListener} for a {@link WordButton}.
     * When a word button is clicked, this listener navigates the user to the
     * {@link SortingWordsPage} for the corresponding word, passing the unit,
     * category, and an action based on the word's current star rating.
     *
     * @param button The clicked {@link WordButton}.
     */
    private void setListenerToWordButton(WordButton button) {
        int wordId = button.getFinalWordProperties().getWordProperties().getWord_id();
        String word = button.getFinalWordProperties().getWordProperties().getWord();
        int numOfStars = button.getFinalWordProperties().getUserDetailsOnWords().getAmountOfStars();

        button.setOnClickListener(v -> {
            UnitAndCategoryOfWord unitAndC = new UnitAndCategoryOfWord(wordId);
            Intent intent = new Intent(SeacrhWordInDbActivity.this, SortingWordsPage.class);
            intent.putExtra("unit", unitAndC.getUnit());
            intent.putExtra("category", unitAndC.getCategory());
            int finalAction = numOfStars;
            if (finalAction > OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS) {
                finalAction = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
            }
            intent.putExtra("action", finalAction + 2); // Adjust action based on star count
            intent.putExtra("wordToMark", word);
            intent.putExtra("intentId", OperationsAndOtherUsefull.SEARCH_WORD_INTENT_ID);
            startActivity(intent);
        });
    }

    /**
     * Sets the color of the status bar at the very top of the phone to {@code R.color.red_orange}.
     */
    private void setVeryTopOfPhoneColor() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.red_orange));
    }

    /**
     * Handles the click event of the exit image button. Navigates the user back
     * to the {@link MenuOfflinePage}, clearing the activity history so that the
     * user cannot navigate back to this search activity by pressing the back button.
     *
     * @param view The clicked exit image {@link View}.
     */
    public void exitImgButtonClick(View view) {
        Intent intent = new Intent(SeacrhWordInDbActivity.this, MenuOfflinePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Called when the activity is no longer visible to the user. Closes the
     * database connection to release resources.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (dbManager != null) {
            dbManager.closeDb();
        }
    }

    /**
     * Called after {@link #onStop()} when the current activity is being
     * re-displayed to the user (the user has navigated back to it). Re-opens the
     * database connection to ensure it's available if needed.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (dbManager != null) {
            dbManager.openDb();
        }
    }
}
