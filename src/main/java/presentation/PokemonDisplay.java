package presentation;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.scalar.db.api.Result;

import command.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PokemonDisplay extends Application {
	
	private String pokemonName;
	
	public PokemonDisplay(String name) {
		this.pokemonName = name;
	}
	
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
		
        AnchorPane root = new AnchorPane();
        
        Pokemon pokemon = new Pokemon("scalardb.properties");
        List<Result> results = pokemon.getPokemonByNameOrId(pokemonName);
        Result result = null;
        if (result == null || results.size()!=1) {
        	// Error for no pokemon found or more than 1 pokemon found
        } else {
        	result = results.get(0);
        }
        
        Button back = PokedexMain.createButton("Back", 100, 20, 10);
        back.setOnAction(e -> {
        PokedexMain pokedexMain = new PokedexMain();
        PokedexMain.launchVerification(pokedexMain, primaryStage);
        });

        StackPane imagePane = new StackPane();
        ImageView image = new ImageView(new Image(new ByteArrayInputStream(result.getBlobAsBytes("image"))));
        String generation = String.valueOf(result.getInt("generation"));
        String imageName = String.format("%03d", generation ) + "_" + result.getText("name");
        Label imageLabel = PokedexMain.createLabel(imageName, 10);
        imagePane.getChildren().addAll(image, imageLabel);

        Label generationLabel = PokedexMain.createLabel("Generation: " + generation, 10);
        Label type1Label = PokedexMain.createLabel("Type 1: " + result.getText("type1"), 10);
        Label type2Label = PokedexMain.createLabel("Type 2: " + result.getText("type2"), 10);
        Label heightLabel = PokedexMain.createLabel("Height: " + result.getDouble("height"), 10);
        Label weightLabel = PokedexMain.createLabel("Weight: " + result.getDouble("weight"), 10);
        VBox informations = PokedexMain.createVBox(20, generationLabel, type1Label, type2Label, heightLabel, weightLabel);

        
        Weakness weakness = new Weakness("scalardb.properties");
        /*list of weakness
         * display resistance defense (x0, 0.25, x0.5)
         * display weakness defense (x2, x4)   
         * display strength attack (x2)
         * display weak attack (x0, x0.5)
         */
        VBox weaknesses = null;

        root.getChildren().addAll(back, imagePane, informations, weaknesses);
        StackPane.setMargin(root, new Insets(80, 0, 0, 0));
		Scene scene = PokedexMain.createScene(backgroundPane, root);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}
}
