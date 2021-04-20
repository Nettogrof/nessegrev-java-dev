package ai.nettogrof.battlesnake.treesearch.search.squad;

import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfoSquad;
import ai.nettogrof.battlesnake.treesearch.AbstractBestFirstSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

public abstract class AbstractSquadSearch extends AbstractBestFirstSearch {
	
	public AbstractSquadSearch() {
		super();		
	}
	
	public AbstractSquadSearch(final AbstractNode root, final int width, final int heigth) {
		super();
		this.root = root;
		this.width = width;
		this.heigth = heigth;
		

	}

	@Override
	public abstract void run() ;

	@Override
	protected void kill(final SnakeInfo death,final List<SnakeInfo> all ) {
		kill((SnakeInfoSquad) death,  all );
		
		
	}

	
	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo snake,final int newHead,final AbstractNode currentNode) {
		return new SnakeInfo(snake, newHead, currentNode.getFood().isFood(newHead));
	}

	@Override
	protected boolean freeSpace(final int square, final List<SnakeInfo> snakes,final SnakeInfo currentSnake) {
		return freeSpace(square, snakes, (SnakeInfoSquad) currentSnake);
		
	}

	protected boolean freeSpace(final int square, final List<SnakeInfoSquad> snakes,final SnakeInfoSquad currentSnake) {
		boolean free = true;
		for (int i = 0; i < snakes.size() && free; i++) {
			free = snakes.get(i).equals(currentSnake) ? !snakes.get(i).isSnake(square)  :  !snakes.get(i).isSnake(square,currentSnake.getSquad()) ;			
		}
		return free;
	}
	
	protected void kill(final SnakeInfoSquad death,final List<SnakeInfoSquad> all) {
		death.die();
		
		for (final SnakeInfoSquad s : all) {
			if (s.getSquad().equals(death.getSquad())) {
				s.die();
			}
		}
		
	}

}
