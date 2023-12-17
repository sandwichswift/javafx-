package com.myapp.myapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
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
    private Integer[] numberOfWordsInt = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    private WordList wordList;
    private Word currentWord;
    private int num;


    private Menu application;


    public void setApplication(Menu mainApp) {
        this.application = mainApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numberOfWords.getItems().addAll(numberOfWordsInt);
        numberOfWords.setOnAction(event ->{
            num = numberOfWords.getValue();
            try {
                wordList = new WordList(num);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
