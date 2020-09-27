package com.java.model.cards;

import com.java.model.map.Country;

import java.io.Serializable;

/**
 * Defining structure for a card
 */
public class Card implements Serializable {
    private ArmyType armyType;
    private Country country;

    public Card(ArmyType armyType, Country country) {
        this.armyType = armyType;
        this.country = country;
    }

    /**
     * get the country assigned to the cards
     * @return the country object
     */
    public Country getCountry() {
        return country;
    }

    /**
     * get the army type assigned to the card
     * @return get the armyType (Enum value)
     */
    public ArmyType getArmyType() {
        return armyType;
    }
}
