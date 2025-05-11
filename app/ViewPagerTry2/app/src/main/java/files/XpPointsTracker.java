package files;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.example.viewpagertry2.R;

import OfflineActivities.ShowRankProgressActivity;

public class XpPointsTracker
{
    public static final String PROFRESS_FILE_NAME = "progressFile.txt";

    //File Data Format: int-rank +"," int-level+","+int-current points
    private static final int[][] SHIELDS_LEVELS_AMOUNT = new int[4][];
    public static final int NUMBER_OF_LEVELS_IN_EACH_SHIELD = 5;
    public static final int[] ranksImagesIds = new int[]{R.drawable.bronze_shield,R.drawable.cyan_shield,R.drawable.purple_shield,R.drawable.purple_with_flame_shield};
    private static int maxRankVal = -1;
    private static int levelCounter = -1;
    private static int shieldCounter = -1;
    private static int currentAmount = -1;


    public static int getCurrentAmount() {
        return currentAmount;
    }
    public static int getCurrentLevel() {
        return levelCounter;
    }
    public static int getCurrentRank() {
        return shieldCounter;
    }
    public static int getAmountForNextLevel() {
        return SHIELDS_LEVELS_AMOUNT[shieldCounter][levelCounter];
    }
    public static int getAmountForProgressBarNextLevel() {
        int nextLevelAmount = getAmountForNextLevel();
        //assumption: currentAmount < nextLevelAmount
        float division = (float) currentAmount/nextLevelAmount;
        int progressBarValue = (int)(division*100);
        return progressBarValue;
    }
    public static void addOrSubToProgressBar2(int amountToAdd, Context context)
    {
        //resetAmount(context);
        if(isRankedUpRightNow(amountToAdd))
        {
            Intent intent = new Intent(context, ShowRankProgressActivity.class);

            currentAmount = currentAmount+amountToAdd - SHIELDS_LEVELS_AMOUNT[shieldCounter][levelCounter];
            shieldCounter++;
            levelCounter = 0;
            if(shieldCounter == SHIELDS_LEVELS_AMOUNT.length)
            {
                //put it to max one o something
            }
            intent.putExtra("shield",shieldCounter);
            context.startActivity(intent);
        }
        else if(isLeveledUpRightNow(amountToAdd))
        {
            currentAmount = currentAmount+amountToAdd - SHIELDS_LEVELS_AMOUNT[shieldCounter][levelCounter];
            levelCounter++;
        }
        else
        {
            currentAmount += amountToAdd;
        }
        writeTofileInFormat(context,shieldCounter,levelCounter,currentAmount);
        //setAmount(context,integerAmount+amountToAdd);
        //String dataToFile = Integer.toString(integerAmount + amountToAdd);
        //FileHelper.writeToFile(context,dataToFile,PROFRESS_FILE_NAME);
    }

    private static boolean isLeveledUpRightNow(int amountToAdd)
    {
        if(SHIELDS_LEVELS_AMOUNT[shieldCounter][0] == maxRankVal)
        {
            return false;
        }

        if(currentAmount+amountToAdd >= SHIELDS_LEVELS_AMOUNT[shieldCounter][levelCounter])
        {
            return true;
        }
        //if level up go to screen of conrutgalation or something
        return false;
    }
    private static boolean isRankedUpRightNow(int amountToAdd)
    {
        if(SHIELDS_LEVELS_AMOUNT[shieldCounter][0] == maxRankVal)
        {
            return false;
        }
        if(currentAmount+amountToAdd >= SHIELDS_LEVELS_AMOUNT[shieldCounter][levelCounter])
        {
            //if leveled up and the level was the last level in the pre rank
            if(levelCounter == SHIELDS_LEVELS_AMOUNT[shieldCounter].length-1)
            {
                return true;
            }
        }
        //if level up go to screen of conrutgalation or something
        return false;
    }
    public static void resetAmount(Context context)
    {
        String reset = "0,0,0";
        levelCounter = 0;
        shieldCounter = 0;
        currentAmount =0;
        FileHelper.writeToFile(context,reset,PROFRESS_FILE_NAME);
        //setAmount(context,0);

    }
    public static void setCountersAtStart(Context context) {
        SHIELDS_LEVELS_AMOUNT[0] =  new int[]{20,20,30,50,50};
        SHIELDS_LEVELS_AMOUNT[1] =  new int[]{50,50,80,100,100};
        SHIELDS_LEVELS_AMOUNT[2] =  new int[]{100,120,130,150,200};
        SHIELDS_LEVELS_AMOUNT[3] = new int[]{maxRankVal};
        String text = FileHelper.readFromFile(context, PROFRESS_FILE_NAME);
        String[] values = text.split(","); // Split the text by commas

        if (values.length < 3) {
            writeTofileInFormat(context,0,0,0);
            setCountersAtStart(context);
        }

        try {
            shieldCounter = Integer.parseInt(values[0].trim());
            levelCounter = Integer.parseInt(values[1].trim());
            currentAmount = Integer.parseInt(values[2].trim());

            // You can now use rank, level, and currAmount as needed
            // Return a meaningful value if needed
            return;
        } catch (NumberFormatException e) {
            writeTofileInFormat(context,0,0,0);
            setCountersAtStart(context);
            //e.printStackTrace();
            //return -1; // Error code for invalid format
        }
    }
    private static void writeTofileInFormat(Context context,int rank,int level,int currAmount)
    {
        String format = rank+","+level+","+currAmount;
        FileHelper.writeToFile(context,format,PROFRESS_FILE_NAME);
    }


    public static ImageView getImgOfCurrentRank2(Context context,ImageView imageView)
    {
        imageView.setImageResource(ranksImagesIds[shieldCounter]);
        return imageView;

    }


}
