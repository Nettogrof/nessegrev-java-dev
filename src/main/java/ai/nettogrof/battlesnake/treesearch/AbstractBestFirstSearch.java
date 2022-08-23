/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch;

import java.util.ArrayList;
import java.util.List;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import gnu.trove.list.array.TFloatArrayList;

/**
 * This abstract BestFirst search, provide basic method use in any search using
 * Best First type of search
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractBestFirstSearch extends AbstractSearch {

	
	/**
	* Sole constructor. (For invocation by subclass 
	* constructors, typically implicit.)
	*/
	protected AbstractBestFirstSearch() {
		super();
	}
	
	/**
	 * This method just merge two lists and remove the duplicate node
	 * 
	 * @param tree   List of node from root to branch
	 * @param branch List of node from branch to leaf
	 */
	protected void mergeList(final List<AbstractNode> tree, final List<AbstractNode> branch) {
		for (int i = 1; i < branch.size(); i++) {
			tree.add(branch.get(i));
		}
	}

	/**
	 * This method updateScore from each node
	 * 
	 * @param tree List of node to update
	 */
	protected void updateFullListNode(final List<AbstractNode> tree) {
		for (final AbstractNode node : tree) {
			node.updateScore();
		}
	}

	/**
	 * This method get a branch, update score starting from the leaf. if a node
	 * score have changed, remove the node from the list.
	 * 
	 * @param branch List of node of that branch
	 * @return list of node that score doesn't have changed.
	 */
	protected List<AbstractNode> updateListNode(final List<AbstractNode> branch) {
		for (int i = 0; i < branch.size(); i++) {
			final float score = branch.get(i).getScoreRatio();
			branch.get(i).updateScore();
			if (branch.get(i).getScoreRatio() == score && branch.get(i).isExp()) {
				for (int j = 0; j < i; j++) {
					branch.remove(0);
				}
				return branch;
			}
		}

		// If the score of all node from the branch have changed, return a list with
		// just the root node
		final List<AbstractNode> rootList = new ArrayList<>();
		rootList.add(root);
		return rootList;
	}

	/**
	 * This method is use to find the next leaf node to explore using MCTS algo
	 * 
	 * @param node The root node
	 * @return List of node from the leaf to the root
	 */
	protected List<AbstractNode> getBestPath(final AbstractNode node) {

		if (node.getChild().isEmpty()) {
			final List<AbstractNode> list = new ArrayList<>();
			list.add(node);
			return list;
		}

		final AbstractNode winner = getWinnerChild(node);
				
			
		final List<AbstractNode> list = (winner == null) ? new ArrayList<>() : getBestPath(winner);
		list.add(node);
		return list;

	}

	/**
	 * Get the best child/winner  from a node
	 * @param node the parent node
	 * @return the winner/best child
	 */
	private AbstractNode getWinnerChild(final AbstractNode node) {
		AbstractNode winner = null;
		final TFloatArrayList upward = new TFloatArrayList();
		final TFloatArrayList down = new TFloatArrayList();
		final TFloatArrayList left = new TFloatArrayList();
		final TFloatArrayList right = new TFloatArrayList();

		fillList(upward, down, left, right, node);

		final float choiceValue = getbestChildValue(upward, down, left, right);

		for (int i = 0; i < node.getChild().size() && winner == null; i++) {
			final AbstractNode childNode = node.getChild().get(i);
			if (childNode.getScoreRatio() == choiceValue && childNode.isExp()) {
				winner = childNode;
			}

		}
		return winner;
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
	private void fillList(final TFloatArrayList upward, final TFloatArrayList down, final TFloatArrayList left,
			final TFloatArrayList right, final AbstractNode node) {
		final int head = node.getSnakes().get(0).getHead();

		for (final AbstractNode child : node.getChild()) {
			if (child.isExp()) {
				final int move = child.getSnakes().get(0).getHead();

				if (move / 1000 < head / 1000) {
					left.add(child.getScoreRatio());
				} else if (move / 1000 > head / 1000) {
					right.add(child.getScoreRatio());
				} else if (move % 1000 < head % 1000) {
					down.add(child.getScoreRatio());
				} else {
					upward.add(child.getScoreRatio());
				}
			}
		}

	}

	/**
	 * This method return the scoreRatio of the best choice based on payoff Matrix
	 * 
	 * @param upward float array list
	 * @param down   float array list
	 * @param left   float array list
	 * @param right  float array list
	 * @return score float
	 */
	protected float getbestChildValue(final TFloatArrayList upward, final TFloatArrayList down,
			final TFloatArrayList left, final TFloatArrayList right) {
		float temp;
		float choiceValue = Float.MIN_VALUE;
		if (!upward.isEmpty()) {
			choiceValue = upward.min();

		}

		if (!down.isEmpty()) {
			temp = down.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}

		if (!left.isEmpty()) {
			temp = left.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}

		if (!right.isEmpty()) {
			temp = right.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}
		return choiceValue;
	}

	/**
	 * This method find and return the best leaf node
	 * 
	 * @param node Parent node
	 * @return AbstractNode best leaf node
	 */
	public AbstractNode getBestChild(final AbstractNode node) {
		if (node.getChild().isEmpty()) {
			return node;
		}
		node.updateScore();
		final AbstractNode winner = getWinnerChild(node);
		if (winner == null) {
			node.setExp(false);
			return node;

		}

		return getBestChild(winner);
	}

}
