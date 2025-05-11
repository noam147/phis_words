package com.example.viewpagertry2;

import java.io.Serializable;

public class WordProperties implements Serializable {
    private String word;
    private String meaning;
    private int word_id;
    private String origin_place;

    public WordProperties(String word, String meaning, int word_id, String origin_place) {
        this.word = word;
        this.meaning = meaning;
        this.word_id = word_id;
        this.origin_place = origin_place;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public int getWord_id() {
        return word_id;
    }

    public String getOrigin_place() {
        return origin_place;
    }
}
