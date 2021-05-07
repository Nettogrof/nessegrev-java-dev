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
	 * Basic constructor
	 */
	public LeftRoyaleSearch() {
		super();
	}

	/**
	 * Constructor used to expand the tree once.
	 * 
	 * @param root   Root node
	 * @param width  Board width
	 * @param height Board height
	 */
	public LeftRoyaleSearch(final AbstractNode root, final int width, final int height) {
		super(root, width, height);
		left_neck = -1;
		right_neck = 1;
		down_neck = 1000;
		up_neck = -1000;
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
	public LeftRoyaleSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout) {
		super(root, width, height, starttime, timeout);
		left_neck = -1;
		right_neck = 1;
		down_neck = 1000;
		up_neck = -1000;

	}

}
