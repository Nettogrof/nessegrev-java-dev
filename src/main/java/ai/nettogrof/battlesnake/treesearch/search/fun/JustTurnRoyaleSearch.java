package ai.nettogrof.battlesnake.treesearch.search.fun;

import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * Based on MCTS search this search prevent our snake to be straight. Our snake
 * will always turn.... always.
 * Royale mode
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class JustTurnRoyaleSearch extends AbstractLimitedMoveRoyaleSearch {

	/**
	 * Basic constructor
	 */
	public JustTurnRoyaleSearch() {
		super();
	}

	/**
	 * Constructor used to expand the tree once.
	 * 
	 * @param root   Root node
	 * @param width  Board width
	 * @param height Board height
	 */
	public JustTurnRoyaleSearch(final AbstractNode root,final  int width,final  int height) {
		super(root, width, height);
		leftNeck = -1000;
		rightNeck = 1000;
		downNeck = -1;
		upNeck = 1;
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
	public JustTurnRoyaleSearch(final AbstractNode root, final int width, final int height, final long starttime, final int timeout) {
		super(root, width, height, starttime, timeout);
		leftNeck = -1000;
		rightNeck = 1000;
		downNeck = -1;
		upNeck = 1;
	}

}
