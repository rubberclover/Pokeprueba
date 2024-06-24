package presentation;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PokedexFilters extends Application {

	private StackPane backgroundPane;
    private ListView<String> listView;
    private String filter = "";
	private Map<String, ObservableList<String>> filterSelections = new HashMap<>();

	public PokedexFilters(StackPane backgroundPane) {
		this.backgroundPane = backgroundPane;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

        listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        updateListView("");
        
        // Filters Buttons
        Button generationSelect = PokedexMain.createButton("Generation: ", 100, 20, 10);
        generationSelect.setOnAction(event -> updateListView("Generation"));
        
        Button type1Select = PokedexMain.createButton("Type 1: ", 100, 20, 10);
        type1Select.setOnAction(event -> updateListView("Type 1"));

        Button type2Select = PokedexMain.createButton("Type 2: ", 100, 20, 10);
        type2Select.setOnAction(event -> updateListView("Type 2"));
        
        Button heightSelect = PokedexMain.createButton("Height: ", 100, 20, 10);
        heightSelect.setOnAction(event -> updateListView("Height"));

        Button weightSelect = PokedexMain.createButton("Weight: ", 100, 20, 10);
        weightSelect.setOnAction(event -> updateListView("Weight"));
        
        Button weaknessesSelect = PokedexMain.createButton("Weaknesses: ", 100, 20, 10);
        weaknessesSelect.setOnAction(event -> updateListView("Weaknesses"));
        
        Button Search = PokedexMain.createButton("Search", 100, 20, 10);
        Search.setOnAction(e -> {
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
        gridPane.add(type1Select, 0, 1);
        gridPane.add(type2Select, 0, 2);
        gridPane.add(heightSelect, 0, 3);
        gridPane.add(weightSelect, 0, 4);
        gridPane.add(weaknessesSelect, 0, 5);
        gridPane.add(Search, 0, 6);
        gridPane.add(listView, 1, 0, 1, 7);
        

		Scene scene = PokedexMain.createScene(backgroundPane, gridPane);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	private void updateListView(String category) {
        ObservableList<String> selectedItems = FXCollections.observableArrayList(listView.getSelectionModel().getSelectedItems());
        filterSelections.put(filter, selectedItems);
		filter = category;
	    selectedItems = filterSelections.getOrDefault(filter, FXCollections.observableArrayList());
        ObservableList<String> items = FXCollections.observableArrayList();
        switch (category) {
            case "Generation":
            	for (int i=1; i<10; i++) {
            		items.add("Generation " + i);
            	}
                break;
            case "Type 1":
            case "Type 2":
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
        	System.out.println(selectedItem);
            listView.getSelectionModel().select(selectedItem);
        }
    }

}
