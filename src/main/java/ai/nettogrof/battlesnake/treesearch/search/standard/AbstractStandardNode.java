/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.standard;

import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import gnu.trove.list.array.TIntArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 * @author carl.lajeunesse
 *
 */
public abstract class AbstractStandardNode extends AbstractNode {
	private final static int defaultv = new Int2IntOpenHashMap().defaultReturnValue();
	/**
	 * 
	 */
	public AbstractStandardNode() {
		super();
	}

	/**
	 * @param snakes
	 * @param food
	 */
	public AbstractStandardNode(final List<SnakeInfo> snakes,final FoodInfo food) {
		super(snakes, food);
		
	}

	
	protected void adjustBorderScore(final int head) {
		final int headX = head / 1000;
		final int headY = head % 1000;
		if (headX == 0) {
			score[0] -= 0.4;
		}
		if (headX == width - 1) {
			score[0] -= 0.4;

		}
		if (headY == 0) {
			score[0] -= 0.4;
		}
		if (headY == height - 1) {
			score[0] -= 0.4;

		}

	}

	protected void addScoreDistance(final int head) {

		score[0] += (width - food.getShortestDistance(head / 1000, head % 1000)) * 0.095f;

	}
	
	protected void listAreaControl() {
		if (snakes.size() < 2) {
			score[0] = 100;
			return;
		}
		final int[][] board = initBoard();

		final Int2IntOpenHashMap old = new Int2IntOpenHashMap();
		final Int2IntOpenHashMap newHash = new Int2IntOpenHashMap();

		for (int i = 0; i < snakes.size(); i++) {
			newHash.put(snakes.get(i).getSnakeBody().get(0), i + 1);
		}

		while (newHash.size() != 0) {
			old.clear();
			applyNewHash(newHash, board);		
			generateHash(newHash, old, board);
			newHash.clear();
			if (old.size() != 0) {
				applyNewHash(old, board);				
				generateHash(old, newHash, board);
			}
			/*
			 * applyNewHash(newHash , board); old = newHash; newHash = new
			 * Int2IntOpenHashMap(); newHash.clear(); generateHash(old,newHash,board);
			 * 
			 */

		}
		adjustScodeBasedonBoardControl(board);

	}

	protected void adjustScodeBasedonBoardControl(final int[][] board) {
		final int biggestSnake = snakes.get(0).getSnakeBody().size() > snakes.get(1).getSnakeBody().size() ? 0 : 1;
		int[] count = new int[snakes.size()];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (board[i][j] > 0) {
					count[board[i][j] - 1]++;
				} else if (board[i][j] == -50) {

					count[biggestSnake]++;
				}
			}
		}
	
		int total = 0;
		for (int i = 0; i < snakes.size(); i++) {
			final int posTail = snakes.get(i).getTail();
			final int boardValue = board[posTail /1000][posTail%1000];
			if(boardValue >0) {
				count[boardValue - 1]+=5;
			}
			total += count[i];
		}

		// System.out.println("0: "+ count[0]);
		for (int i = 0; i < snakes.size(); i++) {
			score[i] += ((float) count[i]) / total;// snakes.get(i).getSnakeBody().size()+(
													// //snakes.get(i).getSnakeBody().size()* 10 *
		}

	}

	protected int[][] initBoard() {
		int[][] board = new int[width][height];

		for (final SnakeInfo snake : snakes) {
			final TIntArrayList body = snake.getSnakeBody();
			for (int i = 0; i < body.size()-1; i++) {
				final int square = body.getQuick(i);

				board[square / 1000][square % 1000] = -99;

			}
		}
		return board;
	}

	protected void applyNewHash(final Int2IntOpenHashMap newHash, int[][] board) {
		newHash.forEach((xy, v) -> {

			board[xy / 1000][xy % 1000] = v;

		});

	}

	protected void generateHash(final Int2IntOpenHashMap old, final Int2IntOpenHashMap newHash, final int[][] board) {

		old.forEach((position, valeur) -> {
			
			final int posX = position / 1000;
			final int posY = position % 1000;

			if (posX + 1 < width && board[posX + 1][posY] == 0) {
				addToHash(newHash, position + 1000, valeur);

			}
			if (posX - 1 >= 0 && board[posX - 1][posY] == 0) {
				addToHash(newHash, position - 1000, valeur);

			}

			if (posY + 1 < height && board[posX][posY + 1] == 0) {
				addToHash(newHash, position + 1, valeur);
			}
			if (posY - 1 >= 0 && board[posX][posY - 1] == 0) {
				addToHash(newHash, position - 1, valeur);
			}

			// }
		});

	}

	protected void addToHash(final Int2IntOpenHashMap newHash, final int position, final int valeur) {
		final int prev = newHash.putIfAbsent(position, valeur);
		if (prev != defaultv && prev != valeur) {
			newHash.put(position, -50);
		}

	}
	
	protected void addBasicLengthScore() {
		for (int i = 0; i < snakes.size(); i++) {
			score[i] = snakes.get(i).isAlive() ? snakes.get(i).getSnakeBody().size() + snakes.get(i).getHealth() / 50 : 0;
		}
	}
	
	protected void addSizeCompareScore() {
		for (int i = 1; i < snakes.size(); i++) {
			if (snakes.get(i).getSnakeBody().size() > snakes.get(0).getSnakeBody().size()) {
				score[0] -=0.4f;
			}else if (snakes.get(i).getSnakeBody().size() < snakes.get(0).getSnakeBody().size()) {
			
			
				score[0] +=0.4f;
			}
		}
		
	}
}
