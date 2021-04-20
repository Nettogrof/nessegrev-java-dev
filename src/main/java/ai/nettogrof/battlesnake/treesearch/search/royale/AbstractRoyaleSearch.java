package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.AbstractBestFirstSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

public abstract class AbstractRoyaleSearch extends AbstractBestFirstSearch {

	public AbstractRoyaleSearch() {
		super();		
	}
	
	public AbstractRoyaleSearch(final AbstractNode root, final int width, final int heigth) {
		super();
		this.root = root;
		this.width = width;
		this.heigth = heigth;
		

	}
	public AbstractRoyaleSearch(final AbstractRoyaleNode root,final  int width,final  int heigth,final  long starttime,final  int timeout) {
		super();
		this.root=root;
		this.width=width;
		this.heigth=heigth;
		this.startTime=starttime;
		this.timeout=timeout;
	}

	
	@Override
	protected void kill(final SnakeInfo death,final List<SnakeInfo> all ) {
		death.die();
	}

	

	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo snake,final int newHead,final AbstractNode currentNode) {
		return new SnakeInfo(snake, newHead, currentNode.getFood().isFood(newHead), ((AbstractRoyaleNode)currentNode).getHazard().isHazard(newHead));
	}

	@Override
	protected boolean freeSpace(final int square, final List<SnakeInfo> snakes,final SnakeInfo currentSnake) {
		boolean free = true;
		for (int i = 0; i < snakes.size() && free; i++) {
				free = !snakes.get(i).isSnake(square);
		}
		return free;
	}

}
