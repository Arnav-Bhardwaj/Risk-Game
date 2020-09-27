package com.java.view;

import com.java.model.Observable;
import com.java.model.player.AttackPhaseState;
import com.java.model.player.FortificationPhaseState;
import com.java.model.player.PlayerStrategy;
import com.java.model.player.ReinforcementPhaseState;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is will view the actions taking place on the three different gameplay phase details
 * by showing the current phase, player name and information regarding the actions taking place
 * Attack phase, reinforcement phase and fortification phase results.
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 2.0.0
 * */
public class PhaseView implements GameView {
	
    private File phaseFile;
    private FileWriter editView;

    /**
     *Creating the file that contains the information regarding each phase
     */
    public PhaseView(){
    	phaseFile = new File("./PhaseView.txt");

        if(phaseFile.exists()){
            phaseFile.delete();
        }
        
        setUpFile();
    }

    /**
     *perform logic to write the file and clear the entire view
     */
    private void setUpFile() {
    	try{
            phaseFile.createNewFile();
            editView = new FileWriter(phaseFile);
            editView.write(""); // to clear the view
            editView.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Do the update for Attack to write in file the different actions that took place
     * reinforcement what each player recived for army and country moved to and country from.
     * fortification used to show from country and to country moved armies, armies left and armies moved.
     * @param observable observable object
     */
    @Override
    public void update(Observable observable) {

        if(observable instanceof PlayerStrategy){

            ArrayList<ReinforcementPhaseState> reinforcementList = ((PlayerStrategy) observable).getReinforcementPhaseState();
            ArrayList<AttackPhaseState> attackList = ((PlayerStrategy) observable).getAttackPhaseState();
            ArrayList<FortificationPhaseState> fortificationList = ((PlayerStrategy) observable).getFortificationPhaseState();

            // reinforcement PHase view
            if(!reinforcementList.isEmpty()) {
                try {
                	setUpFile();
                    editView.write("\nCurrent Phase: Reinforcement Phase");
                    editView.write("\nCurrent PlayerStrategy: " + ((PlayerStrategy) observable).getPlayerName());
                    editView.write("\nActions:" );

                    editView.flush();
                    // iterate over reinforcementPhase and write to the file results
                    for (ReinforcementPhaseState eachReinforcement : reinforcementList) {
                    	Integer numberOfArmies = eachReinforcement.getNumberOfArmiesReceived();
                    	if(numberOfArmies > 0) {
                    		editView.write("\n  Number of Armies Received: " + numberOfArmies);
                            editView.flush();
                    	}
                    }

                    for (ReinforcementPhaseState eachReinforcement : reinforcementList) {

                    	String country = eachReinforcement.getToCountry();
                    	Integer numberOfArmiesPlaced = eachReinforcement.getNumberOfArmiesPlaced();

                    	if(country != null) {
                    		editView.write("\n  ***********");
                            editView.write("\n  To Country: " + country);
                            editView.write("\n  Number of Armies Placed: " + numberOfArmiesPlaced );
                            editView.flush();
                    	}
                    }
                    editView.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!attackList.isEmpty()) {
            	try {
           		 setUpFile();
                    editView.write("\nCurrent Phase: Attack Phase");
                    editView.write("\nCurrent PlayerStrategy: " + ((PlayerStrategy) observable).getPlayerName());
                    editView.write("\nActions:" );

                    editView.flush();
                    
                    // iterate over AttackPhaseState and write to the file results
                    for (AttackPhaseState eachAttack : attackList) {
                    	
                    	String attackingPlayer = ((PlayerStrategy) observable).getPlayerName();
                    	String defendingPlayer = eachAttack.getDefendingPlayer();

                    	String attackingCountry = eachAttack.getAttackingCountry();
                    	String defendingCountry = eachAttack.getDefendingCountry();

                    	Integer attackerDiceCount = eachAttack.getAttackerDiceCount();
                    	Integer defenderDiceCount = eachAttack.getDefenderDiceCount();
                    	ArrayList<Integer> attackerDiceRollResults = eachAttack.getAttackerDiceRollResults();
                    	ArrayList<Integer> defenderDiceRollResults = eachAttack.getDefenderDiceRollResults();
                    	
                    	editView.write("\n  ***********");
                        editView.write("\n  Attacking player: " + attackingPlayer);
                    	
                    	//if(defendingPlayer != null) {
                            editView.write("\n  Defending player: " + defendingPlayer);
                            editView.write("\n  Attacking country: " + attackingCountry);
                            editView.write("\n  Defending country: " + defendingCountry);
                            editView.write("\n  Attacker dice count: " + attackerDiceCount);
                            editView.write("\n  Defending dice count: " + defenderDiceCount);
                            editView.write("\n  Attacker dice roll results: ");
                            
                            if(attackerDiceRollResults != null) {
                            	for(int diceRollResult : attackerDiceRollResults) {
                                	editView.write("(" + diceRollResult + ") ");
                                }
                            }
                            
                            editView.write("\n  Defender dice roll results: ");
                            
                            if(defenderDiceRollResults != null) {
                            	for(int diceRollResult : defenderDiceRollResults) {
                                	editView.write("(" + diceRollResult + ") ");
                                }
                            }
                            editView.write("\n");
                            editView.flush();
                        //}
                    }
                    
                    editView.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }	
            	
            } else if (!fortificationList.isEmpty()) {
            	 try {
            		 setUpFile();
                     editView.write("\nCurrent Phase: Fortification Phase");
                     editView.write("\nCurrent PlayerStrategy: " + ((PlayerStrategy) observable).getPlayerName());
                     editView.write("\nActions:" );

                     editView.flush();
                     
                     // iterate over FortificationPhase and write to the file results
                     for (FortificationPhaseState eachFortification : fortificationList) {
                    	 
                    	 String fromCountry = eachFortification.getFromCountry();
                    	 String toCountry = eachFortification.getToCountry();
                    	 Integer numberOfArmiesMoved = eachFortification.getNumberOfArmiesMoved();
                    	 
                    	 if(fromCountry != null || toCountry != null) {
                    		 editView.write("\n  ***********");
                             editView.write("\n  From Country: " + fromCountry);
                             editView.write("\n  To Country: " + toCountry);
                             editView.write("\n  Number of armies moved: " + numberOfArmiesMoved);
                             editView.flush();
                    	 }
                     }
                     
                     editView.close();

                 } catch (IOException e) {
                     e.printStackTrace();
                 }	
            }
        }
    }
}
