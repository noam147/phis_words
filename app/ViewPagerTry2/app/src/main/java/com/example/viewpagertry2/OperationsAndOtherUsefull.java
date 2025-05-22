
package com.example.viewpagertry2;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ExercisesPages.Question;

/**
 * This class contains various constants and utility methods used throughout the application.
 * It includes identifiers for intents, numerical constants related to word organization,
 * action codes for filtering words, and functions for retrieving word data, shuffling arrays,
 * generating quiz questions, and manipulating UI elements.
 */
public class OperationsAndOtherUsefull {
    /**
     * Identifier for intents related to the main menu.
     */
    public static final int MENU_INTENT_ID = 1;

    /**
     * Identifier for intents related to summarizing multiple-answer questions.
     */
    public static final int SUMMERIZE_MULTIPLE_ANSWERS_QUESIOTNS_INTENT_ID = 2;

    public static final int SEARCH_WORD_INTENT_ID = 3;
    /**
     * The number of words contained within each unit.
     */
    public static final int WORDS_PER_UNIT = 50;

    /**
     * The number of units that belong to each category.
     */
    public static final int NUM_OF_UNITS_IN_EACH_CATEGORY = 10;

    /**
     * The minimum number of stars indicating a word is known.
     */
    public static final int MIN_KNOW_WORD_AMOUNT_OF_STARS = 1;

    /**
     * A special value indicating that the amount of stars has not yet been sorted
     * or assigned a definitive value.
     */
    public static final int DID_NOT_SORTAMOUNT_OF_STARS = 0;

    /**
     * The maximum number of stars indicating a word is not known (currently set to -1).
     */
    public static final int MAX_DO_NOT_KNOW_WORD_AMOUNT_OF_STARS = -1;

    /**
     * The minimum number of stars indicating a word is not known.
     */
    public static final int MIN_DO_NOT_KNOW_WORD_AMOUNT_OF_STARS = -1;

    /**
     * Action code representing words that the user does not know.
     */
    public static final int DO_NOT_KNOW_WORDS = 1;

    /**
     * Action code representing words for which the user has not yet decided their knowledge level.
     */
    public static final int DO_NOT_DECIDE_WORDS = DO_NOT_KNOW_WORDS + 1;

    /**
     * Action code representing words that the user knows.
     */
    public static final int DO_KNOW_WORDS = DO_NOT_DECIDE_WORDS + 1;

    /**
     * Action code representing all words, regardless of their knowledge status.
     */
    public static final int ALL_WORDS_ACTION = DO_KNOW_WORDS + 1;

    /**
     * Action code representing words that have been marked by the user.
     */
    public static final int MARKED_WORDS_ACTION = ALL_WORDS_ACTION + 1;

    /**
     * The amount of words to fetch each time a search operation is performed.
     */
    public static final int AMOUNT_OF_WORDS_EACH_TIME_SEARCHING = 50;

    /**
     * The base amount of questions to include in an exercise.
     */
    public static final int BASE_AMOUNT_OF_QUESTIONS_IN_EXERCISE = 10;

    /**
     * Calculates the starting word ID of a given unit within a specific category.
     *
     * @param category The category number (1-based).
     * @param unit     The unit number within the category (1-based).
     * @return The starting word ID of the specified unit.
     */
    public static int getStartIdOfUnit(int category, int unit) {
        return (category - 1) * NUM_OF_UNITS_IN_EACH_CATEGORY * WORDS_PER_UNIT + (unit - 1) * WORDS_PER_UNIT;
    }

    /**
     * Calculates the starting word ID of a given category.
     *
     * @param category The category number (1-based).
     * @return The starting word ID of the specified category.
     */
    public static int getStartIdOfCategory(int category) {
        return (category - 1) * NUM_OF_UNITS_IN_EACH_CATEGORY * WORDS_PER_UNIT;
    }

