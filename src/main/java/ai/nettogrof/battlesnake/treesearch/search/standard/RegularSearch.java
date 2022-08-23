package ai.nettogrof.battlesnake.treesearch.search.standard;

import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This regular search was used during the Winter classic 2020 by my Beta and
 * Gamma snake It start by search the smallest branch and expand it (100 times)
 * then : 1. find the best branch from the root 2. expand the best leaf node 3.
 * expand 6 time the smallest branch from the best node (found in step 1).
 * 
 * Repeat those 3 steps until no time left or the root is not more expandable.
 * 
 * 
 * @author carl.lajeunesse
 * @version Winter 2020
 */
public class RegularSearch extends AbstractStandardSearch {

	/**
	 * Basic constructor
	 */
	public RegularSearch() {
		super();
	}

	/**
	 * Constructor used to expand the tree once.
	 * 
	 * @param root   Root node
	 * @param width  Board width
	 * @param height Board height
	 */
	public RegularSearch(final AbstractNode root, final int width, final int height) {
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
	public RegularSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout) {
		super(root, width, height);
		this.startTime = starttime;
		this.timeout = timeout;
	}

	@Override
	public void run() {

		// Expanding the smallest branch
		for (int i = 0; i < 100; i++) {
			generateChild(getSmallestChild(root));
		}

		long currentTime = System.currentTimeMillis() - startTime;
		while (cont && currentTime < timeout && root.isExp()) {
			final AbstractNode bestChild = getBestChild(root); // Find the best branch/leaf node
			generateChild(bestChild); // Expand the best leaf node

			// Expanding the smallest branch from the earlier leaf node
			generateChild(getSmallestChild(bestChild));
			generateChild(getSmallestChild(bestChild));
			generateChild(getSmallestChild(bestChild));
			generateChild(getSmallestChild(bestChild));
			generateChild(getSmallestChild(bestChild));
			generateChild(getSmallestChild(bestChild));
			Thread.yield(); // Use to let other thread some cpu time
			currentTime = System.currentTimeMillis() - startTime;

		}
	}
}
