/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.squad;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.SnakeInfoSquad;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants;
import ai.nettogrof.battlesnake.treesearch.node.AbstractEvaluationNode;
import gnu.trove.list.array.TIntArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 * This abstract squad node class is the based of all node class, provide basic
 * method use in any node for squad rules.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractSquadNode extends AbstractEvaluationNode {

	/**
	 * default openhashmap value
	 */
	private final static int defaultv = new Int2IntOpenHashMap().defaultReturnValue();

	/**
	 * Constructor with snakes and food information
	 * 
	 * @param snakes List of snakes
	 * @param food   Food information
	 */
	@SuppressWarnings("unchecked")
	public AbstractSquadNode(final List<? extends SnakeInfo> snakes, final FoodInfo food) {
		super((List<SnakeInfo>) snakes, food);

	}

	@Override
	public float getScoreRatio() {
		if ("".equals(((SnakeInfoSquad) snakes.get(0)).getSquad())) {
			float totalOther = 1;
			for (int i = 1; i < score.length; i++) {
				totalOther += score[i];
			}

			return score[0] / (float) totalOther;

		} else {

			float totalOther = 0.01f;
			for (int i = 1; i < score.length; i++) {
				if (!((SnakeInfoSquad) snakes.get(0)).getSquad().equals(((SnakeInfoSquad) snakes.get(0)).getSquad())) {
					totalOther += score[i];
				}
			}

			return score[0] / (float) totalOther;

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
	 * Initiate the board array
	 * 
	 * @return board array
	 */
	protected int[][] initBoard() {
		// TODO Change area control in Squad to "by-pass" teammate body
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
			newHash.put(position, BattleSnakeConstants.SPLIT_AREA);
		}

	}

	

}
