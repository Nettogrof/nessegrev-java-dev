/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.squad;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.SnakeInfoSquad;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants;
import ai.nettogrof.battlesnake.treesearch.search.standard.DuelNode;
import gnu.trove.list.array.TIntArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.BASIC_SCORE;
import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.EMPTY_AREA;
import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.STOP_EXPAND_LIMIT;

/**
 * This abstract squad node class is the based of all node class, provide basic
 * method use in any node for squad rules.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractSquadNode extends DuelNode {

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
	@Override
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

	/**
	 * Gets the index , for the teammate
	 * 
	 * @param snake Current snake
	 * @return int for the index of the teammate
	 */
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
	@Override
	protected void generateHash(final Int2IntOpenHashMap old, final Int2IntOpenHashMap newHash, final int[][] board) {

		old.forEach((position, value) -> {

			final int posX = position / 1000;
			final int posY = position % 1000;

			if (posX + 1 < width && checkBoardHash(posX + 1, posY, board, value)) {
				addToHash(newHash, position + 1000, value);

			}
			if (posX - 1 >= 0 && checkBoardHash(posX - 1, posY, board, value)) {
				addToHash(newHash, position - 1000, value);

			}

			if (posY + 1 < height && checkBoardHash(posX, posY + 1, board, value)) {
				addToHash(newHash, position + 1, value);
			}
			if (posY - 1 >= 0 && checkBoardHash(posX, posY - 1, board, value)) {
				addToHash(newHash, position - 1, value);
			}

		});

	}

	/**
	 * Check the board array if the hash value can expand
	 * 
	 * @param newPosX Position X
	 * @param newPosY Position Y
	 * @param board   Board array
	 * @param value   the hash value
	 * @return if possible to expand
	 */
	protected boolean checkBoardHash(final int newPosX, final int newPosY, final int[][] board, final int value) {
		return board[newPosX][newPosY] == EMPTY_AREA || board[newPosX][newPosY] == -value;
	}
	
	/**
	 * Update the score ratio
	 */
	@Override
	public void updateScoreRatio() {
		float totalOwnSquad = score[0];
		
		float totalOther = BASIC_SCORE;
		for (int i = 1; i < snakes.size(); i++) {
			if (((SnakeInfoSquad)snakes.get(i)).getSquad().equals(((SnakeInfoSquad)snakes.get(0)).getSquad())){
				totalOwnSquad += score[i];
			} else {
				totalOther += score[i];
			}
		}
		for (int i = 1; i < score.length; i++) {
			totalOther += score[i];
		}

		scoreRatio = (float) (totalOwnSquad/ (float) totalOther);
		if (scoreRatio == 0.0 || scoreRatio > STOP_EXPAND_LIMIT) {
			exp = false;
		}
	}

}
