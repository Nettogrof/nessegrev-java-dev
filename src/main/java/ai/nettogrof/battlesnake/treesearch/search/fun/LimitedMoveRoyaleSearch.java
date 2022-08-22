/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.fun;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.AbstractRoyaleSearch;

/**
 * This abstract Standard search based on RoyaleSearch provide methods to
 * prevent snake to move in particular direction Royale mode
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class LimitedMoveRoyaleSearch extends AbstractRoyaleSearch {
	/**
	 * Prevent left move if head - neck equals that amount
	 */
	protected static int leftNeck = 5;
	/**
	 * Prevent right move if head - neck equals that amount
	 */
	protected static int rightNeck = 5;
	/**
	 * Prevent down move if head - neck equals that amount
	 */
	protected static int downNeck = 5;
	/**
	 * Prevent up move if head - neck equals that amount
	 */
	protected static int upNeck = 5;

	/**
	 * Constructor used to expand to do the tree search.
	 * 
	 * @param root      Root node
	 * @param width     Board width
	 * @param height    Board height
	 * @param starttime starting time for the search in millisecond
	 * @param timeout   the time limit to run the search
	 * @param rules		the game ruleset info
	 */
	public LimitedMoveRoyaleSearch(final AbstractNode root, final int width, final int height,
			final long starttime, final int timeout, final GameRuleset rules) {
		super(root, width, height, starttime, timeout, rules);
	}

	/**
	 * Generate all moves possible for a snake given.
	 * 
	 * @param snakeInfo Information about the snake
	 * @param node      Parent node
	 * @param allSnakes List of all snakes
	 * @return list of snakeinfo
	 */
	@Override
	protected List<SnakeInfo> generateSnakeInfoDestination(final SnakeInfo snakeInfo, final AbstractNode node,
			final List<SnakeInfo> allSnakes) {
		final ArrayList<SnakeInfo> listNewSnakeInfo = new ArrayList<>();

		if (snakeInfo.isAlive() && !snakeInfo.equals(allSnakes.get(0))) {
			moveSnake(snakeInfo, node, allSnakes,listNewSnakeInfo);
		} else if (snakeInfo.isAlive()) {

			selfDestination(snakeInfo.getHead(), allSnakes, snakeInfo, node, listNewSnakeInfo);

		}
		return listNewSnakeInfo;
	}

	/**
	 * 
	 * Generate all move possible for our snake, and check the snake direction to
	 * prevent some move.
	 * 
	 * @param head             Head position
	 * @param allSnakes        List of all snakes
	 * @param snakeInfo        Information about the snake
	 * @param node             Parent node
	 * @param listNewSnakeInfo List of new moves
	 */
	protected void selfDestination(final int head, final List<SnakeInfo> allSnakes, final SnakeInfo snakeInfo,
			final AbstractNode node, final List<SnakeInfo> listNewSnakeInfo) {
		if (head / 1000 > 0 && head - snakeInfo.getSnakeBody().get(1) != leftNeck) {
			addMove(head - 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
		}

		if (head / 1000 < width - 1 && head - snakeInfo.getSnakeBody().get(1) != rightNeck) {
			addMove(head + 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
		}

		if (head % 1000 > 0 && head - snakeInfo.getSnakeBody().get(1) != downNeck) {
			addMove(head - 1, allSnakes, snakeInfo, node, listNewSnakeInfo);
		}
		if (head % 1000 < height - 1 && head - snakeInfo.getSnakeBody().get(1) != upNeck) {
			addMove(head + 1, allSnakes, snakeInfo, node, listNewSnakeInfo);
		}

	}
	
	
	/**
	 *  Set config for Right snake (never turn left)
	 */
	public static void setRightOnly() {
		leftNeck = 1;
		rightNeck = -1;
		downNeck = -1000;
		upNeck = 1000;
	}
	
	/**
	 *  Set config for Left snake (never turn right)
	 */
	public static void setLeftOnly() {
		leftNeck = -1;
		rightNeck = 1;
		downNeck = 1000;
		upNeck = -1000;
	}
	
	/**
	 *  Set config for Just Turn snake (never go straight)
	 */
	public static void setJustTurn() {
		leftNeck = -1000;
		rightNeck = 1000;
		downNeck = -1;
		upNeck = 1;
	}
	
	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo snake, final int newHead, final AbstractNode currentNode) {
		return new SnakeInfo(snake, newHead, currentNode.getFood().isFood(newHead), false, rules);
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 12; i++) {
			generateChild(getSmallestChild(root));
		}
		executeMCTS();

	}
}
