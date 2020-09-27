package com.java.model.player;

import com.java.model.cards.Card;
import com.java.model.cards.CardsDeck;
import com.java.model.gamedata.GameData;
import com.java.model.map.Country;
import com.java.model.map.GameMap;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * This class test the benevolent strategy behavior.
 * 
 * reinforce - its weaker country.
 * attack - never attacks.
 * fortify - weaker countries.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 3.0.0
 *
 */
public class BenevolentModeTest {
	
    private static GameData gameData;
    private static Player playerOne,playerTwo;
    private static ArrayList<Player> players;
    
    /**
	 * Sets up the scenario for the tests. Creates a map, two players, a human and a
	 * Benevolent.
	 */
    @BeforeClass
    static public void setUp() {
        // Load the game with a dummy map and dummy data.
        gameData = new GameData();
        gameData.gameMap = new GameMap();
        // Test data with two players, two continents and six countries.
        players = new ArrayList<>();
        playerOne   = new Player();
        playerOne.setStrategyType(new BenevolentMode(1, "P1"));
        playerTwo   = new Player();
        playerTwo.setStrategyType(new BenevolentMode(2, "P2"));

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
     * Verify there is no attack in benevolent mode. The number of armies
     * before and after calling the attack method should be the same.
     */
    @Test
    public void testAttack(){

        HashSet<String> conqueredCountryByPlayerOne = gameData.gameMap.getConqueredCountries(1);
        Integer armyCountBeforeAttack = 0;
        for(String country : conqueredCountryByPlayerOne){
            armyCountBeforeAttack += gameData.gameMap.getCountry(country).getCountryArmyCount();
        }
        // armyCountBeforeAttack is 10 now.
        playerOne.getStrategyType().executeAttack();
        HashSet<String> conqueredCountryByPlayerOneAfterAttack = gameData.gameMap.getConqueredCountries(1);
        Integer armyCountAfterAttack = 0;
        for(String country : conqueredCountryByPlayerOneAfterAttack){
            armyCountAfterAttack += gameData.gameMap.getCountry(country).getCountryArmyCount();
        }
        // armyCountAfterAttack is also 10.
        Assert.assertEquals(armyCountBeforeAttack, armyCountAfterAttack);

    }
    
    /**
     * Test the fortification benevolent strategy.
     */
    @Test
    public void testCalculateFortificationPaths() {

        // PlayerStrategy owns all countries except C5 & C6
        // PlayerStrategy can fortify FROM C2, C3 only (as C1 and C4 only have 1 army on the ground)
        HashMap<String, ArrayList<String>> actual_paths = playerOne.getStrategyType().getFortificationScenarios();
        HashMap<String, ArrayList<String>> expected_paths = new HashMap<String, ArrayList<String>>();

        for (String keySourceCountry : actual_paths.keySet()) {
            System.out.println("KEY " + keySourceCountry + ": \n");
            for (String correspondingDestinationCountry : actual_paths.get(keySourceCountry)) {
                System.out.println(correspondingDestinationCountry + "\n");
            }
        }

        ArrayList<String> expected_dest_for_C2 = new ArrayList<String>();
        expected_dest_for_C2.add("C4");
        expected_dest_for_C2.add("C1");
        expected_dest_for_C2.add("C3");
        expected_paths.put("C2", expected_dest_for_C2);

        // sort the Lists because we care about contents (not order of options)
        Collections.sort(expected_dest_for_C2);
        Collections.sort(actual_paths.get("C2"));
        assertEquals(actual_paths.get("C2"),expected_dest_for_C2);

        ArrayList<String> expected_dest_for_C3 = new ArrayList<String>();
        expected_dest_for_C3.add("C1");
        expected_dest_for_C3.add("C4");
        expected_dest_for_C3.add("C2");
        expected_paths.put("C3", expected_dest_for_C3);

        Collections.sort(expected_dest_for_C3);
        Collections.sort(actual_paths.get("C3"));
        assertEquals(actual_paths.get("C3"),expected_dest_for_C3);
    }

    /**
     * Calculate reinforcement army for a player with three cards in hand.
     * The trade is automatic.
     */
    @Test
    public void testCalculateReinforcementArmyFromValidTradeCards() {

        ArrayList<Country> countryList = new ArrayList<Country>();
        countryList.add(gameData.gameMap.getCountry("C1"));
        countryList.add(gameData.gameMap.getCountry("C2"));
        countryList.add(gameData.gameMap.getCountry("C3"));

        gameData.cardsDeck = new CardsDeck(countryList);
        ArrayList<Card> cardList = playerOne.getStrategyType().getPlayerCardList();
        System.out.println();
        for (int cardCount = 0; cardCount < 3; cardCount++) {
            Card card = gameData.cardsDeck.getCard();
            playerOne.getStrategyType().addToPlayerCardList(card);
        }
        playerOne.getStrategyType().cardExchangeArmyCount = 5;
        /*calculateTotalReinforcement internally verifies for the validity of the card exchange.*/
        int actual_value = playerOne.getStrategyType().tradeCardsAI(playerOne.getStrategyType().getPlayerCardList());

        /*expected value is 7 as 5 armies will be given for trading 3 cards of different types and
        2 extra armies as the territory in the card is conquered by the same player. */
        int expected_value = 7;
        assertEquals(expected_value,actual_value);
    }
}
