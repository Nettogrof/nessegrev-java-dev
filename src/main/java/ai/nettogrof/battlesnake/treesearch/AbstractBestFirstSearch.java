/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * @author carl.lajeunesse
 *
 */
public abstract class AbstractBestFirstSearch extends AbstractSearch {

	

	protected void mergeList(final List<AbstractNode> bestChild, final List<AbstractNode> retNode) {
		for (int i = 1; i < retNode.size(); i++) {
			bestChild.add(retNode.get(i));
		}
	}

	protected void updateFullListNode(final List<AbstractNode> bestPath) {
		for(final AbstractNode node : bestPath){
			node.updateScore();
		}
	}

	protected List<AbstractNode> updateListNode(final List<AbstractNode> bestChild) {
		for (int i = 0; i < bestChild.size(); i++) {
			final float score = bestChild.get(i).getScoreRatio();
			bestChild.get(i).updateScore();
			if (bestChild.get(i).getScoreRatio() == score && bestChild.get(i).exp) {
				for (int j =0 ; j < i; j++) {
				bestChild.remove(0);
				}
				return bestChild;
			} 
		}
		final List<AbstractNode> rootList = new ArrayList<>();
		rootList.add(root);
		return rootList;

	}

}
