/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.decision;

import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * TODO Create comments
 * 
 * @author carl.lajeunesse
 *
 */
public abstract class AbstractDecisionUtils {

		
	
	/**
	 * @param node
	 */
	public abstract void updateScore(AbstractNode node);
	/**
	 * @param node
	 * @return
	 */
	public abstract AbstractNode getBestChild(AbstractNode node);

}
