package ai.nettogrof.battlesnake.treesearch.search.fun;

import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * Based on MCTS search this search prevent our snake to turn right. So that
 * snake can't be right For Royale-mode
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class LeftRoyaleSearch extends AbstractLimitedMoveRoyaleSearch {

	/**
	 * Constructor used to expand to do the tree search.
	 * 
	 * @param root      Root node
	 * @param width     Board width
	 * @param height    Board height
	 * @param starttime starting time for the search in millisecond
	 * @param timeout   the time limit to run the search
	 */
	public LeftRoyaleSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout) {
		super(root, width, height, starttime, timeout);
		leftNeck = -1;
		rightNeck = 1;
		downNeck = 1000;
		upNeck = -1000;

	}

}
