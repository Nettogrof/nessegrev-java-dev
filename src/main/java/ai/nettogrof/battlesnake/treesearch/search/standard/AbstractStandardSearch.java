package ai.nettogrof.battlesnake.treesearch.search.standard;

import java.util.List;

import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.AbstractMCTS;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 *  This abstract Standard search, provide basic method use in any search in battlesnake standard mode 
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractStandardSearch extends AbstractMCTS {

	/**
	 * Basic constructor
	 */
	public AbstractStandardSearch() {
		super();		
	}
	
	/**
	 * Constructor used to expand the tree once.
	 * @param root Root node 
	 * @param width Board width
	 * @param height Board height
	 */
	public AbstractStandardSearch(final AbstractNode root, final int width, final int height) {
		super();
		this.root = root;
		this.width = width;
		this.height = height;
		

	}

	
	@Override
	protected void kill(final SnakeInfo death,final List<SnakeInfo> all ) {
		death.die();
	}

	
	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo snake, final int newHead,final AbstractNode node) {
		return new SnakeInfo(snake, newHead, node.getFood().isFood(newHead));
	}

	@Override
	protected boolean freeSpace(final int square, final List<SnakeInfo> snakes,final SnakeInfo yourSnake) {
		boolean free = true;
		for (int i = 0; i < snakes.size() && free; i++) {
				free = !snakes.get(i).isSnake(square);
		}
		return free;
	}

}
