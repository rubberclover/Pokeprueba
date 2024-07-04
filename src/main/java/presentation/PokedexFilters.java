package presentation;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PokedexFilters extends Application {

    private ListView<String> listView;
    private String filter = "";
	private Map<String, ObservableList<String>> filterSelections = new HashMap<>();

	@Override
	public void start(Stage primaryStage) throws Exception {
		byte[] backgroundBytes = Files.readAllBytes(Paths.get("images/PokedexFilter.png"));
        Image backgroundImage = new Image(new ByteArrayInputStream(backgroundBytes));
		BackgroundSize backgroundSize = new BackgroundSize(540, 700, true, true, true, true);
		BackgroundImage backgroundImg = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
		Background background = new Background(backgroundImg);

		StackPane backgroundPane = new StackPane();
		backgroundPane.setBackground(background);
		
        listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        updateListView("");
        
        // Filters Buttons
        Button generationSelect = PokedexMain.createButton("Generation: ", 100, 20, 10);
        generationSelect.setOnAction(event -> updateListView("Generation"));
        
        Button typeSelect = PokedexMain.createButton("Type: ", 100, 20, 10);
        typeSelect.setOnAction(event -> updateListView("Type"));
        
        Button heightSelect = PokedexMain.createButton("Height: ", 100, 20, 10);
        heightSelect.setOnAction(event -> updateListView("Height"));

        Button weightSelect = PokedexMain.createButton("Weight: ", 100, 20, 10);
        weightSelect.setOnAction(event -> updateListView("Weight"));
        
        Button weaknessesSelect = PokedexMain.createButton("Weaknesses: ", 100, 20, 10);
        weaknessesSelect.setOnAction(event -> updateListView("Weaknesses"));
        
        Button Search = PokedexMain.createButton("Search", 100, 20, 10);
        Search.setOnAction(e -> {
    		ObservableList<String> selectedItems = FXCollections.observableArrayList(listView.getSelectionModel().getSelectedItems());
    		if (!selectedItems.isEmpty()) {
                filterSelections.put(filter, selectedItems);
            } else {
                filterSelections.remove(filter);
            }
			PokedexMain pokemonDisplay = new PokedexMain(filterSelections);
			PokedexMain.launchVerification(pokemonDisplay, primaryStage);
		});
        
        // Layout using GridPane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        
        // Add labels and ListView to GridPane
        gridPane.add(generationSelect, 0, 0);
        gridPane.add(typeSelect, 0, 1);
        gridPane.add(heightSelect, 0, 2);
        gridPane.add(weightSelect, 0, 3);
        gridPane.add(weaknessesSelect, 0, 4);
        gridPane.add(Search, 0, 5);
        gridPane.add(listView, 1, 0, 1, 6);
        
        StackPane.setMargin(gridPane, new Insets(80, 0, 0, 0));
		Scene scene = PokedexMain.createScene(backgroundPane, gridPane);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	private void updateListView(String category) {
		ObservableList<String> selectedItems = FXCollections.observableArrayList(listView.getSelectionModel().getSelectedItems());
		if (!selectedItems.isEmpty()) {
            filterSelections.put(filter, selectedItems);
        } else {
            filterSelections.remove(filter);
        }
		filter = category;
	    selectedItems = filterSelections.getOrDefault(filter, FXCollections.observableArrayList());
        ObservableList<String> items = FXCollections.observableArrayList();
        switch (category) {
            case "Generation":
            	for (int i=1; i<10; i++) {
            		items.add("Generation " + i);
            	}
                break;
            case "Type":
            case "Weaknesses":
                items.addAll("None", "Normal", "Fire", "Water", "Electric", "Grass", "Ice", 
        	            "Fighting", "Poison", "Ground", "Flying", "Psychic", 
        	            "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy");
                break;
            case "Height":
                items.addAll("0-1m", "1-2m", "2-5m", "5-10m", "10+m");
                break;
            case "Weight":
                items.addAll("0-10kg", "10-20kg", "20-50kg", "50-100kg", "100+kg");
                break;
            default:
                break;
        }
        listView.setItems(items);

        listView.getSelectionModel().clearSelection();
        for (String selectedItem : selectedItems) {
            listView.getSelectionModel().select(selectedItem);
        }
    }
}
