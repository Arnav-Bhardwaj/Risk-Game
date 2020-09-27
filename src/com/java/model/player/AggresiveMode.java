package com.java.model.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.java.model.cards.Card;

//import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

/**
 * This class is a player strategy that is created in startuphase.java and
 * is a subclass of player strategy, all the methods are called from player(context class)
 * the methods called are 
 * 
 * reinforce - place armies in the strongest country.
 * attack - always attack all out mode.
 * fortify - in order to maximize aggregation of forces in one country.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 3.0.0
 */
public class AggresiveMode extends PlayerStrategy {
	
	/**
	 * Creates a new aggressive player.
	 * 
	 * @param playerID the player id.
	 * @param playerName the player name.
	 */
    public AggresiveMode(Integer playerID, String playerName) {

        super(playerID,playerName);

    }

    /**
     * Executes aggressive reinforcement, placing armies in the strongest country
     */
    @Override
    public void executeReinforcement() {
        notifyView();

        Integer getReinforcementCountFromCards = getReinforcementCountFromValidCardsAI();
        Integer totalReinforcementArmyCount = getReinforcementCountFromCards + calculateReinforcementArmy();
        ReinforcementPhaseState reinforcementPhase = new ReinforcementPhaseState();
        reinforcementPhase.setNumberOfArmiesReceived(totalReinforcementArmyCount);

        reinforcementPhaseState.add(reinforcementPhase);

        notifyView();
        placeArmy(totalReinforcementArmyCount);

    }

    /**
     * Method to place armies.
     * @param reinforcementArmy the number of armies to be reinforced
     */
    @Override
    public void placeArmy(Integer reinforcementArmy) {

        Integer currentPlayerID = playerID;
        HashSet<String> conqueredCountryByThisPlayer = gameData.gameMap.getConqueredCountries(currentPlayerID);

        System.out.println();
        System.out.println("**** Reinforcement Phase Begins for player " + this.playerName + "..****\n");

        String strongestCountry = "";
        Integer strongestCountryArmyCount = Integer.MIN_VALUE;
        Integer currentCountryArmyCount = 0;
        for(String country: conqueredCountryByThisPlayer){
            currentCountryArmyCount = gameData.gameMap.getCountry(country).getCountryArmyCount();
            if(strongestCountryArmyCount < currentCountryArmyCount){
                strongestCountry = country;
                strongestCountryArmyCount = currentCountryArmyCount;
            }
        }
        gameData.gameMap.getCountry(strongestCountry).addArmy(reinforcementArmy);
        System.out.println("\nReinforcement is done for player "+playerName+". Here is an overview. \n");
        for(String country: conqueredCountryByThisPlayer){
            System.out.println("Country: "+country+", Army Count: "+gameData.gameMap.getCountry(country).getCountryArmyCount());
        }
    }

