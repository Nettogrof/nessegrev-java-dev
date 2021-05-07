/**
 * 
 */
package ai.nettogrof.battlesnake.snakes;

import java.lang.reflect.Constructor;

import ai.nettogrof.battlesnake.treesearch.AbstractSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.fun.LeftRoyaleSearch;
import ai.nettogrof.battlesnake.treesearch.search.fun.LeftStandardSearch;

/**
 * Right snake. This class is the "Nessegrev-Left" snake on Battlesnake. This
 * snake was made just for fun during  a Weekly Meet-up. This snake can turn left
 * or go straight.
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class LeftSnake extends BetaSnake {

	/**
	 * Basic / unused constructor
	 */
	public LeftSnake() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public LeftSnake(final String gameId) {
		super(gameId);
		fileConfig = "Left.properties";
		setProperties();
	}

	@Override
	protected void setFileConfig() {
		fileConfig = "Left.properties";

	}

	@Override
	protected String getFileConfig() {
		return "Left.properties";
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
			return LeftStandardSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class);
		}
		return LeftRoyaleSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class, int.class);
		
	}
}
