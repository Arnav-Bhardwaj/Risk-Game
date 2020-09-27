package com.java.controller.map;

import com.java.model.map.GameMap;

import java.io.Serializable;

/**
 * MapCreator implements the logic to make it possible for a user to create map
 * on console. It extends MapEditor class to make use of all the map creation
 * methods in MapEditor to avoid redundancy in the code.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 1.0.0
 */
public class MapCreator extends MapEditor implements Serializable {

	/**
	 * Constructor to initialize local map object
	 * @param map map to be edited 
	 */
	public MapCreator(GameMap map) {
		super(map);
	}

	/**
	 *  this will manage the map creation based on input from console.
	 * 
	 * @return a completly parsed map to be able to used in the maploader object.
	 * @see MapLoader
	 */
	public GameMap createMap() {

		Integer userChoice = 0;
		do {
			userChoice = getCreateMapUserChoice();
		} while (userChoice < 1 || userChoice > 11);

		switch (userChoice) {
		case 1:
			addMapAuthor();
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
			return null;
		case 11:
			if (mapValidator.validateMap(editedMap)) {
				return editedMap;
			}
			break;
		}

		return createMap();
	}

	/**
	 * provide the user with options to create the map on console
	 * @return The value user has selected
	 */
	private Integer getCreateMapUserChoice() {
		System.out.println("\nCreate a new Map: \n1. Add Author\n2. Add a Continent\n3. Remove a Continent\n"
				+ "4. Add a Country\n5. Remove a Country\n6. Add Adjacency\n7. Remove Adjacency\n"
				+ "8. Show Map Content\n9. Validate Map\n10. Discard changes and Go back\n11. Save and Exit");
		System.out.print("Enter choice: ");

		String userChoiceStr = scanner.nextLine();
		if (isNaN(userChoiceStr) || Integer.parseInt(userChoiceStr) < 1 || Integer.parseInt(userChoiceStr) > 11) {
			System.out.println("Invalid input!!");
			userChoiceStr = "0";
		}
		return Integer.parseInt(userChoiceStr);
	}

	/**
	 * Takes input of the user's name and sets it as a author of the map file
	 */
	private void addMapAuthor() {
		System.out.print("Please Enter the name of the author: ");
		String mapAuthorName = scanner.nextLine().trim();
		if (mapAuthorName.isEmpty() || mapAuthorName.length() == 0) {
			System.out.println("Invalid Name");
			return;
		}
		editedMap.setMapAuthor(mapAuthorName);
		System.out.println("Author: " + mapAuthorName);
	}

}
