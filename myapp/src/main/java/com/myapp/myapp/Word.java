package com.myapp.myapp;

public class Word {
    public Word( String word, String meaning,String type) {
        this.word = word;
        this.meaning = meaning;
        this.type = type;
    }
    private int id;
    private String word;
    private String meaning;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



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
