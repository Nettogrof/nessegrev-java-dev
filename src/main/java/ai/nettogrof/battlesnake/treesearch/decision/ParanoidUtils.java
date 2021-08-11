/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.decision;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.BASIC_SCORE;
import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.INVALID_SCORE;
import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.MAX_SCORE;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import gnu.trove.list.array.TIntArrayList;

/**
 * @author carl.lajeunesse
 *
 */
public final class ParanoidUtils extends AbstractDecisionUtils {
	/**
	 * Single posssible move
	 */
	public final static int ONE = 1;

	

	@Override
	public void updateScore(final AbstractNode node) {
		if (!node.getChild().isEmpty()) {

			if (node.possibleMove == ONE) {
				updateScoreSinglePossibleMove(node);
			} else {
				updateScoreMultiplePossibleMove(node);
			}
		}

	}

	@Override
	public AbstractNode getBestChild(final AbstractNode node) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Update score if more than 1 possible move
	 * 
	 * @param node  Node to evaluate
	 */
	private void updateScoreMultiplePossibleMove(final AbstractNode node) {
		final ArrayList<float[]> scores = new ArrayList<>();

		initPayoffMatrix(scores,node);
		computePayoffMatrix(scores,node);

	}

	/**
	 * Compute the payoff Matric
	 * 
	 * @param scores List of score array
	 * @param node  Node to evaluate
	 */
	private void computePayoffMatrix(final List<float[]> scores,final AbstractNode node) {
		final int ind = findBestIndex(scores);
		if (ind > -1) {
			for (int i = 0; i < scores.get(ind).length && scores.get(ind)[i] != -500; i++) {
				node.score[i] = scores.get(ind)[i];
			}

			if (scores.get(ind).length < node.score.length) {
				for (int i = scores.get(ind).length; i < node.score.length; i++) {
					node.score[i] = (float) 0.0001;
				}
			}
		} else {
			for (int i = 0; i < node.score.length; i++) {
				node.score[i] = 0;
			}
			for (final AbstractNode current : node.getChild()) {
				for (int j = 0; j < current.score.length; j++) {
					node.score[j] += current.score[j];
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
	 * @param node  Node to evaluate
	 */
	private void initPayoffMatrix(final List<float[]> scores,final AbstractNode node) {
		final TIntArrayList head = new TIntArrayList();
		for (final AbstractNode c : node.getChild()) {
			final int currentHead = c.getSnakes().get(0).getHead();
			if (head.contains(currentHead)) {

				float[] currentS = scores.get(head.indexOf(currentHead));
				currentS[0] = c.score[0] < currentS[0] ? c.score[0] : currentS[0];
				for (int i = 1; i < c.score.length; i++) {

					currentS[i] = c.score[i] > currentS[i] ? c.score[i] : currentS[i];
				}

				if (node.score.length > c.score.length) {
					for (int i = c.score.length; i < node.score.length; i++) {
						currentS[i] = BASIC_SCORE;
					}
				}
			} else {
				head.add(currentHead);
				float[] beta = { INVALID_SCORE, INVALID_SCORE, INVALID_SCORE, INVALID_SCORE, INVALID_SCORE,
						INVALID_SCORE, INVALID_SCORE, INVALID_SCORE, INVALID_SCORE };
				System.arraycopy(c.score, 0, beta, 0, c.score.length);

				for (int i = c.score.length; i < node.score.length; i++) {
					beta[i] = BASIC_SCORE;
				}

				scores.add(beta);
			}

		}

	}
	
	/**
	 * Update score if just one possible move.
	 * 
	 * @param node  Node to evaluate
	 */
	private void updateScoreSinglePossibleMove(final AbstractNode node) {
		for (int i = 1; i < node.score.length; i++) {
			node.score[i] = 0;
		}
		node.score[0] = MAX_SCORE;
		for (final AbstractNode current : node.getChild()) {
			node.score[0] = current.score[0] < node.score[0] ? current.score[0] : node.score[0];
			for (int i = 1; i < current.score.length; i++) {

				node.score[i] = current.score[i] > node.score[i] ? current.score[i] : node.score[i];
			}
		}

	}

}
