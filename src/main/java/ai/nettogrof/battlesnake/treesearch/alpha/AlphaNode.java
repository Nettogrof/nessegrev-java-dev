package ai.nettogrof.battlesnake.treesearch.alpha;

import java.util.ArrayList;
import java.util.List;

import com.google.common.flogger.FluentLogger;

import ai.nettogrof.battlesnake.info.*;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This alpha node class is the for the snake Alpha, still a lot buggy. This
 * snake participated in the Communitech tournament in the Rookie division.
 * Still has some search bugs. It uses the minimax algorithm
 * 
 * @deprecated Because very buggy
 * @author carl.lajeunesse
 * @version Spring 2021
 */
@Deprecated
public class AlphaNode extends AbstractNode {
	/**
	 * Logger
	 */
	protected static transient FluentLogger log = FluentLogger.forEnclosingClass();

	/**
	 * Basic constructor
	 */
	public AlphaNode() {
		super();
	}

	/**
	 * Constructor with snakes and food information
	 * 
	 * @param snakes   List of snakes
	 * @param foodInfo Food information
	 */
	public AlphaNode(final List<SnakeInfo> snakes, final FoodInfo foodInfo) {
		super();
		this.snakes = snakes;
		food = foodInfo;
		score = new float[snakes.size()];

		for (int i = 0; i < score.length; i++) {
			score[i] = 0;
		}

		for (int i = 0; i < snakes.size(); i++) {
			score[i] = snakes.get(i).getSnakeBody().size() + snakes.get(i).getHealth() / 100;
		}

	}

	/**
	 * Constructor with snakes and food information
	 * 
	 * @param snakes   List of snakes
	 * @param foodInfo Food information
	 */
	public AlphaNode(final SnakeInfo[] snakes, final FoodInfo foodInfo) {
		super();
		this.snakes = new ArrayList<>();
		for (final SnakeInfo snake : snakes) {
			this.snakes.add(snake);
		}

		food = foodInfo;
		score = new float[snakes.length];

		for (int i = 0; i < score.length; i++) {
			score[i] = 0;
		}

		for (int i = 0; i < snakes.length; i++) {
			score[i] = snakes[i].getSnakeBody().size() + snakes[i].getHealth() / 100;
		}

	}

	/**
	 * Constructor with snakes and food information
	 * 
	 * @param snakes   single snakes
	 * @param foodInfo Food information
	 */
	public AlphaNode(final SnakeInfo snakes, final FoodInfo foodInfo) {
		super();
		this.snakes = new ArrayList<>();
		this.snakes.add(snakes);
		food = foodInfo;
		score = new float[1];

		for (int i = 0; i < score.length; i++) {
			score[i] = snakes.getSnakeBody().size() + snakes.getHealth() / 100;
		}
	}

	/**
	 * Update score of this node
	 */
	@Override
	public void updateScore() {

		if (child.isEmpty()) {

			for (int i = 0; i < score.length; i++) {
				score[i] = snakes.get(i).isAlive() ? snakes.get(i).getSnakeBody().size() : 0;
			}

		} else {

			for (int i = 0; i < score.length; i++) {
				score[i] = 0;
			}
			try {
				for (final AbstractNode node : child) {

					for (int j = 0; j < node.score.length; j++) {
						score[j] += node.score[j];
					}

				}
			} catch (Exception e) {
				log.atWarning().log(e.getMessage() + "\n" + e.getStackTrace());
			}
		}

		int subtotal = 1;
		for (final AbstractNode node : child) {
			subtotal += node.getChildCount();
		}
		allChildsCount = subtotal;

	}

	/**
	 * Returns all snakes score added
	 * 
	 * @return int
	 */
	public int getTotalScore() {
		if (score[0] == 0) {
			return 999_999;
		}
		int totalOther = 0;
		for (int i = 0; i < score.length; i++) {
			totalOther += Math.round(score[i]);
		}

		return totalOther;

	}

	/**
	 * Gets the best child node or the shortest branch
	 * 
	 * @param shortest boolean if return the shortest branch
	 * @return node
	 */
	public AbstractNode getBestChild(final boolean shortest) {
		updateScore();
		if (child.isEmpty()) {
			return this;
		}

		AbstractNode bestChild = null;
		if (snakes.size() == 1) {

			double maxR = -1000;

			for (final AbstractNode c : child) {
				if (c.getScoreRatio() > maxR) {
					maxR = c.getScoreRatio();
					bestChild = c;
				}

			}

		} else if (snakes.size() > 1) {

			if (shortest) {
				int countChild = Integer.MAX_VALUE;
				for (final AbstractNode c : child) {
					if (c.getChildCount() < countChild && c.getScoreRatio() > 0 && c.exp) {
						countChild = c.getChildCount();
						bestChild = c;
					}
				}
			} else {
				bestChild = bestChildForAll();
			}
		}
		if (bestChild == null) {
			exp = false;
			return this;
		}

		return ((AlphaNode) bestChild).getBestChild(shortest);
	}

	/**
	 * Kind of payoff matrix to retrieve the best child node
	 * 
	 * @return best child node
	 */
	private AbstractNode bestChildForAll() {
		AbstractNode bestChild = null;
		double maxR = -2000;

		for (final AbstractNode c : child) {
			if (c.getScoreRatio() > maxR) {
				maxR = c.getScoreRatio();
				bestChild = c;
			}
		}

		final int bestHead = bestChild.getSnakes().get(0).getHead();

		double minR = 9_999_000;

		for (final AbstractNode c : child) {
			if (c.getSnakes().get(0).getHead() == bestHead && c.getScoreRatio() < minR) {

				minR = c.getScoreRatio();
				bestChild = c;

			}
		}

		return bestChild;
	}

	@Override
	public int getChildCount() {
		return allChildsCount;
	}

	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes, final AbstractNode currentNode) {
		return new AlphaNode(snakes, currentNode.getFood());

	}

}
