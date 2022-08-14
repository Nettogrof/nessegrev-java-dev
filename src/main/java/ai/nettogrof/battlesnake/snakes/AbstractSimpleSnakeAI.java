/**
 * 
 */
package ai.nettogrof.battlesnake.snakes;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.API_V1;

import java.util.Map;

/**
 * Any snake simple should extend this abstract class will contains basic constant
 * fields, related to the field name in json call from BattleSnake. Also some
 * method that any snake must implements.
 * 
 * @author carl.lajeunesse
 * @version Summer 2022
 */
public abstract class AbstractSimpleSnakeAI extends AbstractSnakeAI {

	/**
	 * Basic / unused constructor
	 */
	protected AbstractSimpleSnakeAI() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	protected AbstractSimpleSnakeAI(final String gameId) {
		super(gameId);
		
	}

	/**
	 * Return the best move from the map
	 * @param possiblemove  map of possible move
	 * @return  move as String
	 */
	protected String getBestPossibleMove(final Map<String, Integer> possiblemove) {
		String res = UPWARD;
		int value = possiblemove.get(UPWARD);

		if (possiblemove.get(DOWN) > value) {
			value = possiblemove.get(DOWN);
			res = DOWN;
		}

		if (possiblemove.get(LEFT) > value) {
			value = possiblemove.get(LEFT);
			res = LEFT;
		}
		if (possiblemove.get(RIGHT) > value) {
			res = RIGHT;
		}
		if (apiversion == API_V1) {
			if (res.equals(UPWARD)) {
				res = DOWN;
			} else if (res.equals(DOWN)) {
				res = UPWARD;
			}
		}
		return res;
	}
	

}
