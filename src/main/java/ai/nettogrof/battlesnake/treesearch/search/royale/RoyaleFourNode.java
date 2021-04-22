package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.HazardInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This Royale FourNode class must be use when only 3 or 4 snakes left, and in
 * royale mode.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class RoyaleFourNode extends AbstractRoyaleNode {

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes   List of snakes
	 * @param foodInfo Food information
	 * @param hazard   Hazard Info
	 */
	public RoyaleFourNode(final List<SnakeInfo> snakes, final FoodInfo foodInfo, final HazardInfo hazard) {
		super(snakes, foodInfo, hazard);
		score = new float[snakes.size()];
		setScore();

	}

	/**
	 * Sets the node score
	 */
	private void setScore() {
		addBasicLengthScore();
		listAreaControl();

		for (int i = 0; i < snakes.size(); i++) {

			if (hazard.isHazard(snakes.get(i).getHead())) {
				score[i] *= 0.9f;
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

	/**
	 * Uses to create fourNode type
	 */
	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakeInfo, final AbstractNode currentNode) {
		return new RoyaleFourNode(snakeInfo, currentNode.getFood(), ((RoyaleFourNode) currentNode).getHazard());
	}

}
