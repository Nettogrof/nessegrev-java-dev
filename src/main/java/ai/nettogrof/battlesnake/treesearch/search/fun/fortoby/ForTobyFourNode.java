/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.fun.fortoby;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.BASIC_SCORE;
import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.MINIMUN_SNAKE;
import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.STOP_EXPAND_LIMIT;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.HazardInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.AbstractRoyaleNode;

/**
 * This For Toby Royale FourNode class must be use when only 3 or 4 snakes left,
 * and in royale/standard mode. This is for a funny snake for a weekly meet-up.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class ForTobyFourNode extends AbstractRoyaleNode {

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes   List of snakes
	 * @param foodInfo Food information
	 * @param hazard   Hazard Info
	 */
	public ForTobyFourNode(final List<SnakeInfo> snakes, final FoodInfo foodInfo, final HazardInfo hazard) {
		super(snakes, foodInfo, hazard);
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
			listAreaControl();
			adjustHazardScore();

		}
		adjustTobyScore();

		updateScoreRatio();

	}

	/**
	 * Adjust Toby score
	 */
	private void adjustTobyScore() {
		for (int i = 1; i < score.length; i++) {
			if ("Toby Flendersnake".equals(snakes.get(i).getName())) {
				if (snakes.get(i).isAlive()) {
					score[i] = numberOfEmptySpaceInFront(i);
					score[0] += score[i] * 10;
				} else {
					score[0] = score[0] / 4;
				}

			}
		}
	}

	/**
	 * @param snakeIndex Index of Toby Snake
	 * @return score
	 */
	private float numberOfEmptySpaceInFront(final int snakeIndex) {
		final int square = snakes.get(snakeIndex).getHead();
		int count = 0;
		if (square % 1000 == 0 && square / 1000 != 0) {
			count += freeBorder(square - 1000, 1);
		} else if (square / 1000 == width - 1) {
			count += freeBorder(square - 1, 1);
		} else if (square % 1000 == height - 1) {
			count += freeBorder(square + 1000, 1);
		} else {
			count += freeBorder(square + 1, 1);
		}

		return count;
	}

	/**
	 * @param square Square to check if empty
	 * @param depth  Depth Level
	 * @return number of square empty
	 */
	private int freeBorder(final int square, final int depth) {
		int count = 0;
		boolean free = true;
		for (int i = 0; i < snakes.size() && free; i++) {
			free = !snakes.get(i).isSnake(square);
		}

		if (free && depth < 40) {
			count++;
			if (square % 1000 == 0 && square / 1000 != 0) {
				count += freeBorder(square - 1000, depth + 1);
			} else if (square / 1000 == width - 1) {
				count += freeBorder(square - 1, depth + 1);
			} else if (square % 1000 == height - 1) {
				count += freeBorder(square + 1000, depth + 1);
			} else {
				count += freeBorder(square + 1, depth + 1);
			}

		}
		return count;

	}

	/**
	 * Update the score ratio
	 */
	@Override
	public void updateScoreRatio() {
		float totalOther = BASIC_SCORE;
		if (score.length > MINIMUN_SNAKE) {
			for (int i = 1; i < score.length; i++) {
				if ("Toby Flendersnake".equals(snakes.get(i).getName())) {
					if (snakes.get(i).isAlive()) {
						score[0] += score[i] * 10;
					} else {
						score[0] = score[0] / 4;
					}
				} else {
					totalOther += score[i];
				}
			}
		} else if (score.length == MINIMUN_SNAKE) {
			if ("Toby Flendersnake".equals(snakes.get(1).getName())) {
				score[0] = score[1];
				totalOther = 100;
			} else {
				for (int i = 1; i < score.length; i++) {
					totalOther += score[i];
				}
			}

		}

		scoreRatio = (float) (score[0] / (float) totalOther);
		if (scoreRatio == 0.0 || scoreRatio > STOP_EXPAND_LIMIT) {
			exp = false;
		}
	}

	/**
	 * Uses to create ForTobyFourNode type
	 */
	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakeInfo, final AbstractNode currentNode) {
		return new ForTobyFourNode(snakeInfo, currentNode.getFood(), ((ForTobyFourNode) currentNode).getHazard());
	}

}
