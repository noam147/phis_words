
package com.example.viewpagertry2;

import java.io.Serializable;

/**
 * This class represents the user's details and interaction status with a specific word.
 * It stores information such as the number of stars assigned to the word, the user's
 * self-assessed knowledge level, and whether the word has been marked by the user.
 * This class implements {@link Serializable} to allow its instances to be easily
 * passed between different components of the application (e.g., Activities, Fragments).
 */
public class UserDetailsOnWords implements Serializable {
    private String word;
    private int amountOfStars;
    private String knowledge_level;
    private boolean isWordMark;

    /**
     * Constructs a new {@code UserDetailsOnWords} object with the provided details.
     *
     * @param word            The word this object pertains to.
     * @param amountOfStars   The number of stars assigned to this word by the user.
     * @param knowledge_level The user's self-assessed knowledge level of this word.
     * @param isWordMark      A boolean indicating whether the user has marked this word.
     */
    public UserDetailsOnWords(String word, int amountOfStars, String knowledge_level, boolean isWordMark) {
        this.word = word;
        this.amountOfStars = amountOfStars;
        this.knowledge_level = knowledge_level;
        this.isWordMark = isWordMark;
    }

    /**
     * Returns the word associated with these user details.
     *
     * @return The word.
     */
    public String getWord() {
        return word;
    }

    /**
     * Returns the number of stars assigned to this word by the user.
     *
     * @return The amount of stars.
     */
    public int getAmountOfStars() {
        return amountOfStars;
    }

    /**
     * Returns the user's self-assessed knowledge level of this word.
     *
     * @return The knowledge level.
     */
    public String getKnowledge_level() {
        return knowledge_level;
    }

    /**
     * Returns a boolean indicating whether the user has marked this word.
     *
     * @return {@code true} if the word is marked, {@code false} otherwise.
     */
    public boolean isWordMark() {
        return isWordMark;
    }

    /**
     * Sets whether the user has marked this word.
     *
     * @param wordMark {@code true} to mark the word, {@code false} to unmark it.
     */
    public void setWordMark(boolean wordMark) {
        isWordMark = wordMark;
    }

    /**
     * Sets the number of stars for this word. If the provided {@code amountOfStars}
     * is equal to {@link OperationsAndOtherUsefull#DID_NOT_SORTAMOUNT_OF_STARS},
     * the method attempts to intelligently set a minimum star value based on the
     * current star count, likely indicating an initial categorization of the word
     * as known or unknown. Otherwise, it directly sets the provided star amount.
     *
     * @param amountOfStars The new amount of stars to set. Use
     * {@link OperationsAndOtherUsefull#DID_NOT_SORTAMOUNT_OF_STARS}
     * to trigger the intelligent assignment of minimum stars.
     */
    public void setAmountOfStars(int amountOfStars) {
        if (amountOfStars == OperationsAndOtherUsefull.DID_NOT_SORTAMOUNT_OF_STARS) {
            if (this.amountOfStars < OperationsAndOtherUsefull.DID_NOT_SORTAMOUNT_OF_STARS) {
                // if this is an increasment (word moved to 'known')
                this.amountOfStars = OperationsAndOtherUsefull.MIN_KNOW_WORD_AMOUNT_OF_STARS;
            } else {
                // if this is a decreasment (word moved to 'not known')
                this.amountOfStars = OperationsAndOtherUsefull.MIN_DO_NOT_KNOW_WORD_AMOUNT_OF_STARS;
            }
            return;
        }
        this.amountOfStars = amountOfStars;
    }

    /**
     * Sets the user's self-assessed knowledge level for this word.
     *
     * @param knowledge_level The new knowledge level to set.
     */
    public void setKnowledge_level(String knowledge_level) {
        this.knowledge_level = knowledge_level;
    }
}
