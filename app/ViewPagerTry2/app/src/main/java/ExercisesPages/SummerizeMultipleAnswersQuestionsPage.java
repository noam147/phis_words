package ExercisesPages;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import com.google.android.material.card.MaterialCardView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;

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
    private void createButton(boolean toCreateRight, AfterAnswerQuestionDetails currentWord) {
        if (toCreateRight != currentWord.getIsUserRight()) {
            return;
        }

        View itemView = LayoutInflater.from(this).inflate(R.layout.item_summary_word, null);
        TextView wordText = itemView.findViewById(R.id.wordText);
        TextView meaningText = itemView.findViewById(R.id.meaningText);
        ImageView statusIcon = itemView.findViewById(R.id.statusIcon);
        View itemContainer = itemView.findViewById(R.id.itemContainer);

        wordText.setText(currentWord.getQuestionDetails().getWordProperties().getWord());
        meaningText.setText(currentWord.getQuestionDetails().getWordProperties().getMeaning());

        if (currentWord.getIsUserRight()) {
            statusIcon.setImageResource(R.drawable.baseline_full_star_24);
            statusIcon.setColorFilter(ContextCompat.getColor(this, R.color.green));
            itemContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.correct_bg));
        } else {
            statusIcon.setImageResource(R.drawable.baseline_close_24);
            statusIcon.setColorFilter(ContextCompat.getColor(this, R.color.red));
            itemContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.incorrect_bg));
        }

        itemView.setOnClickListener(v -> {
            // Optional: Handle click to show more details or play audio
        });

        addViewIntoLinear(itemView, false);
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
    private void createButtons(AfterAnswerQuestionDetails[] words) {
        LinearLayout buttonsContainer = findViewById(R.id.linearLayoutButtonContainer4);
        buttonsContainer.removeAllViews();

        // Add Section Header for Correct Answers
        TextView wordsKnow = new TextView(this);
        wordsKnow.setText("מילים שידעת");
        wordsKnow.setTextSize(20);
        wordsKnow.setPadding(40, 40, 40, 20);
        wordsKnow.setTextColor(ContextCompat.getColor(this, R.color.green));
        wordsKnow.setTypeface(null, android.graphics.Typeface.BOLD);
        buttonsContainer.addView(wordsKnow);

        for (int i = 0; i < words.length; i++) {
            createButton(true, words[i]);
        }

        // Add Section Header for Incorrect Answers
        TextView wordsDoesntKnow = new TextView(this);
        wordsDoesntKnow.setText("מילים שטעית");
        wordsDoesntKnow.setTextSize(20);
        wordsDoesntKnow.setPadding(40, 40, 40, 20);
        wordsDoesntKnow.setTextColor(ContextCompat.getColor(this, R.color.red));
        wordsDoesntKnow.setTypeface(null, android.graphics.Typeface.BOLD);
        buttonsContainer.addView(wordsDoesntKnow);

        for (int i = 0; i < words.length; i++) {
            createButton(false, words[i]);
        }

        FinalWordProperties[] previousQuestionsArr = getWordProArr(words);

        // Action Buttons Container
        LinearLayout actionsLayout = new LinearLayout(this);
        actionsLayout.setOrientation(LinearLayout.VERTICAL);
        actionsLayout.setPadding(32, 48, 32, 32);

        // btn - another game with same words
        Button playAgainSame = new com.google.android.material.button.MaterialButton(this);
        playAgainSame.setText("שחק שוב עם אותן מילים");
        playAgainSame.setAllCaps(false);
        LinearLayout.LayoutParams paramsSame = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        playAgainSame.setLayoutParams(paramsSame);
        actionsLayout.addView(playAgainSame);

        playAgainSame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SummerizeMultipleAnswersQuestionsPage.this, WordQuestionsPageMultipleAnswers.class);
                intent.putExtra("questions", previousQuestionsArr);
                startActivity(intent);
                finish();
            }
        });

        // btn - another game with new words
        Button playAgainDiffrent = new com.google.android.material.button.MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        playAgainDiffrent.setText("שחק שוב עם מילים חדשות");
        playAgainDiffrent.setAllCaps(false);
        LinearLayout.LayoutParams paramsDiff = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsDiff.topMargin = 16;
        playAgainDiffrent.setLayoutParams(paramsDiff);
        actionsLayout.addView(playAgainDiffrent);

        playAgainDiffrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SummerizeMultipleAnswersQuestionsPage.this, WordQuestionsPageMultipleAnswers.class);
                intent.putExtra("amount", words.length);
                startActivity(intent);
                finish();
            }
        });

        buttonsContainer.addView(actionsLayout);
    }
    public void exitImgButtonClick5(View view)
    {
        Intent intent = new Intent(SummerizeMultipleAnswersQuestionsPage.this, MenuOfflinePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);//clear all previous pages
        startActivity(intent);

    }
}