package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.List;

import ai.nettogrof.battlesnake.info.BoardInfo;
import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.hazard.AbstractHazard;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.MINIMUN_SNAKE;

/**
 * This Royale FourNode class must be use when only 3 or 4 snakes left, and in
 * royale mode.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class RoyaleFourNode extends AbstractRoyaleNode {

	/**
	 * 
	 */
	private static final int FOUR = 4;

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes    List of snakes
	 * @param foodInfo  Food information
	 * @param hazard    Hazard Info
	 * @param boardInfo board information
	 */
	public RoyaleFourNode(final List<SnakeInfo> snakes, final FoodInfo foodInfo, final AbstractHazard hazard,
			final BoardInfo boardInfo) {
		super(snakes, foodInfo, hazard, boardInfo);
		setScore();

	}

	/**
	 * Sets the node score
	 */
	private void setScore() {
		if (countSnakeAlive() < MINIMUN_SNAKE) {
			setWinnerMaxScore();
		} else {

			addBasicLengthScore();
			if (snakes.size() < FOUR) {
				listAreaControl();
				adjustHazardScore();
			} else {
				addScoreDistance(snakes.get(0).getHead());
			}

		}

		updateScoreRatio();

	}

	/**
	 * Uses to create fourNode type
	 */
	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakeInfo, final AbstractNode currentNode) {
		return new RoyaleFourNode(snakeInfo, currentNode.getFood(), ((RoyaleFourNode) currentNode).getHazard(),
				boardInfo);
	}

}
