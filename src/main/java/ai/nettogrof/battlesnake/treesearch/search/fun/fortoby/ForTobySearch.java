/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.fun.fortoby;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleSearch;

/**
 * @author carl.lajeunesse
 *
 */
public class ForTobySearch extends RoyaleSearch {

	/**
	 * Basic constructor
	 */
	public ForTobySearch() {
		super();
	}

	/**
	 * Constructor used to expand the tree once.
	 * 
	 * @param root   Root node
	 * @param width  Board width
	 * @param height Board height
	 */
	public ForTobySearch(final AbstractNode root, final int width, final int height) {
		super(root, width, height);
	}

	/**
	 * Constructor used to expand to do the tree search.
	 * 
	 * @param root      Root node
	 * @param width     Board width
	 * @param height    Board height
	 * @param starttime starting time for the search in millisecond
	 * @param timeout   the time limit to run the search
	 */
	public ForTobySearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout) {
		super(root, width, height, starttime, timeout);

	}

	@Override
	public void run() {
		for (int i = 0; i < 12; i++) {
			generateChild(getSmallestChild(root));
		}
		executeMCTS();

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

		if (snakeInfo.isAlive() && !"Toby Flendersnake".equals(snakeInfo.getName())) {
			final int head = snakeInfo.getHead();

			if (head / 1000 > 0) {
				addMove(head - 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
			}

			if (head / 1000 < width - 1) {
				addMove(head + 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
			}

			if (head % 1000 > 0) {
				addMove(head - 1, allSnakes, snakeInfo, node, listNewSnakeInfo);
			}
			if (head % 1000 < height - 1) {
				addMove(head + 1, allSnakes, snakeInfo, node, listNewSnakeInfo);
			}
		} else if (snakeInfo.isAlive()) {

			tobyMove(snakeInfo.getHead(), allSnakes, snakeInfo, node, listNewSnakeInfo);

		}
		return listNewSnakeInfo;
	}

	private void tobyMove(final int head, final List<SnakeInfo> allSnakes, final SnakeInfo snakeInfo,
			final AbstractNode node, final List<SnakeInfo> listNewSnakeInfo) {

		if (head % 1000 == 0 && head / 1000 != 0) {
			addMove(head - 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
		} else if (head / 1000 == width - 1) {
			addMove(head - 1, allSnakes, snakeInfo, node, listNewSnakeInfo);
		} else if (head % 1000 == height - 1) {
			addMove(head + 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
		} else {
			addMove(head + 1, allSnakes, snakeInfo, node, listNewSnakeInfo);
		}

	}

}
