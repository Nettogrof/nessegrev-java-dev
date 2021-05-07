/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.HazardInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.MINIMUN_SNAKE;

/**
 * This Royale Duel node class must be use when only 2 snakes left, and in
 * royale mode. Used by Nessegrev-Beta in the Spring 2021 league
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class RoyaleDuelNode extends AbstractRoyaleNode {

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes List of snakes
	 * @param food   Food information
	 */
	public RoyaleDuelNode(final List<SnakeInfo> snakes, final FoodInfo food) {
		super(snakes, food);
		setScore();
	}

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes List of snakes
	 * @param food   Food information
	 * @param hazard Hazard Information
	 */
	public RoyaleDuelNode(final List<SnakeInfo> snakes, final FoodInfo food, final HazardInfo hazard) {
		super(snakes, food, hazard);
		setScore();
	}

	/**
	 * Sets the node score
	 */
	private void setScore() {
		if (countSnakeAlive() < MINIMUN_SNAKE) {
			setWinnerMaxScore();
		} else {
			listAreaControl();
			adjustHazardScore();
		}
		updateScoreRatio();

	}

	/**
	 * Uses to create royale duel node
	 */
	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakeInfo, final AbstractNode currentNode) {
		return new RoyaleDuelNode(snakeInfo, currentNode.getFood(), ((RoyaleDuelNode) currentNode).getHazard());
	}

}
