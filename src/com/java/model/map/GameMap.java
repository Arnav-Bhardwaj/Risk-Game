package com.java.model.map;

import com.java.model.Observable;
import com.java.model.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class models the game map
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez 
 * @version 2.0.0
 * */
public class GameMap extends Observable implements Cloneable, Serializable {

	public static final Integer DEFAULT_NUMBER_OF_COUNTRIES = 42;
	private HashMap<String, Country> countryObjects;
	private HashMap<String, Continent> continentObjects;

	private HashMap<String, HashSet<String>> adjacentCountries;
	private HashMap<String, HashSet<String>> continentCountries;

	private HashMap<Integer, HashSet<String>> conqueredCountriesPerPlayer;
	private HashMap<Integer, HashSet<String>> conqueredContinentsPerPlayer;

	private String mapAuthor;
	public  String warn;

	private HashMap<String,Double> ownershipPercentage;
	private HashMap<String,Integer> numberOfArmiesPerPlayer;
	private ArrayList<Player> playersInfo;

	/**
	 * Creates a default map by created instances of every map.
	 * */
	public GameMap() {
		mapAuthor = "";
		warn = "";
		countryObjects = new HashMap<>();
		adjacentCountries = new HashMap<>();
		continentObjects = new HashMap<>();
		continentCountries = new HashMap<>();
		conqueredCountriesPerPlayer = new HashMap<>();
		conqueredContinentsPerPlayer = new HashMap<>();
	}
	
	/**
	 * Clones a game creating new objects.
	 * 
	 */
	@Override
    public GameMap clone() {
		GameMap gameMap = null;
		try {
			gameMap = (GameMap) super.clone();
			gameMap.countryObjects = new HashMap<>(countryObjects);
			gameMap.continentObjects = new HashMap<>(continentObjects);
			
			HashMap<String, HashSet<String>> newAdjacentCountriesObject = new HashMap<>();
			for(String countryName : this.adjacentCountries.keySet()) {
				newAdjacentCountriesObject.put(countryName, new HashSet<>(adjacentCountries.get(countryName)));
			}
			gameMap.adjacentCountries = newAdjacentCountriesObject;
			
			HashMap<String, HashSet<String>> newContinentCountries = new HashMap<>();
			for(String continentName : this.continentCountries.keySet()) {
				newContinentCountries.put(continentName, new HashSet<>(continentCountries.get(continentName)));
			}
			gameMap.continentCountries = newContinentCountries;
			
		} catch (CloneNotSupportedException e) {
			System.out.println("Gamemap Cloning error");
		}
 
        return gameMap;        // return deep copy
    }
	
	/**
	 * Adds the country to the map.
	 * 
	 * @param countryName the name of the country to be add.
	 * @param countryContinentName the continent name.
	 */
	public void addCountry(String countryName, String countryContinentName) {
		Country country = new Country(countryName, countryContinentName);
		this.countryObjects.put(country.getCountryName(), country);
		if(!this.continentCountries.containsKey(country.getCountryContinentName())) {
			this.continentCountries.put(country.getCountryContinentName(), new HashSet<>());
		}
		this.continentCountries.get(country.getCountryContinentName()).add(country.getCountryName());
	}

	/**
	 * Removes country from the map. Only to be used by MapEditor, before Startup Phase.
	 * 
	 * @param countryName name of the country to be removed
	 */
	public void removeCountry(String countryName) {
		Country country = this.countryObjects.get(countryName);

		/* Removes country name from adjacentCountries */
		this.adjacentCountries.remove(countryName);
		for(String fromCountryKey : adjacentCountries.keySet()) {
			if(this.adjacentCountries.get(fromCountryKey).contains(countryName)) {
				this.adjacentCountries.get(fromCountryKey).remove(countryName);
			}
		}

		/* Removes country name from continentCountries */
		this.continentCountries.get(country.getCountryContinentName()).remove(countryName);

		/* Removes country object from countryObjects */
		this.countryObjects.remove(countryName);
	}
	
