package com.java.model.player;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class models the state of attack phase to be presented in phase View.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez 
 * @version 1.0.0
 * */
public class AttackPhaseState implements Serializable {

	private String attackingPlayer = " ";
	private String defendingPlayer = " ";

	private String attackingCountry = " ";
	private String defendingCountry = " ";

	private Integer attackerDiceCount = 0;
	private Integer defenderDiceCount = 0;
	private ArrayList<Integer> attackerDiceRollResults = null;
	private ArrayList<Integer> defenderDiceRollResults = null;

	private Integer attackerLostArmyCount = 0;
	private Integer defenderLostArmyCount = 0;
	private Boolean battleOutcomeFlag = false; // true if attacker conquers the territory else false;
	
	/**
	 * Default constructor to instance AttackPhaseState.
	 * */
	public AttackPhaseState() {

	}
	
	/**
	 * Gets the attacking player.
	 * 
	 * @return the attacking player.
	 * 
	 * */
	public String getAttackingPlayer() {
		return attackingPlayer;
	}
	
	/**
	 * Sets the attacking player.
	 * 
	 * @param attackingPlayer attacking player.
	 * 
	 * */
	public void setAttackingPlayer(String attackingPlayer) {
		this.attackingPlayer = attackingPlayer;
	}
	
	/**
	 * Gets the defending player.
	 * 
	 * @return the defending player.
	 * 
	 * */
	public String getDefendingPlayer() {
		return defendingPlayer;
	}
	
	/**
	 * Sets the defending player.
	 * 
	 * @param defendingPlayer defending player.
	 * 
	 * */
	public void setDefendingPlayer(String defendingPlayer) {
		this.defendingPlayer = defendingPlayer;
	}
	
	/**
	 * Gets the attacking country.
	 * 
	 * @return the attacking country.
	 * 	 
	 */
	public String getAttackingCountry() {
		return attackingCountry;
	}
	
	/**
	 * Sets the attacking country.
	 * 
	 * @param attackingCountry attacking country.
	 * 
	 * */
	public void setAttackingCountry(String attackingCountry) {
		this.attackingCountry = attackingCountry;
	}

	/**
	 * Gets the defending country.
	 * 
	 * @return the defending country.
	 * 	 
	 */
	public String getDefendingCountry() {
		return defendingCountry;
	}
	
	/**
	 * Sets the defending country.
	 * 
	 * @param defendingCountry defending country.
	 * 
	 * */
	public void setDefendingCountry(String defendingCountry) {
		this.defendingCountry = defendingCountry;
	}
	
	/**
	 * Gets the attacker dice count.
	 * 
	 * @return the attacker dice count.
	 * 
	 * */
	public Integer getAttackerDiceCount() {
		return attackerDiceCount;
	}
	
	/**
	 * Sets the attacker dice count.
	 * 
	 * @param attackerDiceCount attacker dice count.
	 * */
	public void setAttackerDiceCount(Integer attackerDiceCount) {
		this.attackerDiceCount = attackerDiceCount;
	}

	/**
	 * Gets defender dice count
	 * 
	 * @return defender dice count.
	 * 
	 * */
	public Integer getDefenderDiceCount() {
		return defenderDiceCount;
	}
	
	/**
	 * Sets defender dice count
	 * 
	 * @param defenderDiceCount defeneder dice count.
	 * */
	public void setDefenderDiceCount(Integer defenderDiceCount) {
		this.defenderDiceCount = defenderDiceCount;
	}
	
	/**
	 * Gets the attacker dice roll results.
	 * 
	 * @return the attacker dice roll results.
	 * 
	 * */
	public ArrayList<Integer> getAttackerDiceRollResults() {
		return attackerDiceRollResults;
	}
	
	/**
	 * Sets the attacker dice roll results.
	 * 
	 * @param attackerDiceRolls list of attacker dice roll results.
	 * 
	 * */
	public void setAttackerDiceRollResults(ArrayList<Integer> attackerDiceRolls) {
		this.attackerDiceRollResults = attackerDiceRolls;
	}

	/**
	 * Gets the list of defender dice roll results.
	 * 
	 * @return the list of defender dice roll results.
	 * */
	public ArrayList<Integer> getDefenderDiceRollResults() {
		return defenderDiceRollResults;
	}

	/**
	 * Sets the list of defender dice roll results
	 * 
	 * @param defenderDiceRolls list of defender dice roll results.
	 * */
	public void setDefenderDiceRollResults(ArrayList<Integer> defenderDiceRolls) {
		this.defenderDiceRollResults = defenderDiceRolls;
	}
	
	/**
	 * Gets the number of army lost by the attacker.
	 * 
	 * @return the number of army lost by the attacker.
	 * */
	public Integer getAttackerLostArmyCount() {
		return attackerLostArmyCount;
	}
	
	/**
	 * Sets the number of army lost by the attacker.
	 * 
	 * @param attackerLostArmyCount number of army lost by the attacker.
	 * */
	public void setAttackerLostArmyCount(Integer attackerLostArmyCount) {
		this.attackerLostArmyCount = attackerLostArmyCount;
	}
	
	/**
	 * Gets the number of army lost by the defender.
	 * 
	 * @return the number of army lost by the defender.
	 * */
	public Integer getDefenderLostArmyCount() {
		return defenderLostArmyCount;
	}
	
	/**
	 * Sets the number of army lost by the defender.
	 * 
	 * @param defenderLostArmyCount the number of army lost by the defender.
	 * 
	 * */
	public void setDefenderLostArmyCount(Integer defenderLostArmyCount) {
		this.defenderLostArmyCount = defenderLostArmyCount;
	}

	/**
	 * Gets true if attacker conquers the territory else false.
	 * 
	 * @return true if attacker conquers the territory.
	 * */
	public Boolean getBattleOutcomeFlag() {
		return battleOutcomeFlag;
	}

	/**
	 * Sets true if attacker conquers the territory else false.
	 * 
	 * @param battleOutcomeFlag is true if attacker conquers the territory.
	 * */
	public void setBattleOutcomeFlag(Boolean battleOutcomeFlag) {
		this.battleOutcomeFlag = battleOutcomeFlag;
	}
}
