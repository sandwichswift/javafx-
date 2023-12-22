package com.myapp.myapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        // 获取数据库文件的路径，仅用于jar包运行，将数据库放在jar包同一目录下
        String dbPath = System.getProperty("user.dir") + "/dbs/Dictionary.db";//数据库文件路径

        //IDEA运行时使用下面这句
//        String dbPath = DatabaseConnection.class.getResource("Dictionary.db").getPath();
        // 构建数据库连接 URL
        String jdbcUrl = "jdbc:sqlite:" + dbPath + "?journal_mode=WAL&synchronous=OFF";//WAL模式

        System.out.println(jdbcUrl);

        // 连接数据库
        return DriverManager.getConnection(jdbcUrl);
    }

    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("数据库连接成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

