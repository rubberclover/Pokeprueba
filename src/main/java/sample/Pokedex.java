package sample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.exception.transaction.CrudConflictException;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.exception.transaction.UnsatisfiedConditionException;
import com.scalar.db.io.Key;
import com.scalar.db.service.TransactionFactory;

public class Pokedex implements AutoCloseable{

	private final DistributedTransactionManager manager;
	
	public Pokedex() throws IOException {
		TransactionFactory factory = TransactionFactory.create("scalardb.properties");
		manager = factory.getTransactionManager();
	}
	
	public void loadInitialData() throws TransactionException, IOException {
		System.out.println("bb");
		DistributedTransaction transaction = null;
	    try {
	      transaction = manager.start();
	      byte[] image = Files.readAllBytes(Paths.get("images/type/Normal.png"));
	      //loadTypeIfNotExists(transaction, 1, "Normal", image);
	      image = Files.readAllBytes(Paths.get("images/type/Fire.png"));
	      loadTypeIfNotExists(transaction, 2, "Fire",image);
	      image = Files.readAllBytes(Paths.get("images/type/Water.png"));
	      loadTypeIfNotExists(transaction, 3, "Water",image);
	      loadWeaknessIfNotExists(transaction, 1, 2, 3, 2.0);
	      /*loadTypeIfNotExists(transaction, 4, "Electric");
	      loadTypeIfNotExists(transaction, 5, "Grass");
	      loadTypeIfNotExists(transaction, 6, "Ice");
	      loadTypeIfNotExists(transaction, 7, "Fighting");
	      loadTypeIfNotExists(transaction, 8, "Poison");
	      loadTypeIfNotExists(transaction, 9, "Ground");
	      loadTypeIfNotExists(transaction, 10, "Flying");
	      loadTypeIfNotExists(transaction, 11, "Psychic");
	      loadTypeIfNotExists(transaction, 12, "Bug");
	      loadTypeIfNotExists(transaction, 13, "Rock");
	      loadTypeIfNotExists(transaction, 14, "Ghost");
	      loadTypeIfNotExists(transaction, 15, "Dragon");
	      loadTypeIfNotExists(transaction, 16, "Dark");
	      loadTypeIfNotExists(transaction, 17, "Steel");
	      loadTypeIfNotExists(transaction, 18, "Fairy");*/
	      transaction.commit();
	    } catch (TransactionException e) {
	      if (transaction != null) {
	        // If an error occurs, abort the transaction
	        transaction.abort();
	      }
	      throw e;
	    }
	}
	
	private void loadWeaknessIfNotExists(DistributedTransaction transaction, Integer id, Integer type_id, Integer attacker_type, Double mult) throws TransactionException {
		Optional<Result> type = transaction.get(
			   	Get.newBuilder()
				.namespace("pokedex")
				.table("weakness")
				.partitionKey(Key.ofInt("weakness_id", id))
				.clusteringKey(Key.of("type_id", type_id,"attacker_type", attacker_type))
				.build());
			if (!type.isPresent()) {
				transaction.put(
					Put.newBuilder()
					.namespace("pokedex")
					.table("weakness")
					.partitionKey(Key.ofInt("weakness_id", id))
					.clusteringKey(Key.of("type_id", type_id,"attacker_type", attacker_type))
					.doubleValue("mult", mult)
					.build());
			}
	}

	private void loadTypeIfNotExists(DistributedTransaction transaction, int typeId, String name, byte[] image) throws TransactionException {
		Optional<Result> type = transaction.get(
		   	Get.newBuilder()
			.namespace("pokedex")
			.table("type")
			.partitionKey(Key.ofInt("type_id", typeId))
			.build());
		if (!type.isPresent()) {
			transaction.put(
				Put.newBuilder()
				.namespace("pokedex")
				.table("type")
				.partitionKey(Key.ofInt("type_id", typeId))
				.textValue("name", name)
				.blobValue("image", image)
				.build());
		}
	}
	
	@Override
	public void close() throws Exception {
	    manager.close();
	}
}
