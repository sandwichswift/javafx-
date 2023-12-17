package com.myapp.myapp.db;

import com.google.gson.*;


import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;

public class JsonToDatabase {
    public static void main(String[] args) {
        String jsonFilePath = "./libs/words_dictionary.json";
        String jdbcUrl = "jdbc:sqlite:./dbs/Dictionary.db";

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            createWordsTable(connection); // 创建数据库表

            insertWordsFromJson(connection, jsonFilePath); // 从JSON文件插入数据
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createWordsTable(Connection connection) throws Exception {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Words (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT NOT NULL," +
                "meaning TEXT)";
        try (PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
            statement.execute();
        }
    }

    private static void insertWordsFromJson(Connection connection, String jsonFilePath) throws Exception {
    JsonParser jsonParser = new JsonParser();

    try (FileReader reader = new FileReader(jsonFilePath)) {
        JsonElement rootElement = jsonParser.parse(reader);

        if (rootElement.isJsonObject()) {
            JsonObject wordsObject = rootElement.getAsJsonObject();

            String insertSQL = "INSERT INTO Words (word) VALUES (?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                for (Map.Entry<String, JsonElement> entry : wordsObject.entrySet()) {
                    String word = entry.getKey();

                    // 插入数据到数据库
                    preparedStatement.setString(1, word);
                    preparedStatement.executeUpdate();
                }
            }
        }
    }
}

}

