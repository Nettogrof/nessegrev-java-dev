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
public abstract class AbstractDecisionUitls {

		
	
	protected abstract void updateScore(AbstractNode node);
	protected abstract AbstractNode getBestChild(AbstractNode node);

}
