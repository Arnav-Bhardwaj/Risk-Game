package com.java.model.player;

import com.java.controller.dice.Dice;
import com.java.model.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * This class is a player strategy that is created startuphase.java and 
 * is a subclass of player strategy, all the methods are called from player(context class).
 * This strategy behaves as a human, it receives inputs from the user.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 3.0.0
 */
public class HumanMode extends PlayerStrategy implements Serializable {
    private static Scanner input = new Scanner(System.in);
    
    /**
	 * Creates a new human player.
	 * 
	 * @param playerID the player id.
	 * @param playerName the player name.
	 */
    public HumanMode(Integer playerID, String playerName) {
        super(playerID,playerName);
    }
    
    /**
     * Gets valid cards to trade.
     *
     * @return array of valid cards.
     */
    public ArrayList<Card> getValidCards() {
            ArrayList<Card> playerCardList = getPlayerCardList();
            ArrayList<Card> playerExchangeCards = new ArrayList<>();
            ArrayList<Card> cumulatedPlayerExchangeCards = new ArrayList<>();

            System.out.println("*** Cards in hand ***");
            this.showCards();
            String userInput = "no";
            if (playerCardList.size() > 2) {
                System.out.println("Do you wish to exchange cards ? (yes/no)");
                userInput = input.nextLine();
            } else {
                System.out.println(playerName + " does not have sufficient cards to trade.");
            }
            while (!((userInput.toLowerCase().equals("yes")) || (userInput.toLowerCase().equals("no")))) {
                System.out.println("Please input either yes or no.");
                userInput = input.nextLine();
            }

            while ((userInput.equals("yes")) || (userInput.equals("no") && playerCardList.size() > 4)) {
                if (userInput.equals("no") && (playerCardList.size() > 4)) {
                    System.out.println("You must exchange cards. You have more than 4 cards in your hand.");
                }
                boolean can_exchange = false;
                this.showCards();
                Integer cardNumber;

                if (!can_exchange) {
                    playerExchangeCards = new ArrayList<Card>();
                    System.out
                            .println("Please enter three card numbers from the list of the same or different army types.");
                    for (int i = 0; i < 3; i++) {
                        cardNumber = input.nextInt();
                        while (cardNumber >= playerCardList.size()) {
                            System.out.println("Please input correct number from list");
                            cardNumber = input.nextInt();
                        }
                        playerExchangeCards.add(playerCardList.get(cardNumber));
                    }
                    can_exchange = isValidExchange(playerExchangeCards);
                    if(!can_exchange){
                        System.out.println("You cannot trade these cards. You should enter three card numbers from the " +
                                "list of the same or different army types.");
                        System.out.println("*** Cards in hand ***");
                        this.showCards();
                        System.out.println("Do you wish to exchange cards ? (yes/no)");
                        userInput = input.nextLine();
                        userInput = input.nextLine();
                        while (!((userInput.toLowerCase().equals("yes")) || (userInput.toLowerCase().equals("no")))) {
                            System.out.println("Please input either yes or no.");
                            userInput = input.nextLine();
                        }
                        continue;
                    }
                }
                for (Card card : playerExchangeCards) {
                    cumulatedPlayerExchangeCards.add(card);
                    playerCardList.remove(card);
                }
                System.out.println("*** Cards in hand ***");
                this.showCards();

                System.out.println("Do you wish to exchange cards ? (yes/no)");
                userInput = input.nextLine();
                while (!((userInput.toLowerCase().equals("yes")) || (userInput.toLowerCase().equals("no")))) {
                    System.out.println("Please input either yes or no.");
                    userInput = input.nextLine();
                }
            }
            if ((userInput.equals("no")) && (playerCardList.size() > 2) && (cumulatedPlayerExchangeCards.size() == 0)) {
                System.out.println(playerName + " does not wish to exchange cards.");
            }
            return cumulatedPlayerExchangeCards;
        }

