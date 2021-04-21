package ai.nettogrof.battlesnake.snakes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.flogger.FluentLogger;

public abstract class AbstractSnakeAI {

	
	protected final static String UPWARD = "up";
	protected final static String DOWN = "down";
	protected final static String LEFT = "left";
	protected final static String RIGHT = "right";
	protected final static String MOVESTR = "move";
	protected final static String BODY = "body";
	protected final static String BOARD = "board";
	protected final static String NAME = "name";
	protected final static String SQUAD = "squad";
	protected final static String HEALTH = "HEALTH";
	protected final static String SNAKES = "snakes";
	protected final static String YOU = "you";
	protected final static String TURN = "turn";
	protected final static String HEAD = "head";
	protected final static String WIDTH_FIELD = "width";
	protected final static String HEIGTH_FIELD = "heigth";
	
	
	public static String fileConfig;

	protected String gameId;

	protected static transient FluentLogger log = FluentLogger.forEnclosingClass();

	public abstract Map<String, String> move(JsonNode moveRequest);

	public abstract Map<String, String> start(JsonNode startRequest);

	

	// Default construtor
	public AbstractSnakeAI() {
		// empty
	}

	public AbstractSnakeAI(final String gameId) {
		this.gameId = gameId;

	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(final String gameId) {
		this.gameId = gameId;
	}

	public void ping() {
		log.atInfo().log("Got Pinged");
	};
	
	public Map<String, String> end(final JsonNode endRequest) {

		final Map<String, String> response = new ConcurrentHashMap<>();

		if (endRequest.get(BOARD).get(SNAKES).size() > 0) {
			log.atInfo().log("Winner is : %s", endRequest.get(BOARD).get(SNAKES).get(0).get(NAME).asText());

		} else {
			log.atInfo().log("DRAW");

		}
		return response;

	}

	protected abstract void setFileConfig();


}