    /**
     * Executes aggressive attack, always attack with strongest country in all out mode.
     * 
     */
    @SuppressWarnings("unchecked")
	@Override
    public void executeAttack() {
    	
    	System.out.println();
        System.out.println("**** Attack Phase Begins for player " + this.playerName + "..****\n");
        
        boolean hasConnqueredAtleastOneCountry = false;
        
	    System.out.println("\n" + "Fetching potential attack scenarios for " + this.playerName + "...\n");
	        
	    // get all scenarios but we're only interested in attacking with the strongest country & in all-out mode
	    HashMap<String, ArrayList<String>> potentialAttackScenarios = getPotentialAttackScenarios();
	        
	    if (potentialAttackScenarios == null) {
	        System.out.println(
	                "There are currently no attack opportunities for " + this.playerName + ".. Sorry!\n");
	        System.out.println("\n****Attack Phase Ends for player " + this.playerName + "..****\n");
	        return;
	     }
	
	    if (potentialAttackScenarios.isEmpty()) {
	        System.out.println(
	                "There are currently no attack opportunities for " + this.playerName + ".. Sorry!\n");
	        System.out.println("\n****Attack Phase Ends for player " + this.playerName + "..****\n");
	        return;
	    }
	        
	   	        
	    String enemyCountryToAttack = null;
	    String strongestCountry = getStrongestCountryConqueredByPlayer(potentialAttackScenarios);
	    if(strongestCountry == null) {
	    	System.out.println(
	                   "There are currently no attack opportunities for " + this.playerName + ".. Sorry!\n");
	        System.out.println("\n****Attack Phase Ends for player " + this.playerName + "..****\n");
	        return;
	    }
	          
	    
        // implement a mini-strategy within this aggressive strategy to target the weakest enemy country first
	    // build map of all enemy countries and their respective army counts
	    ArrayList<String> allAdjacentEnemyCountries = potentialAttackScenarios.get(strongestCountry); 
	    HashMap<String,Integer> allAdjacentEnemyCountriesAndArmyCounts = new HashMap<String,Integer>();
	    
	    for(String enemyCountry: allAdjacentEnemyCountries) {
        	Integer currentEnemyCountryArmyCount = gameData.gameMap.getCountry(enemyCountry).getCountryArmyCount();
        	allAdjacentEnemyCountriesAndArmyCounts.putIfAbsent(enemyCountry, currentEnemyCountryArmyCount);
	    }
	    
	    // sort in ascending order based on lowest army count -> highest
	    Object[] sortedAdjacentEnemyCountriesAndArmyCounts = allAdjacentEnemyCountriesAndArmyCounts.entrySet().toArray();
	    Arrays.sort(sortedAdjacentEnemyCountriesAndArmyCounts, (o1, o2) -> ((Map.Entry<String, Integer>) o1).getValue()
		           .compareTo(((Map.Entry<String, Integer>) o2).getValue()));
	        

	    HashSet<String> countriesConquered = new HashSet<>();
	    
	    // attack each surrounding enemy country while you can
	    for (Object e : sortedAdjacentEnemyCountriesAndArmyCounts) {

	    	// setup phase state
	    	AttackPhaseState attackPhase = new AttackPhaseState();
	 	    attackPhase.setAttackingPlayer(this.playerName);
	 	    attackPhaseState.add(attackPhase);
	 	    notifyView();
	 	    // attacking country will be the same for a given turn
	 	    attackPhase.setAttackingCountry(strongestCountry);
	        notifyView();
	    	
	    	enemyCountryToAttack = ((Map.Entry<String, Integer>) e).getKey(); 
	    	attackPhase.setDefendingCountry(enemyCountryToAttack);
	    	notifyView();
	
	    	String defendingPlayer = gameData
	            .getPlayer(this.gameData.gameMap.getCountry(enemyCountryToAttack).getCountryConquerorID())
	            .getStrategyType().getPlayerName();
	    	attackPhase.setDefendingPlayer(defendingPlayer);
	    	notifyView();
	        
	    	// fight in all out mode until you win or run out of armies on the ground
		    while (!attackPhase.getBattleOutcomeFlag() && this.gameData.gameMap.getCountry(strongestCountry).getCountryArmyCount() > 1) {
	            // proceed with max allowed dice count for both sides
	            Integer attackerDiceCount = getActualMaxAllowedDiceCountForAction("attack", strongestCountry, 3);
		        attackPhase.setAttackerDiceCount(attackerDiceCount);
		        //Integer defenderDiceCount = getActualMaxAllowedDiceCountForAction("defend",enemyCountryToAttack, 2);
		        // always set defender dice count to the min to maximize aggression 
		        Integer defenderDiceCount = 1;
		        attackPhase.setDefenderDiceCount(defenderDiceCount);
		        rollDiceBattle(attackPhase);
	            hasConnqueredAtleastOneCountry = fight(attackPhase) || hasConnqueredAtleastOneCountry;
	            
	            if(attackPhase.getBattleOutcomeFlag()) {
	            	countriesConquered.add(strongestCountry);
	            	countriesConquered.add(enemyCountryToAttack);
	            	
	            }
	            
	        }
		    
	    }
	    
	    String weakestCountry = getCountryWithMostNumberOfBordersShared(countriesConquered);
	    
	    if(weakestCountry != strongestCountry) {
	    	
	    	Integer strongestCountryArmyCount = this.gameData.gameMap.getCountry(strongestCountry).getCountryArmyCount();
	    	
	    	if(strongestCountryArmyCount > 1) {
	    		this.gameData.gameMap.deductArmyToCountry(strongestCountry, strongestCountryArmyCount - 1);
				this.gameData.gameMap.addArmyToCountry(weakestCountry, strongestCountryArmyCount - 1);
	    	}
	    	
	    }
	    
        checkIfPlayerHasConqueredTheWorld();
    
		
	    if (hasConnqueredAtleastOneCountry) {
		   	Card card = gameData.cardsDeck.getCard();
	            
		    if(card == null) {
		      	System.out.println("No more cards left in the deck");
		    } else {
		   		this.cardList.add(card);
		   		System.out.println("PlayerStrategy received 1 card => Army Type: " + card.getArmyType() + ", Country: " + card.getCountry().getCountryName());
		   		System.out.println("Total cards : " + this.cardList.size());
		   	}
		}   
	    
	    HashSet<String> conqueredCountryByThisPlayer = gameData.gameMap.getConqueredCountries(playerID);
        System.out.println("\nOverview of army counts: \n");
        for(String country: conqueredCountryByThisPlayer){
            System.out.println("Country: "+country+", Army Count: "+gameData.gameMap.getCountry(country).getCountryArmyCount());
        }
	        
	    endAttack();
    }
    
