package sample;

import java.io.IOException;
import java.util.Optional;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.db.service.TransactionFactory;

public class Pokedex implements AutoCloseable{

	private final DistributedTransactionManager manager;
	
	public Pokedex() throws IOException {
		TransactionFactory factory = TransactionFactory.create("scalardb.properties");
		manager = factory.getTransactionManager();
	}
	
	public void loadInitialData() throws TransactionException {
		System.out.println("bb");
		DistributedTransaction transaction = null;
	    try {
	      transaction = manager.start();
	      loadTypeIfNotExists(transaction, 1, "Normal");
	      transaction.commit();
	    } catch (TransactionException e) {
	      if (transaction != null) {
	        // If an error occurs, abort the transaction
	        transaction.abort();
	      }
	      throw e;
	    }
	}
	
	private void loadTypeIfNotExists(DistributedTransaction transaction, int typeId, String name) throws TransactionException {
		Optional<Result> type = transaction.get(
		   	Get.newBuilder()
			.namespace("sample")
			.table("customers")
			.partitionKey(Key.ofInt("type_id", typeId))
			.build());
		if (!type.isPresent()) {
			transaction.put(
				Put.newBuilder()
				.namespace("sample")
				.table("customers")
				.partitionKey(Key.ofInt("type_id", typeId))
				.textValue("name", name)
				.build());
		}
	}
	
	@Override
	public void close() throws Exception {
	    manager.close();
	}
}
