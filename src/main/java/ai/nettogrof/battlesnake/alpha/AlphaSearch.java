package ai.nettogrof.battlesnake.alpha;

import java.util.ArrayList;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.HazardInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;

public class AlphaSearch implements Runnable {
	protected boolean cont=true;
	protected AlphaNode root;
	
	protected int heigth;
	protected int width;
	protected int timeout=250;
	protected long st;
	
	
	public AlphaSearch(AlphaNode r, int w, int h) {
		root=r;
		width=w;
		heigth=h ;
		
	}
	
	public AlphaSearch(AlphaNode r, int w, int h,long starttime, int t) {
		root=r;
		width=w;
		heigth=h ;
		st=starttime;
		timeout = t;
	}
	
	public void stopSearching() {
		cont=false;
	}
	
	
	public void run() {
		
		while(cont && (System.currentTimeMillis()-st < timeout) && root.getScoreRatio()>0) {
			AlphaNode bm = root.getBestChild(true);
			
			
			generateChild(bm);
			
			root.updateScore();
			
		}
		

	}
	
	public void generateChild() {
		generateChild(root);
	}
	
	protected void generateChild(AlphaNode bm) {
		
		ArrayList<SnakeInfo> current = bm.getSnakes();
		int nbSnake = current.size();
		ArrayList<SnakeInfo> alphaMove=multi(current.get(0), bm.getFood(),current, bm.getHazard());
		
		//SnakeInfo[][] poss = new SnakeInfo[nbSnake][3];
		ArrayList<ArrayList<SnakeInfo>> moves = new ArrayList<ArrayList<SnakeInfo>>(); 
		if (alphaMove.size() >0) {
			moves=merge(moves,alphaMove);
			for (int i = 1 ;  i < nbSnake;i++) {
				moves=merge(moves, multi(current.get(i), bm.getFood(),current, bm.getHazard()));
			}
			
			
			
			
			clean(moves);
			boolean stillAlive = false;
			for(ArrayList<SnakeInfo> move: moves) {
				if ( move.get(0).isAlive()) {
					bm.addChild(new AlphaNode(move, bm.getFood()));
					stillAlive =true;
				}else {
					
					AlphaNode lostMove = new AlphaNode(move, bm.getFood());
					lostMove.score[0] =0;
					
					bm.addChild(lostMove);
				}
			}
			if ( !stillAlive) {
				bm.getSnakes().get(0).die();
				bm.exp=false;
				bm.score[0]=0;
			}
			
		}else {
			bm.getSnakes().get(0).die();
			bm.exp=false;
			bm.score[0]=0;
		}
	}
	
	
	
	protected ArrayList<SnakeInfo> multi(SnakeInfo s, FoodInfo f, ArrayList<SnakeInfo> all, final HazardInfo h) {
		final ArrayList<SnakeInfo> ret = new ArrayList<>();

		if (s.isAlive()) {
			final int head = s.getHead();
			int newhead = head;
			if (head/1000 > 0) {
				
				newhead-=1000;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(s, newhead, f.isFood(newhead), h.isHazard(newhead)));
				}
				newhead+=1000;
			}

			if (head/1000 < width - 1) {
				
				newhead+=1000;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(s, newhead, f.isFood(newhead), h.isHazard(newhead)));
				}
				newhead-=1000;
			}

			if (head%1000 > 0) {
				
				newhead-=1;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(s, newhead, f.isFood(newhead), h.isHazard(newhead)));
				}
				newhead += 1;
			}

			if (head%1000 < heigth - 1) {
				
				newhead += 1;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(s,newhead, f.isFood(newhead), h.isHazard(newhead)));
				}
				
			}
		}

		return ret;
	}
	


	protected boolean freeSpace(final int square,final ArrayList<SnakeInfo> s) {
		boolean free = true;
		for (int i = 0; i < s.size() && free; i++) {
			free = !s.get(i).isSnake(square);
		}
		return free;
	}
	
	protected  void clean(ArrayList<ArrayList<SnakeInfo>> moves) {
		for(ArrayList<SnakeInfo> move: moves) {
			
			if (move.size() >1) {
				for ( int i = 0 ; i < move.size()-1; i++) {
					for (int j =i+1 ; j <move.size();j++) {
						
						

						if (move.get(i).getHead()==move.get(j).getHead()) {
							int ml = move.get(i).getSnakeBody().size();
							int ol = move.get(i).getSnakeBody().size();

							if (ml > ol) {
								move.get(j).die();
								
							} else if (ml == ol){
								move.get(i).die();
								move.get(j).die();
							}else {
								move.get(i).die();
							}
						}
					}
				}
				
			}
			
			
		}
		
	}

	@SuppressWarnings("unchecked")
	protected  ArrayList<ArrayList<SnakeInfo>> merge (ArrayList<ArrayList<SnakeInfo>> list, ArrayList<SnakeInfo> sn) {
		if (sn.size() != 0) {
			ArrayList<ArrayList<SnakeInfo>> ret = new ArrayList<ArrayList<SnakeInfo>>();
			if (list.size()==0) {
				
				for (int i  =0; i < sn.size(); i++) {
					
						ArrayList<SnakeInfo> m = new ArrayList<SnakeInfo>();
						m.add(sn.get(i));
						ret.add(m);
					
				}
				
			}else {
				for (int i  =0; i < sn.size(); i++) {
					// 2 3 4
				//	ArrayList<SnakeInfo> m = new ArrayList<SnakeInfo>();
					for(ArrayList<SnakeInfo> s : list){
						// 0 1 
						ArrayList<SnakeInfo> m = (ArrayList<SnakeInfo>) s.clone();
						
						m.add(sn.get(i));
						ret.add(m);
					}
					
				  // ret.add() 0 2
				}
			}
			
			
			return ret;
			
		}else {
			return list;
		}
	}

}
