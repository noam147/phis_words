
package files;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.example.viewpagertry2.R;

import OfflineActivities.ShowRankProgressActivity;

/**
 * This class manages the tracking of XP points, user ranks, and levels within the application.
 * It handles the logic for gaining XP, leveling up, and ranking up, as well as persisting
 * this progress to a file.
 */
public class XpPointsTracker {
    /**
     * The name of the file used to store the user's progress (rank, level, current XP).
     */
    public static final String PROFRESS_FILE_NAME = "progressFile.txt";

    /**
     * A 2D array defining the XP points required for each level within each rank.
     * The outer array represents ranks, and the inner arrays represent levels within those ranks.
     */
    private static final int[][] SHIELDS_LEVELS_AMOUNT = new int[4][];

    /**
     * The number of levels available within each shield/rank (except the last one).
     */
    public static final int NUMBER_OF_LEVELS_IN_EACH_SHIELD = 5;

    /**
     * An array of resource IDs for the images representing each rank/shield.
     */
    public static final int[] ranksImagesIds = new int[]{R.drawable.bronze_shield, R.drawable.cyan_shield, R.drawable.purple_shield, R.drawable.purple_with_flame_shield};

    /**
     * A value indicating the maximum rank value (used for the last rank, which has infinite levels).
     */
    private static int maxRankVal = -1;

    /**
     * The current level of the user within the current rank (0-indexed).
     */
    private static int levelCounter = -1;

    /**
     * The current rank/shield of the user (0-indexed).
     */
    private static int shieldCounter = -1;

    /**
     * The current total amount of XP points the user has.
     */
    private static int currentAmount = -1;

    /**
     * Returns the current total amount of XP points.
     *
     * @return The current XP amount.
     */
    public static int getCurrentAmount() {
        return currentAmount;
    }

    /**
     * Returns the current level of the user within the current rank.
     *
     * @return The current level.
     */
    public static int getCurrentLevel() {
        return levelCounter;
    }

    /**
     * Returns the current rank/shield of the user.
     *
     * @return The current rank.
     */
    public static int getCurrentRank() {
        return shieldCounter;
    }

    /**
     * Returns the amount of XP points required to reach the next level within the current rank.
     *
     * @return The XP amount for the next level.
     */
    public static int getAmountForNextLevel() {
        return SHIELDS_LEVELS_AMOUNT[shieldCounter][levelCounter];
    }

    /**
     * Calculates the progress for the progress bar towards the next level, as a percentage (0-100).
     *
     * @return The progress value for the progress bar.
     */
    public static int getAmountForProgressBarNextLevel() {
        int nextLevelAmount = getAmountForNextLevel();
        // Assumption: currentAmount < nextLevelAmount
        float division = (float) currentAmount / nextLevelAmount;
        return (int) (division * 100);
    }

    /**
     * Adds or subtracts XP points and handles level and rank progression.
     * It also updates the progress file and potentially starts a {@link ShowRankProgressActivity}
     * if the user ranks up.
     *
     * @param amountToAdd The amount of XP points to add (can be negative for subtraction).
     * @param context     The application context.
     */
    public static void addOrSubToProgressBar2(int amountToAdd, Context context) {
        if (isRankedUpRightNow(amountToAdd)) {
            Intent intent = new Intent(context, ShowRankProgressActivity.class);
            currentAmount = currentAmount + amountToAdd - SHIELDS_LEVELS_AMOUNT[shieldCounter][levelCounter];
            shieldCounter++;
            levelCounter = 0;
            if (shieldCounter == SHIELDS_LEVELS_AMOUNT.length) {
                // Put it to max one or something if there are no more ranks
            }
            intent.putExtra("shield", shieldCounter);
            context.startActivity(intent);
        } else if (isLeveledUpRightNow(amountToAdd)) {
            currentAmount = currentAmount + amountToAdd - SHIELDS_LEVELS_AMOUNT[shieldCounter][levelCounter];
            levelCounter++;
        } else {
            currentAmount += amountToAdd;
        }
        writeTofileInFormat(context, shieldCounter, levelCounter, currentAmount);
    }

