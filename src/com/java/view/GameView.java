package com.java.view;

import com.java.model.Observable;

/**
 * Main Observer view abstract class.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 1.0.0
 * */
public interface GameView {
	
	/**
	 * Update the view
	 * 
	 * @param o observable object wieht new state.
	 * */
    public void update(Observable o);
}
