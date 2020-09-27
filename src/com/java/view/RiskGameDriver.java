package com.java.view;

import com.java.controller.dice.Dice;
import com.java.controller.map.MapLoader;
import com.java.controller.startup.StartUpPhase;
import com.java.model.gamedata.GameData;
import com.java.model.map.Country;
import com.java.model.player.HumanMode;
import com.java.model.player.Player;
import com.java.model.player.PlayerStrategy;

import java.io.*;
import java.util.*;


/**
 * RiskGameDriver class is used as a view in which the user will interact with a given console.
 * It allows the risk game to start by loading the map selected by user from console.
 * Players then get to chose the number of players and the names to play, system provides an id based on that amount.
 * In roundrobin fashion each player will be assigned a country at random.
 * Each player will be able to place their armies on the assigned country based on the army total.
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez 
 * @version 1.0.0
 */
public class RiskGameDriver extends TournamentModeHelper{

	private GameData gameData;
	private StartUpPhase startUp;
	private MapLoader maploader;
	static  Scanner input;

	private static Integer MAX_TURNS = -1; // -1 by default represents no restriction on number of turns
	private static Boolean IS_TOURNAMENT_MODE = false;
    /**
     * Constructor will allow to load the map from user selection.
     * It would assign the parsed values from the map and store it in gameData object to use.
     */
	public RiskGameDriver() {
		input = new Scanner(System.in);
		gameData = new GameData();
		// using this will load the map
		maploader = new MapLoader();
	}

    /**
     * Begins the console interface by initiating the startup
     */
	public void startGame() {

		Integer userChoice = setAndGetGameModeFromUser();
		
		if (RiskGameDriver.IS_TOURNAMENT_MODE) {
			getTournamentModeDetailsFromUser();
			
			for(Integer key : this.tournamentModeGameData.keySet()) {
				for(GameData gameData : this.tournamentModeGameData.get(key)) {
					this.gameData = gameData;
					startUp = new StartUpPhase(gameData);
					initiateRoundRobin();
					registerObservers();
					ramdomAssignationOfCountries();
					initialArmyPlacement();
					startTurn();
				}
			}
			
			printTournamentModeResults();
			
		}
		else if(userChoice == 3){
			GameData gameData_load = null;
			String filename = "";
			boolean isValidFileName = false;
			File folder = new File("./SavedGames");

			while(!isValidFileName) {
				System.out.println("List of files saved:\n");
				for(File filenames:folder.listFiles()){
					System.out.println(filenames.getName());
				}
				System.out.println("\nEnter name of the file: ");
				filename = input.nextLine().trim();

				try {
					// Reading the object from a file
					FileInputStream inputFile = new FileInputStream("./SavedGames/" + filename);
					ObjectInputStream in = new ObjectInputStream(inputFile);

					// Method for deserialization of object
					gameData_load = (GameData) in.readObject();

					in.close();
					inputFile.close();

					System.out.println("Map has been loaded successfully. Game is resumed.");
				} catch (IOException ex) {
					System.out.println("\n"+ex.getMessage() + ". Please verify the filename from the list\n");
					continue;
				} catch (ClassNotFoundException ex) {
					System.out.println("ClassNotFoundException is caught" + ex.getMessage());
					continue;
				}
				isValidFileName = true;
				this.gameData = gameData_load;
				startTurn();
			}
		}
		else {
			gameData.gameMap = maploader.loadMap();
			initiateStartUpPhase();
			initiateRoundRobin();
			registerObservers();
			ramdomAssignationOfCountries();
			initialArmyPlacement();
			startTurn();
		}
	}

