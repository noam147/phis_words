package NewViews;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.viewpagertry2.OperationsAndOtherUsefull;
import com.example.viewpagertry2.R;

import ExercisesPages.AfterAnswerQuestionDetails;

public class StatisticsButton extends androidx.appcompat.widget.AppCompatButton {

    Context context;
    AfterAnswerQuestionDetails wordProperties;
    public StatisticsButton(Context context,AfterAnswerQuestionDetails wordProperties) {
        super(context);
        this.context = context;
        this.wordProperties = wordProperties;
        putStars2();
    }
    /*private void putStars() {
        LinearLayout starLayout = new LinearLayout(context);
        starLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Create the first star ImageView
        ImageView star1 = new ImageView(context);
        star1.setImageResource(R.drawable.baseline_full_star_24);

        // Create the second star ImageView
        ImageView star2 = new ImageView(context);
        star2.setImageResource(R.drawable.baseline_full_star_24);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 0, 0); // Add margin to the second star
        star2.setLayoutParams(params);

        // Add stars to the layout
        starLayout.addView(star1);
        starLayout.addView(star2);

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // Add the star layout to the button
        this.addView(starLayout, buttonLayoutParams);
    }*/
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
