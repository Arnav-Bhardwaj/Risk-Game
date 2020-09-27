package com.java.model.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.java.model.cards.Card;
import com.java.model.map.Country;

/**
 * This class is a player strategy that is created startuphase.java and 
 * is a subclass of player strategy, all the methods are called from player(context class)
 * the methods called are reinforce - doubled the number of armies on all countries owned
 * fortify- doubles army for country that is surrounded by the enemies and attack will automatically own the country
 * the neighbours.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 3.0.0
 */
public class CheaterMode extends PlayerStrategy{

	/**
	 * Creates a new cheater player.
	 * 
	 * @param playerID the player id.
	 * @param playerName the player name.
	 */
    public CheaterMode(Integer playerID, String playerName) {
        super(playerID,playerName);
    }
    
    /**
     * Executes reinforcement, cheater player will double its armies in every country.
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
        placeArmy(0);
    }
    
    /**
     * Place the armies in every country owned by cheater, basically is adding the double to every country.
     */
    @Override
    public void placeArmy(Integer reinforcementArmy) {
    	
    	Integer currentPlayerID = playerID;
    	
        HashSet<String> countriesOwned = this.gameData.gameMap.getConqueredCountries(currentPlayerID);

        System.out.println();
        System.out.println("**** Reinforcement Phase Begins for player " + this.playerName + "..****\n");
        System.out.print(playerName + "'s Total Reinforcement Army Count Remaining -> [" + String.valueOf(reinforcementArmy) + "]\n");
        
        /* Information about the countries owned by the player and enemy countries. */
        for (String countries : countriesOwned) {
        	
        	System.out.println("\nCountry owned by " + playerName + "-> " + countries + " & Army Count: " + this.gameData.gameMap.getCountry(countries).getCountryArmyCount());

            HashSet<String> adjCountries = this.gameData.gameMap.getAdjacentCountries(countries);
            
            if (adjCountries.isEmpty()){
            	System.out.println("No neighboring enemy country for country " + countries);
            }

            for (String enemyCountries : adjCountries) {
            	if (this.gameData.gameMap.getCountry(enemyCountries).getCountryConquerorID() != currentPlayerID) {
            		System.out.println("Neighboring Enemy country name: " + enemyCountries + " & Army Count: " + this.gameData.gameMap.getCountry(enemyCountries).getCountryArmyCount());
            	}
            }
        }
        
        Country country;
        Integer numberOfArmiesToBePlaced;
        ReinforcementPhaseState reinforcementPhase;
            
        for (String countries : countriesOwned) {
        	
        	country = gameData.gameMap.getCountry(countries);
        	numberOfArmiesToBePlaced = country.getCountryArmyCount();
            this.gameData.gameMap.addArmyToCountry(countries, numberOfArmiesToBePlaced);
            
            reinforcementPhase = new ReinforcementPhaseState();
            reinforcementPhase.setToCountry(countries);
            reinforcementPhase.setNumberOfArmiesPlaced(numberOfArmiesToBePlaced);
            reinforcementPhaseState.add(reinforcementPhase);
            notifyView();
        }
        
        /* End of reinforcement phase, Print the final overview. */
        System.out.println("\nReinforcement Phase is now complete. Here's an overview: \n\n");
        for (String countries : countriesOwned) {
            System.out.println("Country owned by you: " + countries + " ,Army Count: " + this.gameData.gameMap.getCountry(countries).getCountryArmyCount());
        }
        reinforcementPhaseState.clear();
        System.out.println("\n**** Reinforcement Phase Ends for player " + this.playerName + "..****\n");
    }
    