	/**
	 * Getter the number of countries.
	 * 
	 * @return the number of countries in the map.
	 * */
	public Integer getNumberOfCountries() {
		return countryObjects.size();
	}
	
	/**
	 * Gets alls countries.
	 * 
	 * @return a hash map with country name as a key and the country object as a value.
	 * */
	public HashMap<String, Country> getAllCountries(){
		return this.countryObjects;
	}

	/**
	 * Gets the country object by giving the country name.
	 * 
	 * @param countryName country name
	 * @return the country object.
	 * */
	public Country getCountry(String countryName) {
		if (!this.countryObjects.containsKey(countryName)) {
			return null;
		}
		return this.countryObjects.get(countryName);
	}
	
	/**
	 * Adds one continent to the collections of continents.
	 * 
	 * @param continentName the continent name.
	 * @param controlValue the number of armies that are going to given when a player owns a continent.
	 * */
	public void addContinent(String continentName, Integer controlValue) {
		Continent continent = new Continent(continentName, controlValue);
		this.continentObjects.put(continent.getContinentName(), continent);
	}

	/**
	 * Removes a continent from the collection of continents
	 * 
	 * @param continentName the continent name.
	 * */
	public void removeContinent(String continentName) {
		this.continentObjects.remove(continentName);
		Object[] continentCountriesSet = getContinentCountries(continentName).toArray();
		for(Object continentCountry : continentCountriesSet) {
			removeCountry(continentCountry.toString());
		}
		continentCountries.remove(continentName);
	}
	
	/**
	 * Getter of all continents.
	 * 
	 * @return the map of continents.
	 * */
	public HashMap<String, Continent> getAllContinents(){
		return this.continentObjects;
	}
	
	/**
	 * Returns the Continent object by giving the continent name.
	 * 
	 * @param continentName the continent name.
	 * @return the continent object.
	 * */
	public Continent getContinent(String continentName) {
		if(!this.continentObjects.containsKey(continentName)) {
			return null;
		}
		return this.continentObjects.get(continentName);
	}
	
	/**
	 * Sets an adjacency between countries in both ways.
	 * 
	 * @param countryName country name of the endpoint.
	 * @param adjacentCountryName contry name of the endpoint.
	 * */
	public void setAdjacentCountry(String countryName, String adjacentCountryName) {
		setAdjacency(countryName, adjacentCountryName);
		setAdjacency(adjacentCountryName, countryName);
	}
	
	/**
	 * Sets an adjacency between countries.
	 * 
	 * @param fromCountry country name of the endpoint.
	 * @param toCountry contry name of the endpoint.
	 * */
	private void setAdjacency(String fromCountry, String toCountry) {
		if (!this.adjacentCountries.containsKey(fromCountry)) {
			this.adjacentCountries.put(fromCountry, new HashSet<>());
		}
		this.adjacentCountries.get(fromCountry).add(toCountry);
	}
	
	/**
	 * Gets adjacents countries.
	 * 
	 * @param countryName country name
	 * @return the adjacent countries
	 * 
	 * */
	public HashSet<String> getAdjacentCountries(String countryName) {
		if(!this.adjacentCountries.containsKey(countryName)) {
			return new HashSet<>();
		}
		return this.adjacentCountries.get(countryName);
	}
	
	/**
	 * Gets the map adjacents countries.
	 * 
	 * @return the map of the adjacent contries.
	 * */
	public HashMap<String, HashSet<String>> getAdjacentCountriesObject() {
		return adjacentCountries;
	}
	
	/**
	 * Removes an adjacency between countries.
	 * 
	 * @param countryName the country name.
	 * @param adjacentCountryName the adjacent country name.
	 * @return true if countries are adjacent.
	 * */
	public Boolean removeAdjacenyBetweenCountries(String countryName, String adjacentCountryName) {
		return (removeAdjacency(countryName, adjacentCountryName) && removeAdjacency(adjacentCountryName, countryName));
	}
	
