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
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.AbstractSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.DuelNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.FourNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.ManyNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.MctsSearch;
import ai.nettogrof.battlesnake.treesearch.search.standard.RegularSearch;

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
	 * The config filename
	 */
	protected static String fileConfig = "Gamma.properties";

	/**
	 * Number of cpu / thread permit
	 */
	private transient int cpu_limit = 2;

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

		final long startTime = System.currentTimeMillis();
		final AbstractNode root = genRoot(moveRequest);
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
	private void treeSearch(final AbstractNode root, final long startTime) {
		if (multiThread) {
			final ArrayList<AbstractNode> nodelist = new ArrayList<>();
			final ArrayList<AbstractNode> expandedlist = new ArrayList<>();
			nodelist.add(root);
			while (expand(nodelist, expandedlist)) {
				//TODO  Code smell awful around here
			}

			final ArrayList<AbstractSearch> listSearchThread = new ArrayList<>();

			for (final AbstractNode c : nodelist) {
				listSearchThread
						.add(new MctsSearch(c, width, height, System.currentTimeMillis(), timeout - minusbuffer));

			}

			for (final AbstractSearch s : listSearchThread) {
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
			final MctsSearch main = new MctsSearch(root, width, height, startTime, timeout - minusbuffer);
			main.run();
		}

	}

	/**
	 * Expand the base list of node until reaching cpu limit
	 * @param nodelist List of node that gonna to "rooted" in multithread search
	 * @param expandedlist List of node to be updated after search
	 * @return boolean if the list have been expand
	 */
	private boolean expand(final List<AbstractNode> nodelist, final List<AbstractNode> expandedlist) {
		if (nodelist.isEmpty()) {
			return false;
		}
		new RegularSearch(nodelist.get(0), width, height).generateChild();
		if (nodelist.size() - 1 + nodelist.get(0).getChild().size() < cpu_limit) {
			final AbstractNode oldroot = nodelist.remove(0);
			expandedlist.add(oldroot);

			for (final AbstractNode c : oldroot.getChild()) {
				nodelist.add(c);

			}
			return true;
		}
		return false;

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

		final ArrayList<SnakeInfo> snakes = new ArrayList<>();
		final JsonNode gammaSnake = moveRequest.get(YOU);

		snakes.add(new SnakeInfo());
		snakes.get(0).setHealth((short) (gammaSnake.get(HEALTH).asInt()));
		snakes.get(0).setName(gammaSnake.get(NAME).asText());
		snakes.get(0).setSnake(gammaSnake);

		for (int i = 0; i < board.get(SNAKES).size(); i++) {
			final JsonNode currentSnake = board.get(SNAKES).get(i);
			if (!currentSnake.get("id").asText().equals(gammaSnake.get("id").asText())) {
				final SnakeInfo otherSnake = new SnakeInfo();
				otherSnake.setHealth((short) currentSnake.get(HEALTH).asInt());
				otherSnake.setName(currentSnake.get(NAME).asText());
				otherSnake.setSnake(currentSnake);

				snakes.add(otherSnake);
			}
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

		return genNode(snakes, food);

	}

	/**
	 * This method generate the root node type
	 * 
	 * @param snakes List of snakes
	 * @param food   Food Information
	 * @return Abstract node
	 */
	private AbstractNode genNode(final List<SnakeInfo> snakes, final FoodInfo food) {
		if (snakes.size() > 4) {
			ManyNode.width = width;
			ManyNode.height = height;
			return new ManyNode(snakes, food);
		} else if (snakes.size() > 2) {
			FourNode.width = width;
			FourNode.height = height;
			return new FourNode(snakes, food);
		}
		DuelNode.width = width;
		DuelNode.height = height;
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

	
	/*
	 * public static void main(String args[]) { final TFloatArrayList right = new
	 * TFloatArrayList(); System.out.println(right.min());
	 * 
	 * /*ObjectMapper json = new ObjectMapper();
	 * 
	 * //String test =
	 * " {\"game\":{\"id\":\"35bfc780-a042-4f7d-a830-e3f387c3263e\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":132,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_XPcwfQmpdGt3jqbDjWcMCCg3\",\"name\":\"BlackHole\",\"latency\":\"410\",\"health\":77,\"body\":[{\"x\":9,\"y\":3},{\"x\":9,\"y\":2},{\"x\":9,\"y\":1},{\"x\":8,\"y\":1},{\"x\":8,\"y\":2},{\"x\":7,\"y\":2},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2},{\"x\":5,\"y\":3},{\"x\":6,\"y\":3},{\"x\":6,\"y\":4},{\"x\":6,\"y\":5}],\"head\":{\"x\":9,\"y\":3},\"length\":12,\"shout\":\"When life gives you melons, you're probably dyslexic.\"},{\"id\":\"gs_wRvwhDHSMVg7QHX9b6C46J69\",\"name\":\"Nessegrev-gamma\",\"latency\":\"341\",\"health\":93,\"body\":[{\"x\":10,\"y\":4},{\"x\":10,\"y\":3},{\"x\":10,\"y\":2},{\"x\":10,\"y\":1},{\"x\":10,\"y\":0},{\"x\":9,\"y\":0},{\"x\":8,\"y\":0},{\"x\":7,\"y\":0},{\"x\":6,\"y\":0},{\"x\":5,\"y\":0}],\"head\":{\"x\":10,\"y\":4},\"length\":10,\"shout\":\"\"}],\"food\":[{\"x\":4,\"y\":10},{\"x\":7,\"y\":9},{\"x\":4,\"y\":8},{\"x\":0,\"y\":10},{\"x\":0,\"y\":9},{\"x\":2,\"y\":8},{\"x\":10,\"y\":6},{\"x\":9,\"y\":4},{\"x\":9,\"y\":9},{\"x\":10,\"y\":5},{\"x\":8,\"y\":7}],\"hazards\":[]},\"you\":{\"id\":\"gs_wRvwhDHSMVg7QHX9b6C46J69\",\"name\":\"Nessegrev-gamma\",\"latency\":\"341\",\"health\":93,\"body\":[{\"x\":10,\"y\":4},{\"x\":10,\"y\":3},{\"x\":10,\"y\":2},{\"x\":10,\"y\":1},{\"x\":10,\"y\":0},{\"x\":9,\"y\":0},{\"x\":8,\"y\":0},{\"x\":7,\"y\":0},{\"x\":6,\"y\":0},{\"x\":5,\"y\":0}],\"head\":{\"x\":10,\"y\":4},\"length\":10,\"shout\":\"\"}}"
	 * ; String test =
	 * "{\"game\":{\"id\":\"1fb065e8-b77e-47db-9b69-c11f02a3206c\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":0,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_gvVx6kDbPT7xy3FDpqgGdrBH\",\"name\":\"Nessegrev-gamma\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":9,\"y\":5},{\"x\":9,\"y\":5},{\"x\":9,\"y\":5}],\"head\":{\"x\":9,\"y\":5},\"length\":3,\"shout\":\"\"},{\"id\":\"gs_fdpFF9kyG68rGjcQmjYtBrHS\",\"name\":\"Nessegrev-BadlyCoded\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":1,\"y\":9},{\"x\":1,\"y\":9},{\"x\":1,\"y\":9}],\"head\":{\"x\":1,\"y\":9},\"length\":3,\"shout\":\"\"}],\"food\":[{\"x\":8,\"y\":4},{\"x\":2,\"y\":8},{\"x\":5,\"y\":5}],\"hazards\":[]},\"you\":{\"id\":\"gs_gvVx6kDbPT7xy3FDpqgGdrBH\",\"name\":\"Nessegrev-gamma\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":9,\"y\":5},{\"x\":9,\"y\":5},{\"x\":9,\"y\":5}],\"head\":{\"x\":9,\"y\":5},\"length\":3,\"shout\":\"\"}}";
	 * String second=
	 * "{\"game\":{\"id\":\"1fb065e8-b77e-47db-9b69-c11f02a3206c\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":1,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_gvVx6kDbPT7xy3FDpqgGdrBH\",\"name\":\"Nessegrev-gamma\",\"latency\":\"354\",\"health\":99,\"body\":[{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":5}],\"head\":{\"x\":9,\"y\":4},\"length\":3,\"shout\":\"\"},{\"id\":\"gs_fdpFF9kyG68rGjcQmjYtBrHS\",\"name\":\"Nessegrev-BadlyCoded\",\"latency\":\"0\",\"health\":99,\"body\":[{\"x\":1,\"y\":10},{\"x\":1,\"y\":9},{\"x\":1,\"y\":9}],\"head\":{\"x\":1,\"y\":10},\"length\":3,\"shout\":\"\"}],\"food\":[{\"x\":8,\"y\":4},{\"x\":2,\"y\":8},{\"x\":5,\"y\":5}],\"hazards\":[]},\"you\":{\"id\":\"gs_gvVx6kDbPT7xy3FDpqgGdrBH\",\"name\":\"Nessegrev-gamma\",\"latency\":\"354\",\"health\":99,\"body\":[{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":5}],\"head\":{\"x\":9,\"y\":4},\"length\":3,\"shout\":\"\"}}";
	 * try { JsonNode parsedRequest = json.readTree(test); int size =
	 * parsedRequest.get("board").get("height").asInt(); GammaSnake t = new
	 * GammaSnake("test");
	 * 
	 * t.heigth = size; t.width = size; t.timeout =5500; t.multiThread = true;
	 * 
	 * 
	 * try { Thread.sleep(1); } catch (InterruptedException e) {
	 * 
	 * e.printStackTrace(); } System.out.println("Starting for real"); //for(int i =
	 * 0 ; i < 5 ; i++) { System.out.println(t.move(parsedRequest)); try {
	 * Thread.sleep(510); } catch (InterruptedException e) {
	 * 
	 * e.printStackTrace(); } System.out.println(t.move(json.readTree(second))); //}
	 * } catch (IOException e) {
	 * 
	 * e.printStackTrace(); } }
	 */

}
