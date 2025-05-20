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

public class WordQuestionsPageWriteAnswer extends BaseActivityForGameQuestions {

    //private int m_counter =0;
    //private FinalWordProperties[] m_wordsQuestions = null;
    private Button m_overrideBtn;
    private Button m_answerBtn;
    private Button m_continueBtn;
    private  EditText m_userAnswerEditText;
    private TextView m_questionTextView;
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

        m_overrideBtn.setVisibility(View.INVISIBLE);
        m_continueBtn.setVisibility(View.INVISIBLE);
        getVarsAtStart();
        setEnterKeyListener(m_userAnswerEditText);
        m_questionTextView.setTextSize(23);
        m_questionTextView.setText(this.m_questions[m_counter].getWordProperties().getWord());

        EditText solution  = findViewById(R.id.solutionMeaningEditText);
        solution.setEnabled(false);
    }
    private void setEnterKeyListener(EditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_SEND ||
                        actionId == EditorInfo.IME_ACTION_NEXT||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // User pressed the Enter key or equivalent action
                    String inputText = editText.getText().toString();
                    // Handle the action here (e.g., validate input or trigger some behavior)
                    setSendBtnClicked(null);
                    return true; // Return true if the event is handled
                }
                return false; // Return false if the event is not handled
            }
        });
    }



private void getVarsAtStart()
{
    if(m_questions != null)
    {
        return;
    }
    if(m_unit == 0 || m_category == 0)
    {
        //at the end we should put by action
        m_questions = dbManager.getRandomEnglishWords(m_amountOfQuestions,m_isEnglish);
    }
    else
    {
        m_questions = OperationsAndOtherUsefull.getWordsOfUnitByAction(m_unit,m_category,m_isEnglish,m_action,dbManager);
        OperationsAndOtherUsefull.shffuleArr(m_questions);//shuffle the questions
    }
}
public void exitImgBtnWriteAnswerClicked(View view)
{
    if(m_unit == 0 || m_category == 0)//if this is a noraml game
    {
        Intent intent = new Intent(WordQuestionsPageWriteAnswer.this, MenuOfflinePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);//clear all previous pages
        startActivity(intent);
        finish();
        return;
    }
    else
    {
        //if the user choose this from a specific unit - return him to that unit.
        Intent intent = new Intent(WordQuestionsPageWriteAnswer.this, SortingWordsPage.class);
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
private void onAnswerClicked(View view)
    {
        //get edit text disabled
        //get button text to i was correct/i was not correct
        //get button continue visible

    }
    private void continueButtonClicked()
    {
        //get edit text enabled
        //get button text to send answer
        //get button continue invisible
        //get question label to next one
    }
    private String getTextFromAnswerTextEdit()
    {
        EditText userAnswer =findViewById(R.id.answerMeaningEditText);
        return userAnswer.getText().toString();
    }
    public void setSendBtnClicked(View view)
    {
        EditText solution  = findViewById(R.id.solutionMeaningEditText);
        solution.setText(m_questions[m_counter].getWordProperties().getMeaning());

        String answer = getTextFromAnswerTextEdit();
        if(answerIsInMeaning(answer,m_questions[m_counter].getWordProperties().getMeaning()))
        {
            whenUserAnsweredRight();
        }
        else {
            whenUserAnsweredWrong();
        }
    }

  private void whenUserAnsweredRight()
  {
      super.whenUserRight();
      m_answerBtn.setBackgroundColor(Color.GREEN);
      //m_overrideBtn.setVisibility(View.VISIBLE);
      //m_overrideBtn.setText("Override - I Was Incorrect.");
        //time.sleep(0.5)
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
          @Override
          public void run() {
              updateToNextQuestion();

          }
      }, 1500);

  }
  public void overrideButtonClicked(View view)
  {
      //do that it will consider this as true
      continueButtonClicked(view,true);
  }
  public void continueButtonClicked(View view)
  {
      continueButtonClicked(view,false);
  }
  public void continueButtonClicked(View view,boolean isTrue)
  {

      if(isTrue)
      {
          super.whenUserRight();
      }
      else
      {super.whenUserWrong();}
      // continue will be just if user were wrong
      m_overrideBtn.setVisibility(View.INVISIBLE);
      m_continueBtn.setVisibility(View.INVISIBLE);
      updateToNextQuestion();

  }
  private void updateToNextQuestion()
  {
      m_answerBtn.setVisibility(View.VISIBLE);
      this.m_userAnswerEditText.setEnabled(true);
      m_answerBtn.setBackgroundColor(Color.BLACK);
      this.m_counter++;
      if(m_counter == this.m_questions.length)
      {
          super.whenFinishQuestions();
          return;
      }
      m_questionTextView.setText(this.m_questions[m_counter].getWordProperties().getWord());
      m_userAnswerEditText.setText("");
      EditText solution  = findViewById(R.id.solutionMeaningEditText);
      solution.setText("");


  }

  private void whenUserAnsweredWrong()
  {

      //m_answerBtn.setBackgroundColor(Color.RED);
      super.activateVibrator();
      m_userAnswerEditText.setEnabled(false);
      m_answerBtn.setVisibility(View.GONE);
      m_overrideBtn.setVisibility(View.VISIBLE);
      m_overrideBtn.setText("Override - I Was Correct.");
      m_continueBtn.setVisibility(View.VISIBLE);
  }
  private String[] getAllMeaningsOfWords(String meaning)
  {
      String[] allMeanings = meaning.split(",");
      return allMeanings;
  }
  private boolean answerIsInMeaning(String userAnswer,String fullMeaning)
  {
      userAnswer = userAnswer.trim();
      String[] allMeanings = getAllMeaningsOfWords(fullMeaning);
      for (String meaning : allMeanings) {
          //equalsIgnoreCase = not case sensitive
          if (meaning.trim().equalsIgnoreCase(userAnswer)) {
              return true;
          }
      }
      return false;
  }

}