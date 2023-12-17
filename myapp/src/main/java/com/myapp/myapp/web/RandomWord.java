package com.myapp.myapp.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomWord  {
    private static final String DATABASE_URL = "jdbc:sqlite:./dbs/Dictionary.db";

    private static final String CREATE_TABLE_SQL1 = "CREATE TABLE IF NOT EXISTS Words (\n"
            + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + "    word TEXT NOT NULL,\n"
            + "    meaning TEXT\n"
            + ");";
    private static final String CREATE_TABLE_SQL2 = "CREATE TABLE IF NOT EXISTS Review (\n"
            + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + "    word TEXT NOT NULL,\n"
            + "    meaning TEXT NOT NULL\n"
            + ");";
    private static void initializeDatabase() throws Exception {//初始化数据库,创建两个表
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            try (PreparedStatement createTableStatement = connection.prepareStatement(CREATE_TABLE_SQL1)) {
                createTableStatement.executeUpdate();
            }
            try (PreparedStatement createTableStatement = connection.prepareStatement(CREATE_TABLE_SQL2)) {
                createTableStatement.executeUpdate();
            }
        }
    }
    public static void main(String[] args) throws Exception {
        initializeDatabase();
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            // 随机抽取一个单词的ID
            int [] randomWordId = getRandomWordId(connection,1);

            for(int i : randomWordId){
                // 在数据库中查找该单词的中文含义
                String chineseMeaning = getChineseMeaning(connection, randomWordId[i]);

                // 打印或使用中文含义
                System.out.println("中文含义：" + chineseMeaning);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int[] getRandomWordId(Connection connection, int num) throws Exception {
        String selectRandomWordIdSQL = "SELECT id FROM Words ORDER BY RANDOM() LIMIT ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectRandomWordIdSQL)) {
            selectStatement.setInt(1, num);//num为随机单词数目
            ResultSet resultSet = selectStatement.executeQuery();

            int[] randomWordId = new int[num];
            int i = 0;
            while (resultSet.next()) {
                randomWordId[i] = resultSet.getInt("id");
                i++;
            }
            return randomWordId;
        }
    }

    public static String getChineseMeaning(Connection connection, int wordId) throws Exception {
        String selectMeaningSQL = "SELECT meaning FROM Words WHERE id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectMeaningSQL)) {
            selectStatement.setInt(1, wordId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                String meaning = resultSet.getString("meaning");
                if (meaning == null || meaning.isEmpty()) {
                    // 如果含义为空，则搜索并更新
                    String word = getWordById(connection, wordId);
                    String chineseMeaning = searchWordMeaning(word);
                    updateMeaningInDatabase(connection, wordId, chineseMeaning);
                    return chineseMeaning;
                } else {
                    // 含义不为空，直接返回
                    return meaning;
                }
            } else {
                throw new RuntimeException("Word not found in the database.");
            }
        }
    }

    public static String getWordById(Connection connection, int wordId) throws Exception {
        String selectWordSQL = "SELECT word FROM Words WHERE id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectWordSQL)) {
            selectStatement.setInt(1, wordId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("word");
            } else {
                throw new RuntimeException("Word not found in the database.");
            }
        }
    }


    private static String searchWordMeaning(String word) throws IOException {
        String searchUrl = "https://dict.youdao.com/result?word=" + word +"&lang=en";
        Document document = Jsoup.connect(searchUrl).get();

        // 获取搜索结果的摘要部分
        Elements trans = document.select("span.trans");
        Elements pos = document.select("span.pos");
        if (!trans.isEmpty() && !pos.isEmpty()) {
            // 创建一个列表来存储 pos 和 trans 的对应关系
            List<String> meanings = new ArrayList<>();

            // 遍历 pos 和 trans，并将它们一一对应
            int j = Math.min(pos.size(), trans.size());
            for (int i = 0; i < j; i++) {
                String posText = pos.get(i).text();
                String transText = trans.get(i).text();
                String meaning = posText + " " + transText;
                meanings.add(meaning);
            }

            // 返回结果列表
            return String.join("\n", meanings);
        }

        return "未找到中文含义或网络异常";
    }

    private static void updateMeaningInDatabase(Connection connection, int wordId, String chineseMeaning) throws Exception {
        String updateMeaningSQL1 = "UPDATE Words SET meaning = ? WHERE id = ?";//更新单词的中文含义
        String updateMeaningSQL2 = "INSERT INTO Review (word, meaning) VALUES (?, ?)";//将单词和中文含义插入到Review表中
        try (PreparedStatement updateStatement = connection.prepareStatement(updateMeaningSQL1)) {
            updateStatement.setString(1, chineseMeaning);
            updateStatement.setInt(2, wordId);
            updateStatement.executeUpdate();
        }
        try (PreparedStatement updateStatement = connection.prepareStatement(updateMeaningSQL2)) {
            updateStatement.setString(1, getWordById(connection, wordId));
            updateStatement.setString(2, chineseMeaning);
            updateStatement.executeUpdate();
        }
    }
}

