package com.java.view;

import com.java.controller.map.MapLoader;
import com.java.controller.startup.StartUpPhase;
import com.java.model.gamedata.GameData;
import com.java.model.player.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;


/**
 * TournamentModeHelper class is used as a helper for RiskGameDriver.
 * It prompts the user to get the essential details required to organize the tournament.
 * 
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez 
 * @version 1.0.0
 */
public class TournamentModeHelper {

	static Scanner scanner;

	protected LinkedHashMap<Integer, ArrayList<GameData>> tournamentModeGameData;
	
	protected static Integer TM_NUMBER_OF_MAPS = 1;
	protected static Integer TM_NUMBER_OF_GAMES_ON_EACH_MAP = 1;
	protected static Integer TM_NUMBER_OF_PLAYERS = 2;
	protected static Integer TM_NUMBER_OF_TURNS = 10;

	protected static final Integer TM_MIN_NUMBER_OF_MAPS = 1;
	protected static final Integer TM_MAX_NUMBER_OF_MAPS = 5;
	protected static final Integer TM_MIN_NUMBER_OF_GAMES_ON_EACH_MAP = 1;
	protected static final Integer TM_MAX_NUMBER_OF_GAMES_ON_EACH_MAP = 5;
	protected static final Integer TM_MIN_NUMBER_OF_PLAYERS = 2;
	protected static final Integer TM_MAX_NUMBER_OF_PLAYERS = 4;
	protected static final Integer TM_MIN_NUMBER_OF_TURNS = 10;
	protected static final Integer TM_MAX_NUMBER_OF_TURNS = 50;

	public TournamentModeHelper() {
		scanner = new Scanner(System.in);
		tournamentModeGameData = new LinkedHashMap<>();
	}

	/**
	 * Prompts user to get details required to execute Tournament mode
	 */
	protected void getTournamentModeDetailsFromUser() {
		setAndGetNumberOfMapsToBeUsed();
		setAndGetNumberOfPlayers();
		setAndGetNumberOfGamesToBePlayedOnEachMap();
		setAndGetMaxNumberOfTurnsForEachGame();
		
		// set up tournament game data
		for(int i = 1; i <= TournamentModeHelper.TM_NUMBER_OF_MAPS; i++) {
			for(int j = 1; j <= TournamentModeHelper.TM_NUMBER_OF_GAMES_ON_EACH_MAP; j++) {
				if(!tournamentModeGameData.containsKey(i) || tournamentModeGameData.get(i).isEmpty()) {
					tournamentModeGameData.put(i, new ArrayList<>());
				}
				GameData gameData = new GameData();
				gameData.setNoOfPlayers(TM_NUMBER_OF_PLAYERS);
				tournamentModeGameData.get(i).add(gameData);
			}
		}
		
		loadGameMaps();
		getPlayersFromUser();
	}

	/**
	 * This method asks
     * for the number of the players and the names of players.
	 */
	private void getPlayersFromUser() {

		// Array list to store the name of the players provided by the user.
		ArrayList<String> playerNames = new ArrayList<String>();
		ArrayList<Integer> playerStrategy = new ArrayList<Integer>();

		// Asks the name of each player.
		for (int i = 0; i < TournamentModeHelper.TM_NUMBER_OF_PLAYERS; i++) {

			String playerNameInput = "";
			String playerStrategyInput = "";

			while (playerNameInput == null || playerNameInput.length() == 0) {
				System.out.println("\nPlayer " + (i + 1));
				System.out.println("Enter your name: ");
				playerNameInput = scanner.nextLine().trim();
			}
			// first check if it is a number then check if it is inside the range of 1 to 5
			do {
				do {
					System.out.println("\nChoose your PlayerStrategy Strategy (BASED ON NUMBER): ");
					System.out.println("\n(1) Aggressive \n(2) Benevolent \n(3) Random \n(4) Cheater");

					playerStrategyInput = scanner.nextLine().trim();
				} while (isNaN(playerStrategyInput));
				// check to make sure it is between 1 and 5 else keep asking
			} while (!(Integer.parseInt(playerStrategyInput) > 0 && Integer.parseInt(playerStrategyInput) < 5));

			playerNames.add(playerNameInput.trim());
			playerStrategy.add(Integer.parseInt(playerStrategyInput)); // parse it and store it as integer
		}

		for(Integer key : tournamentModeGameData.keySet()) {
			for(GameData gameData : tournamentModeGameData.get(key)) {
				StartUpPhase startUp = new StartUpPhase(gameData);
				startUp.generatePlayers(playerNames, playerStrategy);
				startUp.generateCardsDeck();
			}
		}
	}

	/**
	 * Load the number of maps specified by the user
	 */
	private void loadGameMaps() {
		
		for(Integer key : tournamentModeGameData.keySet()) {
			
			System.out.println("\nMap " + key + ":\n");
			MapLoader maploader = new MapLoader();
			maploader.loadMap();
			for (GameData gameData : tournamentModeGameData.get(key)) {
				maploader = new MapLoader();
				maploader.loadMapFromFile(MapLoader.SAVED_MAP_FILE_PATH);
				gameData.gameMap = maploader.map;
			}
		}
		
	}

