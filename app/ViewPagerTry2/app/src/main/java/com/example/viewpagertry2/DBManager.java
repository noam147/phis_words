package com.example.viewpagertry2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ExercisesPages.Question;


public class DBManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "words.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_PATH = "/data/data/com.example.viewpagertry2/databases/";
    private final Context context;



    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        // Copy the database if it doesn't exist
        try {
            copyDatabaseIfNeeded(false);
        } catch (IOException e) {
            Log.e("DBManager", "Error copying database: " + e.getMessage());
        }
    }
    private void copyDatabaseIfNeeded(boolean overrideCuurentFile) throws IOException
    {
        File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
        if(overrideCuurentFile)
        {
            dbFile.delete();
        }

        // Check if the database already exists
        if (!dbFile.exists()) {
            // Create the database folder if it doesn't exist
            File dbFolder = new File(DATABASE_PATH);
            if (!dbFolder.exists()) {
                dbFolder.mkdirs();
            }

            // Copy the database from assets
            try (InputStream input = context.getAssets().open(DATABASE_NAME);
                 OutputStream output = new FileOutputStream(dbFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                Log.d("DBManager", "Database copied successfully.");
            } catch (IOException e) {
                throw new IOException("Error copying database: " + e.getMessage());
            }
            catch (Exception e)
            {
                int a =5;
            }
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // This method is called when the database is created for the first time.
        // You can define your table creation SQL statements here if needed.
        //Log.d("DBManager", "Database created.");
       // db.execSQL("CREATE TABLE IF NOT EXISTS words (word_id INTEGER PRIMARY KEY, word TEXT, meaning TEXT, word_type TEXT, origin_place TEXT, amountOfStars INTEGER, knowledge_level TEXT, isWordMark INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This method is called when the database needs to be upgraded.
        //Log.d("DBManager", "Database upgraded.");
    }

    public SQLiteDatabase openDb() {
        SQLiteDatabase db = null;  // Initialize db to null
        try {
            db = this.getWritableDatabase(); // Attempt to open the database
            Log.d("DBManager", "Database opened successfully.");
        } catch (Exception e) {
            Log.e("DBManager", "Cannot open database: " + e.getMessage());
        }
        return db; // Return the database (may be null if an error occurred)
    }

    public void closeDb() {
        this.close();
        Log.d("DBManager", "Database closed.");
    }
    private int execCountCommand(String query)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        // Initialize count
        int count = 0;

        // Execute the query and get the result using Cursor
        Cursor cursor =null;

        try {
            cursor=db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                // Get the count from the first column
                count = cursor.getInt(0);
            }
        }
        catch (Exception e)
        {
            int a =0;
        }
        finally {
            // Always close the cursor to avoid memory leaks
            if (cursor != null) {
                cursor.close();
            }
        }
        // Return the count
        return count;
    }
    private void execUpdateQuery(String query)
    {
        SQLiteDatabase db = null;
        SQLiteStatement statement = null;

        try {
            db = this.getWritableDatabase(); // Get a writable database instance
            statement = db.compileStatement(query); // Compile the statement
            // Execute the update
            statement.executeUpdateDelete();
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
        } finally {
            if (statement != null) {
                statement.close(); // Close the statement
            }
            if (db != null) {
                db.close(); // Close the database connection
            }
        }
    }
    public void updateIsWordMarkedBasedOnWord(String word,boolean isMarked)
    {
        //sql injection!
        String query = "UPDATE user_details_on_words SET isWordMark = "+isMarked+" WHERE word = '"+word+"';";
        execUpdateQuery(query);
    }
    public void oppsiteWordMarkedBasedOnWord(String word,boolean isMarked)
    {
    //maybe when user clicks long just upside the word marked beside checking if true or false
    }
    public FinalWordProperties[] getMarkedWords()
    {
        String query = "select * from user_details_on_words where isWordMark = 1";
        //need to fix here

        return execQueryOfBothTablesByUserDetailsValues(query,null);
    }
    private void updateAmountOfStarsBasedOnWord(String word, int newAmount) {
        String query = "UPDATE user_details_on_words SET amountOfStars = ? WHERE word = ?";
        SQLiteDatabase db = null;
        SQLiteStatement statement = null;

        try {
            db = this.getWritableDatabase(); // Get a writable database instance
            statement = db.compileStatement(query); // Compile the statement

            // Bind parameters
            statement.bindLong(1, newAmount); // Bind the new amount of stars to the first placeholder
            statement.bindString(2, word);    // Bind the word to the second placeholder

            // Execute the update
            statement.executeUpdateDelete();
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
        } finally {
            if (statement != null) {
                statement.close(); // Close the statement
            }
            if (db != null) {
                db.close(); // Close the database connection
            }
        }
    }

    private void increaseKnowledeLevel(UserDetailsOnWords userDetailsOnWords)
    {
        //knowlage level - each time user is accor with a word in a game
        //the knowlage level will increase in one
        String word = userDetailsOnWords.getWord();
        int updateKnowledge = Integer.parseInt(userDetailsOnWords.getKnowledge_level())+1;
        String query = "UPDATE englishWords SET knowledge_level = "+updateKnowledge+" where word = "+word+";";
        this.execUpdateQuery(query);
    }
    public void updateAmountOfStarsAndKnowledge(String word,boolean isRight)
    {
        UserDetailsOnWords userDetailsOnWords = getUserDetailsBasedOnWord(word);
        increaseKnowledeLevel(userDetailsOnWords);
        int amountOfStars = userDetailsOnWords.getAmountOfStars();
        String getKnowledge_level = userDetailsOnWords.getKnowledge_level();
        if(isRight)
        {
            if(amountOfStars >= OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS+1)
            {
                //if already the amount is suffcient
                return;
            }
            else
            {
                int newAmount = amountOfStars+1;
                if(newAmount < OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS)
                {
                    newAmount = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
                }
                updateAmountOfStarsBasedOnWord(word,newAmount);
                return;
            }
        }
        else
        {
            if(amountOfStars == OperationsAndOtherUsefull.MAX_DO_NOT_KNOW_WORD_AMOUNT_OF_STARS)
            {
                return;
            }
            else
            {
                int newAmount = amountOfStars-1;
                if(newAmount == OperationsAndOtherUsefull.DID_NOT_SORTAMOUNT_OF_STARS)
                {
                    newAmount--;//the sort is happing just once!
                }
                updateAmountOfStarsBasedOnWord(word,newAmount);
                return;
            }
        }
    }
    public int getCountOfUnitsInACategory(boolean isEnglish,int categoryNum)
    {
        int sumWords = getCountOfWords(isEnglish);
        if(categoryNum == getCountOfCategories(isEnglish))
        {
            int wordsAfterAllOtherUnits = sumWords - (categoryNum-1)*OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY*OperationsAndOtherUsefull.WORDS_PER_UNIT;
            int numOfUnits = wordsAfterAllOtherUnits/OperationsAndOtherUsefull.WORDS_PER_UNIT+1;
            //if the category is the last - the units are not maxed
            return numOfUnits;
        }
        return OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY;
    }
    public int getCountOfCategories(boolean isEnglish)
    {
        int sumWords = getCountOfWords(isEnglish);
        int numCategories = sumWords/(OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY*OperationsAndOtherUsefull.WORDS_PER_UNIT)+1;
        return numCategories;
    }
    public int getCountOfWordsUserKnow() {
        int minAmount = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
        String query;
        // Set the query based on the language
        //problem  - takes words also with hebrew - change db or get rid of hebrew words
        query = "SELECT COUNT(*) FROM user_details_on_words where amountOfStars >= "+minAmount+";";
        return execCountCommand(query);
    }
    public int getCountOfWordsUserKnowInSpecificCategory(int category) {
        int minAmount = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
        int minId = OperationsAndOtherUsefull.getStartIdOfCategory(category);
        int maxId = minId + OperationsAndOtherUsefull.WORDS_PER_UNIT*OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY;
        String query;
        // Set the query based on the language
        //problem  - takes words also with hebrew - change db or get rid of hebrew words
        query = "SELECT COUNT(*) FROM user_details_on_words where amountOfStars >= "+minAmount;
        query += " and word_id > "+minId+" and word_id <= "+maxId+";";
        return execCountCommand(query);
    }
    /*public int getCountOfWordsUserKnowBasedOnCategory(int category,int unitToCheckAmount) {
        int minAmount = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
        int minId = OperationsAndOtherUsefull.getStartIdOfUnit(category,unitToCheckAmount);
        int maxId = minId + OperationsAndOtherUsefull.WORDS_PER_UNIT;
        String query;
        // Set the query based on the language
        //problem  - takes words also with hebrew - change db or get rid of hebrew words
        query = "SELECT COUNT(*) FROM user_details_on_words where amountOfStars >= "+minAmount;
        query += " and word_id > "+minId+" and word_id <= "+maxId+";";
        return execCountCommand(query);
    }*/
    public int getCountOfWordsUserKnowBasedOnCategory(int category,int unitToCheckAmount) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        int minAmount = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
        int minId = OperationsAndOtherUsefull.getStartIdOfUnit(category,unitToCheckAmount);
        int maxId = minId + OperationsAndOtherUsefull.WORDS_PER_UNIT;
        // Query to count words meeting the criteria
        String query = "SELECT COUNT(*) FROM user_details_on_words WHERE  amountOfStars >= ? AND word_id  > ? AND  word_id <= ?";

        // Use try-with-resources to automatically close the cursor
        try (Cursor cursor = db.rawQuery(query, new String[] {
                String.valueOf(minAmount), String.valueOf(minId), String.valueOf(maxId)
        })) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0); // Get the count
            }
        }
        return count;
    }

    public int getCountOfWords(boolean isEnglish) {

        String query;

        // Set the query based on the language
        if (isEnglish) {
            query = "SELECT COUNT(*) FROM englishWords";
        } else {
            query = "SELECT COUNT(*) FROM hebrewWords";
        }
        return execCountCommand(query);
    }

    private WordProperties getWordPropetiesBasedOnWord(String word)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        WordProperties currentWordProper = null;

        String query = "SELECT * FROM englishWords WHERE word = ?";
        Cursor cursor = db.rawQuery(query, new String[]{word});

        if (cursor.moveToFirst()) {
            String origin_place = cursor.getString(cursor.getColumnIndex("origin_place"));
            String meaning = cursor.getString(cursor.getColumnIndex("meaning"));
            int word_id = cursor.getInt(cursor.getColumnIndex("word_id")); // SQLite stores booleans as integers
            currentWordProper = new WordProperties(word, meaning, word_id, origin_place);
        }
        cursor.close();
        return currentWordProper;
    }
    // Get user details based on the word
    private UserDetailsOnWords getUserDetailsBasedOnWord(String word) {
        SQLiteDatabase db = this.getReadableDatabase();
        UserDetailsOnWords currentUserDetails = null;

        String query = "SELECT * FROM user_details_on_words WHERE word = ?";
        Cursor cursor = db.rawQuery(query, new String[]{word});

        if (cursor.moveToFirst()) {
            String knowledgeLevel = cursor.getString(cursor.getColumnIndex("knowledge_level"));
            int amountOfStars = cursor.getInt(cursor.getColumnIndex("amountOfStars"));
            boolean isWordMark = cursor.getInt(cursor.getColumnIndex("isWordMark")) > 0; // SQLite stores booleans as integers
            currentUserDetails = new UserDetailsOnWords(word, amountOfStars, knowledgeLevel, isWordMark);
        }
        cursor.close();
        return currentUserDetails;
    }

    private FinalWordProperties[] execQueryOfBothTablesByUserDetailsValues(String query,String[] selectionArgs)
    {
        List<FinalWordProperties> allWordsProperties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        try
        {
            cursor= db.rawQuery(query,selectionArgs);
        }
        catch (Exception e)
        {
            int a =0;
            return null;
        }

        while (cursor.moveToNext()) {

            // Fetch user details based on the word

            UserDetailsOnWords userDetails = getUserDetailsFromCursor(cursor);
            WordProperties wordProperties = getWordPropetiesBasedOnWord(userDetails.getWord());

            // Combine word properties and user details
            FinalWordProperties finalProperties = new FinalWordProperties(userDetails, wordProperties);
            allWordsProperties.add(finalProperties);
        }

        cursor.close();
        return allWordsProperties.toArray(new FinalWordProperties[0]);
    }
    private FinalWordProperties[] execQueryOfBothTables(String query,String[] selectionArgs)
    {
        List<FinalWordProperties> allWordsProperties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        try
        {
            cursor= db.rawQuery(query,selectionArgs);
        }
        catch (Exception e)
        {
            int a =0;
            return null;
        }

        while (cursor.moveToNext()) {

            // Fetch user details based on the word
            WordProperties wordProperties = getWordPropertiesFromCursor(cursor);
            UserDetailsOnWords userDetails = getUserDetailsBasedOnWord(wordProperties.getWord());

            // Combine word properties and user details
            FinalWordProperties finalProperties = new FinalWordProperties(userDetails, wordProperties);
            allWordsProperties.add(finalProperties);
        }

        cursor.close();
        return allWordsProperties.toArray(new FinalWordProperties[0]);
    }

    private WordProperties getWordPropertiesFromCursor(Cursor cursor)
    {
        String word = cursor.getString(cursor.getColumnIndex("word"));
        String meaning = cursor.getString(cursor.getColumnIndex("meaning"));
        int wordId = cursor.getInt(cursor.getColumnIndex("word_id"));
        String originPlace = cursor.getString(cursor.getColumnIndex("origin_place"));
        WordProperties wordProperties = new WordProperties(word, meaning, wordId, originPlace);
       return wordProperties;
    }
    private UserDetailsOnWords getUserDetailsFromCursor(Cursor cursor)
    {
        String knowledgeLevel = cursor.getString(cursor.getColumnIndex("knowledge_level"));
        String word = cursor.getString(cursor.getColumnIndex("word"));
        int amountOfStars = cursor.getInt(cursor.getColumnIndex("amountOfStars"));
        boolean isWordMark = cursor.getInt(cursor.getColumnIndex("isWordMark")) > 0;
        UserDetailsOnWords userDetailsOnWords = new UserDetailsOnWords(word,amountOfStars, knowledgeLevel, isWordMark);
        return userDetailsOnWords;
    }


    public FinalWordProperties[] searchWordsBasedOnStart(String startRegex,int amount)
    {
        String query = "Select * from  englishWords where word like ? limit "+String.valueOf(amount);
        return execQueryOfBothTables(query,new String[]{startRegex + "%"});
    }
    public void setWordAsKnowWord(String word) {
        updateAmountOfStarsBasedOnWord(word,OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS);
    }
    public void setWordAsDoesNOTKnowWord(String word)
    {
        updateAmountOfStarsBasedOnWord(word,-1);
    }


    public String[] getThreeRandomAnswers(boolean isEnglish, String rightanswer) {

        String query = "";
        if (isEnglish) {
            query = "SELECT meaning FROM englishWords WHERE word != ? ORDER BY RANDOM() LIMIT 3";
        } else {
            query = "SELECT meaning FROM hebrewWords WHERE word != ? ORDER BY RANDOM() LIMIT 3";
        }
        String[] answers = new String[3];
        Cursor cursor = null;
        try {
            // Execute the query
            cursor = this.getReadableDatabase().rawQuery(query, new String[]{rightanswer});

            // Iterate over the cursor to get the words
            int i = 0;
            while (cursor.moveToNext() && i < 3) {
                answers[i] = cursor.getString(0); // Assuming the word is in the first column
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle any exceptions, e.g., logging errors
        } finally {
            if (cursor != null) {
                cursor.close(); // Always close the cursor to avoid memory leaks
            }
        }
        return answers;
    }

    // Get random English words with their details
    public FinalWordProperties[] getRandomEnglishWords(int amount,boolean isEngish) {
        String query = "";
        if(isEngish)
        {query = "SELECT * FROM englishWords ORDER BY RANDOM() LIMIT ?";}
        else
        {query = "SELECT * FROM hebrewWords ORDER BY RANDOM() LIMIT ?";}
        return execQueryOfBothTables(query,new String[]{String.valueOf(amount)});
    }
    public FinalWordProperties[] getWordsOfUnit(int unit,int category,boolean isEnglish,int action) {
            //TO COMPLETE
        //get the start id of words
        int startId = (category-1)*OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY*OperationsAndOtherUsefull.WORDS_PER_UNIT;
        startId += (unit-1) *OperationsAndOtherUsefull.WORDS_PER_UNIT;
        String query = "";

        if(isEnglish)
        {
            query = "SELECT * FROM englishWords ORDER BY word_id LIMIT ? offset ?";
        }
        else
        {
            query = "SELECT * FROM hebrewWords ORDER BY word_id LIMIT ? offset ?";
        }

        return execQueryOfBothTables(query,new String[]{String.valueOf(OperationsAndOtherUsefull.WORDS_PER_UNIT),String.valueOf(startId)});
    }

    public FinalWordProperties[] getWordsOfUnit(int unit,int category,boolean isEnglish) {

        //get the start id of words
        int startId = (category-1)*OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY*OperationsAndOtherUsefull.WORDS_PER_UNIT;
        startId += (unit-1) *OperationsAndOtherUsefull.WORDS_PER_UNIT;
        String query = "";
        if(isEnglish)
        {
            query = "SELECT * FROM englishWords ORDER BY word_id LIMIT ? offset ?";
        }
       else
        {
            query = "SELECT * FROM hebrewWords ORDER BY word_id LIMIT ? offset ?";
        }

        return execQueryOfBothTables(query,new String[]{String.valueOf(OperationsAndOtherUsefull.WORDS_PER_UNIT),String.valueOf(startId)});
    }
}