    /**
     * The executeAttack() method encompasses the overall attack phase logic and flow
     * including both single and all-out mode. Attack phase ends when player either
     * no longer wants to attack, or cannot attack.
     */
    @Override
    public void executeReinforcement() {
        notifyView();

        ArrayList<Card> playerExchangeCards;
        playerExchangeCards = getValidCards();
        Integer totalReinforcementArmyCount = calculateTotalReinforcement(playerExchangeCards);

        ReinforcementPhaseState reinforcementPhase = new ReinforcementPhaseState();
        reinforcementPhase.setNumberOfArmiesReceived(totalReinforcementArmyCount);

        reinforcementPhaseState.add(reinforcementPhase);

        notifyView();
        placeArmy(totalReinforcementArmyCount);
    }
    
    /**
     * Executes human mode attack.
     */
    @Override
    public void executeAttack() {
        System.out.println();
        System.out.println("**** Attack Phase Begins for player " + this.playerName + "..****\n");

        // implement an all-out mode
        boolean allOut = false;
        Boolean hasConnqueredAtleastOneCountry = false;

        while (gameOn) {

            AttackPhaseState attackPhase = new AttackPhaseState();
            attackPhase.setAttackingPlayer(this.playerName);
            attackPhaseState.add(attackPhase);
            notifyView();

            boolean wantToAttack = false;
            String playerDecision = "no";

            // First get confirmation from the player that attack is desired
            // unless player had specified "all out" mode

            System.out.println("\n Would you like to attack? (YES/NO)");
            if (input.hasNextLine()) {
                playerDecision = input.nextLine();
            }

            switch (playerDecision.toLowerCase()) {
                case "yes":
                case "yeah":
                case "y":
                case "sure":
                    wantToAttack = true;
                    break;
            }

            if (!wantToAttack) {
                System.out.println("\n" + this.playerName + "does not wish to attack..");
                attackPhaseState.clear();
                notifyView();
                break;
            }

            // Now fetch all attack possibilities for player
            System.out.println("\n" + "Fetching potential attack scenarios for " + this.playerName + "...\n");
            HashMap<String, ArrayList<String>> attackScenarios = getPotentialAttackScenarios();

            if (attackScenarios.isEmpty()) {
                System.out
                        .println("There are currently no attack opportunities for " + this.playerName + ".. Sorry!\n");
                break;
            }

            // show PlayerStrategy all the options they have
            showAllAttackScenarios(attackScenarios);
            String selectedSourceCountry = getCountryToAttackFrom(attackScenarios);
            attackPhase.setAttackingCountry(selectedSourceCountry);
            notifyView();

            String selectedDestinationCountry = getEnemyCountryToAttack(selectedSourceCountry, attackScenarios);
            attackPhase.setDefendingCountry(selectedDestinationCountry);
            notifyView();

            String defendingPlayer = gameData
                    .getPlayer(this.gameData.gameMap.getCountry(selectedDestinationCountry).getCountryConquerorID())
                    .getStrategyType().getPlayerName();
            attackPhase.setDefendingPlayer(defendingPlayer);
            notifyView();

            // Check if attacking player wants to "go all out"
            if (!allOut) {
                System.out.println("\n Would you like to go all out? (YES/NO)");
                if (input.hasNextLine()) {
                    playerDecision = input.nextLine();
                }

                switch (playerDecision.toLowerCase()) {
                    case "yes":
                    case "yeah":
                    case "y":
                    case "sure":
                        allOut = true;
                        break;
                }
            }

            // Based on what mode the attack is set to happen in, these will be determined
            // differently
            Integer selectedAttackerDiceCount = 1;
            Integer selectedDefenderDiceCount = 1;

            // attack once
            if (!allOut) {
                // prompt attacker and defender for dice count preferences
                selectedAttackerDiceCount = getDesiredDiceCountFromPlayer(this.playerName, selectedSourceCountry,"attack");
                attackPhase.setAttackerDiceCount(selectedAttackerDiceCount);
                notifyView();

                selectedDefenderDiceCount = getDesiredDiceCountFromPlayer(defendingPlayer, selectedDestinationCountry,"defend");
                attackPhase.setDefenderDiceCount(selectedDefenderDiceCount);
                notifyView();

                rollDiceBattle(attackPhase);

                hasConnqueredAtleastOneCountry = fight(attackPhase) || hasConnqueredAtleastOneCountry;
            }

            // or keep attacking if all-out mode & player still can attack & player still
            // hasn't conquered target
            while (allOut && !attackPhase.getBattleOutcomeFlag()) {
                if (this.gameData.gameMap.getCountry(selectedSourceCountry).getCountryArmyCount() > 1) {
                    // dont prompt players for input, proceed with max allowed dice count for both
                    // players
                    selectedAttackerDiceCount = getActualMaxAllowedDiceCountForAction("attack", selectedSourceCountry,
                            3);
                    attackPhase.setAttackerDiceCount(selectedAttackerDiceCount);
                    selectedDefenderDiceCount = getActualMaxAllowedDiceCountForAction("defend",
                            selectedDestinationCountry, 2);
                    attackPhase.setDefenderDiceCount(selectedDefenderDiceCount);
                    rollDiceBattle(attackPhase);
                    hasConnqueredAtleastOneCountry = fight(attackPhase) || hasConnqueredAtleastOneCountry;
                } else {
                    break;
                }
            }
            allOut = false;

            checkIfPlayerHasConqueredTheWorld();

        }

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

        endAttack();
    }
    
