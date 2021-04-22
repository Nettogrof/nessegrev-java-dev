package ai.nettogrof.battlesnake.treesearch.search.standard;

import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.AbstractMCTS;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

public abstract class AbstractStandardSearch extends AbstractMCTS {

	public AbstractStandardSearch() {
		super();		
	}
	
	public AbstractStandardSearch(final AbstractNode root, final int width, final int heigth) {
		super();
		this.root = root;
		this.width = width;
		this.height = heigth;
		

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
