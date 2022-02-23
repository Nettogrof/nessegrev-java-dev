package ai.nettogrof.battlesnake.snakes;

import java.lang.reflect.Constructor;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.treesearch.AbstractSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.fun.JustTurnSearch;
import ai.nettogrof.battlesnake.treesearch.search.fun.JustTurnRoyaleSearch;

/**
 * JustTurn snake. This class is the JustTurn snake on Battlesnake. This snake
 * was made just for fun during a Weekly Meet-up. This snake can just turn,
 * never straight
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class JustTurnSnake extends BetaSnake {

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public JustTurnSnake(final String gameId) {
		super(gameId);
		fileConfig = "JustTurn.properties";
		setProperties();
	}

	@Override
	protected String getFileConfig() {
		return "JustTurn.properties";
	}

	/**
	 * This method generate the search type
	 * 
	 * @return Abstract Search
	 * @throws ReflectiveOperationException In case of invalid search type
	 */
	@Override
	protected Constructor<? extends AbstractSearch> genSearchType() throws ReflectiveOperationException {

		if ("standard".equals(ruleset)) {
			return JustTurnSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class, int.class, GameRuleset.class);
		}
		return JustTurnRoyaleSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
				int.class, GameRuleset.class);

	}

}
