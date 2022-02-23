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
 * @author carll
 *
 */
public class WrappedRoyaleSearch extends AbstractRoyaleSearch {

	/**
	 * @param root
	 * @param width
	 * @param height
	 * @param starttime
	 * @param timeout
	 * @param rules
	 */
	public WrappedRoyaleSearch(AbstractNode root, int width, int height, long starttime, int timeout,
			GameRuleset rules) {
		super(root, width, height, starttime, timeout, rules);
		// TODO Auto-generated constructor stub
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
				addMove(head + (height - 1) , allSnakes, snakeInfo, node, listNewSnakeInfo);
			}
			if (head % 1000 < height - 1) {
				addMove(head + 1, allSnakes, snakeInfo, node, listNewSnakeInfo);
			}else {
				addMove(head / 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
			}
		}
		return listNewSnakeInfo;
	}

	@Override
	public void run() {

		for (int i = 0; i < 12; i++) {
			generateChild(getSmallestChild(root));
		}
		executeMCTS();

	}

}
