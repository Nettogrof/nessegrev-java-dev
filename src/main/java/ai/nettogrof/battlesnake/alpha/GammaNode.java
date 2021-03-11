package ai.nettogrof.battlesnake.alpha;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

public class GammaNode extends Node {

	private transient final ArrayList<GammaNode> child = new ArrayList<>();
	public transient boolean exp = true;

	public transient double score[];
	public transient int possibleMove = 0;
	

	
	
	public GammaNode(final SnakeInfo[] sn, final FoodInfo fi) {
	
		snakes = new ArrayList<>();

		for (int i = 0; i < sn.length; i++) {
			snakes.add(sn[i]);
		}
		// snakes = sn;
		food = fi;
		score = new double[sn.length];
		setScore();

	}
	
	public GammaNode(final SnakeInfo[] sn, final FoodInfo fi, boolean fullAnalyse ) {
		
		snakes = new ArrayList<>();

		for (int i = 0; i < sn.length; i++) {
			snakes.add(sn[i]);
		}
		// snakes = sn;
		food = fi;
		score = new double[sn.length];
		if(fullAnalyse) {
			setScore();
		}else {
			setQuickScore();
		}

	}

	public GammaNode(final SnakeInfo sn, final FoodInfo fi) {

		
		snakes = new ArrayList<>();
		snakes.add(sn);
		food = fi;
		score = new double[1];

		setScore();
	}
	
	public GammaNode(final SnakeInfo sn, final FoodInfo fi, boolean fullAnalyse) {

		
		snakes = new ArrayList<>();
		snakes.add(sn);
		food = fi;
		score = new double[1];
		if(fullAnalyse) {
			setScore();
		}else {
			setQuickScore();
		}
	}

	public GammaNode(final ArrayList<SnakeInfo> sn, final FoodInfo fi) {
		snakes = sn;

		food = fi;
		score = new double[sn.size()];
		setScore();
		
	}
	public GammaNode(final ArrayList<SnakeInfo> sn, final FoodInfo fi, boolean fullAnalyse ) {
		snakes = sn;

		food = fi;
		score = new double[sn.size()];
		if(fullAnalyse) {
			setScore();
		}else {
			setQuickScore();
		}
		
	}
	
