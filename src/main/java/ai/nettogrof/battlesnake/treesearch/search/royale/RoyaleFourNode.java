package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.HazardInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This Royale FourNode class must be use when only 3 or 4 snakes left, and in
 * royale mode.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class RoyaleFourNode extends AbstractRoyaleNode {

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes   List of snakes
	 * @param foodInfo Food information
	 * @param hazard   Hazard Info
	 */
	public RoyaleFourNode(final List<SnakeInfo> snakes, final FoodInfo foodInfo, final HazardInfo hazard) {
		super(snakes, foodInfo, hazard);
		setScore();

	}

	/**
	 * Sets the node score
	 */
	private void setScore() {
		if (countSnakeAlive() < 2) {
			setWinnerMaxScore();
		} else {
			addBasicLengthScore();
			listAreaControl();
			adjustHazardScore();

		}

		updateScoreRatio();

	}

	/**
	 * Uses to create fourNode type
	 */
	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakeInfo, final AbstractNode currentNode) {
		return new RoyaleFourNode(snakeInfo, currentNode.getFood(), ((RoyaleFourNode) currentNode).getHazard());
	}

}
