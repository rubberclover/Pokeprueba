package command;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.exception.transaction.AbortException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.db.service.TransactionFactory;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private static final String IMAGE = "image";
	
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
		// Commit the transaction*/
		tx.commit();
		return results;
	
	    } catch (Exception e) {
	     	tx.abort();
	    	throw e;
	    }
	}
  
  /*public void addPokemon(String id, String name, int generation, int type1, int type2, double height, double weight, String image) throws TransactionException {
	// Start a transaction
	DistributedTransaction tx = manager.start();
	
	try {
		Put put = Put.newBuilder()
		              .namespace(NAMESPACE)
		              .table(TABLENAME)
		              .partitionKey(Key.ofText(POKEMON_ID, id))
		              .textValue(NAME, name)
		              .intValue(GENERATION, generation)
		              .intValue(TYPE1, type1)
		              .intValue(TYPE2, type2)
		              .doubleValue(HEIGHT, height)
		              .doubleValue(WEIGHT, weight)
		              .textValue(IMAGE, image)
		              .build();
	    // Add the pokemon
	tx.put(put);
	// Commit the transaction
	    tx.commit();
	} catch (Exception e) {
	  tx.abort();
	  throw e;
	}
  }  */

	public List<Result> getPokemonsFiltered(Map<String, ObservableList<String>> filterSelections) throws TransactionException{
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
			// Commit the transaction*/
			tx.commit();
			Set<String> keys = filterSelections.keySet();
			for(String key : keys) {
				switch(key) {
					case "Generation":
						for(String filterCommand : filterSelections.get(key)) {
							results.removeIf(e -> e.getInt("generation") != Integer.parseInt(filterCommand));
						};
						break;
					case "Type":
						for(String filterCommand : filterSelections.get(key)) {
							results.removeIf(e -> e.getInt("type1") != Integer.parseInt(filterCommand) || e.getInt("type2") != Integer.parseInt(filterCommand));
						};
						break;
					case "Height":
						for(String filterCommand : filterSelections.get(key)) {
							if(filterCommand.contains("-")) {
							String[] arrOfStr = filterCommand.split("-", 2);
					        arrOfStr[1] = arrOfStr[1].replace("m", "");
							results.removeIf(e -> e.getDouble("height") < Double.parseDouble(arrOfStr[0]) || e.getDouble("height") > Double.parseDouble(arrOfStr[1]));
								}
								else {
									String[] arrOfStr = filterCommand.split("+", 2);
									results.removeIf(e -> e.getDouble("height") < Double.parseDouble(arrOfStr[0]));
								}
							};
						break;
					case "Weight":
						for(String filterCommand : filterSelections.get(key)) {
							if(filterCommand.contains("-")) {
								String[] arrOfStr = filterCommand.split("-", 2);
						        arrOfStr[1] = arrOfStr[1].replace("kg", "");
								results.removeIf(e -> e.getDouble("weight") < Double.parseDouble(arrOfStr[0]) || e.getDouble("weight") > Double.parseDouble(arrOfStr[1]));
									}
									else {
										String[] arrOfStr = filterCommand.split("+", 2);
										results.removeIf(e -> e.getDouble("weight") < Double.parseDouble(arrOfStr[0]));
									};
						};
						break;
					case "Weaknesses":
						List<Result> weakTo = weakness.getWeaknessByTypes(filterSelections.get(key));
						results.removeIf(e -> weakTo.contains(e.getInt("type1")) == false || weakTo.contains(e.getInt("type2")) == false);
						break;
					default:
		                break;
						}
				
			}
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
		// Commit the transaction*/
		tx.commit();
		if(search.matches("-?\\d+(\\.\\d+)?")){
			results.removeIf(e -> e.getInt("pokemon_id") != Integer.parseInt(search));
		}
		else
		{
		results.removeIf(e -> e.getText("name") != search);
		}
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
