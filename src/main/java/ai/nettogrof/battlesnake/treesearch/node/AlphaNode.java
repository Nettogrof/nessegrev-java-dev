package ai.nettogrof.battlesnake.treesearch.node;

import java.util.ArrayList;
import java.util.List;

import com.google.common.flogger.FluentLogger;

import ai.nettogrof.battlesnake.info.*;

public class AlphaNode extends AbstractNode{
	protected static transient FluentLogger log = FluentLogger.forEnclosingClass();
	
	protected List<AlphaNode> child = new ArrayList<>();
	public AlphaNode() {
		super();
	}

	

	public AlphaNode(final List<SnakeInfo> snakes,final FoodInfo foodInfo) {
		this.snakes = snakes;
		food = foodInfo;
		score = new float[snakes.size()];

		for (int i = 0; i < score.length; i++) {
			score[i] = 0;
		}

		for (int i = 0; i < snakes.size(); i++) {
			score[i] = snakes.get(i).getSnakeBody().size() + snakes.get(i).getHealth() / 100;
		}

	}
	
	public AlphaNode(final SnakeInfo[] snakes,final FoodInfo foodInfo) {
		this.snakes = new ArrayList<>();
		for(final SnakeInfo snake : snakes) {
			this.snakes.add(snake);
		}
		//snakes = sn;
		food = foodInfo;
		score = new float[snakes.length];

		for (int i = 0; i < score.length; i++) {
			score[i] = 0;
		}

		for (int i = 0; i < snakes.length; i++) {
			score[i] = snakes[i].getSnakeBody().size() + snakes[i].getHealth() / 100;
		}

	}
	//

	public AlphaNode(final SnakeInfo snakes,final FoodInfo foodInfo) {
		this.snakes = new ArrayList<>();
		this.snakes.add(snakes);
		food = foodInfo;
		score = new float[1];

		for (int i = 0; i < score.length; i++) {

			score[i] = snakes.getSnakeBody().size() + snakes.getHealth() / 100;
			
		}
		
		
	}

	

	

	public void updateScore() {

		if (child.isEmpty()) {
			
			for (int i = 0; i < score.length; i++) {
				score[i] = snakes.get(i).isAlive() ? snakes.get(i).getSnakeBody().size() : 0;
				

			}
		
		} else {

			for (int i = 0; i < score.length; i++) {
				score[i] = 0;
			}
			try {
			for (final AlphaNode node : child) {
				
				for (int j = 0; j < node.score.length; j++) {
					score[j] += node.score[j];
				}

			}
			}catch(Exception e) {
				log.atWarning().log(e.getMessage() + "\n" + e.getStackTrace());
			}
		}
		
		int subtotal = 1;
		for(final AlphaNode node : child){
			subtotal += node.getChildCount();

		}
		allChildsCount=subtotal;

	}

	

	public int getTotalScore() {
		if (score[0] == 0) {
			return 999_999;
		}
		int totalOther = 0;
		for (int i = 0; i < score.length; i++) {
			totalOther += Math.round(score[i]);
		}

		return totalOther;

	}

	public AlphaNode getBestChild(final boolean shortest) {
		updateScore();
		if (child.isEmpty()) {
			return this;
		}

		AlphaNode bestChild = null;
		if (snakes.size() == 1) {

			double maxR = -1000;

			for (final AlphaNode c : child) {
				if (c.getScoreRatio() > maxR) {
					maxR = c.getScoreRatio();
					bestChild = c;
				}

			}
			

		} else if (snakes.size() > 1) {

			if (shortest) {
				int countChild = Integer.MAX_VALUE;
				for (final AlphaNode c : child) {
					if (c.getChildCount() < countChild && c.getScoreRatio() > 0  && c.exp) {
						countChild = c.getChildCount();
						bestChild = c;
					}
				}
			} else {
				bestChild = bestChildForAll();
				

			}

			

		}
		if (bestChild == null) {
			exp=false;
			return this;
		}

		return bestChild.getBestChild(shortest);
	}

	private AlphaNode bestChildForAll() {
		AlphaNode bestChild = null;
		double maxR = -2000;

		for (final AlphaNode c : child) {
			if (c.getScoreRatio() > maxR) {
				maxR = c.getScoreRatio();
				bestChild = c;
			}
		}

		final int bestHead = bestChild.snakes.get(0).getHead();

		double minR = 9_999_000;

		for (final AlphaNode c : child) {
			if (c.getSnakes().get(0).getHead() == bestHead && c.getScoreRatio() < minR) {
				
					minR = c.getScoreRatio();
					bestChild = c;
				
			}
		}

		return bestChild;
	}

	
	@Override
	public int getChildCount() {
		
		
		return allChildsCount;
	}



	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes,final AbstractNode currentNode) {
		return new AlphaNode(snakes, currentNode.getFood());
		
	}

}
