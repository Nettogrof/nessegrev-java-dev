package ai.nettogrof.battlesnake.alpha;

import java.util.ArrayList;
import java.util.LinkedList;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.HazardInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;

public class BetaSearch implements Runnable {

	protected transient boolean cont = true;
	protected transient BetaNode root;
	protected ArrayList<BetaNode> roots;
	protected transient int heigth;
	protected transient int width;
	protected transient int timeout = 250;
	protected transient long st;
	public static int w;
	public static int h;

	public BetaSearch(final BetaNode r, final int w, final int h) {
		root = r;
		width = w;
		heigth = h;
		BetaSearch.w = width;
		BetaSearch.h = heigth;

	}

	public BetaSearch(final BetaNode r, final int w, final int h, final long starttime, final int t) {
		root = r;
		width = w;
		heigth = h;
		st = starttime;
		timeout = t;

		BetaSearch.w = width;
		BetaSearch.h = heigth;
	}

	public BetaSearch(final ArrayList<BetaNode> r, final int w, final int h, final long starttime, final int t) {
		roots = r;
		width = w;
		heigth = h;
		st = starttime;
		timeout = t;

		BetaSearch.w = width;
		BetaSearch.h = heigth;
	}

	@Override
	public void run() {
		
			
			for ( int i = 0 ; i < 100 ; i++) {
				generateChild(root.getSmallestChild());
			}
			while (cont && System.currentTimeMillis() - st < timeout && root.exp) { // && root.getScoreRatio() > 0
				final BetaNode bc = (BetaNode) root.getBestChild();
				generateChild(bc);
				generateChild(bc.getBestChild());
				generateChild(bc.getBestChild());
				generateChild(bc.getBestChild());
				generateChild(bc.getBestChild());
				generateChild(bc.getBestChild());
				// root.updateScore();
				Thread.yield();
			}
		
	}

	public void stopSearching() {
		cont = false;
	}

	protected void generateChild(final BetaNode bm) {
		if (bm.getChild().size() > 0) {
			/*
			 * boolean end = false; for (final BetaNode c : bm.getChild()) { end = c.exp ||
			 * end; } if (!end) {
			 */
			bm.exp = false;
			/*
			 * }else { System.out.println("wattsetset"); }
			 */
		} else {

			final ArrayList<SnakeInfo> current = bm.getSnakes();

			final ArrayList<SnakeInfo> alphaMove = multi(current.get(0), bm.getFood(), current, bm.getHazard());
			bm.possibleMove = alphaMove.size();
			// SnakeInfo[][] poss = new SnakeInfo[nbSnake][3];
			// ArrayList<ArrayList<SnakeInfo>> moves = new ArrayList<>();

			if (alphaMove.isEmpty()) {
				bm.getSnakes().get(0).die();
				bm.exp = false;
				bm.score[0] = 0;

			} else {
				LinkedList<ArrayList<SnakeInfo>> moves = new LinkedList<>();
				final int nbSnake = current.size();
				moves = merge(moves, alphaMove);

				for (int i = 1; i < nbSnake; i++) {
					moves = merge(moves, multi(current.get(i), bm.getFood(), current, bm.getHazard()));
				}

				clean(moves);
				boolean stillAlive = false;
				for (ArrayList<SnakeInfo> move : moves) {

					if (move.get(0).isAlive()) {
						bm.addChild(new BetaNode(move, bm.getFood(), bm.getHazard()));
						stillAlive = true;
					} else {

						final BetaNode lostMove = new BetaNode(move, bm.getFood(), bm.getHazard());
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
		generateChild(root);
	}

	protected ArrayList<SnakeInfo> multi(final SnakeInfo s, final FoodInfo f, final ArrayList<SnakeInfo> all,
			final HazardInfo h) {
		final ArrayList<SnakeInfo> ret = new ArrayList<>();

		if (s.isAlive()) {
			final int head = s.getHead();
			int newhead = head;
			if (head / 1000 > 0) {

				newhead = head - 1000;
				if (freeSpace(newhead, all,s)) {
					ret.add(new SnakeInfo(s, newhead, f.isFood(newhead), h.isHazard(newhead)));
				}
				
			}

			if (head / 1000 < width - 1) {

				newhead = head +1000;
				if (freeSpace(newhead, all,s)) {
					ret.add(new SnakeInfo(s, newhead, f.isFood(newhead), h.isHazard(newhead)));
				}
				
			}

			if (head % 1000 > 0) {

				newhead = head - 1;
				if (freeSpace(newhead, all,s)) {
					ret.add(new SnakeInfo(s, newhead, f.isFood(newhead), h.isHazard(newhead)));
				}
			
			}

			if (head % 1000 < heigth - 1) {

				newhead = head + 1;
				if (freeSpace(newhead, all,s)) {
					ret.add(new SnakeInfo(s, newhead, f.isFood(newhead), h.isHazard(newhead)));
				}

			}
		}

		return ret;
	}

	protected boolean freeSpace(final int square, final ArrayList<SnakeInfo> s, SnakeInfo me) {
		boolean free = true;
		for (int i = 0; i < s.size() && free; i++) {
			if (s.get(i).equals(me)) {
				free = !s.get(i).isSnake(square);
			}else {
			free = !s.get(i).isSnake(square,me.getSquad());
			}
		}
		return free;
	}

	protected void clean(final LinkedList<ArrayList<SnakeInfo>> moves) {
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
		if (!death.getSquad().equals("")) {
			for (SnakeInfo s : all) {
				if (s.getSquad().equals(death.getSquad())) {
					s.die();
				}
			}
		}
		
	}

	protected LinkedList<ArrayList<SnakeInfo>> merge(final LinkedList<ArrayList<SnakeInfo>> list,
			final ArrayList<SnakeInfo> sn) {
		LinkedList<ArrayList<SnakeInfo>> ret;
		if (sn.isEmpty()) {
			ret = list;

		} else {
			ret = new LinkedList<>();
			if (list.isEmpty()) {

				for (int i = 0; i < sn.size(); i++) {

					final ArrayList<SnakeInfo> m = new ArrayList<>(1);
					m.add(sn.get(i).cloneSnake());
					ret.add(m);

				}

			} else {
				for (int i = 0; i < sn.size(); i++) {
					// 2 3 4
					// ArrayList<SnakeInfo> m = new ArrayList<SnakeInfo>();
					for (final ArrayList<SnakeInfo> s : list) {
						final ArrayList<SnakeInfo> m = new ArrayList<>(s.size() + 1);
						for (final SnakeInfo si : s) {
							m.add(si.cloneSnake());
						}

						// ArrayList<SnakeInfo> m = (ArrayList<SnakeInfo>) s.clone();

						m.add(sn.get(i).cloneSnake());
						ret.add(m);
					}

					// ret.add() 0 2
				}
			}

		}

		return ret;
	}

}
