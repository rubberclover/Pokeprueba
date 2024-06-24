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
		  List<String> typeNames = Arrays.asList(
				  "None", "Normal", "Fire", "Water","Electric", "Grass", "Ice", 
		            "Fighting", "Poison", "Ground", "Flying", "Psychic", 
		            "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy");
	      byte[] image;
	      try {
		      for (int i=0; i<19; i++) {
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
		    loadPokemonIfNotExists(transaction, 1,"Bulbasaur",1,5,8,0.7,6.9,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/004_Charmander.png"));
		    loadPokemonIfNotExists(transaction, 4,"Charmander",1,2,0,0.6,8.5,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/007_Squirtle.png"));
		    loadPokemonIfNotExists(transaction, 7,"Squirtle",1,3,0,0.5,9,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/010_Caterpie.png"));
		    loadPokemonIfNotExists(transaction, 10,"Caterpie",1,12,0,0.3,2.9,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/016_Pidgey.png"));
		    loadPokemonIfNotExists(transaction, 16,"Pidgey",1,1,10,0.3,1.8,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/023_Ekans.png"));
		    loadPokemonIfNotExists(transaction, 23,"Ekans",1,8,0,2,6.9,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/025_Pikachu.png"));
		    loadPokemonIfNotExists(transaction, 25,"Pikachu",1,4,0,0.4,6,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/035_Clefairy.png"));
		    loadPokemonIfNotExists(transaction, 35,"Clefairy",1,18,0,0.6,7.5,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/050_Diglett.png"));
		    loadPokemonIfNotExists(transaction, 50,"Diglett",1,9,0,0.2,0.8,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/063_Abra.png"));
		    loadPokemonIfNotExists(transaction, 63,"Abra",1,11,0,0.9,19.5,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/066_Machop.png"));
		    loadPokemonIfNotExists(transaction, 66,"Machop",1,7,0,0.8,19.5,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/074_Geodude.png"));
		    loadPokemonIfNotExists(transaction, 74,"Geodude",1,13,9,0.4,20,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/092_Gastly.png"));
		    loadPokemonIfNotExists(transaction, 92,"Gastly",1,14,8,1.3,0.1,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/147_Dratiny.png"));
		    loadPokemonIfNotExists(transaction, 147,"Dratiny",1,16,0,1.8,3.3,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/198_Murkrow.png"));
		    loadPokemonIfNotExists(transaction, 198,"Murkrow",2,17,10,0.5,2.1,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/215_Sneasel.png"));
		    loadPokemonIfNotExists(transaction, 215,"Sneasel",2,17,6,0.9,28,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/216_Teddiursa.png"));
		    loadPokemonIfNotExists(transaction, 216,"Teddiursa",2,1,0,0.6,8.8,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/227_Scarmory.png"));
		    loadPokemonIfNotExists(transaction, 227,"Scarmory",2,18,10,1.7,50.5,image);
	      }
		catch (TransactionException e) {
	    	  throw e;
	      } 
		catch (IOException e) {
		      throw e;
	      }
		
	}

	private void loadPokemonIfNotExists(DistributedTransaction transaction, int id, String name,int generation,int type1,int type2,double height,double weight, byte[] image) throws TransactionException {
		Optional<Result> type = transaction.get(
			   	Get.newBuilder()
				.namespace("pokedex")
				.table("pokemon")
				.partitionKey(Key.ofInt("pokemon_id", id))
				.clusteringKey(Key.of("type1", type1,"type2", type2))
				.build());
			if (!type.isPresent()) {
				transaction.put(
					Put.newBuilder()
					.namespace("pokedex")
					.table("pokemon")
					.partitionKey(Key.ofInt("pokemon_id", id))
					.clusteringKey(Key.of("type1", type1,"type2", type2))
					.textValue("name", name)
					.intValue("generation", generation)
					.doubleValue("height", height)
					.doubleValue("weight", weight)
					.blobValue("image", image)
					.build());
			}
		
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
		
	@Override
	public void close() throws Exception {
	    manager.close();
	}
}
