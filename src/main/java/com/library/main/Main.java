package com.library.main;

import com.library.database.DatabaseHandler;
import com.library.util.LibraryUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {   

    @Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));       
       Scene scene = new Scene(root);       
       stage.setScene(scene);
       stage.setTitle("Library Login");       
       LibraryUtil.setIcon(stage);
       stage.show();
       
       new Thread(() -> {		
			DatabaseHandler.getInstance();
       }).start();
    }

    public static void main(String[] args) {
        launch();
    }

}

