package ai.nettogrof.battlesnake.snakes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;

public class BasicSnake extends AbstractSnakeAI {

	public BasicSnake() {
		super();
	}

	public BasicSnake(final String gameId) {
		super(gameId);

	}

	@Override
	public Map<String, String> start(final JsonNode startRequest) {

		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("color", "#ff00ff");
		response.put("headType", "sand-worm");
		response.put("tailType", "sharp");
		return response;
	}

	@Override
	public Map<String, String> move(final JsonNode moveRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		possiblemove.put(UPWARD, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);
		final JsonNode boardJsonNode = moveRequest.get("board");
		final int boardWidth = boardJsonNode.get("width").asInt();
		final int boardHeight = boardJsonNode.get("height").asInt();
		int[][] board = new int[boardWidth][boardHeight];
		for (int x = 0; x < boardWidth; x++) {
			for (int y = 0; y < boardHeight; y++) {
				board[x][y] = 0;
			}
		}
		boardJsonNode.withArray("snakes").forEach(s -> {
			s.withArray("body").forEach(c -> {
				board[c.get("x").asInt()][c.get("y").asInt()] = -99;

			});

		});

		final int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
		final int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
		int maxd[] = { 99 };

		int[] foodxy = new int[2];
		boardJsonNode.withArray("food").forEach(f -> {
			if (Math.abs(f.get("x").asInt() - snakex) + Math.abs(f.get("y").asInt() - snakey) < maxd[0]) {
				maxd[0] = Math.abs(f.get("x").asInt() - snakex) + Math.abs(f.get("y").asInt() - snakey);
				foodxy[0] = f.get("x").asInt();
				foodxy[1] = f.get("y").asInt();
			}

		});
		final int foodx = foodxy[0];
		final int foody = foodxy[1];
		possiblemove.put(DOWN, snakey - foody);
		possiblemove.put(UPWARD, foody - snakey);
		possiblemove.put(LEFT, snakex - foodx);
		possiblemove.put(RIGHT, foodx - snakex);

		response.put("move", bestMove(snakex, snakey, possiblemove, board, boardHeight, boardWidth));
		return response;
	}

	private String bestMove(final int snakex, final int snakey, final Map<String, Integer> possiblemove,
			final int[][] board, final int boardHeight, final int boardWidth) {
		if (snakey == boardHeight - 1) {
			possiblemove.put(UPWARD, -90);
		} else {
			possiblemove.put(UPWARD, possiblemove.get(UPWARD) + board[snakex][snakey + 1]);
		}

		if (snakey == 0) {
			possiblemove.put(DOWN, -90);
		} else {
			possiblemove.put(DOWN, possiblemove.get(DOWN) + board[snakex][snakey - 1]);
		}

		if (snakex == 0) {
			possiblemove.put(LEFT, -90);
		} else {
			possiblemove.put(LEFT, possiblemove.get(LEFT) + board[snakex - 1][snakey]);
		}

		if (snakex == boardWidth - 1) {
			possiblemove.put(RIGHT, -90);
		} else {
			possiblemove.put(RIGHT, possiblemove.get(RIGHT) + board[snakex + 1][snakey]);
		}

		String res = UPWARD;
		int value = possiblemove.get(UPWARD);

		if (possiblemove.get(DOWN) > value) {
			value = possiblemove.get(DOWN);
			res = DOWN;
		}

		if (possiblemove.get(LEFT) > value) {
			value = possiblemove.get(LEFT);
			res = LEFT;
		}
		if (possiblemove.get(RIGHT) > value) {
			res = RIGHT;
		}
		return res;
	}

	
	public static Map<String, String> getInfo() {

		fileConfig = "Basic.properties";
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
	protected void setFileConfig() {
		// TODO Auto-generated method stub

	}

}
