package com.java.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.java.model.Observable;
import com.java.model.cards.Card;
import com.java.model.player.PlayerStrategy;
import com.java.model.player.ReinforcementPhaseState;

/**
 * This class displays the information regarding cards owned by the current player
 * and the respective player's selection of cards to be exchanged.  
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 2.0.0
 * */
public class CardsExchangeView implements GameView {

	private File cardsExchangeViewFile;
    private FileWriter editView;

    /**
     * Creates a file to store the actions regarding the different values.
     */
    public CardsExchangeView() {
    	
    	cardsExchangeViewFile = new File("./CardsExchangeView.txt");

        if(cardsExchangeViewFile.exists()){
        	cardsExchangeViewFile.delete();
        }
        
        setUpFile();
    }

    /**
     * allows to perform the setUpFile to clear the view and flush out the buffer stream
     */
    private void setUpFile() {
    	try{
    		cardsExchangeViewFile.createNewFile();
            editView = new FileWriter(cardsExchangeViewFile);
            editView.write(""); // to clear the view
            editView.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    }
	
	@Override
	public void update(Observable o) {
		PlayerStrategy player = (PlayerStrategy) o;
		ArrayList<ReinforcementPhaseState> reinforcementList = player.getReinforcementPhaseState();
		if(reinforcementList.isEmpty()) {
			setUpFile();
		} else {
			try {
				ArrayList<Card> cardList = player.getPlayerCardList();
				editView.write("\r\nCurrent PlayerStrategy: " + player.getPlayerName());
				editView.write("\r\nPlayerStrategy has " + cardList.size() + " cards");
				editView.write("\r\nCards owned by player:");
				for(int i = 0; i < cardList.size(); i++) {
					editView.write("\r\nCard " + (i + 1) + ":");
					editView.write(" Country : " + cardList.get(i).getCountry().getCountryName());
					editView.write(", Army Type : " + cardList.get(i).getArmyType());
				}
				editView.flush();
			} catch (IOException e) {
                e.printStackTrace();
            }
			
		}
	}

}
