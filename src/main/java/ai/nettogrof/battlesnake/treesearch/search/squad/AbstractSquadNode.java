/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.squad;

import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfoSquad;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * @author carl.lajeunesse
 *
 */
public abstract class AbstractSquadNode extends AbstractNode {
	//protected List<SnakeInfoSquad> snakes;
	
	
	/**
	 * @param snakes
	 * @param food
	 */
	@SuppressWarnings("unchecked")
	public AbstractSquadNode(final List<? extends SnakeInfo> snakes,final FoodInfo food) {
		super((List<SnakeInfo>) snakes,food);
		
	}
	
	
	@Override
	public float getScoreRatio() {
		if ("".equals(((SnakeInfoSquad)snakes.get(0)).getSquad())) {
			float totalOther = 1;
		for (int i = 1; i < score.length; i++) {
			totalOther += score[i];
		}
		
		
		return score[0] / (float) totalOther;
		
		}else {
			
			float totalOther = 0.01f;
			for (int i = 1; i < score.length; i++) {
				if (!((SnakeInfoSquad)snakes.get(0)).getSquad().equals(((SnakeInfoSquad)snakes.get(0)).getSquad())) {
					totalOther += score[i];
				}
			}
			
			
			return score[0] / (float) totalOther;
			
		}

	}
	
	

}
