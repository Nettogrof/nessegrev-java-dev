/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.royale.wrapped;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.AbstractRoyaleSearch;

/**
 * This regular search was used during the Spring 2022 league Nessegrev-Beta
 * snake for the wrapped mode  It start by search the smallest branch and expand it (12 times) then :
 * 1. find the best branch from the previous best branch that the score doesn't
 * change (root the first time) 2. expand the best leaf node 3. update score of
 * the branch keep branch info if score doesn't changed
 * 
 * Repeat those 3 steps until no time left or the root is not more expandable.
 * 
 * 
 * @author carl.lajeunesse
 * @version Spring 2022
 */
public class WrappedRoyaleSearch extends AbstractRoyaleSearch {

	/**
	 * Constructor used to expand to do the tree search.
	 * 
	 * @param root      Root node
	 * @param width     Board width
	 * @param height    Board height
	 * @param starttime starting time for the search in millisecond
	 * @param timeout   the time limit to run the search
	 * @param rules 	Game ruleset
	 */
	public WrappedRoyaleSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout, final GameRuleset rules) {
		super(root, width, height, starttime, timeout, rules);
	}

	@Override
	protected List<SnakeInfo> generateSnakeInfoDestination(final SnakeInfo snakeInfo, final AbstractNode node,
			final List<SnakeInfo> allSnakes) {
		final ArrayList<SnakeInfo> listNewSnakeInfo = new ArrayList<>();

		if (snakeInfo.isAlive()) {
			final int head = snakeInfo.getHead();

			if (head / 1000 > 0) {
				addMove(head - 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
			} else {
				addMove(head + (width - 1) * 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
			}

			if (head / 1000 < width - 1) {
				addMove(head + 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
			} else {
				addMove(head % 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
			}

			if (head % 1000 > 0) {
				addMove(head - 1, allSnakes, snakeInfo, node, listNewSnakeInfo);
			} else {
				addMove(head + (height - 1), allSnakes, snakeInfo, node, listNewSnakeInfo);
			}
			if (head % 1000 < height - 1) {
				addMove(head + 1, allSnakes, snakeInfo, node, listNewSnakeInfo);
			} else {
				addMove(head / 1000 * 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
			}
		}
		return listNewSnakeInfo;
	}
	@Override
	protected boolean freeSpace(final int square, final List<SnakeInfo> snakes, final SnakeInfo currentSnake) {
		
		boolean free = true;
		for (int i = 0; i < snakes.size() && free; i++) {
			free = !snakes.get(i).isSnake(square);
		}
		return free;
	}

	@Override
	public void run() {

		for (int i = 0; i < 12; i++) {
			generateChild(getSmallestChild(root));
		}
		executeMCTS();

	}

}
