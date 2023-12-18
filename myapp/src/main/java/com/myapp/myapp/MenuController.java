package com.myapp.myapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Menu;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    @FXML
    private Button buttonA;
    @FXML
    private Button buttonB;
    @FXML
    private Button buttonC;
    @FXML
    private Button buttonD;
    @FXML
    private Label labelA;
    @FXML
    private Label labelB;
    @FXML
    private Label labelC;
    @FXML
    private Label labelD;
    @FXML
    private Button formerButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private Label judgeLabel;
    @FXML
    private ChoiceBox<Integer> numberOfWords;
    @FXML
    private MenuItem junior;
    @FXML
    private MenuItem senior;
    @FXML
    private MenuItem gre;
    @FXML
    private MenuItem toefl;
    @FXML
    private MenuItem cet4;
    @FXML
    private MenuItem cet6;
    private Integer[] numberOfWordsInt = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    private String jdbcURL ="./dbs/Dictionary.db";
    private WordList wordList;
    private Word currentWord;
    private int num = 50;//默认单词数
    private String category;
    private Connection conn;

    private MyMenu application;

    public MenuController() throws SQLException {
    }


    public void setApplication(MyMenu mainApp) {
        this.application = mainApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numberOfWords.getItems().addAll(numberOfWordsInt);
        numberOfWords.setOnAction(event ->{
            num = numberOfWords.getValue();
            try {
                wordList = new WordList(category,num);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        junior.setOnAction(event -> {
            category = "words_junior";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().size());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        senior.setOnAction(event -> {
            category = "words_senior";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().size());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        gre.setOnAction(event -> {
            category = "words_GRE";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().size());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        toefl.setOnAction(event -> {
            category = "words_TOEFL";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().size());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        cet4.setOnAction(event -> {
            category = "words_CET4";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().size());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        cet6.setOnAction(event -> {
            category = "words_CET6";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().size());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }
}
