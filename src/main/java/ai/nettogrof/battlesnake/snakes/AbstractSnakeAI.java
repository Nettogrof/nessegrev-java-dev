package ai.nettogrof.battlesnake.snakes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.flogger.FluentLogger;

public abstract class AbstractSnakeAI {

	// public abstract Map<String, String> root(JsonNode startRequest);
	// public static abstract Map<String, String> getInfo();
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

		if (endRequest.get("board").get("snakes").size() > 0) {
			log.atInfo().log("Winner is : %s", endRequest.get("board").get("snakes").get(0).get("name").asText());

		} else {
			log.atInfo().log("DRAW");

		}
		return response;

	}

	protected abstract void setFileConfig();


}
