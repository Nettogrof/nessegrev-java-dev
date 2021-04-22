package ai.nettogrof.battlesnake.treesearch.node;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstant;
import gnu.trove.list.array.TIntArrayList;

/**
 * This abstract node class is the based of all node class, provide basic method use in any node.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractNode {
	
	/**
	 * Board width useful for some evaluation method
	 */
	public static int width;
	
	/**
	 * Board height useful for some evaluation method
	 */
	public static int height;
	
	/**
	 * List of child node
	 */
	protected transient List<AbstractNode> child = new ArrayList<>();
	
	/**
	 * List of snakes 
	 */
	protected transient List<SnakeInfo> snakes;
	
	/**
	 *  Food information 
	 */
	protected transient FoodInfo food;
	
	/**
	 *  Current scoreRatio  
	 */
	protected transient float scoreRatio;
	
	/**
	 *  Array of score of each snake 
	 */
	public transient float score[];
	
	/**
	 *  Number of possible move of our snake
	 */
	public transient int possibleMove;
	
	/**
	 *  Counts of all node of that branch 
	 */
	public transient int allChildsCount = 1;
	
	/**
	 *  Does that node can be explore 
	 */
	public transient boolean exp = true;

	/**
	 * Basic  constructor
	 */
	public AbstractNode() {
		// Empty top level
	}

	/**
	 * Constructor with the minimal info needed
	 * @param snakes  List of snakes
	 * @param food Food information
	 */
	public AbstractNode(final List<SnakeInfo> snakes,final FoodInfo food) {
		this.snakes =  snakes;
		this.food = food;
	}

	/**
	 * Gets food information, 
	 * @return return the food information
	 */
	public FoodInfo getFood() {
		return food;
	}

	/**
	 * Gets list of snakes from this node
	 * @return  Lost of snakeInfo
	 */
	public List< SnakeInfo> getSnakes() {
		return snakes;
	}

	/**
	 * Return counts of all node of that branch 
	 *
	 * @return  All Childs count
	 */
	public int getChildCount() {

		return allChildsCount;
	}

	/**
	 * Gets the score ratio,  the score ratio is compute has follow
	 * Our snake score  / (all others snakes score added)
	 * @return float score ratio
	 */
	public float getScoreRatio() {
		return scoreRatio;
	}

	/**
	 * Add a child to this node
	 * @param newChild  Node to be added
	 */
	public void addChild(final AbstractNode newChild) {
		child.add(newChild);
		allChildsCount++;
	}

	/**
	 * Update this node score
	 */
	public void updateScore() {

		if (!child.isEmpty()) {

			if (possibleMove == 1) {
				updateScoreSinglePossibleMove();
			} else {
				updateScoreMultiplePossibleMove();
			}
		}
	
		updateScoreRatio();
		updateChildCount();

	}
	
	/**
	 *  Update the score ratio
	 */
	public void updateScoreRatio() {
		float totalOther = BattleSnakeConstant.BASIC_SCORE;
		for (int i = 1; i < score.length; i++) {
			totalOther += score[i];
		}

		scoreRatio= (float) (score[0] / (float) totalOther);
		if (scoreRatio == 0.0 || scoreRatio > BattleSnakeConstant.STOP_EXPAND_LIMIT) {
			exp =false;
		}
	}

	/**
	 * Update score if more than 1 possible move
	 */
	private void updateScoreMultiplePossibleMove() {
		final ArrayList<float[]> scores = new ArrayList<>();

		initPayoffMatrix(scores);
		computePayoffMatrix(scores);
		
	}

	/**
	 * Compute the payoff Matric
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
			for(final AbstractNode current: child) {
				for (int j = 0; j < current.score.length; j++) {
					score[j] += current.score[j];
				}
			}

		}
		
	}

	/**
	 * Find the best index in the payoff matrix 
	 * @param scores List of score array
	 * @return int the index 
	 */
	private int findBestIndex(final List<float[]> scores) {
		int ind = -1;
		float ration = -1;
		for (int i = 0; i < scores.size(); i++) {
			float other = 0;
			for (int j = 1; j < scores.get(i).length; j++) {
				if (scores.get(i)[j] != BattleSnakeConstant.INVALID_SCORE) {
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
	 * @param scores List of score array 
	 */
	private void initPayoffMatrix(final List<float[]> scores) {
		final TIntArrayList head = new TIntArrayList();
		for (final AbstractNode c : child) {
			final int currentHead = c.getSnakes().get(0).getHead();
			if (head.contains(currentHead)) {

				float[] currentS = scores.get(head.indexOf(currentHead));
				currentS[0] = c.score[0] < currentS[0] ? c.score[0] : currentS[0];
				for (int i = 1; i < c.score.length; i++) {

					currentS[i] = c.score[i] > currentS[i] ? c.score[i] : currentS[i];
				}

				if (score.length > c.score.length) {
					for (int i = c.score.length; i < score.length; i++) {
						currentS[i] = BattleSnakeConstant.BASIC_SCORE;
					}
				}
			} else {
				head.add(currentHead);
				float[] beta = { BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE, BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE};
				System.arraycopy(c.score, 0, beta, 0, c.score.length);
				/*
				 * for (int i = 0; i < c.score.length; i++) { beta[i] = c.score[i]; }
				 */

				for (int i = c.score.length; i < score.length; i++) {
					beta[i] = BattleSnakeConstant.BASIC_SCORE;
				}

				scores.add(beta);
			}

		}
		
	}

	/**
	 * Update the child count 
	 */
	private void updateChildCount() {
		allChildsCount = 1;
		for (final AbstractNode c : child) {
			allChildsCount += c.getChildCount();
		}

	}

	/**
	 * Update score if just one possible move.
	 */
	private void updateScoreSinglePossibleMove() {
		for (int i = 1; i < score.length; i++) {
			score[i] = 0;
		}
		score[0] = BattleSnakeConstant.MAX_SCORE;
		for (final AbstractNode current : child) {
			score[0] = current.score[0] < score[0] ? current.score[0] : score[0];
			for (int i = 1; i < current.score.length; i++) {

				score[i] = current.score[i] > score[i] ? current.score[i] : score[i];
			}
		}

	}

	/**
	 * Gets list of  child from this node
	 * @return list of  child
	 */
	public List<AbstractNode> getChild() {
		return  child;
	}

	/**
	 * Create a node from the same type
	 * @param snakes List of snake Info
	 * @param currentNode Current node 
	 * @return new node from this type
	 */
	public abstract AbstractNode createNode(List<SnakeInfo> snakes, AbstractNode currentNode);

}
