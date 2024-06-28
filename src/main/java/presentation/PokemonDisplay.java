package presentation;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.scalar.db.api.Result;

import command.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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
        System.out.println(pokemonName);
        List<Result> results = pokemon.getPokemonByNameOrId(pokemonName);
        Result result = null;
        if (results == null || results.size()!=1) {
	        PokedexMain pokedexMain = new PokedexMain("");
	        PokedexMain.launchVerification(pokedexMain, primaryStage);
        } else {
        	result = results.get(0);
        }
        
        Button back = PokedexMain.createButton("Back", 100, 30, 10);
        back.setOnAction(e -> {
	        PokedexMain pokedexMain = new PokedexMain("");
	        PokedexMain.launchVerification(pokedexMain, primaryStage);
        });

        ImageView image = new ImageView(new Image(new ByteArrayInputStream(result.getBlobAsBytes("image"))));
		image.setFitWidth(200);
        image.setFitHeight(200);
        String imageName = String.format("%03d", result.getInt("pokemon_id")) + "_" + result.getText("name");
        Label imageLabel = PokedexMain.createLabel(imageName, 10);
        VBox imagePane = PokedexMain.createVBox(-5, image, imageLabel);

		List<String> typeNames = Arrays.asList(
				  "None", "Normal", "Fire", "Water","Electric", "Grass", "Ice", 
		            "Fighting", "Poison", "Ground", "Flying", "Psychic", 
		            "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy");
		
        Label generationLabel = PokedexMain.createLabel("Generation: " + result.getInt("generation"), 10);
        Label type1Label = PokedexMain.createLabel("Type 1: " + typeNames.get(result.getInt("type1")), 10);
        Label type2Label = PokedexMain.createLabel("Type 2: " + typeNames.get(result.getInt("type2")), 10);
        Label heightLabel = PokedexMain.createLabel("Height: " + result.getDouble("height") + "m", 10);
        Label weightLabel = PokedexMain.createLabel("Weight: " + result.getDouble("weight") + "kg", 10);
        VBox informations = PokedexMain.createVBox(20, generationLabel, type1Label, type2Label, heightLabel, weightLabel);

        Weakness weakness = new Weakness("scalardb.properties");
        List<Result> weaknessList = weakness.getWeaknessByTypes(result.getInt("type1"), result.getInt("type2"));
        List<Result> resistanceList = weakness.getResistanceByTypes(result.getInt("type1"), result.getInt("type2"));
        List<Result> effectiveAttackList = weakness.getEffectiveAttackByType(result.getInt("type1"));
        List<Result> effectiveAttackList2 = weakness.getEffectiveAttackByType(result.getInt("type2"));
        List<Result> weakAttackList = weakness.getNonEffectiveAttackByType(result.getInt("type1"));
        List<Result> weakAttackList2 = weakness.getNonEffectiveAttackByType(result.getInt("type2"));
        
        VBox weaknesses = PokedexMain.createVBox(10);
        
        // Weaknesses
        Label title = PokedexMain.createLabel("Weakness: ", 10);
        Label text = PokedexMain.createLabel("", 10);
        FlowPane box = new FlowPane(title);
        box.setPrefWrapLength(300);
        box.setAlignment(Pos.CENTER_LEFT);
        if (weaknessList.size() == 0) {
        	text = PokedexMain.createLabel("None.", 10);
        	box.getChildren().add(text);
        }
        for (int i = 0; i < weaknessList.size(); i++) {
        	text = PokedexMain.createLabel("", 10);
        	text.setText(typeNames.get(weaknessList.get(i).getInt("attacker_type")) + ((i < weaknessList.size()-1)?" | ":""));
        	box.getChildren().add(text);
        }
        weaknesses.getChildren().add(box);

        // Resistances
        title = PokedexMain.createLabel("Resistance: ", 10);
        box = new FlowPane(title);
        box.setAlignment(Pos.CENTER_LEFT);
        if (resistanceList.size() == 0) {
        	text = PokedexMain.createLabel("None.", 10);
        	box.getChildren().add(text);
        }
        for (int i = 0; i < resistanceList.size(); i++) {
        	text = PokedexMain.createLabel("", 10);
        	if (resistanceList.get(i).getDouble("mult") == 0) {
            	text.setText(typeNames.get(resistanceList.get(i).getInt("attacker_type")) + ": Ineffective " + ((i < resistanceList.size()-1)?" | ":""));
        	} else {
        		text.setText(typeNames.get(resistanceList.get(i).getInt("attacker_type")) + ((i < resistanceList.size()-1)?" | ":""));
        	}
        	box.getChildren().add(text);
        }
        weaknesses.getChildren().add(box);

        // Effective attacks for type 1
        title = PokedexMain.createLabel("Effective Attacks: ", 10);
        box = new FlowPane(title);
        box.setAlignment(Pos.CENTER_LEFT);
        title = PokedexMain.createLabel(typeNames.get(result.getInt("type1")) + ": ", 10);
        box.getChildren().add(title);
        if (effectiveAttackList.size() == 0) {
        	text = PokedexMain.createLabel("None.", 10);
        	box.getChildren().add(text);
        }
        for (int i = 0; i < effectiveAttackList.size(); i++) {
        	text = PokedexMain.createLabel("", 10);
        	text.setText(typeNames.get(effectiveAttackList.get(i).getInt("type_id")) + ((i < effectiveAttackList.size()-1)?" | ":""));
        	box.getChildren().add(text);
        }
        weaknesses.getChildren().add(box);

        // Effective attacks for type 2
        if (result.getInt("type2") != 0) {
	        box = new FlowPane();
	        box.setAlignment(Pos.CENTER_LEFT);
	        title = PokedexMain.createLabel(typeNames.get(result.getInt("type2")) + ": ", 10);
	        box.getChildren().add(title);
	        if (effectiveAttackList2.size() == 0) {
	        	text = PokedexMain.createLabel("None.", 10);
	        	box.getChildren().add(text);
	        }
	        for (int i = 0; i < effectiveAttackList2.size(); i++) {
	        	text = PokedexMain.createLabel("", 10);
	        	text.setText(typeNames.get(effectiveAttackList2.get(i).getInt("type_id")) + ((i < effectiveAttackList2.size()-1)?" | ":""));
	        	box.getChildren().add(text);
	        }
	        weaknesses.getChildren().add(box);
        }

        // Weak attacks for type 1
        title = PokedexMain.createLabel("Weak Attacks: ", 10);
        box = new FlowPane(title);
        box.setAlignment(Pos.CENTER_LEFT);
        title = PokedexMain.createLabel(typeNames.get(result.getInt("type1")) + ": ", 10);
        box.getChildren().add(title);
        if (weakAttackList.size() == 0) {
        	text = PokedexMain.createLabel("None.", 10);
        	box.getChildren().add(text);
        }
        for (int i = 0; i < weakAttackList.size(); i++) {
        	text = PokedexMain.createLabel("", 10);
        	if (weakAttackList.get(i).getDouble("mult") == 0) {
        		text.setText(typeNames.get(weakAttackList.get(i).getInt("type_id")) + ": Ineffective" + ((i < weakAttackList.size()-1)?" | ":""));
        	} else {
        		text.setText(typeNames.get(weakAttackList.get(i).getInt("type_id")) + ((i < weakAttackList.size()-1)?" | ":""));
        	}
        	box.getChildren().add(text);
        }
        weaknesses.getChildren().add(box);

        // Weak attacks for type 2
        if (result.getInt("type2") != 0) {
	        box = new FlowPane();
	        box.setAlignment(Pos.CENTER_LEFT);
	        title = PokedexMain.createLabel(typeNames.get(result.getInt("type2")) + ": ", 10);
	        box.getChildren().add(title);
	        if (weakAttackList2.size() == 0) {
	        	text = PokedexMain.createLabel("None.", 10);
	        	box.getChildren().add(text);
	        }
	        for (int i = 0; i < weakAttackList2.size(); i++) {
	            text = PokedexMain.createLabel("", 10);
	            text.setText(typeNames.get(weakAttackList2.get(i).getInt("type_id")) + ((i < weakAttackList2.size()-1)?" | ":""));
	            box.getChildren().add(text);
	        }
	        weaknesses.getChildren().add(box);
        }

        root.getChildren().addAll(back, imagePane, informations, weaknesses);
        AnchorPane.setTopAnchor(back, 120.0); 
        AnchorPane.setRightAnchor(back, 100.0); 
        AnchorPane.setTopAnchor(imagePane, 190.0);
        AnchorPane.setLeftAnchor(imagePane, 90.0);
        AnchorPane.setTopAnchor(informations, 210.0);
        AnchorPane.setRightAnchor(informations, 110.0);
        AnchorPane.setBottomAnchor(weaknesses, ((result.getInt("type2") != 0)?115.0:170.0));
        AnchorPane.setLeftAnchor(weaknesses, 90.0);

		Scene scene = PokedexMain.createScene(backgroundPane, root);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}
}
