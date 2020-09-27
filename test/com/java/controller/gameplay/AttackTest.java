package com.java.controller.gameplay;

import com.java.model.gamedata.GameData;
import com.java.model.map.GameMap;
import com.java.model.player.AttackPhaseState;
import com.java.model.player.HumanMode;
import com.java.model.player.Player;
import com.java.model.player.PlayerStrategy;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * AttackTest class tests the potential attack scenarios
 * as well as the dynamic interactions of a simulated attack
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 2.0.0
 */
public class AttackTest {

    private static GameData gameData;
    private static Player playerOne,playerTwo,playerThree;
    private static ArrayList<Player> players;
    public static AttackPhaseState attackPhase = new AttackPhaseState();
    public static ArrayList<AttackPhaseState> attackPhaseState = new ArrayList<>();
    

    @BeforeClass
    static public void setUp() {

        // Load the game with a dummy map and dummy data.
        gameData = new GameData();
        gameData.gameMap = new GameMap();

        // Test data with two players, two continents and six countries.
        players = new ArrayList<>();
        playerOne   = new Player();
        playerOne.setStrategyType(new HumanMode(1, "P1"));
        playerTwo   = new Player();
        playerTwo.setStrategyType(new HumanMode(2, "P2"));
        playerThree = new Player();
        playerThree.setStrategyType(new HumanMode(3, "P3"));
        players.add(playerOne);
        players.add(playerTwo);
        players.add(playerThree);

        gameData.setPlayers(players);
        gameData.gameMap.setupPlayerNames(players);
        gameData.gameMap.addContinent("Continent1", 5);

        gameData.gameMap.addCountry("C1", "Continent1");
        gameData.gameMap.addCountry("C2", "Continent1");
        gameData.gameMap.addCountry("C3", "Continent1");
        gameData.gameMap.setAdjacentCountry("C1", "C2");

        gameData.gameMap.setCountryConquerer("C1", 1);
        gameData.gameMap.setCountryConquerer("C2", 2);

        gameData.gameMap.getCountry("C1").addArmy(3);
        gameData.gameMap.getCountry("C2").addArmy(2);

        playerOne.getStrategyType().setGameData(gameData);
        playerTwo.getStrategyType().setGameData(gameData);
    
		attackPhaseState.add(attackPhase);

        attackPhase.setAttackingPlayer(playerOne.getStrategyType().getPlayerName());
        attackPhase.setDefendingPlayer(playerTwo.getStrategyType().getPlayerName());
        
        attackPhase.setAttackingCountry("C1");
        attackPhase.setDefendingCountry("C2");
        
        attackPhase.setAttackerDiceCount(2);
        attackPhase.setDefenderDiceCount(2);
        
        ArrayList<Integer> attackerDiceRolls = new ArrayList<Integer>();
        attackerDiceRolls.add(5);
        attackerDiceRolls.add(2);
        java.util.Collections.sort(attackerDiceRolls, java.util.Collections.reverseOrder());
        
        ArrayList<Integer> defenderDiceRolls = new ArrayList<Integer>();
        defenderDiceRolls.add(4);
        defenderDiceRolls.add(1);
        java.util.Collections.sort(defenderDiceRolls, java.util.Collections.reverseOrder());
        
		attackPhase.setAttackerDiceRollResults(attackerDiceRolls);
		attackPhase.setDefenderDiceRollResults(defenderDiceRolls);
           
    }
    /**
     * Test dice count thresholds for attack - PlayerStrategy 1 from C1
     */
    @Test
    public void testAttackDiceThresholds() {
    	Integer actualMaxDice = playerOne.getStrategyType().getActualMaxAllowedDiceCountForAction("attack", "C1", 2);
    	Integer expectedMaxDice = 2;
    	assertEquals(actualMaxDice,expectedMaxDice);
    }
    
