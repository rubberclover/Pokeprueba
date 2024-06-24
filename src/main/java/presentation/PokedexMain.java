package presentation;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.scalar.db.api.Result;

import command.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PokedexMain extends Application {

     private StackPane root = new StackPane();
     private String scalarDB = "scalardb.properties";
     
	 @Override
	    public void start(Stage primaryStage) {
	        primaryStage.setTitle("Pokedex Application");

	        // Create a button to initialize Pokedex
	        Button btn = new Button();
	        btn.setText("Initialize Pokedex");
	        btn.setOnAction(event -> {
				try {
					initializePokedex();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			});
	        
	        Button btn1 = new Button();
	        btn1.setText("Get Types");
	        btn1.setOnAction(event -> {
				try {
					getAllTypes();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

	        VBox box = new VBox();
	        box.getChildren().addAll(btn, btn1);
	        root.getChildren().addAll(box);

	        primaryStage.setScene(new Scene(root, 300, 250));
	        primaryStage.show();
	    }

	    private void getAllTypes() throws Exception {
			Type type = new Type(scalarDB);
			
			List<Result> results = type.getAllTypes();
			System.out.print("Number of types " + results.size());
			
			byte[] bytes = results.get(0).getBlobAsBytes("image");
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			Image image = new Image(bis);
			ImageView imageView = new ImageView(image);
			root.getChildren().add(imageView);
	    }

		private void initializePokedex() throws Exception {
			Pokedex pokedex = new Pokedex();
			if (pokedex.loadInitialData()) {
				System.out.println("Pokedex initialized successfully");
			}
			pokedex.close();
	    }

	    public static void main(String[] args) {
	        launch(args);
	    }
}
