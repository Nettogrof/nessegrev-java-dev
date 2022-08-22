package ai.nettogrof.battlesnake.info;

import java.util.List;

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
	 * Constant Field name
	 */
	private static final String STANDARD = "standard";
	
	/**
	 * Constant Field name
	 */
	private static final String SQUAD = "squad";
	
	/**
	 * Constant Field name
	 */
	private static final String ROYALE = "royale";

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
	 * 
	 */
	private final int skrinkSpeed;
	
	/**
	 * 
	 */
	private final SquadRuleset squad;

	

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
		case ROYALE:
			gameType = 2;
			break;
		case "wrapped":
			gameType = 3;
			break;
		case SQUAD:
			gameType = 4;
			break;

		default:
			gameType = 0;
			break;
		}

		this.hazardDamage = setting.get("hazardDamagePerTurn").asInt();
		this.foodSpawnChance = setting.get("foodSpawnChance").asInt();
		this.minimumFood = setting.get("minimumFood").asInt();
		this.skrinkSpeed = setting.get(ROYALE).get("shrinkEveryNTurns").asInt();
		this.squad = new SquadRuleset(setting.get(SQUAD));
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
	
	/**
	 * Return game type 
	 * @return the gameType
	 */
	public String getRuleset() {
		final String[] ruleset = {STANDARD,"constructor",ROYALE,"wrapped",SQUAD};
		return ruleset[gameType];
		
	}
	
	
	/**
	 * Apply Squad rule to all snakes
	 * @param snakes list of snakes
	 */
	public void applySquadRules(final List<SnakeInfo> snakes) {
		squad.applyRules(snakes);
	}

	/**
	 * @return skrink Speed (royale mode)
	 */
	public int getSkrinkSpeed() {
		return skrinkSpeed;
	}

}
