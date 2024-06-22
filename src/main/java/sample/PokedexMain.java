package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PokedexMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new javafx.scene.layout.VBox(), 400, 400);

        primaryStage.setTitle("Pokedex");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