    /**
     * PlaceArmy method allows the player to position the armies in the player's
     * owned countries.
     *
     * @param reinforcementArmy total reinforcement army count to be placed by the
     *                          current player.
     */
    @Override
    public void placeArmy(Integer reinforcementArmy) {

        Integer currentPlayerID = playerID;
        HashSet<String> countriesOwned = this.gameData.gameMap.getConqueredCountries(currentPlayerID);

        System.out.println();
        System.out.println("**** Reinforcement Phase Begins for player " + this.playerName + "..****\n");

        while (reinforcementArmy > 0) {

            System.out.print(playerName + "'s Total Reinforcement Army Count Remaining -> ["
                    + String.valueOf(reinforcementArmy) + "]\n");

            /* Information about the countries owned by the player and enemy countries. */
            for (String countries : countriesOwned) {

                System.out.println("\nCountry owned by " + playerName + "-> " + countries + " & Army Count: "
                        + this.gameData.gameMap.getCountry(countries).getCountryArmyCount());

                HashSet<String> adjCountries = this.gameData.gameMap.getAdjacentCountries(countries);

                if (adjCountries.isEmpty()) {
                    System.out.println("No neighboring enemy country for country " + countries);
                }

                for (String enemyCountries : adjCountries) {
                    if (this.gameData.gameMap.getCountry(enemyCountries).getCountryConquerorID() != currentPlayerID) {
                        System.out.println("Neighboring Enemy country name: " + enemyCountries + " & Army Count: "
                                + this.gameData.gameMap.getCountry(enemyCountries).getCountryArmyCount());
                    }
                }
            }
            System.out.println("\nEnter the country name to place armies: ");
            String countryNameByUser = "";
            do{
                System.out.println("Please enter the country name to place armies: ");
                countryNameByUser = input.nextLine();
            }while (countryNameByUser.equals(""));


            /* Check for an invalid country name. */
            if (this.gameData.gameMap.getCountry(countryNameByUser) == null) {
                System.out.println("'" + countryNameByUser
                        + "' is an invalid country name. Please verify the country name from the list.\n\n");
                continue;
            }

            /*
             * Check for a valid country name, but the country belonging to a different
             * player.
             */
            if (this.gameData.gameMap.getCountry(countryNameByUser).getCountryConquerorID() != currentPlayerID) {
                System.out.println("'" + countryNameByUser
                        + "' does not belong to you yet!!. Please verify your countries owned from the list below.\n\n");
                continue;
            }

            /* Information about armies and placement of armies */
            System.out.println("Enter the number of armies to be placed, Remaining Army (" + reinforcementArmy + ") :");

            try {
                Integer numberOfArmiesToBePlacedByUser = Integer.parseInt(input.nextLine());

                if (numberOfArmiesToBePlacedByUser > reinforcementArmy) {
                    System.out.println("Input value '" + numberOfArmiesToBePlacedByUser
                            + "' should not be greater than the Total Reinforcement Army Count " + "("
                            + String.valueOf(reinforcementArmy) + ")\n\n");
                    continue;
                }

                if (!(numberOfArmiesToBePlacedByUser > 0)) {
                    System.out.println("Please input a value greater than 0.\n\n");
                    continue;
                }

                System.out.println("Successful...Country chosen " + countryNameByUser + " ,Number of armies placed: "
                        + numberOfArmiesToBePlacedByUser + "\n\n");

                this.gameData.gameMap.addArmyToCountry(countryNameByUser, numberOfArmiesToBePlacedByUser);
                reinforcementArmy -= numberOfArmiesToBePlacedByUser;

                ReinforcementPhaseState reinforcementPhase = new ReinforcementPhaseState();
                reinforcementPhase.setToCountry(countryNameByUser);
                reinforcementPhase.setNumberOfArmiesPlaced(numberOfArmiesToBePlacedByUser);
                reinforcementPhaseState.add(reinforcementPhase);
                notifyView();

            } catch (NumberFormatException ex) {
                System.out.println(ex.getMessage() + ", please enter numeric values only!\n\n");
            }
        }
        /* End of reinforcement phase, Print the final overview. */
        System.out.println("Reinforcement Phase is now complete. Here's an overview: \n\n");
        for (String countries : countriesOwned) {
            System.out.println("Country owned by you: " + countries + " ,Army Count: "
                    + this.gameData.gameMap.getCountry(countries).getCountryArmyCount());
        }
        reinforcementPhaseState.clear();
        System.out.println("\n**** Reinforcement Phase Ends for player " + this.playerName + "..****\n");
    }

