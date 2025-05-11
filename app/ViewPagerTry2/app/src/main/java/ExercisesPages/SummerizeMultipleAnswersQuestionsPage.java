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

import OfflineActivities.MenuOfflinePage;

public class SummerizeMultipleAnswersQuestionsPage extends AppCompatActivity {

    //assgin each exrecise with unic id
    private int intentOfExrecise;
    private ArrayList<AfterAnswerQuestionDetails> afterAnswerQuestionDetails = null;
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
            AfterAnswerQuestionDetails[] listAnswers = new AfterAnswerQuestionDetails[afterAnswerQuestionDetails.size()];
            listAnswers = afterAnswerQuestionDetails.toArray(listAnswers);
            createButtons(listAnswers);
        }
        catch (Exception e)
        {
            int a =0;
        }


    }
    private void hm()
    {
        //we want to create the scroll view with the buttons
        //but now the buttons will be or green or red - based on user rightWrong
        //
    }

    private void addViewIntoLinear(View view,boolean isSpecial)
    {
        LinearLayout buttonsContainer = findViewById(R.id.linearLayoutButtonContainer4);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Button width
                LinearLayout.LayoutParams.WRAP_CONTENT  // Button height
        );
        if(isSpecial)
        {
            params.setMargins(10, 30, 10, 15); // Add some margins (optional)
        }
        else
        {
            params.setMargins(10, 20, 10, 0); // Add some margins (optional)
        }
        view.setLayoutParams(params);
        buttonsContainer.addView(view);
    }
    private void createButton(boolean toCreateRight,AfterAnswerQuestionDetails currentWord)
    {
        if(toCreateRight != currentWord.getIsUserRight())
        {
            return;
        }
        StatisticsButton btn = new StatisticsButton(this,currentWord);
        btn.setAllCaps(false);
        btn.setText(currentWord.getQuestionDetails().getWordProperties().getWord()+": "+currentWord.getQuestionDetails().getWordProperties().getMeaning());
        btn.setBackgroundColor(Color.WHITE);
        addViewIntoLinear(btn,false);
    }
    private FinalWordProperties[] getWordProArr(AfterAnswerQuestionDetails[] words)
    {
        FinalWordProperties[] arr = new FinalWordProperties[words.length];
        for(int i =0; i < words.length;i++)
        {
            FinalWordProperties curr = new FinalWordProperties(words[i].getQuestionDetails());
            arr[i] = curr;
        }
        return arr;
    }
    private void createButtons(AfterAnswerQuestionDetails[] words)
    {
        LinearLayout buttonsContainer = findViewById(R.id.linearLayoutButtonContainer4);
        // Clear previous buttons (optional, if you want to reset the container)
        buttonsContainer.removeAllViews();
        //add title for know words
        TextView wordsKnow = new TextView(this);
        wordsKnow.setText("מילים שידעת:");
        wordsKnow.setTextSize(30);
        wordsKnow.setBackgroundColor(Color.RED);
        addViewIntoLinear(wordsKnow,true);
        for(int i =0; i <words.length;i++)
        {
            createButton(true,words[i]);
        }
        TextView wordsDoesntKnow = new TextView(this);
        wordsDoesntKnow.setText("מילים שטעית:");
        wordsDoesntKnow.setTextSize(30);
        wordsDoesntKnow.setBackgroundColor(Color.RED);
        addViewIntoLinear(wordsDoesntKnow,true);
        for(int i =0; i <words.length;i++)
        {
            createButton(false,words[i]);
        }
        FinalWordProperties[] previousQuestionsArr = getWordProArr(words);

        //btn - another game with same words
        Button playAgainSame = new Button(this);
        playAgainSame.setText("play again with same words");
        playAgainSame.setBackgroundColor(Color.GREEN);
        addViewIntoLinear(playAgainSame,true);
        playAgainSame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SummerizeMultipleAnswersQuestionsPage.this, WordQuestionsPageMultipleAnswers.class);
                intent.putExtra("questions",previousQuestionsArr);
                startActivity(intent);
                finish();
            }
        });
        //btn - another game with new words
        Button playAgainDiffrent = new Button(this);
        playAgainDiffrent.setText("play again with diffrent words");
        playAgainDiffrent.setBackgroundColor(Color.GREEN);
        addViewIntoLinear(playAgainDiffrent,true);
        playAgainDiffrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SummerizeMultipleAnswersQuestionsPage.this, WordQuestionsPageMultipleAnswers.class);
                intent.putExtra("amount",words.length);
                startActivity(intent);
                finish();
            }
        });
    }
    public void exitImgButtonClick5(View view)
    {
        Intent intent = new Intent(SummerizeMultipleAnswersQuestionsPage.this, MenuOfflinePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);//clear all previous pages
        startActivity(intent);

    }
}