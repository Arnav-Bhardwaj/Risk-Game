package com.java.controller.map;

import com.java.model.map.GameMap;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * MapTest class tests the important aspects of map loader and validator, in
 * particular map connectivity and .map file validation.
 * 
 * @author Karan Dhingra
 * @version 2.0.0
 */
public class MapTest {

	static GameMap disconnectedGameMap;
	static GameMap connectedGameMap;
	static MapValidator mapValidator;
	static Method mapLoaderMethod;
	static String invalidMapFilePath1;
	static String invalidMapFilePath2;
	static String validMapFilePath;

	@BeforeClass
	public static void beforeEverything() {

		mapValidator = new MapValidator();
		invalidMapFilePath1 = "./map/invalid_maps/invalid1_continent_missing.map";
		invalidMapFilePath2 = "./map/invalid_maps/invalid2_disconnected_map.map";
		validMapFilePath = "./map/Manhattan.map";

		try {
			mapLoaderMethod = MapLoader.class.getDeclaredMethod("loadMapFromFile", String.class);
			mapLoaderMethod.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests the map text file validator, using the file which has a continent
	 * missing under Continent tag.
	 */
	@Test
	public void testReadInavlidMapFileWithContinentMissing() {
		try {
			assertFalse(Boolean.parseBoolean(mapLoaderMethod.invoke(new MapLoader(), invalidMapFilePath1).toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests the map text file validator, using file with the disconnected map.
	 */
	@Test
	public void testReadInavalidDisconnectedMapFile() {
		try {
			assertFalse(Boolean.parseBoolean(mapLoaderMethod.invoke(new MapLoader(), invalidMapFilePath2).toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests the map text file validator, using file with a valid map.
	 */
	@Test
	public void testReadValidMapFile() {
		try {
			assertTrue(Boolean.parseBoolean(mapLoaderMethod.invoke(new MapLoader(), validMapFilePath).toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests the validation of disconnected map
	 */
	@Test
	public void testValidateDisconnectedMap() {

		// Setting up disconnected map
		disconnectedGameMap = new GameMap();
		disconnectedGameMap.setMapAuthor("karan");
		disconnectedGameMap.addContinent("Continent1", 10);
		disconnectedGameMap.addContinent("Continent2", 20);
		disconnectedGameMap.addCountry("Country1", "Continent1");
		disconnectedGameMap.addCountry("Country2", "Continent1");
		disconnectedGameMap.addCountry("Country3", "Continent2");
		disconnectedGameMap.addCountry("Country4", "Continent2");
		disconnectedGameMap.setAdjacentCountry("Country1", "Country2");
		disconnectedGameMap.setAdjacentCountry("Country3", "Country4");

		assertFalse(mapValidator.validateMap(disconnectedGameMap));
	}

	/**
	 * Tests the validation of connected map
	 */
	@Test
	public void testValidateConnectedMap() {
		// Setting up connected map
		connectedGameMap = new GameMap();
		connectedGameMap.setMapAuthor("karan");
		connectedGameMap.addContinent("Continent1", 10);
		connectedGameMap.addContinent("Continent2", 20);
		connectedGameMap.addCountry("Country1", "Continent1");
		connectedGameMap.addCountry("Country2", "Continent1");
		connectedGameMap.addCountry("Country3", "Continent2");
		connectedGameMap.addCountry("Country4", "Continent2");
		connectedGameMap.setAdjacentCountry("Country1", "Country2");
		connectedGameMap.setAdjacentCountry("Country3", "Country4");
		connectedGameMap.setAdjacentCountry("Country1", "Country3");
		assertTrue(mapValidator.validateMap(connectedGameMap));
	}

}
