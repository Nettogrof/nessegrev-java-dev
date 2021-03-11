package ai.nettogrof.battlesnake.alpha;

import java.util.ArrayList;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
//import ai.nettogrof.battlesnake.proofnumber.HazardInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;

public class GammaSearch implements Runnable {

	protected transient boolean cont = true;
	protected transient GammaNode root;
	
	protected transient int heigth;
	protected transient int width;
	protected transient int timeout = 250;
	protected transient long st;
	public static int w;
	public static int h;

	public GammaSearch(final GammaNode r, final int w, final int h) {
		root = r;
		width = w;
		heigth = h;
		GammaSearch.w = width;
		GammaSearch.h = heigth;

	}

	public GammaSearch(final GammaNode r, final int w, final int h, final long starttime, final int t) {
		root = r;
		width = w;
		heigth = h;
		st = starttime;
		timeout = t;

		GammaSearch.w = width;
		GammaSearch.h = heigth;
	}

	
	@Override
	public void run() {
		
			for ( int i = 0 ; i < 100 ; i++) {
				generateChild(root.getSmallestChild(), false);
			}
			long ct = System.currentTimeMillis() - st;
			while (cont && ct < timeout && root.exp ) { // && root.getScoreRatio() > 0
				final GammaNode bc = (GammaNode) root.getBestChild();
				generateChild(bc, true);
				generateChild(bc.getSmallestChild(),true);
				generateChild(bc.getSmallestChild(),true);
				generateChild(bc.getSmallestChild(),true);
				generateChild(bc.getSmallestChild(),true);
				generateChild(bc.getSmallestChild(),true);
				generateChild(bc.getSmallestChild(),true);
				ct = System.currentTimeMillis() - st;
				// root.updateScore();
				Thread.yield();

			}
		

	}

	public void stopSearching() {
		cont = false;
	}

	protected void generateChild(final GammaNode bm, final boolean fullAnalyse) {
		if (bm.getChild().size() > 0) {
			
			bm.exp = false;
			
		} else {

			final ArrayList<SnakeInfo> current = bm.getSnakes();

			final ArrayList<SnakeInfo> alphaMove = multi(current.get(0), bm.getFood(), current);
			bm.possibleMove = alphaMove.size();
			
			if (alphaMove.isEmpty()) {
				bm.getSnakes().get(0).die();
				bm.exp = false;
				bm.score[0] = 0;

			} else {
				ArrayList<ArrayList<SnakeInfo>> moves = new ArrayList<>();
				final int nbSnake = current.size();
				moves = merge(moves, alphaMove);

				for (int i = 1; i < nbSnake; i++) {
					moves = merge(moves, multi(current.get(i), bm.getFood(), current));
				}

				clean(moves);
				boolean stillAlive = false;
				for (ArrayList<SnakeInfo> move : moves) {

					if (move.get(0).isAlive()) {
						bm.addChild(new GammaNode(move, bm.getFood(),fullAnalyse));
						stillAlive = true;
					} else {

						final GammaNode lostMove = new GammaNode(move, bm.getFood(),fullAnalyse);
						lostMove.score[0] = 0;

						bm.addChild(lostMove);
					}
				}
				if (!stillAlive) {
					bm.getSnakes().get(0).die();
					bm.exp = false;
					bm.score[0] = 0;
				}
			}

		}
	}

	public void generateChild() {
		generateChild(root, false);
	}

	protected ArrayList<SnakeInfo> multi(final SnakeInfo s, final FoodInfo f, final ArrayList<SnakeInfo> all) {
		final ArrayList<SnakeInfo> ret = new ArrayList<>();

		if (s.isAlive()) {
			final int head = s.getHead();
			int newhead = head;
			if (head / 1000 > 0) {

				newhead = head - 1000;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(s, newhead, f.isFood(newhead)));
				}
				
			}

			if (head / 1000 < width - 1) {

				newhead = head +1000;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(s, newhead, f.isFood(newhead)));
				}
				
			}

			if (head % 1000 > 0) {

				newhead = head - 1;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(s, newhead, f.isFood(newhead)));
				}
			
			}

			if (head % 1000 < heigth - 1) {

				newhead = head + 1;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(s, newhead, f.isFood(newhead)));
				}

			}
		}

		return ret;
	}

	protected boolean freeSpace(final int square, final ArrayList<SnakeInfo> s) {
		boolean free = true;
		for (int i = 0; i < s.size() && free; i++) {
				free = !s.get(i).isSnake(square);
		}
		return free;
	}

	protected void clean(final ArrayList<ArrayList<SnakeInfo>> moves) {
		for (final ArrayList<SnakeInfo> move : moves) {

			for (int i = 0; i < move.size() - 1; i++) {
				for (int j = i + 1; j < move.size(); j++) {

					if (move.get(i).getHead() == move.get(j).getHead()) {
						final int ml = move.get(i).getSnakeBody().size();
						final int ol = move.get(j).getSnakeBody().size();

						if (ml > ol) {
							kill(move.get(j),move);

						} else if (ml == ol) {
							kill(move.get(i),move);
							kill(move.get(j),move);
						} else {
							kill(move.get(i),move);
						}
					}
				}
			}

		}

	}
	
	protected void kill(SnakeInfo death,ArrayList<SnakeInfo> all ) {
		death.die();
	}

	protected ArrayList<ArrayList<SnakeInfo>> merge(final ArrayList<ArrayList<SnakeInfo>> list,
			final ArrayList<SnakeInfo> sn) {
		ArrayList<ArrayList<SnakeInfo>> ret;
		if (sn.isEmpty()) {
			ret = list;

		} else {
			ret = new ArrayList<ArrayList<SnakeInfo>>();
			if (list.isEmpty()) {
				for (final SnakeInfo si : sn) {
					final ArrayList<SnakeInfo> m = new ArrayList<>(1);
					m.add(si.cloneSnake());
					ret.add(m);
				}

			} else {
				for (int i = 0; i < sn.size(); i++) {
					// 2 3 4
			
					for (final ArrayList<SnakeInfo> s : list) {
						final ArrayList<SnakeInfo> m = new ArrayList<>(s.size() + 1);
						for (final SnakeInfo si : s) {
							m.add(si.cloneSnake());
						}

						m.add(sn.get(i).cloneSnake());
						ret.add(m);
					}

				}
			}

		}

		return ret;
	}

}
