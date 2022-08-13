/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.node;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.BASIC_SCORE;
import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.INVALID_SCORE;
import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.MAX_SCORE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import gnu.trove.list.array.TIntArrayList;

/**
 * This abstract node class is the based of all node class, provide basic update
 * Score Currently using Paranoid only, I hope to add MaxN soon
 * 
 * @author carl.lajeunesse
 * @version Fall 2021
 *
 */
public abstract class AbstractDecisionNode extends AbstractNode {

	/**
	 * Basic constructor
	 */
	protected AbstractDecisionNode() {
		super();
	}

	/**
	 * Basic constructor
	 * 
	 * @param snakes list of snakes, and food info
	 * @param food   food info
	 */
	protected AbstractDecisionNode(final List<SnakeInfo> snakes, final FoodInfo food) {
		super(snakes, food);
	}

	/**
	 * Update this node score
	 */
	@Override
	public void updateScore() {
		if (!this.getChild().isEmpty()) {

			if (this.possibleMove == ONE) {
				updateScoreSinglePossibleMove();
			} else {
				updateScoreMultiplePossibleMove();
			}
		}

		updateScoreRatio();
		updateChildCount();

	}

	/**
	 * Update score if more than 1 possible move
	 * 
	 */
	private void updateScoreMultiplePossibleMove() {
		final ArrayList<float[]> scores = new ArrayList<>();

		initPayoffMatrix(scores);
		computePayoffMatrix(scores);

	}

	/**
	 * Compute the payoff Matric
	 * 
	 * @param scores List of score array
	 */
	private void computePayoffMatrix(final List<float[]> scores) {
		final int ind = findBestIndex(scores);
		if (ind > -1) {
			for (int i = 0; i < scores.get(ind).length && scores.get(ind)[i] != -500; i++) {
				score[i] = scores.get(ind)[i];
			}

			if (scores.get(ind).length < score.length) {
				for (int i = scores.get(ind).length; i < score.length; i++) {
					score[i] = (float) 0.0001;
				}
			}
		} else {
			for (int i = 0; i < score.length; i++) {
				score[i] = 0;
			}
			for (final AbstractNode current : getChild()) {
				for (int j = 0; j < current.score.length; j++) {
					score[j] += current.score[j];
				}
			}

		}

	}

	/**
	 * Find the best index in the payoff matrix
	 * 
	 * @param scores List of score array
	 * @return int the index
	 */
	private int findBestIndex(final List<float[]> scores) {
		int ind = -1;
		float ration = -1;
		for (int i = 0; i < scores.size(); i++) {
			float other = 0;
			for (int j = 1; j < scores.get(i).length; j++) {
				if (scores.get(i)[j] != INVALID_SCORE) {
					other += scores.get(i)[j];
				}
			}
			final float currentratio = scores.get(i)[0] / other;
			if (currentratio > ration) {
				ration = currentratio;
				ind = i;
			}
		}
		return ind;
	}

	/**
	 * Initiate the payoff matrix
	 * 
	 * @param scores List of score array
	 */
	private void initPayoffMatrix(final List<float[]> scores) {
		final TIntArrayList head = new TIntArrayList();
		for (final AbstractNode c : getChild()) {
			final int currentHead = c.getSnakes().get(0).getHead();
			if (head.contains(currentHead)) {

				float[] currentS = scores.get(head.indexOf(currentHead));
				currentS[0] = c.score[0] < currentS[0] ? c.score[0] : currentS[0];
				for (int i = 1; i < c.score.length; i++) {

					currentS[i] = c.score[i] > currentS[i] ? c.score[i] : currentS[i];
				}

				if (score.length > c.score.length) {
					for (int i = c.score.length; i < score.length; i++) {
						currentS[i] = BASIC_SCORE;
					}
				}
			} else {
				head.add(currentHead);
				float[] beta = new float[score.length];
				Arrays.fill(beta, INVALID_SCORE);
					
				System.arraycopy(c.score, 0, beta, 0, c.score.length);

				for (int i = c.score.length; i < score.length; i++) {
					beta[i] = BASIC_SCORE;
				}

				scores.add(beta);
			}

		}

	}

	/**
	 * Update score if just one possible move.
	 * 
	 */
	private void updateScoreSinglePossibleMove() {
		for (int i = 1; i < score.length; i++) {
			score[i] = 0;
		}
		score[0] = MAX_SCORE;
		for (final AbstractNode current : getChild()) {
			score[0] = current.score[0] < score[0] ? current.score[0] : score[0];
			for (int i = 1; i < current.score.length; i++) {

				score[i] = current.score[i] > score[i] ? current.score[i] : score[i];
			}
		}

	}
	
	/**
	 * Count the number of snake still alive
	 * 
	 * @return Number of snake alive
	 */
	protected int countSnakeAlive() {

		int nbAlive = 0;

		for (final SnakeInfo s : snakes) {
			if (s.isAlive()) {
				nbAlive++;
			}
		}

		return nbAlive;

	}

}
