package OfflineActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.ButtonDraggableFuncs;
import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.FinalWordProperties;
import NewViews.LockableScrollView;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;
import NewViews.WordButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import ExercisesPages.WordQuestionsPageMultipleAnswers;
import files.HistoryOfUnitAndCategoryPrefs;

public class SortingWordsPage extends AppCompatActivity {
    private int KEEP_UNIT_AND_CATEGORY_AS_IS = -1;

    private boolean isUserWantMeanings = true;
    private int currAction;
    private boolean isEnglish;
    private int unit;
    private int category;
    private DBManager dbManager;
    private String wordToMark = "";

    private FinalWordProperties[] words;
    private int lenKnowWrods;
    private int lenDONOTKnowWords;
    private int lenDONOTDECIDEWords;


    // Open the database


    protected void setIntentData(Intent intent) {
        currAction = intent.getIntExtra("action", OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS);
        if(currAction == OperationsAndOtherUsefull.ALL_WORDS_ACTION)
        {
            currAction = OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS;
        }

        unit = intent.getIntExtra("unit", 1);
        wordToMark = intent.getStringExtra("wordToMark");
        category = intent.getIntExtra("category", 1);
        if(currAction != OperationsAndOtherUsefull.MARKED_WORDS_ACTION)
        {
            HistoryOfUnitAndCategoryPrefs.updateUnitAndCategory(this,category,unit);
        }
        isEnglish = intent.getBooleanExtra("isEnglish", true);
        isUserWantMeanings = intent.getBooleanExtra("isUserWantMeanings", true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sorting_words_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setVeryTopOfPhoneColor();
        setIntentData(getIntent());
        Switch withMeaningSwitch = findViewById(R.id.withMeaningSwich);
        withMeaningSwitch.setChecked(isUserWantMeanings);

        Button chosenButton = findChoseButton();
        if(chosenButton != null)
        {
            chosenButton.setTextSize(20);
            chosenButton.setBackgroundColor(Color.BLACK);
        }

        dbManager = new DBManager(this);
        dbManager.openDb();
         //words= dbManager.getRandomEnglishWords(10,isEnglish);
        if(currAction == OperationsAndOtherUsefull.MARKED_WORDS_ACTION)
        {
            hide_buttons();
            //when the marked - we so not want to interfare with the unit and category
            this.unit = KEEP_UNIT_AND_CATEGORY_AS_IS;
            this.category = KEEP_UNIT_AND_CATEGORY_AS_IS;
            words = dbManager.getMarkedWords();
        }
        else
        {
            words = dbManager.getWordsOfUnit(unit,category,isEnglish);
        }

        //sort the words alphabeticly:
        Arrays.sort(words, Comparator.comparing(word -> word.getWordProperties().getWord()));

        dbManager.closeDb();
        createButtons(words);
        //ButtonDraggableFuncs.makeButtonDraggableOnXAxsis(b1);
        setRedBackroundHieght();
        setButtonsNames();
        whenSwitchChange();



    }
    private void setVeryTopOfPhoneColor()
    {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.red_orange));
    }
    private void hide_buttons()
    {
        Button categoryBtn = findViewById(R.id.categoryChooseButton);
        Button unitBtn = findViewById(R.id.currentUnitButton);
        Button knowWordsButton = findViewById(R.id.wordsThatUserKnowButton);
        Button doNotknowWordsButton = findViewById(R.id.wordsThatUserDOESNTKnowButton);
        Button notDecideWordsButton = findViewById(R.id.orgnizeWordsButton);
        Button testOnUnit = findViewById(R.id.TestOnSpecificUnitButton);
        categoryBtn.setVisibility(View.GONE);
        unitBtn.setVisibility(View.GONE);
        categoryBtn.setVisibility(View.GONE);
        knowWordsButton.setVisibility(View.GONE);
        doNotknowWordsButton.setVisibility(View.GONE);
        notDecideWordsButton.setVisibility(View.GONE);
        testOnUnit.setVisibility(View.GONE);
    }
    public void setButtonsNames()
    {
        Button categoryBtn = findViewById(R.id.categoryChooseButton);
        Button unitBtn = findViewById(R.id.currentUnitButton);
        categoryBtn.setText("רמה: "+String.valueOf(category));
        //categoryBtn.setText(category);
        unitBtn.setText("יחידה למיון:"+String.valueOf(unit));

        Button knowWordsButton = findViewById(R.id.wordsThatUserKnowButton);
        knowWordsButton.setText(knowWordsButton.getText()+"\n"+String.valueOf(this.lenKnowWrods));
        Button doNotknowWordsButton = findViewById(R.id.wordsThatUserDOESNTKnowButton);
        doNotknowWordsButton.setText(doNotknowWordsButton.getText()+"\n"+String.valueOf(this.lenDONOTKnowWords));
        Button notDecideWordsButton = findViewById(R.id.orgnizeWordsButton);
        notDecideWordsButton.setText(notDecideWordsButton.getText()+"\n"+String.valueOf(this.lenDONOTDECIDEWords));
    }
    public void categoryChooseButtonClicked(View view)
    {
        Intent intent = new Intent(SortingWordsPage.this, CategoryChooser.class);
        intent.putExtra("isEnglish", isEnglish);
        intent.putExtra("action",currAction);
        intent.putExtra("isCategoryChoice",true);
        intent.putExtra("unit", unit);
        intent.putExtra("category", category);
        try {
            startActivity(intent);
        }
        catch (Exception e)
        {
            Button btn = findViewById(R.id.wordsThatUserDOESNTKnowButton);
            btn.setText(e.getMessage());
        }
    }

    private void whenSwitchChange() {
        Switch withMeaningSwitch = findViewById(R.id.withMeaningSwich);
        withMeaningSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Your code here
                if (isChecked) {
                    // Switch is ON
                    SortingWordsPage.this.isUserWantMeanings = true;  // Replace with your outer class name
                    createButtons(words);
                } else {
                    // Switch is OFF
                    SortingWordsPage.this.isUserWantMeanings = false; // Replace with your outer class name
                    createButtons(words);
                }
            }
        });
    }
    public void TestOnSpecifWordsInUnitButtonClicked(View view)
    {
        Intent intent = new Intent(SortingWordsPage.this, WordQuestionsPageMultipleAnswers.class);
        if(currAction == OperationsAndOtherUsefull.MARKED_WORDS_ACTION)
        {
            //if this in marked words just get the current words...
            intent.putExtra("questions",words);
        }
        else
        {
            intent.putExtra("unit", unit);
            intent.putExtra("category", category);
        }
        intent.putExtra("isEnglish", isEnglish);
        intent.putExtra("action",this.currAction);
        // intent.putExtra("action",currAction);
        startActivity(intent);
        finish();
    }
    public void TextButtonClicked(View view)
    {
        //maybe go to intent of option to exrecises with an X option
        Intent intent = new Intent(SortingWordsPage.this, WordQuestionsPageMultipleAnswers.class);
        intent.putExtra("unit", unit);
        intent.putExtra("category", category);
        intent.putExtra("isEnglish", isEnglish);
        //intent.putExtra("action",this.currAction); - the defult action is all words!
        startActivity(intent);
        //finish(); we do not want to finish
    }
    public void unitChooseButtonClicked(View view)
    {
        Intent intent = new Intent(SortingWordsPage.this,CategoryChooser.class);
        intent.putExtra("isEnglish", isEnglish);
        intent.putExtra("action",currAction);
        intent.putExtra("isCategoryChoice",false);
        intent.putExtra("unit", unit);
        intent.putExtra("category", category);
        startActivity(intent);

    }
    private void setRedBackroundHieght()
    {
        //to understand
        View redBackground = findViewById(R.id.redBackgroundView);
        ScrollView scrollView = findViewById(R.id.buttonsScrollView);

// Get the layout parameters of the ScrollView
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) scrollView.getLayoutParams();

