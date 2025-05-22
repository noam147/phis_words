
package ExercisesPages;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;

import OfflineActivities.MenuOfflinePage;
import OfflineActivities.SortingWordsPage;

/**
 * An activity that presents a word-writing question to the user.
 * The user types the meaning of the displayed word, and the activity
 * checks if the answer is correct. It provides feedback, updates
 * the user's progress, and proceeds to the next question.
 */
public class WordQuestionsPageWriteAnswer extends BaseActivityForGameQuestions {

    private Button m_overrideBtn; // Button to override a wrong answer
    private Button m_answerBtn; // Button to send the user's answer
    private Button m_continueBtn; // Button to continue to the next question
    private EditText m_userAnswerEditText; // EditText for the user to type their answer
    private TextView m_questionTextView; // TextView to display the question word

    /**
     * Called when the activity is first created. Sets up the layout,
     * initializes UI elements, retrieves questions, and displays the
     * first question.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_word_questions_page_write_answer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        m_overrideBtn = findViewById(R.id.overrideAnswerButton);
        m_answerBtn = findViewById(R.id.sendAnswerButton);
        m_continueBtn = findViewById(R.id.continueButton);
        m_answerBtn.setBackgroundColor(Color.BLACK);
        m_userAnswerEditText = findViewById(R.id.answerMeaningEditText);
        m_questionTextView = findViewById(R.id.WordQuestionTextView);

        m_overrideBtn.setVisibility(View.INVISIBLE); // Initially hide override button
        m_continueBtn.setVisibility(View.INVISIBLE); // Initially hide continue button
        getVarsAtStart(); // Retrieve intent variables and fetch questions
        setEnterKeyListener(m_userAnswerEditText); // Set listener for Enter key press
        m_questionTextView.setTextSize(23);
        m_questionTextView.setText(this.m_questions[m_counter].getWordProperties().getWord()); // Display the first question word

        EditText solution = findViewById(R.id.solutionMeaningEditText);
        solution.setEnabled(false); // Disable editing of the solution EditText
    }

    /**
     * Sets a listener to the EditText to detect when the user presses
     * the Enter key. When Enter is pressed, it simulates a click
     * on the send button.
     *
     * @param editText The EditText to attach the listener to.
     */
    private void setEnterKeyListener(EditText editText) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_SEND ||
                    actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // User pressed the Enter key or equivalent action
                setSendBtnClicked(null); // Simulate send button click
                return true; // Return true if the event is handled
            }
            return false; // Return false if the event is not handled
        });
    }

    /**
     * Retrieves the questions for the exercise. If questions were passed
     * directly via intent, it uses those. Otherwise, it fetches a set
     * of random words based on the specified unit, category, and action.
     */
    private void getVarsAtStart() {
        if (m_questions != null) {
            return; // Questions were passed directly
        }
        if (m_unit == 0 || m_category == 0) {
            // Get random words if no specific unit/category
            m_questions = dbManager.getRandomEnglishWords(m_amountOfQuestions, m_isEnglish);
        } else {
            // Get words based on unit, category, and action
            m_questions = OperationsAndOtherUsefull.getWordsOfUnitByAction(m_unit, m_category, m_isEnglish, m_action, dbManager);
            OperationsAndOtherUsefull.shffuleArr(m_questions); // Shuffle the questions
        }
    }

    /**
     * Handles the click event of the exit image button. Navigates the
     * user back to the {@link MenuOfflinePage} if the game was started
     * without a specific unit or category. If the game was started
     * from a specific unit, it navigates back to the
     * {@link SortingWordsPage} for that unit and category.
     *
     * @param view The clicked exit image {@link View}.
     */
    public void exitImgBtnWriteAnswerClicked(View view) {
        Intent intent;
        if (m_unit == 0 || m_category == 0) {
            // Navigate to the main menu if no specific unit/category was chosen
            intent = new Intent(WordQuestionsPageWriteAnswer.this, MenuOfflinePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            // Navigate back to the sorting page for the specific unit/category
            intent = new Intent(WordQuestionsPageWriteAnswer.this, SortingWordsPage.class);
            intent.putExtra("unit", m_unit);
            intent.putExtra("category", m_category);
            intent.putExtra("isEnglish", m_isEnglish);
            intent.putExtra("action", m_action);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Consider if needed
        }
        startActivity(intent);
        finish();
    }

    /**
     * Placeholder for handling the click event of an answer button.
     * Not currently used.
     *
     * @param view The clicked answer {@link View}.
     */
    private void onAnswerClicked(View view) {
        // Placeholder for future functionality
    }

    /**
     * Placeholder for continuing to the next question.
     * Not currently used.
     */
    private void continueButtonClicked() {
        // Placeholder for future functionality
    }

    /**
     * Gets the text entered by the user in the answer EditText.
     *
     * @return The text entered by the user.
     */
    private String getTextFromAnswerTextEdit() {
        EditText userAnswer = findViewById(R.id.answerMeaningEditText);
        return userAnswer.getText().toString();
    }

    /**
     * Handles the click event of the send button. It checks if the
     * user's answer is correct, displays the solution, and calls
     * the appropriate method to handle a correct or incorrect answer.
     *
     * @param view The clicked send {@link View}.
     */
    public void setSendBtnClicked(View view) {
        EditText solution = findViewById(R.id.solutionMeaningEditText);
        solution.setText(m_questions[m_counter].getWordProperties().getMeaning()); // Display the correct solution

        String answer = getTextFromAnswerTextEdit();
        if (answerIsInMeaning(answer, m_questions[m_counter].getWordProperties().getMeaning())) {
            whenUserAnsweredRight(); // Handle correct answer
        } else {
            whenUserAnsweredWrong(); // Handle incorrect answer
        }
    }

    /**
     * Handles the case when the user answers correctly. It updates
     * the UI to indicate a correct answer and proceeds to the next
     * question after a short delay.
     */
    private void whenUserAnsweredRight() {
        super.whenUserRight(); // Update database for a correct answer
        m_answerBtn.setBackgroundColor(Color.GREEN);
        Handler handler = new Handler();
        handler.postDelayed(this::updateToNextQuestion, 1500);
    }

    /**
     * Handles the click event of the override button. It treats
     * the user's answer as correct and proceeds to the next question.
     *
     * @param view The clicked override {@link View}.
     */
    public void overrideButtonClicked(View view) {
        // Treat the answer as correct
        continueButtonClicked(view, true);
    }

    /**
     * Handles the click event of the continue button. It proceeds
     * to the next question, treating the previous answer as either
     * correct or incorrect based on the 'isTrue' parameter.
     *
     * @param view  The clicked continue {@link View}.
     * @param isTrue True if the previous answer should be treated as correct, false otherwise.
     */
    public void continueButtonClicked(View view, boolean isTrue) {
        if (isTrue) {
            super.whenUserRight(); // Update database for a correct answer
        } else {
            super.whenUserWrong(); // Update database for an incorrect answer
        }
        m_overrideBtn.setVisibility(View.INVISIBLE); // Hide override button
        m_continueBtn.setVisibility(View.INVISIBLE); // Hide continue button
        updateToNextQuestion(); // Proceed to the next question
    }

    /**
     * Updates the UI to display the next question. It enables the
     * answer EditText, resets the button colors, increments the
     * question counter, and displays the new question word.
     */
    private void updateToNextQuestion() {
        m_answerBtn.setVisibility(View.VISIBLE); // Show the send button
        this.m_userAnswerEditText.setEnabled(true); // Enable the answer EditText
        m_answerBtn.setBackgroundColor(Color.BLACK); // Reset send button color
        this.m_counter++; // Increment the question counter
        if (m_counter == this.m_questions.length) {
            super.whenFinishQuestions(); // Proceed when all questions are done
            return;
        }
        m_questionTextView.setText(this.m_questions[m_counter].getWordProperties().getWord()); // Display the next question word
        m_userAnswerEditText.setText(""); // Clear the answer EditText
        EditText solution = findViewById(R.id.solutionMeaningEditText);
        solution.setText(""); // Clear the solution EditText
    }

    /**
     * Handles the case when the user answers incorrectly. It
     * provides visual feedback (vibration), disables the answer
     * EditText, and displays the override and continue buttons.
     */
    private void whenUserAnsweredWrong() {
        super.activateVibrator(); // Provide vibration feedback
        m_userAnswerEditText.setEnabled(false); // Disable the answer EditText
        m_answerBtn.setVisibility(View.GONE); // Hide the send button
        m_overrideBtn.setVisibility(View.VISIBLE); // Show the override button
        m_overrideBtn.setText("Override - I Was Correct.");
        m_continueBtn.setVisibility(View.VISIBLE); // Show the continue button
    }

    /**
     * Splits a meaning string into an array of possible meanings.
     *
     * @param meaning The full meaning string, potentially containing multiple meanings separated by commas.
     * @return An array of individual meanings.
     */
    private String[] getAllMeaningsOfWords(String meaning) {
        return meaning.split(",");
    }

    /**
     * Checks if the user's answer is present in the full meaning string.
     * It compares the user's answer (case-insensitively) against each
     * individual meaning in the full meaning string.
     *
     * @param userAnswer The user's typed answer.
     * @param fullMeaning The full meaning string, potentially containing multiple meanings separated by commas.
     * @return True if the user's answer is found in the full meaning, false otherwise.
     */
    private boolean answerIsInMeaning(String userAnswer, String fullMeaning) {
        userAnswer = userAnswer.trim();
        String[] allMeanings = getAllMeaningsOfWords(fullMeaning);
        for (String meaning : allMeanings) {
            if (meaning.trim().equalsIgnoreCase(userAnswer)) {
                return true; // User's answer matches a possible meaning
            }
        }
        return false; // User's answer does not match any meaning
    }
}
