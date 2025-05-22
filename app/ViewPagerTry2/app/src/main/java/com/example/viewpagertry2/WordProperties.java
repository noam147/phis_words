
package com.example.viewpagertry2;

import java.io.Serializable;

/**
 * This class encapsulates the core properties of a word within the application.
 * It stores the word itself, its meaning, a unique identifier for the word,
 * and its origin place. This class implements {@link Serializable} to allow
 * instances of {@code WordProperties} to be easily passed between different
 * components of the application.
 */
public class WordProperties implements Serializable {
    private String word;
    private String meaning;
    private int word_id;
    private String origin_place;

    /**
     * Constructs a new {@code WordProperties} object with the specified attributes.
     *
     * @param word         The word itself.
     * @param meaning      The meaning or definition of the word.
     * @param word_id      A unique identifier for the word within the application's data.
     * @param origin_place The origin or source of the word.
     */
    public WordProperties(String word, String meaning, int word_id, String origin_place) {
        this.word = word;
        this.meaning = meaning;
        this.word_id = word_id;
        this.origin_place = origin_place;
    }

    /**
     * Returns the word.
     *
     * @return The word as a {@link String}.
     */
    public String getWord() {
        return word;
    }

    /**
     * Returns the meaning of the word.
     *
     * @return The meaning as a {@link String}.
     */
    public String getMeaning() {
        return meaning;
    }

    /**
     * Returns the unique identifier of the word.
     *
     * @return The word ID as an {@code int}.
     */
    public int getWord_id() {
        return word_id;
    }

    /**
     * Returns the origin place of the word.
     *
     * @return The origin place as a {@link String}.
     */
    public String getOrigin_place() {
        return origin_place;
    }
}
