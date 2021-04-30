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

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.EMPTY_AREA;

/**
 * This abstract squad node class is the based of all node class, provide basic
 * method use in any node for squad rules.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractSquadNode extends AbstractEvaluationNode {

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
	 * Initiate the board array
	 * 
	 * @return board array
	 */
	protected int[][] initBoard() {

		int[][] board = new int[width][height];

		for (final SnakeInfo snake : snakes) {
			final int value = -getPartnerIndex((SnakeInfoSquad) snake);
			final TIntArrayList body = snake.getSnakeBody();
			for (int i = 0; i < body.size() - 1; i++) {
				final int square = body.getQuick(i);

				board[square / 1000][square % 1000] = value;

			}
		}
		return board;
	}

	private int getPartnerIndex(final SnakeInfoSquad snake) {
		for (int i = 0; i < snakes.size(); i++) {
			if (snake.getSquad().equals(((SnakeInfoSquad) snakes.get(i)).getSquad())
					&& !snake.getName().equals(snakes.get(i).getName())) {
				return i;
			}
		}
		return BattleSnakeConstants.SNAKE_BODY;
	}

	/**
	 * Generate new position to be added to hash
	 * 
	 * @param old     Previous hash
	 * @param newHash New hash
	 * @param board   Board array
	 */
	protected void generateHash(final Int2IntOpenHashMap old, final Int2IntOpenHashMap newHash, final int[][] board) {

		old.forEach((position, value) -> {

			final int posX = position / 1000;
			final int posY = position % 1000;

			if (posX + 1 < width && (board[posX + 1][posY] == EMPTY_AREA || board[posX + 1][posY] == -value)) {
				addToHash(newHash, position + 1000, value);

			}
			if (posX - 1 >= 0 && (board[posX - 1][posY] == EMPTY_AREA || board[posX - 1][posY] == -value)) {
				addToHash(newHash, position - 1000, value);

			}

			if (posY + 1 < height && (board[posX][posY + 1] == EMPTY_AREA || board[posX][posY + 1] == -value)) {
				addToHash(newHash, position + 1, value);
			}
			if (posY - 1 >= 0 && (board[posX][posY - 1] == EMPTY_AREA || board[posX][posY - 1] == -value)) {
				addToHash(newHash, position - 1, value);
			}

		});

	}

}
