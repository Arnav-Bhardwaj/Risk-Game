
package com.java.model.player;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import com.java.model.gamedata.GameData;
import com.java.model.map.GameMap;

/**
 * This class tests the random strategy behavior.
 * 
 * Reinforces random country. Attack random country random number of
 * times. Fortify random country.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 3.0.0
 *
 */
public class RandomModeTest {

	private static GameData gameData;
	private static Player playerOne, playerTwo;
	private static ArrayList<Player> players;
	private static Random random;

	/**
	 * Sets up the scenario for the tests. Creates a map, two Random players
	 */
	@BeforeClass
	static public void setUp() {

		random = new Random(42);

		// Load the game with a dummy map and dummy data.
		gameData = new GameData();
		gameData.gameMap = new GameMap();
		// Test data with two players, two continents and six countries.
		players = new ArrayList<Player>();
		playerOne = new Player();
		playerOne.setStrategyType(new RandomMode(1, "P1", 42));
		playerTwo = new Player();
		playerTwo.setStrategyType(new RandomMode(2, "P2", 42));

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
	public void testCalculateReinforcement() {

		int actual_value = playerTwo.getStrategyType().calculateReinforcementArmy();
		int expected_value = 3;
		assertEquals(expected_value, actual_value);
	}

	/**
	 * Tests the received number of armies from reinforcement.
	 */
	@Test
	public void testCalculateReinforcementWithConqueredContinent() {

		int actual_value = playerOne.getStrategyType().calculateReinforcementArmy();
		int expected_value = 8;
		assertEquals(expected_value, actual_value);
	}

	/**
	 * Tests the received number of armies from reinforcement.
	 */
	@Test
	public void executeReinforcement() {

		Integer reinforcementArmy = playerOne.getStrategyType().calculateReinforcementArmy();

		HashSet<String> countriesOwned = playerOne.getStrategyType().gameData.gameMap
				.getConqueredCountries(playerOne.getStrategyType().getPlayerID());

		ArrayList<String> countriesOwnedList = new ArrayList<>(countriesOwned);

		HashMap<String, Integer> expectedArmyCountPerCountryAfterReinforcement = new HashMap<>();

		for (String country : countriesOwnedList) {
			expectedArmyCountPerCountryAfterReinforcement.put(country,
					playerOne.getStrategyType().gameData.gameMap.getCountry(country).getCountryArmyCount());
		}

		while (reinforcementArmy > 0) {

			Integer randomReinforcementCount = 0;
			Integer randomCountryIndex = random.nextInt(countriesOwnedList.size());

			String randomCountry = countriesOwnedList.get(randomCountryIndex);

			while (randomReinforcementCount == 0) {
				randomReinforcementCount = random.nextInt(reinforcementArmy + 1);
			}

			Integer randomCountryArmyCountBeforeFortification = expectedArmyCountPerCountryAfterReinforcement
					.get(randomCountry);

			Integer randomCountryArmyCountAfterFortification = randomCountryArmyCountBeforeFortification
					+ randomReinforcementCount;

			expectedArmyCountPerCountryAfterReinforcement.put(randomCountry, randomCountryArmyCountAfterFortification);

			reinforcementArmy -= randomReinforcementCount;

		}

		playerOne.getStrategyType().executeReinforcement();

		HashMap<String, Integer> actualArmyCountPerCountryAfterReinforcement = new HashMap<>();

		for (String country : countriesOwnedList) {
			actualArmyCountPerCountryAfterReinforcement.put(country,
					playerOne.getStrategyType().gameData.gameMap.getCountry(country).getCountryArmyCount());
		}

		assertEquals(actualArmyCountPerCountryAfterReinforcement, expectedArmyCountPerCountryAfterReinforcement);
		// assertTrue(strongerCountryAfterReinforcement >
		// strongerCountryBeforeReinforcement);

	}

	/**
	 * Test Random fortification logic, fortifies random country
	 */
	@Test
	public void executeFortification() {

		HashMap<String, ArrayList<String>> fortificationScenarios = playerOne.getStrategyType()
				.getFortificationScenarios();

		HashMap<String, Integer> armiesPerPotentialFortificationSourceCountry = new HashMap<String, Integer>();

		// Print all the options out for the player to see and choose from
		for (String keySourceCountry : fortificationScenarios.keySet()) {
			armiesPerPotentialFortificationSourceCountry.put(keySourceCountry,
					playerOne.getStrategyType().gameData.gameMap.getCountry(keySourceCountry).getCountryArmyCount());
		}

		ArrayList<String> sourceCountries = new ArrayList<String>(fortificationScenarios.keySet());
		String randomSourceCountry = sourceCountries.get(random.nextInt(sourceCountries.size()));

		ArrayList<String> correspondingDestinationCountries = fortificationScenarios.get(randomSourceCountry);
		String randomCorrespondingDestinationCountry = correspondingDestinationCountries
				.get(random.nextInt(correspondingDestinationCountries.size()));

		Integer possibleNumOfArmyRange = armiesPerPotentialFortificationSourceCountry.get(randomSourceCountry) - 1;
		Integer randomNumberOfArmiesToMove = 0;

		while (randomNumberOfArmiesToMove == 0) {
			randomNumberOfArmiesToMove = random.nextInt(possibleNumOfArmyRange + 1);
		}

		Integer sourceCountryArmyCountBeforeFortification = playerOne.getStrategyType().gameData.gameMap
				.getCountry(randomSourceCountry).getCountryArmyCount();
		Integer correspondingDestinationCountryArmyCountBeforeFortification = playerOne
				.getStrategyType().gameData.gameMap.getCountry(randomCorrespondingDestinationCountry)
						.getCountryArmyCount();

		Integer sourceCountryArmyCountAfterFortification = sourceCountryArmyCountBeforeFortification
				- randomNumberOfArmiesToMove;

		Integer correspondingDestinationCountryArmyCountAfterFortification = correspondingDestinationCountryArmyCountBeforeFortification
				+ randomNumberOfArmiesToMove;

		playerOne.getStrategyType().executeFortification();

		assertEquals(sourceCountryArmyCountAfterFortification,
				playerOne.getStrategyType().gameData.gameMap.getCountry(randomSourceCountry).getCountryArmyCount());
		assertEquals(correspondingDestinationCountryArmyCountAfterFortification,
				playerOne.getStrategyType().gameData.gameMap.getCountry(randomCorrespondingDestinationCountry)
						.getCountryArmyCount());

	}
}