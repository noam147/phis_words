package com.example.viewpagertry2;

public class UnitAndCategoryOfWord {
    private int unit;
    private int category;
    private int wordId;

    public UnitAndCategoryOfWord(int wordId) {
        this.wordId = wordId;
        setUnitAndCategory();
    }

    private void setUnitAndCategory() {
        //wordId = 3 - category = 1 unit = 3
        //wordId = 603 - category =2 unit = 3
        category = wordId / (OperationsAndOtherUsefull.WORDS_PER_UNIT * OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY) + 1;
        unit = wordId % (OperationsAndOtherUsefull.WORDS_PER_UNIT * OperationsAndOtherUsefull.NUM_OF_UNITS_IN_EACH_CATEGORY) / OperationsAndOtherUsefull.WORDS_PER_UNIT + 1;
    }

    public int getUnit() {
        return unit;
    }

    public int getCategory() {
        return category;
    }
}