	private void setQuickScore() {
		
		
		for (int i = 0; i < snakes.size(); i++) {
			score[i] = snakes.get(i).isAlive() ? snakes.get(i).getSnakeBody().size() : 0;
		}
		addScoreDistance(snakes.get(0).getHead());
		
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
			
			

		}else if (snakes.size() == 1) {
			score[0] += 1000;
		}
		

	}

	private void setScore() {

		for (int i = 0; i < snakes.size(); i++) {
			score[i] = snakes.get(i).isAlive() ? snakes.get(i).getSnakeBody().size() + snakes.get(i).getHealth() / 50 : 0;
		}
		if (GammaSearch.w !=0 && snakes.size() < 4) {
			listAreaControl();
			final int head = snakes.get(0).getHead();
			addScoreDistance(head);
		}else {
			final int head = snakes.get(0).getHead();
			addScoreDistance(head);
			adjustBorderScore(head);
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
			
			

		}else if (snakes.size() == 1) {
			score[0] += 1000;
		}
		

	}
	
	private void adjustBorderScore(final int head) {
		final int x = head / 1000;
		final int y = head % 1000;
		if (x == 0) {
			score[0] -= 0.4;
		}
		if (x == GammaSearch.w - 1) {
			score[0] -= 0.4;

		}
		if (y == 0) {
			score[0] -= 0.4;
		}
		if (y == GammaSearch.h - 1) {
			score[0] -= 0.4;

		}

	}

	private void addScoreDistance(final int head) {
		
		score[0] += (GammaSearch.w - food.getShortestDistance(head/ 1000, head % 1000)) * 0.095;

	}
	
	
	private void listAreaControl() {
		int[][] board = new int[GammaSearch.w][GammaSearch.h];
		
		
		for (SnakeInfo sn : snakes) {
			TIntArrayList body = sn.getSnakeBody();
			for (int i =0 ; i < body.size() ; i++) {
				int sq = body.get(i);
			
				board[sq / 1000][sq % 1000] = -99;
				
				
			}
		}
		
		
		
		HashMap<Integer, Integer> old = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> newHash = new HashMap<Integer, Integer>();
		
		
		
		
		
		for(int i = 0; i < snakes.size();i++) {
			newHash.put(snakes.get(i).getSnakeBody().get(0), i+1);			
		}
		
		while(newHash.size() !=0) {
			applyNewHash(newHash , board);
			old = newHash;
			newHash = new HashMap<Integer, Integer>();
			generateHash(old,newHash,board);
			
			
		}
		int[] count = new int[snakes.size()];
		for (int i = 0 ; i < GammaSearch.w; i++) {
			for (int j =0 ; j <GammaSearch.h; j++) {
				if (board[i][j] >0) {
					count[board[i][j] - 1]++;
				}
			}
		}
		
		int total =0;
		for(int i =0; i < snakes.size();i++) {
			total += count[i];
		}
		
		//System.out.println("0: "+ count[0]);
		for(int i =0; i < snakes.size();i++) {
			score[i] =( snakes.get(i).getSnakeBody().size()* 10 * (((float)count[i]) / total));
		}
		
	}
	
	
	

	private void applyNewHash(HashMap<Integer, Integer> newHash, int[][] board) {
		newHash.forEach((xy, v ) ->{
			
			board[xy / 1000 ][xy % 1000] = v;
			
		});
		
	}

	private void generateHash(HashMap<Integer, Integer> old, HashMap<Integer, Integer> newHash, int[][] board) {
		
		old.forEach((xy, v ) ->{
			int x = xy / 1000;
			int y = xy % 1000;
			//if (board[xy % 1000][xy / 1000] == 0) {
			//	board[xy % 1000][xy / 1000] = v;
				if (x + 1 < GammaSearch.w &&  board[x + 1][y] == 0){
					Integer prev =newHash.putIfAbsent(xy+1000, v);
					if (prev != null && prev != v) {
						newHash.put(xy+1000, -50);
					}
				}
				if (x- 1 >= 0  && board[x - 1][y] == 0){
					Integer prev =newHash.putIfAbsent(xy-1000, v);
					if (prev != null && prev != v) {
						newHash.put(xy-1000, -50);
					}
				}
				
				if (y + 1  <GammaSearch.h  && board[x ][y + 1] == 0){
					Integer prev =newHash.putIfAbsent(xy+1, v);
					if (prev != null && prev != v) {
						newHash.put(xy+1, -50);
					}
				}
				if (y - 1 >= 0 && board[x ][y - 1] == 0){
					Integer prev =newHash.putIfAbsent(xy-1, v);
					if (prev != null && prev != v) {
						newHash.put(xy-1, -50);
					}
				}
				
				
			//}
		});
		
	}


	
	

	public void updateScore() {

		if (!child.isEmpty()) {

			if (possibleMove == 1) {
				for (int i = 1; i < score.length; i++) {
					score[i] = 0;
				}
				score[0] = 9999;
				for (final GammaNode current : child) {
					score[0] = current.score[0] < score[0] ? current.score[0] : score[0];
					for (int i = 1; i < current.score.length; i++) {

						score[i] = current.score[i] > score[i] ? current.score[i] : score[i];
					}
				}
			} else if (possibleMove > 1 && child.size() > 1) {
				final TIntArrayList head = new TIntArrayList();
				final ArrayList<double[]> s = new ArrayList<>();

				for (final GammaNode c : child) {
					final int currentHead = c.getSnakes().get(0).getHead();
					if (head.contains(currentHead)) {
						
						double[] currentS = s.get(head.indexOf(currentHead));
						currentS[0] = c.score[0] < currentS[0] ? c.score[0] : currentS[0];
						for (int i = 1; i < c.score.length; i++) {

							currentS[i] = c.score[i] > currentS[i] ? c.score[i] : currentS[i];
						}

						if (score.length > c.score.length) {
							for (int i = c.score.length; i < score.length; i++) {
								currentS[i] = 0.0001;
							}
						}
					} else {
						head.add(currentHead);
						double[] beta = new double[] { -500, -500, -500, -500, -500, -500, -500, -500, -500 };
						System.arraycopy(c.score, 0, beta, 0, c.score.length);
						/*
						 * for (int i = 0; i < c.score.length; i++) { beta[i] = c.score[i]; }
						 */
						
							for (int i = c.score.length; i < score.length; i++) {
								beta[i] = 0.0001;
							}
					

						s.add(beta);
					}

				}

				int ind = -1;
				double ration = -1;
				for (int i = 0; i < s.size(); i++) {
					double other = 0;
					for (int j = 1; j < s.get(i).length; j++) {
						if (s.get(i)[j] != -500) {
							other += s.get(i)[j];
						}
					}
					final double currentratio = s.get(i)[0] / other;
					if (currentratio > ration) {
						ration = currentratio;
						ind = i;
					}
				}
				if (ind > -1) {
					for (int i = 0; i < s.get(ind).length && s.get(ind)[i] != -500; i++) {
						score[i] = s.get(ind)[i];
					}

					if (s.get(ind).length < score.length) {
						for (int i = s.get(ind).length; i < score.length; i++) {
							score[i] = 0.0001;
						}
					}
				} else {
					for (int i = 0; i < score.length; i++) {
						score[i] = 0;
					}

					for (int i = 0; i < child.size(); i++) {
						final GammaNode current =  child.get(i);
						for (int j = 0; j < current.score.length; j++) {
							score[j] += current.score[j];
						}
					}

				}

			} else {

				for (int i = 0; i < score.length; i++) {
					score[i] = 0;
				}

				for (int i = 0; i < child.size(); i++) {
					final GammaNode current = child.get(i);
					for (int j = 0; j < current.score.length; j++) {
						score[j] += current.score[j];
					}

				}

			}
		}
		/*if (score[0] != 0) {
			score[0] += 0.1 * possibleMove;
		}*/
		cc =1;
		for (GammaNode c : child) {
			cc += c.getChildCount();
		}
		
		
		if (getScoreRatio() > 30) {
			exp = false;
		}

	}

	@Override
	public double getScoreRatio() {
		
		int totalOther = 1;
		for (int i = 1; i < score.length; i++) {
			totalOther += score[i];
		}
		
		
		return score[0] / (double) totalOther;
		
		

	}

	public GammaNode getSmallestChild() {

		
		
		if (child.isEmpty()) {
			return this;
		} else {
			updateScore();
			GammaNode smallChild = null;
			if (snakes.size() == 1) {

				double maxR = -1000;

				for (final Node c : child) {
					if (c.getScoreRatio() > maxR) {
						maxR = c.getScoreRatio();
						smallChild = (GammaNode) c;
					}

				}

			} else if (snakes.size() > 1) {

				
					int countChild = Integer.MAX_VALUE;
					for (GammaNode c : child) {
						if (c.getChildCount() < countChild  && c.exp) {
							countChild = c.getChildCount();
							smallChild = c;
						}
					}
				

			}
			if (smallChild == null) {
			//	System.out.println("exp"+exp);
				exp = false;
				return this;
			}
			
			return  smallChild.getSmallestChild();
		}
		
	}

	
	public GammaNode getBestChild() {
		// double score =-200;
		if (child.isEmpty()) {
			return this;
		}
		updateScore();
		GammaNode winner = null;
		final TDoubleArrayList up = new TDoubleArrayList();
		final TDoubleArrayList down = new TDoubleArrayList();
		final TDoubleArrayList left = new TDoubleArrayList();
		final TDoubleArrayList right = new TDoubleArrayList();
	/*	final ArrayList<Double> up = new ArrayList<>();
		final ArrayList<Double> down = new ArrayList<>();
		final ArrayList<Double> left = new ArrayList<>();
		final ArrayList<Double> right = new ArrayList<>();*/
		// ArrayList<Double> choice = new ArrayList<Double>();
		final int head = snakes.get(0).getHead();

		for (int i = 0; i < child.size(); i++) {
			// if (child.get(i).getSnakes()[0].alive) {
			final int move = child.get(i).getSnakes().get(0).getHead();

			if (move /1000 < head/1000) {
				left.add(child.get(i).getScoreRatio());
			}else if (move/1000 > head/1000) {
				right.add(child.get(i).getScoreRatio());
			}else if (move%1000 < head%1000) {
				up.add(child.get(i).getScoreRatio());
			}else if (move%1000 > head%1000) {
				down.add(child.get(i).getScoreRatio());
			}
			// }
		}
		double temp;
		double choiceValue = -1;
		if (up.size() > 0) {
			choiceValue = up.min();
			
		}
		
		if (down.size() > 0) {
			temp = down.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}
		

		if (left.size() > 0) {
			temp = left.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}

		if (right.size() > 0) {
			temp = right.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}

		for (int i = 0; i < child.size(); i++) {
			final GammaNode c = child.get(i);
			if (c.getScoreRatio() == choiceValue && c.getSnakes().get(0).isAlive()) {
				winner = c;
				i = child.size();
			}

		}
		if (winner ==null) {
			System.out.println("FUCK!!!");
			return this;
		}

		return winner.getBestChild();
	}
	

	public List<GammaNode> getChild() {
		return (ArrayList<GammaNode>) child;
	}

	public void addChild(final GammaNode c) {
		child.add(c);
		cc++;
	}

	

}