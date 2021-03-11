package ai.nettogrof.battlesnake.alpha;

import ai.nettogrof.battlesnake.proofnumber.*;

import java.util.ArrayList;

public class AlphaNode extends Node{

	
	protected ArrayList<AlphaNode> child = new ArrayList<AlphaNode>();
	public int score[];
	public boolean exp=true;
	public int cc =1;
	public AlphaNode() {

	}

	

	public AlphaNode(ArrayList<SnakeInfo> sn, FoodInfo fi) {
		snakes = sn;
		food = fi;
		score = new int[sn.size()];

		for (int i = 0; i < score.length; i++) {
			score[i] = 0;
		}

		for (int i = 0; i < sn.size(); i++) {
			score[i] = sn.get(i).getSnakeBody().size() + sn.get(i).getHealth() / 100;
		}

	}
	
	public AlphaNode(SnakeInfo[] sn, FoodInfo fi) {
		snakes = new ArrayList<SnakeInfo>();
		for(int i = 0 ; i < sn.length;i++) {
			snakes.add(sn[i]);
		}
		//snakes = sn;
		food = fi;
		score = new int[sn.length];

		for (int i = 0; i < score.length; i++) {
			score[i] = 0;
		}

		for (int i = 0; i < sn.length; i++) {
			score[i] = sn[i].getSnakeBody().size() + sn[i].getHealth() / 100;
		}

	}
	//

	public AlphaNode(SnakeInfo sn, FoodInfo fi) {
		snakes = new ArrayList<SnakeInfo>();
		snakes.add(sn);
		food = fi;
		score = new int[1];

		for (int i = 0; i < score.length; i++) {

			score[i] = sn.getSnakeBody().size() + sn.getHealth() / 100;
			if (sn.eat) {
				score[i] += 1;
			}
		}
		
		
	}

	

	

	public void updateScore() {

		if (child.size() == 0) {
			
			for (int i = 0; i < score.length; i++) {
				score[i] = snakes.get(i).isAlive() ? snakes.get(i).getSnakeBody().size() : 0;
				

			}
		
		} else {

			for (int i = 0; i < score.length; i++) {
				score[i] = 0;
			}
			try {
			for (int i = 0; i < child.size(); i++) {
				AlphaNode c = child.get(i);
				for (int j = 0; j < c.score.length; j++) {
					score[j] += c.score[j];
				}

			}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		int s = 1;
		for (int i = 0; i < child.size(); i++) {
			s += child.get(i).getChildCount();

		}
		cc=s;

	}

	public double getScoreRatio() {
		int totalOther = 1;
		for (int i = 1; i < score.length; i++) {
			totalOther += score[i];
		}
		if (totalOther == 0) {
			return 999999;
		}
		return score[0] / (double) totalOther;

	}

	public int getTotalScore() {
		if (score[0] == 0) {
			return 999999;
		}
		int totalOther = 0;
		for (int i = 0; i < score.length; i++) {
			totalOther += score[i];
		}

		return totalOther;

	}

	public AlphaNode getBestChild(boolean shortest) {
		updateScore();
		if (child.size() == 0) {
			return this;
		}

		AlphaNode bestChild = null;
		if (snakes.size() == 1) {

			double maxR = -1000;

			for (AlphaNode c : child) {
				if (c.getScoreRatio() > maxR) {
					maxR = c.getScoreRatio();
					bestChild = c;
				}

			}
			

		} else if (snakes.size() > 1) {

			if (shortest) {
				int countChild = Integer.MAX_VALUE;
				for (AlphaNode c : child) {
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

		for (AlphaNode c : child) {
			if (c.getScoreRatio() > maxR) {
				maxR = c.getScoreRatio();
				bestChild = c;
			}
		}

		int bestHead = bestChild.snakes.get(0).getHead();

		double minR = 9999000;

		for (AlphaNode c : child) {
			if (c.getSnakes().get(0).getHead() == bestHead) {
				if (c.getScoreRatio() < minR) {
					minR = c.getScoreRatio();
					bestChild = c;
				}
			}
		}

		return bestChild;
	}

	public void addChild(AlphaNode c) {
		child.add(c);
		cc++;
	}

	public ArrayList<AlphaNode> getChild() {
		return child;
	}

	public int getChildCount() {
		/*
		if (child.size() == 0) {
			return 1;
		}
		int s = 1;
		for (int i = 0; i < child.size(); i++) {
			s += child.get(i).getChildCount();

		}
		return s;*/
		
		return cc;
	}

}
