package command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.io.File;

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
	
	public boolean loadInitialData() throws TransactionException, IOException {
		DistributedTransaction transaction = null;
	    try {
	      transaction = manager.start();
	      loadInitialTypes(transaction);
	      loadInitialPokemons(transaction);
	      loadInitialWeaknesses(transaction);
	      transaction.commit();
	      return true;
	    } catch (TransactionException e) {
	      if (transaction != null) {
	        transaction.abort();
	      }
	      throw e;
	    } catch (IOException e) {
	      throw e;
	    }
	}

	public void loadInitialTypes(DistributedTransaction transaction) throws TransactionException, IOException {
	      /*List<String> typeNames = Arrays.asList(
	            "None", "Normal", "Fire", "Water", "Electric", "Grass", "Ice", 
	            "Fighting", "Poison", "Ground", "Flying", "Psychic", 
	            "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy"
	      );*/
		  List<String> typeNames = Arrays.asList(
				  "None", "Normal", "Fire", "Water","Electric", "Grass", "Ice", 
		            "Fighting", "Poison", "Ground", "Flying", "Psychic", 
		            "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy");
	      byte[] image;
	      try {
		      for (int i=1; i<18; i++) {
		    	  image = Files.readAllBytes(Paths.get("images/type/"+typeNames.get(i)+".png"));
		    	  loadTypeIfNotExists(transaction, i, typeNames.get(i), image);
		      }
	      } catch (TransactionException e) {
	    	  throw e;
	      } catch (IOException e) {
		      throw e;
	      }
	}

	public void loadInitialPokemons(DistributedTransaction transaction) throws TransactionException, IOException {
		byte[] image;
		try {
		    image = Files.readAllBytes(Paths.get("images/pokemon/001_Bulbasaur.png"));
		    loadPokemonIfNotExists(transaction, 001,"Bulbasaur", image);
	      } catch (IOException e) {
		      throw e;
	      }
		
	}

	private void loadPokemonIfNotExists(DistributedTransaction transaction, int i, String string, byte[] image) {
		// TODO Auto-generated method stub
		
	}

	public void loadInitialWeaknesses(DistributedTransaction transaction) throws TransactionException {
	      List<String> typeNames = Arrays.asList(
	            "None", "Normal", "Fire", "Water", "Electric", "Grass", "Ice", 
	            "Fighting", "Poison", "Ground", "Flying", "Psychic", 
	            "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy"
	      );
	      try {
	    	  loadWeaknessIfNotExists(transaction, 1, typeNames.indexOf("Normal"), typeNames.indexOf("Fighting"), 2.0);
	      } catch (TransactionException e) {
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
	
	//loadPokemonIfNotExists
	
	@Override
	public void close() throws Exception {
	    manager.close();
	}
}
