package com.java.model.map;

import java.io.Serializable;

/**
 * This class models the Continent, the name, the control value which is the number of armies
 * to be given once a player conquer a continent. Also, the continent holds a player id as conqueror.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez 
 * @version 1.0.0
 * */
public class Continent implements Serializable {
	
	/**
	 * Continent name
	 * */
	private String continentName;
	
	/**
	 * Number of armies to be given when continent is conquered.
	 * */
	private Integer continentControlValue;
	
	/**
	 * Hold the conqueror player Id
	 * */
	private Integer continentConquerorID;

	/**
	 * Contructor which requires the continen name and the control value.
	 * 
	 * @param continentName The continent name.
	 * @param controlValue The number of armies to be given once a continent is conquered.
	 * */
	public Continent(String continentName, Integer controlValue) {
		this.continentName = continentName;
		this.continentControlValue = controlValue;
	}
	
	/**
	 * Getter continent name.
	 * 
	 * @return Continent name
	 * */
	public String getContinentName() {
		return continentName;
	}
	
	/**
	 * Updates the continent name.
	 * 
	 * @param continentName the continent name.
	 * */
	public void updateContinentName(String continentName) {
		this.continentName = continentName;
	}
	
	/**
	 * Getter continent control value.
	 * 
	 * @return the number of armies to be given once a continent is conquered.
	 * */
	public Integer getContinentControlValue() {
		return continentControlValue;
	}

	/**
	 * Sets the continent control value.
	 * 
	 * @param continentControlValue the continent control value.
	 * */
	public void setContinentControlValue(Integer continentControlValue) {
		this.continentControlValue = continentControlValue;
	}
	
	/**
	 * Getter of the conqueror player ID.
	 * 
	 * @return The conqueror player ID
	 * */
	public Integer getContinentConquerorID() {
		return continentConquerorID;
	}
	
	/**
	 * Setter of the conqueror player ID.
	 * 
	 * @param countryConquerorID player conqueror id.
	 * */
	public void setContinentConquerorID(Integer countryConquerorID) {
		this.continentConquerorID = countryConquerorID;
	}


}
