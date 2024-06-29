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
import java.util.Collections;
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
	
	public void orderMyList(List<Result> list){
		for(int i = 0; i < list.size(); i ++) {
			int index = 0;
			for(int j = i + 1; j < list.size(); j ++) {
				System.out.print(list.get(i));
				if(list.get(i).getInt(POKEMON_ID) > list.get(j).getInt(POKEMON_ID)) {
					index = j;
					Result aux = list.get(j);
					list.set(j,list.get(index));
					list.set(index,aux);
					
				}
			}
		}
		System.out.print(list);
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
		// Commit the transaction
		tx.commit();
		//orderMyList(results);
		return results;
		
	    } catch (Exception e) {
	     	tx.abort();
	    	throw e;
	    }
	}

	public List<Result> getPokemonsFiltered(Map<String, ObservableList<String>> filterSelections) throws TransactionException, IOException{
		// Convert ObservableList into List of string and make search of filters
		// Weaknesses should be called in Weakness.java to find the types
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
						List<Double> valuesPair = new ArrayList<>();
						List<Double> valuesOdd = new ArrayList<>();
						for(String filterCommand : filterSelections.get(key)) {
							if(filterCommand.contains("-")) {
								String[] arrOfStr = filterCommand.split("-", 2);
						        arrOfStr[1] = arrOfStr[1].replace("m", "");
						        valuesPair.add(Double.parseDouble(arrOfStr[0]));
						        valuesOdd.add(Double.parseDouble(arrOfStr[1]));
						        //results.removeIf(e -> e.getDouble(HEIGHT) < Double.parseDouble(arrOfStr[0]) || e.getDouble(HEIGHT) > Double.parseDouble(arrOfStr[1]));
							} else {
								existAdd = true;
								//String[] arrOfStr = filterCommand.split("+", 2);
								//valuesHeigh.add(filterCommand);
								//results.removeIf(e -> e.getDouble(HEIGHT) < Double.parseDouble(arrOfStr[0]));
							}
						};
						
						if(existAdd) {
							results.removeIf(e -> (valuesPair.stream().allMatch(i -> i > e.getDouble(HEIGHT)) || valuesOdd.stream().allMatch(i -> i < e.getDouble(HEIGHT))) & e.getDouble(WEIGHT) < 10.0);
						}
						else {
							//System.out.print(valuesPair.stream().anyMatch(i -> i > results.get(0).getDouble(HEIGHT)));
							//System.out.print(valuesOdd.stream().anyMatch(i -> i < results.get(0).getDouble(HEIGHT)));
							
							results.removeIf(e -> valuesPair.stream().allMatch(i -> i > e.getDouble(HEIGHT)) || valuesOdd.stream().allMatch(i -> i < e.getDouble(HEIGHT)));
						}
						
						break;
					case "Weight":
						boolean existAdd2 = false;
						List<Double> valuesPair2 = new ArrayList<>();
						List<Double> valuesOdd2 = new ArrayList<>();
						for(String filterCommand : filterSelections.get(key)) {
							if(filterCommand.contains("-")) {
								String[] arrOfStr = filterCommand.split("-", 2);
								arrOfStr[1] = arrOfStr[1].replace("kg", "");
						        valuesPair2.add(Double.parseDouble(arrOfStr[0]));
						        valuesOdd2.add(Double.parseDouble(arrOfStr[1]));
						        //results.removeIf(e -> e.getDouble(HEIGHT) < Double.parseDouble(arrOfStr[0]) || e.getDouble(HEIGHT) > Double.parseDouble(arrOfStr[1]));
							} else {
								existAdd2 = true;
								//String[] arrOfStr = filterCommand.split("+", 2);
								//valuesHeigh.add(filterCommand);
								//results.removeIf(e -> e.getDouble(HEIGHT) < Double.parseDouble(arrOfStr[0]));
							}
						};
						
						if(existAdd2) {
							results.removeIf(e -> (valuesPair2.stream().allMatch(i -> i > e.getDouble(WEIGHT)) || valuesOdd2.stream().allMatch(i -> i < e.getDouble(WEIGHT))) & e.getDouble(WEIGHT) < 100.0);
						}
						else {
							//System.out.print(valuesPair.stream().anyMatch(i -> i > results.get(0).getDouble(HEIGHT)));
							//System.out.print(valuesOdd.stream().anyMatch(i -> i < results.get(0).getDouble(HEIGHT)));
				
							results.removeIf(e -> valuesPair2.stream().allMatch(i -> i > e.getDouble(WEIGHT)) || valuesOdd2.stream().allMatch(i -> i < e.getDouble(WEIGHT)));
						}
						/*for(String filterCommand : filterSelections.get(key)) {
							if(filterCommand.contains("-")) {
								String[] arrOfStr = filterCommand.split("-", 2);
						        arrOfStr[1] = arrOfStr[1].replace("kg", "");
								results.removeIf(e -> e.getDouble(WEIGHT) < Double.parseDouble(arrOfStr[0]) || e.getDouble(WEIGHT) > Double.parseDouble(arrOfStr[1]));
							} else {
								String[] arrOfStr = filterCommand.split("+", 2);
								results.removeIf(e -> e.getDouble(WEIGHT) < Double.parseDouble(arrOfStr[0]));
							}
						};*/
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
