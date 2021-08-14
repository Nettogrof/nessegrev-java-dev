/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.node;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;

/**
 * @author carl.lajeunesse
 *
 */
public abstract class AbstractDecisionNode extends AbstractNode {
	
	private int decision = 1;

	/**
	 * @return the decision
	 */
	public int getDecision() {
		return decision;
	}

	/**
	 * @param decision the decision to set
	 */
	public void setDecision(int decision) {
		this.decision = decision;
	}

	/**
	 * 
	 */
	public AbstractDecisionNode() {
		super();
	}

	
	/**
	 * @param snakes
	 * @param food
	 */
	public AbstractDecisionNode(List<SnakeInfo> snakes, FoodInfo food) {
		super(snakes, food);
		
	}

	

	/**
	 * Update this node score
	 */
	public  void updateScore() {

		updateScore();

		updateScoreRatio();
		updateChildCount();

	}

}
