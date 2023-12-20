package com.myapp.myapp;

import com.myapp.myapp.web.RandomWord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class WordList {
    private List<Word> words;
    private String category;
    private static final String DATABASE_URL = "jdbc:sqlite:./dbs/Dictionary.db";

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public WordList(List<Word> words) {
        this.words = words;
    }

    public void addWord(Word word) {
        words.add(word);
    }
    public void removeWord(Word word) {
        words.remove(word);
    }
    public Word getRandomWord() {
        int index = (int) (Math.random() * words.size());
        System.out.println(words.get(index).getWord());
        return words.get(index);
    }
    public static List<Word> generateRandomWords(String category, int num) throws Exception {//生成指定数目随机单词
        Connection conn= DriverManager.getConnection(DATABASE_URL);
        List<Word> words = new ArrayList<Word>();
        int [] randomWordId = RandomWord.getRandomWordId(conn,category,num);
        for(int i =0 ;i<num;i++){
            String word = RandomWord.getWordById(conn,category, randomWordId[i]);
            String meaning = RandomWord.getChineseMeaning(conn,category,randomWordId[i]);
            String type = RandomWord.getTypeById(conn,category,randomWordId[i]);
            words.add(new Word(randomWordId[i], word,meaning,type));
        }
        return words;
    }
    public WordList(String category,int num) throws Exception {
        this.category = category;
        Connection conn= DriverManager.getConnection(DATABASE_URL);
        List<Word> words = new ArrayList<Word>();
        int [] randomWordId = RandomWord.getRandomWordId(conn,category,num);
        for(int i =0 ;i<num;i++){
            String word = RandomWord.getWordById(conn,category, randomWordId[i]);
            String meaning = RandomWord.getChineseMeaning(conn,category,randomWordId[i]);
            String type = RandomWord.getTypeById(conn,category,randomWordId[i]);
            words.add(new Word(randomWordId[i], word,meaning,type));
        }
        this.words = words;
    }
    public WordList(String category) throws Exception {//生成数据库中所有单词
        this.category = category;
        Connection conn= DriverManager.getConnection(DATABASE_URL);
        List<Word> words = new ArrayList<Word>();
        String countSQL= "SELECT COUNT(id) FROM " + category;
        PreparedStatement countStatement= conn.prepareStatement(countSQL);
        countStatement.execute();
        int count = countStatement.getResultSet().getInt(1);
        int [] randomWordId = RandomWord.getRandomWordId(conn,category,count);
        for(int i =0 ;i<count;i++){
            String word = RandomWord.getWordById(conn,category, randomWordId[i]);
            String meaning = RandomWord.getChineseMeaning(conn,category,randomWordId[i]);
            String type = RandomWord.getTypeById(conn,category,randomWordId[i]);
            words.add(new Word(randomWordId[i], word,meaning,type));
        }
        this.words = words;
    }
}