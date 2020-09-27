package com.java.controller.gameplay;

import com.java.model.cards.Card;
import com.java.model.cards.CardsDeck;
import com.java.model.gamedata.GameData;
import com.java.model.map.Country;
import com.java.model.map.GameMap;
import com.java.model.player.HumanMode;
import com.java.model.player.Player;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * ReinforcementTest class tests the total reinforcement count for four scenarios.
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 2.0.0
 */
public class ReinforcementTest {

    private static GameData gameData;
    private static Player playerOne,playerTwo;
    private static ArrayList<Player> players;

    @BeforeClass
    static public void setUp() {

        // Load the game with a dummy map and dummy data.
        gameData = new GameData();
        gameData.gameMap = new GameMap();

        // Test data with two players, two continents and six countries.
        players = new ArrayList<Player>();
        playerOne = new Player();
        playerTwo = new Player();
        playerOne.setStrategyType(new HumanMode(1, "P1"));
        playerTwo.setStrategyType(new HumanMode(2, "P2"));
        players.add(playerOne);
        players.add(playerTwo);

        gameData.gameMap.setupPlayerNames(players);
        gameData.gameMap.addContinent("Continent1", 5);
        gameData.gameMap.addContinent("Continent2", 6);

        gameData.gameMap.addCountry("C1", "Continent1");
        gameData.gameMap.addCountry("C2", "Continent1");
        gameData.gameMap.addCountry("C3", "Continent1");
        gameData.gameMap.addCountry("C4", "Continent2");
        gameData.gameMap.addCountry("C5", "Continent2");
        gameData.gameMap.addCountry("C6", "Continent2");

        gameData.gameMap.setCountryConquerer("C1", 1);
        gameData.gameMap.setCountryConquerer("C2", 1);
        gameData.gameMap.setCountryConquerer("C3", 1);
        gameData.gameMap.setCountryConquerer("C4", 1);
        gameData.gameMap.setCountryConquerer("C5", 1);
        gameData.gameMap.setCountryConquerer("C6", 1);

        gameData.gameMap.setContinentConquerer("Continent1", 1);
        gameData.gameMap.setContinentConquerer("Continent2", 1);

        gameData.gameMap.getCountry("C1").addArmy(3);
        gameData.gameMap.getCountry("C2").addArmy(1);
        gameData.gameMap.getCountry("C3").addArmy(2);
        gameData.gameMap.getCountry("C4").addArmy(1);
        gameData.gameMap.getCountry("C5").addArmy(5);
        gameData.gameMap.getCountry("C6").addArmy(6);

        playerOne.getStrategyType().setGameData(gameData);
        playerTwo.getStrategyType().setGameData(gameData);
    }

    /**
     * Calculate total reinforcement army for a player with no conquered continents and no conquered countries.
     */
    @Test
    public void testCalculateReinforcementArmyNoConqueredContinent() {

        int actual_value = playerTwo.getStrategyType().calculateReinforcementArmy();
        int expected_value = 3;
        assertEquals(expected_value, actual_value);
    }

    /**
     * Calculate total reinforcement army for a player with conquered continents having control values.
     */
    @Test
    public void testCalculateReinforcementArmyWithConqueredContinent() {

        int actual_value = playerOne.getStrategyType().calculateReinforcementArmy();
        int expected_value = 14;
        assertEquals(expected_value,actual_value);
    }

    /**
     * Check if the combination of cards for reinforcement army is correct before trade .
     * The three cards traded must be of the same or different types.
     */
    @Test
    public void testCardsValidityForTrade() {

        ArrayList<Country> countryList = new ArrayList<Country>();
        countryList.add(gameData.gameMap.getCountry("C1"));
        countryList.add(gameData.gameMap.getCountry("C2"));
        countryList.add(gameData.gameMap.getCountry("C3"));

        gameData.cardsDeck = new CardsDeck(countryList);

        for (int cardCount = 0; cardCount < 3; cardCount++) {
            Card card = gameData.cardsDeck.getCard();
            playerTwo.getStrategyType().addToPlayerCardList(card);
        }
        boolean actual_value = playerTwo.getStrategyType().isValidExchange(playerTwo.getStrategyType().getPlayerCardList());
        assertTrue(actual_value);
    }

    /**
     * Calculate total reinforcement army for a player with conquered continents having control values
     * and three cards in hand.
     */
    @Test
    public void testCalculateReinforcementArmyWithConqueredContinentAndTradeCards() {

        ArrayList<Country> countryList = new ArrayList<Country>();
        countryList.add(gameData.gameMap.getCountry("C1"));
        countryList.add(gameData.gameMap.getCountry("C2"));
        countryList.add(gameData.gameMap.getCountry("C3"));

        gameData.cardsDeck = new CardsDeck(countryList);

        for (int cardCount = 0; cardCount < 3; cardCount++) {
            Card card = gameData.cardsDeck.getCard();
            playerOne.getStrategyType().addToPlayerCardList(card);
        }
        /*calculateTotalReinforcement internally verifies for the validity of the card exchange.*/
        int actual_value = playerOne.getStrategyType().calculateTotalReinforcement(playerOne.getStrategyType().getPlayerCardList());
        int expected_value = 21;
        assertEquals(expected_value,actual_value);
    }
}
