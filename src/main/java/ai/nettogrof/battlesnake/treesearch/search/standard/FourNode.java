/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.standard;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.MINIMUN_SNAKE;

/**
 * This FourNode class must be use when only 3 or 4 snakes left, and in standard
 * mode.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class FourNode extends AbstractStandardNode {

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes   List of snakes
	 * @param foodInfo Food information
	 */
	public FourNode(final List<SnakeInfo> snakes, final FoodInfo foodInfo) {
		super(snakes, foodInfo);		
		setScore();
	}

	/**
	 * Uses to create fourNode type
	 */
	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes, final AbstractNode currentNode) {
		return new FourNode(snakes, currentNode.getFood());
	}

	/**
	 * Sets the node score
	 */
	private void setScore() {
		addBasicLengthScore();

		final int head = snakes.get(0).getHead();
		addScoreDistance(head);
		adjustBorderScore(head);
		addSizeCompareScore();

		if (snakes.size() >= MINIMUN_SNAKE) {
			int nbAlive = 0;
			for (final SnakeInfo s : snakes) {
				if (s.isAlive()) {
					nbAlive++;
				}
			}
			if (nbAlive < MINIMUN_SNAKE) {
				exp = false;
			}
		} else if (isSingleSnake()) {
			score[0] += BattleSnakeConstants.MAX_SCORE;
		}
	}

}
