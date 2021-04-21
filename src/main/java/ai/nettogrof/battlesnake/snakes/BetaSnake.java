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
import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.HazardInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfoSquad;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.AbstractRoyaleNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleDuelNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleFourNode;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleSearch;

/**
 * @author carl.lajeunesse
 *
 */
public class BetaSnake extends AbstractTreeSearchSnakeAI {

	/**
	 * 
	 */
	private transient AbstractRoyaleNode lastRoot;
	/**
	 * 
	 */
	protected static String fileConfig = "Beta.properties";
	/**
	 * 
	 */
	public transient int api;
	/**
	 * 
	 */
	public transient boolean squad;

	/**
	 * 
	 */
	public BetaSnake() {
		super();
	}

	/**
	 * @param gameId
	 */
	public BetaSnake(final String gameId) {

		super(gameId);

		try (InputStream input = Files.newInputStream(Paths.get(fileConfig))) {

			final Properties prop = new Properties();

			prop.load(input);

		} catch (IOException ex) {
			log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());
		}

	}

	/**
	 *
	 */
	@Override
	public Map<String, String> move(final JsonNode moveRequest) {

		if (moveRequest.get(YOU).has("head")) {
			apiversion = 1;
		}
		
		

		final Long startTime = System.currentTimeMillis();
		

		final AbstractRoyaleNode root = (AbstractRoyaleNode) genRoot(moveRequest);
		root.exp = true;
		treeSearch(root,startTime);
		/*************************** NEW Multithread */
		
		AbstractNode winner = chooseBestMove(root);
		
		if (winner == null && !root.getChild().isEmpty() ) {
			
				winner = root.getChild().get(0);
			
		}
		
		
		lastRoot = root;
				
		log.atInfo().log("Turn:" +moveRequest.get(TURN).asInt() +" nb nodes" + root.getChildCount() + "  time: " + (System.currentTimeMillis() - startTime));
		nodeTotalCount += root.getChildCount();
		timeTotal += System.currentTimeMillis() - startTime;
		return generateResponse(winner,root, moveRequest.get(YOU).withArray(BODY).get(0));
	}

	private void treeSearch(final AbstractRoyaleNode root,final Long startTime) {
		if (multiThread && root.getSnakes().size() <5) {
			new RoyaleSearch(root, width, heigth).generateChild();
			// final int cpu =2;

			final ArrayList<RoyaleSearch> listSearchThread = new ArrayList<>();

			for (final AbstractNode c : root.getChild()) {
				listSearchThread
						.add(new RoyaleSearch((AbstractRoyaleNode) c, width, heigth, startTime, timeout - minusbuffer));

			}

			for (final RoyaleSearch s : listSearchThread) {
				final Thread subThread = new Thread(s);
				subThread.setPriority(3);
				subThread.start();
				// System.out.println("start" + (System.currentTimeMillis() -st));
			}

			try {

				Thread.sleep(timeout - minusbuffer - 50);
			} catch (InterruptedException e) {

				log.atSevere().log("Thread?!", e);
			}

			for (final RoyaleSearch search : listSearchThread) {
				search.stopSearching();
				// System.out.println("stop" + (System.currentTimeMillis() -st));
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {

				log.atSevere().log("Thread?!", e);
			}
			root.updateScore();
		} else {
			final RoyaleSearch main = new RoyaleSearch(root, width, heigth, startTime, timeout - minusbuffer);
			main.run();
		}
		
	}

	/**
	 * @param moveRequest
	 * @return
	 */
	private AbstractNode genRoot(final JsonNode moveRequest) {
		final JsonNode board = moveRequest.get(BOARD);
		final FoodInfo food = new FoodInfo(board);

		final ArrayList<SnakeInfo> snakes = new ArrayList<>();
		final JsonNode betaSnake = moveRequest.get(YOU);
		if (squad) {
			genSnakeInfoSquad(snakes, board, betaSnake);
		}else {
			genSnakeInfo(snakes,board,  betaSnake);
		}
		
		
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

	private void genSnakeInfo(final List<SnakeInfo> snakes,final JsonNode board,final JsonNode betaSnake) {
		snakes.add(new SnakeInfo());
		snakes.get(0).setHealth(betaSnake.get(HEALTH).asInt());
		snakes.get(0).setName(betaSnake.get(NAME).asText());
		snakes.get(0).setSnake(betaSnake);
		

		for (int i = 0; i < board.get(SNAKES).size(); i++) {
			final JsonNode currentSnake = board.get(SNAKES).get(i);
			if (!currentSnake.get("id").asText().equals(betaSnake.get("id").asText())) {
				final SnakeInfo otherSnake = new SnakeInfo();
				otherSnake.setHealth(currentSnake.get(HEALTH).asInt());
				otherSnake.setName(currentSnake.get(NAME).asText());
				otherSnake.setSnake(currentSnake);
				snakes.add(otherSnake);
			}
		}
		
	}

	private void genSnakeInfoSquad(final List<SnakeInfo> snakes,final JsonNode board,final JsonNode betaSnake) {
		snakes.add(new SnakeInfoSquad());
		snakes.get(0).setHealth(betaSnake.get(HEALTH).asInt());
		snakes.get(0).setName(betaSnake.get(NAME).asText());
		snakes.get(0).setSnake(betaSnake);
		if (betaSnake.get(SQUAD) != null) {
			((SnakeInfoSquad)snakes.get(0)).setSquad(betaSnake.get(SQUAD).asText());
		}

		for (int i = 0; i < board.get(SNAKES).size(); i++) {
			final JsonNode currentSnake = board.get(SNAKES).get(i);
			if (!currentSnake.get("id").asText().equals(betaSnake.get("id").asText())) {
				final SnakeInfoSquad otherSnake = new SnakeInfoSquad();
				otherSnake.setHealth(currentSnake.get(HEALTH).asInt());
				otherSnake.setName(currentSnake.get(NAME).asText());
				otherSnake.setSnake(currentSnake);
				if (currentSnake.get(SQUAD) == null) {
					otherSnake.setSquad("");
				} else {
					otherSnake.setSquad(currentSnake.get(SQUAD).asText());
				}
				snakes.add(otherSnake);
			}
		}
		
	}

	/**
	 *
	 */
	@Override
	public Map<String, String> start(final JsonNode startRequest) {

		if (startRequest.get("ruleset") != null) {
			api = 1;
			if (SQUAD.equals(startRequest.get("ruleset").get(NAME).asText())) {
				squad = true;
			}
		}

		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("color", "#216121");
		response.put("headType", "shac-gamer");
		response.put("tailType", "shac-coffee");
		width = startRequest.get(BOARD).get(WIDTH_FIELD).asInt();
		heigth = startRequest.get(BOARD).get(HEIGTH_FIELD).asInt();

		timeout = startRequest.get("game").get("timeout").asInt();

		return response;
	}

	/**
	 * @param snakes
	 * @param food
	 * @param hazard
	 * @return
	 */
	private AbstractNode genNode(final List<SnakeInfo> snakes, final FoodInfo food, final HazardInfo hazard) {
		
		
		if (snakes.size() > 4) {
			RoyaleFourNode.width = width;
			RoyaleFourNode.heigth = heigth;
			return new RoyaleFourNode(snakes, food, hazard);
		} else if (snakes.size() > 2) {
			RoyaleFourNode.width = width;
			RoyaleFourNode.heigth = heigth;
			return new RoyaleFourNode(snakes, food, hazard);
		}
	
		RoyaleDuelNode.width = width;
		RoyaleDuelNode.heigth = heigth;
		return new RoyaleDuelNode(snakes, food, hazard);
	}

	/**
	 *
	 */
	@Override
	protected void setFileConfig() {
		fileConfig = "Beta.properties";
	}

	/**
	 *
	 */
	@Override
	protected String getFileConfig() {
		return fileConfig;
	}

	/**
	 * @return
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
	 * @param args
	 */
	/*public static void main(String args[]) {
		ObjectMapper json = new ObjectMapper();
		String pretest = " {\"you\":{\"latency\":\"214.828\",\"shout\":\"\",\"body\":[{\"y\":5,\"x\":6},{\"y\":6,\"x\":6},{\"y\":7,\"x\":6},{\"y\":8,\"x\":6},{\"y\":8,\"x\":7},{\"y\":9,\"x\":7},{\"y\":9,\"x\":8},{\"y\":9,\"x\":9},{\"y\":10,\"x\":9},{\"y\":10,\"x\":8},{\"y\":10,\"x\":7},{\"y\":10,\"x\":6},{\"y\":10,\"x\":5},{\"y\":10,\"x\":4},{\"y\":9,\"x\":4},{\"y\":9,\"x\":3},{\"y\":8,\"x\":3},{\"y\":7,\"x\":3},{\"y\":6,\"x\":3},{\"y\":6,\"x\":2},{\"y\":6,\"x\":1},{\"y\":7,\"x\":1},{\"y\":7,\"x\":2},{\"y\":8,\"x\":2},{\"y\":8,\"x\":1},{\"y\":9,\"x\":1},{\"y\":9,\"x\":0},{\"y\":8,\"x\":0},{\"y\":7,\"x\":0},{\"y\":6,\"x\":0},{\"y\":5,\"x\":0},{\"y\":5,\"x\":1},{\"y\":5,\"x\":2},{\"y\":5,\"x\":3},{\"y\":5,\"x\":4},{\"y\":5,\"x\":5}],\"id\":\"cbaeeb77-b551-4fab-99c2-eb383d2f7bec\",\"health\":79,\"length\":36,\"name\":\"Beta\",\"head\":{\"y\":5,\"x\":6}},\"turn\":445,\"board\":{\"snakes\":[{\"latency\":\"214.828\",\"shout\":\"\",\"body\":[{\"y\":5,\"x\":6},{\"y\":6,\"x\":6},{\"y\":7,\"x\":6},{\"y\":8,\"x\":6},{\"y\":8,\"x\":7},{\"y\":9,\"x\":7},{\"y\":9,\"x\":8},{\"y\":9,\"x\":9},{\"y\":10,\"x\":9},{\"y\":10,\"x\":8},{\"y\":10,\"x\":7},{\"y\":10,\"x\":6},{\"y\":10,\"x\":5},{\"y\":10,\"x\":4},{\"y\":9,\"x\":4},{\"y\":9,\"x\":3},{\"y\":8,\"x\":3},{\"y\":7,\"x\":3},{\"y\":6,\"x\":3},{\"y\":6,\"x\":2},{\"y\":6,\"x\":1},{\"y\":7,\"x\":1},{\"y\":7,\"x\":2},{\"y\":8,\"x\":2},{\"y\":8,\"x\":1},{\"y\":9,\"x\":1},{\"y\":9,\"x\":0},{\"y\":8,\"x\":0},{\"y\":7,\"x\":0},{\"y\":6,\"x\":0},{\"y\":5,\"x\":0},{\"y\":5,\"x\":1},{\"y\":5,\"x\":2},{\"y\":5,\"x\":3},{\"y\":5,\"x\":4},{\"y\":5,\"x\":5}],\"id\":\"cbaeeb77-b551-4fab-99c2-eb383d2f7bec\",\"health\":79,\"length\":36,\"name\":\"Beta\",\"head\":{\"y\":5,\"x\":6}},{\"latency\":\"213.889\",\"shout\":\"\",\"body\":[{\"y\":4,\"x\":5},{\"y\":4,\"x\":6},{\"y\":4,\"x\":7},{\"y\":4,\"x\":8},{\"y\":4,\"x\":9},{\"y\":4,\"x\":10},{\"y\":3,\"x\":10},{\"y\":2,\"x\":10},{\"y\":2,\"x\":9},{\"y\":2,\"x\":8},{\"y\":1,\"x\":8},{\"y\":1,\"x\":7},{\"y\":2,\"x\":7},{\"y\":3,\"x\":7},{\"y\":3,\"x\":6},{\"y\":2,\"x\":6},{\"y\":1,\"x\":6},{\"y\":1,\"x\":5},{\"y\":1,\"x\":4},{\"y\":2,\"x\":4},{\"y\":2,\"x\":3},{\"y\":1,\"x\":3},{\"y\":1,\"x\":2},{\"y\":2,\"x\":2},{\"y\":3,\"x\":2},{\"y\":3,\"x\":1},{\"y\":2,\"x\":1},{\"y\":1,\"x\":1},{\"y\":1,\"x\":0},{\"y\":2,\"x\":0},{\"y\":3,\"x\":0},{\"y\":4,\"x\":0},{\"y\":4,\"x\":1},{\"y\":4,\"x\":2}],\"id\":\"cd7edb09-0b06-41ad-8273-647135b2259f\",\"health\":54,\"length\":34,\"name\":\"Gamma\",\"head\":{\"y\":4,\"x\":5}}],\"width\":11,\"hazards\":[],\"height\":11,\"food\":[{\"y\":0,\"x\":9},{\"y\":0,\"x\":0},{\"y\":0,\"x\":8},{\"y\":9,\"x\":6},{\"y\":3,\"x\":5},{\"y\":7,\"x\":4},{\"y\":0,\"x\":10},{\"y\":6,\"x\":9},{\"y\":7,\"x\":9},{\"y\":5,\"x\":10},{\"y\":1,\"x\":10},{\"y\":3,\"x\":9}]},\"game\":{\"ruleset\":{\"name\":\"standard\",\"version\":\"Mojave/3.1\"},\"timeout\":500,\"id\":\"25d8fb1e-41d6-42c6-9bbd-3152a9408b12\"}} ";
		;
		String test = "{\"game\":{\"id\":\"2d3598c8-e029-4898-9ae9-624e62c5f7f7\",\"timeout\":500,\"ruleset\":{\"name\":\"royale\",\"version\":\"v1\"}},\"turn\":211,\"board\":{\"height\":11,\"width\":11,\"food\":[{\"x\":8,\"y\":1},{\"x\":0,\"y\":1},{\"x\":1,\"y\":2},{\"x\":6,\"y\":2}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":1},{\"x\":1,\"y\":2},{\"x\":1,\"y\":3},{\"x\":1,\"y\":4},{\"x\":1,\"y\":5},{\"x\":1,\"y\":6},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":1},{\"x\":2,\"y\":2},{\"x\":2,\"y\":3},{\"x\":2,\"y\":4},{\"x\":2,\"y\":5},{\"x\":2,\"y\":6},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":1},{\"x\":3,\"y\":2},{\"x\":3,\"y\":3},{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":3,\"y\":6},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":1},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":4,\"y\":5},{\"x\":4,\"y\":6},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":1},{\"x\":5,\"y\":2},{\"x\":5,\"y\":3},{\"x\":5,\"y\":4},{\"x\":5,\"y\":5},{\"x\":5,\"y\":6},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":6,\"y\":3},{\"x\":6,\"y\":4},{\"x\":6,\"y\":5},{\"x\":6,\"y\":6},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":1},{\"x\":7,\"y\":2},{\"x\":7,\"y\":3},{\"x\":7,\"y\":4},{\"x\":7,\"y\":5},{\"x\":7,\"y\":6},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":1},{\"x\":8,\"y\":2},{\"x\":8,\"y\":3},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}],\"snakes\":[{\"id\":\"e9cc3085-1f37-4335-b76d-a958bbb3a554\",\"name\":\"Beta\",\"health\":65,\"body\":[{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":10},{\"x\":4,\"y\":10},{\"x\":5,\"y\":10},{\"x\":5,\"y\":9},{\"x\":4,\"y\":9},{\"x\":4,\"y\":8},{\"x\":4,\"y\":7},{\"x\":4,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":2,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":4},{\"x\":2,\"y\":4}],\"latency\":0,\"head\":{\"x\":2,\"y\":8},\"length\":17,\"shout\":\"\",\"squad\":\"\"},{\"id\":\"af0b6e80-51c4-49ec-bb76-a1c6421bfa85\",\"name\":\"Old\",\"health\":32,\"body\":[{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":8,\"y\":10},{\"x\":7,\"y\":10},{\"x\":6,\"y\":10},{\"x\":6,\"y\":9},{\"x\":7,\"y\":9},{\"x\":8,\"y\":9},{\"x\":8,\"y\":8}],\"latency\":0,\"head\":{\"x\":9,\"y\":7},\"length\":11,\"shout\":\"\",\"squad\":\"\"}]},\"you\":{\"id\":\"e9cc3085-1f37-4335-b76d-a958bbb3a554\",\"name\":\"Beta\",\"health\":65,\"body\":[{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":10},{\"x\":4,\"y\":10},{\"x\":5,\"y\":10},{\"x\":5,\"y\":9},{\"x\":4,\"y\":9},{\"x\":4,\"y\":8},{\"x\":4,\"y\":7},{\"x\":4,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":2,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":4},{\"x\":2,\"y\":4}],\"latency\":0,\"head\":{\"x\":2,\"y\":8},\"length\":17,\"shout\":\"\",\"squad\":\"\"}} ";

		try {
			JsonNode parsedRequest = json.readTree(pretest);
			int size = parsedRequest.get(BOARD).get("height").asInt();
			BetaSnake t = new BetaSnake("test");

			t.heigth = size;
			t.width = size;
			t.timeout = 2500;
			t.multiThread = true;

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			System.out.println(t.move(parsedRequest));
			parsedRequest = json.readTree(test);
			//System.out.println(t.move(parsedRequest));
			try {
				Thread.sleep(510);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}*/

}
