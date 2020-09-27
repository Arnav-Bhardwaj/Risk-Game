package com.java.model.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class is a player strategy that is created in startuphase.java and
 * is a subclass of player strategy, all the methods are called from player(context class)
 * the methods called are reinforce - place armies in the weakest country.
 * attack - never attacks any other player.
 * fortify- moves army to the weakest country from the strongest country possible in its path.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 3.0.0
 */
public class BenevolentMode extends PlayerStrategy {
	
	/**
	 * Creates a new cheater player.
	 * 
	 * @param playerID the player id.
	 * @param playerName the player name.
	 */
    public BenevolentMode(Integer playerID, String playerName) {

        super(playerID,playerName);

    }

    /**
     * Executes benevolent mode reinforcement, it places armies in the weakest country.
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
     * 
     * @param reinforcementArmy the number of armies to be reinforced
     */
    @Override
    public void placeArmy(Integer reinforcementArmy) {

        Integer currentPlayerID = playerID;
        HashSet<String> conqueredCountryByThisPlayer = gameData.gameMap.getConqueredCountries(currentPlayerID);

        System.out.println();
        System.out.println("**** Reinforcement Phase Begins for player " + this.playerName + "..****\n");

        while (reinforcementArmy > 0) {
             String weakestCountry = "";
             Integer weakestArmyCount = Integer.MIN_VALUE;
             for(String country: conqueredCountryByThisPlayer){

                Integer currentCountryArmyCount = gameData.gameMap.getCountry(country).getCountryArmyCount();

                HashSet<String> adjacentCountries = gameData.gameMap.getAdjacentCountries(country);
                Integer enemyArmyCount = 0;
                for(String adjCountry : adjacentCountries){
                    if(gameData.gameMap.getCountry(adjCountry).getCountryConquerorID() != currentPlayerID){
                        enemyArmyCount += gameData.gameMap.getCountry(adjCountry).getCountryArmyCount();
                    }
                }
                 if(weakestArmyCount < (enemyArmyCount - currentCountryArmyCount)){
                     weakestArmyCount = (enemyArmyCount - currentCountryArmyCount);
                     weakestCountry = country;
                 }
            }
             gameData.gameMap.getCountry(weakestCountry).addArmy(1);
             reinforcementArmy -= 1;
        }
        System.out.println("\nReinforcement is done for player "+playerName+". Here is an overview. \n");
        for(String country: conqueredCountryByThisPlayer){
            System.out.println("Country: "+country+", Army Count: "+gameData.gameMap.getCountry(country).getCountryArmyCount());
        }
    }

    /**
     * Method to execute benevolent attack, never attacks any other player.
     */
    @Override
    public void executeAttack() {

        AttackPhaseState attackPhaseState = new AttackPhaseState();
        attackPhaseState.setAttackingPlayer(this.playerName);
        this.attackPhaseState.add(attackPhaseState);
        notifyView();
        System.out.println("\n\n**** Attack Phase Begins for player " + this.playerName + "..****\n");
        attackPhaseState.setAttackingCountry("I do not attack. I am benevolent.");
        this.notifyView();
        System.out.println("Player "+playerName+" is Benevolent. The player never attacks");
    }

