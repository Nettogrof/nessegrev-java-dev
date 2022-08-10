package ai.nettogrof.battlesnake.treesearch.search.fun;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * Based on MCTS search this search prevent our snake to be straight. Our snake
 * will always turn.... always. Royale mode
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class JustTurnRoyaleSearch extends AbstractLimitedMoveRoyaleSearch {

	/**
	 * Constructor used to expand to do the tree search.
	 * 
	 * @param root      Root node
	 * @param width     Board width
	 * @param height    Board height
	 * @param starttime starting time for the search in millisecond
	 * @param timeout   the time limit to run the search
	 * @param rules		Game ruleset
	 */
	public JustTurnRoyaleSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout, final GameRuleset rules) {
		super(root, width, height, starttime, timeout, rules);
		leftNeck = -1000;
		rightNeck = 1000;
		downNeck = -1;
		upNeck = 1;
	}

}
