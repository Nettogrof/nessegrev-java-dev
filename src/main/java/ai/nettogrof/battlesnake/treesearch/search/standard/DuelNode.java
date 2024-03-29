package ai.nettogrof.battlesnake.treesearch.search.standard;

import java.util.List;

import ai.nettogrof.battlesnake.info.BoardInfo;
import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractEvaluationNode;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.MINIMUN_SNAKE;

/**
 * This Duel node class must be use when only 2 snakes left, and in standard
 * mode. Used by Nessegrev-Gamma in the Spring 2021 league
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class DuelNode extends AbstractEvaluationNode {

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes    List of snakes
	 * @param foodInfo  Food information
	 * @param boardInfo Board Information
	 */
	public DuelNode(final List<SnakeInfo> snakes, final FoodInfo foodInfo, final BoardInfo boardInfo) {
		super(snakes, foodInfo, boardInfo);
		if (countSnakeAlive() < MINIMUN_SNAKE) {
			setWinnerMaxScore();
		} else {
			setScore();
		}
		updateScoreRatio();
	}

	/**
	 * Sets the node score
	 */
	private void setScore() {

		listAreaControl();
		for (int i = 0; i < snakes.size(); i++) {
			score[i] += snakes.get(i).getHealth() / 250f;
			if (snakes.get(i).getHealth() <= 0 || !snakes.get(i).isAlive()) {
				score[i] = 0f;
			}
		}

	}

	/**
	 * Uses to create duel node
	 */
	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes, final AbstractNode currentNode) {
		return new DuelNode(snakes, currentNode.getFood(), boardInfo);
	}

}