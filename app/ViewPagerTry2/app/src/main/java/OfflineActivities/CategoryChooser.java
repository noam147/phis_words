package OfflineActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;

public class CategoryChooser extends AppCompatActivity {

    boolean isEnglish;
    int currunit;
    int currCategory;
    DBManager dbManager;
    int currAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_chooser);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbManager = new DBManager(this);
        dbManager.openDb();
        isEnglish = getIntent().getBooleanExtra("isEnglish", true);
        currunit = getIntent().getIntExtra("unit", 1);
        currCategory = getIntent().getIntExtra("category", 1);
        currAction = getIntent().getIntExtra("action", OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS);
        boolean isCategoryChoice = getIntent().getBooleanExtra("isCategoryChoice", true);
        int numOfButtons = 0;
        if(isCategoryChoice)
        {
            numOfButtons = dbManager.getCountOfCategories(isEnglish);;
        }
        else {
            numOfButtons = dbManager.getCountOfUnitsInACategory(isEnglish,currCategory);
        }
        createButtons(numOfButtons,isCategoryChoice);

    }

    public int getIntInAmountButtonText(View btn) {
        String text = ((Button)btn).getText().toString();
        String finalIntString = "";
        for (int i = 0; i < text.length(); i++) {
            if(text.charAt(i) == '\n')
            //the \n indicates that now new line with the amount of
                //words that user know starts
            {
               break;
            }
            if (text.charAt(i) >= '0' && text.charAt(i) <= '9') {
                finalIntString += text.charAt(i);
            }
        }
        try {
            int intText = Integer.parseInt(finalIntString.toString());
            return intText;
        } catch (NumberFormatException e) {
            // If conversion fails, return 1 as a default value
            return 1;
        }

    }

    private void setTextOnButtonWithCategoryChoice(Button btn,int index)
    {
        //index is curr category-1
        int currCategory = index+1;
        String text = "Level: " + (currCategory)+"\n";
        int wordsInCategory = OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY*OperationsAndOtherUsefull.WORDS_PER_UNIT;
        int wordsInCategoryUserKnow = dbManager.getCountOfWordsUserKnowInSpecificCategory(currCategory);
        text+=wordsInCategoryUserKnow+"/"+wordsInCategory;
        //check how many words knows in that level - category and how much sum
        btn.setText(text);
    }
    private void setTextOnButtonWithUnityChoice(Button btn,int index)
    {
        String text = "Unit: " + (index + 1)+"\n";

        //check how many words knows in that unit - category and how much sum
        int wordsUserKnow = dbManager.getCountOfWordsUserKnowBasedOnCategory(this.currCategory,index+1);
        //int wordsUserKnow = 999;
        int amountOfWords = OperationsAndOtherUsefull.WORDS_PER_UNIT;

        text += wordsUserKnow+"/"+amountOfWords;
        if(wordsUserKnow == amountOfWords)
        {
            text += "\uD83C\uDF89";//celebrate emoji
        }
        btn.setText(text);
    }
    private void createButtons(int numOfButtons, boolean isCategoryChoice) {
        LinearLayout buttonsContainer = findViewById(R.id.categoryButtonsManager);
        // Clear previous buttons (optional, if you want to reset the container)
        buttonsContainer.removeAllViews();

        for (int i = 0; i < numOfButtons; i++) {

            Button btn = new Button(this);
            btn.setSingleLine(false);
            if (isCategoryChoice) {
                Log.e("sigame","category"+i);
                setTextOnButtonWithCategoryChoice(btn,i);
                //btn.setText("Level: " + (i + 1));
            } else {
                setTextOnButtonWithUnityChoice(btn,i);
                //btn.setText("Unit: " + (i + 1));
            }
            btn.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // Button width
                    LinearLayout.LayoutParams.WRAP_CONTENT  // Button height
            );
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CategoryChooser.this, SortingWordsPage.class);
                    intent.putExtra("isEnglish", isEnglish);
                    intent.putExtra("action",currAction);
                    if (isCategoryChoice) {
                        intent.putExtra("unit", currunit);
                        int category = getIntInAmountButtonText(v);
                        intent.putExtra("category", category);
                    } else {
                        //unit change
                        intent.putExtra("category", currCategory);
                        int unit = getIntInAmountButtonText(v);
                        intent.putExtra("unit", unit);
                    }
                    finish();
                    startActivity(intent);
                }


            });
            params.setMargins(10, 20, 10, 0); // Add some margins (optional)
            btn.setLayoutParams(params);
            btn.setAllCaps(false);//for some reason all the words were upper case
            buttonsContainer.addView(btn);


        }
    }
    public void exitImgButtonClick(View view)
    {
        Intent intent = new Intent(CategoryChooser.this,SortingWordsPage.class);
        intent.putExtra("isEnglish", isEnglish);
        intent.putExtra("action",currAction);
        intent.putExtra("unit", currunit);
        intent.putExtra("category", this.currCategory);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);//clear all previous pages
        startActivity(intent);
        finish();

    }
}
