package OfflineActivities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ScrollView;

import com.example.viewpagertry2.FinalWordProperties;
import com.example.viewpagertry2.R;
import NewViews.WordButton;

import java.util.ArrayList;import android.view.ViewGroup;

public class SortingWordsPageWithSpecificWordMarked extends SortingWordsPage {

    private String wordToMark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting_words_page); // Set parent layout
        getLayoutInflater().inflate(R.layout.activity_sorting_words_page_with_specific_word_marked, (ViewGroup) findViewById(R.id.main), true);
        wordToMark = getIntent().getStringExtra("wordToMark");
    }
    @Override
    protected void setIntentData(Intent intent) {
        Intent realIntent = getIntent();
        int unit = intent.getIntExtra("unit", 1);
        super.setIntentData(realIntent);
    }
    @Override
    protected ArrayList<WordButton> createButtons(FinalWordProperties[] words)
    {
        ArrayList<WordButton> wordButtonArrayList = super.createButtons(words);
        ScrollView btnsScrollView = findViewById(R.id.buttonsScrollView);
        int yPosition = 0;//get the start y position
        for (WordButton btn: wordButtonArrayList) {
            String currWord = btn.getFinalWordProperties().getWordProperties().getWord();
            if(currWord.equals(wordToMark))
            {
                btn.setBackgroundColor(Color.RED);
                int btnAbsoluteY = yPosition + btn.getTop();
                int scrollToY = btnAbsoluteY - (btnsScrollView.getHeight() / 2) + (btn.getHeight() / 2);
                btnsScrollView.smoothScrollTo(0, scrollToY);
                //btnsScrollView.smoothScrollBy(0,yPosition);
            }
            yPosition += btn.getHeight()+ btn.getPaddingBottom();
        }


        return wordButtonArrayList;

    }
}