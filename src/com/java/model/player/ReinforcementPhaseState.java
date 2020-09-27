package com.java.model.player;

import java.io.Serializable;

/**
 * This class models the state of Reinforcement phase to be presented in phase View.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez 
 * @version 1.0.0
 * */
public class ReinforcementPhaseState implements Serializable {
	
	private String toCountry = null;
	private Integer numberOfArmiesPlaced = 0;
	private Integer numberOfArmiesReceived = 0;
	
	/**
	 * Default constructor to instance ReinforcementPhaseState.
	 * */
	public ReinforcementPhaseState() {
	}

	/**
	 * Gets the name of country
	 * 
	 *  @return the country
	 * */
	public String getToCountry() {
		return toCountry;
	}
	
	/**
	 * Sets the country to be attacked
	 * @param toCountry country
	 */
	public void setToCountry(String toCountry) {
		this.toCountry = toCountry;
	}
	
	/**
	 * Gets the number of armies placed
	 * 
	 * @return the number of armies placed
	 */
	public Integer getNumberOfArmiesPlaced() {
		return numberOfArmiesPlaced;
	}
	
	/**
	 * Sets the number of armies to be placed
	 * 
	 * @param numberOfArmiesPlaced the number of armies placed
	 */
	public void setNumberOfArmiesPlaced(Integer numberOfArmiesPlaced) {
		this.numberOfArmiesPlaced = numberOfArmiesPlaced;
	}
	
	/**
	 * Gets the number of armies received
	 * 
	 * @return the number of armies received
	 */
	public Integer getNumberOfArmiesReceived() {
		return numberOfArmiesReceived;
	}
	
	/**
	 * Sets the number of armies received
	 * 
	 * @param numberOfArmiesReceived the number of armies received
	 */
	public void setNumberOfArmiesReceived(Integer numberOfArmiesReceived) {
		this.numberOfArmiesReceived = numberOfArmiesReceived;
	}


}
