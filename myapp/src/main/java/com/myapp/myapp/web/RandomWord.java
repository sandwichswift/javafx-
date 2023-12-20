package com.myapp.myapp.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomWord  {
    private static final String DATABASE_URL = "jdbc:sqlite:./dbs/Dictionary.db";
    public static String category;

    public static void main(String[] args) throws Exception {
        /*try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            // 随机抽取一个单词的ID
            int [] randomWordId = getRandomWordId(connection,category,1);

            for(int i : randomWordId){
                // 在数据库中查找该单词的中文含义
                String chineseMeaning = getChineseMeaning(connection, category, randomWordId[i]);

                // 打印或使用中文含义
                System.out.println("中文含义：" + chineseMeaning);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        System.out.println(searchEnglish("喜欢"));
    }

    public static int[] getRandomWordId(Connection connection, String category, int num) throws Exception {
        String selectRandomWordIdSQL = "SELECT id FROM " + category + " WHERE isExtracted IS NULL ORDER BY RANDOM() LIMIT ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectRandomWordIdSQL)) {
            selectStatement.setInt(1, num);//num为随机单词数目
            ResultSet resultSet = selectStatement.executeQuery();

            int[] randomWordId = new int[num];
            int i = 0;
            while (resultSet.next()) {
                randomWordId[i] = resultSet.getInt("id");
                updateWordAsExtracted(connection,randomWordId[i],category);//更新数据库中的isExtracted字段
                i++;
            }
            return randomWordId;
        }
    }

    public static void updateWordAsExtracted(Connection connection, int wordId,String category) throws SQLException {
        // 更新数据库中的 isExtracted 字段
        String updateQuery = "UPDATE " + category + " SET isExtracted = 1 WHERE id = ?";


        PreparedStatement statement = connection.prepareStatement(updateQuery);
        statement.setInt(1, wordId);
        statement.executeUpdate();
    }

    public static String getChineseMeaning(Connection connection, String category, int wordId) throws Exception {
        String selectMeaningSQL = "SELECT translation FROM " + category +" WHERE id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectMeaningSQL)) {
            selectStatement.setInt(1, wordId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                String meaning = resultSet.getString("translation");
                if (meaning == null || meaning.isEmpty()) {
                    // 如果含义为空，则搜索并更新
                    String word = getWordById(connection, category,wordId);
                    String chineseMeaning = searchWordMeaning(word);
                    updateReviewDatabase(connection, wordId, category);
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

    public static String getWordById(Connection connection, String category, int wordId) throws Exception {
        String selectWordSQL = "SELECT word FROM " + category +" WHERE id = ?";
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
    public static String getTypeById(Connection connection, String category, int wordId) throws Exception {
        String selectWordSQL = "SELECT type FROM " + category +" WHERE id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectWordSQL)) {
            selectStatement.setInt(1, wordId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("type");
            } else {
                throw new RuntimeException("Word not found in the database.");
            }
        }
    }

    public static String getMeaningById(Connection connection, String category, int wordId) throws Exception {
        String selectWordSQL = "SELECT translation FROM " + category +" WHERE id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectWordSQL)) {
            selectStatement.setInt(1, wordId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("translation");//translation为中文含义
            } else {
                throw new RuntimeException("Word not found in the database.");
            }
        }
    }

    public static int getReviewTimeById(Connection connection, String category, int wordId) throws Exception {
        String selectWordSQL = "SELECT review_time FROM review" + category +" WHERE id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectWordSQL)) {
            selectStatement.setInt(1, wordId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("review_time");
        }
            return 0;
    }
    }

    //从有道词典中搜索单词的中文含义
    public static String searchWordMeaning(String word) throws IOException {
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
    //从有道词典中获取英文翻译
    public static String searchEnglish(String word) throws IOException{
        String searchUrl = "https://dict.youdao.com/result?word=" + word +"&lang=en";
        Document document = Jsoup.connect(searchUrl).get();

        // 获取搜索结果的摘要部分
        Elements point = document.select("div.trans-ce a.point");//获取英文翻译
        if(!point.isEmpty()){
            int size = point.size();
            List<String> english = new ArrayList<>();
            for(int i = 0;i<size;i++){
                String englishText = point.get(i).text();
                english.add(englishText);
            }
            return String.join("\n",english);
        }
        return "未找到英文翻译或网络异常";
    }
    //更新Review表
    public static void updateReviewDatabase(Connection connection, int wordId, String tableName) throws Exception {
        //先查找Review表中是否有该单词
        String selectWordSQL = "SELECT * FROM review" + tableName +" WHERE id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectWordSQL)) {
            selectStatement.setInt(1, wordId);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                //如果有该单词，则更新review_time
                updateReviewTime(connection, wordId, tableName);
            } else {
                //如果没有该单词，则插入该单词
                String updateMeaningSQL = "INSERT INTO review" + tableName + " (word, translation,type,review_time) VALUES (?, ?, ?, ?)";//将单词和中文含义插入到Review表中
                try (PreparedStatement updateStatement = connection.prepareStatement(updateMeaningSQL)) {
                updateStatement.setString(1, getWordById(connection, tableName, wordId));
                updateStatement.setString(2, getMeaningById(connection, tableName, wordId));
                updateStatement.setString(3, getTypeById(connection, tableName, wordId));
                updateStatement.setInt(4, getReviewTimeById(connection, tableName, wordId) + 1);//review_time加1
                updateStatement.executeUpdate();
        }
            }
        }

    }

    private static void updateReviewTime(Connection connection, int wordId, String tableName) {
        String updateReviewTimeSQL = "UPDATE review" + tableName + " SET review_time = ? WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateReviewTimeSQL)) {
            updateStatement.setInt(1, getReviewTimeById(connection, tableName, wordId) + 1);//review_time加1
            updateStatement.setInt(2, wordId);
            updateStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkNetwork() {
        try {
            Document document = Jsoup.connect("https://www.baidu.com/").get();
            Elements elements = document.select("title");
            if (elements.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }
}

