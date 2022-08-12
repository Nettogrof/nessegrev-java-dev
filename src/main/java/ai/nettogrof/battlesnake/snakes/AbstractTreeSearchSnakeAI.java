package ai.nettogrof.battlesnake.snakes;

import com.fasterxml.jackson.databind.JsonNode;
import gnu.trove.list.array.TFloatArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.info.hazard.HazardSquare;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants;
import ai.nettogrof.battlesnake.snakes.common.SnakeGeneticConstants;
import ai.nettogrof.battlesnake.treesearch.AbstractSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.constrictor.ConstrictorSearch;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleSearch;
import ai.nettogrof.battlesnake.treesearch.search.royale.wrapped.WrappedRoyaleSearch;
import ai.nettogrof.battlesnake.treesearch.search.squad.SquadSearch;
import ai.nettogrof.battlesnake.treesearch.search.standard.MctsSearch;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.BASIC_SCORE;
import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.API_V1;

/**
 * Any snake using a tree-search could extend this abstract class it contains
 * basic constant fields, related to the field name in json call from
 * BattleSnake. Also some method that any snake must implements.
 * 
 * @author carl.lajeunesse
 * @version Spring 2022
 */
public abstract class AbstractTreeSearchSnakeAI extends AbstractSnakeAI {

	/**
	 * Int value use to check how much time does the snake have do to tree-search,
	 * value define by json field
	 */
	protected transient int timeout = 300;

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
	 * Ruleset that this game is played.
	 */
	protected transient String ruleset = "standard";

