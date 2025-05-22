
package com.example.viewpagertry2;

/**
 * This class determines the unit and category of a word based on its unique word ID.
 * It uses predefined constants from {@link OperationsAndOtherUsefull} to perform
 * the calculation and assign the word to a specific unit and category.
 */
public class UnitAndCategoryOfWord {
    private int unit;
    private int category;
    private int wordId;

    /**
     * Constructs a new {@code UnitAndCategoryOfWord} object with the given word ID.
     * Upon creation, it automatically calculates and sets the unit and category
     * for this word ID.
     *
     * @param wordId The unique identifier of the word.
     */
    public UnitAndCategoryOfWord(int wordId) {
        this.wordId = wordId;
        setUnitAndCategory();
    }

    /**
     * Calculates and sets the unit and category of the word based on its {@code wordId}
     * using constants defined in {@link OperationsAndOtherUsefull}. The category is
     * determined by dividing the {@code wordId} by the total number of words per
     * category and adding 1. The unit is determined by taking the remainder of the
     * {@code wordId} divided by the total words per category, then dividing that
     * by the number of words per unit, and finally adding 1.
     *
     * <p>Example calculations based on the comments in the original code:
     * <ul>
     * <li>If {@code wordId} is 3, with {@code WORDS_PER_UNIT} and
     * {@code NUM_OF_UNITS_IN_EACH_CATEGORY} as assumed values, the category
     * and unit will be calculated accordingly.</li>
     * <li>If {@code wordId} is 603, the category and unit will be calculated
     * based on its position within the larger set of words.</li>
     * </ul>
     *
     * <p>It's crucial that {@link OperationsAndOtherUsefull#WORDS_PER_UNIT} and
     * {@link OperationsAndOtherUsefull#NUM_OF_UNITS_IN_EACH_CATEGORY} are correctly
     * defined for this logic to work as intended.
     */
    private void setUnitAndCategory() {
        category = wordId / (OperationsAndOtherUsefull.WORDS_PER_UNIT * OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY) + 1;
        unit = wordId % (OperationsAndOtherUsefull.WORDS_PER_UNIT * OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY) / OperationsAndOtherUsefull.WORDS_PER_UNIT + 1;
    }

    /**
     * Returns the unit to which the word belongs.
     *
     * @return The unit of the word as an {@code int}.
     */
    public int getUnit() {
        return unit;
    }

    /**
     * Returns the category to which the word belongs.
     *
     * @return The category of the word as an {@code int}.
     */
    public int getCategory() {
        return category;
    }
}