    /** 
     * Helper method to assist aggressive attack logic with shifting power according 
     * to neighboring threats (and attack potentials)
     * 
     * @param countriesConquered
     * @return weakestCountry
     */  
    private String getCountryWithMostNumberOfBordersShared(HashSet<String> countriesConquered) {
		String weakestCountry = "";
		Integer maxNeighbours = Integer.MIN_VALUE;
		
		for(String country : countriesConquered) {
			Integer enemyNeighbours = 0;
			for(String adjacentCountry : this.gameData.gameMap.getAdjacentCountries(country)) {
				if(this.gameData.gameMap.getCountry(adjacentCountry).getCountryConquerorID() != this.playerID) {
					enemyNeighbours++;
				}
			}
			
			if(enemyNeighbours > maxNeighbours) {
				maxNeighbours = enemyNeighbours;
				weakestCountry = country;
			}
			
		}
		return weakestCountry;
		
	}

	/**
     * Executes aggressive fortification, in order to maximize aggregation of forces in one country.
     */
    @Override
    public void executeFortification() {
    	
    	System.out.println();
        System.out.println("**** Fortification Phase Begins for player " + this.playerName + "..****\n");
        FortificationPhaseState fortificationPhase = new FortificationPhaseState();
        fortificationPhaseState.add(fortificationPhase);
        notifyView();

        System.out.println("\n" + "Fetching potential fortification scenarios for " + this.playerName + "...\n");
        HashMap<String, ArrayList<String>> potentialFortificationScenarios = getFortificationScenarios();
        
        if (potentialFortificationScenarios == null) {
            System.out.println(
                    "There are currently no fortification opportunities for " + this.playerName + ".. Sorry!\n");
            System.out.println("\n****Fortification Phase Ends for player " + this.playerName + "..****\n");
            return;
        }

        // if the list is empty or only contains a single scenario means we don't have anything to check/do 
        // that's because the hashmap of scenarios is keyed on source country to fortify from
        // and if there's only 1 scenario in total, means other countries only have 1 army on the ground and cant help
        if (potentialFortificationScenarios.isEmpty() || potentialFortificationScenarios.size() == 1) {
            System.out.println(
                    "There are currently no 'aggressive' fortification opportunities for " + this.playerName + ".. Sorry!\n");
            System.out.println("\n****Fortification Phase Ends for player " + this.playerName + "..****\n");
            return;
        }
        
        HashMap<String, ArrayList<String>> topPotentialFortificationScenarios = new HashMap<String, ArrayList<String>>();
        
		for (String keySourceCountry : potentialFortificationScenarios.keySet()) {        
			for (String correspondingDestinationCountry : potentialFortificationScenarios.get(keySourceCountry)) {
				if(potentialFortificationScenarios.containsKey(correspondingDestinationCountry)) {
					topPotentialFortificationScenarios.putIfAbsent(keySourceCountry, new ArrayList<String>());
					topPotentialFortificationScenarios.get(keySourceCountry).add(correspondingDestinationCountry);
				}
			}
		}
		
        if (topPotentialFortificationScenarios.isEmpty() || topPotentialFortificationScenarios.size() == 1) {
            System.out.println(
                    "There are currently no 'aggressive' fortification opportunities for " + this.playerName + ".. Sorry!\n");
            System.out.println("\n****Fortification Phase Ends for player " + this.playerName + "..****\n");
            return;
        }
		
		String strongestCountry = getStrongestCountryConqueredByPlayer(topPotentialFortificationScenarios);
        String secondStrongestCountry = getSecondStrongestCountryConqueredByPlayer(topPotentialFortificationScenarios,strongestCountry);

        Integer maxNoOfArmiesToMove = gameData.gameMap.getCountry(secondStrongestCountry).getCountryArmyCount() - 1;

        // move armies based on determined source - destination countries per startegy logic
        gameData.gameMap.getCountry(secondStrongestCountry).deductArmy(maxNoOfArmiesToMove);
        gameData.gameMap.getCountry(strongestCountry).addArmy(maxNoOfArmiesToMove);
        
        fortificationPhase.setFromCountry(secondStrongestCountry);
        fortificationPhase.setToCountry(strongestCountry);
        fortificationPhase.setNumberOfArmiesMoved(maxNoOfArmiesToMove);

        fortificationPhaseState.add(fortificationPhase);

        notifyView();
        
        HashSet<String> conqueredCountryByThisPlayer = gameData.gameMap.getConqueredCountries(playerID);
        System.out.println("Moved "+maxNoOfArmiesToMove+" armies from "+secondStrongestCountry+" to "+strongestCountry);
        System.out.println("\nAn overview after Fortification.\n");
        for(String country: conqueredCountryByThisPlayer){
            System.out.println("Country: "+country+", Army Count: "+gameData.gameMap.getCountry(country).getCountryArmyCount());
        }

    }
        
