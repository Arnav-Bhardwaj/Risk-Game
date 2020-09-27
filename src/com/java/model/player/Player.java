package com.java.model.player;

import java.io.Serializable;

/**
 * This class models the player, it holds the strategy and the main methods to perform one turn.
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 3.0.0
 */
public class Player implements Serializable {
	
    private PlayerStrategy strategyType;

    /**
     * Setter method to set the strategy
     * 
     * @param strategyType the type of strategy.
     */
    public void setStrategyType(PlayerStrategy strategyType){
        this.strategyType = strategyType;
    }

    /**
     * Getter method to get the strategy.
     * 
     * @return the strategy.
     */
    public PlayerStrategy getStrategyType() {
        return strategyType;
    }

    /**
     * The startTurn() method organizes the flow of the game by ordering phase-execution.
     */
    public void startTurn() {
        boolean isWinner = false;
        startReinforcement();
        startAttack();
        // attack phase changes state so before going to fortify logic, check
         isWinner = checkIfPlayerHasConqueredTheWorld();
        if (!isWinner) {
            startFortification();
        }
    }
    
    /**
     * Method to start Reinforcement, it calls the method from the strategy.
     */
    public void startReinforcement() {
       strategyType.executeReinforcement();
    }
    
    /**
     * Method to start Attack, it calls the method from the strategy.
     */
    public void startAttack() {
        strategyType.executeAttack();
    }
    
    /**
     * Method to start Fortification, it calls the method from the strategy.
     */
    public void startFortification() {
        strategyType.executeFortification();
    }
    
    /**
     * Checks if a player conquered the whole map, therefore is the winner.
     * @return true if the player conquered the world
     */
    public boolean checkIfPlayerHasConqueredTheWorld(){
        return strategyType.checkIfPlayerHasConqueredTheWorld();
    }
}
