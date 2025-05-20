package ExercisesPages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

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

import android.view.View;

import java.util.ArrayList;

import OfflineActivities.SortingWordsPage;
import files.XpPointsTracker;

public class BaseActivityForGameQuestions extends AppCompatActivity {

    protected int m_amountOfQuestions;
    protected boolean m_isEnglish;
    protected int m_action;
    protected DBManager dbManager;

    protected int m_unit;
    protected int m_category;
    protected int m_counter =0;

    protected FinalWordProperties[] m_questions = null;
    protected ArrayList<AfterAnswerQuestionDetails> m_afterAnswerQuestionDetails = new ArrayList<>();

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
    protected void putVarsIntoIntent(Intent intent)
    {
        intent.putExtra("action", m_action);
        intent.putExtra("unit", m_unit);
        intent.putExtra("category", m_category);
        intent.putExtra("amount", m_amountOfQuestions);
        intent.putExtra("isEnglish", m_isEnglish);
    }
    private void atStartOfExercisesPages()
    {
        try {
            m_questions = (FinalWordProperties[])getIntent().getSerializableExtra("questions");
        }
        catch (Exception e)
        {
            m_questions = null;
        }

        m_amountOfQuestions = getIntent().getIntExtra("amount", OperationsAndOtherUsefull.BASE_AMOUNT_OF_QUESTIONS_IN_EXERCISE);
        m_isEnglish = getIntent().getBooleanExtra("isEnglish",true);
        m_unit = getIntent().getIntExtra("unit",0);
        m_category = getIntent().getIntExtra("category",0);
        m_action = getIntent().getIntExtra("action", OperationsAndOtherUsefull.ALL_WORDS_ACTION);
        dbManager = new DBManager(this);
        dbManager.openDb();

    }
    protected void activateVibrator()
    {
        /*//for later - page to set this
        Intent intent = new Intent("android.intent.action.SET_ALARM");
        intent.putExtra("android.intent.extra.alarm.HOUR", 8);
        intent.putExtra("android.intent.extra.alarm.MINUTES", 30);
        intent.putExtra("android.intent.extra.alarm.MESSAGE", "Time To Learn Words!");
        startActivity(intent);  // Start the intent to set the alarm*/
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(400);
        }
        // Vibrate for 500 milliseconds (0.5 seconds)
        //for notification - user made a room or something
        /*if (vibrator != null && vibrator.hasVibrator()) {
            long[] pattern = {0, 200, 100, 300};
            VibrationEffect effect = VibrationEffect.createWaveform(new long[]{0, 200, 100, 300}, -1);
            //vibrator.vibrate(effect);
            vibrator.vibrate(pattern,-1);
        }*/
    }
    private void updateDbWrongAnswer()
    {
        XpPointsTracker.addOrSubToProgressBar2(1,this);
        String word = m_questions[m_counter].getWordProperties().getWord();
        this.dbManager.updateAmountOfStarsAndKnowledge(word,false);
    }
    private void updateDbRightAnswer()
    {
        XpPointsTracker.addOrSubToProgressBar2(3,this);
        String word = m_questions[m_counter].getWordProperties().getWord();
        this.dbManager.updateAmountOfStarsAndKnowledge(word,true);
    }
    public void whenAudioImgButtonClicked(View view)
    {
        int wordId =m_questions[m_counter].getWordProperties().getWord_id();
        MakeViewPlayAudio.playRecordingOfWord(this,wordId);
    }
    protected void whenFinishQuestions()
    {
        if(m_unit != 0&& m_category!=0)
        {
            //if the user used a מיון
            Intent intent = new Intent(BaseActivityForGameQuestions.this, SortingWordsPage.class);
            putVarsIntoIntent(intent);
            startActivity(intent);
            finish();
            return;
        }
        Intent intent = new Intent(BaseActivityForGameQuestions.this, SummerizeMultipleAnswersQuestionsPage.class);
        intent.putExtra("afterAnswerQuestionDetails",this.m_afterAnswerQuestionDetails);
        startActivity(intent);
        finish();
    }
    protected void whenUserRight()
    {
        updateDbRightAnswer();
        //the db updated but the question instance did not
        this.m_questions[m_counter].getUserDetailsOnWords().setAmountOfStars(this.m_questions[m_counter].getUserDetailsOnWords().getAmountOfStars()+1);
        //this.m_questions[m_counter].getUserDetailsOnWords().setKnowledge_level(this.m_questions[m_counter].getUserDetailsOnWords().getAmountOfStars()+1);

        AfterAnswerQuestionDetails current = new AfterAnswerQuestionDetails(this.m_questions[m_counter],true);
        this.m_afterAnswerQuestionDetails.add(current);
    }
    protected void whenUserWrong()
    {
        activateVibrator();
        updateDbWrongAnswer();
        this.m_questions[m_counter].getUserDetailsOnWords().setAmountOfStars(this.m_questions[m_counter].getUserDetailsOnWords().getAmountOfStars()-1);
        AfterAnswerQuestionDetails current = new AfterAnswerQuestionDetails(this.m_questions[m_counter],false);
        this.m_afterAnswerQuestionDetails.add(current);
    }


}