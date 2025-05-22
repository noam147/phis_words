
package ExercisesPages;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;

import java.util.Random;

import OfflineActivities.MenuOfflinePage;
import OfflineActivities.SortingWordsPage;

/**
 * An activity that presents multiple-choice questions to the user to test their
 * knowledge of words. It extends {@link BaseActivityForGameQuestions} to inherit
 * common game functionalities. The user selects one of four answer buttons, and
 * the activity provides visual feedback on whether the answer was correct or
 * incorrect, updates the user's progress, and proceeds to the next question.
 */
public class WordQuestionsPageMultipleAnswers extends BaseActivityForGameQuestions {

    private Button[] m_listOfAnswerButtons;

    /**
     * Called when the activity is first created. Sets up the layout, initializes
     * the answer buttons, retrieves questions, and displays the first question.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_word_questions_page_multiple_answers);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getVarsAtStart(); // Retrieve intent variables and fetch questions
        m_listOfAnswerButtons = new Button[]{
                findViewById(R.id.answerButton1),
                findViewById(R.id.answerButton2),
                findViewById(R.id.answerButton3),
                findViewById(R.id.answerButton4)
        };
        changeToNextQuestion(); // Display the first question
        changeColorsOfButtons(); // Set initial colors of the answer buttons
    }

    /**
     * Updates the {@link TextView} that tracks the current question number and
     * the total number of questions.
     */
    private void updateQuestionsTrackerTextView() {
        TextView questionTracker = findViewById(R.id.questionsCounterTextView);
        questionTracker.setText((this.m_counter + 1) + "/" + this.m_questions.length);
    }

    /**
     * Finds the button that contains the correct answer among the list of answer buttons.
     *
     * @param btns An array of {@link Button} representing the answer choices.
     * @return The {@link Button} containing the correct meaning, or null if not found.
     */
    public Button findBtnWithRightAnswer(Button[] btns) {
        for (Button btn : btns) {
            if (btn.getText().toString().equals(m_questions[m_counter].getWordProperties().getMeaning())) {
                return btn;
            }
        }
        return null;
    }