    /**
     * Helper method to get and also validate the source country player wishes to
     * attack from.
     *
     * @param attackScenarios: the comprehensive list with all source and target
     *                         countries.
     * @return sourceCountry: a validated country option to attack from.
     */
    @Override
    public String getCountryToAttackFrom(HashMap<String, ArrayList<String>> attackScenarios) {
        String selectedSourceCountry = " ";

        do {
            System.out.println("Which one of your countries would you like to attack FROM?");
            if (input.hasNextLine()) {
                selectedSourceCountry = input.nextLine();
            }
        } while (!attackScenarios.containsKey(selectedSourceCountry));

        return selectedSourceCountry;
    }

    /**
     * Helper method to get and also validate the target country to be attacked.
     *
     * @param attackScenarios:       the comprehensive list with all source and target
     *                               countries.
     * @param selectedSourceCountry: a validated country option to attack from.
     * @return destinationCountry: a validated target country based on the source.
     */
    @Override
    public String getEnemyCountryToAttack(String selectedSourceCountry, HashMap<String, ArrayList<String>> attackScenarios) {
        {

            String selectedDestinationCountry = " ";

            do {
                System.out.println("\n Which enemy country would you like to attack from " + selectedSourceCountry + "?");
                if (input.hasNextLine()) {
                    selectedDestinationCountry = input.nextLine();
                }
            } while (!attackScenarios.get(selectedSourceCountry).contains(selectedDestinationCountry));

            return selectedDestinationCountry;
        }
    }