    /**
     * Extracts the first integer found within the text of a given {@link View} (assumed to be a {@link Button}).
     * If no integer is found or if a {@link NumberFormatException} occurs, it returns a default value of 1.
     *
     * @param btn The {@link View} (should be a {@link Button}) to extract the integer from.
     * @return The integer value found in the button's text, or 1 if no integer is found.
     */
    public static int getIntInButtonText(View btn) {
        String text = ((Button) btn).getText().toString();
        String finalIntString = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) >= '0' && text.charAt(i) <= '9') {
                finalIntString += text.charAt(i);
            }
        }
        try {
            return Integer.parseInt(finalIntString);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * Retrieves an array of {@link FinalWordProperties} for a specific unit and category,
     * filtered based on the user's action (e.g., show only known words, unknown words, etc.).
     *
     * @param unit      The unit number (1-based).
     * @param category  The category number (1-based).
     * @param isEnglish A boolean indicating if the words are English.
     * @param action    The action code to filter words (e.g., {@link #ALL_WORDS_ACTION},
     * {@link #DO_NOT_KNOW_WORDS}, {@link #DO_KNOW_WORDS}).
     * @param dbManager An instance of {@link DBManager} to interact with the database.
     * @return An array of {@link FinalWordProperties} that match the specified criteria.
     */
    public static FinalWordProperties[] getWordsOfUnitByAction(int unit, int category, boolean isEnglish, int action, DBManager dbManager) {
        FinalWordProperties[] words = dbManager.getWordsOfUnit(unit, category, isEnglish);
        if (action == OperationsAndOtherUsefull.ALL_WORDS_ACTION) {
            return words;
        }
        List<FinalWordProperties> wordsAfterCheckingList = new ArrayList<>();
        for (FinalWordProperties word : words) {
            int currAmountOfStars = word.getUserDetailsOnWords().getAmountOfStars();
            if (check(currAmountOfStars, -1, action, OperationsAndOtherUsefull.DO_NOT_KNOW_WORDS)) {
                wordsAfterCheckingList.add(word);
            }
            if (check(currAmountOfStars, 0, action, OperationsAndOtherUsefull.DO_NOT_DECIDE_WORDS)) {
                wordsAfterCheckingList.add(word);
            }
            if (check(currAmountOfStars, 1, action, OperationsAndOtherUsefull.DO_KNOW_WORDS) ||
                    check(currAmountOfStars, 2, action, OperationsAndOtherUsefull.DO_KNOW_WORDS)) {
                wordsAfterCheckingList.add(word);
            }
            if (currAmountOfStars == 2) {
                // Potential debugging line, currently does nothing
            }
        }
        return wordsAfterCheckingList.toArray(new FinalWordProperties[0]);
    }

    /**
     * Shuffles the elements of a given array of {@link FinalWordProperties} in place.
     *
     * @param arrToShuffle The array of {@link FinalWordProperties} to shuffle.
     */
    public static void shffuleArr(FinalWordProperties[] arrToShuffle) {
        List<FinalWordProperties> list = Arrays.asList(arrToShuffle);
        Collections.shuffle(list);
    }

    /**
     * Checks if the current amount of stars for a word matches the needed amount of stars
     * for a specific action and operation.
     *
     * @param currAmountOfStars The current amount of stars of the word.
     * @param neededAmountOfStars The amount of stars required for the operation.
     * @param action The current action being performed.
     * @param operation The specific operation to check against the action.
     * @return {@code true} if the conditions are met, {@code false} otherwise.
     */
    private static boolean check(int currAmountOfStars, int neededAmountOfStars, int action, int operation) {
        return currAmountOfStars == neededAmountOfStars && action == operation;
    }

    /**
     * Generates an array of quiz {@link Question} objects based on the provided
     * array of {@link FinalWordProperties}. For each word, it fetches three random
     * incorrect answers from the database.
     *
     * @param isEnglish           A boolean indicating if the questions are for English words.
     * @param finalWordProperties An array of {@link FinalWordProperties} to create questions from.
     * @param dbManager           An instance of {@link DBManager} to fetch incorrect answers.
     * @return An array of {@link Question} objects.
     */
    public static Question[] getRandQuestions(boolean isEnglish, FinalWordProperties[] finalWordProperties, DBManager dbManager) {
        Question[] questions = new Question[finalWordProperties.length];
        for (int i = 0; i < finalWordProperties.length; i++) {
            String[] wrongAnswers = dbManager.getThreeRandomAnswers(isEnglish, finalWordProperties[i].getWordProperties().getMeaning());
            Question currQuestion = new Question(wrongAnswers, finalWordProperties[i]);
            questions[i] = currQuestion;
        }
        return questions;
    }

    /**
     * Resizes a given {@link Drawable} to the specified width and height.
     *
     * @param width     The desired width of the drawable.
     * @param height    The desired height of the drawable.
     * @param drawable The {@link Drawable} to resize.
     * @return The resized {@link Drawable}.
     */
    public static Drawable resizeDrawable(int width, int height, Drawable drawable) {
        drawable.setBounds(0, 0, width, height);
        return drawable;
    }
}
