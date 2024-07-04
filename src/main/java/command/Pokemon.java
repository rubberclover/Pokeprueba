package command;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.service.TransactionFactory;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Pokemon {

	private static final String NAMESPACE = "pokedex";
	private static final String TABLENAME = "pokemon";
	private static final String POKEMON_ID = "pokemon_id";
	private static final String NAME = "name";
	private static final String GENERATION = "generation";
	private static final String TYPE1 = "type1";
	private static final String TYPE2 = "type2";
	private static final String HEIGHT = "height";
	private static final String WEIGHT = "weight";
	
	private final DistributedTransactionManager manager;
	
	public Pokemon(String scalarDBProperties) throws IOException {
	    TransactionFactory factory = TransactionFactory.create(scalarDBProperties);
	    manager = factory.getTransactionManager();
	}

	public List<Result> getAllPokemons() throws TransactionException {
		// Start a transaction
	    DistributedTransaction tx = manager.start();
	    try {
	    Scan scan =
	    Scan.newBuilder()
	        .namespace(NAMESPACE)
	        .table(TABLENAME)
	        .all()
	        .build();
	    List<Result> results = tx.scan(scan);    
        results.sort(Comparator.comparing(result -> result.getInt("pokemon_id")));
		// Commit the transaction
		tx.commit();
		return results;
		
	    } catch (Exception e) {
	     	tx.abort();
	    	throw e;
	    }
	}

	public List<Result> getPokemonsFiltered(Map<String, ObservableList<String>> filterSelections) throws TransactionException, IOException{
		Weakness weakness = new Weakness("scalardb.properties");
		DistributedTransaction tx = manager.start();
		try {
		    Scan scan =
		    Scan.newBuilder()
		        .namespace(NAMESPACE)
		        .table(TABLENAME)
		        .all()
		        .build();
		    List<Result> results = tx.scan(scan);
			Set<String> keys = filterSelections.keySet();
			for(String key : keys) {
				switch(key) {
					case "Generation":
					    Set<Integer> generationsToKeep = filterSelections.get(key).stream()
					            .map(filterCommand -> Integer.parseInt(filterCommand.replaceAll("\\D+", "")))
					            .collect(Collectors.toSet());
	
					    results.removeIf(e -> !generationsToKeep.contains(e.getInt(GENERATION)));
					    break;
					case "Type":
						List<String> typeNames = Arrays.asList(
								  "None", "Normal", "Fire", "Water","Electric", "Grass", "Ice", 
						            "Fighting", "Poison", "Ground", "Flying", "Psychic", 
						            "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy");
						
						for(String filterCommand : filterSelections.get(key)) {
							results.removeIf(e -> e.getInt(TYPE1) != typeNames.indexOf(filterCommand) && e.getInt(TYPE2) != typeNames.indexOf(filterCommand));
						};
						break;
					case "Height":
						boolean existAdd = false;
						List<Result> aux3 = new ArrayList<>(results.size());
						Set<Result> aux4 = new LinkedHashSet<>();
						List<Double> valuesPair = new ArrayList<>();
						List<Double> valuesOdd = new ArrayList<>();
						for(String filterCommand : filterSelections.get(key)) {
							if(filterCommand.contains("-")) {
								String[] arrOfStr = filterCommand.split("-", 2);
						        arrOfStr[1] = arrOfStr[1].replace("m", "");
						        valuesPair.add(Double.parseDouble(arrOfStr[0]));
						        valuesOdd.add(Double.parseDouble(arrOfStr[1]));
							} else {
								existAdd = true;
							}
						};
						
						if(existAdd) {
							for(int m = 0; m < valuesPair.size(); m++) {
								int x = m;
								aux3 = results.stream().collect(Collectors.toList());
								aux3.removeIf(e -> e.getDouble(HEIGHT) < valuesPair.get(x) || valuesOdd.get(x) < e.getDouble(HEIGHT));
								aux4.addAll(aux3);
							}
							aux3 = results.stream().collect(Collectors.toList());
							aux3.removeIf(e -> e.getDouble(HEIGHT) < 10.0);
							aux4.addAll(aux3);
							results = new ArrayList<>(aux4);
						} else {
							for(int i = 0; i < valuesPair.size(); i++) {
								int x = i;
								aux3 = results.stream().collect(Collectors.toList());
								aux3.removeIf(e -> e.getDouble(HEIGHT) < valuesPair.get(x) || valuesOdd.get(x) < e.getDouble(HEIGHT));
								aux4.addAll(aux3);
								
							}
							results = new ArrayList<>(aux4);
						}
						break;
					case "Weight":
						boolean existAdd2 = false;
						List<Result> aux = new ArrayList<>(results.size());
						Set<Result> aux2 = new LinkedHashSet<>();
						List<Double> valuesPair2 = new ArrayList<>();
						List<Double> valuesOdd2 = new ArrayList<>();
						for(String filterCommand : filterSelections.get(key)) {
							if(filterCommand.contains("-")) {
								String[] arrOfStr = filterCommand.split("-", 2);
								arrOfStr[1] = arrOfStr[1].replace("kg", "");
						        valuesPair2.add(Double.parseDouble(arrOfStr[0]));
						        valuesOdd2.add(Double.parseDouble(arrOfStr[1]));
							} else {
								existAdd2 = true;
							}
						};
						
						if(existAdd2) {
							for(int m = 0; m < valuesPair2.size(); m++) {
								int x = m;
								aux = results.stream().collect(Collectors.toList());
								aux.removeIf(e -> e.getDouble(WEIGHT) < valuesPair2.get(x) || valuesOdd2.get(x) < e.getDouble(WEIGHT));
								aux2.addAll(aux);
							}
							aux = results.stream().collect(Collectors.toList());
							aux.removeIf(e -> e.getDouble(WEIGHT) < 100.0);
							aux2.addAll(aux);
							results = new ArrayList<>(aux2);
						} else {
							for(int i = 0; i < valuesPair2.size(); i++) {
								int x = i;
								aux = results.stream().collect(Collectors.toList());
								aux.removeIf(e -> e.getDouble(WEIGHT) < valuesPair2.get(x) || valuesOdd2.get(x) < e.getDouble(WEIGHT));
								aux2.addAll(aux);
								
							}
							results = new ArrayList<>(aux2);
						}
						break;
					case "Weaknesses":
						List<Result> weaknessList = weakness.getWeaknessByTypes(filterSelections.get(key));
						Set<Integer> typeIds = weaknessList.stream()
                                .map(result -> result.getInt("type_id"))
                                .collect(Collectors.toSet());
						results.removeIf(e -> !typeIds.contains(e.getInt(TYPE1)) && !typeIds.contains(e.getInt(TYPE2)));
						break;
					default:
		                break;
				}
			}  
	        results.sort(Comparator.comparing(result -> result.getInt("pokemon_id")));
			// Commit the transaction
			tx.commit();
			return results;
	    } catch (Exception e) {
	     	tx.abort();
	    	throw e;
	    }
	}
	
	public List<Result> getPokemonByNameOrId(String search) throws TransactionException {
		DistributedTransaction tx = manager.start();
	    try {
		    Scan scan =
		    Scan.newBuilder()
		        .namespace(NAMESPACE)
		        .table(TABLENAME)
		        .all()
		        .build();
		    List<Result> results = tx.scan(scan);
			if(search.matches("[0-9]+")){
				results.removeIf(e -> e.getInt(POKEMON_ID) != Integer.parseInt(search));
			} else {
				results.removeIf(e -> !e.getText(NAME).toLowerCase().contains(search.toLowerCase()));
			}    
	        results.sort(Comparator.comparing(result -> result.getInt("pokemon_id")));
			// Commit the transaction
			tx.commit();
			return results;
	    } catch (Exception e) {
	     	tx.abort();
	    	throw e;
	    }
	}

	public void close() {
		manager.close();
	}
}
