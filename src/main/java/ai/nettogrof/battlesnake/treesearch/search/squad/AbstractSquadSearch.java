package ai.nettogrof.battlesnake.treesearch.search.squad;

import java.util.List;

import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.SnakeInfoSquad;
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
	public AbstractSquadSearch() {
		super();
	}

	/**
	 * Constructor used to expand the tree once.
	 * 
	 * @param root   Root node
	 * @param width  Board width
	 * @param height Board height
	 */
	public AbstractSquadSearch(final AbstractNode root, final int width, final int height) {
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
	public AbstractSquadSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout) {
		super();
		this.root = root;
		this.width = width;
		this.height = height;
		this.startTime = starttime;
		this.timeout = timeout;
	}

	/**
	 * This method will be use to "kill" a snake
	 * 
	 * @param death SnakeInfo of the snake to kill
	 * @param all   List of all snakeinfo
	 */
	@Override
	protected void kill(final SnakeInfo death, final List<SnakeInfo> all) {
		kill((SnakeInfoSquad) death, all);
	}

	/**
	 * This method will be use to "kill" a snake in squad
	 * 
	 * @param death SnakeInfo of the snake to kill
	 * @param all   List of all snakeinfo
	 */
	protected void kill(final SnakeInfoSquad death, final List<SnakeInfoSquad> all) {
		death.die();
		for (final SnakeInfoSquad s : all) {
			if (s.getSquad().equals(death.getSquad())) {
				s.die();
			}
		}

	}

	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo snake, final int newHead, final AbstractNode currentNode) {
		return new SnakeInfoSquad((SnakeInfoSquad)snake, newHead, currentNode.getFood().isFood(newHead));
	}

	@Override
	protected boolean freeSpace(final int square, final List<SnakeInfo> snakes, final SnakeInfo currentSnake) {
		return freeSpace(square, snakes, (SnakeInfoSquad) currentSnake);

	}

	/**
	 * Check if the snake can move on the square for squad mode
	 * 
	 * @param square       the int square
	 * @param snakes    List of all snakes
	 * @param currentSnake current Snake
	 * @return boolean free to move on that square
	 */
	protected boolean freeSpace(final int square, final List<SnakeInfoSquad> snakes,
			final SnakeInfoSquad currentSnake) {
		boolean free = true;
		for (int i = 0; i < snakes.size() && free; i++) {
			free = snakes.get(i).equals(currentSnake) ? !snakes.get(i).isSnake(square)
					: !snakes.get(i).isSnake(square, currentSnake.getSquad());
		}
		return free;
	}

}
