
package ExercisesPages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.MakeViewPlayAudio;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;

import java.util.ArrayList;

import OfflineActivities.SortingWordsPage;
import files.XpPointsTracker;

/**
 * A base activity for all game-related question pages. It handles common functionalities
 * such as retrieving intent data, managing questions, updating the database based on
 * answers, providing feedback (vibration), playing audio for words, and navigating
 * to the next stage (either back to word sorting or to a summary page).
 */
public class BaseActivityForGameQuestions extends AppCompatActivity {

    protected int m_amountOfQuestions; // The total number of questions for the current exercise
    protected boolean m_isEnglish; // Indicates if the questions are in English
    protected int m_action; // The action that led to these questions (e.g., all words, marked words)
    protected DBManager dbManager; // Instance of the database manager

    protected int m_unit; // The current unit of words being tested (0 if not specified)
    protected int m_category; // The current category of words (0 if not specified)
    protected int m_counter = 0; // Counter for the current question index

    protected FinalWordProperties[] m_questions = null; // Array to hold the questions
    protected ArrayList<AfterAnswerQuestionDetails> m_afterAnswerQuestionDetails = new ArrayList<>(); // List to store details after each answer

    /**
     * Called when the activity is first created. Sets up edge-to-edge display and calls
     * {@link #atStartOfExercisesPages()} to initialize variables and data.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_base_for_game_questions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        atStartOfExercisesPages();
    }

    /**
     * Puts the current activity's relevant variables (action, unit, category, amount, isEnglish)
     * into a given {@link Intent}. This is useful when navigating to the next activity.
     *
     * @param intent The {@link Intent} to which the variables should be added as extras.
     */
    protected void putVarsIntoIntent(Intent intent) {
        intent.putExtra("action", m_action);
        intent.putExtra("unit", m_unit);
        intent.putExtra("category", m_category);
        intent.putExtra("amount", m_amountOfQuestions);
        intent.putExtra("isEnglish", m_isEnglish);
    }

    /**
     * Initializes the activity by retrieving data passed via the intent (questions array,
     * amount of questions, language, unit, category, action), and opens the database.
     */
    private void atStartOfExercisesPages() {
        try {
            m_questions = (FinalWordProperties[]) getIntent().getSerializableExtra("questions");
        } catch (Exception e) {
            m_questions = null; // Handle case where no questions array is passed
        }

        m_amountOfQuestions = getIntent().getIntExtra("amount", OperationsAndOtherUsefull.BASE_AMOUNT_OF_QUESTIONS_IN_EXERCISE);
        m_isEnglish = getIntent().getBooleanExtra("isEnglish", true);
        m_unit = getIntent().getIntExtra("unit", 0);
        m_category = getIntent().getIntExtra("category", 0);
        m_action = getIntent().getIntExtra("action", OperationsAndOtherUsefull.ALL_WORDS_ACTION);
        dbManager = new DBManager(this);
        dbManager.openDb();
    }

    /**
     * Activates the device's vibrator for a short duration to provide feedback for wrong answers.
     */
    protected void activateVibrator() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(400); // Vibrate for 400 milliseconds
        }
    }

    /**
     * Updates the database when the user answers a question incorrectly. It decreases
     * the XP progress and potentially reduces the star rating and knowledge level of the word.
     */
    private void updateDbWrongAnswer() {
        XpPointsTracker.addOrSubToProgressBar2(1, this); // Decrease XP progress
        String word = m_questions[m_counter].getWordProperties().getWord();
        this.dbManager.updateAmountOfStarsAndKnowledge(word, false); // Update word knowledge in DB
    }

    /**
     * Updates the database when the user answers a question correctly. It increases
     * the XP progress and potentially increases the star rating and knowledge level of the word.
     */
    private void updateDbRightAnswer() {
        XpPointsTracker.addOrSubToProgressBar2(3, this); // Increase XP progress
        String word = m_questions[m_counter].getWordProperties().getWord();
        this.dbManager.updateAmountOfStarsAndKnowledge(word, true); // Update word knowledge in DB
    }

    /**
     * Handles the click event of an audio image button. It plays the recording of the
     * word associated with the current question.
     *
     * @param view The clicked audio image {@link View}.
     */
    public void whenAudioImgButtonClicked(View view) {
        int wordId = m_questions[m_counter].getWordProperties().getWord_id();
        MakeViewPlayAudio.playRecordingOfWord(this, wordId); // Play audio for the current word
    }

    /**
     * Called when all the questions in the current exercise have been answered.
     * It navigates the user back to the {@link SortingWordsPage} if the questions
     * were accessed through word sorting (i.e., a specific unit and category were chosen).
     * Otherwise, it navigates to the {@link SummerizeMultipleAnswersQuestionsPage} to
     * show a summary of the user's performance.
     */
    protected void whenFinishQuestions() {
        if (m_unit != 0 && m_category != 0) {
            // If the user came from word sorting, go back there
            Intent intent = new Intent(BaseActivityForGameQuestions.this, SortingWordsPage.class);
            putVarsIntoIntent(intent);
            startActivity(intent);
            finish();
            return;
        }
        // If not from word sorting, go to the summary page
        Intent intent = new Intent(BaseActivityForGameQuestions.this, SummerizeMultipleAnswersQuestionsPage.class);
        intent.putExtra("afterAnswerQuestionDetails", this.m_afterAnswerQuestionDetails);
        startActivity(intent);
        finish();
    }

    /**
     * Called when the user answers a question correctly. It updates the database,
     * increments the star rating of the word in the current question instance,
     * and adds details of the answered question to the {@link #m_afterAnswerQuestionDetails} list.
     */
    protected void whenUserRight() {
        updateDbRightAnswer();
        // Update the star rating of the word in the current question instance
        this.m_questions[m_counter].getUserDetailsOnWords().setAmountOfStars(
                this.m_questions[m_counter].getUserDetailsOnWords().getAmountOfStars() + 1);
        // Create an AfterAnswerQuestionDetails object for the correct answer
        AfterAnswerQuestionDetails current = new AfterAnswerQuestionDetails(this.m_questions[m_counter], true);
        this.m_afterAnswerQuestionDetails.add(current);
    }

    /**
     * Called when the user answers a question incorrectly. It activates the vibrator,
     * updates the database, decrements the star rating of the word in the current
     * question instance, and adds details of the answered question to the
     * {@link #m_afterAnswerQuestionDetails} list.
     */
    protected void whenUserWrong() {
        activateVibrator();
        updateDbWrongAnswer();
        // Update the star rating of the word in the current question instance
        this.m_questions[m_counter].getUserDetailsOnWords().setAmountOfStars(
                this.m_questions[m_counter].getUserDetailsOnWords().getAmountOfStars() - 1);
        // Create an AfterAnswerQuestionDetails object for the wrong answer
        AfterAnswerQuestionDetails current = new AfterAnswerQuestionDetails(this.m_questions[m_counter], false);
        this.m_afterAnswerQuestionDetails.add(current);
    }
}
