package ai.nettogrof.battlesnake.alpha;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.HazardInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import gnu.trove.list.linked.TIntLinkedList;

public class BetaNode extends Node {

	private transient final ArrayList<BetaNode> child = new ArrayList<>();
	public transient boolean exp = true;

	public transient double score[];
	public transient int possibleMove = 0;
	

	
	
	public BetaNode(final SnakeInfo[] sn, final FoodInfo fi, final HazardInfo h) {
		hazard = h;
		snakes = new ArrayList<>();

		for (int i = 0; i < sn.length; i++) {
			snakes.add(sn[i]);
		}
		// snakes = sn;
		food = fi;
		score = new double[sn.length];
		setScore();

	}

	public BetaNode(final SnakeInfo sn, final FoodInfo fi, final HazardInfo h) {

		hazard = h;
		snakes = new ArrayList<>();
		snakes.add(sn);
		food = fi;
		score = new double[1];

		setScore();
	}

	public BetaNode(final ArrayList<SnakeInfo> sn, final FoodInfo fi, final HazardInfo h) {
		snakes = sn;

		food = fi;
		score = new double[sn.size()];
		setScore();
		hazard = h;
	}

	private void setScore() {

		for (int i = 0; i < snakes.size(); i++) {
			score[i] = snakes.get(i).isAlive() ? snakes.get(i).getSnakeBody().size() + snakes.get(i).getHealth() / 50 : 0;
		}
		final int head = snakes.get(0).getHead();
		addScoreDistance(head);
		adjustBorderScore(head);
		adjustHazardScore(head);
		for (int i = 1; i < snakes.size(); i++) {
			if (snakes.get(i).getSnakeBody().size() > snakes.get(0).getSnakeBody().size()) {
				score[0] -=0.4;
			}else if (snakes.get(i).getSnakeBody().size() < snakes.get(0).getSnakeBody().size()) {
			
			
				score[0] +=0.4;
			}
		}
		
		//score[0] += (snakes.get(0).getHealth() - 50) * 0.01;
		if (snakes.size() > 1) {
			int nbAlive = 0;
			for (final SnakeInfo s : snakes) {
				if (s.isAlive()) {
					nbAlive++;
				}
			}
			if (nbAlive < 2) {
				exp = false;
			}else if ( nbAlive == 2 &&  !snakes.get(0).getSquad().equals("") &&
					snakes.get(0).getSquad().equals(snakes.get(1).getSquad())  && snakes.get(1).isAlive()) {
				score[0] += 1000;
			}
			
			

		}else if (snakes.size() == 1) {
			score[0] += 1000;
		}
		

	}

	private void adjustHazardScore(final int head) {
		if (hazard != null && hazard.isHazard(head/1000,head % 1000)) {

			score[0] -= 3.8;

		}

	}

	private void adjustBorderScore(final int head) {
		final int x = head / 1000;
		final int y = head % 1000;
		if (x == 0) {
			score[0] -= 0.4;
		}
		if (x == BetaSearch.w - 1) {
			score[0] -= 0.4;

		}
		if (y == 0) {
			score[0] -= 0.4;
		}
		if (y == BetaSearch.h - 1) {
			score[0] -= 0.4;

		}

	}

	private void addScoreDistance(final int head) {
		
		score[0] += (BetaSearch.w - food.getShortestDistance(head/ 1000, head % 1000)) * 0.095;

	}

	public void updateScore() {

		if (!child.isEmpty()) {

			if (possibleMove == 1) {
				for (int i = 0; i < score.length; i++) {
					score[i] = 0;
				}
				score[0] = 9999;
				for (final BetaNode current : child) {
					score[0] = current.score[0] < score[0] ? current.score[0] : score[0];
					for (int i = 1; i < current.score.length; i++) {

						score[i] = current.score[i] > score[i] ? current.score[i] : score[i];
					}
				}
			} else if (possibleMove > 1 && child.size() > 1) {
				final TIntLinkedList head = new TIntLinkedList();
				final ArrayList<double[]> s = new ArrayList<>();

				for (final BetaNode c : child) {
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
						final BetaNode current =  child.get(i);
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
					final BetaNode current = child.get(i);
					for (int j = 0; j < current.score.length; j++) {
						score[j] += current.score[j];
					}

				}

			}
		}
		if (score[0] != 0) {
			score[0] += 0.1 * possibleMove;
		}
		int s =1;
		for (int i = 0; i < child.size(); i++) {
			s += child.get(i).getChildCount();

		}
		cc = s;
		if (getScoreRatio() > 30) {
			exp = false;
		}

	}

