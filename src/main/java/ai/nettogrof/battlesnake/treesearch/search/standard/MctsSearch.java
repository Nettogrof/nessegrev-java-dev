/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.standard;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * @author carl.lajeunesse
 *
 */
public class MctsSearch extends AbstractStandardSearch {

	/**
	 * 
	 */
	public MctsSearch() {
		super();
	}

	/**
	 * @param root
	 * @param width
	 * @param heigth
	 */
	public MctsSearch(final AbstractNode root, final int width, final int heigth) {
		super(root, width, heigth);

	}

	public MctsSearch(final AbstractNode root, final int width, final int heigth, final long starttime,
			final int timeout) {
		super();
		this.root = root;
		this.width = width;
		this.heigth = heigth;
		startTime = starttime;
		this.timeout = timeout;

	}

	@Override
	public void run() {
		for (int i = 0; i < 509; i++) {
			generateChild(getSmallestChild(root));
		}
		long currentTime = System.currentTimeMillis() - startTime;
		List<AbstractNode> retNode = new ArrayList<>();
		retNode.add(root);
		while (cont && currentTime < timeout && root.exp) {
			final List<AbstractNode> bestChild =  getMCTSBestPath(retNode.get(0));
			
			generateChild(bestChild.get(0));
			mergeList(bestChild, retNode);
			retNode = updateListNode(bestChild);
			Thread.yield();
			currentTime = System.currentTimeMillis() - startTime;

	

		}
		updateFullListNode(retNode);

	}

	

	

}
