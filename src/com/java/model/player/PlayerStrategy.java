
package com.java.model.player;

import com.java.controller.dice.Dice;
import com.java.model.Observable;
import com.java.model.cards.ArmyType;
import com.java.model.cards.Card;
import com.java.model.gamedata.GameData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class models the player strategy, it holds the id, the name, and the order to
 * play for the round robing.
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 3.0.0
 */
public abstract class PlayerStrategy extends Observable implements Serializable {

	protected Integer playerID;
	protected String playerName;
	protected Integer orderOfPlay;
	protected ArrayList<Card> cardList;
	protected static int cardExchangeArmyCount = 5;

	protected Boolean isWinner = false;

	protected GameData gameData;
	protected Dice playerDice;

	protected ArrayList<AttackPhaseState> attackPhaseState;
	protected ArrayList<ReinforcementPhaseState> reinforcementPhaseState;
	protected ArrayList<FortificationPhaseState> fortificationPhaseState;

	protected static final int REINFORCEMENT_DIVISION_FACTOR = 3;
	public Boolean isActive = true;
	protected static final int MINIMUM_REINFORCEMENT_ARMY_NUMBER = 3;
	public Boolean gameOn = true;

	public PlayerStrategy(){
	}

	/**
	 * Creates a player by giving the id and the name
	 *
	 * @param playerID   the player id.
	 * @param playerName the player name.
	 */
	public PlayerStrategy(Integer playerID, String playerName) {
		this.playerID = playerID;
		this.playerName = playerName;
		this.cardList = new ArrayList<Card>();
		attackPhaseState = new ArrayList<>();
		reinforcementPhaseState = new ArrayList<>();
		fortificationPhaseState = new ArrayList<>();
		this.playerDice = new Dice();
	}

	/**
	 * Get player's win status
	 *
	 * @return true if player is a winner else false
	 */
	public Boolean getIsWinner() {
		return isWinner;
	}

	/**
	 * get player's active status
	 *
	 * @return true if player is still in the game else false
	 */
	public boolean getIsActive() {
		return isActive;
	}

	/**
	 * set true if player won the game
	 *
	 * @param isWinner true if the player won the game.
	 */
	public void setIsWinner(Boolean isWinner) {
		this.isWinner = isWinner;
	}


	/**
	 * Displays the cards.
	 */
	protected void showCards() {

		ArrayList<Card> playerCardList = getPlayerCardList();
		ArrayList<String> playerCountryList = new ArrayList<String>();

		int cardsCount = 0;

		for (Card cards : playerCardList) {
			System.out.println(cardsCount + ". " + cards.getCountry().getCountryName() + " " + cards.getArmyType());
			playerCountryList.add(cards.getCountry().getCountryName());
			cardsCount++;
		}
	}

	/**
	 * Calculates the total number of armies to be given during reinforcement.
	 *
	 * @param playerExchangeCards the cards that the player exchanged
	 * @return The total number of reinforcement army for the player(integer value).
	 */
	public int calculateTotalReinforcement(ArrayList<Card> playerExchangeCards) {
		int totalReinforcementArmyCount = 0;
		totalReinforcementArmyCount += (reinforcementArmyCountFromCards(playerExchangeCards)
				+ calculateReinforcementArmy());

		return totalReinforcementArmyCount;
	}

	/**
	 * Function to count the reinforcement army based on the number of territories
	 * and continents owned.
	 *
	 * @return total reinforcement army count
	 */
	public Integer calculateReinforcementArmy() {

		Integer totalReinforcementArmyCount = 0;
		Integer totalCountriesOwnedByPlayer;
		Integer currentPlayerID = playerID;

		/*
		 * Count the total number of continents owned by the player and retrieve the
		 * continent's control value.
		 */
		HashSet<String> conqueredContinentsPerPlayer = this.gameData.gameMap
				.getConqueredContinentsPerPlayer(currentPlayerID);

		for (String continent : conqueredContinentsPerPlayer) {
			Integer controlValue = this.gameData.gameMap.getContinent(continent).getContinentControlValue();
			totalReinforcementArmyCount += controlValue;
		}

		/*
		 * Count the total number of countries owned by the player and provide a minimum
		 * of three armies.
		 */
		totalCountriesOwnedByPlayer = this.gameData.gameMap.getConqueredCountries(currentPlayerID).size();
		totalReinforcementArmyCount += totalCountriesOwnedByPlayer
				/ REINFORCEMENT_DIVISION_FACTOR > MINIMUM_REINFORCEMENT_ARMY_NUMBER
				? totalCountriesOwnedByPlayer / REINFORCEMENT_DIVISION_FACTOR
				: MINIMUM_REINFORCEMENT_ARMY_NUMBER;
		return totalReinforcementArmyCount;
	}

