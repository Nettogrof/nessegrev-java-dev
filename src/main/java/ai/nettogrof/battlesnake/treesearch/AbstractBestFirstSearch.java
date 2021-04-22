/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch;

import java.util.ArrayList;
import java.util.List;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This abstract BestFirst search, provide basic method use in any search using
 * Best First type of search
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractBestFirstSearch extends AbstractSearch {

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
	 * @param branch
	 * @return list of node that score doesn't have changed.
	 */
	protected List<AbstractNode> updateListNode(final List<AbstractNode> branch) {
		for (int i = 0; i < branch.size(); i++) {
			final float score = branch.get(i).getScoreRatio();
			branch.get(i).updateScore();
			if (branch.get(i).getScoreRatio() == score && branch.get(i).exp) {
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

}