    /**
     * Checks if the user will level up with the addition of the given XP amount.
     *
     * @param amountToAdd The amount of XP points to add.
     * @return {@code true} if a level up will occur, {@code false} otherwise.
     */
    private static boolean isLeveledUpRightNow(int amountToAdd) {
        if (SHIELDS_LEVELS_AMOUNT[shieldCounter][0] == maxRankVal) {
            return false;
        }
        return currentAmount + amountToAdd >= SHIELDS_LEVELS_AMOUNT[shieldCounter][levelCounter];
    }

    /**
     * Checks if the user will rank up with the addition of the given XP amount.
     * A rank up occurs when the user levels up from the last level of the current rank.
     *
     * @param amountToAdd The amount of XP points to add.
     * @return {@code true} if a rank up will occur, {@code false} otherwise.
     */
    private static boolean isRankedUpRightNow(int amountToAdd) {
        if (SHIELDS_LEVELS_AMOUNT[shieldCounter][0] == maxRankVal) {
            return false;
        }
        if (currentAmount + amountToAdd >= SHIELDS_LEVELS_AMOUNT[shieldCounter][levelCounter]) {
            // If leveled up and the level was the last level in the previous rank
            if (levelCounter == SHIELDS_LEVELS_AMOUNT[shieldCounter].length - 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resets the user's XP progress to zero, including rank and level, and updates the progress file.
     *
     * @param context The application context.
     */
    public static void resetAmount(Context context) {
        String reset = "0,0,0";
        levelCounter = 0;
        shieldCounter = 0;
        currentAmount = 0;
        FileHelper.writeToFile(context, reset, PROFRESS_FILE_NAME);
    }

    /**
     * Initializes the XP progress counters (rank, level, current XP) at the start of the application
     * by reading from the progress file. If the file is empty or malformed, it resets the progress.
     *
     * @param context The application context.
     */
    public static void setCountersAtStart(Context context) {
        SHIELDS_LEVELS_AMOUNT[0] = new int[]{20, 20, 30, 50, 50};
        SHIELDS_LEVELS_AMOUNT[1] = new int[]{50, 50, 80, 100, 100};
        SHIELDS_LEVELS_AMOUNT[2] = new int[]{100, 120, 130, 150, 200};
        SHIELDS_LEVELS_AMOUNT[3] = new int[]{maxRankVal};
        String text = FileHelper.readFromFile(context, PROFRESS_FILE_NAME);
        String[] values = text.split(","); // Split the text by commas

        if (values.length < 3) {
            writeTofileInFormat(context, 0, 0, 0);
            setCountersAtStart(context);
            return;
        }

        try {
            shieldCounter = Integer.parseInt(values[0].trim());
            levelCounter = Integer.parseInt(values[1].trim());
            currentAmount = Integer.parseInt(values[2].trim());
        } catch (NumberFormatException e) {
            writeTofileInFormat(context, 0, 0, 0);
            setCountersAtStart(context);
        }
    }

    /**
     * Writes the current rank, level, and XP amount to the progress file in a specific format.
     *
     * @param context    The application context.
     * @param rank       The current rank.
     * @param level      The current level.
     * @param currAmount The current XP amount.
     */
    private static void writeTofileInFormat(Context context, int rank, int level, int currAmount) {
        String format = rank + "," + level + "," + currAmount;
        FileHelper.writeToFile(context, format, PROFRESS_FILE_NAME);
    }

    /**
     * Sets the image of the provided {@link ImageView} to the image corresponding to the user's current rank.
     *
     * @param context   The application context.
     * @param imageView The {@link ImageView} to set the rank image on.
     * @return The {@link ImageView} after setting the image.
     */
    public static ImageView getImgOfCurrentRank2(Context context, ImageView imageView) {
        imageView.setImageResource(ranksImagesIds[shieldCounter]);
        return imageView;
    }
}