	/**
	 * Prompts user to choose game mode between "Single Game Mode" and "Tournament
	 * Mode"
	 * 
	 * @return selected game mode represented as 1(Integer) for Single Game Mode and
	 *         2(Integer) for Tournament Mode
	 */
	private Integer setAndGetGameModeFromUser() {

		String gameModeUserDecision = "";
		File file = new File("./SavedGames");
		do {
			do {
				System.out.println("\nSelect GameMode (BASED ON NUMBER): ");
				if((file.isDirectory()) && (file.list().length>0)) {
					System.out.println("\n(1) Single Game Mode \n(2) Tournament\n(3) Load Saved Games\n");
				}
				else{
					System.out.println("\n(1) Single Game Mode \n(2) Tournament\n");
				}
				System.out.print("Enter choice: ");

				gameModeUserDecision = input.nextLine().trim();
			} while (isNaN(gameModeUserDecision));
		} while (!(Integer.parseInt(gameModeUserDecision) > 0 && Integer.parseInt(gameModeUserDecision) < 4));

		if (Integer.parseInt(gameModeUserDecision) == 2) {
			RiskGameDriver.IS_TOURNAMENT_MODE = true;
		}

		return Integer.parseInt(gameModeUserDecision);
	}
	
	
	/**
	 * Able to attach the views Phase View, CardsExchange View to the observer PlayerStrategy
	 */
	private void registerObservers() {
		CardsExchangeView cardsExchangeView = new CardsExchangeView();
		PhaseView phaseView = new PhaseView();

		for(Player currentPlayer : gameData.getPlayers()) {
			currentPlayer.getStrategyType().addObserver(phaseView);
			currentPlayer.getStrategyType().addObserver(cardsExchangeView);
		}
		
		PlayersWorldDominationView playersWorldDominationView = new PlayersWorldDominationView();
		gameData.gameMap.addObserver(playersWorldDominationView);
	}
	/**
     * The Start up phase intiates in this method by calling the helpers from StartUp phase. This method asks
     * for the names of the players and the number of players.
     */
	private void initiateStartUpPhase() {
		
		// Need to call the controller start uphase
		startUp = new StartUpPhase(gameData);
		int numOfPlayer = 0;
		String numOfPlayerStr = "";
		
		// Retrives the number of players provided by the user.
		do {

			System.out.println("\nNote: You can only have players between 2 to " + Math.min(GameData.MAX_PLAYERS, gameData.gameMap.getNumberOfCountries()));
			System.out.println("Enter the number of players: ");

			numOfPlayerStr = input.nextLine();

		} while (isNaN(numOfPlayerStr)
                || Integer.parseInt(numOfPlayerStr) < GameData.MIN_PLAYERS
				|| Integer.parseInt(numOfPlayerStr) > Math.min(GameData.MAX_PLAYERS, gameData.gameMap.getNumberOfCountries()));
		
		// Stores the number of players in game data.
		numOfPlayer = Integer.parseInt(numOfPlayerStr);
		gameData.setNoOfPlayers(numOfPlayer);
		
		// Array list to store the name of the players provided by the user.
		ArrayList<String> playerNames = new ArrayList<String>();
		ArrayList<Integer> playerStrategy = new ArrayList<Integer>();
		
		// Asks the name of each player.
		for (int i = 0; i < numOfPlayer; i++) {
			
			String playerNameInput = "";
			String playerStrategyInput = "";

			while(playerNameInput == null || playerNameInput.length() == 0) {
				System.out.println("\nPlayer " + (i + 1));
				System.out.println("Enter your name: ");
				playerNameInput = input.nextLine().trim();
			}
			//first check if it is a number then check if it is inside the range of 1 to 5
			do {
				do {
					System.out.println("\nChoose your Player Strategy (BASED ON NUMBER): ");
					System.out.println("\n(1) Aggressive \n(2) Benevolent \n(3) Random \n(4) Cheater \n(5) Human");

					playerStrategyInput = input.nextLine().trim();
				} while (isNaN(playerStrategyInput));
			}
			while(!(Integer.parseInt(playerStrategyInput) > 0 && Integer.parseInt(playerStrategyInput) <6));// check to make sure it is between 1 and 5 else keep asking

			playerNames.add(playerNameInput.trim());
			playerStrategy.add(Integer.parseInt(playerStrategyInput)); // parse it and store it as integer
		}
		
		// Array list to store the name of the players provided by the user.
		startUp.generatePlayers(playerNames, playerStrategy);
		startUp.generateCardsDeck();
	}
	
