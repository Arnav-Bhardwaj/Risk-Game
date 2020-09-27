package com.java.controller.map;

import com.java.model.map.Continent;
import com.java.model.map.Country;
import com.java.model.map.GameMap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * MapValidator handles the map validation part of the game. It can validate
 * either a .map file or a user created map on console.
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 1.0.0
 */
public class MapValidator implements Serializable {

	GameMap map;

	/**
	 * Entry point to map validator for validating an already loaded map.
	 * 
	 * @param map map to be validated
	 * @return true if map is valid, false otherwise
	 */
	public Boolean validateMap(GameMap map) {
		this.map = map;

		Boolean response = true;

		System.out.println();
		System.out.println("############### Starting Map Validation ###############");
		response = validateMapMetaData();
		response = validateMapContinents() && response;
		response = validateMapCountries() && response;
		response = validateMapConnectivity() && response;
		System.out.println("############### Map Validation Finished ###############");

		if (!response) {
			System.out.println("Result: Invalid Map");
		} else {
			System.out.println("Result: Map is Valid");
		}
		return response;
	}

	/**
	 * Validates whether each country has a path to all the countries in the map
	 * 
	 * @return true if map is connected, false otherwise
	 */
	private boolean validateMapConnectivity() {

		Boolean change = true;

		HashMap<String, HashSet<String>> connectivity = (HashMap<String, HashSet<String>>) map.clone()
				.getAdjacentCountriesObject();

		while (change) {
			change = false;
			for (String countryName : connectivity.keySet()) {
				for (String countryToBeCompared : connectivity.keySet()) {

					if (connectivity.get(countryName).size() == connectivity.size() - 1) {
						break;
					}

					if (!countryToBeCompared.equalsIgnoreCase(countryName)
							&& connectivity.get(countryToBeCompared).contains(countryName)) {

						Integer oldSize = connectivity.get(countryName).size();
						connectivity.get(countryName).add(countryToBeCompared);
						connectivity.get(countryName).addAll(connectivity.get(countryToBeCompared));
						connectivity.get(countryName).remove(countryName);
						Integer newSize = connectivity.get(countryName).size();

						if (oldSize != newSize) {
							change = true;
						}

						oldSize = connectivity.get(countryToBeCompared).size();
						connectivity.get(countryToBeCompared).add(countryName);
						connectivity.get(countryToBeCompared).addAll(connectivity.get(countryName));
						connectivity.get(countryToBeCompared).remove(countryToBeCompared);
						newSize = connectivity.get(countryToBeCompared).size();

						if (oldSize != newSize) {
							change = true;
						}
					}
				}
			}
		}

		for (String countryName : connectivity.keySet()) {
			if (connectivity.get(countryName).size() != connectivity.size() - 1) {
				System.out.println("ERROR: Map disconnected");
				return false;
			}
		}

		return true;
	}
	
	/**
	 * Validates a map.
	 * 
	 * @return true if a map is valid.
	 * */
	private Boolean validateMapCountries() {

		Boolean response = true;

		HashMap<String, Country> countries = map.getAllCountries();

		if (countries.size() < 2) {
			System.out.println("ERROR: Less than 2 countries");
			response = false;
		}

		for (String countryName : countries.keySet()) {
			if (map.getAdjacentCountries(countryName).size() == 0) {
				System.out.println("ERROR: Country " + countryName + " doesn't have any adjacent country");
				response = false;
			}
		}

		return response;
	}
	
	/**
	 * Helper method which validates if a string can be converted to an integer.
	 * 
	 * @param string the string to be tested.
	 * */
	private boolean isNaN(final String string) {
		try {
			Integer.parseInt(string);
		} catch (final Exception e) {
			return true;
		}
		return false;
	}
	
	/**
	 * Validates the continents
	 * 
	 * @return true if the continents are valid.
	 * */
	private Boolean validateMapContinents() {

		Boolean response = true;
		HashMap<String, Continent> continenets = map.getAllContinents();

		if (continenets.size() < 1) {
			System.out.println("ERROR: No continents in the map");
			response = false;
		}

		for (String continentName : continenets.keySet()) {
			if (map.getContinentCountries(continentName).size() < 2) {
				System.out.println("ERROR: Continent " + continentName + " has less than 2 countries");
				response = false;
			}
		}
		return response;
	}
	
	/**
	 * Validates the map metadata, in this case the author name.
	 * 
	 * @return true if the metadata is valid.
	 * */
	private Boolean validateMapMetaData() {

		Boolean response = true;

		if (map.getMapAuthor().length() == 0) {
			System.out.println("ERROR: Map Author name missing");
			response = false;
		}

		return response;
	}
	
