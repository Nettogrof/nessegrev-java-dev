package ai.nettogrof.battlesnake.snakes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.SnakeInfoSquad;
import ai.nettogrof.battlesnake.info.hazard.HazardSquare;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleDuelNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleFourNode;
import ai.nettogrof.battlesnake.treesearch.search.squad.SquadNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.DuelNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.FourNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.ManyNode;

/**
 * Beta snake. This class is the "Nessegrev-Beta" snake on Battlesnake. This
 * snake was in the Summer League/Veteran Division. Still active on the global
 * arena. Based on the Alpha snake, but fewer bugs and a bit quicker. Decent
 * snake using minimax/payoff matrix algorithm only. This snake can play
 * standard, squad, and royale. This snake should work with API v0 and API v1.
 * All the move calculation are based on API v0, and if it's API v1, then the
 * snake switch UP and DOWN response.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class BetaSnake extends AbstractTreeSearchSnakeAI {

	/**
	 * Boolean if a squad game
	 */
	public transient boolean squad;

	public BetaSnake() {
		super("0");
		fileConfig = "Beta.properties";
		setProperties();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public BetaSnake(final String gameId) {
		super(gameId);
		fileConfig = "Beta.properties";
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
		final HazardSquare hazard = new HazardSquare(board);
		final JsonNode betaSnake = moveRequest.get(YOU);
		final List<SnakeInfo> snakes = squad ? genSnakeInfoSquad(board, betaSnake) : genSnakeInfo(board, betaSnake);
		final AbstractNode oldChild = findChildNewRoot(snakes, food, hazard);
		return oldChild == null ? genNode(snakes, food, new HazardSquare(board)) : oldChild;

	}

	/**
	 * Generate all snakes info from the json board field
	 * 
	 * @param board     Json board field
	 * @param betaSnake Json you field
	 * @return list of snakes info
	 */
	private List<SnakeInfo> genSnakeInfo(final JsonNode board, final JsonNode betaSnake) {
		final List<SnakeInfo> snakes = new ArrayList<>();
		snakes.add(new SnakeInfo(betaSnake));
		for (int i = 0; i < board.get(SNAKES).size(); i++) {
			final JsonNode currentSnake = board.get(SNAKES).get(i);
			if (!currentSnake.get("id").asText().equals(betaSnake.get("id").asText())) {
				snakes.add(new SnakeInfo(currentSnake));
			}
		}
		return snakes;

	}

	/**
	 * Generate all snakes info from the json board field for squad mode
	 * 
	 * @param board     Json board field
	 * @param betaSnake Json you field
	 * @return list of snakes info
	 */
	private List<SnakeInfo> genSnakeInfoSquad(final JsonNode board, final JsonNode betaSnake) {
		// TODO Refactoring this method, way too similar to the other genSnakeInfo
		final List<SnakeInfo> snakes = new ArrayList<>();
		snakes.add(new SnakeInfoSquad(betaSnake));
		for (int i = 0; i < board.get(SNAKES).size(); i++) {
			final JsonNode currentSnake = board.get(SNAKES).get(i);
			if (!currentSnake.get("id").asText().equals(betaSnake.get("id").asText())) {
				snakes.add(new SnakeInfoSquad(currentSnake));
			}
		}
		return snakes;

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

		if (startRequest.get("game").get("ruleset") != null) {
			ruleset = startRequest.get("game").get("ruleset").get(NAME).asText();
			apiversion = 1;
			if (ruleset.equals(SQUAD)) {
				squad = true;
			}
		}

		width = startRequest.get(BOARD).get(WIDTH_FIELD).asInt();
		height = startRequest.get(BOARD).get(HEIGHT_FIELD).asInt();

		timeout = startRequest.get("game").get("timeout").asInt();

		try {
			searchType = genSearchType();
		} catch (ReflectiveOperationException e) {
			log.atWarning().log(e.getMessage() + "\n" + e.getStackTrace());
		}
		return getInfo();
	}

	/**
	 * This method generate the root node type
	 * 
	 * @param snakes List of snakes
	 * @param food   Food Information
	 * @param hazard Hazard Information
	 * @return Abstract node
	 */
	protected AbstractNode genNode(final List<SnakeInfo> snakes, final FoodInfo food, final HazardSquare hazard) {
		AbstractNode node;
		AbstractNode.width = width;
		AbstractNode.height = height;
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
			if (snakes.size() > FOUR_SNAKE) {
				node = new ManyNode(snakes, food);
			} else if (snakes.size() > TWO_SNAKE) {
				node = new FourNode(snakes, food);
			} else {
				node = new DuelNode(snakes, food);
			}
			break;
		}

		return node;
	}

	@Override
	protected String getFileConfig() {
		return fileConfig;
	}

}
