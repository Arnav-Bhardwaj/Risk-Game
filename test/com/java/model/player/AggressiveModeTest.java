
package com.java.model.player;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;
import com.java.model.cards.CardsDeck;
import com.java.model.gamedata.GameData;
import com.java.model.map.Country;
import com.java.model.map.GameMap;

/**
 * This class tests the aggressive strategy behavior.
 * 
 * reinforce - reinforces its strongest country.
 * attack - attacks from its strongest country whatever it can.
 * fortify - fortifies its strongest country or nothing.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 3.0.0
 *
 */
public class AggressiveModeTest {
	
	private static GameData gameData;
    private static Player playerOne,playerTwo;
    private static ArrayList<Player> players;
        
	/**
	 * Sets up the scenario for the tests. Creates a map, two players, a human and an aggressive.
	 */
	@BeforeClass
	static public void setUp() {
		// Load the game with a dummy map and dummy data.
		gameData = new GameData();
		gameData.gameMap = new GameMap();
		// Test data with two players, two continents and six countries.
		players = new ArrayList<Player>();
		playerOne = new Player();
		playerOne.setStrategyType(new HumanMode(1, "P1"));
		playerTwo = new Player();
		playerTwo.setStrategyType(new AggresiveMode(2, "P2"));

		players.add(playerOne);
		players.add(playerTwo);

		gameData.gameMap.setupPlayerNames(players);

		gameData.gameMap.addContinent("Continent1", 5);
		gameData.gameMap.addContinent("Continent2", 6);
		gameData.gameMap.addCountry("C1", "Continent1");
		gameData.gameMap.addCountry("C2", "Continent1");
		gameData.gameMap.addCountry("C3", "Continent1");
		gameData.gameMap.addCountry("C4", "Continent2");
		gameData.gameMap.addCountry("C5", "Continent2");
		gameData.gameMap.addCountry("C6", "Continent2");

		// set adjacency as it's a pillar requirements for fortification functionality
		gameData.gameMap.setAdjacentCountry("C1", "C3");
		gameData.gameMap.setAdjacentCountry("C1", "C4");
		gameData.gameMap.setAdjacentCountry("C2", "C4");
		gameData.gameMap.setAdjacentCountry("C3", "C5");
		gameData.gameMap.setAdjacentCountry("C4", "C5");
		gameData.gameMap.setAdjacentCountry("C4", "C6");
		gameData.gameMap.setCountryConquerer("C1", 1);
		gameData.gameMap.setCountryConquerer("C2", 1);
		gameData.gameMap.setCountryConquerer("C3", 1);
		gameData.gameMap.setCountryConquerer("C4", 1);
		gameData.gameMap.setCountryConquerer("C5", 2);
		gameData.gameMap.setCountryConquerer("C6", 2);

		gameData.gameMap.addArmyToCountry("C1", 1);
		gameData.gameMap.addArmyToCountry("C2", 3);
		gameData.gameMap.addArmyToCountry("C3", 5);
		gameData.gameMap.addArmyToCountry("C4", 1);
		gameData.gameMap.addArmyToCountry("C5", 7);
		gameData.gameMap.addArmyToCountry("C6", 9);

		playerOne.getStrategyType().setGameData(gameData);
		playerTwo.getStrategyType().setGameData(gameData);

		gameData.gameMap.setupPlayerNames(players);
		gameData.setPlayers(players);
	}

	/**
	 * Tests the received number of armies from reinforcement.
	 */
	@Test
	public void executeReinforcement() {

		// C5 only has 7 armies, whereas C6 has got 9
		Country weakerCountry = gameData.gameMap.getCountry("C5");;
		Country strongerCountry = gameData.gameMap.getCountry("C6");
		Integer weakerCountryBeforeReinforcement = weakerCountry.getCountryArmyCount();
		Integer strongerCountryBeforeReinforcement = strongerCountry.getCountryArmyCount();


		playerTwo.getStrategyType().executeReinforcement();

		weakerCountry = gameData.gameMap.getCountry("C5");;
		strongerCountry = gameData.gameMap.getCountry("C6");
		Integer weakerCountryAfterReinforcement = weakerCountry.getCountryArmyCount();
		Integer strongerCountryAfterReinforcement = strongerCountry.getCountryArmyCount();

		assertEquals(weakerCountryBeforeReinforcement, weakerCountryAfterReinforcement);
		//assertTrue(strongerCountryAfterReinforcement > strongerCountryBeforeReinforcement);

	}

