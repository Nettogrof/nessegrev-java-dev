package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.HazardInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

public class RoyaleFourNode extends AbstractRoyaleNode {

	

	public RoyaleFourNode(final List<SnakeInfo> snakes, final FoodInfo foodInfo, final HazardInfo hazard) {
		super(snakes,foodInfo,hazard);
		score = new float[snakes.size()];
		setScore();
		
	}

	private void setScore() {
/*
		addBasicLengthScore();
		final int head = snakes.get(0).getHead();
		addScoreDistance(head);
		adjustBorderScore(head);
		adjustHazardScore(head);
		
	
		addSizeCompareScore();
		
		*/
		addBasicLengthScore();
		listAreaControl();
		
		for(int i =0 ; i <snakes.size();i++) {
			
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
		return new RoyaleFourNode(snakeInfo, currentNode.getFood(), ((RoyaleFourNode)currentNode).getHazard());
	}
	



}
