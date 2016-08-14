import application.card.JavaCard;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.controller.MainController;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("MultiCard");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> close());
    }

    private void close() {
        MainController.cancelTimer();
        JavaCard.getInstance().shutdown();
    }
}