    /**
     * Helper method to get and also validate the attacker and defender's dice
     * selections. This is meant to be a generic functionality which tailors to both
     * actions.
     *
     * @param player:  the player rolling the dice.
     * @param country: either the source country to attack from / the target country
     *                 to be attacked
     * @param action:  either attack/defense.
     * @return selectedDiceCount: a valid number of dice player can roll.
     */
    @Override
    public Integer getDesiredDiceCountFromPlayer(String player, String country, String action) {

        String selectedDiceCount = "1";
        
        PlayerStrategy defendingPlayerStrategy = gameData.getPlayer(this.gameData.gameMap.getCountry(country).getCountryConquerorID()).getStrategyType();
        
        // max # of dice a defender can roll is 2
        int defaultMaxDiceCountAllowedForAction = 2;

        // max # of dice an attacker can roll is 3
        if (action.equals("attack")) {
            defaultMaxDiceCountAllowedForAction = 3;
        }
        
        // but as countries are attacked, army counts changes and we need to dynamically
        // update thresholds
        int maxDiceCountAllowedForAction = getActualMaxAllowedDiceCountForAction(action, country, defaultMaxDiceCountAllowedForAction);
        
        System.out.println("How many dice would " + player + " like to roll to " + action + "?" + "\t (up to " + maxDiceCountAllowedForAction + " dice)\n");
        
        if(defendingPlayerStrategy instanceof HumanMode) {
        	do {
                if (input.hasNextLine()) {
                    selectedDiceCount = input.nextLine();
                }
            } while (isNaN(selectedDiceCount) || Integer.parseInt(selectedDiceCount) < 1
                    || Integer.parseInt(selectedDiceCount) > maxDiceCountAllowedForAction);
    	} else {
  
    		Dice dice = new Dice();
    		selectedDiceCount = dice.rollDice(1, maxDiceCountAllowedForAction).toString();
    		System.out.println(player + " will roll " + selectedDiceCount);
    	}

        return Integer.parseInt(selectedDiceCount);
    }

    /**
     * Obtain the number of armies that the attcker choses to move to the conqured
     * country
     *
     * @param selectedSourceCountry pass the value they decide to move to
     * @return value to move the amount of army
     */
    @Override
    public Integer getNumberofArmiesAttackerWantsToMove(String selectedSourceCountry) {

            String selectedArmyCount = "1";
            int maxArmyCountAllowed = this.gameData.gameMap.getCountry(selectedSourceCountry).getCountryArmyCount() - 1;

            do {
                System.out.println("How many armies would " + this.playerName + " like to move to conquered country?"
                        + "\t (up to " + maxArmyCountAllowed + " armies)\n");
                if (input.hasNextLine()) {
                    selectedArmyCount = input.nextLine();
                }
            } while (isNaN(selectedArmyCount) || Integer.parseInt(selectedArmyCount) < 1
                    || Integer.parseInt(selectedArmyCount) > maxArmyCountAllowed);

            return Integer.parseInt(selectedArmyCount);
        }
    
