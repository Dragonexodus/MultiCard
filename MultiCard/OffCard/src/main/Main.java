package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        /*System.out.println("Starte SmartCard");

        try {
            SmartCard.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Beende SmartCard");
        try {
            SmartCard.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/stile.css").toString());
        Stage stage = new Stage();
        stage.setTitle("MultiCard");
        stage.setScene(scene);
        stage.show();
//        stage.setOnCloseRequest(e -> close());
    }
}
