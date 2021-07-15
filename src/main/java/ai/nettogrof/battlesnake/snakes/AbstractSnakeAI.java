package ai.nettogrof.battlesnake.snakes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.flogger.FluentLogger;

/**
 * Any snake should extend this abstract class it contains basic constant
 * fields, related to the field name in json call from BattleSnake. Also some
 * method that any snake must implements.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractSnakeAI {

	/**
	 * Constant Field name
	 */
	protected static final String UPWARD = "up";

	/**
	 * Constant Field name
	 */
	protected static final String DOWN = "down";

	/**
	 * Constant Field name
	 */
	protected static final String LEFT = "left";

	/**
	 * Constant Field name
	 */
	protected static final String RIGHT = "right";

	/**
	 * Constant Field name
	 */
	protected static final String MOVESTR = "move";

	/**
	 * Constant Field name
	 */
	protected static final String BODY = "body";

	/**
	 * Constant Field name
	 */
	protected static final String BOARD = "board";

	/**
	 * Constant Field name
	 */
	protected static final String NAME = "name";

	/**
	 * Constant Field name
	 */
	protected static final String SQUAD = "squad";

	/**
	 * Constant Field name
	 */
	protected static final String HEALTH = "HEALTH";

	/**
	 * Constant Field name
	 */
	protected static final String SNAKES = "snakes";

	/**
	 * Constant Field name
	 */
	protected static final String YOU = "you";

	/**
	 * Constant Field name
	 */
	protected static final String TURN = "turn";

	/**
	 * Constant Field name
	 */
	protected static final String HEAD = "head";

	/**
	 * Constant Field name
	 */
	protected static final String WIDTH_FIELD = "width";

	/**
	 * Constant Field name
	 */
	protected static final String HEIGHT_FIELD = "height";

	/**
	 * Constant Field name
	 */
	protected static final int FOUR_SNAKE = 4;

	/**
	 * Constant Field name
	 */
	protected static final int TWO_SNAKE = 2;

	/**
	 * Constant Field name
	 */
	protected static final int SINGLE_SNAKE = 1;

	/**
	 * Board width
	 */
	protected int width;

	/**
	 * Board height
	 */
	protected int height;

	/**
	 * API version use, some snakes are able to play both version v0 and v1.
	 */
	protected transient int apiversion;

	/**
	 * Any snake must use a fileConfig (a properties files), and the name of the
	 * file must store in the string
	 */
	protected String fileConfig;

	/**
	 * Snakes can play games in parallel, so the game id field is use to distinguish
	 * different game
	 */
	protected transient String gameId;

	/**
	 * Basic logger object
	 */
	protected static transient FluentLogger log = FluentLogger.forEnclosingClass();

	/**
	 * Basic constructor not used
	 */
	public AbstractSnakeAI() {
		// empty
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public AbstractSnakeAI(final String gameId) {
		this.gameId = gameId;

	}

	/**
	 * This method was used in API v0 to retrieve snake info, but in API v1 the
	 * method is call but Battlesnake doesn't need a response. This can be use to
	 * initialize your snake
	 * 
	 * @param startRequest Json call received
	 * @return map that can be empty because it will be ignore by BattleSnake server
	 */
	public Map<String, String> start(final JsonNode startRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("ok", "ok");
		return response;
	}

	/**
	 * This method will be call on each move request receive by BattleSnake, so all
	 * snakes must implement it, and add game logic in it
	 * 
	 * @param moveRequest Json call received
	 * @return map of field to be return to battlesnake, example "move" , "up"
	 */
	public abstract Map<String, String> move(final JsonNode moveRequest);

	/**
	 * This method will be call at the end of the game, can be override if you want
	 * to clean-up some game info.
	 * 
	 * @param endRequest Json call received
	 * @return map that can be empty because it will be ignore by BattleSnake server
	 */
	public Map<String, String> end(final JsonNode endRequest) {

		final Map<String, String> response = new ConcurrentHashMap<>();

		if (endRequest.get(BOARD).get(SNAKES).size() > 0) {
			log.atInfo().log("Winner is : %s", endRequest.get(BOARD).get(SNAKES).get(0).get(NAME).asText());
		} else {
			log.atInfo().log("DRAW");

		}
		return response;
	}

	/**
	 * Method that was use in API v0 to check if the bots was up. in API v1 is not
	 * used anymore
	 */
	public void ping() {
		log.atInfo().log("Got Pinged");
	};

	/**
	 * Method use to set the fileConfig string
	 */
	protected abstract void setFileConfig();

	/**
	 * Gets the config file to set properties correctly
	 * 
	 * @return the config filename
	 */
	protected abstract String getFileConfig();

	/**
	 * Return the infos need by Battlesnake when receive a (root GET /) request
	 * 
	 * @return map of info for Battlesnake
	 */
	public Map<String, String> getInfo() {

		final Map<String, String> response = new ConcurrentHashMap<>();
		try (InputStream input = Files.newInputStream(Paths.get(getFileConfig()))) {

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

}
