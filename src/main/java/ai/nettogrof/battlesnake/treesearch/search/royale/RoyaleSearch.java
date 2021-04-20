package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

public class RoyaleSearch extends AbstractRoyaleSearch {

	public RoyaleSearch(final AbstractRoyaleNode root, final int width, final int heigth) {
		super(root, width, heigth);

	}

	public RoyaleSearch(final AbstractRoyaleNode root, final int width, final int heigth, final long starttime,
			final int timeout) {
		super(root, width, heigth, starttime, timeout);
	}

	/*
	 * public void run() {
	 * 
	 * 
	 * for ( int i = 0 ; i < 100 ; i++) { generateChild(root.getSmallestChild()); }
	 * while (cont && System.currentTimeMillis() - startTime < timeout && root.exp)
	 * { // && root.getScoreRatio() > 0 final RoyaleFourNode bc = (RoyaleFourNode)
	 * root.getBestChild(); generateChild(bc); generateChild(bc.getBestChild());
	 * generateChild(bc.getBestChild()); generateChild(bc.getBestChild());
	 * generateChild(bc.getBestChild()); generateChild(bc.getBestChild()); //
	 * root.updateScore(); Thread.yield(); }
	 * 
	 * }
	 */

	@Override
	public void run() {
		for (int i = 0; i < 12; i++) {
			generateChild(getSmallestChild(root));
		}
		long currentTime = System.currentTimeMillis() - startTime;
		List<AbstractNode> retNode = new ArrayList<>();
		retNode.add(root);
		
		while (cont && currentTime < timeout && root.exp) {
			//generateChild(retNode.get(0).getSmallestChild());// && root.getScoreRatio() > 0
			final List<AbstractNode> bestChild = getBestPath(retNode.get(0));
			generateChild(bestChild.get(0));
			mergeList(bestChild, retNode);
			retNode = updateListNode(bestChild);
			Thread.yield();
			currentTime = System.currentTimeMillis() - startTime;
			
		

		}
		updateFullListNode(retNode);

	}

	
	

	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo currentSnake,final  int newHead,final  AbstractNode node) {
		return new SnakeInfo(currentSnake, newHead, node.getFood().isFood(newHead),
				((AbstractRoyaleNode) node).getHazard().isHazard(newHead));
	}

	@Override
	protected boolean freeSpace(final int square, final List<SnakeInfo> snakes,final SnakeInfo yourSnake) {
		boolean free = true;
		for (int i = 0; i < snakes.size() && free; i++) {
				free = !snakes.get(i).isSnake(square);
		}
		return free;
	}

	@Override
	protected void kill(final SnakeInfo death,final List<SnakeInfo> all ) {
		death.die();
	}

}
