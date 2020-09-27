package com.java.model;

import com.java.view.GameView;

import java.util.ArrayList;

/**
 * Main observable abstract class.
 * 
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 1.0.0
 * */
public abstract class Observable {
	/**
	 * Holds every observer attached to this Observable.
	 * 
	 * */
    private ArrayList<GameView> listView;

    /**
     * Instance a new list of observers.
     * */
    public Observable(){
        listView = new ArrayList<GameView>();
    }
    
    /**
     * Adds a new observer
     * 
     * @param observer new observer
     * */
    public void addObserver(GameView observer){
       if(observer != null) {
           listView.add(observer);
       }
    }
    
    /**
     * Deletes an observer
     * 
     * @param observer to be erased
     * */
    public void deleteObserver(GameView observer){
       if(observer != null) {
           listView.remove(observer);
       }
    }
    
    /**
     * Notify the views
     * */
    public void notifyView(){
        for (GameView gm : listView){
            gm.update(this);
        }
    }


}