    /**
     * Method to execute benevolent fortification, moves army to the weakest country from the strongest country possible in its path.
     */
    @Override
    public void executeFortification() {

        System.out.println();
        System.out.println("**** Fortification Phase Begins for player " + this.playerName + "..****\n");
        FortificationPhaseState fortificationPhase = new FortificationPhaseState();
        fortificationPhaseState.add(fortificationPhase);
        notifyView();

        System.out.println("\n" + "Fetching potential fortification scenarios for " + this.playerName + "...\n");

        HashMap<String,ArrayList<String>> reversedPotentialFortificationScenarios = getReversedSenario();
        HashMap<String, ArrayList<String>> potentialFortificationScenarios = getFortificationScenarios();
        HashSet<String> potentialCountriesToBeFortified = new HashSet<>();

        if (potentialFortificationScenarios == null || potentialFortificationScenarios.isEmpty()) {
            System.out.println(
                    "There are currently no fortification opportunities for " + this.playerName + ".. Sorry!\n");
            System.out.println("\n****Fortification Phase Ends for player " + this.playerName + "..****\n");
            return;
        }

            for (String key : potentialFortificationScenarios.keySet()) {
                for (String country : potentialFortificationScenarios.get(key)) {
                    reversedPotentialFortificationScenarios.putIfAbsent(country, new ArrayList<>());
                    reversedPotentialFortificationScenarios.get(country).add(key);
                }
            }

            for (String country : potentialFortificationScenarios.keySet()) {
                ArrayList<String> countries = potentialFortificationScenarios.get(country);
                for (String cntry : countries) {
                    potentialCountriesToBeFortified.add(cntry);
                }
            }


        String weakestCountry = "";
        Integer weakerCountryArmyCount = 0;
        Integer currentPlayerID = playerID;

            for(String country : potentialCountriesToBeFortified){
                Integer currentCountryArmyCount = gameData.gameMap.getCountry(country).getCountryArmyCount();

                HashSet<String> adjacentCountries = gameData.gameMap.getAdjacentCountries(country);
                Integer enemyArmyCount = 0;
                for(String adjCountry : adjacentCountries){
                    if(gameData.gameMap.getCountry(adjCountry).getCountryConquerorID() != currentPlayerID){
                        enemyArmyCount += gameData.gameMap.getCountry(adjCountry).getCountryArmyCount();
                    }
                }
                if(weakerCountryArmyCount < (enemyArmyCount - currentCountryArmyCount)){
                    weakerCountryArmyCount = (enemyArmyCount - currentCountryArmyCount);
                    weakestCountry = country;
                }
            }

        ArrayList<String> potentialArmySuppliers = reversedPotentialFortificationScenarios.get(weakestCountry);
        Integer maximumArmy = 0;
        Integer currentArmyCount;
        String armySupplier = "";
        if(reversedPotentialFortificationScenarios.isEmpty() || reversedPotentialFortificationScenarios == null || weakestCountry.equals("")){
            System.out.println("No fortification Scenarios for player: "+playerName);
            return;
        }
        else {
            for (String country : potentialArmySuppliers) {
                currentArmyCount = ((gameData.gameMap.getCountry(country).getCountryArmyCount()) - 1);
                if (currentArmyCount > maximumArmy) {
                    maximumArmy = currentArmyCount;
                    armySupplier = country;
                }
            }
            gameData.gameMap.getCountry(armySupplier).deductArmy(maximumArmy);
            gameData.gameMap.getCountry(weakestCountry).addArmy(maximumArmy);
            fortificationPhase = new FortificationPhaseState();
            fortificationPhase.setFromCountry(armySupplier);
            fortificationPhase.setToCountry(weakestCountry);
            fortificationPhase.setNumberOfArmiesMoved(maximumArmy);

            fortificationPhaseState.add(fortificationPhase);

            notifyView();
            HashSet<String> conqueredCountryByThisPlayer = gameData.gameMap.getConqueredCountries(currentPlayerID);
            System.out.println("Moved " + maximumArmy + " armies from " + armySupplier + " to " + weakestCountry);
            System.out.println("\nAn overview after Fortification.\n");
            for (String country : conqueredCountryByThisPlayer) {
                System.out.println("Country: " + country + ", Army Count: " + gameData.gameMap.getCountry(country).getCountryArmyCount());
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
        return null;
    }
    
    /**
     * Since this is a bot strategy there is no imput from the user.
     */
    @Override
    public String getEnemyCountryToAttack(String selectedSourceCountry, HashMap<String, ArrayList<String>> attackScenarios) {
        return null;
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
