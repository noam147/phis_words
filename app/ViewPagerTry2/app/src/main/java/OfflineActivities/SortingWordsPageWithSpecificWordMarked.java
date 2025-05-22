
package OfflineActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.R;

import NewViews.WordButton;

import java.util.ArrayList;

/**
 * An extension of {@link SortingWordsPage} that highlights and scrolls to a
 * specific word within the displayed list of words. The word to highlight
 * is passed via an intent extra with the key "wordToMark".
 */
public class SortingWordsPageWithSpecificWordMarked extends SortingWordsPage {
    private String wordToMark;

    /**
     * Called when the activity is first created. It sets the content view by
     * inflating the layout of the parent {@link SortingWordsPage} and then
     * inflating the specific layout for this activity on top of it. It also
     * retrieves the word to mark from the intent extras.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting_words_page); // Set parent layout
        getLayoutInflater().inflate(R.layout.activity_sorting_words_page_with_specific_word_marked, (ViewGroup) findViewById(R.id.main), true);
        wordToMark = getIntent().getStringExtra("wordToMark");
    }

    /**
     * Overrides the {@link SortingWordsPage#setIntentData(Intent)} method to ensure
     * that the intent data is retrieved from the original launching intent, not
     * the current intent of this activity.
     *
     * @param intent The intent containing the data (should be the original launching intent).
     */
    @Override
    protected void setIntentData(Intent intent) {
        Intent realIntent = getIntent();
        super.setIntentData(realIntent);
    }

    /**
     * Overrides the {@link SortingWordsPage#createButtons(FinalWordProperties[])} method
     * to first create the word buttons using the parent's implementation and then
     * iterate through them to find the button corresponding to the {@code wordToMark}.
     * If found, the button's background color is set to red, and the {@link ScrollView}
     * containing the buttons is scrolled to bring the marked button into the center of the view.
     *
     * @param words An array of {@link FinalWordProperties} to create buttons for.
     * @return An {@link ArrayList} of the created {@link WordButton}s.
     */
    @Override
    protected ArrayList<WordButton> createButtons(FinalWordProperties[] words) {
        ArrayList<WordButton> wordButtonArrayList = super.createButtons(words);
        ScrollView btnsScrollView = findViewById(R.id.buttonsScrollView);
        int yPosition = 0; // Tracks the cumulative vertical position of buttons

        for (WordButton btn : wordButtonArrayList) {
            String currWord = btn.getFinalWordProperties().getWordProperties().getWord();
            if (currWord.equals(wordToMark)) {
                btn.setBackgroundColor(Color.RED);
                // Calculate the absolute Y position of the button within the ScrollView
                int btnAbsoluteY = yPosition + btn.getTop();
                // Calculate the Y position to scroll to, centering the button in the ScrollView
                int scrollToY = btnAbsoluteY - (btnsScrollView.getHeight() / 2) + (btn.getHeight() / 2);
                // Smoothly scroll to the calculated Y position
                btnsScrollView.smoothScrollTo(0, Math.max(0, scrollToY)); // Ensure scrollToY is not negative
            }
            yPosition += btn.getHeight() + btn.getPaddingBottom(); // Update Y position for the next button
        }

        return wordButtonArrayList;
    }
}
