package com.myapp.myapp;

import com.myapp.myapp.web.RandomWord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class WordList {
    private List<Word> words;
    private static final String DATABASE_URL = "jdbc:sqlite:./dbs/Dictionary.db";


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
    public static List<Word> generateRandomWords(int num) throws Exception {//生成指定数目随机单词
        Connection conn= DriverManager.getConnection(DATABASE_URL);
        List<Word> words = new ArrayList<Word>();
        int [] randomWordId = RandomWord.getRandomWordId(conn,num);
        for(int i =0 ;i<num;i++){
            String word = RandomWord.getWordById(conn, randomWordId[i]);
            int finalI = i;
            new Thread (()->{
                try {
                    String chineseMeaning = RandomWord.getChineseMeaning(conn, randomWordId[finalI]);
                    words.add(new Word(finalI,word,chineseMeaning));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        return words;
    }
    public WordList(int num) throws Exception {
        Connection conn= DriverManager.getConnection(DATABASE_URL);
        List<Word> words = new ArrayList<Word>();
        int [] randomWordId = RandomWord.getRandomWordId(conn,num);//生成指定数目随机单词
        for(int i =0 ;i<num;i++){
            String word = RandomWord.getWordById(conn, randomWordId[i]);
            int finalI = i;
            new Thread (()->{//多线程,减少查找时间
                try {
                    String chineseMeaning = RandomWord.getChineseMeaning(conn, randomWordId[finalI]);
                    words.add(new Word(finalI,word,chineseMeaning));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        this.words = words;
    }
}