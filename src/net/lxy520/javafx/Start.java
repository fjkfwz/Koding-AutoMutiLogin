package net.lxy520.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Start extends Application {
    private  Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.readSetting();
        primaryStage.setTitle("Koding自动重启工具");
        primaryStage.setScene(new Scene(root, 630, 310));
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
    }



    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
