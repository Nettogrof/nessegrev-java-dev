package ai.nettogrof.battlesnake.treesearch.search.squad;

import java.util.List;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.AbstractMCTS;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This abstract Squad search, provide basic method use in any search in
 * battlesnake squad mode
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractSquadSearch extends AbstractMCTS {

	/**
	 * Basic constructor
	 */
	protected AbstractSquadSearch() {
		super();
	}

	/**
	 * Constructor used to expand the tree once.
	 * 
	 * @param root   Root node
	 * @param width  Board width
	 * @param height Board height
	 */
	protected AbstractSquadSearch(final AbstractNode root, final int width, final int height) {
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
	 * @param rules		Game ruleset
	 */
	protected AbstractSquadSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout,final GameRuleset rules) {
		super();
		this.root = root;
		this.width = width;
		this.height = height;
		this.startTime = starttime;
		this.timeout = timeout;
		this.rules = rules;
	}

	/**
	 * This method will be use to "kill" a snake
	 * 
	 * @param death SnakeInfo of the snake to kill
	 * @param all   List of all snakeinfo
	 */
	@Override
	protected void kill(final SnakeInfo death, final List<SnakeInfo> all) {
		killSquad( death, all);
	}

	/**
	 * This method will be use to "kill" a snake in squad
	 * 
	 * @param death SnakeInfo of the snake to kill
	 * @param all   List of all snakeinfo
	 */
	protected void killSquad(final SnakeInfo death, final List<SnakeInfo> all) {
		death.die();
		for (final SnakeInfo s : all) {
			if ( s.getSquad().equals(death.getSquad())) {
				s.die();
			}
		}

	}

	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo snake, final int newHead, final AbstractNode currentNode) {
		return new SnakeInfo( snake, newHead, currentNode.getFood().isFood(newHead));
	}

	@Override
	protected boolean freeSpace(final int square, final List<SnakeInfo> snakes, final SnakeInfo currentSnake) {
		return freeSpaceSquad(square, snakes, currentSnake);

	}

	/**
	 * Check if the snake can move on the square for squad mode
	 * 
	 * @param square       the int square
	 * @param snakes       List of all snakes
	 * @param currentSnake current Snake
	 * @return boolean free to move on that square
	 */
	protected boolean freeSpaceSquad(final int square, final List<SnakeInfo> snakes,
			final SnakeInfo currentSnake) {
		boolean free = true;
		for (int i = 0; i < snakes.size() && free; i++) {
			free = snakes.get(i).equals(currentSnake) ? !snakes.get(i).isSnake(square)
					: !(snakes.get(i)).isSnake(square, currentSnake.getSquad());
		}
		return free;
	}

}
