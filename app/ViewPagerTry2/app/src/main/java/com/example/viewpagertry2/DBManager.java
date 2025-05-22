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

    /**
     * Constructs a new DBManager instance. It initializes the SQLiteOpenHelper with the database name and version,
     * and attempts to copy the database from the application's assets if it doesn't already exist.
     *
     * @param context The application context.
     */
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

    /**
     * Copies the database file from the application's assets folder to the device's database directory,
     * but only if the database file does not already exist. If {@code overrideCuurentFile} is true,
     * it will delete the existing database file before attempting to copy.
     *
     * @param overrideCuurentFile A boolean indicating whether to override the existing database file if it exists.
     * @throws IOException If an error occurs during the file copying process.
     */
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
                Log.e("DBManager", "An unexpected error occurred during database copy: " + e.getMessage());
            }
        }
    }

    /**
     * Called when the database is created for the first time.
     * This method is empty in this implementation, as table creation is assumed to be handled
     * by the pre-existing database file copied from assets.
     *
     * @param db The SQLite database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // This method is called when the database is created for the first time.
        // You can define your table creation SQL statements here if needed.
        //Log.d("DBManager", "Database created.");
        // db.execSQL("CREATE TABLE IF NOT EXISTS words (word_id INTEGER PRIMARY KEY, word TEXT, meaning TEXT, word_type TEXT, origin_place TEXT, amountOfStars INTEGER, knowledge_level TEXT, isWordMark INTEGER)");
    }

    /**
     * Called when the database needs to be upgraded.
     * This method is empty in this implementation, as database upgrades are not explicitly handled.
     *
     * @param db         The SQLite database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This method is called when the database needs to be upgraded.
        //Log.d("DBManager", "Database upgraded.");
    }

    /**
     * Opens the database for writing. If the database cannot be opened, an error message is logged.
     *
     * @return A writable SQLiteDatabase instance, or null if an error occurred.
     */
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

    /**
     * Closes the database connection.
     */
    public void closeDb() {
        this.close();
        Log.d("DBManager", "Database closed.");
    }

    /**
     * Executes a raw SQL query that is expected to return a single integer count.
     * It retrieves the count from the first column of the first row of the result.
     * The cursor is always closed in a finally block to prevent memory leaks.
     *
     * @param query The raw SQL query to execute (e.g., "SELECT COUNT(*) FROM ...").
     * @return The integer count returned by the query, or 0 if an error occurs or the cursor is empty.
     */
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
            Log.e("DBManager", "Error executing count query: " + e.getMessage());
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

    /**
     * Executes a raw SQL query that performs an update or delete operation on the database.
     * It compiles the query into an SQLiteStatement and executes it.
     * The database and statement are always closed in a finally block.
     *
     * @param query The raw SQL query to execute (e.g., "UPDATE ... SET ... WHERE ...", "DELETE FROM ... WHERE ...").
     */
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
            Log.e("DBManager", "Error executing update query: " + e.getMessage());
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
    /**
     * Retrieves an array of {@link FinalWordProperties} representing words that the user has marked.
     * It queries the 'user_details_on_words' table for entries where the 'isWordMark' flag is set to 1.
     *
     * @return An array of {@link FinalWordProperties} for marked words, or an empty array if no words are marked.
     */
    public FinalWordProperties[] getMarkedWords()
    {
        String query = "select * from user_details_on_words where isWordMark = 1";
        //need to fix here

        return execQueryOfBothTablesByUserDetailsValues(query,null);
    }
    /**
     * Updates the 'amountOfStars' for a specific word in the 'user_details_on_words' table.
     * It uses a parameterized query to prevent SQL injection.
     *
     * @param word      The word for which to update the star amount.
     * @param newAmount The new amount of stars to set for the word.
     */
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

    /**
     * Increases the knowledge level of a word in the 'englishWords' table.
     * The knowledge level is incremented by one based on the current knowledge level stored in the provided {@link UserDetailsOnWords} object.
     *
     * @param userDetailsOnWords The {@link UserDetailsOnWords} object containing the current knowledge level and the word.
     */
    private void increaseKnowledeLevel(UserDetailsOnWords userDetailsOnWords)
    {
        //knowlage level - each time user is accor with a word in a game
        //the knowlage level will increase in one
        String word = userDetailsOnWords.getWord();
        int updateKnowledge = Integer.parseInt(userDetailsOnWords.getKnowledge_level())+1;
        String query = "UPDATE englishWords SET knowledge_level = "+updateKnowledge+" where word = "+word+";";
        this.execUpdateQuery(query);
    }
    /**
     * Updates the amount of stars and knowledge level for a given word based on whether the user's answer was correct.
     * If the answer is correct ('isRight' is true), it increases the 'amountOfStars' if it's below a certain threshold.
     * If the answer is incorrect ('isRight' is false), it decreases the 'amountOfStars' if it's above a certain threshold.
     * It also calls {@link #increaseKnowledeLevel(UserDetailsOnWords)} to increment the word's knowledge level.
     *
     * @param word    The word to update.
     * @param isRight A boolean indicating whether the user's answer was correct (true) or incorrect (false).
     */
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
    /**
     * Calculates the number of units within a specific category for a given language.
     * If the provided category number is the last category, it calculates the remaining units based on the total number of words.
     * Otherwise, it returns a predefined constant for the number of units per category.
     *
     * @param isEnglish   A boolean indicating whether to consider English words (true) or another language (false).
     * @param categoryNum The index of the category for which to count the units.
     * @return The number of units in the specified category.
     */
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
    /**
     * Calculates the total number of categories for a given language based on the total number of words
     * and the predefined number of units and words per unit in each category.
     *
     * @param isEnglish A boolean indicating whether to consider English words (true) or another language (false).
     * @return The total number of categories.
     */
    public int getCountOfCategories(boolean isEnglish)
    {
        int sumWords = getCountOfWords(isEnglish);
        int numCategories = sumWords/(OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY*OperationsAndOtherUsefull.WORDS_PER_UNIT)+1;
        return numCategories;
    }
    /**
     * Retrieves the count of words that the user knows based on their 'amountOfStars' in the 'user_details_on_words' table.
     * It considers words with an 'amountOfStars' greater than or equal to a predefined minimum value as known.
     * <p>
     * Note: The comment "//problem  - takes words also with hebrew - change db or get rid of hebrew words" indicates a potential issue where the query might include words from other languages if they exist in the 'user_details_on_words' table.
     * </p>
     *
     * @return The number of words the user knows.
     */
    public int getCountOfWordsUserKnow() {
        int minAmount = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
        String query;
        // Set the query based on the language
        //problem  - takes words also with hebrew - change db or get rid of hebrew words
        query = "SELECT COUNT(*) FROM user_details_on_words where amountOfStars >= "+minAmount+";";
        return execCountCommand(query);
    }
    /**
     * Retrieves the count of words that the user knows within a specific category.
     * It filters words based on their 'amountOfStars' and their 'word_id' range corresponding to the given category.
     * <p>
     * Note: The comment "//problem  - takes words also with hebrew - change db or get rid of hebrew words" indicates a potential issue where the query might include words from other languages if they exist in the 'user_details_on_words' table.
     * </p>
     *
     * @param category The index of the category to filter by.
     * @return The number of known words in the specified category.
     */
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

    /**
     * Retrieves the count of words that the user knows within a specific unit of a category.
     * It filters words based on their 'amountOfStars' and their 'word_id' range corresponding to the given category and unit.
     *
     * @param category         The index of the category.
     * @param unitToCheckAmount The index of the unit within the category.
     * @return The number of known words in the specified unit of the category.
     */
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

    /**
     * Retrieves the total count of words in either the 'englishWords' or 'hebrewWords' table based on the provided language flag.
     *
     * @param isEnglish A boolean indicating whether to count English words (true) or Hebrew words (false).
     * @return The total number of words in the specified language table.
     */
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

    /**
     * Retrieves the properties of a single word from the 'englishWords' table based on the word itself.
     *
     * @param word The English word to retrieve properties for.
     * @return A {@link WordProperties} object containing the word's ID, meaning, and origin place, or null if the word is not found.
     */
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
    /**
     * Retrieves the user-specific details for a given word from the 'user_details_on_words' table.
     *
     * @param word The word to retrieve user details for.
     * @return A {@link UserDetailsOnWords} object containing the word's amount of stars, knowledge level, and marked status, or null if the word is not found in the user details table.
     */
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

    /**
     * Executes a raw SQL query that potentially joins data from multiple tables (specifically implied to be 'user_details_on_words' and 'englishWords' or similar)
     * based on user details values. It iterates through the cursor, retrieves user details and corresponding word properties,
     * and combines them into {@link FinalWordProperties} objects.
     *
     * @param query         The raw SQL query to execute.
     * @param selectionArgs Optional arguments for the query, to prevent SQL injection.
     * @return An array of {@link FinalWordProperties} representing the combined data, or null if an exception occurs during the query execution.
     */
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

