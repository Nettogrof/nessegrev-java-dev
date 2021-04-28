package ai.nettogrof.battlesnake.snakes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
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
	 * The config filename
	 */
	protected static String fileConfig = "Beta.properties";

	/**
	 * Boolean if a squad game
	 */
	public transient boolean squad;
	
	
	/**
	 * Basic / unused constructor
	 */
	public BetaSnake() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public BetaSnake(final String gameId) {
		super(gameId, fileConfig);
	}

	
	

	

	/**
	 * Generate the root node based on the /move request
	 * 
	 * @param moveRequest Json request
	 * @return AbstractNode the root
	 */
	protected AbstractNode genRoot(final JsonNode moveRequest) {
		final JsonNode board = moveRequest.get(BOARD);
		final FoodInfo food = new FoodInfo(board);

		final JsonNode betaSnake = moveRequest.get(YOU);
		final List<SnakeInfo> snakes = squad ? genSnakeInfoSquad(board, betaSnake) : genSnakeInfo(board, betaSnake);

		if (lastRoot != null) {

			for (final AbstractNode c : lastRoot.getChild()) {
				if (food.equals(c.getFood()) && c.getSnakes().size() == snakes.size()) {
					final List<SnakeInfo> csnake = c.getSnakes();
					boolean found = true;
					for (int i = 0; i < csnake.size() && found; i++) {
						found = csnake.get(i).equals(snakes.get(i));
					}
					if (found) {

						return c;
					}

				}
			}
		}

		return genNode(snakes, food, new HazardInfo(board));

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

		timeout = startRequest.get("game").get("timeout").asInt();

		return response;
	}

	/**
	 * This method generate the root node type
	 * 
	 * @param snakes List of snakes
	 * @param food   Food Information
	 * @param hazard Hazard Information
	 * @return Abstract node
	 */
	private AbstractNode genNode(final List<SnakeInfo> snakes, final FoodInfo food, final HazardInfo hazard) {
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
			}else {
				node = new DuelNode(snakes, food);
			}
			break;
		}

		
		return node;
	}

	/**
	 * Return the infos need by Battlesnake when receive a (root GET /) request 
	 * @return map of info for Battlesnake
	 */
	public static Map<String, String> getInfo() {
		
		final Map<String, String> response = new ConcurrentHashMap<>();
		try (InputStream input = Files.newInputStream(Paths.get(fileConfig))) {

			final Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			// get the property value and print it out

			response.put("apiversion", prop.getProperty("apiversion"));
			response.put("head", prop.getProperty("headType"));
			response.put("tail", prop.getProperty("tailType"));
			response.put("color", prop.getProperty("color"));
			response.put("author", "nettogrof");

		} catch (IOException ex) {
			log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());
		}

		return response;
	}

	

	/**
	 * Method use to set the fileConfig string
	 */
	@Override
	protected void setFileConfig() {
		fileConfig = "Beta.properties";
	}

	/**
	 * @param args
	 */
	/*
	  public static void main(String args[]) { 
		  ObjectMapper json = new ObjectMapper();
		  String pretest = "{\"game\":{\"id\":\"c9084ed7-4615-44e1-9464-8668b373d9c6\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":170,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_GSHFxtdQb8cqPxBRTdMp8k6V\",\"name\":\"Nessegrev-beta\",\"latency\":\"253\",\"health\":97,\"body\":[{\"x\":6,\"y\":10},{\"x\":6,\"y\":9},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":4,\"y\":10},{\"x\":4,\"y\":9},{\"x\":3,\"y\":9},{\"x\":2,\"y\":9},{\"x\":2,\"y\":8},{\"x\":2,\"y\":7},{\"x\":1,\"y\":7},{\"x\":1,\"y\":6},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7}],\"head\":{\"x\":6,\"y\":10},\"length\":14,\"shout\":\"\"},{\"id\":\"gs_QjrmXK8RxgwXrB9y6xhr9tgG\",\"name\":\"Awesome Snakes Done Quick von Haval\",\"latency\":\"276\",\"health\":96,\"body\":[{\"x\":7,\"y\":5},{\"x\":7,\"y\":4},{\"x\":6,\"y\":4},{\"x\":5,\"y\":4},{\"x\":4,\"y\":4},{\"x\":4,\"y\":5},{\"x\":4,\"y\":6},{\"x\":4,\"y\":7},{\"x\":5,\"y\":7}],\"head\":{\"x\":7,\"y\":5},\"length\":9,\"shout\":\"\"},{\"id\":\"gs_CkRCKgcKQYrM9CgxSG77WBPQ\",\"name\":\"Queueueue\",\"latency\":\"183\",\"health\":64,\"body\":[{\"x\":7,\"y\":3},{\"x\":6,\"y\":3},{\"x\":5,\"y\":3},{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2},{\"x\":1,\"y\":2},{\"x\":1,\"y\":1},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2}],\"head\":{\"x\":7,\"y\":3},\"length\":11,\"shout\":\"\"}],\"food\":[{\"x\":10,\"y\":10},{\"x\":3,\"y\":1},{\"x\":8,\"y\":10}],\"hazards\":[]},\"you\":{\"id\":\"gs_GSHFxtdQb8cqPxBRTdMp8k6V\",\"name\":\"Nessegrev-beta\",\"latency\":\"253\",\"health\":97,\"body\":[{\"x\":6,\"y\":10},{\"x\":6,\"y\":9},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":4,\"y\":10},{\"x\":4,\"y\":9},{\"x\":3,\"y\":9},{\"x\":2,\"y\":9},{\"x\":2,\"y\":8},{\"x\":2,\"y\":7},{\"x\":1,\"y\":7},{\"x\":1,\"y\":6},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7}],\"head\":{\"x\":6,\"y\":10},\"length\":14,\"shout\":\"\"}}"; 
		 // String test = "{\"game\":{\"id\":\"2d3598c8-e029-4898-9ae9-624e62c5f7f7\",\"timeout\":500,\"ruleset\":{\"name\":\"royale\",\"version\":\"v1\"}},\"turn\":211,\"board\":{\"height\":11,\"width\":11,\"food\":[{\"x\":8,\"y\":1},{\"x\":0,\"y\":1},{\"x\":1,\"y\":2},{\"x\":6,\"y\":2}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":1},{\"x\":1,\"y\":2},{\"x\":1,\"y\":3},{\"x\":1,\"y\":4},{\"x\":1,\"y\":5},{\"x\":1,\"y\":6},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":1},{\"x\":2,\"y\":2},{\"x\":2,\"y\":3},{\"x\":2,\"y\":4},{\"x\":2,\"y\":5},{\"x\":2,\"y\":6},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":1},{\"x\":3,\"y\":2},{\"x\":3,\"y\":3},{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":3,\"y\":6},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":1},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":4,\"y\":5},{\"x\":4,\"y\":6},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":1},{\"x\":5,\"y\":2},{\"x\":5,\"y\":3},{\"x\":5,\"y\":4},{\"x\":5,\"y\":5},{\"x\":5,\"y\":6},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":6,\"y\":3},{\"x\":6,\"y\":4},{\"x\":6,\"y\":5},{\"x\":6,\"y\":6},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":1},{\"x\":7,\"y\":2},{\"x\":7,\"y\":3},{\"x\":7,\"y\":4},{\"x\":7,\"y\":5},{\"x\":7,\"y\":6},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":1},{\"x\":8,\"y\":2},{\"x\":8,\"y\":3},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}],\"snakes\":[{\"id\":\"e9cc3085-1f37-4335-b76d-a958bbb3a554\",\"name\":\"Beta\",\"health\":65,\"body\":[{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":10},{\"x\":4,\"y\":10},{\"x\":5,\"y\":10},{\"x\":5,\"y\":9},{\"x\":4,\"y\":9},{\"x\":4,\"y\":8},{\"x\":4,\"y\":7},{\"x\":4,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":2,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":4},{\"x\":2,\"y\":4}],\"latency\":0,\"head\":{\"x\":2,\"y\":8},\"length\":17,\"shout\":\"\",\"squad\":\"\"},{\"id\":\"af0b6e80-51c4-49ec-bb76-a1c6421bfa85\",\"name\":\"Old\",\"health\":32,\"body\":[{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":8,\"y\":10},{\"x\":7,\"y\":10},{\"x\":6,\"y\":10},{\"x\":6,\"y\":9},{\"x\":7,\"y\":9},{\"x\":8,\"y\":9},{\"x\":8,\"y\":8}],\"latency\":0,\"head\":{\"x\":9,\"y\":7},\"length\":11,\"shout\":\"\",\"squad\":\"\"}]},\"you\":{\"id\":\"e9cc3085-1f37-4335-b76d-a958bbb3a554\",\"name\":\"Beta\",\"health\":65,\"body\":[{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":10},{\"x\":4,\"y\":10},{\"x\":5,\"y\":10},{\"x\":5,\"y\":9},{\"x\":4,\"y\":9},{\"x\":4,\"y\":8},{\"x\":4,\"y\":7},{\"x\":4,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":2,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":4},{\"x\":2,\"y\":4}],\"latency\":0,\"head\":{\"x\":2,\"y\":8},\"length\":17,\"shout\":\"\",\"squad\":\"\"}} " ;
	  
		  try { 
			  JsonNode parsedRequest = json.readTree(pretest); 
			  int size =  parsedRequest.get(BOARD).get("height").asInt();
			  BetaSnake t = new BetaSnake("test");
			  t.ruleset = "constrictor";
			  t.height = size; t.width = size; t.timeout = 500; t.multiThread = true;
			  t.cpu_limit=4;
			  
			  try {
				t.searchType=t.genSearchType();
			} catch (ReflectiveOperationException e1) {
			
				e1.printStackTrace();
			}
			  try { Thread.sleep(100); 
			  } catch (InterruptedException e) {
	  
				  e.printStackTrace(); 
				  }
	  
			  System.out.println(t.move(parsedRequest));
			 // parsedRequest = json.readTree(test);
			  //System.out.println(t.move(parsedRequest));
			  try {
	  Thread.sleep(510); } catch (InterruptedException e) {
	  
	  e.printStackTrace(); }
	  
	  } catch (IOException e) {
	  
	  e.printStackTrace(); } }
	 */

}
