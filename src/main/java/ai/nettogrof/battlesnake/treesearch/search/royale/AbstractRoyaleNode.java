package ai.nettogrof.battlesnake.treesearch.search.royale;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.HazardInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants;
import ai.nettogrof.battlesnake.treesearch.node.AbstractEvaluationNode;
import gnu.trove.list.array.TIntArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 * This abstract royale node class is the based of all node class, provide basic
 * method use in any node for royale rules.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractRoyaleNode extends AbstractEvaluationNode {

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
	 * Generate score based on the area control by the snake. Using a kind of
	 * voronoi algo.
	 */
	protected void listAreaControl() {

		// If a single snake assign max score
		if (snakes.size() < 2) {
			score[0] = BattleSnakeConstants.MAX_SCORE;
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
				} else if (board[i][j] == BattleSnakeConstants.SPLIT_AREA) {

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
				count[boardValue - 1] += BattleSnakeConstants.TAIL_VALUE_AREA;
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

				board[square / 1000][square % 1000] = BattleSnakeConstants.SNAKE_BODY;

			}
		}
		return board;
	}

}
