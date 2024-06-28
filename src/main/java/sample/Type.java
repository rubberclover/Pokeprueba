package sample;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Result;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.db.service.TransactionFactory;
import java.io.IOException;
import java.util.Optional;

public class Type {

  private static final String NAMESPACE = "pokedex";
  private static final String TABLENAME = "type";
  private static final String TYPE_ID = "type_id";
  private static final String NAME = "name";

  private final DistributedTransactionManager manager;

  public Type(String scalarDBProperties) throws IOException {
    TransactionFactory factory = TransactionFactory.create(scalarDBProperties);
    manager = factory.getTransactionManager();
  }

  public String getType(String id) throws TransactionException {
	// Start a transaction
	DistributedTransaction tx = manager.start();
	
	try {
		Get get = Get.newBuilder()
		              .namespace(NAMESPACE)
		              .table(TABLENAME)
		              .indexKey(Key.ofText(TYPE_ID, id))
		              .build();
		
		Optional<Result> result = tx.get(get);
	
		String type = "";
	    if (result.isPresent()) {
	    	type = result.get().getText(NAME);
	    }
	
	    // Commit the transaction
	    tx.commit();
	
	    return type;
	} catch (Exception e) {
		tx.abort();
		throw e;
	}
<<<<<<< Updated upstream:src/main/java/sample/Type.java
  }
  
  public Result getTypeByName(String name) throws TransactionException {
		// Start a transaction
		DistributedTransaction tx = manager.start();
		
		try {
			Scan scan =
				    Scan.newBuilder()
				        .namespace(NAMESPACE)
				        .table(TABLENAME)
				        .all()
				        .build();
			List<Result> results =  tx.scan(scan);  
			
			results.removeIf(e -> e.getText(NAME) != name);
			Result res = results.get(0);
		    // Commit the transaction
		    tx.commit();
		
		    return res;
		} catch (Exception e) {
			tx.abort();
			throw e;
		}
	  }
  
  public List<Result> getAllTypes() throws TransactionException {
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
  
  public void addType(String id, String name) throws TransactionException {
	// Start a transaction
	DistributedTransaction tx = manager.start();
	
	try {
		Put put = Put.newBuilder()
		              .namespace(NAMESPACE)
		              .table(TABLENAME)
		              .partitionKey(Key.ofText(TYPE_ID, id))
		              .textValue(NAME, name)
		              .build();
	    // Add the type
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
=======
	
	public void close() {
		manager.close();
	}
>>>>>>> Stashed changes:src/main/java/command/Type.java
}