	/**
	 * What kind of search that gonna be use
	 */
	protected transient Constructor<? extends AbstractSearch> searchType;

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
	}

	/**
	 * Set the properties to the snake object
	 */
	protected void setProperties() {
		try (InputStream input = Files.newInputStream(Paths.get(fileConfig))) {

			final Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			apiversion = Integer.parseInt(prop.getProperty("apiversion"));
			minusbuffer = Integer.parseInt(prop.getProperty("minusbuffer"));

			final Random rand = new Random();
			losing = BattleSnakeConstants.LOSE_SHOUT[rand.nextInt(BattleSnakeConstants.LOSE_SHOUT.length)];
			winning = BattleSnakeConstants.WIN_SHOUT[rand.nextInt(BattleSnakeConstants.WIN_SHOUT.length)];

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
			apiversion = API_V1;
		}

		final Long startTime = System.currentTimeMillis();

		final AbstractNode root = genRoot(moveRequest);
		root.exp = true;
		final GameRuleset rules = new GameRuleset(moveRequest.get("game").get("ruleset"));
		try {
			treeSearch(root, startTime, rules);
		} catch (ReflectiveOperationException e) {
			log.atWarning().log(e.getMessage() + "\n" + e.getStackTrace());
		}

		AbstractNode winner = chooseBestMove(root);

		if (winner == null && !root.getChild().isEmpty()) {
			winner = root.getChild().get(0);
		}

		lastRoot = root;

		log.atInfo().log("Turn:" + moveRequest.get(TURN).asInt() + " nb nodes" + root.getChildCount() + "  time: "
				+ (System.currentTimeMillis() - startTime) + "  Max Depth" + getMaxDepth(root));
		nodeTotalCount += root.getChildCount();
		timeTotal += System.currentTimeMillis() - startTime;
		return generateResponse(winner, root, moveRequest.get(YOU).withArray(BODY).get(0));
	}

	private int getMaxDepth(final AbstractNode root) {
		AbstractNode node = root;
		int depth = 0;

		while (node.getChildCount() > 1) {
			depth++;

			final List<AbstractNode> childs = node.getChild();
			int childCount = 0;
			for (final AbstractNode child : childs) {
				if (child.getChildCount() > childCount) {
					node = child;
					childCount = child.getChildCount();
				}
			}
		}

		return depth;
	}

	/**
	 * Execute the tree search
	 * 
	 * @param root      The root node
	 * @param startTime The start time in millisecond
	 * @throws ReflectiveOperationException In case of invalid search type
	 */
	protected void treeSearch(final AbstractNode root, final Long startTime, final GameRuleset rules)
			throws ReflectiveOperationException {

		final AbstractSearch main = searchType.newInstance(root, width, height, startTime, timeout - minusbuffer,
				rules);

		if (main == null) {
			log.atSevere().log("Unable to find Search Type ");
		} else {
			main.run();
		}

	}

	/**
	 * Generate the root node based on the /move request
	 * 
	 * @param moveRequest Json request
	 * @return AbstractNode the root
	 */
	protected abstract AbstractNode genRoot(JsonNode moveRequest);

	/**
	 * This method will be call at the end of the game, can be override if you want
	 * to clean-up some game info. And log some info about computing stats.
	 * 
	 * @param endRequest Json call received
	 * @return map that can be empty because it will be ignore by BattleSnake server
	 */
	@Override
	public Map<String, String> end(final JsonNode endRequest) {
		log.atInfo().log("Average node/s : " + (nodeTotalCount / timeTotal * 1000));
		return super.end(endRequest);
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
		// TODO In losing posisition snake should choose the path with a the best
		// chance, not the longest child
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
		TFloatArrayList[] upward = new TFloatArrayList[2];
		final TFloatArrayList[] down = new TFloatArrayList[2];
		final TFloatArrayList[] left = new TFloatArrayList[2];
		final TFloatArrayList[] right = new TFloatArrayList[2];
		upward[0] = new TFloatArrayList();
		upward[1] = new TFloatArrayList();
		down[0] = new TFloatArrayList();
		down[1] = new TFloatArrayList();
		right[0] = new TFloatArrayList();
		right[1] = new TFloatArrayList();
		left[0] = new TFloatArrayList();
		left[1] = new TFloatArrayList();
		fillList(upward, down, left, right, root);
		logValue(upward, down, left, right);
		float choiceValue = -1f;
		int move = 0;
		final int head = root.getSnakes().get(0).getHead();
		if (!upward[0].isEmpty() && upward[0].min() > choiceValue) {
			choiceValue = upward[0].min();
			move = head % 1000 == height - 1 ? head / 1000 * 1000 : head + 1;

		}
		if (!down[0].isEmpty() && down[0].min() > choiceValue) {
			choiceValue = down[0].min();
			move = head % 1000 == 0 ? head + height - 1 : head - 1;

		}
		if (!left[0].isEmpty() && left[0].min() > choiceValue) {
			choiceValue = left[0].min();
			move = head / 1000 == 0 ? head + (width - 1) * 1000 : head - 1000;

		}
		if (!right[0].isEmpty() && right[0].min() > choiceValue) {
			choiceValue = right[0].min();
			move = head / 1000 == width - 1 ? head - (width - 1) * 1000 : head + 1000;

		}

		for (final AbstractNode child : root.getChild()) {
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
	 * @return score float
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
	private void fillList(final TFloatArrayList upward[], final TFloatArrayList down[], final TFloatArrayList left[],
			final TFloatArrayList right[], final AbstractNode node) {
		final int head = node.getSnakes().get(0).getHead();

		for (int i = 0; i < node.getChild().size(); i++) {
			final int move = node.getChild().get(i).getSnakes().get(0).getHead();

			if ((move / 1000) + 1 == head / 1000 || move / 1000 != 0 && head / 1000 == 0) {
				left[0].add(node.getChild().get(i).getScoreRatio());
				left[1].add(node.getChild().get(i).getChildCount());
			} else if ((move / 1000) - 1 == head / 1000 || move / 1000 == 0 && head / 1000 != 0) {
				right[0].add(node.getChild().get(i).getScoreRatio());
				right[1].add(node.getChild().get(i).getChildCount());
			} else if (move % 1000 + 1 == head % 1000 || move % 1000 != 0 && head % 1000 == 0) {
				down[0].add(node.getChild().get(i).getScoreRatio());
				down[1].add(node.getChild().get(i).getChildCount());
			} else if (move % 1000 - 1 == head % 1000 || move % 1000 == 0 && head % 1000 != 0) {
				upward[0].add(node.getChild().get(i).getScoreRatio());
				upward[1].add(node.getChild().get(i).getChildCount());
			} else {
				log.atWarning().log("Undefined Move");
				upward[0].add(node.getChild().get(i).getScoreRatio());

			}
		}

	}

	/**
	 * Log value for each possible move, that help to debug/understand why the snake
	 * choose which move.
	 * 
	 * @param upward float array list
	 * @param down   float array list
	 * @param left   float array list
	 * @param right  float array list
	 */
	private void logValue(final TFloatArrayList upward[], final TFloatArrayList down[], final TFloatArrayList left[],
			final TFloatArrayList right[]) {
		final StringBuilder logtext = new StringBuilder();
		if (!upward[0].isEmpty()) {
			logtext.append("\nup ").append(upward[0].min()).append(" node count").append(upward[1].sum());
		}
		if (!down[0].isEmpty()) {
			logtext.append("\ndown ").append(down[0].min()).append(" node count").append(down[1].sum());
		}
		if (!left[0].isEmpty()) {
			logtext.append("\nleft ").append(left[0].min()).append(" node count").append(left[1].sum());
		}
		if (!right[0].isEmpty()) {
			logtext.append("\nright ").append(right[0].min()).append(" node count").append(right[1].sum());
		}
		log.atInfo().log(logtext.toString());
	}

	/**
	 * Generate the response to send, adding a shout if in winning/losing position.
	 * 
	 * @param winner the best node
	 * @param root   the current root node
	 * @param head   current snake head from the json
	 * @return response for Battlesnake
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
			if (winner.getScoreRatio() < BASIC_SCORE) {
				response.put("shout", losing);
				choosenNode = lastChance(root);
			} else if (winner.getScoreRatio() > SnakeGeneticConstants.stopExpandLimit) {
				response.put("shout", winning);
				choosenNode = finishHim(root, winner);
			}

			final int move = choosenNode.getSnakes().get(0).getHead();

			final int snakex = head.get("x").asInt();
			final int snakey = head.get("y").asInt();

			if (move / 1000 == snakex - 1 || snakex == 0 && move / 1000 > 1) {
				res = LEFT;
			} else if (move / 1000 == snakex + 1 || snakex == width - 1 && move / 1000 == 0) {
				res = RIGHT;
			} else if (move % 1000 == snakey - 1 || snakey == 0 && move % 1000 > 1) {
				res = apiversion == 1 ? DOWN : UPWARD;
			} else {
				res = apiversion == 1 ? UPWARD : DOWN;
			}

		}
		response.put(MOVESTR, res);
		return response;
	}

	/**
	 * This method generate the search type
	 * 
	 * @return Abstract Search
	 * @throws ReflectiveOperationException In case of invalid search type
	 */
	protected Constructor<? extends AbstractSearch> genSearchType() throws ReflectiveOperationException {

		switch (ruleset) {
		case "constrictor":
			return ConstrictorSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class, GameRuleset.class);
		case "royale":
			return RoyaleSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class, int.class,
					GameRuleset.class);
		case "squad":
			return SquadSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class, int.class,
					GameRuleset.class);
		case "wrapped":
			return WrappedRoyaleSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class, GameRuleset.class);
		default:
			return MctsSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class, int.class,
					GameRuleset.class);
		}

	}

	/**
	 * Check in the previous search if a child from root, is equals the current
	 * board situation.,
	 * 
	 * @param snakes List of snakes
	 * @param food   Food info
	 * @param hazard Hazard Info
	 * @return null or child node
	 */
	protected AbstractNode findChildNewRoot(final List<SnakeInfo> snakes, final FoodInfo food,
			final HazardSquare hazard) {
		if (lastRoot != null) {

			for (final AbstractNode c : lastRoot.getChild()) {
				if ((hazard == null || hazard.equals(c.getHazard())) && food.equals(c.getFood())
						&& c.getSnakes().size() == snakes.size()) {
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
		return null;
	}

	/**
	 * Set the move timeout
	 * 
	 * @param timeout the timeout to set
	 */
	public void setTimeout(final int timeout) {
		this.timeout = timeout;
	}

}