	/**
	 * Obtains the input regarding number of turns to be played by each player
	 * the number is regarding max value
	 */
	private void setAndGetMaxNumberOfTurnsForEachGame() {
		String usersInput = "";

		do {
			System.out.print("Enter the maximum number of Turns to be played in each game (min: "
					+ TournamentModeHelper.TM_MIN_NUMBER_OF_TURNS + ", max: "
					+ TournamentModeHelper.TM_MAX_NUMBER_OF_TURNS + "): ");

			usersInput = scanner.nextLine();

		} while (isNaN(usersInput) || Integer.parseInt(usersInput) < TournamentModeHelper.TM_MIN_NUMBER_OF_TURNS
				|| Integer.parseInt(usersInput) > TournamentModeHelper.TM_MAX_NUMBER_OF_TURNS);

		// Stores the number of players in game data.
		TournamentModeHelper.TM_NUMBER_OF_TURNS = Integer.parseInt(usersInput);

	}
	/**
	 * Obtains the input regarding the number of games to be played by the player
	 * and sets the static value
	 */
	private void setAndGetNumberOfGamesToBePlayedOnEachMap() {
		String usersInput = "";

		do {
			System.out.print("Enter the number of Games to be played on each map (min: "
					+ TournamentModeHelper.TM_MIN_NUMBER_OF_GAMES_ON_EACH_MAP + ", max: "
					+ TournamentModeHelper.TM_MAX_NUMBER_OF_GAMES_ON_EACH_MAP + "): ");

			usersInput = scanner.nextLine();

		} while (isNaN(usersInput) || Integer.parseInt(usersInput) < TournamentModeHelper.TM_MIN_NUMBER_OF_GAMES_ON_EACH_MAP
				|| Integer.parseInt(usersInput) > TournamentModeHelper.TM_MAX_NUMBER_OF_GAMES_ON_EACH_MAP);

		// Stores the number of players in game data.
		TournamentModeHelper.TM_NUMBER_OF_GAMES_ON_EACH_MAP = Integer.parseInt(usersInput);
	}

	/**
	 * Obtain the total number of players to play in this mode
	 */
	private void setAndGetNumberOfPlayers() {
		String usersInput = "";

		do {
			System.out.print("Enter the number of Players (min: " + TournamentModeHelper.TM_MIN_NUMBER_OF_PLAYERS
					+ ", max: " + TournamentModeHelper.TM_MAX_NUMBER_OF_PLAYERS + "): ");

			usersInput = scanner.nextLine();

		} while (isNaN(usersInput) || Integer.parseInt(usersInput) < TournamentModeHelper.TM_MIN_NUMBER_OF_PLAYERS
				|| Integer.parseInt(usersInput) > TournamentModeHelper.TM_MAX_NUMBER_OF_PLAYERS);

		// Stores the number of players in game data.
		TournamentModeHelper.TM_NUMBER_OF_PLAYERS = Integer.parseInt(usersInput);
	}

	/**
	 * Obtain the total number of maps to play with in this mode
	 */
	private void setAndGetNumberOfMapsToBeUsed() {
		String usersInput = "";

		do {
			System.out.print("Enter the number of Maps (min: " + TournamentModeHelper.TM_MIN_NUMBER_OF_MAPS + ", max: "
					+ TournamentModeHelper.TM_MAX_NUMBER_OF_MAPS + "): ");

			usersInput = scanner.nextLine();

		} while (isNaN(usersInput) || Integer.parseInt(usersInput) < TournamentModeHelper.TM_MIN_NUMBER_OF_MAPS
				|| Integer.parseInt(usersInput) > TournamentModeHelper.TM_MAX_NUMBER_OF_MAPS);

		// Stores the number of players in game data.
		TournamentModeHelper.TM_NUMBER_OF_MAPS = Integer.parseInt(usersInput);
		
	}
	
	/**
	 * Prints tournaments outcome
	 */
	
	protected void printTournamentModeResults() {
		
		System.out.println("\nTournament Results: ");
		
		for (Integer key : this.tournamentModeGameData.keySet()) {
			System.out.println("\nMap " + key);
			ArrayList<GameData> games = this.tournamentModeGameData.get(key);
			for (int i = 0; i < games.size(); i++) {
				String result = "Draw";
				Player winnerPlayer = games.get(i).getWinner();
				
				if(winnerPlayer != null) {
					if (winnerPlayer.getStrategyType() instanceof RandomMode) {
						result = winnerPlayer.getStrategyType().getPlayerName() + "(Random)";
					} else if (winnerPlayer.getStrategyType() instanceof AggresiveMode) {
						result = winnerPlayer.getStrategyType().getPlayerName() + "(Aggresive)";
					} else if (winnerPlayer.getStrategyType() instanceof CheaterMode) {
						result = winnerPlayer.getStrategyType().getPlayerName() + "(Cheater)";
					} else if (winnerPlayer.getStrategyType() instanceof BenevolentMode) {
						result = winnerPlayer.getStrategyType().getPlayerName() + "(Benevolent)";
					}
				}
				
				System.out.println("  Game " + (i+1) + ": " + result);
			}
		}
	}

	/**
	 * Helper method to test if a given string can be converted to a integer.
	 * 
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

}
