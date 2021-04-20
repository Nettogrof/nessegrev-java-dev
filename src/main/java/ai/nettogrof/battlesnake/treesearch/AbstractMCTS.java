/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstant;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import gnu.trove.list.array.TFloatArrayList;

/**
 * @author carl.lajeunesse
 *
 */
public abstract class AbstractMCTS extends AbstractBestFirstSearch {

	/**
	 * 
	 */
	public AbstractMCTS() {
		super();
	}

	
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

	private float mctsScore(final AbstractNode parentNode, final AbstractNode childNode) {
		return (float) (childNode.getScoreRatio() + BattleSnakeConstant.MCTS_BIAS
				* Math.sqrt(Math.log(parentNode.getChildCount()) / childNode.getChildCount()));
	}

	private void fillMCTSList(final TFloatArrayList upward, final TFloatArrayList down, final TFloatArrayList left,
			final TFloatArrayList right, final AbstractNode node) {
		final int head = node.getSnakes().get(0).getHead();

		for (int i = 0; i < node.getChild().size(); i++) {
			if (node.getChild().get(i).exp) {
				final int move = node.getChild().get(i).getSnakes().get(0).getHead();

				if (move / 1000 < head / 1000) {
					left.add(mctsScore(node, node.getChild().get(i)));
				} else if (move / 1000 > head / 1000) {
					right.add(mctsScore(node, node.getChild().get(i)));
				} else if (move % 1000 < head % 1000) {
					down.add(mctsScore(node, node.getChild().get(i)));
				} else {
					upward.add(mctsScore(node, node.getChild().get(i)));
				}
			}
		}

	}

}
