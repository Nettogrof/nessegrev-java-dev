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
import ai.nettogrof.battlesnake.treesearch.AbstractSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.AbstractRoyaleNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleDuelNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleFourNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleSearch;
import ai.nettogrof.battlesnake.treesearch.search.standard.RegularSearch;

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

		super(gameId);

		try (InputStream input = Files.newInputStream(Paths.get(fileConfig))) {

			final Properties prop = new Properties();

			prop.load(input);
			cpu_limit = Integer.parseInt(prop.getProperty("cpu"));
		} catch (IOException ex) {
			log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());
		}

	}

	/**
	 * This method will be call on each move request receive by BattleSnake
	 * 
	 * @param moveRequest Json call received
	 * @return map of field to be return to battlesnake, example "move" , "up"
	 */
	@Override
	public Map<String, String> move(final JsonNode moveRequest) {

		if (moveRequest.get(YOU).has("head")) {
			apiversion = 1;
		}

		final Long startTime = System.currentTimeMillis();

		final AbstractRoyaleNode root = (AbstractRoyaleNode) genRoot(moveRequest);
		root.exp = true;
		treeSearch(root, startTime);

		AbstractNode winner = chooseBestMove(root);

		if (winner == null && !root.getChild().isEmpty()) {
			winner = root.getChild().get(0);
		}

		lastRoot = root;

		log.atInfo().log("Turn:" + moveRequest.get(TURN).asInt() + " nb nodes" + root.getChildCount() + "  time: "
				+ (System.currentTimeMillis() - startTime));
		nodeTotalCount += root.getChildCount();
		timeTotal += System.currentTimeMillis() - startTime;
		return generateResponse(winner, root, moveRequest.get(YOU).withArray(BODY).get(0));
	}

	/**
	 * Execute the tree search
	 * 
	 * @param root      The root node
	 * @param startTime The start time in millisecond
	 */
	private void treeSearch(final AbstractRoyaleNode root, final Long startTime) {

		if (multiThread && root.getSnakes().size() < 5) {
			final ArrayList<AbstractNode> nodelist = new ArrayList<>();
			final ArrayList<AbstractNode> expandedlist = new ArrayList<>();
			nodelist.add(root);
			expand(nodelist, expandedlist);
			
			final ArrayList<RoyaleSearch> listSearchThread = new ArrayList<>();

			for (final AbstractNode c : root.getChild()) {
				listSearchThread
						.add(new RoyaleSearch((AbstractRoyaleNode) c, width, height, startTime, timeout - minusbuffer));

			}

			for (final RoyaleSearch s : listSearchThread) {
				final Thread subThread = new Thread(s);
				subThread.setPriority(3);
				subThread.start();
			}

			try {

				Thread.sleep(timeout - minusbuffer - 50);
			} catch (InterruptedException e) {

				log.atSevere().log("Thread?!", e);
			}

			for (final AbstractSearch search : listSearchThread) {
				search.stopSearching();

			}

			for (final AbstractNode c : nodelist) {
				c.updateScore();
			}

			for (int i = expandedlist.size() - 1; i >= 0; i--) {
				expandedlist.get(i).updateScore();
			}
			log.atInfo().log("Nb Thread: " + nodelist.size());
		} else {
			// Single thread
			final RoyaleSearch main = new RoyaleSearch(root, width, height, startTime, timeout - minusbuffer);
			main.run();
		}

	}
	
	/**
	 * Expand the base list of node until reaching cpu limit
	 * @param nodelist List of node that gonna to "rooted" in multithread search
	 * @param expandedlist List of node to be updated after search
	 */
	private void expand(final List<AbstractNode> nodelist, final List<AbstractNode> expandedlist) {
		boolean cont = true;
		while (cont) {
			if (nodelist.isEmpty()) {
				cont = false;
			}else {
				new RegularSearch(nodelist.get(0), width, height).generateChild();
				if (nodelist.size() - 1 + nodelist.get(0).getChild().size() < cpu_limit) {
					final AbstractNode oldroot = nodelist.remove(0);
					expandedlist.add(oldroot);
		
					for (final AbstractNode c : oldroot.getChild()) {
						nodelist.add(c);
					}
					cont = true;
				}else {
					cont = false;
				}
			}
		}	

	}

	/**
	 * Generate the root node based on the /move request
	 * 
	 * @param moveRequest Json request
	 * @return AbstractNode the root
	 */
	private AbstractNode genRoot(final JsonNode moveRequest) {
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
				snakes.add( new SnakeInfo(currentSnake));
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

		if (startRequest.get("ruleset") != null) {
			apiversion = 1;
			if (SQUAD.equals(startRequest.get("ruleset").get(NAME).asText())) {
				squad = true;
			}
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
		// TODO Add others type of node/rule.
		if (snakes.size() > 4) {
			RoyaleFourNode.width = width;
			RoyaleFourNode.height = height;
			return new RoyaleFourNode(snakes, food, hazard);
		} else if (snakes.size() > 2) {
			RoyaleFourNode.width = width;
			RoyaleFourNode.height = height;
			return new RoyaleFourNode(snakes, food, hazard);
		}

		RoyaleDuelNode.width = width;
		RoyaleDuelNode.height = height;
		return new RoyaleDuelNode(snakes, food, hazard);
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
	 * public static void main(String args[]) { ObjectMapper json = new
	 * ObjectMapper(); String pretest =
	 * " {\"you\":{\"latency\":\"214.828\",\"shout\":\"\",\"body\":[{\"y\":5,\"x\":6},{\"y\":6,\"x\":6},{\"y\":7,\"x\":6},{\"y\":8,\"x\":6},{\"y\":8,\"x\":7},{\"y\":9,\"x\":7},{\"y\":9,\"x\":8},{\"y\":9,\"x\":9},{\"y\":10,\"x\":9},{\"y\":10,\"x\":8},{\"y\":10,\"x\":7},{\"y\":10,\"x\":6},{\"y\":10,\"x\":5},{\"y\":10,\"x\":4},{\"y\":9,\"x\":4},{\"y\":9,\"x\":3},{\"y\":8,\"x\":3},{\"y\":7,\"x\":3},{\"y\":6,\"x\":3},{\"y\":6,\"x\":2},{\"y\":6,\"x\":1},{\"y\":7,\"x\":1},{\"y\":7,\"x\":2},{\"y\":8,\"x\":2},{\"y\":8,\"x\":1},{\"y\":9,\"x\":1},{\"y\":9,\"x\":0},{\"y\":8,\"x\":0},{\"y\":7,\"x\":0},{\"y\":6,\"x\":0},{\"y\":5,\"x\":0},{\"y\":5,\"x\":1},{\"y\":5,\"x\":2},{\"y\":5,\"x\":3},{\"y\":5,\"x\":4},{\"y\":5,\"x\":5}],\"id\":\"cbaeeb77-b551-4fab-99c2-eb383d2f7bec\",\"health\":79,\"length\":36,\"name\":\"Beta\",\"head\":{\"y\":5,\"x\":6}},\"turn\":445,\"board\":{\"snakes\":[{\"latency\":\"214.828\",\"shout\":\"\",\"body\":[{\"y\":5,\"x\":6},{\"y\":6,\"x\":6},{\"y\":7,\"x\":6},{\"y\":8,\"x\":6},{\"y\":8,\"x\":7},{\"y\":9,\"x\":7},{\"y\":9,\"x\":8},{\"y\":9,\"x\":9},{\"y\":10,\"x\":9},{\"y\":10,\"x\":8},{\"y\":10,\"x\":7},{\"y\":10,\"x\":6},{\"y\":10,\"x\":5},{\"y\":10,\"x\":4},{\"y\":9,\"x\":4},{\"y\":9,\"x\":3},{\"y\":8,\"x\":3},{\"y\":7,\"x\":3},{\"y\":6,\"x\":3},{\"y\":6,\"x\":2},{\"y\":6,\"x\":1},{\"y\":7,\"x\":1},{\"y\":7,\"x\":2},{\"y\":8,\"x\":2},{\"y\":8,\"x\":1},{\"y\":9,\"x\":1},{\"y\":9,\"x\":0},{\"y\":8,\"x\":0},{\"y\":7,\"x\":0},{\"y\":6,\"x\":0},{\"y\":5,\"x\":0},{\"y\":5,\"x\":1},{\"y\":5,\"x\":2},{\"y\":5,\"x\":3},{\"y\":5,\"x\":4},{\"y\":5,\"x\":5}],\"id\":\"cbaeeb77-b551-4fab-99c2-eb383d2f7bec\",\"health\":79,\"length\":36,\"name\":\"Beta\",\"head\":{\"y\":5,\"x\":6}},{\"latency\":\"213.889\",\"shout\":\"\",\"body\":[{\"y\":4,\"x\":5},{\"y\":4,\"x\":6},{\"y\":4,\"x\":7},{\"y\":4,\"x\":8},{\"y\":4,\"x\":9},{\"y\":4,\"x\":10},{\"y\":3,\"x\":10},{\"y\":2,\"x\":10},{\"y\":2,\"x\":9},{\"y\":2,\"x\":8},{\"y\":1,\"x\":8},{\"y\":1,\"x\":7},{\"y\":2,\"x\":7},{\"y\":3,\"x\":7},{\"y\":3,\"x\":6},{\"y\":2,\"x\":6},{\"y\":1,\"x\":6},{\"y\":1,\"x\":5},{\"y\":1,\"x\":4},{\"y\":2,\"x\":4},{\"y\":2,\"x\":3},{\"y\":1,\"x\":3},{\"y\":1,\"x\":2},{\"y\":2,\"x\":2},{\"y\":3,\"x\":2},{\"y\":3,\"x\":1},{\"y\":2,\"x\":1},{\"y\":1,\"x\":1},{\"y\":1,\"x\":0},{\"y\":2,\"x\":0},{\"y\":3,\"x\":0},{\"y\":4,\"x\":0},{\"y\":4,\"x\":1},{\"y\":4,\"x\":2}],\"id\":\"cd7edb09-0b06-41ad-8273-647135b2259f\",\"health\":54,\"length\":34,\"name\":\"Gamma\",\"head\":{\"y\":4,\"x\":5}}],\"width\":11,\"hazards\":[],\"height\":11,\"food\":[{\"y\":0,\"x\":9},{\"y\":0,\"x\":0},{\"y\":0,\"x\":8},{\"y\":9,\"x\":6},{\"y\":3,\"x\":5},{\"y\":7,\"x\":4},{\"y\":0,\"x\":10},{\"y\":6,\"x\":9},{\"y\":7,\"x\":9},{\"y\":5,\"x\":10},{\"y\":1,\"x\":10},{\"y\":3,\"x\":9}]},\"game\":{\"ruleset\":{\"name\":\"standard\",\"version\":\"Mojave/3.1\"},\"timeout\":500,\"id\":\"25d8fb1e-41d6-42c6-9bbd-3152a9408b12\"}} "
	 * ; ; String test =
	 * "{\"game\":{\"id\":\"2d3598c8-e029-4898-9ae9-624e62c5f7f7\",\"timeout\":500,\"ruleset\":{\"name\":\"royale\",\"version\":\"v1\"}},\"turn\":211,\"board\":{\"height\":11,\"width\":11,\"food\":[{\"x\":8,\"y\":1},{\"x\":0,\"y\":1},{\"x\":1,\"y\":2},{\"x\":6,\"y\":2}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":1},{\"x\":1,\"y\":2},{\"x\":1,\"y\":3},{\"x\":1,\"y\":4},{\"x\":1,\"y\":5},{\"x\":1,\"y\":6},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":1},{\"x\":2,\"y\":2},{\"x\":2,\"y\":3},{\"x\":2,\"y\":4},{\"x\":2,\"y\":5},{\"x\":2,\"y\":6},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":1},{\"x\":3,\"y\":2},{\"x\":3,\"y\":3},{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":3,\"y\":6},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":1},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":4,\"y\":5},{\"x\":4,\"y\":6},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":1},{\"x\":5,\"y\":2},{\"x\":5,\"y\":3},{\"x\":5,\"y\":4},{\"x\":5,\"y\":5},{\"x\":5,\"y\":6},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":6,\"y\":3},{\"x\":6,\"y\":4},{\"x\":6,\"y\":5},{\"x\":6,\"y\":6},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":1},{\"x\":7,\"y\":2},{\"x\":7,\"y\":3},{\"x\":7,\"y\":4},{\"x\":7,\"y\":5},{\"x\":7,\"y\":6},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":1},{\"x\":8,\"y\":2},{\"x\":8,\"y\":3},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}],\"snakes\":[{\"id\":\"e9cc3085-1f37-4335-b76d-a958bbb3a554\",\"name\":\"Beta\",\"health\":65,\"body\":[{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":10},{\"x\":4,\"y\":10},{\"x\":5,\"y\":10},{\"x\":5,\"y\":9},{\"x\":4,\"y\":9},{\"x\":4,\"y\":8},{\"x\":4,\"y\":7},{\"x\":4,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":2,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":4},{\"x\":2,\"y\":4}],\"latency\":0,\"head\":{\"x\":2,\"y\":8},\"length\":17,\"shout\":\"\",\"squad\":\"\"},{\"id\":\"af0b6e80-51c4-49ec-bb76-a1c6421bfa85\",\"name\":\"Old\",\"health\":32,\"body\":[{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":8,\"y\":10},{\"x\":7,\"y\":10},{\"x\":6,\"y\":10},{\"x\":6,\"y\":9},{\"x\":7,\"y\":9},{\"x\":8,\"y\":9},{\"x\":8,\"y\":8}],\"latency\":0,\"head\":{\"x\":9,\"y\":7},\"length\":11,\"shout\":\"\",\"squad\":\"\"}]},\"you\":{\"id\":\"e9cc3085-1f37-4335-b76d-a958bbb3a554\",\"name\":\"Beta\",\"health\":65,\"body\":[{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":10},{\"x\":4,\"y\":10},{\"x\":5,\"y\":10},{\"x\":5,\"y\":9},{\"x\":4,\"y\":9},{\"x\":4,\"y\":8},{\"x\":4,\"y\":7},{\"x\":4,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":2,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":4},{\"x\":2,\"y\":4}],\"latency\":0,\"head\":{\"x\":2,\"y\":8},\"length\":17,\"shout\":\"\",\"squad\":\"\"}} "
	 * ;
	 * 
	 * try { JsonNode parsedRequest = json.readTree(pretest); int size =
	 * parsedRequest.get(BOARD).get("height").asInt(); BetaSnake t = new
	 * BetaSnake("test");
	 * 
	 * t.heigth = size; t.width = size; t.timeout = 2500; t.multiThread = true;
	 * 
	 * try { Thread.sleep(100); } catch (InterruptedException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * System.out.println(t.move(parsedRequest)); parsedRequest =
	 * json.readTree(test); //System.out.println(t.move(parsedRequest)); try {
	 * Thread.sleep(510); } catch (InterruptedException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * } catch (IOException e) {
	 * 
	 * e.printStackTrace(); } }
	 */

}
