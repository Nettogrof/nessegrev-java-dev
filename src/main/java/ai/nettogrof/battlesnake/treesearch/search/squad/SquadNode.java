/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.squad;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This Squad node class must be in squad mode
 * 
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class SquadNode extends AbstractSquadNode {
	protected final static int MINIMUN_SNAKE = 4;
	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes List of snakes
	 * @param food   Food information
	 */
	public SquadNode(final List<? extends SnakeInfo> snakes, final FoodInfo food) {
		super(snakes, food);
		setScore();
	}

	/**
	 * Sets the node score
	 */
	private void setScore() {
		if (countSnakeAlive() < MINIMUN_SNAKE) {
			// Only one team alive no need to explore this node anymore and set max score
			// to surviving team
			setWinnerMaxScore();
		} else {
			listAreaControl();
			for (int i = 0; i < snakes.size(); i++) {
				score[i] += snakes.get(i).getHealth() / 250f;
				if (snakes.get(i).getHealth() <= 0 || !snakes.get(i).isAlive()) {
					score[i] = 0f;
				}
			}
		}

		updateScoreRatio();

	}

	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes, final AbstractNode currentNode) {
		return new SquadNode(snakes, currentNode.getFood());
	}

}
