package com.java.controller.map;

import com.java.model.map.GameMap;

import java.io.Serializable;
import java.util.Scanner;

/**
 * MapEditor implements the logic to make it possible for a user to edit an
 * already loaded map into the game. 
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 1.0.0
 */
public class MapEditor implements Serializable {

	GameMap originalMap;
	GameMap editedMap;
	static Scanner scanner;
	MapValidator mapValidator;

	/**
	 * Constructor to initialize local map object
	 * @param map map to be edited 
	 */
	public MapEditor(GameMap map) {
		this.originalMap = map;
		this.editedMap = originalMap.clone();
		scanner = new Scanner(System.in);
		mapValidator = new MapValidator();
	}

	/**
	 * A public method which manages the entire map edit process.
	 * 
	 * @return a properly parsed valid map for the load map to use and construct the data
	 */
	public GameMap editMap() {
		Integer userChoice = 0;
		do {
			userChoice = getEditMapUserChoice();
		} while (userChoice < 1 || userChoice > 11);

		switch (userChoice) {
		case 1:
			changeMapAuthor();
			break;
		case 2:
			addContinenToMap();
			break;
		case 3:
			removeContinentFromMap();
			break;
		case 4:
			addCountryToMap();
			break;
		case 5:
			removeCountryFromMap();
			break;
		case 6:
			addAdjacenecyBetweenCountries();
			break;
		case 7:
			removeAdjacenecyBetweenCountries();
			break;
		case 8:
			showMapContent();
			break;
		case 9:
			mapValidator.validateMap(editedMap);
			break;
		case 10:
			editedMap = originalMap.clone();
			System.out.println("Changes discarded");
			break;
		case 11:
			if (mapValidator.validateMap(editedMap)) {
				return editedMap;
			}
			break;
		}

		return editMap();
	}

	/**
	 * User will be able to edt the contents of the map by selecting the options via menu
	 * @return the option that user has selected
	 */
	private Integer getEditMapUserChoice() {
		System.out.println("\nEdit map: \n1. Change Author\n2. Add a Continent\n3. Remove a Continent\n"
				+ "4. Add a Country\n5. Remove a Country\n6. Add Adjacency\n7. Remove Adjacency\n"
				+ "8. Show Map Content\n9. Validate Map\n10. Discard changes\n11. Save and Exit");
		System.out.print("Enter choice: ");

		String userChoiceStr = scanner.nextLine();
		if (isNaN(userChoiceStr) || Integer.parseInt(userChoiceStr) < 1 || Integer.parseInt(userChoiceStr) > 11) {
			System.out.println("Invalid input!!");
			userChoiceStr = "0";
		}
		return Integer.parseInt(userChoiceStr);
	}

	/**
	 * prints the contents of the map to the user.
	 */
	protected void showMapContent() {
		editedMap.printMap();
	}

	/**
	 * allow user to change the contires that are nebiours and edit it out of the map.
	 * checks if the edge exits after removal to ensure that
	 */
	protected void removeAdjacenecyBetweenCountries() {
		String countryName1 = null;
		String countryName2 = null;

		System.out.println("Please enter the names of the countries to be disconnected: ");
		System.out.print("Country : ");

		countryName1 = scanner.nextLine().trim();
		if (editedMap.getCountry(countryName1) == null) {
			System.out.println(countryName1 + " doesn't exist in the map.");
			return;
		}

		System.out.print("Country : ");
		countryName2 = scanner.nextLine().trim();

		if (editedMap.getCountry(countryName2) == null) {
			System.out.println(countryName2 + " doesn't exist in the map.");
			return;
		}

		if (!editedMap.removeAdjacenyBetweenCountries(countryName1, countryName2)) {
			System.out.println("No edge extsts between the countries");
			return;
		}

		System.out.println("Edge removed successfully");
	}

