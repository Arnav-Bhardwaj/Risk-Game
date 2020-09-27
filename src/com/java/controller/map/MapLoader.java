package com.java.controller.map;

import com.java.model.map.Continent;
import com.java.model.map.Country;
import com.java.model.map.GameMap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * MapLoader manages the complete map generation operations for the game, in
 * particular it allows user to load the default map, load user's .map file,
 * create map, and edit map.
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 1.0.0
 */
public class MapLoader implements Serializable {

	public GameMap map = new GameMap();
	static Scanner scanner;
	public static final String DEFAULT_MAP_FILE_PATH = "./map/default.map";
	public static final String SAVED_MAP_FILE_PATH = "./map/game_map/finalMap.map";

	public MapLoader() {
		scanner = new Scanner(System.in);
	}

	/**
	 * A public method which manages the entire map generation process including map
	 * edit, console map creation, loading .map file and validating the loaded map.
	 * @return a valid map
	 */
	public GameMap loadMap() {

		Integer defaultMapChoice = null;

		while (defaultMapChoice == null) {
			defaultMapChoice = getChoiceToUseDefaultMap();
		}

		if (defaultMapChoice == 1) {
			loadMapFromFile(DEFAULT_MAP_FILE_PATH);
		} else if (defaultMapChoice == 2) {
			String userMapFilePath = null;

			do {
				userMapFilePath = getAndValidateUserMapFilePath();
			} while (userMapFilePath == null);

			while (!loadMapFromFile(userMapFilePath)) {
				System.out.println("\nError in file content");
				return loadMap();
			}
			System.out.println("\nMap Loaded successfully");
		} else {
			MapCreator mapCreator = new MapCreator(map);
			map = mapCreator.createMap();
			if (map == null) {
				map = new GameMap();
				return loadMap();
			}

		}
		Integer editOrContinueChoice = 0;

		do {
			editOrContinueChoice = getChoiceToContinueOrEditMap();
			if (editOrContinueChoice == 1) {
				MapEditor mapEditor = new MapEditor(map);
				map = mapEditor.editMap();
			}
		} while (editOrContinueChoice != 2);

		saveMapAsTextFile();

		return map;
	}

	/**
	 * Obtain the choice the of continuing with game with the map or allow edit to process results
	 * 
	 * @return choice of the user and apply logic based on their input.
	 */
	private Integer getChoiceToContinueOrEditMap() {
		String choice = null;
		do {
			System.out.println();
			System.out.println("\nDo you want to edit the map or continue with the game?");
			System.out.println("1. Edit map");
			System.out.println("2. Continue");
			System.out.println();
			
			System.out.print("\nEnter choice: ");
			choice = scanner.nextLine();
		} while (isNaN(choice) || !(Integer.parseInt(choice) >= 1 && Integer.parseInt(choice) <= 2));

		return Integer.parseInt(choice);
	}

	/**
	 * Allow user to choose the options for the given input
	 * 
	 * @return the value selected by the user
	 */
	private Integer getChoiceToUseDefaultMap() {

		System.out.println("\nPlease choose one of the following options to load the map");
		System.out.println("1. Load default map");
		System.out.println("2. Load your own map");
		System.out.println("3. Create map");
		System.out.println();
		System.out.print("\nEnter choice: ");
		String choiceStr = scanner.nextLine();

		if (isNaN(choiceStr) || !(Integer.parseInt(choiceStr) >= 1 && Integer.parseInt(choiceStr) <= 3)) {
			System.out.println("Invalid input");
			return null;
		}
		return Integer.parseInt(choiceStr);
	}

	/**
	 * Obtain the file path location from the user and double check all the results
	 * 
	 * @return the file path as a string to be
	 */
	private String getAndValidateUserMapFilePath() {
		System.out.println();
		System.out.print("\nEnter File Path: ");
		String filePath = null;
		while ((filePath = scanner.nextLine()).isEmpty()) {
			if (!filePath.equals("")) {
				break;
			}
		}
		if (!new File(filePath).exists()) {
			System.out.println("\nOoooops, File not found. Try Again.");
			return null;
		}
		return filePath;
	}

	/**\
	 * obtain the nextline of the scanner values
	 * @param mapFileBufferedReader allows to use the buffereder
	 * @return string of input from user based on the entire line
	 * @throws IOException when there is no input
	 */
	private String nextLine(BufferedReader mapFileBufferedReader) throws IOException {
		String currentLine = null;

		do {
			currentLine = mapFileBufferedReader.readLine();
		} while (currentLine != null && currentLine.length() == 0);

		if (currentLine != null) {
			currentLine = currentLine.trim();
		}

		return currentLine;
	}

