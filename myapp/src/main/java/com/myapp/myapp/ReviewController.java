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
    @FXML
    private ChoiceBox<String> MODE;
    @FXML
    private Label modeLabel;
    @FXML
    private Label judgeLabel;
    @FXML
    private Label current;
    @FXML
    private Label currentDict;
    private int num;//每次复习的单词数
    private String mode="E2C";//当前模式,默认英译中
    private String modes[] = {"C2E","E2C"};//中译英，英译中
    private Label[] labels;
    private String[] labelText = new String[4];
    private Button[] buttonsMenu;
    private Button[] buttonsOption;
    private ObservableList<PieChart.Data> pieChartData;
    private String category;
    private String jdbcURL = "jdbc:sqlite:./src/main/resources/dbs/Dictionary.db" + "?journal_mode=WAL&synchronous=OFF";
    private WordList wordList;
    private Word currentWord;
    private  int index = 0;//当前单词在wordList中的索引
    private  String answer;//正确答案

    public void setApplication(MyMenu app){
        this.application = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        index = 0;
        //设置模式选择框
        MODE.getItems().addAll(modes);
        //设置模式选择框的响应事件
        MODE.setOnAction(event -> {
            mode = MODE.getValue();
            System.out.println("当前模式： " + mode);
            if(mode.equals("E2C")){
                modeLabel.setText("当前模式：英译中");
                setWord(index);
                setButton(currentWord);
            }
            else{
                modeLabel.setText("当前模式：中译英");
                setWord(index);
                setButton(currentWord);
            }
        });

        //设置菜单按钮的响应事件
        buttonsMenu = new Button[]{junior, senior, CET4, CET6, GRE, TOEFL};
        for(Button button : buttonsMenu){
            try {
                setButtonMenuAction(button);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //设置选项按钮的响应事件
        buttonsOption = new Button[]{buttonA, buttonB, buttonC, buttonD};
        for(Button button : buttonsOption){
            setButtonsOptionAction(button);
        }

        labels = new Label[]{labelA, labelB, labelC, labelD};
        //设置开始复习按钮的响应事件，涉及数据库读操作
        startReview.setOnAction(event -> {
            pieChart.setVisible(false);//隐藏饼图
            try {
                wordList = new WordList("review"+category);
                num = wordList.getWords().size();//获取单词数
                System.out.println("待复习单词数： " + num);
                if(num == 0){
                    MODE.setDisable(true);//设置模式选择框不可用
                    new Alert(Alert.AlertType.ERROR, "当前词典并未学习").showAndWait();
                    return;
                }
                else{
                    MODE.setDisable(false);//设置模式选择框可用
                }
                setWord(index);
                setButton(currentWord);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            showButtonsOption();
            showLabel();
        });
        //设置下一个单词按钮的响应事件
        nextButton.setOnAction(event -> {
            if(index == num - 1){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText("已经是最后一个单词了");
                alert.showAndWait();
            }
            else{
                index++;
                setWord(index);
                setButton(currentWord);
            }
            setLabelNull();
        });
        //设置上一个单词按钮的响应事件
        formerButton.setOnAction(event -> {
            if(index == 0){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText("已经是第一个单词了");
                alert.showAndWait();
            }
            else{
                index--;
                setWord(index);
                setButton(currentWord);
            }
            setLabelNull();
        });

    }

    //涉及数据库读操作
     private void setButton(Word answer) {//根据正确答案设置按钮,返回按钮上的文字
        try(Connection conn = DatabaseConnection.getConnection()){
            //随机从原数据库生成4个单词
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);//设置事务隔离级别
            conn.setAutoCommit(false);//设置手动提交
            //Statement().execute("PRAGMA synchronous = OFF");//设置同步模式为OFF
            int [] randomWordId = RandomWord.getRandomWordId(conn,category,4);
            conn.commit();
            for(int i = 0;i< buttonsOption.length;i++){
                if(mode.equals("E2C")){//英译中
                    buttonsOption[i].setText(RandomWord.getChineseMeaning(conn,category, randomWordId[i]));
                    labelText[i]=RandomWord.getWordById(conn,category, randomWordId[i]);
                }
                else{
                    buttonsOption[i].setText(RandomWord.getWordById(conn,category, randomWordId[i]));
                    labelText[i]=RandomWord.getChineseMeaning(conn,category, randomWordId[i]);
                }
            }

            int index = (int) (Math.random() * 4);//随机替换一个为正确答案

            if(mode.equals("E2C")){//英译中
                buttonsOption[index].setText(answer.getMeaning());
                setAnswer(answer.getMeaning());
                labelText[index]=answer.getWord();
            }
            else{
                buttonsOption[index].setText(answer.getWord());
                setAnswer(answer.getWord());
                labelText[index]=answer.getMeaning();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setAnswer(String answer) {
        this.answer = answer;
    }

    //只涉及数据库读操作
    private void setButtonMenuAction(Button button) throws SQLException{
        button.setOnAction(event -> {
            hideButtonsOption();
            hideLabel();
            category = button.getId();//提前在fxml中设置好按钮的id
            System.out.println(category);
            try(Connection conn = DatabaseConnection.getConnection()){
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);//设置事务隔离级别
                conn.setAutoCommit(false);//设置手动提交
                //conn.createStatement().execute("PRAGMA synchronous = OFF");//设置同步模式为OFF
                String countSQL = "SELECT COUNT(isExtracted) FROM " + category + " WHERE isExtracted = 1";
                PreparedStatement countStatement = conn.prepareStatement(countSQL);
                int count = countStatement.executeQuery().getInt(1);
                conn.commit();
                System.out.println("count: " + count);
                num = count;
                String countTotalSQL = "SELECT COUNT(id) FROM " + category;
                PreparedStatement countTotalStatement = conn.prepareStatement(countTotalSQL);
                int countTotal = countTotalStatement.executeQuery().getInt(1);
                conn.commit();
                System.out.println("countTotal: " + countTotal);
                pieChartData = FXCollections.observableArrayList(
                        new PieChart.Data("已背诵:"+count, count),
                        new PieChart.Data("待背诵:"+(countTotal-count), countTotal - count)
                );
                pieChart.setData(pieChartData);
                pieChart.setVisible(true);//显示饼图
                hideButtonsOption();
                startReview.setDisable(false);//设置开始复习按钮可用
                current.setText("");
                MODE.setDisable(true);
            }
            catch (SQLException e){
                e.printStackTrace();
            }
            currentDict.setVisible(true);//显示当前词典
            currentDict.setText("当前词典：\n" + category);
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public void setWord(int index){
        if(index < 0 || index >= num){
            System.out.println("index out of range");
            new Alert(Alert.AlertType.ERROR, "超出边界").showAndWait();
            return;
        }
        if(wordList.getWords().size()==0){
            System.out.println("wordList is empty");
            new Alert(Alert.AlertType.ERROR, "当前词典并未学习").showAndWait();
            return;
        }
        currentWord = wordList.getWords().get(index);
        if(mode.equals("E2C")){
        current.setText("当前进度： " + (index+1) + "/" + num +"\n当前单词：" + currentWord.getWord());
        setAnswer(currentWord.getMeaning());
        }
        else{
            current.setText("当前进度： " + (index+1) + "/" + num +"\n当前单词：" + currentWord.getMeaning());
            setAnswer(currentWord.getWord());
        }
    }
     private void setLabel(String[] labelText){
        for (int i = 0; i < labels.length; i++) {
            labels[i].setText(labelText[i]);
        }
    }
    private void setLabelNull(){
        for (int i = 0; i < labels.length; i++) {
            labels[i].setText("");
        }
        judgeLabel.setText("");
    }
    private void hideLabel(){
        for (int i = 0; i < labels.length; i++) {
            labels[i].setVisible(false);
        }
    }
    private void showLabel(){
        for (int i = 0; i < labels.length; i++) {
            labels[i].setVisible(true);
        }
    }
    //涉及数据库写操作
    private void setButtonsOptionAction(Button button){
        button.setOnAction(event -> {
            if(button.getText().equals(answer)){
                System.out.println("正确");
                judgeLabel.setText("正确");
                judgeLabel.setStyle("-fx-text-fill: green");
                /*try(Connection conn = DatabaseConnection.getConnection()){
                    RandomWord.updateReviewDatabase(conn,currentWord.getId(),"review"+category);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }*/
            }
            else{
                System.out.println("错误");
                judgeLabel.setText("错误");
                judgeLabel.setStyle("-fx-text-fill: red");
            }
            setLabel(labelText);
        });
    }
}
