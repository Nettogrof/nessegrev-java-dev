/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.standard.challenge;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.BASIC_SCORE;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.snakes.common.SnakeGeneticConstants;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.AbstractStandardNode;

/**
 * This Solo node class must be use for challenge only
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class SoloNode extends AbstractStandardNode {
	/**
	 * Index challenge
	 */
	public static int challengeType = 1;

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes List of snakes
	 * @param food   Food information
	 */
	public SoloNode(final List<SnakeInfo> snakes, final FoodInfo food) {
		super(snakes, food);
		switch (challengeType) {

		case 1:
			soloSurvival();
			break;
		case 2:
			longSnake();
			break;
		case 3:
			friendly();
			break;
		case 4:
			fourCorner();
			break;
		case 5:
			fullBoard();
			break;
		default:
			addBasicLengthScore();
			break;// Training challenge
		}

		updateScoreRatio();
	}

	/**
	 * Evaluation for full board challenge
	 */
	private void fullBoard() {
		// TODO Create Evaluation for full board challenge

	}

	/**
	 * Evaluation for four corner challenge
	 */
	private void fourCorner() {
		// TODO Create Evaluation for four corner challenge

	}

	/**
	 * Evaluation for friendly challenge
	 */
	private void friendly() {
		// TODO Create Evaluation for "friendly" challenge

	}

	/**
	 * Evaluation for long snake challenge
	 */
	private void longSnake() {
		// TODO Create Evaluation for long snake challenge

	}

	/**
	 * Evaluation for soloSurvival challenge
	 */
	private void soloSurvival() {
		score[0] = 200 - snakes.get(0).getSnakeBody().size() * 2;

	}

	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes, final AbstractNode currentNode) {
		return new SoloNode(snakes, currentNode.getFood());
	}

	/**
	 * Update the score ratio
	 */
	@Override
	public void updateScoreRatio() {
		if (snakes.size() > 1) {
			float totalOther = BASIC_SCORE;
			for (int i = 1; i < score.length; i++) {
				totalOther += score[i];
			}

			scoreRatio = (float) (score[0] / (float) totalOther);
			if (scoreRatio == 0.0 || scoreRatio > SnakeGeneticConstants.stopExpandLimit) {
				exp = false;
			}
		} else {
			scoreRatio = score[0];
		}
	}

}
