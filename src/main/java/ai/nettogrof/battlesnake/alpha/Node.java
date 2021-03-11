package ai.nettogrof.battlesnake.alpha;

import java.util.ArrayList;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.HazardInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;

public abstract class Node {
	protected ArrayList<SnakeInfo> snakes;
	protected FoodInfo food;
	protected HazardInfo hazard;
	public transient int cc =1;
	
	public Node() {}
	
	public FoodInfo getFood() {
		return food;
	}

	public void setFood(final FoodInfo food) {
		this.food = food;
	}
	public void setSnakes(final SnakeInfo... snakes) {
		for( int i = 0 ; i < snakes.length; i++) {
			this.snakes.add(snakes[i]);
		}
		
	}
	public ArrayList<SnakeInfo> getSnakes() {
		return snakes;
	}


	public int getChildCount() {
		
		return cc;
	}

	protected abstract double getScoreRatio();

	public HazardInfo getHazard() {
		return hazard;
	}

	public void setHazard(final HazardInfo hazard) {
		this.hazard = hazard;
	}

}
