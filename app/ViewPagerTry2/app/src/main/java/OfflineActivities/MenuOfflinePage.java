package OfflineActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viewpagertry2.DBManager;
import com.example.viewpagertry2.MakeViewPlayAudio;
import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;
import com.example.viewpagertry2.XpPointsAnimations;
import com.google.gson.Gson;

import API.API_Handler;
import API.DailyWord;
import API.ReqCallback;
import NewViews.TextProgressBar;
import files.HistoryOfUnitAndCategoryPrefs;
import files.UnitAndCaetogryHistoryHelper;
import files.XpPointsTracker;

import ExercisesPages.WordQuestionsPageMultipleAnswers;
import ExercisesPages.WordQuestionsPageWriteAnswer;

public class MenuOfflinePage extends AppCompatActivity {

    DBManager dbManager;
    ImageView shieldImageView;
    int BUCKET_AMOUNT = 20;
    int HANDFUL_AMOUNT = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_offline_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbManager = new DBManager(this);
        shieldImageView = findViewById(R.id.progressRankImageView);
        shieldImageView.post(new Runnable() {
            @Override
            public void run() {
                setListernsToShopButtons();
            }
        });
        onStartPage();

        get_daily_word();
        MakeViewPlayAudio.playRecording(this,"composeapptry2melody.mp3",true);
        //XpPointsTracker.resetAmount(this);
    }
    private void get_daily_word()
    {
        TextView daily_text = findViewById(R.id.daily_word_textView);
        API_Handler.sendGetRequest("http://13.51.79.222:15555/daily_word", new ReqCallback() {
            @Override
            public void onSuccess(String response) {
                String a = "hi";
                Gson gson = new Gson();
                try {
                    DailyWord word_meaning = gson.fromJson(response, DailyWord.class);
                    daily_text.setText(word_meaning.word+": "+word_meaning.meaning);
                }
                catch (Exception e)
                {
                    //pass
                }



            }

            @Override
            public void onFailure(Exception e) {
                int a =0;
            }
        });
    }
    public void RankImageClicked(View view)
    {
        Intent intent = new Intent(MenuOfflinePage.this, ShowRankProgressActivity.class);
        startActivity(intent);


    }

    private void progressBar()
    {
        TextProgressBar progressBar = findViewById(R.id.progressBar);
        TextView levelTextView = findViewById(R.id.currentLevelTextView);
        ImageView imageView = findViewById(R.id.progressRankImageView);

        XpPointsTracker.setCountersAtStart(this);
        int currentAmount = XpPointsTracker.getCurrentAmount();
        int progressBarValue = XpPointsTracker.getAmountForProgressBarNextLevel();
        progressBar.setProgress(progressBarValue);
        int nextLevelPoints = XpPointsTracker.getAmountForNextLevel();
        if(nextLevelPoints == -1)
        {
            progressBar.setProgress(100);
            progressBar.setText(Integer.toString(currentAmount));
            levelTextView.setVisibility(View.GONE);
        }
        else {
            progressBar.setText(currentAmount+"/"+nextLevelPoints);
        }


        XpPointsTracker.getImgOfCurrentRank2(this,imageView);
        int currLevel = XpPointsTracker.getCurrentLevel();
        levelTextView.setText(Integer.toString(currLevel));
    }
    private void onStartPage()
    {
        progressBar();
        Button orgenizeBtn = findViewById(R.id.orgenizeWordsButtonMenu);
        int amountOfWordsKnow,amountOfAllWords;
        amountOfAllWords =  dbManager.getCountOfWords(true);
        amountOfWordsKnow = dbManager.getCountOfWordsUserKnow();




        orgenizeBtn.setSingleLine(false);
        orgenizeBtn.setText("Sort Words\n"+amountOfWordsKnow+"/"+amountOfAllWords);


    }
    public void gameButtonClicked(View view)
    {
        Intent intent = new Intent(MenuOfflinePage.this, WordQuestionsPageMultipleAnswers.class);
        startActivity(intent);
        //this.finish();
    }
    public void orgenizeButtonClicked(View view)
    {
        Intent intent = new Intent(MenuOfflinePage.this, SortingWordsPage.class);
        //get previous category and unit
        UnitAndCaetogryHistoryHelper categoryUnit = HistoryOfUnitAndCategoryPrefs.getUnitAndCategory(this);
        intent.putExtra("category",categoryUnit.getCategoryIndex());
        intent.putExtra("unit",categoryUnit.getUnitIndex());
        startActivity(intent);
        //this.finish();
    }
    public void searchWordsButtonClicked(View view)
    {
        Intent intent = new Intent(MenuOfflinePage.this, SeacrhWordInDbActivity.class);
        startActivity(intent);

        //this.finish();
    }
    public  void writeGameButtonClicked(View view)
    {
        Intent intent = new Intent(MenuOfflinePage.this, WordQuestionsPageWriteAnswer.class);
        startActivity(intent);
    }
    public void markedWordButtonClicked(View view)
    {
        //need to cancel the buttons in the sorting page
        //maybe need fragment?
        Intent intent = new Intent(MenuOfflinePage.this, SortingWordsPage.class);
        intent.putExtra("action", OperationsAndOtherUsefull.MARKED_WORDS_ACTION);

        float xPosition = view.getX();
        float yPosition = view.getY();

        float endX = shieldImageView.getX()+shieldImageView.getWidth()/2;
        float endY = shieldImageView.getY()+shieldImageView.getHeight()/2;

        float radios = 50;


        ConstraintLayout layout = findViewById(R.id.main);
        //XpPointsAnimations.animateAndAddXpPoints(40,this,layout,xPosition,yPosition,endX,endY);




        //do not want this to work - full of bugs
        startActivity(intent);

    }
    private void setListernsToShopButtons()
    {
        ImageView oneCoin = findViewById(R.id.imageView3);
        ImageView handfulCoins = findViewById(R.id.imageView2);
        ImageView bucketCoins = findViewById(R.id.imageView);

        oneCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopButtonClicked(view,1);
            }
        });
        handfulCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopButtonClicked(view,HANDFUL_AMOUNT);
            }
        });
        bucketCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopButtonClicked(view,BUCKET_AMOUNT);
            }
        });
    }
    public void resetPointsBtnClicked(View view)
    {
        XpPointsTracker.resetAmount(this);
    }
    private void shopButtonClicked(View view,int amount)
    {
        ConstraintLayout layout = findViewById(R.id.main);
        float endX = shieldImageView.getX()+shieldImageView.getWidth()/2;
        float endY = shieldImageView.getY()+shieldImageView.getHeight()/2;
        float xPosition = view.getX();
        float yPosition = view.getY();
        XpPointsAnimations.animateAndAddXpPoints(amount,view.getContext(),layout,xPosition,yPosition,endX,endY);
    }
}