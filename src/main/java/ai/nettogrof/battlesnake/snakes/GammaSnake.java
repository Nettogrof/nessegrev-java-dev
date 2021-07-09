package ai.nettogrof.battlesnake.snakes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.DuelNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.FourNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.ManyNode;

/**
 * Gamma snake. This class is the "Nessegrev-gamma" snake on Battlesnake. This
 * snake was in the Fall League and in Winter Classic in elite division. Use
 * mostly area control as evaluation. With the new release Gamma is now more
 * duel oriented. Doesn't take hazard sauce into account. This snake should work
 * with API v0 and API v1. All the move calculation are based on API v0, and if
 * it's API v1, then the snake switch UP and DOWN response.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class GammaSnake extends AbstractTreeSearchSnakeAI {

	/**
	 * Basic / unused constructor
	 */
	public GammaSnake() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public GammaSnake(final String gameId) {
		super(gameId);
		fileConfig = "Gamma.properties";
		setProperties();

	}

	/**
	 * Generate the root node based on the /move request
	 * 
	 * @param moveRequest Json request
	 * @return AbstractNode the root
	 */
	@Override
	protected AbstractNode genRoot(final JsonNode moveRequest) {
		final JsonNode board = moveRequest.get(BOARD);
		final FoodInfo food = new FoodInfo(board);

		final List<SnakeInfo> snakes = new ArrayList<>();
		final JsonNode gammaSnake = moveRequest.get(YOU);

		snakes.add(new SnakeInfo(gammaSnake));
		for (int i = 0; i < board.get(SNAKES).size(); i++) {
			final JsonNode currentSnake = board.get(SNAKES).get(i);
			if (!currentSnake.get("id").asText().equals(gammaSnake.get("id").asText())) {
				snakes.add(new SnakeInfo(currentSnake));
			}
		}

		final AbstractNode oldChild = findChildNewRoot(snakes, food, null);
		return oldChild == null ? genNode(snakes, food) : oldChild;

	}

	/**
	 * This method generate the root node type
	 * 
	 * @param snakes List of snakes
	 * @param food   Food Information
	 * @return Abstract node
	 */
	private AbstractNode genNode(final List<SnakeInfo> snakes, final FoodInfo food) {
		AbstractNode.width = width;
		AbstractNode.height = height;
		if (snakes.size() > 4) {
			return new ManyNode(snakes, food);
		} else if (snakes.size() > 2) {
			return new FourNode(snakes, food);
		}
		return new DuelNode(snakes, food);
	}

	/**
	 * This method was used in API v0 to retrieve snake info, but in API v1 the
	 * method is call but Battlesnake doesn't need a response. Beta snake is
	 * compatible for both API version that why it's return snake info
	 * 
	 * @param startRequest Json call received
	 * @return map that can be empty because it will be ignore by BattleSnake server
	 */
	@Override
	public Map<String, String> start(final JsonNode startRequest) {
		if (startRequest.get("ruleset") == null) {
			timeout = 500;
		} else {
			apiversion = 1;
			timeout = startRequest.get("game").get("timeout").asInt();
		}
		ruleset = "standard"; // Gamma Snake, play only standard game.
		try {
			searchType = genSearchType();
		} catch (ReflectiveOperationException e) {
			log.atWarning().log(e.getMessage() + "\n" + e.getStackTrace());
		}

		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("color", "#216121");
		response.put("headType", "shac-gamer");
		response.put("tailType", "shac-coffee");
		width = startRequest.get(BOARD).get(WIDTH_FIELD).asInt();
		height = startRequest.get(BOARD).get(HEIGHT_FIELD).asInt();

		return response;
	}

	/**
	 * Method use to set the fileConfig string
	 */
	@Override
	protected void setFileConfig() {
		fileConfig = "Gamma.properties";
	}

	@Override
	protected String getFileConfig() {
		return fileConfig;
	}

	/*
	 * public static void main(String args[]) {
	 * MctsSearch.class.getConstructor(parameterTypes) }
	 */

}
