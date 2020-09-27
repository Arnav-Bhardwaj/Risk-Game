package com.java.controller.gameplay;

import com.java.model.gamedata.GameData;
import com.java.model.map.GameMap;
import com.java.model.player.HumanMode;
import com.java.model.player.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * FortificationTest class tests the fortification paths retrieval for a given scenario. 
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez 
 * @version 1.0.0
 */
public class FortificationTest {

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
        playerOne   = new Player();
        playerOne.setStrategyType(new HumanMode(1, "P1"));
        playerTwo   = new Player();
        playerTwo.setStrategyType(new HumanMode(2, "P2"));
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

        gameData.gameMap.getCountry("C1").addArmy(1);
        gameData.gameMap.getCountry("C2").addArmy(3);
        gameData.gameMap.getCountry("C3").addArmy(5);
        gameData.gameMap.getCountry("C4").addArmy(1);
        gameData.gameMap.getCountry("C5").addArmy(7);
        gameData.gameMap.getCountry("C6").addArmy(9);
        
        playerOne.getStrategyType().setGameData(gameData);
        playerTwo.getStrategyType().setGameData(gameData);
    }

    
    /**
     * Calculate all possible fortification paths for player.
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
}
