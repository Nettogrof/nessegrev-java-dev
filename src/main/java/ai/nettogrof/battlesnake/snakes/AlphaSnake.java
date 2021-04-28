package ai.nettogrof.battlesnake.snakes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.alpha.AlphaNode;
import ai.nettogrof.battlesnake.treesearch.alpha.AlphaSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * Alpha snake. This class is the "Nessegrev-Alpha" snake on Battlesnake.This
 * snake participated in the Communitech tournament in the Rookie division.
 * Still has some search bugs. It uses the minimax algorithm. This snake work
 * only API v1. All the move calculation are based on API v0, and then the snake
 * switch UP and DOWN response.
 * 
 * @author carl.lajeunesse
 * @version Fall 2020
 * @deprecated AlphaSnake won't be improve anymore,  so buggy.
 */
@Deprecated
public class AlphaSnake extends AbstractTreeSearchSnakeAI {

	/**
	 * The config filename
	 */
	private static String fileConfig = "Alpha.properties";

	/**
	 * Basic / unused constructor
	 */
	public AlphaSnake() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public AlphaSnake(final String gameId) {
		super(gameId, fileConfig);

	}

	/**
	 * This method will be call on each move request receive by BattleSnake
	 * 
	 * @param moveRequest Json call received
	 * @return map of field to be return to battlesnake, example "move" , "up"
	 */
	@Override
	public Map<String, String> move(final JsonNode moveRequest) {
		final Long startTime = System.currentTimeMillis();
		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		possiblemove.put(UPWARD, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);

		// String name = moveRequest.get(YOU).get(NAME).asText();

		// int turn = moveRequest.get("turn").asInt();

		final int snakex = moveRequest.get(YOU).withArray(BODY).get(0).get("x").asInt();
		final int snakey = moveRequest.get(YOU).withArray(BODY).get(0).get("y").asInt();

		final FoodInfo food = new FoodInfo(moveRequest.get(BOARD));
		SnakeInfo[] snakes = new SnakeInfo[moveRequest.get(BOARD).get(SNAKES).size()];

		final JsonNode yourSnake = moveRequest.get(YOU);
		snakes[0] = new SnakeInfo();
		snakes[0].setHealth((short) yourSnake.get("health").asInt());
		snakes[0].setName(yourSnake.get(NAME).asText());
		snakes[0].setSnake(yourSnake);

		for (int i = 0, j = 1; i < snakes.length; i++, j++) {
			final JsonNode snake = moveRequest.get(BOARD).get(SNAKES).get(i);
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
				if (food.equals(child.getFood()) && Arrays.deepEquals(child.getSnakes().toArray(), snakes)) {

					root = (AlphaNode) child;
					break;

				}
			}
		}
		if (root == null) {
			root = new AlphaNode(snakes, food);
		}

		if (multiThread) {
			new AlphaSearch(root, width, height).generateChild();
			// MoveGenerator.generateChild(root,width,heigth);
			final ArrayList<AlphaSearch> listThread = new ArrayList<>();
			for (final AbstractNode childNode : root.getChild()) {
				listThread.add(new AlphaSearch((AlphaNode) childNode, width, height, startTime, timeout - minusbuffer));

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
			final AlphaSearch main = new AlphaSearch(root, width, height, startTime, timeout - minusbuffer);
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
		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("color", "#212121");
		response.put("headType", "shac-gamer");
		response.put("tailType", "shac-coffee");
		width = startRequest.get(BOARD).get(WIDTH_FIELD).asInt();
		height = startRequest.get(BOARD).get(HEIGHT_FIELD).asInt();

		timeout = startRequest.get("game").get("timeout").asInt();

		// timeout = timeout-200;

		return response;
	}

	/**
	 * Method use to set the fileConfig string
	 */
	@Override
	protected void setFileConfig() {
		fileConfig = "Alpha.properties";

	}

	@Override
	protected AbstractNode genRoot(JsonNode moveRequest) {
		return null;
	}

}
