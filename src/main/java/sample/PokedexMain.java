package sample;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.scalar.db.api.Result;
import com.scalar.db.exception.transaction.TransactionException;

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
     
	 @Override
	    public void start(Stage primaryStage) {
	        primaryStage.setTitle("Pokedex Application");

	        // Create a button to initialize Pokedex
	        Button btn = new Button();
	        btn.setText("Initialize Pokedex");
	        btn.setOnAction(event -> initializePokedex());
	        
	        Button btn1 = new Button();
	        btn1.setText("Get Types");
	        btn1.setOnAction(event -> {
				try {
					getAllTypes(root);
				} catch (TransactionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

	        VBox box = new VBox();
	        box.getChildren().addAll(btn, btn1);
	        root.getChildren().addAll(box);

	        primaryStage.setScene(new Scene(root, 300, 250));
	        primaryStage.show();
	    }

	    private void getAllTypes(StackPane root) throws TransactionException {
	        try {
				Type type = new Type("scalardb.properties");
				List<Result> results = type.getAllTypes();
				System.out.print("Number of types " + results.size());
				byte[] bytes = results.get(0).getBlobAsBytes("image");
				ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				Image image = new Image(bis);
				ImageView imageView = new ImageView(image);
				root.getChildren().add(imageView);
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
	        
	    }

		private void initializePokedex() {
	        // Create a callable instance
	        InitializePokedex initializePokedex = new InitializePokedex();
	        
	        // Create an executor service to run the callable
	        ExecutorService executorService = Executors.newSingleThreadExecutor();
	        
	        // Submit the callable to the executor service
	        Future<Integer> future = executorService.submit(initializePokedex);

	        // Optionally, handle the result or exception
	        try {
	            Integer result = future.get();
	            System.out.println("Pokedex initialized with result: " + result);
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            executorService.shutdown();
	        }
	    }

	    public static void main(String[] args) {
	        launch(args);
	    }
}
