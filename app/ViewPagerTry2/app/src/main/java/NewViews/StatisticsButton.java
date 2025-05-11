package NewViews;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;
import com.example.viewpagertry2.UnitAndCategoryOfWord;

import ExercisesPages.AfterAnswerQuestionDetails;
import OfflineActivities.SeacrhWordInDbActivity;
import OfflineActivities.SortingWordsPage;

public class StatisticsButton extends androidx.appcompat.widget.AppCompatButton {

    Context context;
    AfterAnswerQuestionDetails wordProperties;
    public StatisticsButton(Context context,AfterAnswerQuestionDetails wordProperties) {
        super(context);
        this.context = context;
        this.wordProperties = wordProperties;
        putStars2();
    }

    @Override
    public boolean performClick() {
        // Call super.performClick() first.
        boolean handledBySuper = super.performClick();

        String word = this.wordProperties.getQuestionDetails().getWordProperties().getWord();
        int wordId = this.wordProperties.getQuestionDetails().getWordProperties().getWord_id();
        int numOfStars= this.wordProperties.getQuestionDetails().getUserDetailsOnWords().getAmountOfStars();

        UnitAndCategoryOfWord unitAndC = new UnitAndCategoryOfWord(wordId);
        Intent intent = new Intent(this.getContext(), SortingWordsPage.class);
        intent.putExtra("unit",unitAndC.getUnit());
        intent.putExtra("category",unitAndC.getCategory());
        int finalAction = numOfStars;
        if (finalAction > OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS)
        {
            finalAction = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
        }
        intent.putExtra("action",finalAction+2);//TODO change
        intent.putExtra("wordToMark",word);
        this.context.startActivity(intent);

        return handledBySuper;
    }

    private void putStars2()
    {
        // finalImg;
        int amountOfStars = wordProperties.getQuestionDetails().getUserDetailsOnWords().getAmountOfStars();
        if(amountOfStars == OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS)
        {

        }
        if(amountOfStars > 0)
        {
            this.post(() -> {
                Drawable finalImg=null;// = getResources().getDrawable(R.drawable.three_stars);
                int buttonHeight = this.getHeight();
                int desiredHeight = (int) (buttonHeight);
                if(amountOfStars == OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS)
                {
                    desiredHeight = (int) (buttonHeight*0.7);
                    finalImg = getResources().getDrawable(R.drawable.two_stars);
                }
                else if(amountOfStars > OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS)
                {
                    finalImg = getResources().getDrawable(R.drawable.three_stars);
                }



                // Resize drawable to match the button's height
                finalImg = OperationsAndOtherUsefull.resizeDrawable(desiredHeight+30, desiredHeight, finalImg);
                // Set the resized drawable to the left of the button text
                this.setCompoundDrawables(finalImg, null, null, null);

                // Optionally, set padding if needed
                //this.setCompoundDrawablePadding(20);
            });
        }
    }
    private void putStars()
    {

        // Set the first star drawable on the left
        if(wordProperties.getIsUserRight())
            this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_full_star_24, 0, 0, 0);
        else
        {
            this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_empty_star_outline_24,0,0,0);
        }
        this.setCompoundDrawablePadding(20); // Adjust the padding value as needed

    }
}