	/**
	 * Get the file text line by line..
	 * 
	 * @return String the next line from the file.
	 * @exception IOException
	 * */
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
	 * Entry point to map validator for validating a .map file content.
	 * 
	 * @param mapFilePath .map file(to be validated) path in String format
	 * @return true if content in .map file is valid, false otherwise
	 * @throws IOException exception
	 */
	public Boolean validateMapTextFile(String mapFilePath) throws IOException {

		BufferedReader mapFileBufferedReader = null;
		String currentLine = null;

		try {
			mapFileBufferedReader = new BufferedReader(new FileReader(mapFilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!validateMapTextFileMetaData(mapFileBufferedReader)) {
			return false;
		}

		if (!validateMapTextFileContinentsAndTerritories(mapFileBufferedReader)) {
			return false;
		}

		return true;
	}

	/**
	 * Validates Map meta data in .map file
	 * 
	 * @param mapFileBufferedReader
	 * @throws IOException
	 */
	private boolean validateMapTextFileMetaData(BufferedReader mapFileBufferedReader) throws IOException {

		String currentLine = null;

		currentLine = nextLine(mapFileBufferedReader);
		if (currentLine == null || !currentLine.equals("[Map]")) {
			System.out.println("ERROR: [Map] tag missing");
			return false;
		}

		currentLine = nextLine(mapFileBufferedReader);

		if (currentLine == null || currentLine.split("=").length < 2
				|| !currentLine.split("=")[0].trim().equals("author")) {
			System.out.println("ERROR: Map Author missing");
			return false;
		}

		currentLine = nextLine(mapFileBufferedReader);
		if (currentLine == null || currentLine.split("=").length < 2
				|| !currentLine.split("=")[0].trim().equals("warn")) {
			System.out.println("ERROR: warn missing");
			return false;
		}
		return true;
	}

	/**
	 * Validates Map Continents and Territories in .map file
	 * 
	 * @param mapFileBufferedReader
	 * @throws IOException
	 */
	private Boolean validateMapTextFileContinentsAndTerritories(BufferedReader mapFileBufferedReader)
			throws IOException {

		String currentLine = null;
		String[] splitString = null;
		HashSet<String> continents = new HashSet<>();
		HashMap<String, ArrayList<String>> countries = new HashMap<>();

		currentLine = nextLine(mapFileBufferedReader);

		if (currentLine == null || !currentLine.equals("[Continents]")) {
			System.out.println("ERROR: [Continents] Tag missing");
			return false;
		}

		currentLine = nextLine(mapFileBufferedReader);

		while (currentLine != null && !currentLine.equals("[Territories]")) {
			splitString = currentLine.split("=");

			continents.add(splitString[0].trim());

			if (splitString.length <= 1 || splitString.length == 0 || splitString[1] == null || isNaN(splitString[1])) {
				System.out
						.println("ERROR: Either continent's control value is missing or is represented by a character");
				return false;
			}
			currentLine = nextLine(mapFileBufferedReader);
		}

		if (continents.size() < 1) {
			System.out.println("ERROR: No continent under [Continent] tag");
		}

		if (!currentLine.equals("[Territories]")) {
			System.out.println("ERROR: [Territories] Tag missing");
			return false;
		}

		currentLine = nextLine(mapFileBufferedReader);

		while (currentLine != null) {
			splitString = currentLine.split(",");
			ArrayList<String> neighbours = new ArrayList<>();

			if (splitString.length <= 2 || splitString[1] == null) {
				System.out.println("ERROR: country doesn't have any neighbour");
				return false;
			}

			if (!continents.contains(splitString[1])) {
				System.out.println("ERROR: Continent " + splitString[1] + " not under [Continent] tag");
				return false;
			}

			for (int i = 2; i < splitString.length; i++) {
				neighbours.add(splitString[i]);
			}

			countries.put(splitString[0].trim(), neighbours);

			currentLine = nextLine(mapFileBufferedReader);
		}

		for (String countryName : countries.keySet()) {
			ArrayList<String> neighbours = new ArrayList<>();
			neighbours = countries.get(countryName);
			for (String neibhbouringCountryName : neighbours) {
				if (!countries.containsKey(neibhbouringCountryName)
						|| !countries.get(neibhbouringCountryName).contains(countryName)) {
					System.out.println("ERROR: in territory data");
					return false;
				}
			}
		}

		if (countries.size() < 1) {
			System.out.println("ERROR: No country under [Territories] tag");
		}

		return true;
	}

}
