package ai.nettogrof.battlesnake.snakes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;

import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.AlphaSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.node.AlphaNode;
import gnu.trove.list.array.TFloatArrayList;

public class AlphaSnake extends AbstractTreeSearchSnakeAI {


	private transient AlphaNode lastRoot;
	protected static String fileConfig = "Alpha.properties";

	public AlphaSnake() {
		super();
	}

	public AlphaSnake(final String gameId) {
		super(gameId);
		
	}

	@Override
	public Map<String, String> move(final JsonNode moveRequest) {
		final Long startTime = System.currentTimeMillis();
		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		possiblemove.put(UPWARD, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);

		// String name = moveRequest.get("you").get(NAME).asText();

		// int turn = moveRequest.get("turn").asInt();

		final int snakex = moveRequest.get("you").withArray(BODY).get(0).get("x").asInt();
		final int snakey = moveRequest.get("you").withArray(BODY).get(0).get("y").asInt();

		final FoodInfo food = new FoodInfo(moveRequest.get(BOARD));
		SnakeInfo[] snakes = new SnakeInfo[moveRequest.get(BOARD).get("snakes").size()];

		final JsonNode yourSnake = moveRequest.get("you");
		snakes[0] = new SnakeInfo();
		snakes[0].setHealth((short) yourSnake.get("health").asInt());
		snakes[0].setName(yourSnake.get(NAME).asText());
		snakes[0].setSnake(yourSnake);

		for (int i = 0, j = 1; i < snakes.length; i++, j++) {
			final JsonNode snake = moveRequest.get(BOARD).get("snakes").get(i);
			if (snake.get(NAME).asText().equals(yourSnake.get(NAME).asText())) {
				j--;
			} else {
				snakes[j] = new SnakeInfo();
				snakes[j].setHealth((short) snake.get("health").asInt());
				snakes[j].setName(snake.get(NAME).asText());
				snakes[j].setSnake(snake);
			}
		}

		AlphaNode root = null;
		if (lastRoot != null) {

			for (final AbstractNode child : lastRoot.getChild()) {
				if (food.equals(child.getFood()) && Arrays.deepEquals(child.getSnakes().toArray(), snakes) ) {
				
						root = (AlphaNode) child;
						lastRoot = null;
						break;

					
				}
			}
		}
		if (root == null) {
			root = new AlphaNode(snakes, food);
		}

		
		if (multiThread) {
			new AlphaSearch(root, width, heigth).generateChild();
			// MoveGenerator.generateChild(root,width,heigth);
			final ArrayList<AlphaSearch> listThread = new ArrayList<>();
			for (final AbstractNode childNode : root.getChild()) {
				listThread.add(new AlphaSearch((AlphaNode) childNode, width, heigth, startTime, timeout - minusbuffer));

			}

			for (final AlphaSearch search : listThread) {
				final Thread searchThread = new Thread(search);
				searchThread.setPriority(1);
				searchThread.start();
				
			}

			try {

				Thread.sleep(timeout - minusbuffer - 50);
			} catch (InterruptedException e) {

				log.atWarning().log(e.getMessage() + "\n" + e.getStackTrace());
			}

			for (final AlphaSearch search : listThread) {
				search.stopSearching();
				
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {

				log.atWarning().log(e.getMessage() + "\n" + e.getStackTrace());
			}
			root.updateScore();
		} else {
			final AlphaSearch main = new AlphaSearch(root, width, heigth, startTime, timeout - minusbuffer);
			main.run();
		}
		AlphaNode winner = (AlphaNode) chooseBestMove(root);

		String res = "";
		if (winner == null && !root.getChild().isEmpty()) {
			
			winner = (AlphaNode) root.getChild().get(0);
			
		}

		if (winner == null) {
			response.put("shout", losing);
			res = DOWN;
			

		} else {
			if (winner.getScoreRatio() == 0) {
				response.put("shout", losing);
				winner = (AlphaNode) lastChance(root);
			}

			if (winner.getScoreRatio() > 100) {
				response.put("shout", winning);
				winner = (AlphaNode) finishHim(root, winner);
			}

			final int move = winner.getSnakes().get(0).getHead();
			if (move / 1000 < snakex) {
				res = LEFT;
			} else if (move / 1000 > snakex) {
				res = RIGHT;
			} else if (move % 1000 < snakey) {
				res = UPWARD;
			} else if (move % 1000 > snakey) {
				res = DOWN;
			}

			/* for api 1 */
			if (UPWARD.equals(res)) {
				res = DOWN;
			} else if (DOWN.equals(res)) {
				res = UPWARD;
			}
		}
		response.put(MOVESTR, res);
		lastRoot = root;

		log.atInfo().log("nb nodes" + root.getChildCount() + "  time: " + (System.currentTimeMillis() - startTime));
		nodeTotalCount += root.getChildCount();
		timeTotal += System.currentTimeMillis() - startTime;
		return response;
	}

	private AlphaNode finishHim(final AlphaNode root, final AlphaNode winner) {
		AlphaNode ret = null;
		int nbnumberChild = Integer.MAX_VALUE;
		for (final AbstractNode childNode : root.getChild()) {
			if (childNode.getChildCount() < nbnumberChild && childNode.getScoreRatio() > 100) {
				ret = (AlphaNode) childNode;
				nbnumberChild = childNode.getChildCount();
			}
		}
		if (ret == null) {
			ret = winner;
		}
		return ret;
	}

	private AlphaNode lastChance(final AlphaNode root) {
		AlphaNode ret = null;
		int numberChild = 0;
		for (final AbstractNode c : root.getChild()) {
			if (c.getChildCount() > numberChild) {
				ret = (AlphaNode) c;
				numberChild = c.getChildCount();
			}
		}

		return ret;
	}

	@Override
	public Map<String, String> start(final JsonNode startRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("color", "#212121");
		response.put("headType", "shac-gamer");
		response.put("tailType", "shac-coffee");
		width = startRequest.get(BOARD).get("width").asInt();
		heigth = startRequest.get(BOARD).get("height").asInt();
		// nbSnake = startRequest.get(BOARD).get("snakes").size();
		try {
			timeout = startRequest.get("game").get("timeout").asInt();
		} catch (Exception e) {
			timeout = 500;
		}

		// timeout = timeout-200;

		return response;
	}

	private AlphaNode chooseBestMove(final AlphaNode root) {
		// double score =-200;
		final ArrayList<AbstractNode> child = (ArrayList<AbstractNode>) root.getChild();
		AlphaNode winner = null;
		final TFloatArrayList upward = new TFloatArrayList();
		final TFloatArrayList down = new TFloatArrayList();
		final TFloatArrayList left = new TFloatArrayList();
		final TFloatArrayList right = new TFloatArrayList();
		// ArrayList<Double> choice = new ArrayList<Double>();
		final int head = root.getSnakes().get(0).getHead();

		for(final AbstractNode childNode : child){
			// if (child.get(i).getSnakes()[0].alive) {
			final int move = childNode.getSnakes().get(0).getHead();

			if (move / 1000 < head / 1000) {
				left.add(childNode.getScoreRatio());
			}

			if (move / 1000 > head / 1000) {
				right.add(childNode.getScoreRatio());
			}
			if (move % 1000 < head % 1000) {
				upward.add(childNode.getScoreRatio());
			}
			if (move % 1000 > head % 1000) {
				down.add(childNode.getScoreRatio());
			}
			// }
		}
		final float wup = upward.min();
		final float wdown = down.min();
		final float wleft = left.min();
		final float wright = right.min();

		double choiceValue = Double.MIN_VALUE;
		if (wup != Double.MAX_VALUE) {
			log.atInfo().log(UPWARD + wup);
			if (wup > choiceValue) {
				choiceValue = wup;
			}
		}
		if (wdown != Double.MAX_VALUE) {
			log.atInfo().log(DOWN + wdown);
			if (wdown > choiceValue) {
				choiceValue = wdown;
			}
		}
		if (wleft != Double.MAX_VALUE) {
			log.atInfo().log(LEFT + wleft);
			if (wleft > choiceValue) {
				choiceValue = wleft;
			}
		}
		if (wright != Double.MAX_VALUE) {
			log.atInfo().log(RIGHT + wright);
			if (wright > choiceValue) {
				choiceValue = wright;
			}
		}

		for (int i = 0; i < child.size(); i++) {
			final double childRatio = child.get(i).getScoreRatio();
			if (childRatio == choiceValue && child.get(i).getSnakes().get(0).isAlive()) {
				winner = (AlphaNode) child.get(i);
				i = child.size();
			}

		}

		return winner;
	}

	@Override
	protected void setFileConfig() {
		fileConfig = "Alpha.properties";

	}

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

	@Override
	protected String getFileConfig() {
		return fileConfig;
	}

}
