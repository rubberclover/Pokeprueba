package presentation;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PokemonDisplay extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		byte[] backgroundBytes = Files.readAllBytes(Paths.get("images/PokedexDisplay.png"));
        Image backgroundImage = new Image(new ByteArrayInputStream(backgroundBytes));
		BackgroundSize backgroundSize = new BackgroundSize(540, 700, true, true, true, true);
		BackgroundImage backgroundImg = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
		Background background = new Background(backgroundImg);

		StackPane backgroundPane = new StackPane();
		backgroundPane.setBackground(background);
		
		
		
		Scene scene = PokedexMain.createScene(backgroundPane);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}
}
