
package ExercisesPages;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.R;

import NewViews.StatisticsButton;

import java.util.ArrayList;
import java.util.List;

import OfflineActivities.MenuOfflinePage;

/**
 * An {@link AppCompatActivity} that displays a summary of the user's performance
 * after completing a multiple-choice question exercise. It shows which words
 * were answered correctly and incorrectly, and provides options to play again
 * with the same or different words.
 */
public class SummerizeMultipleAnswersQuestionsPage extends AppCompatActivity {

    private ArrayList<AfterAnswerQuestionDetails> afterAnswerQuestionDetails = null;

    /**
     * Called when the activity is first created. Sets up the layout, enables
     * edge-to-edge display, retrieves the list of answered questions from the
     * intent, and then calls {@link #createButtons(AfterAnswerQuestionDetails[])}
     * to display the summary.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_summerize_multiple_answers_questions_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            afterAnswerQuestionDetails = (ArrayList<AfterAnswerQuestionDetails>) getIntent().getSerializableExtra("afterAnswerQuestionDetails");
            if (afterAnswerQuestionDetails != null) {
                AfterAnswerQuestionDetails[] listAnswers = afterAnswerQuestionDetails.toArray(new AfterAnswerQuestionDetails[0]);
                createButtons(listAnswers);
            }
        } catch (Exception e) {
            // Handle the case where the extra is not found or is of the wrong type
        }
    }

    /**
     * Adds a given {@link View} to the {@link LinearLayout} used to display the summary.
     * It applies layout parameters to the view, including margins. A special margin
     * is applied if the view is considered a title.
     *
     * @param view      The {@link View} to add to the layout.
     * @param isSpecial A boolean indicating if the view is a title requiring special margins.
     */
    private void addViewIntoLinear(View view, boolean isSpecial) {
        LinearLayout buttonsContainer = findViewById(R.id.linearLayoutButtonContainer4);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        if (isSpecial) {
            params.setMargins(10, 30, 10, 15);
        } else {
            params.setMargins(10, 20, 10, 0);
        }
        view.setLayoutParams(params);
        buttonsContainer.addView(view);
    }

    /**
     * Creates a {@link StatisticsButton} for a given {@link AfterAnswerQuestionDetails} object
     * and adds it to the summary if it matches the specified correctness (right or wrong).
     *
     * @param toCreateRight A boolean indicating whether to create a button for correctly answered words (true) or incorrectly answered words (false).
     * @param currentWord   The {@link AfterAnswerQuestionDetails} object containing the word and the user's answer.
     */
    private void createButton(boolean toCreateRight, AfterAnswerQuestionDetails currentWord) {
        if (toCreateRight != currentWord.getIsUserRight()) {
            return;
        }
        StatisticsButton btn = new StatisticsButton(this, currentWord);
        btn.setAllCaps(false);
        btn.setText(currentWord.getQuestionDetails().getWordProperties().getWord() + ": " + currentWord.getQuestionDetails().getWordProperties().getMeaning());
        btn.setBackgroundColor(Color.WHITE);
        addViewIntoLinear(btn, false);
    }

    /**
     * Extracts the {@link FinalWordProperties} array from an array of
     * {@link AfterAnswerQuestionDetails}. This is used to pass the questions
     * back to the game for playing again.
     *
     * @param words An array of {@link AfterAnswerQuestionDetails}.
     * @return An array of {@link FinalWordProperties} extracted from the input.
     */
    private FinalWordProperties[] getWordProArr(AfterAnswerQuestionDetails[] words) {
        FinalWordProperties[] arr = new FinalWordProperties[words.length];
        for (int i = 0; i < words.length; i++) {
            arr[i] = new FinalWordProperties(words[i].getQuestionDetails());
        }
        return arr;
    }

    /**
     * Creates the summary buttons and titles based on the array of
     * {@link AfterAnswerQuestionDetails}. It adds titles for "Words You Knew"
     * and "Words You Missed", followed by the respective word buttons. It also
     * adds buttons to play again with the same words or with different words.
     *
     * @param words An array of {@link AfterAnswerQuestionDetails} representing the answered questions.
     */
    private void createButtons(AfterAnswerQuestionDetails[] words) {
        LinearLayout buttonsContainer = findViewById(R.id.linearLayoutButtonContainer4);
        buttonsContainer.removeAllViews(); // Clear previous buttons

        // Title for correctly answered words
        TextView wordsKnow = new TextView(this);
        wordsKnow.setText("מילים שידעת:"); // Assuming Hebrew text
        wordsKnow.setTextSize(30);
        wordsKnow.setBackgroundColor(Color.GREEN);
        addViewIntoLinear(wordsKnow, true);
        for (AfterAnswerQuestionDetails word : words) {
            createButton(true, word);
        }

        // Title for incorrectly answered words
        TextView wordsDoesntKnow = new TextView(this);
        wordsDoesntKnow.setText("מילים שטעית:"); // Assuming Hebrew text
        wordsDoesntKnow.setTextSize(30);
        wordsDoesntKnow.setBackgroundColor(Color.RED);
        addViewIntoLinear(wordsDoesntKnow, true);
        for (AfterAnswerQuestionDetails word : words) {
            createButton(false, word);
        }

        FinalWordProperties[] previousQuestionsArr = getWordProArr(words);

        // Button to play again with the same words
        Button playAgainSame = new Button(this);
        playAgainSame.setText("play again with same words");
        playAgainSame.setBackgroundColor(Color.YELLOW);
        addViewIntoLinear(playAgainSame, true);
        playAgainSame.setOnClickListener(view -> {
            Intent intent = new Intent(SummerizeMultipleAnswersQuestionsPage.this, WordQuestionsPageMultipleAnswers.class);
            intent.putExtra("questions", previousQuestionsArr);
            startActivity(intent);
            finish();
        });

        // Button to play again with different words
        Button playAgainDifferent = new Button(this);
        playAgainDifferent.setText("play again with different words");
        playAgainDifferent.setBackgroundColor(Color.YELLOW);
        addViewIntoLinear(playAgainDifferent, true);
        playAgainDifferent.setOnClickListener(view -> {
            Intent intent = new Intent(SummerizeMultipleAnswersQuestionsPage.this, WordQuestionsPageMultipleAnswers.class);
            intent.putExtra("amount", words.length);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Handles the click event of the exit image button, navigating the user back
     * to the {@link MenuOfflinePage} and clearing the activity stack.
     *
     * @param view The clicked exit image {@link View}.
     */
    public void exitImgButtonClick5(View view) {
        Intent intent = new Intent(SummerizeMultipleAnswersQuestionsPage.this, MenuOfflinePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
