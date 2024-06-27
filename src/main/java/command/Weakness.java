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

import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	public List<Result> getWeaknessByTypes(ObservableList<String> types) throws TransactionException {
		List<String> typeNames = Arrays.asList(
				  "None", "Normal", "Fire", "Water","Electric", "Grass", "Ice", 
		            "Fighting", "Poison", "Ground", "Flying", "Psychic", 
		            "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy");
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
			List<Integer> idTypes = new ArrayList<>();  
			for(String type : types) {
				int typeInt = typeNames.indexOf(type);
				idTypes.add(typeInt);
			}
			results.removeIf(e -> idTypes.contains(e.getInt("weakness_id")) == false);
		    // Commit the transaction
		    tx.commit();
		
		    return results;
		} catch (Exception e) {
			tx.abort();
			throw e;
		}
	}
  // Method to get weaknesses for 2 types
	
	public List<Result> getWeaknessByTypes(Integer type1, Integer type2) throws TransactionException {
		// Start a transaction
		DistributedTransaction tx = manager.start();
		
		try {
			Scan scan =
				    Scan.newBuilder()
				        .namespace(NAMESPACE)
				        .table("weakness")
				        .all()
				        .build();
			List<Result> endResults = new ArrayList<>();  
			List<Result> results =  tx.scan(scan);  
			List<Result> results2 =  tx.scan(scan); 
			results.removeIf(e -> e.getInt("weakness_id") != type1);
			results2.removeIf(e -> e.getInt("weakness_id") != type2);
			for(Result res: results) {
				for(Result res2: results2) {
					if(res.getInt("attacker_type") == res2.getInt("attacker_type") & (res.getDouble("mult") * res2.getDouble("mult")) >= 2.0) {
						endResults.add(res);
					}
				}
			}
		    // Commit the transaction
		    tx.commit();
		
		    return endResults;
		} catch (Exception e) {
			tx.abort();
			throw e;
		}
	}
	
	public List<Result> getWeaknessNonEffectiveByTypes(Integer type1, Integer type2) throws TransactionException {
		// Start a transaction
		DistributedTransaction tx = manager.start();
		
		try {
			Scan scan =
				    Scan.newBuilder()
				        .namespace(NAMESPACE)
				        .table("weakness")
				        .all()
				        .build();
			List<Result> endResults = new ArrayList<>();  
			List<Result> results =  tx.scan(scan);  
			List<Result> results2 =  tx.scan(scan); 
			results.removeIf(e -> e.getInt("weakness_id") != type1);
			results2.removeIf(e -> e.getInt("weakness_id") != type2);
			for(Result res: results) {
				for(Result res2: results2) {
					if(res.getInt("attacker_type") == res2.getInt("attacker_type") & (res.getDouble("mult") * res2.getDouble("mult")) <= 0.5) {
						endResults.add(res);
					}
				}
			}
		    // Commit the transaction
		    tx.commit();
		
		    return endResults;
		} catch (Exception e) {
			tx.abort();
			throw e;
		}
	}
  
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
