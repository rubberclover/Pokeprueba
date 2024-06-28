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
import java.util.List;

public class Weakness {

	private static final String NAMESPACE = "pokedex";
	private static final String TABLENAME = "weakness";
	private static final String TYPE_ID = "type_id";
	private static final String ATTACKER_TYPE = "attacker_type";
	private static final String MULT = "mult";

	private final DistributedTransactionManager manager;

	public Weakness(String scalarDBProperties) throws IOException {
		TransactionFactory factory = TransactionFactory.create(scalarDBProperties);
		manager = factory.getTransactionManager();
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
			results.removeIf(e -> idTypes.contains(e.getInt(ATTACKER_TYPE)) == false || e.getDouble(MULT) < 2);
		    // Commit the transaction
		    tx.commit();
		
		    return results;
		} catch (Exception e) {
			tx.abort();
			throw e;
		}
	}
	
	public List<Result> getWeaknessByTypes(Integer type1, Integer type2) throws TransactionException {
		// Start a transaction
		DistributedTransaction tx = manager.start();
		
		try {
			Scan scan =
				    Scan.newBuilder()
				        .namespace(NAMESPACE)
				        .table(TABLENAME)
				        .all()
				        .build();
			List<Result> endResults = new ArrayList<>();  
			List<Result> results =  tx.scan(scan);  
			
			for (Result res : results) {
	            if (res.getInt(TYPE_ID) == type1 || res.getInt(TYPE_ID) == type2) {
	                double multiplier = res.getDouble(MULT);
	                if (multiplier >= 2.0) {
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
						        .table(TABLENAME)
						        .all()
						        .build();
					List<Result> endResults = new ArrayList<>();  
					List<Result> results =  tx.scan(scan);  
					
					for (Result res : results) {
			            if (res.getInt(TYPE_ID) == type1 || res.getInt(TYPE_ID) == type2) {
			                double multiplier = res.getDouble(MULT);
			                if (multiplier < 1.0) {
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

	public List<Result> getEffectiveAttackByTypes(int type1, int type2) throws TransactionException {
		/// Start a transaction
		DistributedTransaction tx = manager.start();
		
		try {
			Scan scan =
				    Scan.newBuilder()
				        .namespace(NAMESPACE)
				        .table(TABLENAME)
				        .all()
				        .build();
			List<Result> endResults = new ArrayList<>();  
			List<Result> results =  tx.scan(scan);  

			for (Result res : results) {
	            if (res.getInt(ATTACKER_TYPE) == type1 || res.getInt(ATTACKER_TYPE) == type2) {
	                double multiplier = res.getDouble(MULT);
	                if (multiplier >= 2.0) {
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

	public List<Result> getNonEffectiveAttackByTypes(int type1, int type2) throws TransactionException {
		/// Start a transaction
		DistributedTransaction tx = manager.start();
		
		try {
			Scan scan =
				    Scan.newBuilder()
				        .namespace(NAMESPACE)
				        .table(TABLENAME)
				        .all()
				        .build();
			List<Result> endResults = new ArrayList<>();  
			List<Result> results =  tx.scan(scan);  

			for (Result res : results) {
	            if (res.getInt(ATTACKER_TYPE) == type1 || res.getInt(ATTACKER_TYPE) == type2) {
	                double multiplier = res.getDouble(MULT);
	                if (multiplier < 1.0) {
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
	
	public void close() {
		manager.close();
	}
}
