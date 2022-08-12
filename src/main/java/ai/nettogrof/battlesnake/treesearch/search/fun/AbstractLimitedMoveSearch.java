package ai.nettogrof.battlesnake.treesearch.search.fun;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This abstract Standard search based on MctsSearch provide methods to prevent
 * snake to move in particular direction
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public abstract class AbstractLimitedMoveSearch extends AbstractLimitedMoveRoyaleSearch {

	/**
	 * Constructor used to expand to do the tree search.
	 * 
	 * @param root      Root node
	 * @param width     Board width
	 * @param height    Board height
	 * @param starttime starting time for the search in millisecond
	 * @param timeout   the time limit to run the search
	 * @param rules     the game ruleset info
	 */
	public AbstractLimitedMoveSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout, final GameRuleset rules) {
		super(root, width, height, starttime, timeout, rules);

	}

	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo snake, final int newHead, final AbstractNode currentNode) {
		return new SnakeInfo(snake, newHead, currentNode.getFood().isFood(newHead), false, rules);
	}

}
