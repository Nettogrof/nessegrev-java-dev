/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.royale.wrapped;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.MINIMUN_SNAKE;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.hazard.AbstractHazard;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.AbstractRoyaleNode;

/**
 * This Royale FourNode class must be use when only 3 or 4 snakes left, and in
 * royale mode.
 * 
 * @author carl.lajeunesse
 * @version Spring 2022
 */
public class WrappedRoyaleNode extends AbstractRoyaleNode {

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes   List of snakes
	 * @param foodInfo Food information
	 * @param hazard   Hazard Info
	 */
	public WrappedRoyaleNode(final List<SnakeInfo> snakes, final FoodInfo food, final AbstractHazard hazard) {
		super(snakes, food, hazard);
		setScore();
	}

	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes, final AbstractNode currentNode) {
		return new WrappedRoyaleNode(snakes, currentNode.getFood(), ((WrappedRoyaleNode) currentNode).getHazard());
	}

	/**
	 * Sets the node score
	 */
	private void setScore() {
		if (countSnakeAlive() < MINIMUN_SNAKE) {
			setWinnerMaxScore();
		} else {

			addBasicLengthScore();

			//adjustHazardScore();
			addScoreDistanceAll();

		}

		updateScoreRatio();

	}
}
