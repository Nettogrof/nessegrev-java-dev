/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.standard;

import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * @author carl.lajeunesse
 *
 */
public class ManyNode extends AbstractStandardNode {

	/**
	 * 
	 */
	public ManyNode() {
		super();
	}

	/**
	 * @param snakes
	 * @param food
	 */
	public ManyNode(final List<SnakeInfo> snakes,final FoodInfo food) {
		super(snakes, food);
		score = new float[snakes.size()];
		setScore();
	}

	private void setScore() {

		addBasicLengthScore();
		
		final int head = snakes.get(0).getHead();
		addScoreDistance(head);
		adjustBorderScore(head);
	
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

	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes,final AbstractNode currentNode) {
		return new ManyNode(snakes, currentNode.getFood());
	}

}
