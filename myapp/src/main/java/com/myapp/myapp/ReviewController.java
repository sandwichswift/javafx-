package com.myapp.myapp;

import com.myapp.myapp.web.RandomWord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Menu;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReviewController implements Initializable {
    private MyMenu application;
    @FXML
    private Button junior;
    @FXML
    private Button senior;
    @FXML
    private Button CET4;
    @FXML
    private Button CET6;
    @FXML
    private Button GRE;
    @FXML
    private Button TOEFL;
    @FXML
    private PieChart pieChart;
    @FXML
    private Button startReview;
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
    private Button nextButton;
    @FXML
    private Button formerButton;
    private Label[] labels;
    private Button[] buttonsMenu;
    private Button[] buttonsOption;
    private ObservableList<PieChart.Data> pieChartData;
    private String category;
    private String jdbcURL = "jdbc:sqlite:dbs/Dictionary.db";
    private WordList wordList;
    private Word currentWord;
    private  int index = 0;//当前单词在wordList中的索引

    public void setApplication(MyMenu app){
        this.application = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonsMenu = new Button[]{junior, senior, CET4, CET6, GRE, TOEFL};
        for(Button button : buttonsMenu){
            try {
                setButtonMenuAction(button);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        buttonsOption = new Button[]{buttonA, buttonB, buttonC, buttonD};
        startReview.setOnAction(event -> {
            pieChart.setVisible(false);//隐藏饼图
            try(Connection conn = DriverManager.getConnection(jdbcURL)){
            wordList = new WordList(category);//生成单词列表
            }
            catch (SQLException e){
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        nextButton.setOnAction(event -> {});

    }
    private void setButtonMenuAction(Button button) throws SQLException{
        button.setOnAction(event -> {
            hideButtonsOption();
            category = button.getId();//提前在fxml中设置好按钮的id
            System.out.println(category);
            try(Connection conn = DriverManager.getConnection(jdbcURL)){

                String countSQL = "SELECT COUNT(isExtracted) FROM " + category + " WHERE isExtracted = 1";
                PreparedStatement countStatement = conn.prepareStatement(countSQL);
                int count = countStatement.executeQuery().getInt(1);
                System.out.println("count: " + count);
                String countTotalSQL = "SELECT COUNT(id) FROM " + category;
                PreparedStatement countTotalStatement = conn.prepareStatement(countTotalSQL);
                int countTotal = countTotalStatement.executeQuery().getInt(1);
                System.out.println("countTotal: " + countTotal);
                pieChartData = FXCollections.observableArrayList(
                        new PieChart.Data("已背诵:"+count, count),
                        new PieChart.Data("待背诵:"+(countTotal-count), countTotal - count)
                );
                pieChart.setData(pieChartData);
                pieChart.setVisible(true);//显示饼图
            }
            catch (SQLException e){
                e.printStackTrace();
            }

        });
    }
    public void hideButtonsOption(){
        for(Button button : buttonsOption){
            button.setVisible(false);
        }
        nextButton.setVisible(false);
        formerButton.setVisible(false);
    }

    public void showButtonsOption(){
        for(Button button : buttonsOption){
            button.setVisible(true);
        }
        nextButton.setVisible(true);
        formerButton.setVisible(true);
    }
}
