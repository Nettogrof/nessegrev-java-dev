/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.HazardInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * @author carl.lajeunesse
 *
 */
public class RoyaleDuelNode extends AbstractRoyaleNode {

	/**
	 * 
	 */
	public RoyaleDuelNode() {
		super();
	}

	/**
	 * @param snakes
	 * @param food
	 */
	public RoyaleDuelNode(final List<SnakeInfo> snakes,final FoodInfo food) {
		super(snakes, food);
		setScore();
	}

	/**
	 * @param snakes
	 * @param food
	 * @param hazard
	 */
	public RoyaleDuelNode(final List<SnakeInfo> snakes,final FoodInfo food,final HazardInfo hazard) {
		super(snakes, food, hazard);
		score = new float[snakes.size()];
		setScore();
	}
	
	
	private void setScore() {

		listAreaControl();
		
		for(int i =0 ; i <snakes.size();i++) {
			score[i] += snakes.get(i).getHealth()/250f;
			if (hazard.isHazard(snakes.get(i).getHead())){
				score[i]*=0.9f;
			}
		}
		
		
		if (snakes.size() > 1) {
			int nbAlive = 0;
			for (final SnakeInfo s : snakes) {
				if (s.isAlive()) {
					nbAlive++;
				}
			}
			if (nbAlive < 2) {
				exp = false;
			}

		} else if (snakes.size() == 1) {
			score[0] += 1000;
		}
		updateScoreRatio();
		

	}

	
	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakeInfo,final AbstractNode currentNode) {
		return new RoyaleDuelNode(snakeInfo, currentNode.getFood(), ((RoyaleDuelNode)currentNode).getHazard());
	}
	

}
