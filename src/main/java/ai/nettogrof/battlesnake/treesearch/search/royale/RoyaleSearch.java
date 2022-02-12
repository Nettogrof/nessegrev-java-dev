package ai.nettogrof.battlesnake.treesearch.search.royale;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This regular search was used during the Spring 2021 league Nessegrev-Beta
 * snake It start by search the smallest branch and expand it (12 times) then :
 * 1. find the best branch from the previous best branch that the score doesn't
 * change (root the first time) 2. expand the best leaf node 3. update score of
 * the branch keep branch info if score doesn't changed
 * 
 * Repeat those 3 steps until no time left or the root is not more expandable.
 * 
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class RoyaleSearch extends AbstractRoyaleSearch {

	/**
	 * Constructor used to expand to do the tree search.
	 * 
	 * @param root      Root node
	 * @param width     Board width
	 * @param height    Board height
	 * @param starttime starting time for the search in millisecond
	 * @param timeout   the time limit to run the search
	 */
	public RoyaleSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout, final GameRuleset rules) {
		super(root, width, height, starttime, timeout, rules);
	}

	@Override
	public void run() {
		for (int i = 0; i < 12; i++) {
			generateChild(getSmallestChild(root));
		}
		executeMCTS();

	}

}