    /**	
     * This helper method is utilized by all phases to return,
     * at any given point in time, the strongest country 
     * currently conquered by the player
     * @param potentialScenarios potentical attack scenario
     * @return strongestCountry the strongest country
     */
    public String getStrongestCountryConqueredByPlayer(HashMap<String, ArrayList<String>> potentialScenarios) {
		
    	String strongestCountry = null;
    	Integer maxArmyCountEncountered = 1;
        
        // find the strongest country conquered by the player, knowing it would be contained as a key 
        for (String country : potentialScenarios.keySet()){
        	Integer currentCountryArmyCount = gameData.gameMap.getCountry(country).getCountryArmyCount();
        	if (currentCountryArmyCount > maxArmyCountEncountered) {
        		strongestCountry = country;
        		maxArmyCountEncountered = currentCountryArmyCount;
        	}        
        }
        
    	return strongestCountry;
    }
    

    /**	
     * This helper method is utilized by fortification to return,
     * given the strongest country to be fortified, 
     * the second strongest country which should serve as a supplier
     * @param  potentialScenarios the potential attack scenarios
     * @param  strongestCountry the strongest country
     * @return secondStrongestCountry the second strongest country
     */
    public String getSecondStrongestCountryConqueredByPlayer(HashMap<String, ArrayList<String>> potentialScenarios, String strongestCountry) {
		
    	String secondStrongestCountry = null;
    	Integer maxArmyCountEncountered = 1;
        
        // find second strongest country conquered by the player based on value passed in as strongest 
    	for(String country: potentialScenarios.get(strongestCountry)) {
        	Integer currentCountryArmyCount = gameData.gameMap.getCountry(country).getCountryArmyCount();
        	// only countries with 2 or more armies on the ground qualify as suppliers
        	if (currentCountryArmyCount > maxArmyCountEncountered) {
        		secondStrongestCountry = country;
        		maxArmyCountEncountered = currentCountryArmyCount;
        	}
        }
        
    	return secondStrongestCountry;
    }
    
    /**
     * Since this is a bot strategy there is no input from the user.
     */
    @Override
    public String getCountryToAttackFrom(HashMap<String, ArrayList<String>> attackScenarios) {
        return null;
    }
    
    /**
     * Since this is a bot strategy there is no input from the user.
     */
    @Override
    public String getEnemyCountryToAttack(String selectedSourceCountry, HashMap<String, ArrayList<String>> attackScenarios) {
        return null;
    }
    
    /**
     * Since this is a bot strategy there is no input from the user.
     */
    @Override
    public Integer getDesiredDiceCountFromPlayer(String player, String country, String action) {
        return null;
    }
    
    /**
     * Since this is a bot strategy there is no input from the user.
     */
    @Override
    public Integer getNumberofArmiesAttackerWantsToMove(String selectedSourceCountry) {
        return null;
    }
    
    
}
