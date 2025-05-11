package com.example.viewpagertry2;

import java.io.Serializable;

public class FinalWordProperties implements Serializable
{
    private  UserDetailsOnWords userDetailsOnWords;
    private WordProperties wordProperties;

    public FinalWordProperties(UserDetailsOnWords userDetailsOnWords, WordProperties wordProperties) {
        this.userDetailsOnWords = userDetailsOnWords;
        this.wordProperties = wordProperties;
    }
    public FinalWordProperties(FinalWordProperties other)
    {
        this.userDetailsOnWords = other.userDetailsOnWords;
        this.wordProperties = other.wordProperties;
    }
    public UserDetailsOnWords getUserDetailsOnWords() {
        return userDetailsOnWords;
    }

    public WordProperties getWordProperties() {
        return wordProperties;
    }

}
