/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.constrictor;

import java.util.List;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.AbstractMCTS;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This Constrictor search add the possibility to play Constrictor mode It start
 * by search the smallest branch and expand it (256 times) then : 1. find the
 * best branch from the previous best branch that the score doesn't change (root
 * the first time) 2. expand the MCTS leaf node (ratio between best / less
 * explored ) 3. update score of the branch keep branch info if score doesn't
 * changed
 * 
 * Repeat those 2 steps until no time left or the root is not more expandable.
 * 
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class ConstrictorSearch extends AbstractMCTS {

	/**
	 * Basic constructor
	 */
	public ConstrictorSearch() {
		super();
	}

	/**
	 * Constructor used to expand the tree once.
	 * 
	 * @param root   Root node
	 * @param width  Board width
	 * @param height Board height
	 */
	public ConstrictorSearch(final AbstractNode root, final int width, final int height) {
		super();
		this.root = root;
		this.width = width;
		this.height = height;

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
	public ConstrictorSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout, final GameRuleset rules) {
		super();
		this.root = root;
		this.width = width;
		this.height = height;
		startTime = starttime;
		this.timeout = timeout;
		this.rules = rules;
	}

	@Override
	public void run() {
		// Expanding the smallest branch
		for (int i = 0; i < 256; i++) {
			generateChild(getSmallestChild(root));
		}
		executeMCTS();
	}

	@Override
	protected void kill(final SnakeInfo death, final List<SnakeInfo> all) {
		death.die();

	}

	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo snake, final int newHead, final AbstractNode node) {
		return new SnakeInfo(snake, newHead, true);
	}

	@Override
	protected boolean freeSpace(final int square, final List<SnakeInfo> snakes, final SnakeInfo yourSnake) {
		boolean free = true;
		for (int i = 0; i < snakes.size() && free; i++) {
			free = !snakes.get(i).isSnake(square);
		}
		return free;
	}

}
