package ai.nettogrof.battlesnake.treesearch.search.standard;

import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstant;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * @author carl.lajeunesse
 *
 */
public class DuelNode extends AbstractStandardNode {
	
	

	/**
	 * @param snakes
	 * @param foodInfo
	 */
	public DuelNode(final List<SnakeInfo> snakes, final FoodInfo foodInfo) {
		super(snakes, foodInfo);
		
		score = new float[snakes.size()];
		setScore();

	}
	
	private void setScore() {

		
		if (countSnakeAlive() < 2) {
			exp = false;
			for (int i = 0; i < score.length; i++) {
				if (snakes.get(i).getHealth() > 0 && snakes.get(i).isAlive()) {
					score[i]=BattleSnakeConstant.MAX_SCORE;
				}
			}
		}else {
			listAreaControl();
			for(int i =0 ; i <snakes.size();i++) {
				score[i] += snakes.get(i).getHealth()/250f;
				if (snakes.get(i).getHealth() <= 0 || !snakes.get(i).isAlive()) {
					score[i]=0f;
				}
			}
		}
		
		updateScoreRatio();
		
	}

	
	private int countSnakeAlive() {

		int nbAlive = 0;

		if (snakes.size() > 1) {
			
			for (final SnakeInfo s : snakes) {
				if (s.isAlive()) {
					nbAlive++;
				}
				
			}

		} else if (snakes.size() == 1) {
			score[0] += 1000;
		}
		return nbAlive;
	}

	/**
	 *
	 */
	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes, final AbstractNode currentNode) {
		return new DuelNode(snakes, currentNode.getFood());
	}

}