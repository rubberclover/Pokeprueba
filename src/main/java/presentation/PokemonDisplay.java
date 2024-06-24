package presentation;

import java.util.Map;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PokemonDisplay extends Application {

	private StackPane backgroundPane;

	public PokemonDisplay(StackPane backgroundPane) {
		this.backgroundPane = backgroundPane;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = PokedexMain.createScene(backgroundPane);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}
}
