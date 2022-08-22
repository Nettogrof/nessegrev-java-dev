/**
 * 
 */
package ai.nettogrof.battlesnake.info;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * SquadRuleset is the class that contain all the informations and related
 * methods to Squad ruleset
 * 
 * @author carl.lajeunesse
 * @version Summer 2022
 */
public class SquadRuleset {

	/**
	 * 
	 */
	private final boolean allowBodyCollisions;

	/**
	 * 
	 */
	private final boolean sharedElimination;

	/**
	 * 
	 */
	private final boolean sharedHealth;
	
	/**
	 * 
	 */
	private final boolean sharedLength;

	/**
	 * @return the allowBodyCollisions
	 */
	public boolean isAllowBodyCollisions() {
		return allowBodyCollisions;
	}

	/**
	 * @return the sharedElimination
	 */
	public boolean isSharedElimination() {
		return sharedElimination;
	}

	/**
	 * @return the sharedHealth
	 */
	public boolean isSharedHealth() {
		return sharedHealth;
	}

	/**
	 * @return the sharedLength
	 */
	public boolean isSharedLength() {
		return sharedLength;
	}

	

	/**
	 * @param node Json node info about squad rules
	 * 
	 */
	public SquadRuleset(final JsonNode node) {
		allowBodyCollisions = false;
		sharedElimination = false;
		sharedHealth = false;
		sharedLength = false;
		
	}

	/**
	 * @param snakes List of snakes
	 */
	public void applyRules(final List<SnakeInfo> snakes) {
		// TODO Auto-generated method stub
		
	}

}
