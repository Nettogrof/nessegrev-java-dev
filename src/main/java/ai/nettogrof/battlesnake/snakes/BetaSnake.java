package ai.nettogrof.battlesnake.snakes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.HazardInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.SnakeInfoSquad;
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

	/**
	 * Basic / unused constructor
	 */
	public BetaSnake() {
		super();
		fileConfig = "Beta.properties";
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
		final HazardInfo hazard = new HazardInfo(board);
		final JsonNode betaSnake = moveRequest.get(YOU);
		final List<SnakeInfo> snakes = squad ? genSnakeInfoSquad(board, betaSnake) : genSnakeInfo(board, betaSnake);
		final AbstractNode oldChild = findChildNewRoot(snakes, food,hazard);
		return oldChild == null ? genNode(snakes, food, new HazardInfo(board)) : oldChild;

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
	protected AbstractNode genNode(final List<SnakeInfo> snakes, final FoodInfo food, final HazardInfo hazard) {
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
			if (snakes.size() > 4) {
				node = new ManyNode(snakes, food);
			} else if (snakes.size() > 2) {
				node = new FourNode(snakes, food);
			} else {
				node = new DuelNode(snakes, food);
			}
			break;
		}

		return node;
	}

	/**
	 * Method use to set the fileConfig string
	 */
	@Override
	protected void setFileConfig() {
		fileConfig = "Beta.properties";
	}

	@Override
	protected String getFileConfig() {
		return fileConfig;
	}

	
