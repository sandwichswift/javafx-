package com.myapp.myapp;

import com.myapp.myapp.web.RandomWord;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Menu;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    private MyMenu application;//主程序
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
    private Label[] labels;
    private Button[] buttons;
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
    private ChoiceBox<String> MODE;
    @FXML
    private MenuItem junior;
    @FXML
    private MenuItem senior;
    @FXML
    private MenuItem GRE;
    @FXML
    private MenuItem TOEFL;
    @FXML
    private MenuItem CET4;
    @FXML
    private MenuItem CET6;
    private MenuItem[] menuItems ;
    private String[] categories = {"words_junior", "words_senior", "words_GRE", "words_TOEFL", "words_CET4", "words_CET6"};
    @FXML
    private Label searchResultLabel;
    @FXML
    private Label current;
    @FXML
    private Label modeLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button reviewButton;
    @FXML
    private Button initializeCurrentDict;
    @FXML
    private Label currentDict;
    private Integer[] numberOfWordsInt = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    private String jdbcURL ="jdbc:sqlite:./src/main/resources/dbs/Dictionary.db" + "?journal_mode=WAL&synchronous=OFF";
    private WordList wordList;
    private Word currentWord;//当前单词
    private int num = 20;//默认单词数
    private String category;
    private int index = 0;//当前单词索引
    private String answer;//当前单词的正确答案
    private int score = 0;//当前分数
    private String mode="E2C";//当前模式,默认英译中
    private String modes[] = {"C2E","E2C"};//中译英，英译中
    private String[] labelText = new String[4];


    public MenuController() throws SQLException {
    }


    public void setApplication(MyMenu mainApp) {
        this.application = mainApp;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try( Connection conn = DatabaseConnection.getConnection()){//尝试连接数据库
            System.out.println("连接数据库成功");
            new Alert(Alert.AlertType.INFORMATION,"数据库连接成功").showAndWait();
        }
        catch (SQLException e) {
            System.out.println("连接数据库失败");
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "连接数据库失败").showAndWait();
        }
        //检查网络连接
        if(!RandomWord.checkNetwork()){
            new Alert(Alert.AlertType.ERROR, "网络连接失败,仅可使用离线功能").showAndWait();
        }

        menuItems = new MenuItem[]{junior,senior,GRE,TOEFL,CET4,CET6};
        buttons = new Button[]{buttonA,buttonB,buttonC,buttonD};
        labels = new Label[]{labelA,labelB,labelC,labelD};

        MODE.getItems().addAll(modes);
        //设置模式选择框事件
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
            setLabelNull();
        });



        numberOfWords.getItems().addAll(numberOfWordsInt);
        //设置单词数选择框事件
        numberOfWords.setOnAction(event ->{
            num = numberOfWords.getValue();
            try {
                index = 0;
                wordList = new WordList(category,num);
                setWord(index);
                setButton(currentWord);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        index = 0;//初始化单词索引
        //设置菜单按钮事件
        /*junior.setOnAction(event -> {
            index  = 0;//初始化单词索引
            category = "words_junior";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().get(0).getWord());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            numberOfWords.setDisable(false);//设置单词数选择框可用
            setWord(index);
        });
        senior.setOnAction(event -> {
            index  = 0;//初始化单词索引
            category = "words_senior";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().get(0).getWord());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            numberOfWords.setDisable(false);//设置单词数选择框可用
            setWord(index);
        });
        GRE.setOnAction(event -> {
            index  = 0;//初始化单词索引
            category = "words_GRE";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().get(0).getWord());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            numberOfWords.setDisable(false);//设置单词数选择框可用
            setWord(index);
        });
        TOEFL.setOnAction(event -> {
            index  = 0;//初始化单词索引
            category = "words_TOEFL";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().get(0).getWord());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            numberOfWords.setDisable(false);//设置单词数选择框可用
            setWord(index);
        });
        CET4.setOnAction(event -> {
            index  = 0;//初始化单词索引
            category = "words_CET4";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().get(0).getWord());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            numberOfWords.setDisable(false);//设置单词数选择框可用
            setWord(index);
        });
        CET6.setOnAction(event -> {
            index  = 0;//初始化单词索引
            category = "words_CET6";
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().get(0).getWord());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            numberOfWords.setDisable(false);//设置单词数选择框可用
            setWord(index);
        });*/

        //设置菜单按钮事件
        for(int i = 0;i<menuItems.length;i++){
            setMenuItemAction(menuItems[i],categories[i]);
        }

        //设置按钮事件
        for(int i = 0;i<buttons.length;i++){
            setButtonAction(buttons[i]);
            hideButton(buttons[i]);
        }

        //设置搜索按钮事件
        searchButton.setOnAction(event -> {
            String word = searchTextField.getText();
            //判断word为中文还是英文，调用不同的方法
            if(word.matches("[\\u4e00-\\u9fa5]+")){//中文
                try {
                    String englishMeaning = RandomWord.searchEnglish(word);
                    searchResultLabel.setText(englishMeaning);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else{//英文
                try {
                    String chineseMeaning = RandomWord.searchWordMeaning(word);
                    searchResultLabel.setText(chineseMeaning);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        searchTextField.setOnKeyPressed(event -> {
            if(event.getCode().toString().equals("ENTER")){
                String word = searchTextField.getText();
                //判断word为中文还是英文，调用不同的方法
                if(word.matches("[\\u4e00-\\u9fa5]+")){//中文
                    try {
                        String englishMeaning = RandomWord.searchEnglish(word);
                        searchResultLabel.setText(englishMeaning);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else{//英文
                    try {
                        String chineseMeaning = RandomWord.searchWordMeaning(word);
                        searchResultLabel.setText(chineseMeaning);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        //设置前一个按钮事件
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

        //设置下一个按钮事件
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

        //设置复习按钮事件
        reviewButton.setOnAction(event -> {
            application.openReviewWindow();
        });

        //设置初始化当前词典按钮事件，涉及数据库写操作
        initializeCurrentDict.setOnAction(event -> {
            //询问是否初始化
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("提示");
            alert1.setHeaderText("确定要初始化当前词典吗？");
            alert1.showAndWait();
            //如果确定要初始化
            if(alert1.getResult().getText().equals("确定")){

                //如果确定要初始化

                try(Connection conn = DatabaseConnection.getConnection()){
                    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);//设置事务隔离级别
                    conn.setAutoCommit(false);//设置手动提交
                    //conn.createStatement().execute("PRAGMA synchronous = OFF");//设置同步模式为OFF
                    RandomWord.initializeCurrentDict(conn,category);
                    conn.commit();//提交事务
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("提示");
                    alert.setHeaderText("初始化成功");
                    alert.showAndWait();}
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //设置当前单词
    private void setWord(int index) {
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
    //设置正确答案
    private void setAnswer(String answer) {
        this.answer = answer;
    }

    //设置菜单按钮事件，根据菜单上的文字设置单词表，涉及数据库
    private void setMenuItemAction(MenuItem menuItem, String category) {
        menuItem.setOnAction(event -> {
            index  = 0;//初始化单词索引
            score = 0;//初始化分数
            this.category = category;
            try {
                wordList = new WordList(category,num);
                System.out.println("category:" + category + "\n" + wordList.getWords().get(0).getWord());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            currentDict.setVisible(true);//设置当前词典标签可见
            currentDict.setText("当前词典：" + category);//设置当前词典标签文字
            numberOfWords.setDisable(false);//设置单词数选择框可用
            setWord(index);
            setButton(currentWord);
            setLabelNull();
            initializeCurrentDict.setDisable(false);//设置初始化当前词典按钮可用
            for(int i = 0;i<buttons.length;i++){//设置按钮可见
                showButton(buttons[i]);
            }
        });
    }

    //设置按钮事件，根据按钮上的文字判断是否正确，涉及数据库写入
    private void setButtonAction(Button button) {
        button.setOnAction(event -> {
            if(button.getText().equals(answer)){
                score++;
                judgeLabel.setText("正确");
                judgeLabel.setStyle("-fx-text-fill: green");
            }
            else{
                judgeLabel.setText("错误");
                judgeLabel.setStyle("-fx-text-fill: red");
            }
            scoreLabel.setText("当前分数：" + score);
            setLabel(labelText);
            try(Connection conn = DatabaseConnection.getConnection()){//更新数据库
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);//设置事务隔离级别
                conn.setAutoCommit(false);//设置手动提交
                //conn.createStatement().execute("PRAGMA synchronous = OFF");//设置同步模式为OFF
                RandomWord.updateWordAsExtracted(conn, currentWord.getId(), category);
                conn.commit();//提交事务
            }
            catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try(Connection conn = DatabaseConnection.getConnection()){
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);//设置事务隔离级别
                conn.setAutoCommit(false);//设置手动提交
                //conn.createStatement().execute("PRAGMA synchronous = OFF");//设置同步模式为OFF
                RandomWord.updateReviewDatabase(conn, currentWord.getId(), category);
                conn.commit();//提交事务
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    //设置按钮，根据正确答案设置按钮，涉及数据库
    private void setButton(Word answer) {//根据正确答案设置按钮,返回按钮上的文字
        try(Connection conn = DatabaseConnection.getConnection()){
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);//设置事务隔离级别
            conn.setAutoCommit(false);//设置手动提交
            //conn.createStatement().execute("PRAGMA synchronous = OFF");//设置同步模式为OFF
            int [] randomWordId = RandomWord.getRandomWordId(conn,category,4);
            conn.commit();//提交事务
            for(int i = 0;i< buttons.length;i++){
                if(mode.equals("E2C")){//英译中
                    buttons[i].setText(RandomWord.getChineseMeaning(conn,category, randomWordId[i]));
                    labelText[i]=RandomWord.getWordById(conn,category, randomWordId[i]);
                }
                else{//中译英
                    buttons[i].setText(RandomWord.getWordById(conn,category, randomWordId[i]));
                    labelText[i]=RandomWord.getChineseMeaning(conn,category, randomWordId[i]);
                }
            }

            int index = (int) (Math.random() * 4);//随机替换一个为正确答案

            if(mode.equals("E2C")){//英译中
                buttons[index].setText(answer.getMeaning());
                setAnswer(answer.getMeaning());
                labelText[index]=answer.getWord();
            }
            else{
                buttons[index].setText(answer.getWord());
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

    //设置标签
    private void setLabel(String[] labelText){
        for (int i = 0; i < labels.length; i++) {
            labels[i].setText(labelText[i]);
        }
    }

    //设置标签为空
    private void setLabelNull(){
        for (int i = 0; i < labels.length; i++) {
            labels[i].setText("");
        }
    }

    //设置按钮可见性
    private void hideButton(Button button){
        button.setVisible(false);
    }
    private void showButton(Button button){
        button.setVisible(true);
    }
}
    
