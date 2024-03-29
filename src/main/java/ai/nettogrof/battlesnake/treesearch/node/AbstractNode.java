package ai.nettogrof.battlesnake.treesearch.node;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.info.BoardInfo;
import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.hazard.AbstractHazard;
import ai.nettogrof.battlesnake.snakes.common.SnakeGeneticConstants;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.BASIC_SCORE;

/**
 * This abstract node class is the based of all node class, provide basic method
 * use in any node.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractNode {

	/**
	 * Board width useful for some evaluation method
	 */
	protected BoardInfo boardInfo;

	/**
	 * Single posssible move
	 */
	protected static final int ONE = 1;

	/**
	 * List of child node
	 */
	protected List<AbstractNode> child = new ArrayList<>();

	/**
	 * List of snakes
	 */
	protected List<SnakeInfo> snakes;

	/**
	 * Food information
	 */
	protected FoodInfo food;

	/**
	 * Hazard information
	 */
	protected AbstractHazard hazard;

	/**
	 * Current scoreRatio
	 */
	protected float scoreRatio;

	/**
	 * Array of score of each snake
	 */
	public float[] score;

	/**
	 * Number of possible move of our snake
	 */
	protected int possibleMove;

	/**
	 * Counts of all node of that branch
	 */
	protected int allChildsCount = 1;

	/**
	 * Does that node can be explore
	 */
	protected boolean exp = true;

	/**
	 * Basic constructor
	 */
	protected AbstractNode() {
		// Empty top level
	}

	/**
	 * Constructor with the minimal info needed
	 * 
	 * @param snakes    List of snakes
	 * @param food      Food information
	 * @param boardInfo Board Info
	 */
	protected AbstractNode(final List<SnakeInfo> snakes, final FoodInfo food, final BoardInfo boardInfo) {
		this.snakes = snakes;
		this.food = food;
		this.boardInfo = boardInfo;
	}

	/**
	 * Gets food information,
	 * 
	 * @return return the food information
	 */
	public FoodInfo getFood() {
		return food;
	}

	/**
	 * Gets list of snakes from this node
	 * 
	 * @return Lost of snakeInfo
	 */
	public List<SnakeInfo> getSnakes() {
		return snakes;
	}

	/**
	 * Return counts of all node of that branch
	 *
	 * @return All Childs count
	 */
	public int getChildCount() {

		return allChildsCount;
	}

	/**
	 * Gets the score ratio, the score ratio is compute has follow Our snake score /
	 * (all others snakes score added)
	 * 
	 * @return float score ratio
	 */
	public float getScoreRatio() {
		return scoreRatio;
	}

	/**
	 * Add a child to this node
	 * 
	 * @param newChild Node to be added
	 */
	public void addChild(final AbstractNode newChild) {
		child.add(newChild);
		allChildsCount++;
	}

	/**
	 * Update this node score
	 */
	public abstract void updateScore();

	/**
	 * Update the score ratio
	 */
	public void updateScoreRatio() {
		float totalOther = BASIC_SCORE;
		for (int i = 1; i < score.length; i++) {
			totalOther += score[i];
		}

		scoreRatio = score[0] / totalOther;
		if (scoreRatio == 0.0 || scoreRatio > SnakeGeneticConstants.getStopExpandLimit()) {
			exp = false;
		}
	}

	/**
	 * Update the child count
	 */
	protected void updateChildCount() {
		allChildsCount = 1;
		for (final AbstractNode c : child) {
			allChildsCount += c.getChildCount();
		}

	}

	/**
	 * Gets list of child from this node
	 * 
	 * @return list of child
	 */
	public List<AbstractNode> getChild() {
		return child;
	}

	/**
	 * Create a node from the same type
	 * 
	 * @param snakes      List of snake Info
	 * @param currentNode Current node
	 * @return new node from this type
	 */
	public abstract AbstractNode createNode(List<SnakeInfo> snakes, AbstractNode currentNode);

	/**
	 * Return true if there's just one snake
	 * 
	 * @return if there's just one snake
	 */
	public boolean isSingleSnake() {
		return snakes.size() == 1;
	}

	/**
	 * Return the hazard object
	 * 
	 * @return the hazard
	 */
	public AbstractHazard getHazard() {
		return hazard;
	}

	/**
	 * Set possible move
	 * 
	 * @param possibleMove int of number of possible move
	 */
	public void setPossibleMove(final int possibleMove) {
		this.possibleMove = possibleMove;
	}

	/**
	 * Does the node is Expendable
	 * 
	 * @return the exp
	 */
	public boolean isExp() {
		return exp;
	}

	/**
	 * @param exp the exp to set
	 */
	public void setExp(final boolean exp) {
		this.exp = exp;
	}

	/**
	 * @return the boardInfo
	 */
	public BoardInfo getBoardInfo() {
		return boardInfo;
	}

}
