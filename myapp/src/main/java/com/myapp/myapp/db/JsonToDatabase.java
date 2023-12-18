package com.myapp.myapp.db;

import com.google.gson.*;


import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;

public class JsonToDatabase {
    public static String jsonFilePath = "./libs/words_dictionary.json";//全部37w单词
    public static String jsonFilePath_junior = "./libs/json/1-初中-顺序.json";//初中词汇
    public static String jsonFilePath_senior = "./libs/json/2-高中-顺序.json";//高中词汇
    public static String jsonFilePath_CET4 = "./libs/json/3-CET4-顺序.json";//四级词汇
    public static String jsonFilePath_CET6 = "./libs/json/4-CET6-顺序.json";//六级词汇
    public static String jsonFilePath_GRE = "./libs/json/5-考研-顺序.json";//GRE词汇
    public static String jsonFilePath_TOEFL = "./libs/json/6-托福-顺序.json";//托福词汇
    public static String jdbcUrl = "jdbc:sqlite:./dbs/Dictionary.db";
    public static String[] jsons = {jsonFilePath_junior,jsonFilePath_senior,jsonFilePath_CET4,jsonFilePath_CET6,jsonFilePath_GRE,jsonFilePath_TOEFL};
    public static String[] tableNames = {"words_junior","words_senior","words_CET4","words_CET6","words_GRE","words_TOEFL"};
    /*public static void main(String[] args) {
        Thread threads[] = new Thread[jsons.length];
        for(int i = 0;i < jsons.length;i++){
            int finalI = i;
            threads[i]= new Thread(()->{
                try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
                createWordsTable(connection,tableNames[finalI]); // 创建数据库表
                insertWordsAndTranslationsFromJson(connection, jsons[finalI],tableNames[finalI]); // 从JSON文件插入数据
                } catch (Exception e) {
                    e.printStackTrace();
                }
                    });
            threads[i].start();
        }
        for(int i = 0;i < jsons.length;i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/
    public static void main(String[] args) {
        for(int i = 0;i < jsons.length;i++){
            try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
                createWordsTable(connection,tableNames[i]); // 创建数据库表
                //insertWordsAndTranslationsFromJson(connection, jsons[i],tableNames[i]); // 从JSON文件插入数据
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void createWordsTable(Connection connection,String tableName) throws Exception {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT NOT NULL," +
                "translation TEXT," +
                "type TEXT)";
        String createReviewTableSQL = "CREATE TABLE IF NOT EXISTS review" + tableName + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT NOT NULL," +
                "translation TEXT," +
                "type TEXT," +
                "review_time INTEGER)";
        try (PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
            statement.execute();
        }
        try (PreparedStatement statement = connection.prepareStatement(createReviewTableSQL)) {
            statement.execute();
        }
    }


    private static void insertWordsAndTranslationsFromJson(Connection connection, String jsonFilePath, String tableName) {
        JsonParser jsonParser = new JsonParser();

        try (FileReader reader = new FileReader(jsonFilePath)) {
            JsonArray wordsArray = jsonParser.parse(reader).getAsJsonArray();

            // 插入数据到 Words 表
            for (int i = 0; i < wordsArray.size(); i++) {
                JsonObject wordObject = wordsArray.get(i).getAsJsonObject();
                insertWord(connection, wordObject, tableName);
                System.out.println(i +"/" + wordsArray.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertWord(Connection connection, JsonObject wordObject, String tableName) throws Exception {
        String insertWordSQL = "INSERT INTO " + tableName + " (word, translation, type) VALUES (?, ?, ?)";

        String word = wordObject.get("word").getAsString();


        JsonArray translationsArray = wordObject.getAsJsonArray("translations");
        for (int j = 0; j < translationsArray.size(); j++) {
            JsonObject translationObject = translationsArray.get(j).getAsJsonObject();

            String translation;
            if(translationObject.get("translation") == null){//有些单词没有translation
                translation = "null";
            }
            else translation = translationObject.get("translation").getAsString();

            String type;
            if(translationObject.get("type") == null){//有些单词没有type
                type = "null";
            }
            else type = translationObject.get("type").getAsString();

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertWordSQL)) {
                preparedStatement.setString(1, word);
                preparedStatement.setString(2, translation);
                preparedStatement.setString(3, type);

                preparedStatement.executeUpdate();
            }
        }
    }

}

