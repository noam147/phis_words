package OfflineActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;

import com.example.viewpagertry2.UnitAndCategoryOfWord;
import NewViews.WordButton;
import com.google.android.material.textfield.TextInputEditText;

public class SeacrhWordInDbActivity extends AppCompatActivity {

    DBManager dbManager;
    FinalWordProperties[] words;
    String currRegexToSearch = "";


    int currAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seacrh_word_in_db);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setVeryTopOfPhoneColor();
        dbManager = new DBManager(this);
        dbManager.openDb();
        addTextWatcher();
        afterTextChanges();
    }

    private void addTextWatcher() {
        TextInputEditText emailEditText = findViewById(R.id.SearchWordEditText);

// Add a TextWatcher to listen for changes in the TextInputEditText
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called to notify you that the text is about to change
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called while the text is being changed
                // You can check the content of 's' for real-time changes
                System.out.println("Text changed to: " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                afterTextChanges();
                // This method is called after the text has changed
                // You can perform any final operations here
            }
        });
    }

    private void afterTextChanges() {
        TextInputEditText SearchWordEditText = findViewById(R.id.SearchWordEditText);
        currRegexToSearch = SearchWordEditText.getText().toString();
        words = dbManager.searchWordsBasedOnStart(SearchWordEditText.getText().toString(), OperationsAndOtherUsefull.AMOUNT_OF_WORDS_EACH_TIME_SEARCHING);
        currAmount = words.length;
        createButtons(words);
    }

    private void createButtons(FinalWordProperties[] words)
    {
        LinearLayout buttonsContainer = findViewById(R.id.linearLayoutButtonContainer2);

        // Clear previous buttons (optional, if you want to reset the container)
        buttonsContainer.removeAllViews();
        for (int i = 0; i < words.length; i++) {
            WordButton btn = new WordButton(this,words[i],this.dbManager);
            btn.setText(words[i].getWordProperties().getWord() + ": " + words[i].getWordProperties().getMeaning());


            btn.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // Button width
                    LinearLayout.LayoutParams.WRAP_CONTENT  // Button height
            );
            params.setMargins(10, 20, 10, 0); // Add some margins (optional)
            btn.setLayoutParams(params);
            btn.setAllCaps(false);//for some reason all the words were upper case
            setListenerToWordButton(btn);
            buttonsContainer.addView(btn);
        }
        if(words.length < OperationsAndOtherUsefull.AMOUNT_OF_WORDS_EACH_TIME_SEARCHING)
        {
            return;//we do not need this button
        }
        Button btn = new Button(this);
        btn.setText("click to see more results");
        btn.setBackgroundColor(Color.RED);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Button width
                LinearLayout.LayoutParams.WRAP_CONTENT  // Button height
        );
        params.setMargins(10, 40, 10, 0); // Add some margins (optional)
        btn.setLayoutParams(params);
        btn.setAllCaps(false);//for some reason all the words were upper case
        buttonsContainer.addView(btn);
        setListenerToMoreResultsButton(btn);


    }
    private void setListenerToMoreResultsButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currAmount += OperationsAndOtherUsefull.AMOUNT_OF_WORDS_EACH_TIME_SEARCHING;
                words = dbManager.searchWordsBasedOnStart(currRegexToSearch,currAmount);
                createButtons(words);
            }
        });
    }
    private void setListenerToWordButton(WordButton button) {
        int wordId = button.getFinalWordProperties().getWordProperties().getWord_id();
        String word = button.getFinalWordProperties().getWordProperties().getWord();
        int numOfStars= button.getFinalWordProperties().getUserDetailsOnWords().getAmountOfStars();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnitAndCategoryOfWord unitAndC = new UnitAndCategoryOfWord(wordId);
                Intent intent = new Intent(SeacrhWordInDbActivity.this, SortingWordsPage.class);
                intent.putExtra("unit",unitAndC.getUnit());
                intent.putExtra("category",unitAndC.getCategory());
                int finalAction = numOfStars;
                if (finalAction > OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS)
                {
                    finalAction = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
                }
                intent.putExtra("action",finalAction+2);//change
                intent.putExtra("wordToMark",word);


                startActivity(intent);
            }
        });
    }
    private void setVeryTopOfPhoneColor()
    {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.red_orange));
    }
    public void exitImgButtonClick(View view)
    {
        Intent intent = new Intent(SeacrhWordInDbActivity.this, MenuOfflinePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);//clear all previous pages
        startActivity(intent);

    }


}