	/**
	 * based on the user input create a edge to add the countries as neighbours
	 */
	protected void addAdjacenecyBetweenCountries() {
		String countryName1 = null;
		String countryName2 = null;
		System.out.println("Please enter the names of the countries to be connected: ");
		System.out.print("Country : ");
		countryName1 = scanner.nextLine().trim();
		if (editedMap.getCountry(countryName1) == null) {
			System.out.println(countryName1 + " doesn't exist in the map.");
			return;
		}
		System.out.print("Country : ");
		countryName2 = scanner.nextLine().trim();
		if (editedMap.getCountry(countryName2) == null) {
			System.out.println(countryName2 + " doesn't exist in the map.");
			return;
		}
		editedMap.setAdjacentCountry(countryName1, countryName2);
		System.out.println("Edge added successfully");
	}

	/**
	 * User enters the country name they wish to delete and check if it exists and delete it
	 */
	protected void removeCountryFromMap() {
		String countryName = null;
		System.out.print("Please Enter the name of the Country to be removed: ");
		countryName = scanner.nextLine().trim();
		if (editedMap.getCountry(countryName) == null) {
			System.out.println(countryName + " doesn't exist in the map.");
			return;
		}
		editedMap.removeCountry(countryName);
		System.out.println("Country removed successfully");
	}

	/**
	 *User inputs a are used to create a country object in the list of map.
	 */
	protected void addCountryToMap() {
		System.out.print("Please Enter the name of the new Country: ");
		String newCountryName = scanner.nextLine().trim();

		if (editedMap.getCountry(newCountryName) != null) {
			System.out.println("Country already exists");
			return;
		}

		String continentName = null;

		System.out.print("Please Enter the name of the Continent, that " + newCountryName + " belongs to: ");
		continentName = scanner.nextLine().trim();

		if (editedMap.getContinent(continentName) == null) {
			System.out.println(continentName + " doesn't exist in the map.");
			return;
		}

		editedMap.addCountry(newCountryName, continentName);
		System.out.println("Country added successfully");
	}

	/**
	 * Take the values from user to remove the continent name,this acts as a edit option
	 */
	protected void removeContinentFromMap() {

		String continentName = null;
		System.out.print("Please Enter the name of the Continent to be removed: ");
		continentName = scanner.nextLine().trim();

		if (editedMap.getContinent(continentName) == null) {
			System.out.println(continentName + " doesn't exist in the map.");
			return;
		}
		editedMap.removeContinent(continentName);
		System.out.println("Continent removed successfully");
	}

	/**
	 * user will be able to place the countries inside the provided map.
	 */
	protected void addContinenToMap() {
		System.out.print("Please Enter the name of the new Continent: ");
		String newContinentName = scanner.nextLine().trim();

		if (editedMap.getContinent(newContinentName) != null) {
			System.out.println("Continent already exists");
			return;
		}
		Integer continentControlValue = -1;

		do {
			try {
				System.out.print("Please Enter the control value for the Continent " + newContinentName + ":");
				continentControlValue = Integer.parseInt(scanner.nextLine().trim());
				if (continentControlValue < 0) {
					System.out.println("Invalid Input.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid Input.");
				continentControlValue = -1;
			}
		} while (continentControlValue < 0);

		editedMap.addContinent(newContinentName, continentControlValue);
		System.out.println("Continent added successfully");
	}

	/**
	 * allow the user to change details of the author's name.
	 *
	 */
	protected void changeMapAuthor() {
		System.out.println("Current Map Author: " + editedMap.getMapAuthor());
		System.out.print("Please Enter the name of the author: ");
		String mapAuthorName = scanner.nextLine().trim();
		if (mapAuthorName.isEmpty() || mapAuthorName.length() == 0) {
			System.out.println("Invalid Name");
			return;
		}
		editedMap.setMapAuthor(mapAuthorName);
		System.out.println("Map Author updated to : " + editedMap.getMapAuthor());
	}

	/**
	 * Check if the string provided by user can be parsed to the int value
	 * @param string input string value
	 * @return return a true if cant be convereted so the menu can be repeated to user, false means no need to repate the menu, valid input.
	 */
	protected boolean isNaN(final String string) {
		try {
			Integer.parseInt(string);
		} catch (final Exception e) {
			return true;
		}
		return false;
	}

}
