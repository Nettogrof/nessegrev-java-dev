/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import gnu.trove.list.array.TFloatArrayList;

/**
 * This abstract MCTS search, provide basic method use in any search using MCTS
 * type of search
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractMCTS extends AbstractBestFirstSearch {

	/**
	 * This method is use to find the next leaf node to explore using MCTS algo
	 * 
	 * @param node The root node
	 * @return List of node from the leaf to the root
	 */
	protected List<AbstractNode> getMCTSBestPath(final AbstractNode node) {

		if (node.getChild().isEmpty()) {
			final List<AbstractNode> list = new ArrayList<>();
			list.add(node);
			return list;
		}

		AbstractNode winner = null;
		final TFloatArrayList upward = new TFloatArrayList();
		final TFloatArrayList down = new TFloatArrayList();
		final TFloatArrayList left = new TFloatArrayList();
		final TFloatArrayList right = new TFloatArrayList();

		fillMCTSList(upward, down, left, right, node);

		final float choiceValue = getbestChildValue(upward, down, left, right);

		for (int i = 0; i < node.getChild().size() && winner == null; i++) {
			final AbstractNode childNode = node.getChild().get(i);
			if (mctsScore(node, childNode) == choiceValue && childNode.exp) {
				winner = childNode;
			}

		}
		final List<AbstractNode> list = (winner == null) ? new ArrayList<>() : getBestPath(winner);
		list.add(node);
		return list;

	}

	/**
	 * Calculation of the Upper Confident Bound score.
	 * 
	 * @param parentNode the parent node
	 * @param childNode  the child node which is the score is calculated
	 * @return float of the score.
	 */
	private float mctsScore(final AbstractNode parentNode, final AbstractNode childNode) {
		return (float) (childNode.getScoreRatio() + BattleSnakeConstants.MCTS_BIAS
				* Math.sqrt(Math.log(parentNode.getChildCount()) / childNode.getChildCount()));
	}

	/**
	 * This method fill 4 list (one for each direction ) with the score of each node
	 * based on the move direction
	 * 
	 * @param upward float array list
	 * @param down   float array list
	 * @param left   float array list
	 * @param right  float array list
	 * @param node   parent node
	 */
	private void fillMCTSList(final TFloatArrayList upward, final TFloatArrayList down, final TFloatArrayList left,
			final TFloatArrayList right, final AbstractNode node) {
		final int head = node.getSnakes().get(0).getHead();

		for (final AbstractNode child : node.getChild()) {
			if (child.exp) {
				final int move = child.getSnakes().get(0).getHead();

				if (move / 1000 < head / 1000) {
					left.add(mctsScore(node, child));
				} else if (move / 1000 > head / 1000) {
					right.add(mctsScore(node, child));
				} else if (move % 1000 < head % 1000) {
					down.add(mctsScore(node, child));
				} else {
					upward.add(mctsScore(node, child));
				}
			}
		}

	}

}
