package presentation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scalar.db.api.Result;

import command.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class PokedexMain extends Application {

     private String scalarDB = "scalardb.properties";
     private Map<String, ObservableList<String>> filterSelections = new HashMap<>();
     
     public PokedexMain() throws Exception {
		try {
			initializePokedex();
		} catch (Exception e) {
			throw e;
		}
	}
     
     public PokedexMain(Map<String, ObservableList<String>> filterSelections) {
    	 this.filterSelections = filterSelections;
     }
     
	 @Override
	    public void start(Stage primaryStage) throws IOException {
	        primaryStage.setTitle("Pokedex Application");
			primaryStage.setResizable(false);
	        
            byte[] iconBytes = Files.readAllBytes(Paths.get("images/pokemon.png"));
            Image iconImage = new Image(new ByteArrayInputStream(iconBytes));
	        primaryStage.getIcons().add(iconImage);
	        
	        byte[] backgroundBytes = Files.readAllBytes(Paths.get("images/pokedex.png"));
	        Image backgroundImage = new Image(new ByteArrayInputStream(backgroundBytes));
			BackgroundSize backgroundSize = new BackgroundSize(540, 700, true, true, true, true);
			BackgroundImage backgroundImg = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
					BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
			Background background = new Background(backgroundImg);

			StackPane backgroundPane = new StackPane();
			backgroundPane.setBackground(background);

			Label volumeLabel = createLabel("Volume", 10);
			MediaPlayer mediaPlayerMusic = BackgroundMusic.getInstance().getMusicPlayer();
			Slider volumeSlider = BackgroundMusic.getInstance().getVolumeSlider();
			mediaPlayerMusic.play();

			HBox sliderContainer = new HBox(10);
			sliderContainer.setAlignment(Pos.CENTER);
			sliderContainer.getChildren().addAll(volumeLabel, volumeSlider);

			TextField searchField = new TextField();
	        searchField.setPromptText("Enter pokemon name or id");

	        searchField.setOnKeyPressed(event -> {
	            if (event.getCode() == KeyCode.ENTER) {
	            	SearchForPokemon(searchField.getText().trim());
	            }
	        });
	        
	        Button searchButton = new Button("Search");
	        searchButton.setOnAction(e -> SearchForPokemon(searchField.getText().trim()));
	        
	        Button advancedFilters = createButton("Advanced Filters", 100, 20, 10);
	        advancedFilters.setOnAction(e -> {
				PokedexFilters pokedexFilters = new PokedexFilters(backgroundPane);
				launchVerification(pokedexFilters, primaryStage);
			});
	        
	        HBox searchBar = createHBox(10, searchField, searchButton);
	        VBox box = createVBox(10, searchBar, advancedFilters, sliderContainer);	        

			Scene scene = createScene(backgroundPane, box);
	        primaryStage.setScene(scene);
			primaryStage.sizeToScene();
	        primaryStage.show();
	    }

		private void SearchForPokemon(String search) {
			System.out.println(search);
			
		}

		private List<HBox> getAllTypes() throws Exception {
			Type type = new Type(scalarDB);
			
			List<Result> results = type.getAllTypes();
			System.out.print("Number of types " + results.size());

			List<HBox> typeList = new ArrayList<HBox>();
			HBox hbox;
			Label typeName;
			ImageView image;
			
			for (Result result: results) {
				typeName = createLabel(result.getText("name"), 10);
				image = new ImageView(new Image(new ByteArrayInputStream(result.getBlobAsBytes("image"))));
	            
	            hbox = createHBox(10, typeName, image);
	            typeList.add(hbox);
			}
			return typeList;
	    }

		private void initializePokedex() throws Exception {
			Pokedex pokedex = new Pokedex();
			if (pokedex.loadInitialData()) {
				System.out.println("Pokedex initialized successfully");
			}
			pokedex.close();
	    }

		protected static Button createButton(String text, int i, int j, int pixel) {
			Button button = new Button(text);
			button.setPrefSize(i, j);
			button.setStyle("-fx-font-size: " + pixel + "px;");
			return button;
		}

		protected static Label createLabel(String text, int pixel) {
			Label label = new Label(text);
			label.setStyle("-fx-font-size: " + pixel + "px; -fx-text-fill: black;");
			return label;
		}

		protected static VBox createVBox(int spacing, Node... nodes) {
			VBox box = new VBox(spacing);
			box.getChildren().addAll(nodes);
			box.setAlignment(Pos.CENTER);
			return box;
		}

		protected static HBox createHBox(int spacing, Node... nodes) {
			HBox box = new HBox(spacing);
			box.getChildren().addAll(nodes);
			box.setAlignment(Pos.CENTER);
			return box;
		}

		protected static Scene createScene(Node... nodes) {
			StackPane sceneContent = new StackPane();
			for (int i = 0; i < nodes.length; i++) {
				sceneContent.getChildren().add(nodes[i]);
			}

			Scene scene = new Scene(sceneContent, 540, 700);
			return scene;
		}

		protected static void launchVerification(Application name, Stage primaryStage) {
			try {
				name.start(primaryStage);
			} catch (Exception e) {
				primaryStage.close();
			}
		}
	    public static void main(String[] args) {
	        launch(args);
	    }
}
