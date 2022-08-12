package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.List;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.AbstractMCTS;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This abstract Royale search, provide basic method use in any search in
 * battlesnake royale mode
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractRoyaleSearch extends AbstractMCTS {

	/**
	 * Constructor used to expand to do the tree search.
	 * 
	 * @param root      Root node
	 * @param width     Board width
	 * @param height    Board height
	 * @param starttime starting time for the search in millisecond
	 * @param timeout   the time limit to run the search
	 * @param rules		the game ruleset info
	 */
	public AbstractRoyaleSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout, final GameRuleset rules) {
		super();
		this.root = root;
		this.width = width;
		this.height = height;
		this.startTime = starttime;
		this.timeout = timeout;
		this.rules = rules;
	}

	@Override
	protected void kill(final SnakeInfo death, final List<SnakeInfo> all) {
		death.die();
	}

	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo snake, final int newHead, final AbstractNode currentNode) {
		return new SnakeInfo(snake, newHead, currentNode.getFood().isFood(newHead),
				root.getHazard().isHazardSquare(newHead), rules);
	}

	@Override
	protected boolean freeSpace(final int square, final List<SnakeInfo> snakes, final SnakeInfo currentSnake) {
		boolean free = true;
		for (int i = 0; i < snakes.size() && free; i++) {
			free = !snakes.get(i).isSnake(square);
		}
		return free;
	}

}
