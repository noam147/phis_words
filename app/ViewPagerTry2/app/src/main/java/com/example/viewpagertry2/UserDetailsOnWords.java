package com.example.viewpagertry2;

import java.io.Serializable;

public class UserDetailsOnWords implements Serializable {
    private String word;
    private int amountOfStars;
    private String knowledge_level;
    private boolean isWordMark;

    public UserDetailsOnWords(String word, int amountOfStars, String knowledge_level, boolean isWordMark) {
        this.word = word;
        this.amountOfStars = amountOfStars;
        this.knowledge_level = knowledge_level;
        this.isWordMark = isWordMark;
    }

    public String getWord() {
        return word;
    }

    public int getAmountOfStars() {
        return amountOfStars;
    }

    public String getKnowledge_level() {
        return knowledge_level;
    }

    public boolean isWordMark() {
        return isWordMark;
    }

    public void setWordMark(boolean wordMark) {
        isWordMark = wordMark;
    }

    public void setAmountOfStars(int amountOfStars) {
        if(amountOfStars == OperationsAndOtherUsefull.DID_NOT_SORTAMOUNT_OF_STARS)
        {
            if(this.amountOfStars < OperationsAndOtherUsefull.DID_NOT_SORTAMOUNT_OF_STARS)
            {
                //if this is an increasment
                this.amountOfStars = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
            }
            else
            {
                this.amountOfStars = OperationsAndOtherUsefull.MIN_DO_NOT_KNOW_WORD_AMOUNT_OF_STARS;
            }
            return;
        }
        this.amountOfStars = amountOfStars;
    }

    public void setKnowledge_level(String knowledge_level) {
        this.knowledge_level = knowledge_level;
    }
}
