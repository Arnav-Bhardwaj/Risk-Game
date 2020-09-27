package com.java.controller.dice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * A Dice class that is used when ever a player needs to obtain a random generated number between 1 and 6
 *  * @author Arnav Bhardwaj
 *  * @author Karan Dhingra
 *  * @author Ghalia Elkerdi
 *  * @author Sahil Singh Sodhi
 *  * @author Cristian Rodriguez
 *  * @version 1.0.0
 */
public class Dice implements Serializable {

	public Dice(){
	}

	/**
	 * When called will generate numbers ranging from 1 to 6
	 * @return a single integer value of random number
	 */
	public Integer rollDice(){
		Random rand1 = new Random();
		return 	rand1.nextInt(6 - 1 +1) + 1;
	}
	
	/**
	 * Generates a range modified dice in order to get a random number between the specifiend range
	 * 
	 * @param min minimum
	 * @param max maximum
	 * 
	 * @return a single integer value of random number
	 */
	public Integer rollDice(int min, int max){
		Random rand1 = new Random();
		return 	rand1.nextInt((max - min) + 1) + min;
	}
	
	/**
	 * When called will generate numbers ranging from 1 to 6
	 * @param numberOfDiceToRoll Number of dice to roll
	 * @return array list of Integers representing outcomes of rolling n dice 
	 */
	public ArrayList<Integer> rollDice(Integer numberOfDiceToRoll){
		
		ArrayList<Integer> result = new ArrayList<>();
		
		for(int i = 0; i < numberOfDiceToRoll; i++) {
			Random rand1 = new Random();
			result.add(rand1.nextInt(6 - 1 +1) + 1);
		}
		
		java.util.Collections.sort(result, java.util.Collections.reverseOrder());
		
		return result;
	}

}
