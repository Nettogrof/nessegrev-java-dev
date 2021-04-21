package ai.nettogrof.battlesnake.treesearch.node;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstant;
import gnu.trove.list.array.TIntArrayList;

public abstract class AbstractNode {
	public static int width;
	public static int heigth;
	protected transient List<AbstractNode> child = new ArrayList<>();
	protected List<SnakeInfo> snakes;
	protected FoodInfo food;
	protected transient float scoreRatio; 
	public transient float score[];
	public int possibleMove;
	public int allChildsCount = 1;
	public boolean exp = true;

	public AbstractNode() {
		// Empty top levet
	}

	public AbstractNode(final List<SnakeInfo> snakes,final FoodInfo food) {
		this.snakes =  snakes;
		this.food = food;
	}

	public FoodInfo getFood() {
		return food;
	}

	public void setFood(final FoodInfo food) {
		this.food = food;
	}

	public void setSnakes(final SnakeInfo... snakes) {
		for(SnakeInfo snake : snakes) {
			this.snakes.add(snake);
		}

	}

	public List< SnakeInfo> getSnakes() {
		return snakes;
	}

	public int getPossibleMove() {
		return possibleMove;
	}

	public void setPossibleMove(final int possibleMove) {
		this.possibleMove = possibleMove;
	}

	public int getAllChildsCount() {
		return allChildsCount;
	}

	public void setAllChildsCount(final int allChildsCount) {
		this.allChildsCount = allChildsCount;
	}

	public boolean isExp() {
		return exp;
	}

	public void setExp(final boolean exp) {
		this.exp = exp;
	}

	public int getChildCount() {

		return allChildsCount;
	}

	public float getScoreRatio() {
		return scoreRatio;
		

	}

	

	public void addChild(final AbstractNode newChild) {
		child.add(newChild);
		allChildsCount++;
	}

	public void updateScore() {

		if (!child.isEmpty()) {

			if (possibleMove == 1) {
				updateScoreSinglePossibleMove();

			} else {
				updateScoreMultiplePossibleMove();
				

			}
		}
	
		updateScoreRatio();
		updateChildCount();

	}
	
	public void updateScoreRatio() {
		float totalOther = 0.01f;
		for (int i = 1; i < score.length; i++) {
			totalOther += score[i];
		}

		scoreRatio= (float) (score[0] / (float) totalOther);
		if (scoreRatio == 0.0) {
			exp =false;
		}
	}

	private void updateScoreMultiplePossibleMove() {
		
		final ArrayList<float[]> scores = new ArrayList<>();

		initPayoffMatrix(scores);
		computePayoffMatrix(scores);

		
		
	}

	private void computePayoffMatrix(final List<float[]> scores) {
		final int ind = findBestIndex(scores);		
		if (ind > -1) {
			for (int i = 0; i < scores.get(ind).length && scores.get(ind)[i] != -500; i++) {
				score[i] = scores.get(ind)[i];
			}

			if (scores.get(ind).length < score.length) {
				for (int i = scores.get(ind).length; i < score.length; i++) {
					score[i] = (float) 0.0001;
				}
			}
		} else {
			for (int i = 0; i < score.length; i++) {
				score[i] = 0;
			}
			for(AbstractNode current: child) {
				for (int j = 0; j < current.score.length; j++) {
					score[j] += current.score[j];
				}
			}

		}
		
	}

	private int findBestIndex(final List<float[]> scores) {
		int ind = -1;
		float ration = -1;
		for (int i = 0; i < scores.size(); i++) {
			float other = 0;
			for (int j = 1; j < scores.get(i).length; j++) {
				if (scores.get(i)[j] != BattleSnakeConstant.INVALID_SCORE) {
					other += scores.get(i)[j];
				}
			}
			final float currentratio = scores.get(i)[0] / other;
			if (currentratio > ration) {
				ration = currentratio;
				ind = i;
			}
		}
		return ind;
	}

	private void initPayoffMatrix(final List<float[]> scores) {
		final TIntArrayList head = new TIntArrayList();
		for (final AbstractNode c : child) {
			final int currentHead = c.getSnakes().get(0).getHead();
			if (head.contains(currentHead)) {

				float[] currentS = scores.get(head.indexOf(currentHead));
				currentS[0] = c.score[0] < currentS[0] ? c.score[0] : currentS[0];
				for (int i = 1; i < c.score.length; i++) {

					currentS[i] = c.score[i] > currentS[i] ? c.score[i] : currentS[i];
				}

				if (score.length > c.score.length) {
					for (int i = c.score.length; i < score.length; i++) {
						currentS[i] = (float) 0.0001;
					}
				}
			} else {
				head.add(currentHead);
				float[] beta = { BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE, BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE,BattleSnakeConstant.INVALID_SCORE};
				System.arraycopy(c.score, 0, beta, 0, c.score.length);
				/*
				 * for (int i = 0; i < c.score.length; i++) { beta[i] = c.score[i]; }
				 */

				for (int i = c.score.length; i < score.length; i++) {
					beta[i] = (float) 0.0001;
				}

				scores.add(beta);
			}

		}
		
	}

	private void updateChildCount() {
		allChildsCount = 1;
		for (final AbstractNode c : child) {
			allChildsCount += c.getChildCount();
		}

		if (getScoreRatio() > BattleSnakeConstant.STOP_EXPAND_LIMIT) {
			exp = false;
		}

	}

	private void updateScoreSinglePossibleMove() {
		for (int i = 1; i < score.length; i++) {
			score[i] = 0;
		}
		score[0] = BattleSnakeConstant.MAX_SCORE;
		for (final AbstractNode current : child) {
			score[0] = current.score[0] < score[0] ? current.score[0] : score[0];
			for (int i = 1; i < current.score.length; i++) {

				score[i] = current.score[i] > score[i] ? current.score[i] : score[i];
			}
		}

	}

	public List<AbstractNode> getChild() {
		return (ArrayList<AbstractNode>) child;
	}

	public abstract AbstractNode createNode(List<SnakeInfo> snakes, AbstractNode currentNode);

}