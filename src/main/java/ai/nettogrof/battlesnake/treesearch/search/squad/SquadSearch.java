/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.squad;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.SnakeInfoSquad;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.AbstractRoyaleNode;

/**
 * This squad search not used yet
 * 
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class SquadSearch extends AbstractSquadSearch {

	/**
	 * Constructor used to expand the tree once.
	 * 
	 * @param root   Root node
	 * @param width  Board width
	 * @param height Board height
	 */
	public SquadSearch(final AbstractNode root, final int width, final int height) {
		super(root, width, height);

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
	public SquadSearch(final AbstractRoyaleNode root, final int width, final int height, final long starttime,
			final int timeout) {
		super(root, width, height, starttime, timeout);
	}

	@Override
	public void run() {

		// TODO Squad Search Need to test if it work.
		for (int i = 0; i < 12; i++) {
			generateChild(getSmallestChild(root));
		}
		long currentTime = System.currentTimeMillis() - startTime;
		List<AbstractNode> retNode = new ArrayList<>();
		retNode.add(root);

		while (cont && currentTime < timeout && root.exp) {
			// generateChild(retNode.get(0).getSmallestChild());// && root.getScoreRatio() >
			// 0
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
	protected SnakeInfo createSnakeInfo(final SnakeInfo currentSnake, final int newHead, final AbstractNode node) {
		return new SnakeInfoSquad((SnakeInfoSquad) currentSnake, newHead, node.getFood().isFood(newHead));
	}

}
