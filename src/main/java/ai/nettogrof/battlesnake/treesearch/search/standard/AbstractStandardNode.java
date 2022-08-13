/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.standard;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractEvaluationNode;

/**
 * This abstract standard node class is the based of all node class, provide
 * basic method use in any node for standard rules.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractStandardNode extends AbstractEvaluationNode {

	/**
	 * Constructor with snakes and food information
	 * 
	 * @param snakes List of snakes
	 * @param food   Food information
	 */
	protected AbstractStandardNode(final List<SnakeInfo> snakes, final FoodInfo food) {
		super(snakes, food);

	}

}
