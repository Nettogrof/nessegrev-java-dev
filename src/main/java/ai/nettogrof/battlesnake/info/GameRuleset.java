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

	private int hazardDamagePerTurn = 14;
	private int foodSpawnChance = 15;
	private int minimumFood = 1;
	/**
	 * game type 0 = Standard 1 = constructor 2 = royale 3 = wrapped 4 = squad
	 */

	private int gameType = 0;

	public GameRuleset(int gameType, int hazardDamagePerTurn, int foodSpawnChance, int minimumFood) {
		super();
		this.hazardDamagePerTurn = hazardDamagePerTurn;
		this.foodSpawnChance = foodSpawnChance;
		this.minimumFood = minimumFood;
		this.gameType = gameType;
	}

	public GameRuleset(JsonNode node) {
		super();
		JsonNode setting = node.get("settings");

		switch (node.get("name").asText()) {
		case "standard":
			gameType = 0;
			break;
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
		}

		this.hazardDamagePerTurn = setting.get("hazardDamagePerTurn").asInt();
		this.foodSpawnChance = setting.get("foodSpawnChance").asInt();
		this.minimumFood = setting.get("minimumFood").asInt();
	}

	public int getDamagePerTurn() {
		return hazardDamagePerTurn;
	}

	/**
	 * @return the foodSpawnChance
	 */
	public int getFoodSpawnChance() {
		return foodSpawnChance;
	}

	/**
	 * @return the minimumFood
	 */
	public int getMinimumFood() {
		return minimumFood;
	}

	/**
	 * @return the gameType
	 */
	public int getGameType() {
		return gameType;
	}

}
