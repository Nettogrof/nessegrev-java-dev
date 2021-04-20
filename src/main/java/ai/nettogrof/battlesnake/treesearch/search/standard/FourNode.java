/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.standard;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * @author carl.lajeunesse
 *
 */
public class FourNode extends AbstractStandardNode {

	/**
	 * 
	 */
	public FourNode() {
		super();
	}

	/**
	 * @param snakes
	 * @param food
	 */
	public FourNode(final List<SnakeInfo> snakes,final FoodInfo food) {
		super(snakes, food);
		score = new float[snakes.size()];
		setScore();
	}

	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes,final AbstractNode currentNode) {
		return new FourNode((ArrayList<SnakeInfo>) snakes, currentNode.getFood());
	}
	
	private void setScore() {
		addBasicLengthScore();
		
		final int head = snakes.get(0).getHead();
		addScoreDistance(head);
		adjustBorderScore(head);
		addSizeCompareScore();
		
		
		//score[0] += (snakes.get(0).getHealth() - 50) * 0.01;
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
			
			

		}else if (snakes.size() == 1) {
			score[0] += 1000;
		}
		

	}

	

	

}