    /**
     * Executes attack, cheater player will conquer every neighboring country whitout rolling any dice.
     */
    @Override
    public void executeAttack() {
        System.out.println();
        System.out.println("**** Attack Phase Begins for player " + this.playerName + "..****\n");

        Boolean hasConnqueredAtleastOneCountry = true;

		AttackPhaseState attackPhaseState = new AttackPhaseState();
		attackPhaseState.setAttackingPlayer(this.playerName);
		this.attackPhaseState.add(attackPhaseState);
		notifyView();

		// Now fetch all attack possibilities for player
		System.out.println("\n" + "Fetching potential attack scenarios for " + this.playerName + "...\n");

		// K = my source country , V = list of other countries that i dont own ( as
		// nebiours)
		HashMap<String, ArrayList<String>> attackScenarios = getPotentialAttackScenarios();

		if (attackScenarios.isEmpty()) {
			System.out.println("There are currently no attack opportunities for " + this.playerName + ".. Sorry!\n");
		}

		// show PlayerStrategy all the options they have
		showAllAttackScenarios(attackScenarios);
		System.out.println("\nCheater is getting all countries");
		// TODO Now chose the country to attack from and in a loop conqure all the
		// nebiours
		for (Map.Entry<String, ArrayList<String>> currCountry : attackScenarios.entrySet()) {

			// get the arraylist here for that player at "i"
			ArrayList<String> attackingCountries = currCountry.getValue();

			// now iterate over each of the attacking countries
			for (String eachattackingCountry : attackingCountries) {

				// get the country object
				Country countryObject = gameData.gameMap.getCountry(eachattackingCountry);

				String defendingPlayerName = gameData.getPlayer(countryObject.getCountryConquerorID()).getStrategyType()
						.getPlayerName();

				attackPhaseState.setDefendingPlayer(defendingPlayerName);
				this.notifyView();

				attackPhaseState.setAttackingCountry(countryObject.getCountryName());
				this.notifyView();

				// change the country conqurer to the current player's lit of countries owned
				Integer oldConquererPlayerId = countryObject.getCountryConquerorID();
				Integer newConquererPlayerId = this.playerID;
				
				if(oldConquererPlayerId != newConquererPlayerId) {
					this.gameData.gameMap.updateCountryConquerer(eachattackingCountry, oldConquererPlayerId,newConquererPlayerId);
					this.gameData.gameMap.calculateNumberOfArmiesPerPlayer();
					System.out.println("\nCheater " + getPlayerName() + " is conquering " + eachattackingCountry + "  from " + defendingPlayerName);
				}
			}
		}

		checkIfPlayerHasConqueredTheWorld();

        if (hasConnqueredAtleastOneCountry) {
             Card card = gameData.cardsDeck.getCard();
             this.cardList.add(card);
             System.out.println("PlayerStrategy received 1 card => Army Type: " + card.getArmyType() + ", Country: " + card.getCountry().getCountryName());
             System.out.println("Total cards : " + this.cardList.size());
        }

        endAttack();
    }
    
    /**
     * Executes fortification, cheater will double its armies in every country which all neighbors belong to other player.
     */
    @Override
    public void executeFortification() {
        System.out.println();
        System.out.println("**** Fortification Phase Begins for player " + this.playerName + "..****\n");

        FortificationPhaseState fortificationPhase = new FortificationPhaseState();
        fortificationPhaseState.add(fortificationPhase);
        notifyView();

        // First get confirmation from the player that fortification is desired.

        System.out.println("\n" + "I am a Cheater! , Going to double all my arimies where I dont own any neighbour countries " + this.playerName + "...\n");

        //get list of countries owned by player
        HashSet<String> countriesOwned = gameData.gameMap.getConqueredCountries(playerID);

        HashSet<String> adacentCountries;

        boolean isCountryBelongsToMe;

        for(String entry : countriesOwned) {
        	isCountryBelongsToMe = false;
            Country belongsToPlayer = new Country();
            // then check if thoes countries nebouires are nont owned by me
             adacentCountries = gameData.gameMap.getAdjacentCountries(entry);

             for(String adjEntry: adacentCountries){
                 // check if the countries are not owned by me the player
                  belongsToPlayer = gameData.gameMap.getCountry(adjEntry);

                 if(belongsToPlayer.getCountryConquerorID() == playerID){
                     isCountryBelongsToMe = true;
                     break;
                 }
             }

             // when it is not in the list I own just double the army amount.
             if(isCountryBelongsToMe == false){
            	 Integer numberOfCheatedArmies = gameData.gameMap.getCountry(entry).getCountryArmyCount();
                 gameData.gameMap.addArmyToCountry(entry,numberOfCheatedArmies);
                 
                 fortificationPhase = new FortificationPhaseState();
                 fortificationPhase.setFromCountry(belongsToPlayer.getCountryName());
                 fortificationPhase.setToCountry(" I am Cheating, :) ");
                 fortificationPhase.setNumberOfArmiesMoved(numberOfCheatedArmies);

                 fortificationPhaseState.add(fortificationPhase);

                 notifyView();
             }

        }

        System.out.println("\n****Fortification Phase Ends for player " + this.playerName + "..****\n");

        fortificationPhaseState.clear();
    }

    /**
     * Since this is a bot strategy there is no imput from the user.
     */
    @Override
    public String getCountryToAttackFrom(HashMap<String, ArrayList<String>> attackScenarios) {
        return null; // not needed since each country will be automatically be attacked from
    }
    
    /**
     * Since this is a bot strategy there is no imput from the user.
     */
    @Override
    public String getEnemyCountryToAttack(String selectedSourceCountry, HashMap<String, ArrayList<String>> attackScenarios) {
        return null; // Not needed
    }
    
    /**
     * Since this is a bot strategy there is no imput from the user.
     */
    @Override
    public Integer getDesiredDiceCountFromPlayer(String player, String country, String action) {
        return null;
    }
    
    /**
     * Since this is a bot strategy there is no imput from the user.
     */
    @Override
    public Integer getNumberofArmiesAttackerWantsToMove(String selectedSourceCountry) {
        return null;
    }

}
