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
import com.google.android.material.textfield.TextInputLayout;
import NewViews.TextProgressBar;

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
    private Button m_answerBtn;
    private EditText m_userAnswerEditText;
    private TextView m_questionTextView;
    private TextInputLayout m_answerInputLayout;
    private TextInputLayout m_solutionInputLayout;
    private TextView m_counterTextView;
    private View m_wrongAnswerActions;
    private EditText m_solutionEditText;

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
        m_answerBtn = findViewById(R.id.sendAnswerButton);
        m_userAnswerEditText = findViewById(R.id.answerMeaningEditText);
        m_questionTextView = findViewById(R.id.WordQuestionTextView);
        m_answerInputLayout = findViewById(R.id.answerInputLayout);
        m_solutionInputLayout = findViewById(R.id.solutionInputLayout);
        m_counterTextView = findViewById(R.id.writeAnswerCounterTextView);
        m_wrongAnswerActions = findViewById(R.id.wrongAnswerActions);
        m_solutionEditText = findViewById(R.id.solutionMeaningEditText);

        getVarsAtStart();
        setEnterKeyListener(m_userAnswerEditText);
        updateUIForQuestion();
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

    public void setSendBtnClicked(View view) {
        m_answerInputLayout.setError(null);
        String answer = m_userAnswerEditText.getText().toString();
        String rightAnswer = m_isFlipped ? m_questions[m_counter].getWordProperties().getWord() : m_questions[m_counter].getWordProperties().getMeaning();

        if (answerIsInMeaning(answer, rightAnswer)) {
            whenUserAnsweredRight();
        } else {
            whenUserAnsweredWrong();
        }
    }

    private void whenUserAnsweredRight() {
        super.whenUserRight();
        m_answerInputLayout.setHelperText("Correct!");
        m_answerBtn.setEnabled(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateToNextQuestion();
            }
        }, 1500);
    }

    public void overrideButtonClicked(View view) {
        continueButtonClicked(view, true);
    }

    public void continueButtonClicked(View view) {
        continueButtonClicked(view, false);
    }

    public void continueButtonClicked(View view, boolean isTrue) {
        if (isTrue) {
            super.whenUserRight();
        } else {
            super.whenUserWrong();
        }
        updateToNextQuestion();
    }

    private void updateToNextQuestion() {
        this.m_counter++;
        if (m_counter == this.m_questions.length) {
            super.whenFinishQuestions();
            return;
        }
        updateUIForQuestion();
    }

    private void updateUIForQuestion() {
        String questionText = m_isFlipped ? m_questions[m_counter].getWordProperties().getMeaning() : m_questions[m_counter].getWordProperties().getWord();
        m_questionTextView.setText(questionText);
        m_userAnswerEditText.setText("");
        m_answerInputLayout.setError(null);
        m_answerInputLayout.setHelperText(null);
        m_solutionInputLayout.setVisibility(View.GONE);
        m_wrongAnswerActions.setVisibility(View.GONE);
        m_answerBtn.setVisibility(View.VISIBLE);
        m_answerBtn.setEnabled(true);

        m_counterTextView.setText((m_counter + 1) + "/" + m_questions.length);
    }

    private void whenUserAnsweredWrong() {
        m_answerInputLayout.setError("Incorrect Answer");
        m_solutionInputLayout.setVisibility(View.VISIBLE);
        String rightAnswer = m_isFlipped ? m_questions[m_counter].getWordProperties().getWord() : m_questions[m_counter].getWordProperties().getMeaning();
        m_solutionEditText.setText(rightAnswer);
        m_wrongAnswerActions.setVisibility(View.VISIBLE);
        m_answerBtn.setVisibility(View.GONE);
    }
    private String[] getAllMeaningsOfWords(String meaning) {
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