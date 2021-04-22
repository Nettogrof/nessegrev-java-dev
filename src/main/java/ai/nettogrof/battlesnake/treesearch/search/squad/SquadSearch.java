/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.squad;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.SnakeInfoSquad;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * @author carl.lajeunesse
 *
 */
public class SquadSearch extends AbstractSquadSearch {

	
	/**
	 * @param root
	 * @param width
	 * @param heigth
	 */
	public SquadSearch(final AbstractNode root,final  int width,final int heigth) {
		super(root, width, heigth);
		
	}

	

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
		return new SnakeInfoSquad((SnakeInfoSquad)currentSnake, newHead, node.getFood().isFood(newHead));
	}


}
