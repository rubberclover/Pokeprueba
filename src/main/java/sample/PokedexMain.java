package sample;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PokedexMain extends Application {

	 @Override
	    public void start(Stage primaryStage) {
	        primaryStage.setTitle("Pokedex Application");

	        // Create a button to initialize Pokedex
	        Button btn = new Button();
	        btn.setText("Initialize Pokedex");
	        btn.setOnAction(event -> initializePokedex());

	        StackPane root = new StackPane();
	        root.getChildren().add(btn);

	        primaryStage.setScene(new Scene(root, 300, 250));
	        primaryStage.show();
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
