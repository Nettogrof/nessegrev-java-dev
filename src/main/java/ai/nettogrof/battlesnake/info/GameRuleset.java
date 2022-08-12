package ai.nettogrof.battlesnake.info;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * GameRuleset is the class that contain all the informations and related
 * methods to Game ruleset
 * 
 * @author carl.lajeunesse
 * @version Spring 2022
 */
public class GameRuleset {

	/**
	 * Hazard damage per turn
	 */
	private final int hazardDamage;
	
	/**
	 * Food spawn chance (0% - 100%)
	 */
	private final int foodSpawnChance;
	
	/**
	 * Minimum food on board
	 */
	private final int minimumFood;
	/**
	 * game type 0 = Standard 1 = constructor 2 = royale 3 = wrapped 4 = squad
	 */

	private final int gameType;

	/**
	 * Object containing all game info
	 * @param gameType				Game type
	 * @param hazardDamage			Hazard damage per turn 
	 * @param foodSpawnChance		food spawn chance	
	 * @param minimumFood			minimum food on board
	 */
	public GameRuleset(final int gameType, final int hazardDamage, final int foodSpawnChance,
			final int minimumFood) {
		super();
		this.hazardDamage = hazardDamage;
		this.foodSpawnChance = foodSpawnChance;
		this.minimumFood = minimumFood;
		this.gameType = gameType;
	}

	/**
	 * Object containing all game info
	 * @param node	root node
	 */
	public GameRuleset(final JsonNode node) {
		super();
		final JsonNode setting = node.get("settings");

		switch (node.get("name").asText()) {
		case "constrictor":
			gameType = 1;
			break;
		case "royale":
			gameType = 2;
			break;
		case "wrapped":
			gameType = 3;
			break;

		default:
			gameType = 0;
			break;
		}

		this.hazardDamage = setting.get("hazardDamagePerTurn").asInt();
		this.foodSpawnChance = setting.get("foodSpawnChance").asInt();
		this.minimumFood = setting.get("minimumFood").asInt();
	}

	/**
	 * Get Damage per turn
	 * @return hazard Damage per Turn
	 */
	public int getHazardDamage() {
		return hazardDamage;
	}

	/**
	 * Get Food spawn chance
	 * @return the foodSpawnChance
	 */
	public int getFoodSpawnChance() {
		return foodSpawnChance;
	}

	/**
	 * Get Minimum food on board
	 * @return the minimumFood
	 */
	public int getMinimumFood() {
		return minimumFood;
	}

	/**
	 * Return game type 
	 * @return the gameType
	 */
	public int getGameType() {
		return gameType;
	}

}