	/**
	 * mapfile is loaded from the file path and it reads the data
	 * @param mapFilePath takes in a file path from as a string
	 * @return true if the file has been read and false otherwise with an message stating why
	 */
	public Boolean loadMapFromFile(String mapFilePath) {

		Boolean response = true;
		BufferedReader mapFileBufferedReader = null;

		MapValidator mapValidator = new MapValidator();
		try {
			if (!mapValidator.validateMapTextFile(mapFilePath)) {
				return false;
			}
		} catch (IOException e2) {
			System.out.println(e2.getMessage());
			return false;
		}

		try {
			mapFileBufferedReader = new BufferedReader(new FileReader(mapFilePath));
		} catch (FileNotFoundException e1) {
			System.out.println("\nFile not Found");
			return false;
		}

		try {
			response = readMapMetaData(mapFileBufferedReader);
			if (response) {
				response = readandLoadContinents(mapFileBufferedReader);
			}
			if (response) {
				response = readandLoadCountries(mapFileBufferedReader);
			}
			if (!mapValidator.validateMap(map)) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * parsing the map file based on the hashbro rules
	 * @param mapFileBufferedReader accepts the buffer reader to read text in the file
	 * @return true if it reads all contents sucessfully, false if it doesnt read
	 * @throws IOException if the file is invalid.
	 */
	private Boolean readMapMetaData(BufferedReader mapFileBufferedReader) throws IOException {
		Boolean response = true;
		String[] splitString;
		String currentLine = "";

		if ((currentLine = nextLine(mapFileBufferedReader)) != null) {
			currentLine = currentLine.trim();

			if (!currentLine.equals("[Map]")) {
				response = false;
			} else {

				if ((currentLine = nextLine(mapFileBufferedReader)) != null) {
					splitString = currentLine.split("=");
					map.setMapAuthor(splitString[1]);
				} else {
					response = false;
				}

				if ((currentLine = nextLine(mapFileBufferedReader)) != null) {
					splitString = currentLine.split("=");
					map.warn = splitString[1];
				} else {
					response = false;
				}
			}
		}
		return response;
	}
	
	/**
	 * Reads the continents from the file text and the loads it.
	 * 
	 * @param mapFileBufferedReader the buffer reader.
	 * @return true if the continents are valid.
	 * @throws IOException if the file is invalid.
	 * */
	private Boolean readandLoadContinents(BufferedReader mapFileBufferedReader) throws IOException {

		Boolean response = true;
		String[] splitString;
		String currentLine = "";

		if ((currentLine = nextLine(mapFileBufferedReader)) != null) {
			String tag = currentLine.trim();
			if (!tag.equals("[Continents]")) {
				response = false;
			} else {
				while ((currentLine = nextLine(mapFileBufferedReader)) != null
						&& !currentLine.equals("[Territories]")) {
					splitString = currentLine.split("=");
					map.addContinent(splitString[0], Integer.parseInt(splitString[1]));
				}
			}
		}

		return response;
	}
	
	/**
	 * Reads and load countries from the file text.
	 * 
	 * @param mapFileBufferedReader the buffer reader.
	 * @return true if the countries are valid.
	 * @throws IOException if the file is invalid.
	 * */
	private Boolean readandLoadCountries(BufferedReader mapFileBufferedReader)
			throws NumberFormatException, IOException {

		Boolean response = true;
		String[] splitString;
		String currentLine = "";
		ArrayList<String> territoryTextData = new ArrayList<>();

		if ((currentLine = nextLine(mapFileBufferedReader)) != null) {

			while (currentLine != null) {
				territoryTextData.add(currentLine);
				splitString = currentLine.split(",");
				String currentTerritory = splitString[0];
				String currentTerritoryContinent = splitString[1];
				map.addCountry(currentTerritory, currentTerritoryContinent);
				currentLine = nextLine(mapFileBufferedReader);
			}

			for (int i = 0; i < territoryTextData.size(); i++) {
				splitString = territoryTextData.get(i).split(",");
				for (int j = 2; j < splitString.length; j++) {
					String adjacentCountry = splitString[j];
					map.setAdjacentCountry(splitString[0], adjacentCountry);
				}
			}

		} else {
			response = false;
		}

		return response;
	}
	
	/**
	 * Saves the map as a file text.
	 * */
	private void saveMapAsTextFile() {
		File file = new File(SAVED_MAP_FILE_PATH);
		HashMap<String, Continent> continents;
		HashMap<String, Country> countries;

		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write("[Map]\n");
			writer.write("author=" + map.getMapAuthor() + "\n");
			writer.write("warn=" + map.warn + "\n");

			writer.write("[Continents]\n");
			continents = map.getAllContinents();
			for (String continentName : continents.keySet()) {
				writer.write(continentName + "=" + continents.get(continentName).getContinentControlValue() + "\n");
			}

			writer.write("[Territories]\n");
			countries = map.getAllCountries();
			for (String countryName : countries.keySet()) {
				writer.write(countryName + "," + map.getCountry(countryName).getCountryContinentName());
				HashSet<String> neighbours = map.getAdjacentCountries(countryName);
				for (String neighbour : neighbours) {
					writer.write("," + neighbour);
				}
				writer.write("\n");
			}

			writer.flush();
			writer.close();

		} catch (IOException e) {
			System.out.println("\nERROR: Failure in map file creation");
		}
	}
	
	/**
	 * Tests if a string can be converted to an integer.
	 * 
	 * @param string to be tested
	 * @return true if the string can not be converted.
	 * */
	private boolean isNaN(final String string) {
		try {
			Integer.parseInt(string);
		} catch (final Exception e) {
			return true;
		}
		return false;
	}

}
