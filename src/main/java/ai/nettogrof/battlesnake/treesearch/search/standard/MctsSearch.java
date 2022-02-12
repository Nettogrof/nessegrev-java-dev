/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.standard;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This MCTS search was used during the Spring 2021 league Nessegrev-Gamma snake
 * It start by search the smallest branch and expand it (500 times) then : 1.
 * find the best branch from the previous best branch that the score doesn't
 * change (root the first time) 2. expand the MCTS leaf node (ratio between best
 * / less explored ) 3. update score of the branch keep branch info if score
 * doesn't changed
 * 
 * Repeat those 2 steps until no time left or the root is not more expandable.
 * 
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class MctsSearch extends AbstractStandardSearch {

	/**
	 * Basic constructor
	 */
	public MctsSearch() {
		super();
	}

	/**
	 * Constructor used to expand the tree once.
	 * 
	 * @param root   Root node
	 * @param width  Board width
	 * @param height Board height
	 */
	public MctsSearch(final AbstractNode root, final int width, final int height) {
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
	public MctsSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout, final GameRuleset rules) {
		super(root, width, height);
		startTime = starttime;
		this.timeout = timeout;
		this.rules = rules;

	}

	@Override
	public void run() {
		// Expanding the smallest branch
		for (int i = 0; i < 509; i++) {
			generateChild(getSmallestChild(root));
		}
		executeMCTS();
	}

}
