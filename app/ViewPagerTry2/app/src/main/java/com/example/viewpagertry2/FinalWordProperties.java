
package com.example.viewpagertry2;

import java.io.Serializable;

/**
 * This class combines user-specific details about a word ({@link UserDetailsOnWords})
 * with the core properties of the word itself ({@link WordProperties}). It acts as a
 * container to hold all relevant information about a word for display and processing
 * within the application. This class implements {@link Serializable} to allow its
 * instances to be easily passed between different components of the application.
 */
public class FinalWordProperties implements Serializable {
    private UserDetailsOnWords userDetailsOnWords;
    private WordProperties wordProperties;

    /**
     * Constructs a new {@code FinalWordProperties} object with the provided user details
     * and word properties.
     *
     * @param userDetailsOnWords The user-specific details for the word.
     * @param wordProperties     The core properties of the word.
     */
    public FinalWordProperties(UserDetailsOnWords userDetailsOnWords, WordProperties wordProperties) {
        this.userDetailsOnWords = userDetailsOnWords;
        this.wordProperties = wordProperties;
    }

    /**
     * Constructs a new {@code FinalWordProperties} object by copying the properties
     * from another {@code FinalWordProperties} object. This is a copy constructor.
     *
     * @param other The {@code FinalWordProperties} object to copy from.
     */
    public FinalWordProperties(FinalWordProperties other) {
        this.userDetailsOnWords = other.userDetailsOnWords;
        this.wordProperties = other.wordProperties;
    }

    /**
     * Returns the user-specific details associated with this word.
     *
     * @return An instance of {@link UserDetailsOnWords}.
     */
    public UserDetailsOnWords getUserDetailsOnWords() {
        return userDetailsOnWords;
    }

    /**
     * Returns the core properties of this word.
     *
     * @return An instance of {@link WordProperties}.
     */
    public WordProperties getWordProperties() {
        return wordProperties;
    }
}
