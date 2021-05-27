/**
 * 
 */
package ai.nettogrof.battlesnake.snakes;

import java.lang.reflect.Constructor;
import java.util.List;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.HazardInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.AbstractSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.constrictor.ConstrictorSearch;
import ai.nettogrof.battlesnake.treesearch.search.fun.fortoby.ForTobyFourNode;
import ai.nettogrof.battlesnake.treesearch.search.fun.fortoby.ForTobySearch;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleDuelNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleFourNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleSearch;
import ai.nettogrof.battlesnake.treesearch.search.squad.SquadNode;
import ai.nettogrof.battlesnake.treesearch.search.squad.SquadSearch;
import ai.nettogrof.battlesnake.treesearch.search.standard.DuelNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.FourNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.ManyNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.MctsSearch;

/**
 * ForTwoBe snake. This class is the "Nessegrev-Mystery-42b" snake on
 * Battlesnake. This snake was made just for fun for a Weekly Meet-up.
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class ForTwoBeSnake extends BetaSnake {

	/**
	 * Basic / unused constructor
	 */
	public ForTwoBeSnake() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public ForTwoBeSnake(final String gameId) {
		super(gameId);
		fileConfig = "ForTwoBe.properties";
		setProperties();
	}

	@Override
	protected void setFileConfig() {
		fileConfig = "ForTwoBe.properties";

	}

	@Override
	protected String getFileConfig() {
		return "ForTwoBe.properties";
	}

	/**
	 * This method generate the search type
	 * 
	 * @return Abstract Search
	 * @throws ReflectiveOperationException In case of invalid search type
	 */
	@Override
	protected Constructor<? extends AbstractSearch> genSearchType() throws ReflectiveOperationException {
		boolean tobyFound = false;
		if (lastRoot != null) {
			for (final SnakeInfo snake : lastRoot.getSnakes()) {
				if ("Toby Flendersnake".equals(snake.getName())) {
					tobyFound = true;
				}
			}
		}

		if (tobyFound) {
			return ForTobySearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class, int.class);
		}
		switch (ruleset) {
		case "standard":
			return MctsSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class, int.class);
		case "constrictor":
			return ConstrictorSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class);
		case "royale":
			return RoyaleSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class, int.class);
		case "squad":
			return SquadSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class, int.class);
		default:
			return MctsSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class, int.class);
		}

	}

	/**
	 * This method generate the root node type
	 * 
	 * @param snakes List of snakes
	 * @param food   Food Information
	 * @param hazard Hazard Information
	 * @return Abstract node
	 */
	protected AbstractNode genNode(final List<SnakeInfo> snakes, final FoodInfo food, final HazardInfo hazard) {
		AbstractNode node;
		AbstractNode.width = width;
		AbstractNode.height = height;
		boolean tobyFound = false;
		if (lastRoot != null) {
			for (final SnakeInfo snake : lastRoot.getSnakes()) {
				if ("Toby Flendersnake".equals(snake.getName())) {
					tobyFound = true;
				}
			}
		}
		if (tobyFound) {
			node = new ForTobyFourNode(snakes, food, hazard);

		} else {
			switch (ruleset) {
			case "royale":
				node = snakes.size() > 2 ? new RoyaleFourNode(snakes, food, hazard)
						: new RoyaleDuelNode(snakes, food, hazard);
				break;
			case "constrictor":
				node = new DuelNode(snakes, food);
				break;
			case "squad":
				node = new SquadNode(snakes, food);
				break;
			default:
				if (snakes.size() > 4) {
					node = new ManyNode(snakes, food);
				} else if (snakes.size() > 2) {
					node = new FourNode(snakes, food);
				} else {
					node = new DuelNode(snakes, food);
				}
				break;
			}
		}
		return node;
	}

}
