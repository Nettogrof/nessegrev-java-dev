/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.squad;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This squad search not used yet
 * 
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class SquadSearch extends AbstractSquadSearch {

	/**
	 * Basic constructor
	 */
	public SquadSearch() {
		super();
	}

	/**
	 * Constructor used to expand the tree once.
	 * 
	 * @param root   Root node
	 * @param width  Board width
	 * @param height Board height
	 */
	public SquadSearch(final AbstractNode root, final int width, final int height) {
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
	 * @param rules     Game ruleset
	 */
	public SquadSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout,final GameRuleset rules) {
		super(root, width, height, starttime, timeout, rules);
	}

	@Override
	public void run() {

		// TODO Squad Search Need to test if it work.
		for (int i = 0; i < 12; i++) {
			generateChild(getSmallestChild(root));
		}
		executeMCTS();
	}

	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo currentSnake, final int newHead, final AbstractNode node) {
		return new SnakeInfo(currentSnake, newHead, node.getFood().isFood(newHead));
	}

}
