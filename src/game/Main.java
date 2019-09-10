package game;

import javafx.application.Application;
import javafx.stage.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("fxml/Menu.fxml"));
            AnchorPane root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Minesweeper");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