	/**
	 * Test aggressive attack logic attacks from its strongest country.
	 */
	@Test
	public void executeAttack() {
		HashSet<String> initialOwnedCountries = gameData.gameMap.getConqueredCountries(2);
		int initialOwnedCountriesNumber = initialOwnedCountries.size();

		Country weakerCountry = gameData.gameMap.getCountry("C5");;
		Country strongerCountry = gameData.gameMap.getCountry("C6");
		Integer weakerCountryBeforeAttack = weakerCountry.getCountryArmyCount();
		Integer strongerCountryBeforeAttack = strongerCountry.getCountryArmyCount();

		ArrayList<Country> countryList = new ArrayList<Country>();

		countryList.add(gameData.gameMap.getCountry("C1"));
		countryList.add(gameData.gameMap.getCountry("C2"));
		countryList.add(gameData.gameMap.getCountry("C3"));
		countryList.add(gameData.gameMap.getCountry("C4"));
		countryList.add(gameData.gameMap.getCountry("C5"));
		countryList.add(gameData.gameMap.getCountry("C6"));

		gameData.cardsDeck = new CardsDeck(countryList);
		
		playerTwo.getStrategyType().executeAttack();
		
		weakerCountry = gameData.gameMap.getCountry("C5");;
		strongerCountry = gameData.gameMap.getCountry("C6");
		Integer weakerCountryAfterAttack = weakerCountry.getCountryArmyCount();
		Integer strongerCountryAfterAttack = strongerCountry.getCountryArmyCount();

		// confirm that army counts for C5 simply don't change because we're not attacking with it
		assertEquals(weakerCountryBeforeAttack, weakerCountryAfterAttack);
		
		// also confirm that army counts for C6 simply change because we attacked C4 with it
		assertTrue(strongerCountryBeforeAttack > strongerCountryAfterAttack);
		
		// now confirm that player 2 owns 1 more country than it did before (3 in total)
		int afterOwnedCountriesNumber = gameData.gameMap.getConqueredCountries(2).size();

		Integer expectedValue = initialOwnedCountriesNumber + 1;
		Integer actualValue = afterOwnedCountriesNumber;

		assertEquals(actualValue, expectedValue);
	}

	/**
	 * Test aggressive fortification logic, fortifies its strongest country
	 * based on max that the second strongest country in the "fortification path" can provide.
	 */
	@Test
	public void executeFortification() {

		// to make things more interesting add a seventh country
		// with second highest overall army count but highest "fortifiable" option
		gameData.gameMap.addCountry("C7", "Continent2");
		gameData.gameMap.setAdjacentCountry("C7", "C5");
		gameData.gameMap.setCountryConquerer("C7", 2);

		gameData.gameMap.addArmyToCountry("C7", 8);

		Country supplierCountry  = gameData.gameMap.getCountry("C5");
		Country strongestCountry = gameData.gameMap.getCountry("C7");

		Integer beforeFortifySupplierArmyCount  = supplierCountry.getCountryArmyCount();
		Integer beforeFortifyStrongestArmyCount = strongestCountry.getCountryArmyCount();

		playerTwo.getStrategyType().executeFortification();

		// supplier gives strongest country its all
		Integer expectedValueSup = 1;
		Integer actualValueSup = supplierCountry.getCountryArmyCount();

		Integer expectedValueStrongest = beforeFortifyStrongestArmyCount + beforeFortifySupplierArmyCount - 1;
		Integer actualValueStrongest = strongestCountry.getCountryArmyCount();

		assertEquals(expectedValueSup, actualValueSup);
		assertEquals(expectedValueStrongest, actualValueStrongest);
	}
}