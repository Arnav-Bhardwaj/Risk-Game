package com.java.model.gamedata;

import com.java.model.cards.CardsDeck;
import com.java.model.map.GameMap;
import com.java.model.player.Player;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class hold the whole data that is going to be used during the game. For instance,
 * the map, the players and the number of players are saved. This allows to keep the data in only place.
 *
 * @author Arnav Bhardwaj
 * @author Karan Dhingra
 * @author Ghalia Elkerdi
 * @author Sahil Singh Sodhi
 * @author Cristian Rodriguez
 * @version 1.0.0
 */
public class GameData implements Serializable {


	/**
	 * The number of players.
	 * */
	private Integer noOfPlayers;

	/**
	 * The players ordered in order to play.
	 * */
	private ArrayList<Player> players;

	/**
	 * The minimum number of players is 2.
	 * */
	public static final Integer MIN_PLAYERS = 2;

	/**
	 * The maximum number of players is 6.
	 * */
	public static final Integer MAX_PLAYERS = 6;
	/**
	 * Holds the map. Countries, continents, and adjacencies.
	 * */
	public GameMap gameMap;
	public CardsDeck cardsDeck;
	/**
	 * Getter players.
	 *
	 * @return the collection of players.
	 * */
	public ArrayList<Player> getPlayers() {
		return this.players;
	}

	/**
	 * Setter player.
	 *
	 * @param players the collection of players.
	 * */
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	/**
	 * Getter player.
	 *
	 * @param playerId player id.
	 * @return player.
	 * */
	public Player getPlayer(Integer playerId) {
		for(Player player : this.players) {
			if(player.getStrategyType().getPlayerID().equals(playerId)) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Remove one player of the main collection of players.
	 *
	 * @param player the player to be removed
	 * */
	public void removePlayers(Player player){
		this.players.remove(player);
	}

	/**
	 * Get the number of players.
	 *
	 * @return the number of players.
	 * */
	public Integer getNoOfPlayers() {
		return noOfPlayers;
	}

	/**
	 * Set the number of players.
	 *
	 * @param noOfPlayers the number of players.
	 * */
	public void setNoOfPlayers(Integer noOfPlayers) {
		this.noOfPlayers = noOfPlayers;
	}
	
	/**
	 * Get player's status
	 * @param player player id
	 * @return true if player is active else false
	 */
	public Boolean getPlayerStatus(Player player) {
		return player.getStrategyType().isActive;
	}
	
	/**
	 * Set player's status
	 * @param player player id.
	 * @param isActive set player status
	 */
	public void setPlayerStatus(Player player, Boolean isActive) {
		player.getStrategyType().isActive = isActive;
	}
	
	/**
	 * Get Winner
	 * 
	 * @return returns winner of the game; return null if game ended with a draw
	 */
	public Player getWinner() {
		for (Player player : this.players) {
			if (player.getStrategyType().getIsWinner() == true) {
				return player;
			}
		}
		return null;
	}


}