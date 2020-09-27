package com.java.controller.gameplay;

import com.java.model.gamedata.GameData;
import com.java.model.map.GameMap;
import com.java.model.player.HumanMode;
import com.java.model.player.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * TurnTest class tests the total reinforcement count for two scenarios. As well as the fortification paths retrieval for them. 
 * @version 1.0
 */
public class TurnTest {

    private static GameData gameData;
    private static Player playerOne,playerTwo;
    private static ArrayList<Player> players;

    @BeforeClass
    static public void setUp() {

        // Load the game with a dummy map and dummy data.
        gameData = new GameData();
        gameData.gameMap = new GameMap();

        // Test data with two players, two continents and six countries.
        players = new ArrayList<Player>();
        Player player1 = new Player();
        player1.setStrategyType(new HumanMode(1, "Player1"));
        Player player2 = new Player();
        player2.setStrategyType(new HumanMode(2, "Player2"));
        players.add(playerOne);
        players.add(playerTwo);

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
        gameData.gameMap.setCountryConquerer("C5", 1);
        gameData.gameMap.setCountryConquerer("C6", 1);

        gameData.gameMap.setContinentConquerer("Continent1", 1);
        gameData.gameMap.setContinentConquerer("Continent2", 1);

        gameData.gameMap.getCountry("C1").addArmy(3);
        gameData.gameMap.getCountry("C2").addArmy(1);
        gameData.gameMap.getCountry("C3").addArmy(2);
        gameData.gameMap.getCountry("C4").addArmy(1);
        gameData.gameMap.getCountry("C5").addArmy(5);
        gameData.gameMap.getCountry("C6").addArmy(6);
        
        playerOne.getStrategyType().setGameData(gameData);
        playerTwo.getStrategyType().setGameData(gameData);
    }

    /**
     * Calculate total reinforcement army for a player with no conquered continents and no conquered countries.
     */
    @Test
    public void testCalculateReinforcementArmyNoConqueredContinent() {

        int actual_value = playerTwo.getStrategyType().calculateReinforcementArmy();
        int expected_value = 3;
        assertEquals(expected_value, actual_value);
    }

    /**
     * Calculate total reinforcement army for a player with conquered continents having control values.
     */
    @Test
    public void testCalculateReinforcementArmyWithConqueredContinentPlayerOne() {

        int actual_value = playerTwo.getStrategyType().calculateReinforcementArmy();
        int expected_value = 14;
        assertEquals(expected_value,actual_value);
    }
    
    /**
     * Calculate total reinforcement army for a player with conquered continents having control values.
     */
    @Test
    public void testCalculateReinforcementArmyWithConqueredContinentPlayerTwo() {

        int actual_value = playerOne.getStrategyType().calculateReinforcementArmy();
        int expected_value = 14;
        assertEquals(expected_value,actual_value);
    }
    
    /**
     * Calculate all possible fortification paths for player.
     */
    @Test
    public void testCalculateFortificationPaths() {
        
    	// to make things interesting, tweak the dominance and army counts
        gameData.gameMap.setCountryConquerer("C5", 2);
        gameData.gameMap.setCountryConquerer("C6", 2);
        gameData.gameMap.getCountry("C1").setArmyCount(1);
        gameData.gameMap.getCountry("C2").setArmyCount(2);
        gameData.gameMap.getCountry("C3").setArmyCount(3);
        gameData.gameMap.getCountry("C4").setArmyCount(1);
        
        // PlayerStrategy owns all countries except C5 & C6
        // PlayerStrategy can fortify FROM C2, C3 only (as C1 and C4 only have 1 army on the ground)
        HashMap<String, ArrayList<String>> actual_paths = playerOne.getStrategyType().getFortificationScenarios();
        HashMap<String, ArrayList<String>> expected_paths = new HashMap<String, ArrayList<String>>();
        
        ArrayList<String> expected_dest_for_C2 = new ArrayList<String>();
        expected_dest_for_C2.add("C4");
        expected_dest_for_C2.add("C1");
        expected_dest_for_C2.add("C3");
        expected_paths.put("C2", expected_dest_for_C2);
        assertEquals(actual_paths.get("C2"),expected_dest_for_C2);
        
        ArrayList<String> expected_dest_for_C3 = new ArrayList<String>();
        expected_dest_for_C3.add("C1");
        expected_dest_for_C3.add("C4");
        expected_dest_for_C3.add("C2");
        expected_paths.put("C3", expected_dest_for_C3);
        assertEquals(actual_paths.get("C3"),expected_dest_for_C3);
        
        System.out.println(actual_paths.equals(expected_paths));

       
    }
}