    /**
     * Test dice count thresholds for defense - PlayerStrategy 2 for C2
     */
    @Test
    public void testDefendDiceThresholds() {
    	Integer actualMaxDice = playerTwo.getStrategyType().getActualMaxAllowedDiceCountForAction("defend", "C2", 2);
    	Integer expectedMaxDice = 2;
    	assertEquals(actualMaxDice,expectedMaxDice);
    }
    
    
    /**
     * Fetch all potential attack scenarios for player
     */
    @Test
    public void testPotentialAttackScenariosForPlayer() {
    	attackPhaseState.clear();
    	gameData.gameMap.setCountryConquerer("C1", 1);
        gameData.gameMap.setCountryConquerer("C2", 2);

        gameData.gameMap.getCountry("C1").addArmy(3);
        gameData.gameMap.getCountry("C2").addArmy(2);

        playerOne.getStrategyType().setGameData(gameData);
        playerTwo.getStrategyType().setGameData(gameData);
    	HashMap<String, ArrayList<String>> actual_scenarios_for_Player1 = playerOne.getStrategyType().getPotentialAttackScenarios();
        HashMap<String, ArrayList<String>> expected_scenarios_for_Player1 = new HashMap<String, ArrayList<String>>();
        
		for (String keySourceCountry : actual_scenarios_for_Player1.keySet()) {
			System.out.println("KEY " + keySourceCountry + ": \n");
			for (String correspondingDestinationCountry : actual_scenarios_for_Player1.get(keySourceCountry)) {	
				System.out.println(correspondingDestinationCountry + "\n"); 
			}
		}
        
        ArrayList<String> expected_dest_for_C1 = new ArrayList<String>();
        expected_dest_for_C1.add("C2");
        expected_scenarios_for_Player1.put("C1", expected_dest_for_C1);

        // sort the Lists because we care about contents (not order of options)
        assertEquals(actual_scenarios_for_Player1.get("C1"),expected_dest_for_C1);
        
    	HashMap<String, ArrayList<String>> actual_scenarios_for_Player2 = playerTwo.getStrategyType().getPotentialAttackScenarios();
        HashMap<String, ArrayList<String>> expected_scenarios_for_Player2 = new HashMap<String, ArrayList<String>>();
        
		for (String keySourceCountry : actual_scenarios_for_Player2.keySet()) {
			System.out.println("KEY " + keySourceCountry + ": \n");
			for (String correspondingDestinationCountry : actual_scenarios_for_Player2.get(keySourceCountry)) {	
				System.out.println(correspondingDestinationCountry + "\n"); 
			}
		}
        
        ArrayList<String> expected_dest_for_C2 = new ArrayList<String>();
        expected_dest_for_C2.add("C1");
        expected_scenarios_for_Player2.put("C2", expected_dest_for_C2);

        // sort the Lists because we care about contents (not order of options)
        assertEquals(actual_scenarios_for_Player2.get("C2"),expected_dest_for_C2);       
  
    }
    
    /**
     * Test attacker / defender battle given predictable data set 
     */
    @Test
    public void testFight() {
    	Boolean attackerWon = false;
    	playerOne.getStrategyType().setIsWinner(true);
    	attackerWon = playerOne.getStrategyType().fight(attackPhase);
    	assertTrue(attackerWon);
    }
    
    /**
     * Test new ownership of the newly conquered country 
     */
    @Test
    public void testOwnershipOfConqueredCountry() {
    	Integer expectedCountryOwner = 2;
    	Integer actualCountryOwner = gameData.gameMap.getCountry("C2").getCountryConquerorID();
    	assertEquals(expectedCountryOwner,actualCountryOwner);
    }
    
    /**
     * Test attacker army count in newly conquered country 
     */
    @Test
    public void testArmyMoveDestination() {
    	Integer expectedArmyCount = 4;
    	Integer actualArmyCount = gameData.gameMap.getCountry("C2").getCountryArmyCount();
    	assertEquals(expectedArmyCount,actualArmyCount);
    }
    
    /**
     * Test army count decrement in source country 
     */
    @Test
    public void testArmyMoveSource() {
    	Integer expectedArmyCount = 4;
    	Integer actualArmyCount = gameData.gameMap.getCountry("C1").getCountryArmyCount();
    	assertEquals(expectedArmyCount,actualArmyCount);
    }
    
}
