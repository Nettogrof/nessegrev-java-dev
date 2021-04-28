/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.squad;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstant;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This Squad node class must be in squad mode
 * 
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class SquadNode extends AbstractSquadNode {

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes   List of snakes
	 * @param food Food information
	 */
	public SquadNode(final List<? extends SnakeInfo> snakes,final FoodInfo food) {
		super(snakes, food);

		score = new float[snakes.size()];
		setScore();
	}
	
	/**
	 * Sets the node score
	 */
	private void setScore() {
		if (countSnakeAlive() < 2) {
			//Only one snake alive no need to explore this node anymore and set max score to surviving snake
			exp = false;
			for (int i = 0; i < score.length; i++) {
				if (snakes.get(i).getHealth() > 0 && snakes.get(i).isAlive()) {
					score[i] = BattleSnakeConstant.MAX_SCORE;
				}
			}
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

	/**
	 * Count the number of snake still alive
	 * 
	 * @return Number of snake alive
	 */
	private int countSnakeAlive() {

		int nbAlive = 0;
		if (snakes.size() > 1) {

			for (final SnakeInfo s : snakes) {
				if (s.isAlive()) {
					nbAlive++;
				}
			}

		} else if (snakes.size() == 1) {
			score[0] += BattleSnakeConstant.MAX_SCORE;
			nbAlive = 1;
		}
		return nbAlive;
	}

	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes,final AbstractNode currentNode) {
		return new SquadNode(snakes, currentNode.getFood());
	}

}
