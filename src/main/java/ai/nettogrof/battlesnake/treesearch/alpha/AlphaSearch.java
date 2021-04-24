package ai.nettogrof.battlesnake.treesearch.alpha;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.HazardInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.AbstractStandardSearch;

/**
 *  This alpha search, used in the alpha snake 
 * 
 * @deprecated Alpha snake is so buggy and won't be develop anymore
 * @author carl.lajeunesse
 * @version Spring 2021
 */
@Deprecated
public class AlphaSearch  extends AbstractStandardSearch  {

	
	/**
	 * Constructor used to expand the tree once.
	 * @param root Root node 
	 * @param width Board width
	 * @param height Board height
	 */
	public AlphaSearch(final AlphaNode root,final int width,final int height) {
		super();
		this.root=root;
		this.width=width;
		this.height=height ;
		
	}
	
	/**
	 * Constructor used to expand to do the tree search.
	 * @param root Root node 
	 * @param width Board width
	 * @param height Board height
	 * @param starttime  starting time for the search in millisecond  
	 * @param timeout  the time limit to run the search 
	 */
	public AlphaSearch(final AlphaNode root, final int width, final int height, final long starttime, final int timeout) {
		super();
		this.root=root;
		this.width=width;
		this.height=height ;
		this.startTime=starttime;
		this.timeout = timeout;
	}
	
	
	@Override
	public void run() {
		
		while(cont && System.currentTimeMillis()-startTime < timeout && root.getScoreRatio()>0) {
			final AbstractNode bestNode = ((AlphaNode) root).getBestChild(true);
			
			
			generateChild(bestNode);
			
			root.updateScore();
			
		}
		

	}
	
	@Override
	public void generateChild() {
		generateChild(root);
	}
	
	/**
	 * Expand / Generate child from a node
	 * @param node the alphatnode to be expand
	 */
	protected void generateChild(final AlphaNode node) {
		
		final List<SnakeInfo> current = node.getSnakes();
		final int nbSnake = current.size();
		final List<SnakeInfo> alphaMove=generateSnakeInfoDestination(current.get(0), node,current);
		
		//SnakeInfo[][] poss = new SnakeInfo[nbSnake][3];
		List<ArrayList<SnakeInfo>> moves = new ArrayList<>(); 
		if (alphaMove.isEmpty()) {
			node.getSnakes().get(0).die();
			node.exp=false;
			node.score[0]=0;
			
		}else {
			moves=merge(moves,alphaMove);
			for (int i = 1 ;  i < nbSnake;i++) {
				moves=merge(moves, generateSnakeInfoDestination(current.get(i), node,current));
			}
			
			checkHeadToHead(moves);
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
	
	
	
	/**
	 * Generate all moves possible for a snake given.
	 * @param snakeInfo snake info 
	 * @param foodInfo Food Information 
	 * @param all List os all snakes
	 * @param hazard Hazard Info
	 * @return List of snake info
	 */
	protected List<SnakeInfo> multi(final SnakeInfo snakeInfo, final FoodInfo foodInfo, final ArrayList<SnakeInfo> all, final HazardInfo hazard) {
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

			if (head%1000 < height - 1) {
				
				newhead += 1;
				if (freeSpace(newhead, all)) {
					ret.add(new SnakeInfo(snakeInfo,newhead, foodInfo.isFood(newhead), hazard.isHazard(newhead)));
				}				
			}
		}

		return ret;
	}
	

	/**
	 * Check if the snake can move on the square
	 * @param square  the int sqaure
	 * @param snakeInfo List of all snakes
	 * @return boolean free to move on that square
	 */	
	protected boolean freeSpace(final int square,final List<SnakeInfo> snakeInfo) {
		boolean free = true;
		for (int i = 0; i < snakeInfo.size() && free; i++) {
			free = !snakeInfo.get(i).isSnake(square);
		}
		return free;
	}
	
	/**
	 * This method check if there's a head-to-head collision.
	 * Shorter snake die,  and if both snakes are the same length both dies
	 * @param moves  List of all possible move 
	 */
	@Override
	protected  void checkHeadToHead(final List<ArrayList<SnakeInfo>> moves) {
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
							}  // There's a lot of stairs here
						}
					}
				}
			}
		}
	}

	/**
	 * This method merge previous snake move (list) , with new snake move
	 * @param list  Previous list 
	 * @param snakeInfo New move list 
	 * @return List of List of move
	 */
	@SuppressWarnings("unchecked")
	protected  List<ArrayList<SnakeInfo>> merge (final ArrayList<ArrayList<SnakeInfo>> list,final ArrayList<SnakeInfo> snakeInfo) {
		if (snakeInfo.isEmpty()) {
			return list;
		}
		final ArrayList<ArrayList<SnakeInfo>> ret = new ArrayList<>();
		if (list.isEmpty()) {
			
			for (final SnakeInfo info: snakeInfo) {
				
					final ArrayList<SnakeInfo> move = new ArrayList<>();
					move.add(info);
					ret.add(move);
			}
			
		}else {
			for (final SnakeInfo info : snakeInfo) {
				for(final ArrayList<SnakeInfo> s : list){
					final ArrayList<SnakeInfo> newSnakeInfo = (ArrayList<SnakeInfo>) s.clone();
					
					newSnakeInfo.add(info);
					ret.add(newSnakeInfo);
				}
			}
		}
		return ret;
	}

}
