package sample;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.GetBuilder;
import com.scalar.db.api.GetBuilder.BuildableGet;
import com.scalar.db.api.GetBuilder.PartitionKeyOrIndexKey;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.db.service.TransactionFactory;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class Pokemon {

  private static final String NAMESPACE = "pokedex";
  private static final String TABLENAME = "pokemon";
  private static final String POKEMON_ID = "pokemon_id";
  private static final String NAME = "name";
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

  public Integer getPokemon(Map<String, Object> filters) throws TransactionException {
	// Start a transaction
	DistributedTransaction tx = manager.start();
	
	try {
		PartitionKeyOrIndexKey getBuilder = Get.newBuilder()
		              .namespace(NAMESPACE)
		              .table(TABLENAME);
		
		for (Map.Entry<String, Object> entry : filters.entrySet()) {
			String columnName = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof String) {
				getBuilder.textValue(columnName, (String) value);
			} else if (value instanceof Integer) {
				getBuilder.intValue(columnName, (Integer) value);
			} else if (value instanceof Double) {
				getBuilder.doubleValue(columnName, (Double) value);
			}
		}
		
	    Get get = getBuilder.build();
		Optional<Result> result = tx.get(get);
	
	    Integer pokemon = -1;
	    if (result.isPresent()) {
	    	pokemon = result.get().getInt(POKEMON_ID);
	    }
	
	    // Commit the transaction
	    tx.commit();
	
	    return pokemon;
	} catch (Exception e) {
		tx.abort();
		throw e;
	}
  }
  
  public void addPokemon(String id, String name, int type1, int type2, double height, double weight, String image) throws TransactionException {
	// Start a transaction
	DistributedTransaction tx = manager.start();
	
	try {
		Put put = Put.newBuilder()
		              .namespace(NAMESPACE)
		              .table(TABLENAME)
		              .partitionKey(Key.ofText(POKEMON_ID, id))
		              .textValue(NAME, name)
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
  }  

  public void close() {
    manager.close();
  }
}
