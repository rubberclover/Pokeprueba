package command;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.api.Scan;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.db.service.TransactionFactory;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Weakness {

	private static final String NAMESPACE = "pokedex";
	private static final String TABLENAME = "weakness";
	private static final String TYPE_ID = "type_id";
	private static final String ATTACKER_TYPE = "attacker_type";
	private static final String WEAKNESS_ID = "weakness_id";
	private static final String MULT = "mult";

	private final DistributedTransactionManager manager;

	public Weakness(String scalarDBProperties) throws IOException {
		TransactionFactory factory = TransactionFactory.create(scalarDBProperties);
		manager = factory.getTransactionManager();
	}
  
	public List<Result> getWeaknessByType(Integer type) throws TransactionException {
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
			
			results.removeIf(e -> e.getInt(TYPE_ID) != type);
		    // Commit the transaction
		    tx.commit();
		
		    return results;
		} catch (Exception e) {
			tx.abort();
			throw e;
		}
	}
  // Method to get weaknesses for 2 types
  
  // Method to calcul weakness for 2 types
  
  // Method to get attack for 1 type
  
  // Method to get attack for 2 type
  
  /*public List<Result> getAllWeaknesses() throws TransactionException {
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
	      return results;

	    } catch (Exception e) {
	      tx.abort();
	      throw e;
	    }
	  } */

	public void close() {
		manager.close();
	}
}
