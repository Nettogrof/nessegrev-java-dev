/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.node;

import java.util.Arrays;
import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants;
import gnu.trove.list.array.TIntArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.BORDER_SCORE;
import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.EMPTY_AREA;

/**
 * This abstract evaluation node class is the based of all node class methods
 * evaluation, provide eval method use in any node.
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public abstract class AbstractEvaluationNode extends AbstractNode {

	/**
	 * default openhashmap value
	 */
	private final static int DEFAULTRV = new Int2IntOpenHashMap().defaultReturnValue();

	/**
	 * Basic constructor
	 */
	public AbstractEvaluationNode() {
		super();
	}

	/**
	 * Constructor with snakes and food information
	 * 
	 * @param snakes List of snakes
	 * @param food   Food information
	 */
	public AbstractEvaluationNode(final List<SnakeInfo> snakes, final FoodInfo food) {
		super(snakes, food);
		score = new float[snakes.size()];
	}

	/**
	 * Adjust our snake score if our snake is on the border of the board
	 * 
	 * @param head Square of the snake head
	 */
	protected void adjustBorderScore(final int head) {
		final int headX = head / 1000;
		final int headY = head % 1000;
		if (headX == 0 || headX == width - 1) {
			score[0] -= BORDER_SCORE;
		}

		if (headY == 0 || headY == height - 1) {
			score[0] -= BORDER_SCORE;
		}

	}

	/**
	 * Adjust our snake score for the distance between head and the nearest food
	 * 
	 * @param head Square of the snake head
	 */
	protected void addScoreDistance(final int head) {
		score[0] += (width - food.getShortestDistance(head / 1000, head % 1000)) * 0.095f;
	}

	/**
	 * Apply the new Hash map value to the board.
	 * 
	 * @param newHash The new hash map of position/value
	 * @param board   Board array
	 */
	protected void applyNewHash(final Int2IntOpenHashMap newHash, int[][] board) {
		newHash.forEach((xy, v) -> {
			board[xy / 1000][xy % 1000] = v;
		});

	}

	/**
	 * Generate new position to be added to hash
	 * 
	 * @param old     Previous hash
	 * @param newHash New hash
	 * @param board   Board array
	 */
	protected void generateHash(final Int2IntOpenHashMap old, final Int2IntOpenHashMap newHash, final int[][] board) {

		old.forEach((position, valeur) -> {

			final int posX = position / 1000;
			final int posY = position % 1000;

			if (posX + 1 < width && board[posX + 1][posY] == EMPTY_AREA) {
				addToHash(newHash, position + 1000, valeur);

			}
			if (posX - 1 >= 0 && board[posX - 1][posY] == EMPTY_AREA) {
				addToHash(newHash, position - 1000, valeur);

			}

			if (posY + 1 < height && board[posX][posY + 1] == EMPTY_AREA) {
				addToHash(newHash, position + 1, valeur);
			}
			if (posY - 1 >= 0 && board[posX][posY - 1] == EMPTY_AREA) {
				addToHash(newHash, position - 1, valeur);
			}

		});

	}

	/**
	 * Add the position and value to the hash map
	 * 
	 * @param newHash  hash map of position / value
	 * @param position the current position
	 * @param value    the value
	 */
	protected void addToHash(final Int2IntOpenHashMap newHash, final int position, final int value) {
		final int prev = newHash.putIfAbsent(position, value);
		if (prev != DEFAULTRV && prev != value) {
			newHash.put(position, BattleSnakeConstants.SPLIT_AREA);
		}

	}

	/**
	 * Adding basic length to score and health score = length + health /50
	 */
	protected void addBasicLengthScore() {
		for (int i = 0; i < snakes.size(); i++) {
			score[i] = snakes.get(i).isAlive() ? snakes.get(i).getSnakeBody().size() + snakes.get(i).getHealth() / 50
					: 0;
		}
	}

	/**
	 * Add or remove score to our snake if it longer or shorter than the other
	 * snakes
	 */
	protected void addSizeCompareScore() {
		for (int i = 1; i < snakes.size(); i++) {
			if (snakes.get(i).getSnakeBody().size() > snakes.get(0).getSnakeBody().size()) {
				score[0] -= 0.4f;
			} else if (snakes.get(i).getSnakeBody().size() < snakes.get(0).getSnakeBody().size()) {
				score[0] += 0.4f;
			}
		}

	}

	/**
	 * Adjust the score based on number of square controls by snakes The board array
	 * contain the snake number from 1 to X snakes
	 * 
	 * @param board Board array
	 */
	protected void adjustScodeBasedonBoardControl(final int[][] board) {

		int[] count = new int[snakes.size()];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (board[i][j] >= 0) {
					count[board[i][j]]++;
				}
			}
		}

		// Adding value if a tail is in the controlled area
		int total = 0;
		for (int i = 0; i < snakes.size(); i++) {
			final int posTail = snakes.get(i).getTail();
			final int boardValue = board[posTail / 1000][posTail % 1000];
			if (boardValue >= 0) {
				count[boardValue] += BattleSnakeConstants.TAIL_VALUE_AREA;
			}
			total += count[i];
		}

		// Assign the score
		for (int i = 0; i < snakes.size(); i++) {
			score[i] += ((float) count[i]) / total;
		}

	}

	/**
	 * Initiate the board array
	 * 
	 * @return board array
	 */
	protected int[][] initBoard() {
		int[][] board = new int[width][height];
		for (int i = 0; i < width; i++) {
			Arrays.fill(board[i], EMPTY_AREA);
		}
		for (final SnakeInfo snake : snakes) {
			final TIntArrayList body = snake.getSnakeBody();
			for (int i = 0; i < body.size() - 1; i++) {
				final int square = body.getQuick(i);

				board[square / 1000][square % 1000] = BattleSnakeConstants.SNAKE_BODY;

			}
		}
		return board;
	}

	/**
	 * Generate score based on the area control by the snake. Using a kind of
	 * voronoi algo.
	 */
	protected void listAreaControl() {

		// If a single snake assign max score
		if (snakes.size() <  BattleSnakeConstants.MINIMUN_SNAKE) {
			score[0] = BattleSnakeConstants.MAX_SCORE;
			return;
		}
		final int[][] board = initBoard();

		final Int2IntOpenHashMap old = new Int2IntOpenHashMap();
		final Int2IntOpenHashMap newHash = new Int2IntOpenHashMap();

		for (int i = 0; i < snakes.size(); i++) {
			newHash.put(snakes.get(i).getSnakeBody().get(0), i);
		}

		// while still new square assign to a snake control
		while (newHash.size() != 0) {
			old.clear();
			applyNewHash(newHash, board);
			generateHash(newHash, old, board);
			newHash.clear();
			if (old.size() != 0) {
				applyNewHash(old, board);
				generateHash(old, newHash, board);
			}
		}
		adjustScodeBasedonBoardControl(board);

	}

	/**
	 * Count the number of snake still alive
	 * 
	 * @return Number of snake alive
	 */
	protected int countSnakeAlive() {

		int nbAlive = 0;

		for (final SnakeInfo s : snakes) {
			if (s.isAlive()) {
				nbAlive++;
			}
		}

		return nbAlive;

	}

	/**
	 * Set Max score to winner snakes.
	 */
	protected void setWinnerMaxScore() {
		exp = false;
		for (int i = 0; i < score.length; i++) {
			if (snakes.get(i).getHealth() > 0 && snakes.get(i).isAlive()) {
				score[i] = BattleSnakeConstants.MAX_SCORE;
			}
		}
	}

}
