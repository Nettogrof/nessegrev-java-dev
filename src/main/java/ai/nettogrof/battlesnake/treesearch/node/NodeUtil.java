/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.node;

/**
 * This NodeUtil class
 * 
 * @author carl.lajeunesse
 * @version Summer 2022
 */
public final class NodeUtil {

	/**
	 * basic/ useless constructor
	 */
	private NodeUtil() {
		// useless constructor
	}

	/**
	 * Return the small child (in size) that are still expendable
	 * 
	 * @param root Parent node
	 * @return the smallest child.
	 */
	public static AbstractNode getSmallestNode(final AbstractNode root) {
		AbstractNode smallChild = null;
		int countChild = Integer.MAX_VALUE;
		for (final AbstractNode childNode : root.getChild()) {
			if (childNode.getChildCount() < countChild && childNode.exp) {
				countChild = childNode.getChildCount();
				smallChild = childNode;
			}
		}
		return smallChild;
	}

	/**
	 * Get the best child
	 * @param node  Parent node
	 * @return the best child
	 */
	public static AbstractNode getBestNode(final AbstractNode node) {
		AbstractNode bestChild = null;
		float maxR = -1000;

		for (final AbstractNode childNode : node.getChild()) {
			if (childNode.getScoreRatio() > maxR) {
				maxR = childNode.getScoreRatio();
				bestChild = childNode;
			}

		}
		return bestChild;
	}

}
