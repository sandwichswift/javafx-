package com.myapp.myapp;

public class Word {
    public Word(int id, String word, String meaning) {
        this.id = id;
        this.word = word;
        this.meaning = meaning;
    }
    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String word;
    private String meaning;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