	/**
     * Every country owned by a player start at least with one army at the beginning, then the player can choose where to put
     * the remaining armies.
     */
	private void initialArmyPlacement() {
		
		// Calculation of initial army
		Integer numberOfArmiesAvailablePerPlayer;
		System.out.println("Calculation of initial armies done....");

		// Gets all countries from game map.
		HashMap<String, Country> countryObjects = gameData.gameMap.getAllCountries();

		for (Player player : gameData.getPlayers()) {

			// Gets all countries from game map.
			Boolean firstTime = true;
			HashSet<String> countriesPerPlayer = gameData.gameMap.getConqueredCountries(player.getStrategyType().getPlayerID());
			String[] countriesPerPlayerArray = Arrays.copyOf(countriesPerPlayer.toArray(), countriesPerPlayer.size(),String[].class);
			numberOfArmiesAvailablePerPlayer = startUp.initialArmyCalculation(gameData.getNoOfPlayers());
			
			// Displays the number of armies per player.
			System.out.println(" ");
			System.out.println("Number of Armies per player: " + numberOfArmiesAvailablePerPlayer);

			while (numberOfArmiesAvailablePerPlayer > 0) {
				
				// Displays the lists of countries owned by a player.
				System.out.println("Player: " + player.getStrategyType().getPlayerName() + " owns the following countries: ");

				// At the begining every country has at least one army.
				for (int i = 0; i < countriesPerPlayerArray.length; i++) {

					String countryName = countriesPerPlayerArray[i];

					if (firstTime) {
						//countryObjects.get(countryName).addArmy(1);
						gameData.gameMap.addArmyToCountry(countryName, 1);
						numberOfArmiesAvailablePerPlayer--;
					}

					System.out.println("\t " + i + ": " + countryName + " has " + countryObjects.get(countryName).getCountryArmyCount() + " armies placed.");
				}

				firstTime = false;


				String chosedCountryByUser = "";
				
				Dice newDice = new Dice();
				Integer randomNumberOfArmiesToPlace;
				Integer randomChoosedCountry;
				
				do {
					System.out.println("\nYou have " + numberOfArmiesAvailablePerPlayer + " armies left.");
					System.out.println("Please pick the number associated with the country in order to place your armies: ");
					
					if(player.getStrategyType() instanceof HumanMode) {
						chosedCountryByUser = input.nextLine();
					} else {
						randomChoosedCountry = newDice.rollDice(0,countriesPerPlayerArray.length - 1);
						chosedCountryByUser =  Integer.toString(randomChoosedCountry);
					}
					
					System.out.println("You picked: " + chosedCountryByUser);
					
					if(isNaN(chosedCountryByUser) || Integer.parseInt(chosedCountryByUser) < 0 
							|| Integer.parseInt(chosedCountryByUser) >= countriesPerPlayerArray.length) {
						System.out.println("Invalid Input!!");
					}

				} while(isNaN(chosedCountryByUser) || Integer.parseInt(chosedCountryByUser) < 0 
						|| Integer.parseInt(chosedCountryByUser) >= countriesPerPlayerArray.length);
				
				String countryName = countriesPerPlayerArray[Integer.parseInt(chosedCountryByUser)];

				String numberOfArmiesByUser = "";
				
				do {
					System.out.println("Player: " + player.getStrategyType().getPlayerName() + " How many armies do you want to place in "
							+ countryName + "?");
					
					if(player.getStrategyType() instanceof HumanMode) {
						numberOfArmiesByUser = input.nextLine();
					} else {
						randomNumberOfArmiesToPlace = newDice.rollDice(0,numberOfArmiesAvailablePerPlayer);
						numberOfArmiesByUser =  Integer.toString(randomNumberOfArmiesToPlace);
					}
					
					System.out.println("You picked: " + numberOfArmiesByUser);
					
					if(isNaN(numberOfArmiesByUser) || Integer.parseInt(numberOfArmiesByUser) < 0 
							|| Integer.parseInt(numberOfArmiesByUser) > numberOfArmiesAvailablePerPlayer) {
						System.out.println("Invalid input!!");
					}

				} while(isNaN(numberOfArmiesByUser) || Integer.parseInt(numberOfArmiesByUser) < 0
						|| Integer.parseInt(numberOfArmiesByUser) > numberOfArmiesAvailablePerPlayer);

				//Country selectedCountry = countryObjects.get(countryName);
				gameData.gameMap.addArmyToCountry(countryName, Integer.parseInt(numberOfArmiesByUser));
				//selectedCountry.addArmy(Integer.parseInt(numberOfArmiesByUser));
				numberOfArmiesAvailablePerPlayer -= Integer.parseInt(numberOfArmiesByUser);
			}
		}
	}
	
	/**
     * Calls the start up generate round robin helper to generate the order to play. Then displays the order of play.
     */
	private void initiateRoundRobin() {
		System.out.println(" ");
		System.out.println("The following list has the order of the players in round robin fashion: ");
		System.out.println("The results are based on the higher number of dice a player rolled ");
		ArrayList<Player> results = startUp.generateRoundRobin();
		for(int i = 0; i < results.size(); i++) {
			System.out.println((i+1) + " " + results.get(i).getStrategyType().getPlayerName());
		}
	}
	
