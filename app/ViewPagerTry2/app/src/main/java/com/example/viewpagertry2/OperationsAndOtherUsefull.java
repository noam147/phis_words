package com.example.viewpagertry2;



import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ExercisesPages.Question;

public class OperationsAndOtherUsefull {
    public static final int MENU_INTENT_ID = 1;
    public static final int SUMMERIZE_MULTIPLE_ANSWERS_QUESIOTNS_INTENT_ID = 2;

    public static final int WORDS_PER_UNIT = 50;
    public static final int NUM_OF_UNITS_IN_EACH_CATEGORY = 10;

    public static final int MIN_KNOW_WORD_AMOUNT_OF_STARS = 1;
    public static final int DID_NOT_SORTAMOUNT_OF_STARS =0;
    public static final int MAX_DO_NOT_KNOW_WORD_AMOUNT_OF_STARS = -1;
    public static final int MIN_DO_NOT_KNOW_WORD_AMOUNT_OF_STARS = -1;

    public static final int DO_NOT_KNOW_WORDS = 1;
    public static final int DO_NOT_DECIDE_WORDS = DO_NOT_KNOW_WORDS + 1;
    public static final int DO_KNOW_WORDS = DO_NOT_DECIDE_WORDS + 1;
    public static final int ALL_WORDS_ACTION = DO_KNOW_WORDS + 1;
    public static final int MARKED_WORDS_ACTION = ALL_WORDS_ACTION+1;

    public static final int AMOUNT_OF_WORDS_EACH_TIME_SEARCHING = 50;

    public static final int BASE_AMOUNT_OF_QUESTIONS_IN_EXERCISE = 10;

    public static int getStartIdOfUnit(int category,int unit)
    {
        return (category-1)*NUM_OF_UNITS_IN_EACH_CATEGORY*WORDS_PER_UNIT + (unit-1)*WORDS_PER_UNIT;
    }
    public static int getStartIdOfCategory(int category)
    {
        return (category-1)*NUM_OF_UNITS_IN_EACH_CATEGORY*WORDS_PER_UNIT;
    }
    public static int getIntInButtonText(View btn) {
        String text = ((Button)btn).getText().toString();
        String finalIntString = "";
        for (int i = 0; i < text.length(); i++) {
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
    public static FinalWordProperties[] getWordsOfUnitByAction(int unit, int category, boolean isEnglish, int action, DBManager dbManager) {
        //that is not a good implentaion - needs to put in the db where amountOfStars = ?
        FinalWordProperties[] words = dbManager.getWordsOfUnit(unit, category, isEnglish);
        if (action == OperationsAndOtherUsefull.ALL_WORDS_ACTION) {//if we want full unit words
            return words;
        }
        List<FinalWordProperties> wordsAfterCheckingList = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            int currAmountOfStars = words[i].getUserDetailsOnWords().getAmountOfStars();
            if (check(currAmountOfStars, -1, action, OperationsAndOtherUsefull.DO_NOT_KNOW_WORDS)) {
                //on do not know
                wordsAfterCheckingList.add(words[i]);
            }
            if (check(currAmountOfStars, 0, action, OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS)) {
                //on do not decide
                wordsAfterCheckingList.add(words[i]);
            }
            if (check(currAmountOfStars, 1, action, OperationsAndOtherUsefull.DO_KNOW_WORDS) || check(currAmountOfStars, 2, action, OperationsAndOtherUsefull.DO_KNOW_WORDS)) {
                //on do know - there is one star or two option
                wordsAfterCheckingList.add(words[i]);
            }

        }
        return wordsAfterCheckingList.toArray(new FinalWordProperties[0]);
    }

    public static void shffuleArr(FinalWordProperties[] arrToShuffle) {
        // Convert the array to a list
        List<FinalWordProperties> list = Arrays.asList(arrToShuffle);
        // Shuffle the list using Collections.shuffle()
        Collections.shuffle(list);
        // No need to convert back since it modifies the original array
    }

    private static boolean check(int currAmountOfStars, int neededAmountOfStars, int action, int operation) {
        if (currAmountOfStars == neededAmountOfStars)//do not know
        {
            if (action == operation) {
                return true;
            }
        }
        return false;
    }

    public static Question[] getRandQuestions(boolean isEnglish, FinalWordProperties[] finalWordProperties, DBManager dbManager)
    {
        //to operation
        Question[] questions = new Question[finalWordProperties.length];
        for(int i =0; i <finalWordProperties.length;i++)
        {
            String[] wrongAnswers = dbManager.getThreeRandomAnswers(isEnglish,finalWordProperties[i].getWordProperties().getMeaning());
            Question currQuestion = new Question(wrongAnswers,finalWordProperties[i]);
            questions[i] = currQuestion;
        }
        return questions;
    }

    public static Drawable resizeDrawable(int width,int height,Drawable drawable)
    {
        drawable.setBounds(0, 0, width, height);
        return drawable;
    }
}
