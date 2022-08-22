/**
 * 
 */
package ai.nettogrof.battlesnake.snakes;

import java.lang.reflect.Constructor;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.treesearch.AbstractSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.fun.LimitedMoveRoyaleSearch;

/**
 * Right snake. This class is the "Nessegrev-Mystery" snake on Battlesnake. This
 * snake was made just for fun for a Weekly Meet-up. This snake can turn right
 * or go straight.
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class RightSnake extends ExpertSnake {

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public RightSnake(final String gameId) {
		super(gameId);
		fileConfig = "Right.properties";
		setProperties();
		LimitedMoveRoyaleSearch.setRightOnly();
	}

	@Override
	protected String getFileConfig() {
		return "Right.properties";
	}

	/**
	 * This method generate the search type
	 * 
	 * @return Abstract Search
	 * @throws ReflectiveOperationException In case of invalid search type
	 */
	@Override
	protected Constructor<? extends AbstractSearch> genSearchType() throws ReflectiveOperationException {
		return  LimitedMoveRoyaleSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class, GameRuleset.class);
		

	}
	
	
}
