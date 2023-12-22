package com.myapp.myapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
public class MyMenu extends Application{
    private Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        mainWindow();
    }
    //打开主窗口
    private void mainWindow() {
        try{
            FXMLLoader loader = new FXMLLoader(MyMenu.class.getResource("menu.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            MenuController controller = loader.getController();
            controller.setApplication(this);

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Menu");
            primaryStage.setResizable(false);//设置窗口不可改变大小
            primaryStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //打开添加新书窗口
    public void openReviewWindow(){
        try{
            FXMLLoader loader = new FXMLLoader(MyMenu.class.getResource("review.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            ReviewController controller = loader.getController();
            controller.setApplication(this);

            // Create new stage
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Review");
            newStage.setResizable(false);//设置窗口不可改变大小
            newStage.initModality(Modality.APPLICATION_MODAL);//设置新窗口为模态窗口,防止数据库被同时访问
            newStage.show();
    }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

}
