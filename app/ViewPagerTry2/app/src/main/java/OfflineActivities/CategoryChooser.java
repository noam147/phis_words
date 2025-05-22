
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

/**
 * An {@link AppCompatActivity} that displays a list of buttons for choosing either
 * a category or a unit, depending on the value of the {@code isCategoryChoice}
 * extra in the launching intent. The number of buttons created is based on the
 * total number of categories or the number of units within a specific category,
 * as determined by the {@link DBManager}.
 */
public class CategoryChooser extends AppCompatActivity {

    private boolean isEnglish;
    private int currunit;
    private int currCategory;
    private DBManager dbManager;
    private int currAction;

    /**
     * Called when the activity is first created. Initializes the layout, retrieves
     * data from the intent, initializes the {@link DBManager}, and creates the
     * category or unit buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
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

        int numOfButtons = isCategoryChoice ?
                dbManager.getCountOfCategories(isEnglish) :
                dbManager.getCountOfUnitsInACategory(isEnglish, currCategory);

        createButtons(numOfButtons, isCategoryChoice);
    }

    /**
     * Extracts the integer value from the text of a button. This is used to get
     * the selected category or unit number when a button is clicked. It assumes
     * that the integer value is on the first line of the button's text.
     *
     * @param btn The {@link Button} whose text to parse.
     * @return The integer value extracted from the button's text, or 1 if parsing fails.
     */
    public int getIntInAmountButtonText(View btn) {
        String text = ((Button) btn).getText().toString();
        StringBuilder finalIntString = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                break; // Stop at the newline character
            }
            if (Character.isDigit(text.charAt(i))) {
                finalIntString.append(text.charAt(i));
            }
        }
        try {
            return Integer.parseInt(finalIntString.toString());
        } catch (NumberFormatException e) {
            Log.e("CategoryChooser", "Error parsing integer from button text: " + text, e);
            return 1; // Default value in case of parsing error
        }
    }

    /**
     * Sets the text for a category button, displaying the level (category number)
     * and the progress of known words within that category.
     *
     * @param btn   The {@link Button} to set the text on.
     * @param index The index of the category (0-based).
     */
    private void setTextOnButtonWithCategoryChoice(Button btn, int index) {
        int currCategory = index + 1;
        String text = "Level: " + currCategory + "\n";
        int wordsInCategory = OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY * OperationsAndOtherUsefull.WORDS_PER_UNIT;
        int wordsInCategoryUserKnow = dbManager.getCountOfWordsUserKnowInSpecificCategory(currCategory);
        text += wordsInCategoryUserKnow + "/" + wordsInCategory;
        btn.setText(text);
    }

    /**
     * Sets the text for a unit button, displaying the unit number and the progress
     * of known words within that unit of the current category. It also adds a
     * celebration emoji if all words in the unit are known.
     *
     * @param btn   The {@link Button} to set the text on.
     * @param index The index of the unit (0-based).
     */
    private void setTextOnButtonWithUnityChoice(Button btn, int index) {
        String text = "Unit: " + (index + 1) + "\n";
        int wordsUserKnow = dbManager.getCountOfWordsUserKnowBasedOnCategory(this.currCategory, index + 1);
        int amountOfWords = OperationsAndOtherUsefull.WORDS_PER_UNIT;
        text += wordsUserKnow + "/" + amountOfWords;
        if (wordsUserKnow == amountOfWords) {
            text += "\uD83C\uDF89"; // Celebrate emoji
        }
        btn.setText(text);
    }

    /**
     * Creates a specified number of buttons and adds them to the {@link LinearLayout}
     * in the layout. The text and the {@link android.view.View.OnClickListener} for
     * each button are set based on whether the activity is in category choice mode
     * or unit choice mode.
     *
     * @param numOfButtons     The number of buttons to create.
     * @param isCategoryChoice A boolean indicating whether to create category buttons or unit buttons.
     */
    private void createButtons(int numOfButtons, boolean isCategoryChoice) {
        LinearLayout buttonsContainer = findViewById(R.id.categoryButtonsManager);
        buttonsContainer.removeAllViews(); // Clear previous buttons

        for (int i = 0; i < numOfButtons; i++) {
            Button btn = new Button(this);
            btn.setSingleLine(false);
            if (isCategoryChoice) {
                Log.d("CategoryChooser", "Creating category button " + (i + 1));
                setTextOnButtonWithCategoryChoice(btn, i);
            } else {
                Log.d("CategoryChooser", "Creating unit button " + (i + 1) + " in category " + currCategory);
                setTextOnButtonWithUnityChoice(btn, i);
            }
            btn.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(CategoryChooser.this, SortingWordsPage.class);
                intent.putExtra("isEnglish", isEnglish);
                intent.putExtra("action", currAction);
                if (isCategoryChoice) {
                    intent.putExtra("unit", currunit);
                    int category = getIntInAmountButtonText(v);
                    intent.putExtra("category", category);
                } else {
                    intent.putExtra("category", currCategory);
                    int unit = getIntInAmountButtonText(v);
                    intent.putExtra("unit", unit);
                }
                finish();
                startActivity(intent);
            });
            params.setMargins(10, 20, 10, 0);
            btn.setLayoutParams(params);
            btn.setAllCaps(false); // Prevent text from being all uppercase
            buttonsContainer.addView(btn);
        }
    }

    /**
     * Handles the click event of the exit image button. It navigates back to the
     * {@link SortingWordsPage} with the current language, action, unit, and category.
     *
     * @param view The clicked {@link View} (the exit image button).
     */
    public void exitImgButtonClick(View view) {
        Intent intent = new Intent(CategoryChooser.this, SortingWordsPage.class);
        intent.putExtra("isEnglish", isEnglish);
        intent.putExtra("action", currAction);
        intent.putExtra("unit", currunit);
        intent.putExtra("category", this.currCategory);
        startActivity(intent);
        finish();
    }

    /**
     * Called when the activity is no longer visible to the user. Closes the
     * database connection to release resources.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (dbManager != null) {
            dbManager.closeDb();
        }
    }

    /**
     * Called after {@link #onStop()} when the current activity is being
     * re-displayed to the user (the user has navigated back to it). Opens the
     * database connection again.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (dbManager != null) {
            dbManager.openDb();
        }
    }
}