    /**
     * Handles the click event of an answer button. It checks if the selected
     * answer is correct, provides visual feedback (green for correct, red for
     * incorrect), updates the user's progress and the database, and proceeds
     * to the next question after a short delay.
     *
     * @param view The clicked answer {@link Button}.
     */
    public void answerButtonClicked(View view) {
        disableAnswerButtonsTouch(); // Prevent further clicks during feedback
        Button clickedButton = (Button) view;
        boolean isWrong = !clickedButton.getText().toString().equals(m_questions[m_counter].getWordProperties().getMeaning());

        if (isWrong) {
            clickedButton.setBackgroundColor(Color.RED);
            super.whenUserWrong(); // Update database for a wrong answer
            Handler handler = new Handler();
            Button rightButton = findBtnWithRightAnswer(m_listOfAnswerButtons);
            handler.postDelayed(() -> {
                if (rightButton != null) {
                    rightButton.setBackgroundColor(Color.GREEN); // Show the correct answer
                    Handler handler2 = new Handler();
                    handler2.postDelayed(() -> {
                        clickedButton.setBackgroundColor(getResources().getColor(R.color.purple)); // Reset color
                        rightButton.setBackgroundColor(getResources().getColor(R.color.purple)); // Reset color
                        changeToNextQuestion(); // Load the next question
                    }, 1000);
                } else {
                    changeToNextQuestion(); // Proceed if correct button not found (shouldn't happen)
                }
            }, 500);
        } else {
            clickedButton.setBackgroundColor(Color.GREEN);
            super.whenUserRight(); // Update database for a correct answer
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                clickedButton.setBackgroundColor(getResources().getColor(R.color.purple)); // Reset color
                changeToNextQuestion(); // Load the next question
            }, 500);
        }
        m_counter++; // Increment the question counter
    }

    /**
     * Changes the displayed question and answer choices. If all questions have
     * been answered, it calls {@link #whenFinishQuestions()} to proceed to the
     * next stage. It updates the question text, randomly assigns the correct
     * answer to one of the buttons, and populates the other buttons with incorrect
     * answer choices.
     */
    private void changeToNextQuestion() {
        if (m_counter == m_questions.length) {
            super.whenFinishQuestions(); // Proceed when all questions are done
            return;
        }
        updateQuestionsTrackerTextView(); // Update the question counter display
        Question currentQuestion = (Question) m_questions[m_counter];
        TextView questionTextView = findViewById(R.id.questionTextView);
        questionTextView.setText(currentQuestion.getWordProperties().getWord()); // Set the question word
        Random random = new Random();
        int correctButtonIndex = random.nextInt(4); // Randomly choose a button for the correct answer
        int wrongAnswersCounter = 0;
        for (int i = 0; i < m_listOfAnswerButtons.length; i++) {
            if (i == correctButtonIndex) {
                m_listOfAnswerButtons[i].setText(currentQuestion.getWordProperties().getMeaning()); // Set correct answer
            } else {
                m_listOfAnswerButtons[i].setText(currentQuestion.getAnswers()[wrongAnswersCounter++]); // Set wrong answers
            }
        }
        enAbleAnswerButtonsTouch(); // Re-enable answer buttons for the new question
    }

    /**
     * Retrieves the questions for the exercise. If questions were passed directly
     * via intent (e.g., from the summary page), it uses those. Otherwise, it fetches
     * a set of random words based on the specified unit, category, and action,
     * and converts them into {@link Question} objects.
     */
    private void getVarsAtStart() {
        if (m_questions != null) {
            // Questions were passed directly, shuffle them and convert to Question type
            OperationsAndOtherUsefull.shffuleArr(m_questions);
            m_questions = OperationsAndOtherUsefull.getRandQuestions(m_isEnglish, m_questions, dbManager);
            return;
        }
        if (m_unit == 0 || m_category == 0) {
            // Get random words if no specific unit/category
            FinalWordProperties[] words = dbManager.getRandomEnglishWords(m_amountOfQuestions, m_isEnglish);
            m_questions = OperationsAndOtherUsefull.getRandQuestions(m_isEnglish, words, dbManager);
        } else {
            // Get words based on unit, category, and action
            FinalWordProperties[] words = OperationsAndOtherUsefull.getWordsOfUnitByAction(m_unit, m_category, m_isEnglish, m_action, dbManager);
            OperationsAndOtherUsefull.shffuleArr(words);
            m_questions = OperationsAndOtherUsefull.getRandQuestions(m_isEnglish, words, dbManager);
        }
    }

    /**
     * Sets the initial background color of all answer buttons to purple.
     */
    private void changeColorsOfButtons() {
        for (Button button : m_listOfAnswerButtons) {
            button.setBackgroundColor(getResources().getColor(R.color.purple));
        }
    }

    /**
     * Disables touch events for all answer buttons, typically used when providing feedback
     * after an answer is selected.
     */
    private void disableAnswerButtonsTouch() {
        for (Button button : m_listOfAnswerButtons) {
            button.setEnabled(false);
            button.setClickable(false);
            button.setFocusable(false);
        }
    }

    /**
     * Enables touch events for all answer buttons, used when a new question is loaded.
     */
    private void enAbleAnswerButtonsTouch() {
        for (Button button : m_listOfAnswerButtons) {
            button.setEnabled(true);
            button.setClickable(true);
            button.setFocusable(true);
        }
    }

    /**
     * Handles the click event of the exit image button. Navigates the user back
     * to the {@link MenuOfflinePage} if the game was started without a specific
     * unit or category. If the game was started from a specific unit, it navigates
     * back to the {@link SortingWordsPage} for that unit and category.
     *
     * @param view The clicked exit image {@link View}.
     */
    public void exitImgButtonClick4(View view) {
        Intent intent;
        if (m_unit == 0 || m_category == 0) {
            // Navigate to the main menu if no specific unit/category was chosen
            intent = new Intent(WordQuestionsPageMultipleAnswers.this, MenuOfflinePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            // Navigate back to the sorting page for the specific unit/category
            intent = new Intent(WordQuestionsPageMultipleAnswers.this, SortingWordsPage.class);
            intent.putExtra("unit", m_unit);
            intent.putExtra("category", m_category);
            intent.putExtra("isEnglish", m_isEnglish);
            intent.putExtra("action", m_action);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Consider if needed
        }
        startActivity(intent);
        finish();
    }
}