/*
	public static void main(String args[]) {
		System.out.println("test");
		ObjectMapper json = new ObjectMapper();
		String pretest = " {\"game\":{\"id\":\"97da1f9a-67f9-4fa9-87cd-fa922b94d057\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":130,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_XQhBg33RYXxHdffpdFdpJctD\",\"name\":\"snakespeare\",\"latency\":\"4\",\"health\":96,\"body\":[{\"x\":3,\"y\":7},{\"x\":4,\"y\":7},{\"x\":4,\"y\":8},{\"x\":5,\"y\":8},{\"x\":6,\"y\":8},{\"x\":6,\"y\":7},{\"x\":6,\"y\":6},{\"x\":5,\"y\":6}],\"head\":{\"x\":3,\"y\":7},\"length\":8,\"shout\":\"\"},{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"351\",\"health\":86,\"body\":[{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2}],\"head\":{\"x\":2,\"y\":6},\"length\":14,\"shout\":\"\"}],\"food\":[{\"x\":5,\"y\":10},{\"x\":6,\"y\":9},{\"x\":9,\"y\":9},{\"x\":9,\"y\":6},{\"x\":7,\"y\":10},{\"x\":1,\"y\":6},{\"x\":4,\"y\":10}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"351\",\"health\":86,\"body\":[{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2}],\"head\":{\"x\":2,\"y\":6},\"length\":14,\"shout\":\"\"}}";
		
		
		String test[] = {
				"{\"game\":{\"id\":\"97da1f9a-67f9-4fa9-87cd-fa922b94d057\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":131,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_XQhBg33RYXxHdffpdFdpJctD\",\"name\":\"snakespeare\",\"latency\":\"6\",\"health\":95,\"body\":[{\"x\":3,\"y\":8},{\"x\":3,\"y\":7},{\"x\":4,\"y\":7},{\"x\":4,\"y\":8},{\"x\":5,\"y\":8},{\"x\":6,\"y\":8},{\"x\":6,\"y\":7},{\"x\":6,\"y\":6}],\"head\":{\"x\":3,\"y\":8},\"length\":8,\"shout\":\"\"},{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"1\",\"health\":85,\"body\":[{\"x\":2,\"y\":7},{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2}],\"head\":{\"x\":2,\"y\":7},\"length\":14,\"shout\":\"I'm your father\"}],\"food\":[{\"x\":5,\"y\":10},{\"x\":6,\"y\":9},{\"x\":9,\"y\":9},{\"x\":9,\"y\":6},{\"x\":7,\"y\":10},{\"x\":1,\"y\":6},{\"x\":4,\"y\":10}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"1\",\"health\":85,\"body\":[{\"x\":2,\"y\":7},{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2}],\"head\":{\"x\":2,\"y\":7},\"length\":14,\"shout\":\"I'm your father\"}}",
			//	"{\"game\":{\"id\":\"97da1f9a-67f9-4fa9-87cd-fa922b94d057\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":132,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_XQhBg33RYXxHdffpdFdpJctD\",\"name\":\"snakespeare\",\"latency\":\"5\",\"health\":79,\"body\":[{\"x\":3,\"y\":9},{\"x\":3,\"y\":8},{\"x\":3,\"y\":7},{\"x\":4,\"y\":7},{\"x\":4,\"y\":8},{\"x\":5,\"y\":8},{\"x\":6,\"y\":8},{\"x\":6,\"y\":7}],\"head\":{\"x\":3,\"y\":9},\"length\":8,\"shout\":\"\"},{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"1\",\"health\":84,\"body\":[{\"x\":2,\"y\":8},{\"x\":2,\"y\":7},{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1}],\"head\":{\"x\":2,\"y\":8},\"length\":14,\"shout\":\"I'm your father\"}],\"food\":[{\"x\":5,\"y\":10},{\"x\":6,\"y\":9},{\"x\":9,\"y\":9},{\"x\":9,\"y\":6},{\"x\":7,\"y\":10},{\"x\":1,\"y\":6},{\"x\":4,\"y\":10}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"1\",\"health\":84,\"body\":[{\"x\":2,\"y\":8},{\"x\":2,\"y\":7},{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1}],\"head\":{\"x\":2,\"y\":8},\"length\":14,\"shout\":\"I'm your father\"}}",
			//	"{\"game\":{\"id\":\"97da1f9a-67f9-4fa9-87cd-fa922b94d057\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":133,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_XQhBg33RYXxHdffpdFdpJctD\",\"name\":\"snakespeare\",\"latency\":\"4\",\"health\":63,\"body\":[{\"x\":4,\"y\":9},{\"x\":3,\"y\":9},{\"x\":3,\"y\":8},{\"x\":3,\"y\":7},{\"x\":4,\"y\":7},{\"x\":4,\"y\":8},{\"x\":5,\"y\":8},{\"x\":6,\"y\":8}],\"head\":{\"x\":4,\"y\":9},\"length\":8,\"shout\":\"\"},{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"1\",\"health\":68,\"body\":[{\"x\":2,\"y\":9},{\"x\":2,\"y\":8},{\"x\":2,\"y\":7},{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1}],\"head\":{\"x\":2,\"y\":9},\"length\":14,\"shout\":\"I'm your father\"}],\"food\":[{\"x\":5,\"y\":10},{\"x\":6,\"y\":9},{\"x\":9,\"y\":9},{\"x\":9,\"y\":6},{\"x\":7,\"y\":10},{\"x\":1,\"y\":6},{\"x\":4,\"y\":10}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"1\",\"health\":68,\"body\":[{\"x\":2,\"y\":9},{\"x\":2,\"y\":8},{\"x\":2,\"y\":7},{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1}],\"head\":{\"x\":2,\"y\":9},\"length\":14,\"shout\":\"I'm your father\"}}",
			//	"{\"game\":{\"id\":\"97da1f9a-67f9-4fa9-87cd-fa922b94d057\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":134,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_XQhBg33RYXxHdffpdFdpJctD\",\"name\":\"snakespeare\",\"latency\":\"5\",\"health\":85,\"body\":[{\"x\":4,\"y\":10},{\"x\":4,\"y\":9},{\"x\":3,\"y\":9},{\"x\":3,\"y\":8},{\"x\":3,\"y\":7},{\"x\":4,\"y\":7},{\"x\":4,\"y\":8},{\"x\":5,\"y\":8},{\"x\":5,\"y\":8}],\"head\":{\"x\":4,\"y\":10},\"length\":9,\"shout\":\"\"},{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"1\",\"health\":52,\"body\":[{\"x\":2,\"y\":10},{\"x\":2,\"y\":9},{\"x\":2,\"y\":8},{\"x\":2,\"y\":7},{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2}],\"head\":{\"x\":2,\"y\":10},\"length\":14,\"shout\":\"I'm your father\"}],\"food\":[{\"x\":5,\"y\":10},{\"x\":6,\"y\":9},{\"x\":9,\"y\":9},{\"x\":9,\"y\":6},{\"x\":7,\"y\":10},{\"x\":1,\"y\":6}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"1\",\"health\":52,\"body\":[{\"x\":2,\"y\":10},{\"x\":2,\"y\":9},{\"x\":2,\"y\":8},{\"x\":2,\"y\":7},{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2}],\"head\":{\"x\":2,\"y\":10},\"length\":14,\"shout\":\"I'm your father\"}}",
				"{\"game\":{\"id\":\"97da1f9a-67f9-4fa9-87cd-fa922b94d057\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":135,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_XQhBg33RYXxHdffpdFdpJctD\",\"name\":\"snakespeare\",\"latency\":\"4\",\"health\":85,\"body\":[{\"x\":5,\"y\":10},{\"x\":4,\"y\":10},{\"x\":4,\"y\":9},{\"x\":3,\"y\":9},{\"x\":3,\"y\":8},{\"x\":3,\"y\":7},{\"x\":4,\"y\":7},{\"x\":4,\"y\":8},{\"x\":5,\"y\":8},{\"x\":5,\"y\":8}],\"head\":{\"x\":5,\"y\":10},\"length\":10,\"shout\":\"\"},{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"2\",\"health\":36,\"body\":[{\"x\":3,\"y\":10},{\"x\":2,\"y\":10},{\"x\":2,\"y\":9},{\"x\":2,\"y\":8},{\"x\":2,\"y\":7},{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3}],\"head\":{\"x\":3,\"y\":10},\"length\":14,\"shout\":\"I'm your father\"}],\"food\":[{\"x\":6,\"y\":9},{\"x\":9,\"y\":9},{\"x\":9,\"y\":6},{\"x\":7,\"y\":10},{\"x\":1,\"y\":6}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"2\",\"health\":36,\"body\":[{\"x\":3,\"y\":10},{\"x\":2,\"y\":10},{\"x\":2,\"y\":9},{\"x\":2,\"y\":8},{\"x\":2,\"y\":7},{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3}],\"head\":{\"x\":3,\"y\":10},\"length\":14,\"shout\":\"I'm your father\"}}"
				
		};
		
		
		
		// String
		// test
		// ={"game":{"id":"97da1f9a-67f9-4fa9-87cd-fa922b94d057","ruleset":{"name":"royale","version":"v1.0.17"},"timeout":500},"turn":134,"board":{"height":11,"width":11,"snakes":[{"id":"gs_XQhBg33RYXxHdffpdFdpJctD","name":"snakespeare","latency":"5","health":85,"body":[{"x":4,"y":10},{"x":4,"y":9},{"x":3,"y":9},{"x":3,"y":8},{"x":3,"y":7},{"x":4,"y":7},{"x":4,"y":8},{"x":5,"y":8},{"x":5,"y":8}],"head":{"x":4,"y":10},"length":9,"shout":""},{"id":"gs_B8qmywFJvy3jt8v7Ywp4mCTP","name":"Nessegrev-gamma","latency":"1","health":52,"body":[{"x":2,"y":10},{"x":2,"y":9},{"x":2,"y":8},{"x":2,"y":7},{"x":2,"y":6},{"x":3,"y":6},{"x":3,"y":5},{"x":4,"y":5},{"x":5,"y":5},{"x":6,"y":5},{"x":6,"y":4},{"x":7,"y":4},{"x":7,"y":3},{"x":7,"y":2}],"head":{"x":2,"y":10},"length":14,"shout":"I'm
		// your
		// father"}],"food":[{"x":5,"y":10},{"x":6,"y":9},{"x":9,"y":9},{"x":9,"y":6},{"x":7,"y":10},{"x":1,"y":6}],"hazards":[{"x":0,"y":0},{"x":0,"y":1},{"x":0,"y":2},{"x":0,"y":3},{"x":0,"y":4},{"x":0,"y":5},{"x":0,"y":6},{"x":0,"y":7},{"x":0,"y":8},{"x":0,"y":9},{"x":0,"y":10},{"x":1,"y":0},{"x":1,"y":9},{"x":1,"y":10},{"x":2,"y":0},{"x":2,"y":9},{"x":2,"y":10},{"x":3,"y":0},{"x":3,"y":9},{"x":3,"y":10},{"x":4,"y":0},{"x":4,"y":9},{"x":4,"y":10},{"x":5,"y":0},{"x":5,"y":9},{"x":5,"y":10},{"x":6,"y":0},{"x":6,"y":9},{"x":6,"y":10},{"x":7,"y":0},{"x":7,"y":9},{"x":7,"y":10},{"x":8,"y":0},{"x":8,"y":9},{"x":8,"y":10},{"x":9,"y":0},{"x":9,"y":1},{"x":9,"y":2},{"x":9,"y":3},{"x":9,"y":4},{"x":9,"y":5},{"x":9,"y":6},{"x":9,"y":7},{"x":9,"y":8},{"x":9,"y":9},{"x":9,"y":10},{"x":10,"y":0},{"x":10,"y":1},{"x":10,"y":2},{"x":10,"y":3},{"x":10,"y":4},{"x":10,"y":5},{"x":10,"y":6},{"x":10,"y":7},{"x":10,"y":8},{"x":10,"y":9},{"x":10,"y":10}]},"you":{"id":"gs_B8qmywFJvy3jt8v7Ywp4mCTP","name":"Nessegrev-gamma","latency":"1","health":52,"body":[{"x":2,"y":10},{"x":2,"y":9},{"x":2,"y":8},{"x":2,"y":7},{"x":2,"y":6},{"x":3,"y":6},{"x":3,"y":5},{"x":4,"y":5},{"x":5,"y":5},{"x":6,"y":5},{"x":6,"y":4},{"x":7,"y":4},{"x":7,"y":3},{"x":7,"y":2}],"head":{"x":2,"y":10},"length":14,"shout":"I'm
		// your
		// father"}}

		try {
			JsonNode parsedRequest = json.readTree(test[0]);
			int size = parsedRequest.get(BOARD).get("height").asInt();
			BetaSnake t = new BetaSnake("test");
			t.ruleset = "royale";
			t.height = size;
			t.width = size;
			t.timeout = 1500;
			t.multiThread = true;
			t.cpuLimit = 4;

			try {
				t.searchType = t.genSearchType();
			} catch (ReflectiveOperationException e1) {

				e1.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			//System.out.println(t.move(parsedRequest)); 
			for (int i = 0 ; i < test.length; i++) {
				parsedRequest =json.readTree(test[i]); 
				System.out.println(t.move(parsedRequest));
				try {
					Thread.sleep(510);
				} catch (InterruptedException e) {
	
					e.printStackTrace();
				}
			
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
*/
}
