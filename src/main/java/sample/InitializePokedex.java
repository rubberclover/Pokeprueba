package sample;

import java.util.concurrent.Callable;

public class InitializePokedex implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    try (Pokedex pokedex = new Pokedex()) {
    	pokedex.loadInitialData();
    	System.out.println("aaaa");
    }
    return 0;
  }
}