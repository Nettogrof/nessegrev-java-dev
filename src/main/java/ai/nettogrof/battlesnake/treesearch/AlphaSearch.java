package ai.nettogrof.battlesnake.treesearch;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.HazardInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.node.AlphaNode;

public class AlphaSearch  extends AbstractSearch  {

	protected transient AlphaNode root;
	
	protected transient int heigth;
	protected transient int width;
	protected transient int timeout=250;
	protected transient long startTime;
	
	
	public AlphaSearch(final AlphaNode root,final int width,final int heigth) {
		super();
		this.root=root;
		this.width=width;
		this.heigth=heigth ;
		
	}
	
	public AlphaSearch(final AlphaNode root, final int width, final int heigth, final long starttime, final int timeout) {
		super();
		this.root=root;
		this.width=width;
		this.heigth=heigth ;
		this.startTime=starttime;
		this.timeout = timeout;
	}
	
	
	@Override
	public void run() {
		
		while(cont && System.currentTimeMillis()-startTime < timeout && root.getScoreRatio()>0) {
			final AlphaNode bestNode = root.getBestChild(true);
			
			
			generateChild(bestNode);
			
			root.updateScore();
			
		}
		

	}
	
	@Override
	public void generateChild() {
		generateChild(root);
	}
	
	protected void generateChild(final AlphaNode node) {
		
		final List<SnakeInfo> current = node.getSnakes();
		final int nbSnake = current.size();
		final List<SnakeInfo> alphaMove=generateSnakeInfoDestination(current.get(0), node,current);
		
		//SnakeInfo[][] poss = new SnakeInfo[nbSnake][3];
		List<ArrayList<SnakeInfo>> moves = new ArrayList<ArrayList<SnakeInfo>>(); 
		if (alphaMove.isEmpty()) {
			node.getSnakes().get(0).die();
			node.exp=false;
			node.score[0]=0;
			
		}else {
			
			
			moves=merge(moves,alphaMove);
			for (int i = 1 ;  i < nbSnake;i++) {
				moves=merge(moves, generateSnakeInfoDestination(current.get(i), node,current));
			}
			
			
			
			
			clean(moves);
			boolean stillAlive = false;
			for(final ArrayList<SnakeInfo> move: moves) {
				if ( move.get(0).isAlive()) {
					node.addChild(new AlphaNode(move, node.getFood()));
					stillAlive =true;
				}else {
					
					final AlphaNode lostMove = new AlphaNode(move, node.getFood());
					lostMove.score[0] =0;
					
					node.addChild(lostMove);
				}
			}
			if ( !stillAlive) {
				node.getSnakes().get(0).die();
				node.exp=false;
				node.score[0]=0;
			}
		}
	}
	
	
	
	protected ArrayList<SnakeInfo> multi(final SnakeInfo snakeInfo, final FoodInfo foodInfo, final ArrayList<SnakeInfo> all, final HazardInfo hazard) {
		final ArrayList<SnakeInfo> ret = new ArrayList<>();

		if (snakeInfo.isAlive()) {
			final int head = snakeInfo.getHead();
			int newhead = head;
			if (head/1000 > 0) {
				
				newhead-=1000;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(snakeInfo, newhead, foodInfo.isFood(newhead), hazard.isHazard(newhead)));
				}
				newhead+=1000;
			}

			if (head/1000 < width - 1) {
				
				newhead+=1000;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(snakeInfo, newhead, foodInfo.isFood(newhead), hazard.isHazard(newhead)));
				}
				newhead-=1000;
			}

			if (head%1000 > 0) {
				
				newhead-=1;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(snakeInfo, newhead, foodInfo.isFood(newhead), hazard.isHazard(newhead)));
				}
				newhead += 1;
			}

			if (head%1000 < heigth - 1) {
				
				newhead += 1;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(snakeInfo,newhead, foodInfo.isFood(newhead), hazard.isHazard(newhead)));
				}
				
			}
		}

		return ret;
	}
	


	protected boolean freeSpace(final int square,final List<SnakeInfo> snakeInfo) {
		boolean free = true;
		for (int i = 0; i < snakeInfo.size() && free; i++) {
			free = !snakeInfo.get(i).isSnake(square);
		}
		return free;
	}
	
	protected  void clean(final List<ArrayList<SnakeInfo>> moves) {
		for(final ArrayList<SnakeInfo> move: moves) {
			
			if (move.size() >1) {
				for ( int i = 0 ; i < move.size()-1; i++) {
					for (int j =i+1 ; j <move.size();j++) {
						
						

						if (move.get(i).getHead()==move.get(j).getHead()) {
							final int firstSnakeLength = move.get(i).getSnakeBody().size();
							final int secondSnakeLength = move.get(i).getSnakeBody().size();

							if (firstSnakeLength > secondSnakeLength) {
								move.get(j).die();
								
							} else if (firstSnakeLength == secondSnakeLength){
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
	protected  ArrayList<ArrayList<SnakeInfo>> merge (final ArrayList<ArrayList<SnakeInfo>> list,final ArrayList<SnakeInfo> snakeInfo) {
		if (snakeInfo.isEmpty()) {
			return list;
		}
		final ArrayList<ArrayList<SnakeInfo>> ret = new ArrayList<ArrayList<SnakeInfo>>();
		if (list.isEmpty()) {
			
			for (SnakeInfo info: snakeInfo) {
				
					ArrayList<SnakeInfo> m = new ArrayList<SnakeInfo>();
					m.add(info);
					ret.add(m);
				
			}
			
		}else {
			for (final SnakeInfo info : snakeInfo) {
				// 2 3 4
			//	ArrayList<SnakeInfo> m = new ArrayList<SnakeInfo>();
				for(final ArrayList<SnakeInfo> s : list){
					// 0 1 
					final ArrayList<SnakeInfo> newSnakeInfo = (ArrayList<SnakeInfo>) s.clone();
					
					newSnakeInfo.add(info);
					ret.add(newSnakeInfo);
				}
				
			  // ret.add() 0 2
			}
		}
		
		
		return ret;
	}

	@Override
	protected void kill(final SnakeInfo death,final List<SnakeInfo> all) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected SnakeInfo createSnakeInfo(final SnakeInfo snakeInfo,final int newHead,final AbstractNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean freeSpace(final int square,final List<SnakeInfo> all,final SnakeInfo yourSnake) {
		// TODO Auto-generated method stub
		return false;
	}

}
