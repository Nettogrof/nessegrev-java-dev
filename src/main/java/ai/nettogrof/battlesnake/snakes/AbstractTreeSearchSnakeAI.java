package ai.nettogrof.battlesnake.snakes;

import com.fasterxml.jackson.databind.JsonNode;
import gnu.trove.list.array.TFloatArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstant;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * Any snake using a tree-search could extend this abstract class it contains
 * basic constant fields, related to the field name in json call from
 * BattleSnake. Also some method that any snake must implements.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractTreeSearchSnakeAI extends AbstractSnakeAI {

	/**
	 * Int value use to check how much time does the snake have do to tree-search,
	 * value define by json field
	 */
	public int timeout = 300;

	/**
	 * Boolean if multithread is use by the snake value define by the config file
	 */
	protected transient boolean multiThread;
	
	/**
	 * Number of cpu / thread permit
	 */
	protected transient int cpu_limit = 2;


	/**
	 * String that gonna be shout by the snake if snake is in a losing position.
	 */
	protected transient String losing = "I have a bad feeling about this";

	/**
	 * String that gonna be shout by the snake if snake is in a winning position.
	 */
	protected transient String winning = "I'm your father";

	/**
	 * Int value that will be subtract from timeout, it's a buffer define in the
	 * config file, to take latency/lag into account
	 */
	protected transient int minusbuffer = 250;

	/**
	 * Number of node analyze during the whole game
	 */
	protected transient long nodeTotalCount;

	/**
	 * Total of time used to compute during the whole game
	 */
	protected transient long timeTotal;

	/**
	 * Keep the root node from the previous move, to be able to continue search from
	 * the previous "tree"
	 */
	protected transient AbstractNode lastRoot;

	/**
	 * Basic and unsed constructor
	 */
	public AbstractTreeSearchSnakeAI() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public AbstractTreeSearchSnakeAI(final String gameId) {
		super(gameId);
		setFileConfig();
		try (InputStream input = Files.newInputStream(Paths.get(getFileConfig()))) {

			final Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			apiversion = Integer.parseInt(prop.getProperty("apiversion"));
			minusbuffer = Integer.parseInt(prop.getProperty("minusbuffer"));
			multiThread = Boolean.parseBoolean(prop.getProperty("multiThread"));

			final Random rand = new Random();
			losing = BattleSnakeConstant.LOSE_SHOUT[rand.nextInt(BattleSnakeConstant.LOSE_SHOUT.length)];
			winning = BattleSnakeConstant.WIN_SHOUT[rand.nextInt(BattleSnakeConstant.WIN_SHOUT.length)];

		} catch (IOException ex) {
			log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());
		}
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
	 * This method will be call at the end of the game, can be override if you want
	 * to clean-up some game info. And log some info about computing stats.
	 * 
	 * @param endRequest Json call received
	 * @return map that can be empty because it will be ignore by BattleSnake server
	 */
	@Override
	public Map<String, String> end(final JsonNode endRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();

		if (endRequest.get(BOARD).get(SNAKES).size() > 0) {
			log.atInfo().log("Winner is : %s", endRequest.get(BOARD).get(SNAKES).get(0).get(NAME).asText());

		} else {
			log.atInfo().log("DRAW");

		}

		log.atInfo().log("Average node/s : " + (nodeTotalCount / timeTotal * 1000));

		return response;
	}

	/**
	 * In a winning position, this method try to find the shortest way to win.
	 * 
	 * @param root   The current Root node
	 * @param winner The predetermine best move
	 * @return the best AbstractNode
	 */
	protected AbstractNode finishHim(final AbstractNode root, final AbstractNode winner) {

		AbstractNode ret = null;

		final ConcurrentHashMap<Integer, Float> scoreCount = new ConcurrentHashMap<>();

		for (final AbstractNode c : root.getChild()) {
			if (scoreCount.get(c.getSnakes().get(0).getHead()) == null) {
				scoreCount.put(c.getSnakes().get(0).getHead(), c.getScoreRatio());
			} else if (scoreCount.get(c.getSnakes().get(0).getHead()) > c.getScoreRatio()) {
				scoreCount.put(c.getSnakes().get(0).getHead(), c.getScoreRatio());
			}

		}

		int numberChild = Integer.MAX_VALUE;
		for (final AbstractNode c : root.getChild()) {

			if (c.getChildCount() < numberChild && scoreCount.get(c.getSnakes().get(0).getHead()) > 100) {
				ret = c;
				numberChild = c.getChildCount();
			}
		}
		if (ret == null) {
			ret = winner;
		}
		return ret;
	}

	/**
	 * In a losing position, this method try to find the longest way, hpoing that
	 * the opponent make a mistake.
	 * 
	 * @param root The current Root node
	 * @return the best AbstractNode
	 */
	protected AbstractNode lastChance(final AbstractNode root) {

		AbstractNode ret = null;
		float score = 0;
		for (final AbstractNode c : root.getChild()) {

			if (c.getScoreRatio() > score) {
				score = c.getScoreRatio();
				ret = c;
			}
		}

		if (ret == null) {
			int numberChild = 0;
			for (final AbstractNode c : root.getChild()) {

				if (c.getChildCount() > numberChild) {
					numberChild = c.getChildCount();
					ret = c;
				}
			}
		}

		if (ret == null) {
			return root.getChild().get(0);
		}

		return ret;
	}

	/**
	 * Choose the best move, based on the assumption that the opponent will always
	 * choose the best counter.
	 * 
	 * @param root The current Root node
	 * @return the best AbstractNode
	 */
	protected AbstractNode chooseBestMove(final AbstractNode root) {

		final List<AbstractNode> children = root.getChild();

		final TFloatArrayList upward = new TFloatArrayList();
		final TFloatArrayList down = new TFloatArrayList();
		final TFloatArrayList left = new TFloatArrayList();
		final TFloatArrayList right = new TFloatArrayList();
		fillList(upward, down, left, right, root);
		logValue(upward, down, left, right);
		Float choiceValue = new Float(-1);
		Integer move = 0;
		final int head = root.getSnakes().get(0).getHead();
		if (!upward.isEmpty() && upward.min() > choiceValue) {
			choiceValue = upward.min();
			move = head + 1;

		}
		if (!down.isEmpty() && down.min() > choiceValue) {
			choiceValue = down.min();
			move = head - 1;

		}
		if (!left.isEmpty() && left.min() > choiceValue) {
			choiceValue = left.min();
			move = head - 1000;

		}
		if (!right.isEmpty() && right.min() > choiceValue) {
			choiceValue = right.min();
			move = head + 1000;

		}
		

		for (final AbstractNode child : children) {
			if (child.getScoreRatio() == choiceValue && child.getSnakes().get(0).isAlive()
					&& move == child.getSnakes().get(0).getHead()) {
				return child;

			}

		}

		return null;
	}

	
	/**
	 * This method return the scoreRatio of the best choice based on payoff Matrix
	 * 
	 * @param upward float array list
	 * @param down   float array list
	 * @param left   float array list
	 * @param right  float array list
	 * @return  score float
	 */
	protected float getbestChildValue(final TFloatArrayList upward, final TFloatArrayList down,
			final TFloatArrayList left, final TFloatArrayList right) {
		float temp;
		float choiceValue = Float.MIN_VALUE;
		if (!upward.isEmpty()) {
			choiceValue = upward.min();

		}

		if (!down.isEmpty()) {
			temp = down.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}

		if (!left.isEmpty()) {
			temp = left.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}

		if (!right.isEmpty()) {
			temp = right.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}
		return choiceValue;
	}

	/**
	 * This method fill 4 list (one for each direction ) with the score of each node
	 * based on the move direction
	 * 
	 * @param upward float array list
	 * @param down   float array list
	 * @param left   float array list
	 * @param right  float array list
	 * @param node   parent node
	 */
	private void fillList(final TFloatArrayList upward, final TFloatArrayList down, final TFloatArrayList left,
			final TFloatArrayList right, final AbstractNode node) {
		final int head = node.getSnakes().get(0).getHead();

		for (int i = 0; i < node.getChild().size(); i++) {
			if (node.getChild().get(i).exp) {
				final int move = node.getChild().get(i).getSnakes().get(0).getHead();

				if (move / 1000 < head / 1000) {
					left.add(node.getChild().get(i).getScoreRatio());
				} else if (move / 1000 > head / 1000) {
					right.add(node.getChild().get(i).getScoreRatio());
				} else if (move % 1000 < head % 1000) {
					down.add(node.getChild().get(i).getScoreRatio());
				} else {
					upward.add(node.getChild().get(i).getScoreRatio());
				}
			}
		}
		
	}
	
	/**
	 * Log value for each possible move, that help to debug/understand why the snake choose which move.
	 * @param upward float array list
	 * @param down   float array list
	 * @param left   float array list
	 * @param right  float array list
	 */
	private void logValue(final TFloatArrayList upward, final TFloatArrayList down, final TFloatArrayList left,
			final TFloatArrayList right) {
		final StringBuilder logtext = new StringBuilder();
		if (!upward.isEmpty()) {
			logtext.append("\nup ").append(upward.min());
		}
		if (!down.isEmpty()) {
			logtext.append("\ndown ").append(down.min());
		}
		if (!left.isEmpty()) {
			logtext.append("\nleft ").append(left.min());
		}
		if (!right.isEmpty()) {
			logtext.append("\nright ").append(right.min());
		}
		log.atInfo().log(logtext.toString());
	}

	/**
	 * Generate the response to send, adding a shout if in winning/losing position.
	 * 
	 * @param winner the best node
	 * @param root   the current root node
	 * @param head   current snake head from the json
	 * @return  response for Battlesnake
	 */
	protected Map<String, String> generateResponse(final AbstractNode winner, final AbstractNode root,
			final JsonNode head) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		String res;
		if (winner == null) {
			response.put("shout", losing);
			res = DOWN;
		} else {
			AbstractNode choosenNode = winner;
			if (winner.getScoreRatio() < 0.001) {
				response.put("shout", losing);
				choosenNode = lastChance(root);
			} else if (winner.getScoreRatio() > 100) {
				response.put("shout", winning);
				choosenNode = finishHim(root, winner);
			}

			final int move = choosenNode.getSnakes().get(0).getHead();

			final int snakex = head.get("x").asInt();

			if (move / 1000 < snakex) {
				res = LEFT;
			} else if (move / 1000 > snakex) {
				res = RIGHT;
			} else if (move % 1000 < head.get("y").asInt()) {
				res = apiversion == 1 ? DOWN : UPWARD;
			} else {
				res = apiversion == 1 ? UPWARD : DOWN;
			}

		}
		response.put(MOVESTR, res);
		return response;
	}

}