	/**
	 * Removes an adjacency between countries.
	 * 
	 * @param countryName the country name.
	 * @param adjacentCountryName the adjacent country name.
	 * @return true if the adjacency is removed.
	 * */
	private Boolean removeAdjacency(String countryName, String adjacentCountryName) {
		if (this.adjacentCountries.containsKey(countryName)
				&& this.adjacentCountries.get(countryName).contains(adjacentCountryName)) {
			this.adjacentCountries.get(countryName).remove(adjacentCountryName);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the countries that belong to a continent.
	 * 
	 * @param continentName the continent name.
	 * @return set of countries.
	 * */
	public HashSet<String> getContinentCountries(String continentName){
		if(!this.continentCountries.containsKey(continentName)) {
			return new HashSet<String>();
		}
		return this.continentCountries.get(continentName);
	}

	/**
	 * Sets the country conqueror.
	 * 
	 * @param countryName the country name.
	 * @param playerId the player id.
	 * */
	public void setCountryConquerer(String countryName, Integer playerId) {

		if(!this.conqueredCountriesPerPlayer.containsKey(playerId)) {
			this.conqueredCountriesPerPlayer.put(playerId, new HashSet<>());
		}
		this.conqueredCountriesPerPlayer.get(playerId).add(countryName);

		Country country= this.getCountry(countryName);
		country.setConquerorID(playerId);
		
		// continent domiancy check
		String countryContinent = country.getCountryContinentName();
		if (this.conqueredCountriesPerPlayer.get(playerId).containsAll(this.continentCountries.get(countryContinent))) {
			this.setContinentConquerer(countryContinent, playerId);
		}
		
		calculateOwnershipPercentage();
	}
	
	/**
	 * Sets the continent conqueror. Only to be used in Start-up Phase.
	 * 
	 * @param continentName the continent name.
	 * @param playerId the player id.
	 * */
	public void setContinentConquerer(String continentName, Integer playerId) {
		if(!this.conqueredContinentsPerPlayer.containsKey(playerId)) {
			this.conqueredContinentsPerPlayer.put(playerId, new HashSet<>());
		}
		
		for (Integer player : this.conqueredContinentsPerPlayer.keySet()) {
			if (this.conqueredContinentsPerPlayer.get(player).contains(continentName)) {
				this.conqueredContinentsPerPlayer.get(player).remove(continentName);
				break;
			}
		}
		this.conqueredContinentsPerPlayer.get(playerId).add(continentName);
		Continent continent = this.getContinent(continentName);
		continent.setContinentConquerorID(playerId);
		notifyView();
	}
	
	/**
	 * Gets the conquered countries per player.
	 * 
	 * @param playerId the player id.
	 * @return the set of countries owned by a player.
	 * */
	public  HashSet<String> getConqueredCountries(Integer playerId){
		if(!this.conqueredCountriesPerPlayer.containsKey(playerId)) {
			return new HashSet<>();
		}
		return this.conqueredCountriesPerPlayer.get(playerId);
	}
	
	/**
	 * Gets the conquered continents per player.
	 * 
	 * @param playerId the player id.
	 * @return the set of continents owned by a player.
	 * */
	public  HashSet<String> getConqueredContinentsPerPlayer(Integer playerId){
		if(!this.conqueredContinentsPerPlayer.containsKey(playerId)) {
			return new HashSet<>();
		}
		return this.conqueredContinentsPerPlayer.get(playerId);
	}

	/**
	 * Updates the country conqueror.
	 * 
	 * @param countryName the country name.
	 * @param oldConquererPlayerId the old conqueror id.
	 * @param newConquererPlayerId the new coqueror id.
	 * */
	public void updateCountryConquerer(String countryName, Integer oldConquererPlayerId, Integer newConquererPlayerId) {
		if(this.conqueredCountriesPerPlayer.get(oldConquererPlayerId).contains(countryName)) {
			this.conqueredCountriesPerPlayer.get(oldConquererPlayerId).remove(countryName);
		}
		this.conqueredCountriesPerPlayer.get(newConquererPlayerId).add(countryName);

		Country country= this.getCountry(countryName);
		country.setConquerorID(newConquererPlayerId);
		
		//continent domiancy check
		String countryContinent = country.getCountryContinentName();
		if(this.conqueredCountriesPerPlayer.get(newConquererPlayerId).containsAll(this.continentCountries.get(countryContinent))) {
			this.setContinentConquerer(countryContinent, newConquererPlayerId);
		}
		calculateOwnershipPercentage();
	}
	
	/**
	 * Sets the map author.
	 * 
	 * @param mapAuthor the author map.
	 * */
	public void setMapAuthor(String mapAuthor) {
		this.mapAuthor = mapAuthor;
	}
	
	/**
	 * Gets the author map name.
	 * 
	 * @return the authors name.
	 * */
	public String getMapAuthor() {
		return this.mapAuthor;
	}
	
	/**
	 * Just prints the map for development purposes.
	 */
	public void printMap() {
		System.out.println("Author: " +  getMapAuthor());

		System.out.println("[[Countries]]");
		for(String countryName: countryObjects.keySet()) {
			System.out.println(countryName);
			System.out.println("Adjacent Countries::" + getAdjacentCountries(countryName).toString());
		}
		
		System.out.println("[[Continent]]");
		for(String continentName: continentObjects.keySet()) {
			System.out.println(continentName + " :: " + getContinentCountries(continentName).toString());
		}
	}
	
	/**
    * Adds army to a country
    * 
    * @param country country to add armies.
    * @param armyCount number of armies to add.
    */
	public void addArmyToCountry(String country, Integer armyCount) {
		
		Country countryObject = countryObjects.get(country);
		countryObject.addArmy(armyCount);
		countryObjects.put(country, countryObject);
		
		calculateNumberOfArmiesPerPlayer();
		notifyView();
	}
	
	/**
	 * Deduct army to a country
	 * 
	 * @param country country to deduct armies.
	 * @param armyCount number of armies to add.
	 */
	public void deductArmyToCountry(String country, Integer armyCount) {
		
		Country countryObject = countryObjects.get(country);
		countryObject.deductArmy(armyCount);
		countryObjects.put(country, countryObject);
		
		calculateNumberOfArmiesPerPlayer();
		notifyView();
	}
	
	/**
    *Calculates the number of armies per player.
    *
    */
	public void calculateNumberOfArmiesPerPlayer() {
		
		HashMap<String, Country> countries = getAllCountries();
		HashSet<String> countriesPerPlayer;
		numberOfArmiesPerPlayer = new HashMap<String,Integer>();
		Integer numberOfArmies;
		Integer armyCount;
		String key;
		Integer value;
		
		for(Player player : playersInfo) {
			
			numberOfArmies = 0;
			countriesPerPlayer = getConqueredCountries(player.getStrategyType().getPlayerID());
			
			for(String countryName : countriesPerPlayer) {
				
				armyCount = countries.get(countryName).getCountryArmyCount();
				numberOfArmies += armyCount;
				
			}
			
			key = player.getStrategyType().getPlayerID().toString();
			value = numberOfArmies;
			
			numberOfArmiesPerPlayer.put(key, value);
		}			
	}

    /**
     * Calculates the country ownership percentage of the map.
     */
    public void calculateOwnershipPercentage(){
    	ownershipPercentage = new HashMap<String,Double>();

        Integer totalNumberOfCountries = getNumberOfCountries();
        Integer counteriesOwnedPlayer = 0;
        Double percentageOfOwnership = 0.0;

        for(Player eachPlayer : playersInfo){
           HashSet<String> countiresOwned= conqueredCountriesPerPlayer.get(eachPlayer.getStrategyType().getPlayerID());
           
           if(countiresOwned == null) {
        	   counteriesOwnedPlayer = 0;
           } else {
        	   counteriesOwnedPlayer = countiresOwned.size();
           }

           // now calculate the % of ownership
            percentageOfOwnership =  ((double)counteriesOwnedPlayer/totalNumberOfCountries) * 100.0;

            //put in map
            getOwnershipPercentage().put(eachPlayer.getStrategyType().getPlayerID().toString(), percentageOfOwnership);
        }
        
        notifyView();
    }
    
    /**
     * Sets the player's information.
     * @param players array with players.
     */
    public void setupPlayerNames(ArrayList<Player> players){
        playersInfo = new ArrayList<Player>(players); // obtain players
    }
    
    /**
     * Gets the array with the player information.
     * 
     * @return the array with player information.
     */
    public ArrayList<Player> getPlayersInfo(){
    	return this.playersInfo;
    }
    

	/**
	 * Getter of Conquered countries per player object
	 * 
	 * @return the map of conquered countries per player.
	 * */
	public HashMap<Integer, HashSet<String>> getConqueredCountriesPerPlayerObject() {
		return conqueredCountriesPerPlayer;
	}
	
	/**
	 * Setter of conquered continents per player
	 * 
	 * @param conqueredContinentsPerPlayer the map of conquered continents per player.
	 * */
	public void setConqueredContinentsPerPlayer(HashMap<Integer, HashSet<String>> conqueredContinentsPerPlayer) {
		this.conqueredContinentsPerPlayer = conqueredContinentsPerPlayer;
	}
	
	/**
	 * Sets the country object map.
	 * 
	 * @param countryObjects the country object map. 
	 * */
	public void setCountryObjects(HashMap<String, Country> countryObjects) {
		this.countryObjects = countryObjects;
	}

	public HashMap<String, Continent> getContinentObjects() {
		return continentObjects;
	}

	/**
	 * Sets the continent object map.
	 * 
	 * @param continentObjects the continent object map. 
	 * */
	public void setContinentObjects(HashMap<String, Continent> continentObjects) {
		this.continentObjects = continentObjects;
	}
	
	/**
	 * Gets the adjacents countries.
	 * 
	 * @return map of adjacent countries. 
	 * */
	public HashMap<String, HashSet<String>> getAdjacentCountries() {
		return adjacentCountries;
	}
	
	/**
	 * Sets the adjacent countries map.
	 * 
	 * @param adjacentCountries the adjacent countries map. 
	 * */
	public void setAdjacentCountries(HashMap<String, HashSet<String>> adjacentCountries) {
		this.adjacentCountries = adjacentCountries;
	}
	
	/**
	 * Gets the continents countries.
	 * 
	 * @return map of the continent countries. 
	 * */
	public HashMap<String, HashSet<String>> getContinentCountries() {
		return continentCountries;
	}
	
	/**
	 * Sets the continent countries map.
	 * 
	 * @param continentCountries the continent countries map. 
	 * */
	public void setContinentCountries(HashMap<String, HashSet<String>> continentCountries) {
		this.continentCountries = continentCountries;
	}
	
	/**
	 * Gets the conquered continents per player.
	 * 
	 * @return map of the conquered continents per player. 
	 * */
	public HashMap<Integer, HashSet<String>> getConqueredContinentsPerPlayer() {
		return this.conqueredContinentsPerPlayer;
	}
	
	/**
	 * Gets the hash map which holds the country ownership percentage.
	 * 
	 * @return the hash map with country ownership percentage.
	 */
	public HashMap<String,Double> getOwnershipPercentage() {
		return this.ownershipPercentage;
	}
	
	/**
	 * Gets the hash map which hold the number of armies per player.
	 * 
	 * @return the hash map with the number of armies per player with player id as a key.
	 */
	public HashMap<String,Integer> getNumberOfArmiesPerPlayer(){
		return this.numberOfArmiesPerPlayer;
	}
}
