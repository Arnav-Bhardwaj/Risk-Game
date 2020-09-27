package com.java.model.player;

import java.io.Serializable;

/**
 * This class models the state of Fortification phase to be presented in phase View.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez 
 * @version 1.0.0
 * */

public class FortificationPhaseState implements Serializable {

	private String fromCountry = " ";
	private String toCountry = " ";
	private Integer numberOfArmiesMoved = 0;
	
	/**
	 * Default constructor to instance FortificationPhaseState.
	 * */
	public FortificationPhaseState() {
	}
	
	/**
	 * Gets the name of the country from where the player wants to move the army.
	 * 
	 * @return the name of the country from where the player wants to move the army.
	 * */
	public String getFromCountry() {
		return fromCountry;
	}
	
	/**
	 * Sets the name of the country from where the player wants to move the army.
	 * 
	 * @param fromCountry the name of the country from where the player wants to move the army.
	 * */
	public void setFromCountry(String fromCountry) {
		this.fromCountry = fromCountry;
	}
	
	/**
	 * Gets the name of the destination country to move the army.
	 * 
	 * @return the name of the destination country to move the army.
	 * */
	public String getToCountry() {
		return toCountry;
	}

	/**
	 * Sets the name of the destination country to move the army.
	 * 
	 * @param toCountry the name of the destination country to move the army.
	 * */
	public void setToCountry(String toCountry) {
		this.toCountry = toCountry;
	}
	
	/**
	 * Gets the number of armies moved.
	 * 
	 * @return the number of armies moved.
	 * */
	public Integer getNumberOfArmiesMoved() {
		return numberOfArmiesMoved;
	}
	
	/**
	 * Sets the number of armies moved.
	 * 
	 * @param numberOfArmiesMoved the number of armies moved.
	 * */
	public void setNumberOfArmiesMoved(Integer numberOfArmiesMoved) {
		this.numberOfArmiesMoved = numberOfArmiesMoved;
	}
}
