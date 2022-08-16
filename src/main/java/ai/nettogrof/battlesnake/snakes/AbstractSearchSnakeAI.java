/**
 * 
 */
package ai.nettogrof.battlesnake.snakes;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.BASIC_SCORE;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants;
import ai.nettogrof.battlesnake.snakes.common.SnakeGeneticConstants;
import ai.nettogrof.battlesnake.treesearch.AbstractSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.constrictor.ConstrictorSearch;
import ai.nettogrof.battlesnake.treesearch.search.royale.RoyaleSearch;
import ai.nettogrof.battlesnake.treesearch.search.royale.wrapped.WrappedRoyaleSearch;
import ai.nettogrof.battlesnake.treesearch.search.squad.SquadSearch;
import ai.nettogrof.battlesnake.treesearch.search.standard.MctsSearch;

/**
 * Any snake using a search could extend this abstract class it contains basic
 * constant fields, related to the field name in json call from BattleSnake.
 * Also some method that any snake must implements.
 * 
 * @author carl.lajeunesse
 * @version Summer 2022
 */
public abstract class AbstractSearchSnakeAI extends AbstractSnakeAI {

	
	/**
	 * Int value use to check how much time does the snake have do to tree-search,
	 * value define by json field
	 */
	protected int timeout = 300;

	/**
	 * Int value that will be subtract from timeout, it's a buffer define in the
	 * config file, to take latency/lag into account
	 */
	protected int minusbuffer = 250;

	/**
	 * Number of node analyze during the whole game
	 */
	protected long nodeTotalCount;

	/**
	 * Total of time used to compute during the whole game
	 */
	protected long timeTotal;

	/**
	 * String that gonna be shout by the snake if snake is in a losing position.
	 */
	protected String losing = "I have a bad feeling about this";

	/**
	 * String that gonna be shout by the snake if snake is in a winning position.
	 */
	protected String winning = "I'm your father";

	/**
	 * Ruleset that this game is played.
	 */
	protected String ruleset = "standard";

	/**
	 * Basic and unsed constructor
	 */
	protected AbstractSearchSnakeAI() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	protected AbstractSearchSnakeAI(final String gameId) {
		super(gameId);		
	}

	/**
	 * This method generate the search type
	 * 
	 * @return Abstract Search
	 * @throws ReflectiveOperationException In case of invalid search type
	 */
	protected Constructor<? extends AbstractSearch> genSearchType() throws ReflectiveOperationException {
		Constructor<? extends AbstractSearch> searchtype;
		switch (ruleset) {
		case "constrictor":
			searchtype = ConstrictorSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class, GameRuleset.class);
			break;
		case "royale":
			searchtype = RoyaleSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class, GameRuleset.class);

			break;
		case "squad":
			searchtype = SquadSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class, GameRuleset.class);

			break;
		case "wrapped":
			searchtype = WrappedRoyaleSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class, GameRuleset.class);

			break;
		default:
			searchtype = MctsSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class, GameRuleset.class);
			break;
		}
		return searchtype;

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
			response.put(SHOUT, losing);
			res = DOWN;
		} else {
			final AbstractNode choosenNode = checkWinLost(winner, root, response);

			final int move = choosenNode.getSnakes().get(0).getHead();

			final int snakex = head.get("x").asInt();
			final int snakey = head.get("y").asInt();

			if (move / 1000 == snakex - 1 || snakex == 0 && move / 1000 > 1) {
				res = LEFT;
			} else if (move / 1000 == snakex + 1 || snakex == width - 1 && move / 1000 == 0) {
				res = RIGHT;
			} else if (move % 1000 == snakey - 1 || snakey == 0 && move % 1000 > 1) {
				res = DOWN;
			} else {
				res = UPWARD;
			}

		}
		response.put(MOVESTR, res);
		return response;
	}

	/**
	 * Check if it's a win or lost
	 * 
	 * @param winner   the best node
	 * @param root     the root node
	 * @param response the response that gonna be send to the server add shout
	 * @return the best node
	 */
	private AbstractNode checkWinLost(final AbstractNode winner, final AbstractNode root, final Map<String, String> response) {
		if (winner.getScoreRatio() < BASIC_SCORE) {
			response.put(SHOUT, losing);
			return lastChance(root);
		} else if (winner.getScoreRatio() > SnakeGeneticConstants.getStopExpandLimit()) {
			response.put(SHOUT, winning);
			return finishHim(root, winner);
		}
		return winner;
	}

	/**
	 * In a losing position, this method try to find the longest way, hpoing that
	 * the opponent make a mistake.
	 * 
	 * @param root The current Root node
	 * @return the best AbstractNode
	 */
	protected abstract AbstractNode lastChance(final AbstractNode root);

	/**
	 * In a winning position, this method try to find the shortest way to win.
	 * 
	 * @param root   The current Root node
	 * @param winner The predetermine best move
	 * @return the best AbstractNode
	 */
	protected abstract AbstractNode finishHim(final AbstractNode root, final AbstractNode winner);

	/**
	 * Add search time to the total
	 * 
	 * @param time time took this turn
	 */

	protected void addTimeTotal(final long time) {
		timeTotal += time;

	}

	/**
	 * add node count to total
	 * 
	 * @param childCount total child count
	 */
	protected void addNodeTotalCount(final int childCount) {
		nodeTotalCount += childCount;

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
		log.atInfo().log("Average node/s : " + (nodeTotalCount / timeTotal * 1000));
		return super.end(endRequest);
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

			losing = BattleSnakeConstants.getLostShout();
			winning = BattleSnakeConstants.getWinShout();

		} catch (IOException ex) {
			log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());
		}
	}

}
