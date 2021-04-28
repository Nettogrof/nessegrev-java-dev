package ai.nettogrof.battlesnake.snakes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Basic snake. This class is the "Nessegrev" snake on Battlesnake. a basic
 * snake that avoid wall, and other snakes' bodies. And try to go the the
 * nearest food, by going directly to it, doesn't check if their a snake between
 * him and the food.
 * 
 * This snake should work only with API v1.
 * 
 * @author carl.lajeunesse
 * @version Winter 2020
 */
public class BasicSnake extends AbstractSnakeAI {

	/**
	 * Basic / unused constructor
	 */
	public BasicSnake() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public BasicSnake(final String gameId) {
		super(gameId);

	}

	/**
	 * This method will be call on each move request receive by BattleSnake
	 * 
	 * @param moveRequest Json call received
	 * @return map of field to be return to battlesnake, example "move" , "up"
	 */
	@Override
	public Map<String, String> move(final JsonNode moveRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();

		// Put a value of 0 in each possible move
		possiblemove.put(UPWARD, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);

		// Create a board[][] filled with value 0
		final JsonNode boardJsonNode = moveRequest.get(BOARD);
		width = boardJsonNode.get(WIDTH_FIELD).asInt();
		height = boardJsonNode.get(HEIGHT_FIELD).asInt();
		int[][] board = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				board[x][y] = 0;
			}
		}

		// Put value -99 for each snake body part
		boardJsonNode.withArray(SNAKES).forEach(s -> {
			s.withArray(BODY).forEach(c -> {
				board[c.get("x").asInt()][c.get("y").asInt()] = -99;

			});

		});

		final int snakex = moveRequest.get(YOU).withArray(BODY).get(0).get("x").asInt();
		final int snakey = moveRequest.get(YOU).withArray(BODY).get(0).get("y").asInt();

		int maxd[] = { 99 }; // Max distance between snake head and food

		int[] foodxy = new int[2];

		// Determine the location (foodxy) of the nearest food
		boardJsonNode.withArray("food").forEach(f -> {
			if (Math.abs(f.get("x").asInt() - snakex) + Math.abs(f.get("y").asInt() - snakey) < maxd[0]) {
				maxd[0] = Math.abs(f.get("x").asInt() - snakex) + Math.abs(f.get("y").asInt() - snakey);
				foodxy[0] = f.get("x").asInt();
				foodxy[1] = f.get("y").asInt();
			}

		});
		final int foodx = foodxy[0];
		final int foody = foodxy[1];

		// Put value to each move based on nearest food
		possiblemove.put(DOWN, snakey - foody);
		possiblemove.put(UPWARD, foody - snakey);
		possiblemove.put(LEFT, snakex - foodx);
		possiblemove.put(RIGHT, foodx - snakex);

		response.put("move", bestMove(snakex, snakey, possiblemove, board));
		return response;
	}

	/**
	 * Choose the best move
	 * 
	 * @param snakex       snake head X
	 * @param snakey       snake head Y
	 * @param possiblemove map of possible move
	 * @param board        the board[][]
	 * @return String of the best move (up , down , left or right)
	 */
	private String bestMove(final int snakex, final int snakey, final Map<String, Integer> possiblemove,
			final int[][] board) {

		// For each direction , put value -90 if it's outside of the board, else put the
		// current value (based on food location) + the board value (based on same body
		// value 0 or - 99)
		if (snakey == height - 1) {
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

		if (snakex == width - 1) {
			possiblemove.put(RIGHT, -90);
		} else {
			possiblemove.put(RIGHT, possiblemove.get(RIGHT) + board[snakex + 1][snakey]);
		}

		// Choose the direction with the max value.
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

	/**
	 * Return the infos need by Battlesnake when receive a (root GET /) request
	 * 
	 * @return map of info for battlesnake
	 */
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

	/**
	 *
	 */
	@Override
	protected void setFileConfig() {
		fileConfig = "Basic.properties";
	}

}
