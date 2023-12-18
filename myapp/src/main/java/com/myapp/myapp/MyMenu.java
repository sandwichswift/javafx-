package com.myapp.myapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
public class MyMenu extends Application{
    private Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        mainWindow();
    }

    private void mainWindow() {
        try{
            FXMLLoader loader = new FXMLLoader(MyMenu.class.getResource("menu.fxml"));
            AnchorPane root = (AnchorPane) loader.load();
            MenuController controller = loader.getController();
            controller.setApplication(this);

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Menu");
            primaryStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

}