	/**
	 * Calculates the reinforcement army from card exchange.
	 *
	 * @param acumulatedPlayerExchangeCards the cards that the player holds.
	 * @return the number of reinforcement army from card exchange.
	 *
	 */
	private int reinforcementArmyCountFromCards(ArrayList<Card> acumulatedPlayerExchangeCards) {
		ArrayList<Card> playerExchangeCards;
		int countReinforcementFromCardExchange = 0;
		while (acumulatedPlayerExchangeCards.size() > 2) {
			playerExchangeCards = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				playerExchangeCards.add(acumulatedPlayerExchangeCards.get(0));
				acumulatedPlayerExchangeCards.remove(0);

			}
			boolean can_exchange = isValidExchange(playerExchangeCards);
			boolean extraTerritoryMatchArmy = isExtraTerritoryMatchArmy(playerExchangeCards);

			if (can_exchange == true) {
				if (extraTerritoryMatchArmy == true) {
					countReinforcementFromCardExchange += 2;
				}
				countReinforcementFromCardExchange += PlayerStrategy.getCardExchangeArmyCount();
				PlayerStrategy.setCardExchangeArmyCount();
			}
			for (Card card : playerExchangeCards) {
				removeFromPlayerCardList(card);
				this.gameData.cardsDeck.setCard(card);
			}
		}
		return countReinforcementFromCardExchange;
	}

	/**
	 * If a player has card with a country that he owns he will receive more armies.
	 *
	 * @param playerExchangeCards cards to be exchanged
	 * @return true if the player deserves more armies.
	 */
	private boolean isExtraTerritoryMatchArmy(ArrayList<Card> playerExchangeCards) {
		boolean extraTerritoryMatchArmy = false;

		for (Card card : playerExchangeCards) {
			if (playerID == card.getCountry().getCountryConquerorID()) {
				extraTerritoryMatchArmy = true;
				break;
			}
		}
		return extraTerritoryMatchArmy;
	}

	/**
	 * Helper method to check if it is valid card exchange.
	 *
	 * @param playerExchangeCards cards to exchange.
	 * @return true if the exchange is valid.
	 */
	public boolean isValidExchange(ArrayList<Card> playerExchangeCards) {
		boolean condition_same = ((playerExchangeCards.get(0).getArmyType()
				.equals(playerExchangeCards.get(1).getArmyType()))
				&& playerExchangeCards.get(0).getArmyType().equals(playerExchangeCards.get(2).getArmyType()));

		boolean condition_different = (!(playerExchangeCards.get(0).getArmyType()
				.equals(playerExchangeCards.get(1).getArmyType())))
				&& (!(playerExchangeCards.get(0).getArmyType().equals(playerExchangeCards.get(2).getArmyType())))
				&& (!(playerExchangeCards.get(1).getArmyType().equals(playerExchangeCards.get(2).getArmyType())));
		return (condition_same || condition_different);
	}

	/**
	 * Helper method to trade cards automatically.
	 *
	 * @param playerCardList list of cards to trade.
	 * @return the number of traded armies.
	 */
	public int tradeCardsAI(ArrayList<Card> playerCardList){
		HashMap<Enum,Integer> playerDeck = new HashMap<>();
		Integer reinforcementAICount = 0;
		Integer infantryCount = 0;
		Integer cavalryCount = 0;
		Integer artilleryCount = 0;
		int minArmyType = 0;
		boolean isExtraTerritoryMatch = false;
		ArrayList<Card> toBeRemoved = new ArrayList<>();
		for(Enum armyType: ArmyType.values()){
			playerDeck.put(armyType,0);
		}
		for(Card card : playerCardList){
			if(card.getArmyType().ordinal() == 0){
				playerDeck.put(card.getArmyType(),++infantryCount);
			}
			if(card.getArmyType().ordinal() == 1){
				playerDeck.put(card.getArmyType(),++cavalryCount);
			}
			if(card.getArmyType().ordinal() == 2){
				playerDeck.put(card.getArmyType(),++artilleryCount);
			}
			minArmyType = Math.min(Math.min(infantryCount,cavalryCount),artilleryCount);
		}

		// Get the max possibilities of different cards
		while(minArmyType != 0){
			boolean isInfantry = false;
			boolean isCavalry = false;
			boolean isArtillery = false;
			for(Card card:playerCardList){
				if((card.getArmyType().equals(ArmyType.INFANTRY)) && (!isInfantry)){
					if(card.getCountry().getCountryConquerorID() == playerID){
						isExtraTerritoryMatch = true;
					}
					playerDeck.put(ArmyType.INFANTRY,--infantryCount);
					toBeRemoved.add(card);
					isInfantry = true;
				}
				if((card.getArmyType().equals(ArmyType.CAVALRY)) && (!isCavalry)){
					if(card.getCountry().getCountryConquerorID() == playerID){
						isExtraTerritoryMatch = true;
					}
					toBeRemoved.add(card);
					playerDeck.put(ArmyType.CAVALRY,--cavalryCount);
					isCavalry = true;
				}
				if((card.getArmyType().equals(ArmyType.ARTILLERY)) && (!isArtillery)){
					if(card.getCountry().getCountryConquerorID() == playerID){
						isExtraTerritoryMatch = true;
					}
					playerDeck.put(ArmyType.ARTILLERY,--artilleryCount);
					toBeRemoved.add(card);
					isArtillery = true;
				}
				if(isInfantry && isCavalry && isArtillery){
					break;
				}
			}
			reinforcementAICount += getCardExchangeArmyCount();
			setCardExchangeArmyCount();

			minArmyType = Math.min(Math.min(infantryCount,cavalryCount),artilleryCount);
			for(Card card: toBeRemoved){
				playerCardList.remove(card);
				this.gameData.cardsDeck.setCard(card);
			}
			toBeRemoved.clear();
		}

		// Get the max possibilities of same cards
		for(Enum key :playerDeck.keySet()){
			while(playerDeck.get(key) > 2){
				reinforcementAICount += getCardExchangeArmyCount();
				setCardExchangeArmyCount();
				int count = 0;
				for(Card card : playerCardList){
					if((card.getArmyType().equals(key)) && (count < 3)){
						if(card.getCountry().getCountryConquerorID() == playerID){
							isExtraTerritoryMatch = true;
						}
						toBeRemoved.add(card);
						count++;
					}
					if(count == 3){
						for(Card removeCard: toBeRemoved){
							playerCardList.remove(removeCard);
							this.gameData.cardsDeck.setCard(removeCard);
						}
						toBeRemoved.clear();
						break;
					}
				}
				playerDeck.put(key,playerDeck.get(key)-3);
			}
		}

		if(isExtraTerritoryMatch == true){
			reinforcementAICount += 2;
		}
		return reinforcementAICount;
	}


	/**
	 * Get the total number of armies to be received on trading cards.
	 * @return number of reinforcement armies.
	 */
	public Integer getReinforcementCountFromValidCardsAI() {

		ArrayList<Card> playerCardList = getPlayerCardList();

		Integer reinforcementArmyCardsCount = 0;

		if(playerCardList.size()>2){
			reinforcementArmyCardsCount = tradeCardsAI(playerCardList);
		}
		return reinforcementArmyCardsCount;
	}

	/**
	 * RollDiceBattle resorts to the core rollDice method in Dice but contains
	 * additional logic to set attackPhase logic
	 *
	 * @param attackPhase has all the dice count info for attacker and defender
	 */

	protected void rollDiceBattle(AttackPhaseState attackPhase) {

		System.out.println("\n ROLLING DICE \n");

		Integer selectedDefenderDiceCount = attackPhase.getDefenderDiceCount();
		Integer selectedAttackerDiceCount = attackPhase.getAttackerDiceCount();

		ArrayList<Integer> attackerDiceRolls = playerDice.rollDice(selectedAttackerDiceCount);
		ArrayList<Integer> defenderDiceRolls = playerDice.rollDice(selectedDefenderDiceCount);

		attackPhase.setAttackerDiceRollResults(attackerDiceRolls);
		attackPhase.setDefenderDiceRollResults(defenderDiceRolls);

		System.out.println("\nAttacker rolled: " + attackerDiceRolls.toString());
		System.out.println("\nDefender rolled: " + defenderDiceRolls.toString());

	}

	/**
	 * All of the potential attack scenarios for current player.
	 *
	 * @return the comprehensive list with all source and target countries.
	 */

	public HashMap<String, ArrayList<String>> getPotentialAttackScenarios() {

		HashMap<String, ArrayList<String>> attackScenarios = new HashMap<String, ArrayList<String>>();
		// source countries which PlayerStrategy could attack FROM
		HashSet<String> poolOfPotentialSourceCountries = this.gameData.gameMap.getConqueredCountries(playerID);


		for (String potentialSourceCountry : poolOfPotentialSourceCountries) {
			// eliminate countries with only a single army count
			if (this.gameData.gameMap.getCountry(potentialSourceCountry).getCountryArmyCount() < 2) {
				continue;
			}
			HashSet<String> adjacentCountries = new HashSet<String>();
			adjacentCountries = this.gameData.gameMap.getAdjacentCountries(potentialSourceCountry);
			for (String adjacentCountry : adjacentCountries) {

				// ensure adjacent country not owned by same player
				if (this.gameData.gameMap.getCountry(adjacentCountry).getCountryConquerorID() != playerID) {

					attackScenarios.putIfAbsent(potentialSourceCountry, new ArrayList<String>());
					attackScenarios.get(potentialSourceCountry).add(adjacentCountry);
				}
			}
		}

		return attackScenarios;
	}

	/**
	 * Helper method to print out all of the potential attack scenarios for current
	 * player.
	 *
	 * @param attackScenarios: the comprehensive list with all source and target
	 *        countries.
	 *
	 */
	protected void showAllAttackScenarios(HashMap<String, ArrayList<String>> attackScenarios) {

		HashMap<String, Integer> maxAttackArmyCountPossiblePerSrcCountry = new HashMap<String, Integer>();
		HashMap<String, Integer> maxDefenseArmyCountPossiblePerDestCountry = new HashMap<String, Integer>();

		// Print all the options out for the player to see and choose from
		for (String keySourceCountry : attackScenarios.keySet()) {

			int actualArmyCount = this.gameData.gameMap.getCountry(keySourceCountry).getCountryArmyCount();
			int maxAttackArmyCount = actualArmyCount - 1;

			if (maxAttackArmyCount >= 3) {
				maxAttackArmyCountPossiblePerSrcCountry.put(keySourceCountry, 3);

			} else if (maxAttackArmyCount >= 2) {
				maxAttackArmyCountPossiblePerSrcCountry.put(keySourceCountry, 2);

			} else {
				maxAttackArmyCountPossiblePerSrcCountry.put(keySourceCountry, 1);
			}
			for (String correspondingDestinationCountry : attackScenarios.get(keySourceCountry)) {

				int defenderArmyCount = this.gameData.gameMap.getCountry(correspondingDestinationCountry)
						.getCountryArmyCount();
				int defenderMaxDiceCount = 1;

				if (defenderArmyCount >= 2) {
					defenderMaxDiceCount = 2;
				}
				maxDefenseArmyCountPossiblePerDestCountry.putIfAbsent(correspondingDestinationCountry,
						defenderMaxDiceCount);

				System.out.println("\n" + keySourceCountry + " (occupied by " + actualArmyCount + " of your armies)"
						+ "\t -> \t" + correspondingDestinationCountry + "\t **defended by " + defenderArmyCount
						+ " of "

						+ gameData.getPlayer(this.gameData.gameMap.getCountry(correspondingDestinationCountry)
						.getCountryConquerorID()).getStrategyType().getPlayerName()
						+ "'s armies**" + "\t (attack with up to "

						+ maxAttackArmyCountPossiblePerSrcCountry.get(keySourceCountry) + " armies)");
			}
		}
	}

	/**
	 * Helper method to get max allowable dice count given specific context: action
	 * and country army count.
	 *
	 * @param action: either attack/defense.
	 * @param countryInScopeForAction: either the source country to attack from /
	 *        the target country to be attacked.
	 * @param maxDiceCountAllowedForAction: a default max based on Risk game rules.
	 * @return selectedDiceCount: a valid number of dice to roll based on action and
	 *         army counts.
	 *
	 */
	public int getActualMaxAllowedDiceCountForAction(String action, String countryInScopeForAction,
													 int maxDiceCountAllowedForAction) {

		int countryArmyCount = this.gameData.gameMap.getCountry(countryInScopeForAction).getCountryArmyCount();

		// attacker must have at least 1 army on the ground at all times
		if (action.equals("attack")) {
			countryArmyCount--;
		}

		if (countryArmyCount == 1 || countryArmyCount == 2) {
			maxDiceCountAllowedForAction = countryArmyCount;
		}

		return maxDiceCountAllowedForAction;
	}

	/**
	 * Main fight method which encompasses the dynamic interactions of rolling and
	 * comparing dice This handles the army count updates as well as conqueror id's
	 * if need be.
	 *
	 * @param attackPhase Attack Phase object
	 * @return true if attacker conquers the target country
	 */

	public Boolean fight(AttackPhaseState attackPhase) {

		String selectedSourceCountry = attackPhase.getAttackingCountry();
		String selectedDestinationCountry = attackPhase.getDefendingCountry();

		System.out.println("\n HEADS-UP " + attackPhase.getDefendingPlayer() + " YOU ARE UNDER ATTACK!");

		Integer attackerLostArmyCount = 0;
		Integer defenderLostArmyCount = 0;
		Boolean battleOutcomeFlag = false;

		Integer selectedDefenderDiceCount = attackPhase.getDefenderDiceCount();
		Integer selectedAttackerDiceCount = attackPhase.getAttackerDiceCount();
		notifyView();

		ArrayList<Integer> attackerDiceRolls = attackPhase.getAttackerDiceRollResults();
		ArrayList<Integer> defenderDiceRolls = attackPhase.getDefenderDiceRollResults();
		notifyView();

		// take the lowest dice count among the two
		int benchDiceRoll = java.lang.Math.min(selectedDefenderDiceCount, selectedAttackerDiceCount);

		for (int d = 0; d < benchDiceRoll; d++) {
			if (defenderDiceRolls.get(d) >= attackerDiceRolls.get(d)) {
				System.out.println("\n Attacker loses 1 army count\n");
				this.gameData.gameMap.deductArmyToCountry(selectedSourceCountry, 1);
				attackerLostArmyCount++;
			} else {
				System.out.println("\n Defender loses 1 army count\n");
				this.gameData.gameMap.deductArmyToCountry(selectedDestinationCountry, 1);
				defenderLostArmyCount++;
			}

			// if attacker wins
			if (this.gameData.gameMap.getCountry(selectedDestinationCountry).getCountryArmyCount() == 0) {
				// declare new winner
				Integer defenderPlayerId = this.gameData.gameMap.getCountry(selectedDestinationCountry).getCountryConquerorID();

				battleOutcomeFlag = true;
				System.out.println("\n" + this.playerName + " has conquered " + selectedDestinationCountry + "!");
				// if game is over, no need to worry about army counts
				if (!checkIfPlayerHasConqueredTheWorld()) {
					selectedAttackerDiceCount = getNumberofArmiesAttackerWantsToMove(selectedSourceCountry);
				}
				
				// safety catch for non-interactive modes
				if(selectedAttackerDiceCount == null) {
					selectedAttackerDiceCount = 1;
				}
				
				this.gameData.gameMap.updateCountryConquerer(selectedDestinationCountry,
						this.gameData.gameMap.getCountry(selectedDestinationCountry).getCountryConquerorID(),
						this.playerID);
				this.gameData.gameMap.deductArmyToCountry(selectedSourceCountry, selectedAttackerDiceCount);
				this.gameData.gameMap.deductArmyToCountry(selectedDestinationCountry, defenderLostArmyCount);
				this.gameData.gameMap.addArmyToCountry(selectedDestinationCountry, selectedAttackerDiceCount);

				// hand over defender's cards to attacker if the defender no longer controls any
				// country in the world
				if (checkIfPlayerLostTheGame(defenderPlayerId)) {
					ArrayList<Card> defendersCards = gameData.getPlayer(defenderPlayerId).getStrategyType().cardList;

					this.cardList.addAll(defendersCards);

					System.out.println("PlayerStrategy receives defender's " + defendersCards.size() + " cards");
					// clear defender's cardList
					gameData.getPlayer(defenderPlayerId).getStrategyType().cardList.clear();
				}
			}
		}

		attackPhase.setBattleOutcomeFlag(battleOutcomeFlag);
		attackPhase.setAttackerLostArmyCount(attackerLostArmyCount);
		attackPhase.setDefenderLostArmyCount(defenderLostArmyCount);
		// attackPhaseState.add(attackPhase);
		notifyView();

		System.out.println("Army count for " + selectedSourceCountry + " is now: "
				+ this.gameData.gameMap.getCountry(selectedSourceCountry).getCountryArmyCount());
		System.out.println("Army count for " + selectedDestinationCountry + " is now: "
				+ this.gameData.gameMap.getCountry(selectedDestinationCountry).getCountryArmyCount());

		return battleOutcomeFlag;
	}

	/**
	 * Helper method called upon when player cannot attack anymore or when player
	 * does not wish to attack anymore.
	 */
	protected void endAttack() {

		gameOn = true;
		System.out.println("\n****Attack Phase Ends for player " + this.playerName + "..****\n");
	}

	/**
	 * Helper method to check if the player conquered the whole world.
	 *
	 * @return true if they conquered the entire world
	 */
	public boolean checkIfPlayerHasConqueredTheWorld() {

		// boolean isWinner = false;
		HashSet<String> allConqueredCountries = new HashSet<String>();
		allConqueredCountries = this.gameData.gameMap.getConqueredCountries(this.playerID);

		if (allConqueredCountries.size() == this.gameData.gameMap.getNumberOfCountries()) {
			isWinner = true;
			gameOn = false;
		}
		return isWinner;
	}

	/**
	 * Helper method to check if the player lost the game.
	 * @param playerId pass the value of the player that is in game
	 * @return true if the player is active
	 */
	private boolean checkIfPlayerLostTheGame(Integer playerId) {

		HashSet<String> allConqueredCountries = new HashSet<String>();
		allConqueredCountries = this.gameData.gameMap.getConqueredCountries(playerId);

		if(allConqueredCountries.size() == 0) {
			return true;
		}

		return false;
	}


	/**
	 * Helper method to build a comprehensive map of all the possible fortification
	 * paths (for both immediate and extended neighbors) It has all the necessary
	 * checks and validation to ensure the path includes only countries owned by the
	 * given current player in the turn. Moreover, ensures that candidates suggested
	 * as "source countries" to move armies from satisfy the minimal requirements of
	 * army presence on the ground.
	 *
	 * @return Hashmap of all potential fortification scenarios for player.
	 */
	public HashMap<String, ArrayList<String>> getFortificationScenarios() {


		HashMap<String, ArrayList<String>> preliminaryScenarios = new HashMap<String, ArrayList<String>>();

		HashMap<String, ArrayList<String>> allScenarios = new HashMap<String, ArrayList<String>>();

		// Step 1: get the comprehensive list of all countries currently conquered by
		// the player
		HashSet<String> poolOfPotentialCountries = new HashSet<String>();
		poolOfPotentialCountries = this.gameData.gameMap.getConqueredCountries(playerID);

		// Step 2: draw preliminary paths - irrespective of army counts & extended
		// neighbors
		for (String potentialSourceCountry : poolOfPotentialCountries) {
			buildFortificationPath(preliminaryScenarios, potentialSourceCountry);
		}

		if (preliminaryScenarios.isEmpty()) {
			return null;
		}

		// Before we return the set, we have to slightly manipulate it
		// to ensure the only keys featured are valid source countries which meet the
		// min requirements (at least 2)
		for (String country : preliminaryScenarios.keySet()) {
			if (this.gameData.gameMap.getCountry(country).getCountryArmyCount() < 2) {
				continue;
			}
			for (String cDestinationCountry : preliminaryScenarios.get(country)) {
				if (!cDestinationCountry.equalsIgnoreCase(country)) {
					allScenarios.putIfAbsent(country, new ArrayList<String>());
					allScenarios.get(country).add(cDestinationCountry);
				}
			}
		}

		// in case all countries conquered by player have 1 army count
		if (allScenarios.isEmpty()) {
			return null;
		}

		return allScenarios;
	}

	/**
	 *Helper method to build a comprehensive map of all the possible fortification
	 *paths (for both immediate and extended neighbors)
	 * @return Reversed the source and destination from getFortificationScenarios.
	 */
	HashMap<String,ArrayList<String>> getReversedSenario() {
		HashMap<String, ArrayList<String>> potentialScenarios = getFortificationScenarios();
		HashMap<String, ArrayList<String>> reversedScenarios = new HashMap<>();
		if (potentialScenarios == null || getFortificationScenarios().isEmpty()) {
			return new HashMap<>();
		} else {
			for (String key : potentialScenarios.keySet()) {
				for (String country : potentialScenarios.get(key)) {
					reversedScenarios.putIfAbsent(country, new ArrayList<>());
					reversedScenarios.get(country).add(key);
				}
			}
			if (reversedScenarios.isEmpty()) {
				return new HashMap<>();
			}
			return reversedScenarios;
		}
	}

	/**
	 * Small helper method to ensure countries in scope are recursively traversed
	 * and included in the "path" This is NOT the final and full picture for
	 * fortification scenarios, it is merely a stepping stone. The main
	 * getFortificationScenarios method eliminates the invalid source
	 * candidates based on army counts
	 *
	 * @param fortificationScenarios a HashStructure to be populated
	 * @param rootCountry            country to be checked for adjacency
	 */
	protected void buildFortificationPath(HashMap<String, ArrayList<String>> fortificationScenarios, String rootCountry) {
		ArrayList<String> longestPathFromRoot = new ArrayList<String>();
		longestPathFromRoot.add(rootCountry);
		traverseNeighbouringCountries(longestPathFromRoot, rootCountry);
		fortificationScenarios.put(rootCountry, longestPathFromRoot);
	}

	/**
	 * Small helper method to recursively traverse all countries owned by player and
	 * build a steady path starting from the root country passed in.
	 *
	 *
	 * @param longestConqueredPathFromRoot to be drawn based on adjacency and
	 *                                     country ownership
	 * @param rootCountry                  country to serve as root of search
	 */
	protected void traverseNeighbouringCountries(ArrayList<String> longestConqueredPathFromRoot, String rootCountry) {

		HashSet<String> adjacentCountries = new HashSet<String>();
		adjacentCountries = this.gameData.gameMap.getAdjacentCountries(rootCountry);
		for (String adjacentCountry : adjacentCountries) {
			// ensure adjacent country also owned by same player - otherwise no path
			// to/through it
			if (this.gameData.gameMap.getCountry(adjacentCountry).getCountryConquerorID() == playerID
					&& !longestConqueredPathFromRoot.contains(adjacentCountry)) {
				longestConqueredPathFromRoot.add(adjacentCountry);
				traverseNeighbouringCountries(longestConqueredPathFromRoot, adjacentCountry);
			}
		}
	}

	/**
	 * Getter of the player name.
	 *
	 * @return the player name.
	 */
	public String getPlayerName() {
		return this.playerName;
	}

	/**
	 * Getter of the player id.
	 *
	 * @return the player id.
	 */
	public Integer getPlayerID() {
		return this.playerID;
	}

	/**
	 * Getter of the order to play.
	 *
	 * @return the order of playe.
	 */
	public Integer getOrderOfPlay() {
		return orderOfPlay;
	}

	/**
	 * Setter of the order of play.
	 *
	 * @param orderOfPlay order of play.
	 */
	public void setOrderOfPlay(Integer orderOfPlay) {
		this.orderOfPlay = orderOfPlay;
	}

	/**
	 * Setter for card exchange army count.
	 */
	public static void setCardExchangeArmyCount() {
		PlayerStrategy.cardExchangeArmyCount += 5;
	}

	/**
	 * Getter for cardExchangeArmyCount which holds the card exchange army count.
	 *
	 * @return the card exchange army count.
	 */
	public static int getCardExchangeArmyCount() {
		return cardExchangeArmyCount;
	}

	/**
	 * Getter for cardList.
	 *
	 * @return the card list
	 */
	public ArrayList<Card> getPlayerCardList() {
		return cardList;
	}

	/**
	 * Adds a player to a card list.
	 *
	 * @param card to be added.
	 */
	public void addToPlayerCardList(Card card) {
		this.cardList.add(card);
	}

	/**
	 * Removes the player from the card list.
	 *
	 * @param card to be removed.
	 */
	public void removeFromPlayerCardList(Card card) {
		this.cardList.remove(card);
	}

	/**
	 * Acts as previous turn constructor to allow acess to the gamedata's data
	 *
	 * @param gamedata gamedata objecto to set.
	 */
	public void setGameData(GameData gamedata) {
		this.gameData = gamedata;
	}

	/**
	 * Helper method to test if a given strin can be converted to a int.
	 *
	 * @param stringInput determines if the string typed by user is an integer
	 * @return the evaluation of true if it is an integer or false otherwise
	 */
	protected boolean isNaN(final String stringInput) {
		try {
			Integer.parseInt(stringInput);

		} catch (final Exception e) {

			System.out.println("Invalid Input");
			return true;
		}
		return false;
	}

	/**
	 * Getter for Attack Phase State to show in the phase view.
	 *
	 * @return the attack phase states to be showed in phase view.
	 */
	public ArrayList<AttackPhaseState> getAttackPhaseState() {
		return attackPhaseState;
	}

	/**
	 * Getter for Reinforcement Phase State to show in the phase view.
	 *
	 * @return the reinforcement phase states to be showed in phase view.
	 */
	public ArrayList<ReinforcementPhaseState> getReinforcementPhaseState() {
		return reinforcementPhaseState;
	}

	/**
	 * Getter for Fortification Phase State to show in the phase view.
	 *
	 * @return the Fortification phase states to be showed in phase view.
	 */
	public ArrayList<FortificationPhaseState> getFortificationPhaseState() {
		return fortificationPhaseState;
	}

	/**
	 * Starts the reinforcement phase by getting valid cards and calculating the
	 * number of armies.
	 */
	public abstract void executeReinforcement();
	
	/**
	 * This method depends on each strategy, in this case how the player is going to put
	 * the army after the calculation of the armies during reinforcement.
	 * 
	 * @param reinforcementArmy number of army to place
	 */
	public abstract void placeArmy(Integer reinforcementArmy);
	
	/**
	 * Method to begin attack.
	 */
	public abstract void executeAttack();
	
	/**
	 * Depends on the strategy of each player, wheter the input comes from the human or bot.
	 * 
	 * @param attackScenarios array of possible attack countries to attack from.
	 * @return the country choosed by the player whether is a human or bot.
	 */
	public abstract String getCountryToAttackFrom(HashMap<String, ArrayList<String>> attackScenarios);
	
	/**
	 * Depends on the strategy of each player, it receives the selected country to attack from and
	 * the method accepts a valid country to attack to.
	 * 
	 * @param selectedSourceCountry country to attack from.
	 * @param attackScenarios every attack scenario.
	 * 
	 * @return the selected country to attack to, whether a human or bot.
	 */
	public abstract String getEnemyCountryToAttack(String selectedSourceCountry, HashMap<String, ArrayList<String>> attackScenarios);
	
	/**
	 * Gets the number of allowed number of dice base on the action ("attack" or "defend")
	 * 
	 * @param player who is going to roll the dice.
	 * @param country the country where the player is attacking or defending.
	 * @param action ("attack" or "defend")
	 * @return the number of dices to roll.
	 */
	public abstract Integer getDesiredDiceCountFromPlayer(String player, String country, String action);
	
	/**
	 * After conquering a country the player can decide how many armies are going to be moved to the conquered country.
	 * 
	 * @param selectedSourceCountry selected country.
	 * @return the number of armies to be moved.
	 */
	public abstract Integer getNumberofArmiesAttackerWantsToMove(String selectedSourceCountry);

	/**
	 * Method to guide the player through various fortification options when
	 * applicable.
	 */
	public abstract void executeFortification();
}