// Retrieve the top margin
        int topMargin = params.topMargin;

// Set the height of redBackground to the top margin of scrollView
        redBackground.getLayoutParams().height = topMargin;

// Apply the layout parameters to redBackground
        redBackground.requestLayout();
    }
    public Button findChoseButton()
    {
        Button chosenButton = null;
        Button orgnizeWordsButton = findViewById(R.id.orgnizeWordsButton);
        Button wordsThatUserDOESNTKnowButton = findViewById(R.id.wordsThatUserDOESNTKnowButton);
        Button wordsThatUserKnowButton = findViewById(R.id.wordsThatUserKnowButton);
        if(currAction == 0 || currAction == OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS)
        {
            chosenButton = orgnizeWordsButton;
            //the default is not decide page
        }
        else if(currAction == OperationsAndOtherUsefull.DO_KNOW_WORDS)
        {
            chosenButton = wordsThatUserKnowButton;
        }
        else if(currAction == OperationsAndOtherUsefull.DO_NOT_KNOW_WORDS)
        {
            chosenButton = wordsThatUserDOESNTKnowButton;
        }
        return chosenButton;
    }
    private void putIntoIntent(Intent intent)
    {
        intent.putExtra("isEnglish", isEnglish);
        intent.putExtra("isCategoryChoice",true);
        if(unit != KEEP_UNIT_AND_CATEGORY_AS_IS && category != KEEP_UNIT_AND_CATEGORY_AS_IS)
        {
            intent.putExtra("unit", unit);
            intent.putExtra("category", category);
        }
        intent.putExtra("isUserWantMeanings",this.isUserWantMeanings);
    }
    public void wordsThatUserDOESNTKnowButtonClicked(View view)
    {
        Intent intent = new Intent(SortingWordsPage.this, SortingWordsPage.class);
        intent.putExtra("action",OperationsAndOtherUsefull.DO_NOT_KNOW_WORDS);
        putIntoIntent(intent);
        this.finish();
        startActivity(intent);
    }
    public void wordsThatUserKnowButtonClicked(View view)
    {
        Intent intent = new Intent(SortingWordsPage.this, SortingWordsPage.class);
        intent.putExtra("action",OperationsAndOtherUsefull.DO_KNOW_WORDS);
        putIntoIntent(intent);
        this.finish();
        startActivity(intent);
    }
    public void orgnizeWordsButtonCLICKED(View view)
    {
        Intent intent = new Intent(SortingWordsPage.this, SortingWordsPage.class);
        intent.putExtra("action",OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS);
        putIntoIntent(intent);
        this.finish();
        startActivity(intent);
    }
    private void addLengthToKnowButtons(int currentWordStars)
    {
        if(currentWordStars == 0)
        {
            this.lenDONOTDECIDEWords++;
        }
        if(currentWordStars ==-1)
        {
            this.lenDONOTKnowWords++;
        }
        if(currentWordStars >0)
        {
            this.lenKnowWrods++;
        }

    }
    private boolean isButtonNeedToBeCreate(int currentWordStars)
    {

        if(currAction == OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS)
        {
            if(currentWordStars != 0)
            {
                return false;
            }
        }
        else if(currAction == OperationsAndOtherUsefull.DO_KNOW_WORDS)
        {
            //know when there is stars
            if(currentWordStars <= 0 )
            {
                return false;
            }
        }
        else if(currAction == OperationsAndOtherUsefull.DO_NOT_KNOW_WORDS)
        {
            if (currentWordStars != -1)
            {
                return false;
            }
        }
        return true;
    }

    protected ArrayList<WordButton> createButtons(FinalWordProperties[] words)
    {
        ArrayList<WordButton> wordButtonArrayList = new ArrayList<>();
        //should also take the text and phrase - later on
        Map<String, Integer> buttonsAndId = new HashMap<>();
        final float[] currentHeight = {0};
        boolean toKeepIncrease = true;
        //the סדר is matter!
        int[] arrOfButtonId = new int[3];
        arrOfButtonId[0] = R.id.wordsThatUserDOESNTKnowButton;
        arrOfButtonId[1] = R.id.orgnizeWordsButton;
        arrOfButtonId[2] = R.id.wordsThatUserKnowButton;

        LinearLayout buttonsContainer = findViewById(R.id.linearLayoutButtonContainer);
        LockableScrollView lockableScrollView = findViewById(R.id.buttonsScrollView);
        // Clear previous buttons (optional, if you want to reset the container)
        buttonsContainer.removeAllViews();
        for(int i =0; i <words.length;i++)
        {
            int currentAmountOfStars = words[i].getUserDetailsOnWords().getAmountOfStars();
            addLengthToKnowButtons(currentAmountOfStars);
            if(!isButtonNeedToBeCreate(currentAmountOfStars))
            {
                continue;
            }
            buttonsAndId.put("current",currAction);

            WordButton btn = new WordButton(this,words[i],dbManager);
            if(isUserWantMeanings)
            {
                btn.setText(words[i].getWordProperties().getWord()+": "+words[i].getWordProperties().getMeaning());
            }
            else {
                btn.setText(words[i].getWordProperties().getWord());
            }
            btn.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // Button width
                    LinearLayout.LayoutParams.WRAP_CONTENT  // Button height
            );

            params.setMargins(10, 20, 10, 0); // Add some margins (optional)
            btn.setLayoutParams(params);
            ButtonDraggableFuncs buttonDraggableFuncs = new ButtonDraggableFuncs();
            buttonDraggableFuncs.makeButtonDraggableOnXAxsis(btn,currAction,dbManager,arrOfButtonId,lockableScrollView);
            //ButtonDraggableFuncs.makeButtonDraggableOnXAxsis(btn,currAction,dbManager,words[i].getWordProperties().getWord(),arrOfButtonId);
            btn.setAllCaps(false);//for some reason all the words were upper case
            buttonsContainer.addView(btn);
            btn.afterAddingToLayout();
            wordButtonArrayList.add(btn);
            if(words[i].getWordProperties().getWord().equals(wordToMark))
            {
                btn.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                toKeepIncrease = false;

            }
            if(toKeepIncrease)
            {
                btn.post(() ->
                {
                    currentHeight[0]+=btn.getHeight();
                    lockableScrollView.post(
                            () -> lockableScrollView.scrollTo(0, (int)currentHeight[0]));
                });
                //currentHeight+=btn.getHeight();
            }




        }

        return wordButtonArrayList;
    }
    public void exitImgButtonClick3(View view)
    {
        Intent intent = new Intent(SortingWordsPage.this, MenuOfflinePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);//clear all previous pages
        startActivity(intent);

    }
}