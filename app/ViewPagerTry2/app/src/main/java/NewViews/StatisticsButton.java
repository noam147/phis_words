
        package NewViews;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;
import com.example.viewpagertry2.UnitAndCategoryOfWord;

import ExercisesPages.AfterAnswerQuestionDetails;
import OfflineActivities.SortingWordsPage;

/**
 * A custom {@link androidx.appcompat.widget.AppCompatButton} designed to display
 * statistics related to a word after a question, such as the number of stars
 * associated with it. Clicking this button navigates the user to the
 * {@link SortingWordsPage} for the specific unit and category of the word,
 * with a pre-selected action based on the current number of stars.
 */
public class StatisticsButton extends androidx.appcompat.widget.AppCompatButton {
    Context context;
    AfterAnswerQuestionDetails wordProperties;

    /**
     * Constructor for creating a StatisticsButton.
     *
     * @param context        The Context the view is running in.
     * @param wordProperties An instance of {@link AfterAnswerQuestionDetails} containing
     * information about the word and the user's interaction with it.
     */
    public StatisticsButton(Context context, AfterAnswerQuestionDetails wordProperties) {
        super(context);
        this.context = context;
        this.wordProperties = wordProperties;
        putStars2();
    }

    /**
     * Overrides the {@link #performClick()} method to define the action taken
     * when the button is clicked. It navigates to the {@link SortingWordsPage}
     * for the word's unit and category, with the list filtered based on the
     * number of stars the word has.
     *
     * @return {@code true} if the click was handled, {@code false} otherwise.
     */
    @Override
    public boolean performClick() {
        boolean handledBySuper = super.performClick();

        String word = this.wordProperties.getQuestionDetails().getWordProperties().getWord();
        int wordId = this.wordProperties.getQuestionDetails().getWordProperties().getWord_id();
        int numOfStars = this.wordProperties.getQuestionDetails().getUserDetailsOnWords().getAmountOfStars();

        UnitAndCategoryOfWord unitAndC = new UnitAndCategoryOfWord(wordId);
        Intent intent = new Intent(this.getContext(), SortingWordsPage.class);
        intent.putExtra("unit", unitAndC.getUnit());
        intent.putExtra("category", unitAndC.getCategory());
        int finalAction = numOfStars;
        if (finalAction > OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS) {
            finalAction = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
        }
        intent.putExtra("action", finalAction + 2); // Maps star count to action (TODO: Verify mapping)
        intent.putExtra("wordToMark", word);
        // Indicates that this intent is for summarizing multiple-answer questions
        intent.putExtra("intentId", OperationsAndOtherUsefull.SUMMERIZE_MULTIPLE_ANSWERS_QUESIOTNS_INTENT_ID);
        this.context.startActivity(intent);

        return handledBySuper;
    }

    /**
     * Dynamically sets the compound drawable (icon) to the left of the button text
     * based on the number of stars associated with the word. It uses different
     * star icons for one or more stars.
     */
    private void putStars2() {
        int amountOfStars = wordProperties.getQuestionDetails().getUserDetailsOnWords().getAmountOfStars();
        if (amountOfStars > 0) {
            this.post(() -> {
                Drawable finalImg = null;
                int buttonHeight = this.getHeight();
                int desiredHeight = (int) (buttonHeight);
                if (amountOfStars == OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS) {
                    desiredHeight = (int) (buttonHeight * 0.7);
                    finalImg = getResources().getDrawable(R.drawable.two_stars);
                } else if (amountOfStars > OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS) {
                    finalImg = getResources().getDrawable(R.drawable.three_stars);
                }
                // Resize drawable to match the button's height
                if (finalImg != null) {
                    finalImg = OperationsAndOtherUsefull.resizeDrawable(desiredHeight + 30, desiredHeight, finalImg);
                    // Set the resized drawable to the left of the button text
                    this.setCompoundDrawables(finalImg, null, null, null);
                }
            });
        }
    }
}