	@Override
	public double getScoreRatio() {
		if (snakes.get(0).getSquad().equals("")) {
		int totalOther = 1;
		for (int i = 1; i < score.length; i++) {
			totalOther += score[i];
		}
		
		
		return score[0] / (double) totalOther;
		
		}else {
			
			int totalOther = 1;
			for (int i = 1; i < score.length; i++) {
				if (!snakes.get(0).getSquad().equals(snakes.get(1).getSquad())) {
					totalOther += score[i];
				}
			}
			
			
			return score[0] / (double) totalOther;
			
		}

	}

	public BetaNode getSmallestChild() {

		
		
		if (child.isEmpty()) {
			return this;
		} else {
			updateScore();
			BetaNode bestChild = null;
			if (snakes.size() == 1) {

				double maxR = -1000;

				for (final Node c : child) {
					if (c.getScoreRatio() > maxR) {
						maxR = c.getScoreRatio();
						bestChild = (BetaNode) c;
					}

				}

			} else if (snakes.size() > 1) {

				
					int countChild = Integer.MAX_VALUE;
					for (BetaNode c : child) {
						if (c.getChildCount() < countChild  && c.exp) {
							countChild = c.getChildCount();
							bestChild = c;
						}
					}
				

			}
			if (bestChild == null) {
			//	System.out.println("exp"+exp);
				exp = false;
				return this;
			}
			
			return  bestChild.getSmallestChild();
		}
		
	}

	
	public BetaNode getBestChild() {
		// double score =-200;
		if (child.isEmpty()) {
			return this;
		}
		updateScore();
		BetaNode winner = null;
		final ArrayList<Double> up = new ArrayList<>();
		final ArrayList<Double> down = new ArrayList<>();
		final ArrayList<Double> left = new ArrayList<>();
		final ArrayList<Double> right = new ArrayList<>();
		// ArrayList<Double> choice = new ArrayList<Double>();
		final int head = snakes.get(0).getHead();

		for (int i = 0; i < child.size(); i++) {
			// if (child.get(i).getSnakes()[0].alive) {
			final int move = child.get(i).getSnakes().get(0).getHead();

			if (move /1000 < head/1000) {
				left.add(child.get(i).getScoreRatio());
			}

			if (move/1000 > head/1000) {
				right.add(child.get(i).getScoreRatio());
			}
			if (move%1000 < head%1000) {
				up.add(child.get(i).getScoreRatio());
			}
			if (move%1000 > head%1000) {
				down.add(child.get(i).getScoreRatio());
			}
			// }
		}
		double wup = Double.MAX_VALUE;
		double wdown = Double.MAX_VALUE;
		double wleft = Double.MAX_VALUE;
		double wright = Double.MAX_VALUE;
		for (final Double v : up) {
			if (v < wup) {
				wup = v;
			}
		}
		for (final Double v : down) {
			if (v < wdown) {
				wdown = v;
			}
		}
		for (final Double v : left) {
			if (v < wleft) {
				wleft = v;
			}
		}
		for (final Double v : right) {
			if (v < wright) {
				wright = v;
			}
		}
		double choiceValue = -1;
		if (wup != Double.MAX_VALUE) {
			
			if (wup > choiceValue) {
				choiceValue = wup;
			}
		}
		if (wdown != Double.MAX_VALUE) {
			
			if (wdown > choiceValue) {
				choiceValue = wdown;
			}
		}
		if (wleft != Double.MAX_VALUE) {
		
			if (wleft > choiceValue) {
				choiceValue = wleft;
			}
		}
		if (wright != Double.MAX_VALUE) {
			
			if (wright > choiceValue) {
				choiceValue = wright;
			}
		}

		for (int i = 0; i < child.size(); i++) {
			final double c = child.get(i).getScoreRatio();
			if (c == choiceValue && child.get(i).getSnakes().get(0).isAlive()) {
				winner = child.get(i);
				i = child.size();
			}

		}
		if (winner ==null) {
			winner = child.get(0);
		}

		return winner.getBestChild();
	}
	

	public List<BetaNode> getChild() {
		return (ArrayList<BetaNode>) child;
	}

	public void addChild(final BetaNode c) {
		child.add(c);
		cc++;
	}

}
