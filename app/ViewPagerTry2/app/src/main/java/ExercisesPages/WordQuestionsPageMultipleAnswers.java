package ExercisesPages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.FinalWordProperties;

import OfflineActivities.MenuOfflinePage;

import com.example.viewpagertry2.MakeViewPlayAudio;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;
import OfflineActivities.SortingWordsPage;

import java.util.Random;

public class WordQuestionsPageMultipleAnswers extends BaseActivityForGameQuestions {

    //private int m_counter =0;
    //Question[] m_questions;
    private Button[] m_listOfAnswerButtons;
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


        getVarsAtStart();
         m_listOfAnswerButtons= new Button[]{
                findViewById(R.id.answerButton1),
                findViewById(R.id.answerButton2),
                findViewById(R.id.answerButton3),
                findViewById(R.id.answerButton4)
        };
        changeToNextQuestion();

        changeColorsOfButtons();
    }
    private void updateQuestionsTrackerTextView()
    {
        TextView questionTracker = findViewById(R.id.questionsCounterTextView);
        questionTracker.setText((this.m_counter+1)+"/"+this.m_questions.length);
    }
    public Button findBtnWithRightAnswer(Button[] btns)
    {
        for(int i =0; i <btns.length;i++)
        {
            String btnText = btns[i].getText().toString();
            if(btnText == m_questions[m_counter].getWordProperties().getMeaning()) {
                return btns[i];
            }
        }
        return null;


    }

    public void answerButtonClicked(View view)
    {
        disableAnswerButtonsTouch();
        Button btn = (Button) view;
        //Button rightBtn = null;
        String btnText = btn.getText().toString();
        boolean isWrong = false;
        if(btnText == m_questions[m_counter].getWordProperties().getMeaning())
        {
           // btn.animate();
            btn.setBackgroundColor(Color.GREEN);
        }
        else
        {
            btn.setBackgroundColor(Color.RED);
            isWrong = true;
        }
        if(isWrong)
        {
            super.whenUserWrong();
            Handler handler2 = new Handler();
            Button rightBtn = findBtnWithRightAnswer(m_listOfAnswerButtons);

            //continue..
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rightBtn.setBackgroundColor(Color.GREEN);
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn.setBackgroundColor(getResources().getColor(R.color.purple));
                            rightBtn.setBackgroundColor(getResources().getColor(R.color.purple));
                            changeToNextQuestion();
                        }
                    },1000);

                }
            }, 500);

        }
        else
        {
            super.whenUserRight();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btn.setBackgroundColor(getResources().getColor(R.color.purple));
                    //rightBtn.setBackgroundColor(getResources().getColor(R.color.purple));
                    changeToNextQuestion();

                }
            }, 500);
        }


        m_counter++;

        //int buttonNum = btnText.charAt(btnText.length()-1);
        //changeToNextQuestion();

    }
    private void changeToNextQuestion()
    {
        if(m_counter == m_questions.length)
        {
            super.whenFinishQuestions();
            //intent
            return;
        }
        updateQuestionsTrackerTextView();
        Question currentQuestion = (Question) m_questions[m_counter];
        TextView questionTextView = findViewById(R.id.questionTextView);
        questionTextView.setText(currentQuestion.getWordProperties().getWord());
        Random random = new Random();
        int randNum = random.nextInt(4);
        int wrongAnswersCounter =0;
        for(int i =0; i <currentQuestion.getAnswers().length+1;i++)
        {
            if(i == randNum)
            {
                m_listOfAnswerButtons[i].setText(currentQuestion.getWordProperties().getMeaning());
            }
            else
            {
                m_listOfAnswerButtons[i].setText(currentQuestion.getAnswers()[wrongAnswersCounter]);
                wrongAnswersCounter++;
            }

        }




        enAbleAnswerButtonsTouch();
    }



    private void getVarsAtStart()
    {

        //if from summerize or somewhere else we recived questions - add answers to them
        if(m_questions != null)
        {
            FinalWordProperties[] words = m_questions;
            OperationsAndOtherUsefull.shffuleArr(words);//shuffle the questions
            m_questions = OperationsAndOtherUsefull.getRandQuestions(m_isEnglish,words,dbManager);//convert to Question type
            return;
        }
        if(m_unit == 0 || m_category == 0)
        {

            FinalWordProperties[] words = dbManager.getRandomEnglishWords(m_amountOfQuestions,m_isEnglish);
            m_questions = OperationsAndOtherUsefull.getRandQuestions(m_isEnglish,words,dbManager);
        }
        else
        {

            // FinalWordProperties[] words = dbManager.getWordsOfUnit(unit,category,m_isEnglish);
            //with current mode of words
            FinalWordProperties[] words = OperationsAndOtherUsefull.getWordsOfUnitByAction(m_unit,m_category,m_isEnglish,m_action,dbManager);
            OperationsAndOtherUsefull.shffuleArr(words);//shuffle the questions
            m_questions = OperationsAndOtherUsefull.getRandQuestions(m_isEnglish,words,dbManager);
        }

    }
    private Question[] getQuestionsArr()
    {
        Question[] questionsArr = new Question[m_amountOfQuestions];
        // populate the array with your logic
        return questionsArr;
    }
    private void changeColorsOfButtons() {
        // Loop through the buttons and increase their height
        for (Button button : m_listOfAnswerButtons) {
            // Create layout params to modify the button height
            button.setBackgroundColor(getResources().getColor(R.color.purple));
        }
    }
    private void disableAnswerButtonsTouch()
    {
        for (Button button : m_listOfAnswerButtons) {
            button.setEnabled(false); // Disables the button
            button.setClickable(false); // Prevents the button from being clicked
            button.setFocusable(false);
        }
    }
    private  void enAbleAnswerButtonsTouch()
    {
        for (Button button : m_listOfAnswerButtons) {
            button.setEnabled(true);
            button.setClickable(true);
            button.setFocusable(true);
        }
    }
    public void exitImgButtonClick4(View view)
    {
        if(m_unit == 0 || m_category == 0)//if this is a noraml game
    {
        Intent intent = new Intent(WordQuestionsPageMultipleAnswers.this, MenuOfflinePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);//clear all previous pages
        startActivity(intent);
        finish();
        return;
    }
        else
    {
            //if the user choose this from a specific unit - return him to that unit.
            Intent intent = new Intent(WordQuestionsPageMultipleAnswers.this, SortingWordsPage.class);
            intent.putExtra("unit",m_unit);
            intent.putExtra("category",m_category);
            intent.putExtra("isEnglish",m_isEnglish);
            intent.putExtra("action",m_action);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);//clear all previous pages
            startActivity(intent);
            finish();
            return;
        }


    }

}

