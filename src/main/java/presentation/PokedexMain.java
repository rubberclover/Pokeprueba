package presentation;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scalar.db.api.Result;

import command.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class PokedexMain extends Application {

     private Map<String, ObservableList<String>> filterSelections = null;
     private String search = "";
     
     public PokedexMain() {
		try {
			initializePokedex();
		} catch (Exception e) {
			System.out.println("Pokedex didn't initialized well");
		}
	}

     public PokedexMain(Map<String, ObservableList<String>> filterSelections) {
    	 this.filterSelections = filterSelections;
     }

     public PokedexMain(String search) {
    	 this.search = search;
     }
     
	 @Override
	    public void start(Stage primaryStage) throws Exception {
	        primaryStage.setTitle("Pokedex Application");
			primaryStage.setResizable(false);
	        
            byte[] iconBytes = Files.readAllBytes(Paths.get("images/pokemon.png"));
            Image iconImage = new Image(new ByteArrayInputStream(iconBytes));
	        primaryStage.getIcons().add(iconImage);
	        
	        byte[] backgroundBytes = Files.readAllBytes(Paths.get("images/Pokedex.png"));
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
	        searchField.setMinWidth(200);

	        searchField.setOnKeyPressed(event -> {
	            if (event.getCode() == KeyCode.ENTER) {
	            	SearchForPokemon(searchField.getText(), primaryStage);
	            }
	        });
	        
	        Button searchButton = new Button("Search");
	        searchButton.setOnAction(e -> SearchForPokemon(searchField.getText(), primaryStage));
	        
	        Button advancedFilters = createButton("Advanced Filters", 100, 20, 10);
	        advancedFilters.setOnAction(e -> {
				PokedexFilters pokedexFilters = new PokedexFilters();
				launchVerification(pokedexFilters, primaryStage);
			});
	        
	        HBox searchBar = createHBox(30, searchField, searchButton);
	        HBox advancedFilterBar = createHBox(50, sliderContainer, advancedFilters);
	        VBox bar = createVBox(10, searchBar, advancedFilterBar);

	        ListView<HBox> listView = new ListView<>();
	        ObservableList<HBox> items = FXCollections.observableArrayList();
	        try {
	        	items.addAll(getPokemonsFiltered());
			} catch (Exception e1) {
				throw e1;
			}
	        listView.setItems(items);
	        listView.setMaxSize(375, 300); 
	        listView.setOnMouseClicked(event -> {
	            if (event.getClickCount() == 2) { // Double clic
	            	PokemonDisplay pokemonDisplay = new PokemonDisplay("");
	            	launchVerification(pokemonDisplay, primaryStage);
	            } 
	        });

	        VBox box = createVBox(30, bar, listView);
	        StackPane root = new StackPane();
	        root.getChildren().add(box);
	        StackPane.setMargin(box, new Insets(80, 0, 0, 0));

	        Scene scene = createScene(backgroundPane, root);
	        primaryStage.setScene(scene);
			primaryStage.sizeToScene();
	        primaryStage.show();
	    }

		private void SearchForPokemon(String search, Stage primaryStage) {
			System.out.println(search);
			PokedexMain pokemonDisplay = new PokedexMain(search.trim());
			PokedexMain.launchVerification(pokemonDisplay, primaryStage);
		}
		
		private List<HBox> getPokemonsFiltered() throws Exception {
			Pokemon pokemon = new Pokemon("scalardb.properties");
			List<Result> results;
			
			List<String> typeNames = Arrays.asList(
					  "None", "Normal", "Fire", "Water","Electric", "Grass", "Ice", 
			            "Fighting", "Poison", "Ground", "Flying", "Psychic", 
			            "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy");
			List<HBox> pokemonList = new ArrayList<>();
			HBox hbox;
			VBox vbox;
			Label name;
			Label type1;
			Label type2;
			ImageView image;

			if (!isFilterSelectionsEmpty()) {
				results = pokemon.getPokemonsFiltered(filterSelections);
			} else if (search != "") {
				results = pokemon.getPokemonByNameOrId(search);
			} else {
				results = pokemon.getAllPokemons();
			}
			name = createLabel("There is " + (results == null ? 0 : results.size()) + " result" + (results == null ? "" : (results.size()>1?"s":"") ) + " found for this research.", 10);
			hbox = createHBox(0, name);
			pokemonList.add(hbox);
			try {
				for (Result result: results) {
					name = createLabel(result.getText("name"), 10);
					type1 = createLabel(typeNames.get(result.getInt("type1")), 10);
					type2 = createLabel(typeNames.get(result.getInt("type2")), 10);
					image = new ImageView(new Image(new ByteArrayInputStream(result.getBlobAsBytes("image"))));
					image.setFitWidth(50);
			        image.setFitHeight(50);
			        hbox = createHBox(20, type1, type2);
					vbox = createVBox(0, name, hbox);
					hbox = createHBox(50, image, vbox);
					pokemonList.add(hbox);
				}
				return pokemonList;
			} catch (NullPointerException e) {
				return pokemonList;
			}
		}

		/*private List<HBox> getAllTypes() throws Exception {
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
	    }*/

		public boolean isFilterSelectionsEmpty() {
	        if (filterSelections == null || filterSelections.isEmpty()) {
	            return true;
	        }

	        for (Map.Entry<String, ObservableList<String>> entry : filterSelections.entrySet()) {
	            if (!entry.getValue().isEmpty()) {
	                return false;
	            }
	        }
	        return true;
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
				e.printStackTrace();
				primaryStage.close();
			}
		}
	    public static void main(String[] args) {
	        launch(args);
	    }
}
