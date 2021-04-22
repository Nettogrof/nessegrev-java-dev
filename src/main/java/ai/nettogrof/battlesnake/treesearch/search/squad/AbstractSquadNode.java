/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.squad;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.SnakeInfoSquad;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This abstract squad node class is the based of all node class, provide
 * basic method use in any node for squad rules.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractSquadNode extends AbstractNode {
	
	
	
	/**
	 * Constructor with snakes and food information
	 * 
	 * @param snakes List of snakes
	 * @param food   Food information
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
