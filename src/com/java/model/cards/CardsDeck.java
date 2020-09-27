package com.java.model.cards;

import com.java.model.map.Country;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class models the CardsDeck. Each card holds the type of army
 * and the country.
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 2.0.0
 * */
public class CardsDeck implements Serializable {

    ArrayList<Card> deck = new ArrayList<Card>();
    ArmyType[] armyTypes = ArmyType.values();
    private Card card;
    private int top = 0;

    public CardsDeck(ArrayList<Country> countryList) {
        Collections.shuffle(countryList);
        for (int card = 0; card < countryList.size(); card++){
            deck.add(new Card(armyTypes[card%armyTypes.length], countryList.get(card)));
        }
        Collections.shuffle(deck);
    }

    /**
     * get a card from the deck
     * @return Card
     */
    public Card getCard(){
    	if(deck.isEmpty()) {
    		return null;
    	}
        card = deck.get(top);
        deck.remove(top);
        return card;
    }

    /**
     * Return the card to the deck and shuffle the cardsdeck.
     * @param returnCard put the card back to the deck
     */
    public void setCard(Card returnCard){
        deck.add(returnCard);
        Collections.shuffle(deck);
    }
}
