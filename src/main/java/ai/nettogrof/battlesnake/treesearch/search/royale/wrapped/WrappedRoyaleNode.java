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
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleFourNode;

/**
 * @author carll
 *
 */
public class WrappedRoyaleNode extends AbstractRoyaleNode {

	/**
	 * @param snakes
	 * @param food
	 * @param hazard2
	 */
	public WrappedRoyaleNode(List<SnakeInfo> snakes, FoodInfo food, AbstractHazard hazard) {
		super(snakes, food, hazard);
		setScore();
	}

	@Override
	public AbstractNode createNode(List<SnakeInfo> snakes, AbstractNode currentNode) {
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

			adjustHazardScore();
			addScoreDistanceAll();

		}

		updateScoreRatio();

	}
}