	/**
     * Calls the assign countries to players from the controller.
     */
	private void ramdomAssignationOfCountries() {
		// Country Assignment begins
		startUp.assignCountriesToPlayers();
	}
	
	/**
     * Helper method to test if a given strin can be converted to a int.
     * @param stringInput determines if the string typed by user is an integer
     * @return the evaluation of true if it is an integer or false otherwise
     */
	private boolean isNaN(final String stringInput) {
		try {
			Integer.parseInt(stringInput);

		} catch (final Exception e) {
			return true;
		}
		return false;
	}

	/**
	 * There will be another round if the number of players is greater than one.
	 */
	/**
	 * Loop through player list circularly until one of the player wins
	 */
	private void startTurn() {
		ArrayList<Player> playerList = this.gameData.getPlayers();
		PlayerStrategy currentPlayer; // first player that will start the game
		String choiceToSave = "";
		String filename = "";

		Boolean doWeHaveAWinner = false;

		Integer turnsLeft = RiskGameDriver.MAX_TURNS;
		
		if(RiskGameDriver.IS_TOURNAMENT_MODE) {
			turnsLeft = TournamentModeHelper.TM_MAX_NUMBER_OF_TURNS;
		}
		
		//There will be another round if the number of players is greater than one.
		while(!doWeHaveAWinner) {

			if(turnsLeft == 0) {
				break;
			}
			
			for (Player player : playerList) {
				
				if(turnsLeft == 0) {
					break;
				} else {
					turnsLeft--;
				}
				
				currentPlayer = player.getStrategyType();
				
				// before actually 
				if(getIsActive(currentPlayer)) {
					System.out.println("\n***** Turn Begins for player "+currentPlayer.getPlayerName() +" *****\n");
					currentPlayer.setGameData(this.gameData);
					player.startTurn();
					System.out.println("\n***** Turn Ends for player "+currentPlayer.getPlayerName() +" *****\n");
				}
				
				// if player has won the game; break the turn loop
				if(getIsWinner(currentPlayer)) {
					doWeHaveAWinner = true;
					System.out.println("Congratulations! "+ currentPlayer.getPlayerName()+ " wins the game.");
					break;
				}
			}

			if(!IS_TOURNAMENT_MODE) {
				choiceToSave = "";
				while ((!(choiceToSave.equals("y") || choiceToSave.equals("n")))) {

					System.out.println("Would you like to save the game (y/n)?");
					choiceToSave = input.nextLine();

					if (choiceToSave.equals("y")) {
						try {
							System.out.println("Please enter the file name: ");
							filename = input.nextLine();
							FileOutputStream file = new FileOutputStream("./SavedGames/"+filename);
							ObjectOutputStream out = new ObjectOutputStream(file);

							// Method for serialization of object
							out.writeObject(this.gameData);

							out.close();
							file.close();

							System.out.println("Game has been saved.");
							return;
						} catch (IOException ex) {
							System.out.println("IOException is caught. " + ex.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * finds if the player is a winner
	 * @param player check based on the player passed in if they hav won the game
	 * @return true if the player has won the game
	 */
	private boolean getIsWinner(PlayerStrategy player) {
		
		boolean isWinner = false;
		HashSet<String> allConqueredCountries = new HashSet<String>();
		allConqueredCountries = this.gameData.gameMap.getConqueredCountries(player.getPlayerID());

		if(allConqueredCountries.size() == this.gameData.gameMap.getNumberOfCountries()) {
			isWinner = true;
			player.setIsWinner(isWinner);
			System.out.println("\n ****" + player.getPlayerName() + " HAS CONQUERED THE WORLD !****");
			System.out.println("\n ******************************* \n");
			System.out.println("\n ********** GAME OVER ********** \n");
			System.out.println("\n ******************************* \n");
		}
		return isWinner;
		
	}

	/**
	 * to keep track of the fact they are still in the game
	 * @param player pass the value of the player that is in game
	 * @return true if the player is active
	 */
	private boolean getIsActive(PlayerStrategy player) {
		
		boolean isActive = true;
		HashSet<String> allConqueredCountries = new HashSet<String>();
		allConqueredCountries = this.gameData.gameMap.getConqueredCountries(player.getPlayerID());

		if(allConqueredCountries.size() == 0) {
			isActive = false;
		}
		
		return isActive;
	}
}
