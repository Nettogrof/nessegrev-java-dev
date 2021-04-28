package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.HazardInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstant;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import gnu.trove.list.array.TIntArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 * This abstract royale node class is the based of all node class, provide basic
 * method use in any node for royale rules.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractRoyaleNode extends AbstractNode {

	/**
	 * default openhashmap value
	 */
	private final static int defaultv = new Int2IntOpenHashMap().defaultReturnValue();

	/**
	 * Hazards info
	 */
	protected transient HazardInfo hazard;

	/**
	 * Basic constructor
	 */
	public AbstractRoyaleNode() {
		super();
	}

	/**
	 * Constructor with snakes and food information
	 * 
	 * @param snakes List of snakes
	 * @param food   Food information
	 */
	public AbstractRoyaleNode(final List<SnakeInfo> snakes, final FoodInfo food) {
		super(snakes, food);

	}

	/**
	 * Constructor with snakes and food information
	 * 
	 * @param snakes List of snakes
	 * @param food   Food information
	 * @param hazard Hazard Info
	 */
	public AbstractRoyaleNode(final List<SnakeInfo> snakes, final FoodInfo food, final HazardInfo hazard) {
		super(snakes, food);
		this.hazard = hazard;
	}

	/**
	 * Gets hazard information from this node
	 * 
	 * @return Hazard info
	 */
	public HazardInfo getHazard() {
		return hazard;
	}

	/**
	 * Adjust our snake score according if our head is in hazard
	 * 
	 * @param head Square of the head
	 */
	protected void adjustHazardScore(final int head) {
		if (hazard != null && hazard.isHazard(head / 1000, head % 1000)) {
			score[0] -= 3.0f;
		}

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
			score[0] -= BattleSnakeConstant.BORDER_SCORE;
		}

		if (headY == 0 || headY == height - 1) {
			score[0] -= BattleSnakeConstant.BORDER_SCORE;
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
	 * Generate score based on the area control by the snake. Using a kind of
	 * voronoi algo.
	 */
	protected void listAreaControl() {

		// If a single snake assign max score
		if (snakes.size() < 2) {
			score[0] = BattleSnakeConstant.MAX_SCORE;
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

		}
		removeHazardZone(board);
		adjustScodeBasedonBoardControl(board);

	}

	/**
	 * Remove hazard from the control Area because there's no value to control
	 * hazard area
	 * 
	 * @param board Board array
	 */
	private void removeHazardZone(final int[][] board) {
		final TIntArrayList listHazard = hazard.getListHazard();
		for (int i = 0; i < listHazard.size(); i++) {
			board[listHazard.get(i) / 1000][listHazard.get(i) % 1000] = -1;
		}

	}

	/**
	 * Adjust the score based on number of square controls by snakes The board array
	 * contain the snake number from 1 to X snakes
	 * 
	 * @param board Board array
	 */
	protected void adjustScodeBasedonBoardControl(final int[][] board) {
		final int biggestSnake = snakes.get(0).getSnakeBody().size() > snakes.get(1).getSnakeBody().size() ? 0 : 1;
		int[] count = new int[snakes.size()];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (board[i][j] > 0) {
					count[board[i][j] - 1]++;
				} else if (board[i][j] == BattleSnakeConstant.SPLIT_AREA) {

					count[biggestSnake]++;
				}
			}
		}

		// Adding value if a tail is in the controlled area
		int total = 0;
		for (int i = 0; i < snakes.size(); i++) {
			final int posTail = snakes.get(i).getTail();
			final int boardValue = board[posTail / 1000][posTail % 1000];
			if (boardValue > 0) {
				count[boardValue - 1] += BattleSnakeConstant.TAIL_VALUE_AREA;
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

		for (final SnakeInfo snake : snakes) {
			final TIntArrayList body = snake.getSnakeBody();
			for (int i = 0; i < body.size() - 1; i++) {
				final int square = body.getQuick(i);

				board[square / 1000][square % 1000] = BattleSnakeConstant.SNAKE_BODY;

			}
		}
		return board;
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
		if (prev != defaultv && prev != value) {
			newHash.put(position, BattleSnakeConstant.SPLIT_AREA);
		}

	}

}
