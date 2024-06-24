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
import com.scalar.db.storage.dynamo.GetItemScanner;

import command.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PokedexMain extends Application {

     private String scalarDB = "scalardb.properties";
     private Map<String, ObservableList<String>> filterSelections = new HashMap<>();
     
     public PokedexMain() {}
     
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
			
	        // Create a button to initialize Pokedex
	        Button initialize = createButton("Initialize Pokedex", 100, 20, 10);
	        initialize.setOnAction(event -> {
				try {
					initializePokedex();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			});

	        Button getTypes = createButton("Get Types", 100, 20, 10);
	        getTypes.setOnAction(event -> {
				try {
					getAllTypes();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
	        
	        Button advancedFilters = createButton("Advanced Filters", 100, 20, 10);
	        advancedFilters.setOnAction(e -> {
				PokedexFilters pokedexFilters = new PokedexFilters(backgroundPane);
				launchVerification(pokedexFilters, primaryStage);
			});

	        VBox box = createVBox(10, initialize, getTypes, advancedFilters);	        

			Scene scene = createScene(backgroundPane, box);
	        primaryStage.setScene(scene);
			primaryStage.sizeToScene();
	        primaryStage.show();
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
