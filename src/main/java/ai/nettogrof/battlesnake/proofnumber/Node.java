package ai.nettogrof.battlesnake.proofnumber;

public class Node {
/*
	private SnakeInfo[] snakes;
	private FoodInfo food;
	private ArrayList<Node> child = new ArrayList<Node>();
	public int score[];*/
/*
	public Node() {

	}

	public Node(SnakeInfo[] sn, FoodInfo fi) {
		snakes = sn;
		food = fi;
		score = new int[sn.length];

		for (int i = 0; i < score.length; i++) {
			score[i] = sn[i].getSnakeBody().size() + sn[i].getHealth() / 100;
		}
		
		
	}
	
	public Node(SnakeInfo[] sn, FoodInfo fi , int nbplayer) {
		snakes = sn;
		food = fi;
		score = new int[nbplayer];

		for (int i = 0; i < score.length; i++) {
			score[i] = 0;
		}
		
		
	}

	public Node(SnakeInfo sn, FoodInfo fi) {
		snakes = new SnakeInfo[1];
		snakes[0] = sn;
		food = fi;
		score = new int[1];

		for (int i = 0; i < score.length; i++) {

			score[i] = sn.getSnakeBody().size() + sn.getHealth() / 100;
			if (sn.eat) {
				score[i] += 1;
			}
		}
	}
	
	

	public SnakeInfo[] getSnakes() {
		return snakes;
	}

	public void setSnakes(SnakeInfo... snakes) {
		this.snakes = snakes;
	}

	public FoodInfo getFood() {
		return food;
	}

	public void setFood(FoodInfo food) {
		this.food = food;
	}

	public void updateScore() {
		
		if (child.isEmpty()) {
			score[0] = 0;
		}

		for (int i = 0; i < snakes.length; i++) {
			score[i] = 0;
		}
	
		for (int i = 0; i < child.size(); i++) {
			Node c = child.get(i);
			for (int j = 0; j < c.score.length; j++) {
				score[j] += c.score[j];
			}

		}

	}

	public double getScoreRatio() {
		int totalOther = 1;
		for (int i = 1; i < score.length; i++) {
			totalOther += score[i];
		}
		
		return score[0] / (double) totalOther;

	}

	public Node getBestChild() {

		if (child.isEmpty()) {
			return this;
		}
		updateScore();
	
		Node bestChild = null;
		if (snakes.length == 1) {
			double maxR = -1000;

			for (Node c : child) {
				if (c.getScoreRatio() > maxR) {
					maxR = c.getScoreRatio();
					bestChild = c;
				}

				if (c.getScoreRatio() == maxR && System.currentTimeMillis() % 2 == 0) {
					maxR = c.getScoreRatio();
					bestChild = c;
				}

			}
			;

			
		} else if (snakes.length >1) {

		

			double maxR = -1000;

			for (Node c : child) {
				if (c.getScoreRatio() > maxR) {
					maxR = c.getScoreRatio();
					bestChild = c;
				}

				if (c.getScoreRatio() == maxR && System.currentTimeMillis() % 2 == 0) {
					maxR = c.getScoreRatio();
					bestChild = c;
				}

			}
			;

			

		}
		

		return bestChild.getBestChild();
	}

	public void addChild(Node c) {
		child.add(c);
	}

	public ArrayList<Node> getChild() {
		return child;
	}

	public int getChildCount() {
		if (child.isEmpty()) {
			return 1;
		}
		int s = 1;
		for (int i = 0; i < child.size(); i++) {
			s += child.get(i).getChildCount();

		}
		return s;
	}*/

}
