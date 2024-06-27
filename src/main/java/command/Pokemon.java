package command;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.db.service.TransactionFactory;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

	public List<Result> getPokemonsFiltered(Map<String, ObservableList<String>> filterSelections) {
		// Convert ObservableList into List of string and make search of filters
		// Weaknesses should be called in Weakness.java to find the types
		return null;
	}
	
	public List<Result> getPokemonByNameOrId(String search) {
		
		return null;
	}

	public void close() {
		manager.close();
	}
}