    /**
     * Executes human mode fortification.
     */
    @Override
    public void executeFortification() {

        System.out.println();
        System.out.println("**** Fortification Phase Begins for player " + this.playerName + "..****\n");

        FortificationPhaseState fortificationPhase = new FortificationPhaseState();
        fortificationPhaseState.add(fortificationPhase);
        notifyView();

        // First get confirmation from the player that fortification is desired.

        boolean doFortify = false;
        String playerDecision = "no";
        System.out.println("Would you like to fortify? (YES/NO)");
        if (input.hasNextLine()) {
            playerDecision = input.nextLine();
        }

        switch (playerDecision.toLowerCase()) {
            case "yes":
                doFortify = true;
                break;
            case "yeah":
                doFortify = true;
                break;
            case "y":
                doFortify = true;
                break;
            case "sure":
                doFortify = true;
                break;
        }

        if (!doFortify) {
            System.out.println(this.playerName + " does not wish to fortify. Ending turn..");
            System.out.println("\n****Fortification Phase Ends for player " + this.playerName + "..****\n");
            return;
        } else {
            System.out.println("\n" + "Fetching potential fortification scenarios for " + this.playerName + "...\n");
        }

        // Now fetch all possibilities for player (this could get long as the game
        // progresses and more land is acquired)

        HashMap<String, ArrayList<String>> fortificationScenarios = getFortificationScenarios();

        if (fortificationScenarios == null) {
            System.out.println(
                    "There are currently no fortification opportunities for " + this.playerName + ".. Sorry!\n");
            System.out.println("\n****Fortification Phase Ends for player " + this.playerName + "..****\n");
            return;
        }

        if (fortificationScenarios.isEmpty()) {
            System.out.println(
                    "There are currently no fortification opportunities for " + this.playerName + ".. Sorry!\n");
            System.out.println("\n****Fortification Phase Ends for player " + this.playerName + "..****\n");
            return;
        }

        // This structure will accelerate and organize the army count process/validation
        HashMap<String, Integer> armiesPerPotentialFortificationSourceCountry = new HashMap<String, Integer>();

        // Print all the options out for the player to see and choose from
        for (String keySourceCountry : fortificationScenarios.keySet()) {
            armiesPerPotentialFortificationSourceCountry.put(keySourceCountry,
                    this.gameData.gameMap.getCountry(keySourceCountry).getCountryArmyCount());
            // the range is one less because of the minimum requirement of having at least 1
            // army on the ground at all times.
            int possibleNumOfArmyRange = armiesPerPotentialFortificationSourceCountry.get(keySourceCountry) - 1;
            for (String correspondingDestinationCountry : fortificationScenarios.get(keySourceCountry)) {
                System.out.println("\n" + keySourceCountry + "\t -> \t" + correspondingDestinationCountry
                        + "\t (up to " + possibleNumOfArmyRange + " armies)");    
            }
        }

        // Recycle variable
        // clear the decision variable holder between choices
        playerDecision = "";

        // while selection doesn't match any of the offered options, prompt user
        while (!fortificationScenarios.containsKey(playerDecision)) {
            System.out.println("\n" + "Please choose one of the suggested countries to move armies FROM: ");
            playerDecision = input.nextLine();
        }
        String fromCountry = playerDecision;
        fortificationPhase.setFromCountry(fromCountry);
        notifyView();
        // while number of armies to be moved is not coherent, prompt user
        // 0 is a valid selection
        String noOfArmiesToMove = "-1";
        do {
            System.out.println("\n" + "How many armies would you like to move from " + fromCountry + "?");
            noOfArmiesToMove = input.nextLine();
        } while (isNaN(noOfArmiesToMove) || Integer.parseInt(noOfArmiesToMove) < 0
                || Integer.parseInt(noOfArmiesToMove) >= armiesPerPotentialFortificationSourceCountry.get(fromCountry));

        fortificationPhase.setNumberOfArmiesMoved(Integer.parseInt(noOfArmiesToMove));
        notifyView();

        playerDecision = "";

        // check that the {from - to} combination specifically makes sense as a valid
        // path
        while (!fortificationScenarios.get(fromCountry).contains(playerDecision)) {
            System.out.println("\n"
                    + "Please choose one of the valid countries to move armies INTO (knowing that you've chosen to move them from country "
                    + fromCountry + "): ");
            playerDecision = input.nextLine();
        }

        String toCountry = playerDecision;

        fortificationPhase.setToCountry(toCountry);
        notifyView();

        // At this stage all that's left to do really is adjust the army counts in the
        // respective countries to reflect they player's fortification move
        this.gameData.gameMap.getCountry(fromCountry).deductArmy(Integer.parseInt(noOfArmiesToMove));
        this.gameData.gameMap.addArmyToCountry(toCountry, Integer.parseInt(noOfArmiesToMove));

        System.out.println(
                "\nFortification Successful for " + this.playerName + ". Here is a summary of the new status-quo:\n");

        System.out.println("Army count for " + fromCountry + " is now: "
                + this.gameData.gameMap.getCountry(fromCountry).getCountryArmyCount());
        System.out.println("Army count for " + toCountry + " is now: "
                + this.gameData.gameMap.getCountry(toCountry).getCountryArmyCount());

        fortificationPhaseState.clear();
        
        System.out.println("\n****Fortification Phase Ends for player " + this.playerName + "..****\n");
    }
}
