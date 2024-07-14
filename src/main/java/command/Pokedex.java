package command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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
		    loadPokemonIfNotExists(transaction, 147,"Dratiny",1,15,0,1.8,3.3,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/198_Murkrow.png"));
		    loadPokemonIfNotExists(transaction, 198,"Murkrow",2,16,10,0.5,2.1,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/215_Sneasel.png"));
		    loadPokemonIfNotExists(transaction, 215,"Sneasel",2,16,6,0.9,28,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/216_Teddiursa.png"));
		    loadPokemonIfNotExists(transaction, 216,"Teddiursa",2,1,0,0.6,8.8,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/227_Scarmory.png"));
		    loadPokemonIfNotExists(transaction, 227,"Scarmory",2,17,10,1.7,50.5,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/336_Seviper.png"));
		    loadPokemonIfNotExists(transaction, 336,"Seviper",3,8,0,2.7,52.5,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/436_Bronzor.png"));
		    loadPokemonIfNotExists(transaction, 436,"Bronzor",4,17,11,0.5,60.5,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/487_Giratina.png"));
		    loadPokemonIfNotExists(transaction, 487,"Giratina",4,14,15,6.9,650.0,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/551_Sandile.png"));
		    loadPokemonIfNotExists(transaction, 551,"Sandile",5,9,16,0.7,15.2,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/700_Sylveon.png"));
		    loadPokemonIfNotExists(transaction, 700,"Sylveon",6,18,0,1.0,23.5,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/768_Golisopod.png"));
		    loadPokemonIfNotExists(transaction, 768,"Golisopod",7,12,3,2.0,108.0,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/823_Corviknight.png"));
		    loadPokemonIfNotExists(transaction, 823,"Corviknight",8,10,17,2.2,75.0,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/928_Smoliv.png"));
		    loadPokemonIfNotExists(transaction, 928,"Smoliv",9,5,1,0.3,6.5,image);
		    image = Files.readAllBytes(Paths.get("images/pokemon/321_Wailord.png"));
		    loadPokemonIfNotExists(transaction, 321,"Wailord",3,3,0,14.5,398.0,image);
		    
		    
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
	    	  loadWeaknessIfNotExists(transaction, 2, typeNames.indexOf("Normal"), typeNames.indexOf("Ghost"), 0.0);
	    	  loadWeaknessIfNotExists(transaction, 3, typeNames.indexOf("Fire"), typeNames.indexOf("Ground"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 4, typeNames.indexOf("Fire"), typeNames.indexOf("Rock"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 5, typeNames.indexOf("Fire"), typeNames.indexOf("Water"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 6, typeNames.indexOf("Fire"), typeNames.indexOf("Bug"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 7, typeNames.indexOf("Fire"), typeNames.indexOf("Steel"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 8, typeNames.indexOf("Fire"), typeNames.indexOf("Fire"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 9, typeNames.indexOf("Fire"), typeNames.indexOf("Grass"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 10, typeNames.indexOf("Fire"), typeNames.indexOf("Ice"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 11, typeNames.indexOf("Fire"), typeNames.indexOf("Fairy"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 12, typeNames.indexOf("Water"), typeNames.indexOf("Grass"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 13, typeNames.indexOf("Water"), typeNames.indexOf("Electric"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 14, typeNames.indexOf("Water"), typeNames.indexOf("Steel"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 15, typeNames.indexOf("Water"), typeNames.indexOf("Fire"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 16, typeNames.indexOf("Water"), typeNames.indexOf("Water"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 17, typeNames.indexOf("Water"), typeNames.indexOf("Ice"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 18, typeNames.indexOf("Electric"), typeNames.indexOf("Ground"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 19, typeNames.indexOf("Electric"), typeNames.indexOf("Flying"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 20, typeNames.indexOf("Electric"), typeNames.indexOf("Electric"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 21, typeNames.indexOf("Electric"), typeNames.indexOf("Steel"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 22, typeNames.indexOf("Grass"), typeNames.indexOf("Flying"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 23, typeNames.indexOf("Grass"), typeNames.indexOf("Poison"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 24, typeNames.indexOf("Grass"), typeNames.indexOf("Bug"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 25, typeNames.indexOf("Grass"), typeNames.indexOf("Fire"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 26, typeNames.indexOf("Grass"), typeNames.indexOf("Ice"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 27, typeNames.indexOf("Grass"), typeNames.indexOf("Ground"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 28, typeNames.indexOf("Grass"), typeNames.indexOf("Water"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 29, typeNames.indexOf("Grass"), typeNames.indexOf("Grass"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 30, typeNames.indexOf("Grass"), typeNames.indexOf("Electric"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 31, typeNames.indexOf("Ice"), typeNames.indexOf("Fighting"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 32, typeNames.indexOf("Ice"), typeNames.indexOf("Rock"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 33, typeNames.indexOf("Ice"), typeNames.indexOf("Steel"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 34, typeNames.indexOf("Ice"), typeNames.indexOf("Fire"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 35, typeNames.indexOf("Ice"), typeNames.indexOf("Ice"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 36, typeNames.indexOf("Fighting"), typeNames.indexOf("Flying"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 37, typeNames.indexOf("Fighting"), typeNames.indexOf("Psychic"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 38, typeNames.indexOf("Fighting"), typeNames.indexOf("Fairy"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 39, typeNames.indexOf("Fighting"), typeNames.indexOf("Rock"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 40, typeNames.indexOf("Fighting"), typeNames.indexOf("Bug"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 41, typeNames.indexOf("Fighting"), typeNames.indexOf("Dark"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 42, typeNames.indexOf("Poison"), typeNames.indexOf("Ground"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 43, typeNames.indexOf("Poison"), typeNames.indexOf("Psychic"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 44, typeNames.indexOf("Poison"), typeNames.indexOf("Fighting"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 45, typeNames.indexOf("Poison"), typeNames.indexOf("Poison"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 46, typeNames.indexOf("Poison"), typeNames.indexOf("Bug"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 47, typeNames.indexOf("Poison"), typeNames.indexOf("Grass"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 48, typeNames.indexOf("Poison"), typeNames.indexOf("Fairy"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 49, typeNames.indexOf("Ground"), typeNames.indexOf("Water"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 50, typeNames.indexOf("Ground"), typeNames.indexOf("Grass"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 51, typeNames.indexOf("Ground"), typeNames.indexOf("Ice"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 52, typeNames.indexOf("Ground"), typeNames.indexOf("Poison"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 53, typeNames.indexOf("Ground"), typeNames.indexOf("Rock"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 54, typeNames.indexOf("Ground"), typeNames.indexOf("Electric"), 0.0);
	    	  loadWeaknessIfNotExists(transaction, 55, typeNames.indexOf("Flying"), typeNames.indexOf("Rock"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 56, typeNames.indexOf("Flying"), typeNames.indexOf("Electric"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 57, typeNames.indexOf("Flying"), typeNames.indexOf("Ice"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 58, typeNames.indexOf("Flying"), typeNames.indexOf("Fighting"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 59, typeNames.indexOf("Flying"), typeNames.indexOf("Bug"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 60, typeNames.indexOf("Flying"), typeNames.indexOf("Grass"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 61, typeNames.indexOf("Flying"), typeNames.indexOf("Ground"), 0.0);
	    	  loadWeaknessIfNotExists(transaction, 62, typeNames.indexOf("Psychic"), typeNames.indexOf("Bug"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 63, typeNames.indexOf("Psychic"), typeNames.indexOf("Ghost"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 64, typeNames.indexOf("Psychic"), typeNames.indexOf("Dark"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 65, typeNames.indexOf("Psychic"), typeNames.indexOf("Fighting"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 66, typeNames.indexOf("Psychic"), typeNames.indexOf("Psychic"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 67, typeNames.indexOf("Bug"), typeNames.indexOf("Flying"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 68, typeNames.indexOf("Bug"), typeNames.indexOf("Rock"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 69, typeNames.indexOf("Bug"), typeNames.indexOf("Fire"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 70, typeNames.indexOf("Bug"), typeNames.indexOf("Fighting"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 71, typeNames.indexOf("Bug"), typeNames.indexOf("Ground"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 72, typeNames.indexOf("Bug"), typeNames.indexOf("Grass"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 73, typeNames.indexOf("Rock"), typeNames.indexOf("Fighting"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 74, typeNames.indexOf("Rock"), typeNames.indexOf("Ground"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 75, typeNames.indexOf("Rock"), typeNames.indexOf("Steel"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 76, typeNames.indexOf("Rock"), typeNames.indexOf("Water"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 77, typeNames.indexOf("Rock"), typeNames.indexOf("Grass"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 78, typeNames.indexOf("Rock"), typeNames.indexOf("Normal"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 79, typeNames.indexOf("Rock"), typeNames.indexOf("Flying"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 80, typeNames.indexOf("Rock"), typeNames.indexOf("Poison"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 81, typeNames.indexOf("Rock"), typeNames.indexOf("Fire"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 82, typeNames.indexOf("Ghost"), typeNames.indexOf("Ghost"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 83, typeNames.indexOf("Ghost"), typeNames.indexOf("Dark"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 84, typeNames.indexOf("Ghost"), typeNames.indexOf("Poison"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 85, typeNames.indexOf("Ghost"), typeNames.indexOf("Bug"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 86, typeNames.indexOf("Ghost"), typeNames.indexOf("Normal"), 0.0);
	    	  loadWeaknessIfNotExists(transaction, 87, typeNames.indexOf("Ghost"), typeNames.indexOf("Fighting"), 0.0);
	    	  loadWeaknessIfNotExists(transaction, 88, typeNames.indexOf("Dragon"), typeNames.indexOf("Dragon"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 89, typeNames.indexOf("Dragon"), typeNames.indexOf("Ice"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 90, typeNames.indexOf("Dragon"), typeNames.indexOf("Fairy"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 91, typeNames.indexOf("Dragon"), typeNames.indexOf("Fire"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 92, typeNames.indexOf("Dragon"), typeNames.indexOf("Water"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 93, typeNames.indexOf("Dragon"), typeNames.indexOf("Grass"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 94, typeNames.indexOf("Dragon"), typeNames.indexOf("Electric"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 95, typeNames.indexOf("Steel"), typeNames.indexOf("Fighting"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 96, typeNames.indexOf("Steel"), typeNames.indexOf("Ground"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 97, typeNames.indexOf("Steel"), typeNames.indexOf("Fire"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 98, typeNames.indexOf("Steel"), typeNames.indexOf("Normal"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 99, typeNames.indexOf("Steel"), typeNames.indexOf("Flying"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 100, typeNames.indexOf("Steel"), typeNames.indexOf("Rock"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 101, typeNames.indexOf("Steel"), typeNames.indexOf("Bug"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 102, typeNames.indexOf("Steel"), typeNames.indexOf("Steel"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 103, typeNames.indexOf("Steel"), typeNames.indexOf("Grass"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 104, typeNames.indexOf("Steel"), typeNames.indexOf("Psychic"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 105, typeNames.indexOf("Steel"), typeNames.indexOf("Ice"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 106, typeNames.indexOf("Steel"), typeNames.indexOf("Dragon"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 107, typeNames.indexOf("Steel"), typeNames.indexOf("Fairy"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 108, typeNames.indexOf("Steel"), typeNames.indexOf("Poison"), 0.0);
	    	  loadWeaknessIfNotExists(transaction, 109, typeNames.indexOf("Dark"), typeNames.indexOf("Fighting"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 110, typeNames.indexOf("Dark"), typeNames.indexOf("Fairy"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 111, typeNames.indexOf("Dark"), typeNames.indexOf("Bug"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 112, typeNames.indexOf("Dark"), typeNames.indexOf("Psychic"), 0.0);
	    	  loadWeaknessIfNotExists(transaction, 113, typeNames.indexOf("Dark"), typeNames.indexOf("Ghost"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 114, typeNames.indexOf("Dark"), typeNames.indexOf("Dark"), 0.5);  
	    	  loadWeaknessIfNotExists(transaction, 115, typeNames.indexOf("Fairy"), typeNames.indexOf("Poison"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 116, typeNames.indexOf("Fairy"), typeNames.indexOf("Steel"), 2.0);
	    	  loadWeaknessIfNotExists(transaction, 117, typeNames.indexOf("Fairy"), typeNames.indexOf("Fighting"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 118, typeNames.indexOf("Fairy"), typeNames.indexOf("Bug"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 119, typeNames.indexOf("Fairy"), typeNames.indexOf("Dark"), 0.5);
	    	  loadWeaknessIfNotExists(transaction, 120, typeNames.indexOf("Fairy"), typeNames.indexOf("Dragon"), 0.0);  
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
	
	@Override
	public void close() throws Exception {
	    manager.close();
	}
}
