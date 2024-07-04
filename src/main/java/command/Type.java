package command;

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

	private final DistributedTransactionManager manager;

	public Type(String scalarDBProperties) throws IOException {
		TransactionFactory factory = TransactionFactory.create(scalarDBProperties);
		manager = factory.getTransactionManager();
	}

	public Result getType(Integer id) throws TransactionException {
		// Start a transaction
		DistributedTransaction tx = manager.start();
		
		try {
			Get get = Get.newBuilder()
			              .namespace(NAMESPACE)
			              .table(TABLENAME)
			              .indexKey(Key.ofInt(TYPE_ID, id))
			              .build();
			
			Optional<Result> result = tx.get(get);
		    // Commit the transaction
		    tx.commit();
		
		    return result.orElse(null);
		} catch (Exception e) {
			tx.abort();
			throw e;
		}
	}
	
	public void close() {
		manager.close();
	}
}
