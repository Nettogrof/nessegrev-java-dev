/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.constrictor;

import java.util.List;

import ai.nettogrof.battlesnake.info.BoardInfo;
import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractEvaluationNode;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.MINIMUN_SNAKE;

/**
 * This Constrictor node class must be use only in constrictor mode.
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class ConstrictorNode extends AbstractEvaluationNode {

	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes   List of snakes
	 * @param food Food information not use
	 * @param boardInfo Board Information
	 */
	public ConstrictorNode(final List<SnakeInfo> snakes, final FoodInfo food, final BoardInfo boardInfo) {
		super(snakes, food, boardInfo);
		setScore();
	}

	/**
	 * Sets the node score
	 */
	private void setScore() {
		if (countSnakeAlive() < MINIMUN_SNAKE) {
			// Only one snake alive no need to explore this node anymore and set max score
			// to surviving snake
			setWinnerMaxScore();
		} else {
			listAreaControl();
			for (int i = 0; i < snakes.size(); i++) {
				if (!snakes.get(i).isAlive()) {
					score[i] = 0f;
				}
			}
		}
		updateScoreRatio();

	}

	
	/**
	 * Uses to create constrictor node
	 */
	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes, final AbstractNode currentNode) {
		return new ConstrictorNode(snakes, currentNode.getFood(), currentNode.getBoardInfo());
	}

}
