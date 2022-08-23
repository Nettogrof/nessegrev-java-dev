package ai.nettogrof.battlesnake.snakes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;

import ai.nettogrof.battlesnake.info.BoardInfo;
import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.hazard.HazardSquare;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleDuelNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleFourNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.wrapped.WrappedRoyaleNode;
import ai.nettogrof.battlesnake.treesearch.search.squad.SquadNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.DuelNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.FourNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.ManyNode;

/**
 * Expert snake. This class is used by the "Nessegrev-Beta" snake on
 * Battlesnake. Decent snake using minimax/payoff matrix algorithm only. This
 * snake can play standard, squad, royale, and wrapped. This snake should work
 * with API v0 and API v1. All the move calculation are based on API v0, and if
 * it's API v1, then the snake switch UP and DOWN response.
 * 
 * @author carl.lajeunesse
 * @version Summer 2022
 */
public class ExpertSnake extends AbstractMultiThreadSnakeAI {

	/**
	 * Basic Constructor
	 * 
	 */
	public ExpertSnake() {
		super("0");
		fileConfig = "Expert.properties";
		setProperties();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public ExpertSnake(final String gameId) {
		super(gameId);
		fileConfig = "Expert.properties";
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
		final JsonNode expertSnake = moveRequest.get(YOU);
		final List<SnakeInfo> snakes = genSnakeInfo(board, expertSnake);
		final AbstractNode oldChild = findChildNewRoot(snakes, food, hazard);
		return oldChild == null ? genNode(snakes, food, new HazardSquare(board)) : oldChild;

	}

	/**
	 * Generate all snakes info from the json board field
	 * 
	 * @param board       Json board field
	 * @param expertSnake Json you field
	 * @return list of snakes info
	 */
	private List<SnakeInfo> genSnakeInfo(final JsonNode board, final JsonNode expertSnake) {
		final List<SnakeInfo> snakes = new ArrayList<>();

		snakes.add(new SnakeInfo(expertSnake));

		for (int i = 0; i < board.get(SNAKES).size(); i++) {
			final JsonNode currentSnake = board.get(SNAKES).get(i);
			if (!currentSnake.get("id").asText().equals(expertSnake.get("id").asText())) {

				snakes.add(new SnakeInfo(currentSnake));

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
			rules = new GameRuleset(startRequest.get("game").get("ruleset"));
			apiversion = 1;
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
		BoardInfo board = new BoardInfo(height, width);

		switch (rules.getRuleset()) {
			case "royale":
				node = snakes.size() > 2 ? new RoyaleFourNode(snakes, food, hazard, board)
						: new RoyaleDuelNode(snakes, food, hazard, board);
				break;
			case "constrictor":
				node = new DuelNode(snakes, food, board);
				break;
			case "squad":
				node = new SquadNode(snakes, food, board);
				break;

			case "wrapped":
				node = new WrappedRoyaleNode(snakes, food, hazard, board);
				break;
			default:
				if (snakes.size() > FOUR_SNAKE) {
					node = new ManyNode(snakes, food, board);
				} else if (snakes.size() > TWO_SNAKE) {
					node = new FourNode(snakes, food, board);
				} else {
					node = new DuelNode(snakes, food, board);
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
