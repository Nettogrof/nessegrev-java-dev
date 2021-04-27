package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;


/**
 *  This regular search was used during the Spring 2021 league  Nessegrev-Beta snake
 *  It start by search the smallest branch and expand it (12 times) then :
 *  1. find the best branch from the previous best branch that the score doesn't change (root the first time)
 *  2. expand the best leaf node 
 *  3. update score of the branch keep branch info if score doesn't changed
 *  
 *  Repeat those 3 steps until no time left or the root is not more expandable.
 *    
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class RoyaleSearch extends AbstractRoyaleSearch {

	
	/**
	 * Basic constructor
	 */
	public RoyaleSearch() {
		super();
	}
	
	/**
	 * Constructor used to expand the tree once.
	 * @param root Root node 
	 * @param width Board width
	 * @param height Board height
	 */
	public RoyaleSearch(final AbstractNode root, final int width, final int height) {
		super(root, width, height);

	}

	/**
	 * Constructor used to expand to do the tree search.
	 * @param root Root node 
	 * @param width Board width
	 * @param height Board height
	 * @param starttime  starting time for the search in millisecond  
	 * @param timeout  the time limit to run the search 
	 */
	public RoyaleSearch(final AbstractNode root, final int width, final int height, final long starttime,
			final int timeout) {
		super(root, width, height, starttime, timeout);
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

	

}
