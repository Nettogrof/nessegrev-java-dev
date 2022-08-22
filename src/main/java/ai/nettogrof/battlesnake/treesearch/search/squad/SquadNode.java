/**
 * 
 */
package ai.nettogrof.battlesnake.treesearch.search.squad;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.BASIC_SCORE;
import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.EMPTY_AREA;

import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants;
import ai.nettogrof.battlesnake.snakes.common.SnakeGeneticConstants;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.AbstractStandardNode;
import gnu.trove.list.array.TIntArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

/**
 * This Squad node class must be in squad mode
 * 
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class SquadNode extends AbstractStandardNode {
	/**
	 * Minimun number of snake for Squad Mode.  If number of snake smaller than this value, the game end
	 */
	protected static final int MINIMUN_SNAKE = 4;
	/**
	 * Constructor, set the information and evaluate/ set score directly
	 * 
	 * @param snakes List of snakes
	 * @param food   Food information
	 */
	public SquadNode(final List< SnakeInfo> snakes, final FoodInfo food) {
		super(snakes, food);
		setSquadScore();
	}

	/**
	 * Sets the node score
	 */
	private void setSquadScore() {
		if (countSnakeAlive() < MINIMUN_SNAKE) {
			// Only one team alive no need to explore this node anymore and set max score
			// to surviving team
			setWinnerMaxScore();
		} else {
			
			addBasicLengthScore();
		}

		updateScoreRatio();

	}

	@Override
	public AbstractNode createNode(final List<SnakeInfo> snakes, final AbstractNode currentNode) {
		return new SquadNode(snakes, currentNode.getFood());
	}
	
	@Override
	public float getScoreRatio() {
		float totalOther;
		if ("".equals( snakes.get(0).getSquad())) {
			totalOther = 1;
			for (int i = 1; i < score.length; i++) {
				totalOther += score[i];
			}

		} else {

			totalOther = 0.01f;
			for (int i = 1; i < score.length; i++) {
				if (!snakes.get(0).getSquad().equals(snakes.get(i).getSquad())) {
					totalOther += score[i];
				}
			}

		}
		return score[0] / totalOther;

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
			final int value = -getPartnerIndex(snake);
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
	private int getPartnerIndex(final SnakeInfo snake) {
		for (int i = 0; i < snakes.size(); i++) {
			if (snake.getSquad().equals(snakes.get(i).getSquad())
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
			if (snakes.get(i).getSquad().equals(snakes.get(0).getSquad())) {
				totalOwnSquad += score[i];
			} else {
				totalOther += score[i];
			}
		}
		for (int i = 1; i < score.length; i++) {
			totalOther += score[i];
		}

		scoreRatio = totalOwnSquad / totalOther;
		if (scoreRatio == 0.0 || scoreRatio > SnakeGeneticConstants.getStopExpandLimit()) {
			exp = false;
		}
	}

}
