package ai.nettogrof.battlesnake.snakes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.API_V1;

/**
 * FloodFill snake. This class is the "Nessegrev-flood" snake on Battlesnake. My
 * first snake that I entered in a tournament (Stay Home and Code / Rookie
 * division) This snake use flood fill algorithm to find the best move. It tries
 * to target food and shorter snakes, and avoids bigger snakes.
 * 
 * This snake should work with API v0 and API v1. All the move calculation are
 * based on API v0, and if it's API v1, then the snake switch UP and DOWN
 * response.
 * 
 * @author carl.lajeunesse
 * @version Winter 2020
 */
public class FloodFillSnake extends AbstractSnakeAI {

	/**
	 * Boardd of empty space
	 */
	private transient int[][] space;

	/**
	 * Constant value
	 */
	private static final int FLOODENEMYBIGGER = -59;

	/**
	 * Constant value
	 */
	private static final int FLODDENEMYSMALLER = 20;

	/**
	 * Constant value
	 */
	private static final int FOODVALUE = 115;

	/**
	 * Constant value
	 */
	private static final int FLODDENEMYGAP = 35;

	/**
	 * Constant value
	 */
	private static final int EMPTY = 5000;

	/**
	 * 
	 */
	private static final int HAZARD_VALUE = 25;

	/**
	 * Basic / unused constructor
	 */
	public FloodFillSnake() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public FloodFillSnake(final String gameId) {
		super(gameId);

		try (InputStream input = Files.newInputStream(Paths.get("FloodFill.properties"))) {

			final Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			apiversion = Integer.parseInt(prop.getProperty("apiversion"));

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
		final JsonNode boardJson = moveRequest.get(BOARD);
		width = boardJson.get(WIDTH_FIELD).asInt();
		height = boardJson.get(HEIGHT_FIELD).asInt();
		int[][] board = new int[width][height];
		final JsonNode you = moveRequest.get(YOU);

		// To determine if the game is under api version 1 or 0 ( default 0)
		if (you.has("head")) {
			apiversion = API_V1;
		}

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				board[x][y] = 0;
				space[x][y] = 0;
			}
		}
		final String yourName = you.get(NAME).asText();
		final int mysnakelength = you.get(BODY).size();

		boardJson.withArray(SNAKES).forEach(s -> { // Foreach snake
			final JsonNode snakeBody = s.get(BODY);
			final int enemylength = snakeBody.size();
			final String snakeName = s.get(NAME).asText();
			final boolean isYourSnake = snakeName.equals(yourName);

			if (!isYourSnake && enemylength >= mysnakelength) {
				floodNegative(snakeBody.get(0).get("x").asInt(), snakeBody.get(0).get("y").asInt(), FLOODENEMYBIGGER,
						board); // Floodfill negative value from enemy snake head if he's bigger ( to avoid to
								// lost head-to-head)

			} else if (!isYourSnake) {
				floodPositive(snakeBody.get(0).get("x").asInt(), snakeBody.get(0).get("y").asInt(), FLODDENEMYSMALLER,
						board);// Floodfill positive value from enemy snake head if he's smaller ( to try to
								// kill it)

			}

			s.withArray(BODY).forEach(c -> { // For each snake body part , put -99 to avoid to move into a snake body

				board[c.get("x").asInt()][c.get("y").asInt()] = -99;

			});

			if (s.get(HEALTH).asInt() < 100 && moveRequest.get(TURN).asInt() > 3) {
				board[snakeBody.get(enemylength - 1).get("x").asInt()][snakeBody.get(enemylength - 1).get("y")
						.asInt()] = 0; // Put value = 0 for tail, because usually you can move on tail ( I didn't check
										// if the snake have eaten or not)
			}

		});

		addFoodValue(board, you, boardJson);
		addHazardsValue(board, boardJson);

		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("move", chooseBestMove(checkPossibleMove(you, board)));

		return response;
	}

	/**
	 * This method add a value on each square of hazards
	 * 
	 * @param board     the board of value
	 * @param boardJson the field Board fron the Json request
	 */
	private void addHazardsValue(final int[][] board, final JsonNode boardJson) {
		boardJson.withArray("hazards").forEach(f -> { // For BattleRoyale only remove -25 for square in hazard
			board[f.get("x").asInt()][f.get("y").asInt()] -= HAZARD_VALUE;

		});

	}

	/**
	 * This method add food value on the board based on your current
	 * <code>FOORVALUE - health</CODE>
	 * 
	 * @param board     the board of value
	 * @param you       the field "you" fron the Json request
	 * @param boardJson the field Board fron the Json request
	 */
	private void addFoodValue(final int[][] board, final JsonNode you, final JsonNode boardJson) {

		final int health = you.get(HEALTH).asInt();
		final int nbsnake = boardJson.get(SNAKES).size();
		boardJson.withArray("food").forEach(f -> {
			if (health < 10 || nbsnake > 1) {
				floodPositive(f.get("x").asInt(), f.get("y").asInt(), FOODVALUE - health, board); // Floodfill positive
																									// value for food (
																									// if snake is more
																									// hungry the value
																									// is higher)
			} else {
				floodNegative(f.get("x").asInt(), f.get("y").asInt(), FLOODENEMYBIGGER, board); // For challenge,
																								// floodfill negative
																								// value if my snake
																								// have more than 10
																								// health
			}

		});

	}

	/**
	 * Create a map of possible move and assign a value to them. a negative value is
	 * bad.
	 * 
	 * @param you   Json field you
	 * @param board the board (array of value)
	 * @return map of possible move and their value
	 */
	private Map<String, Integer> checkPossibleMove(final JsonNode you, final int[][] board) {
		final int snakex = you.withArray(BODY).get(0).get("x").asInt();
		final int snakey = you.withArray(BODY).get(0).get("y").asInt();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();

		// Put possible move to value 0
		possiblemove.put(UPWARD, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);

		// Put a value of -90 if out of bounds. Value is the current value + board value
		// + countEmptySquare value
		if (snakey == 0) {
			possiblemove.put(UPWARD, -90);

		} else {
			floodEmptySpace(snakex, snakey - 1, EMPTY, board);
			possiblemove.put(UPWARD, possiblemove.get(UPWARD) + board[snakex][snakey - 1] + countEmptySquare());
		}

		if (snakey == height - 1) {
			possiblemove.put(DOWN, -90);

		} else {
			floodEmptySpace(snakex, snakey + 1, EMPTY, board);
			possiblemove.put(DOWN, possiblemove.get(DOWN) + board[snakex][snakey + 1] + countEmptySquare());
		}

		if (snakex == 0) {
			possiblemove.put(LEFT, -90);

		} else {
			floodEmptySpace(snakex - 1, snakey, EMPTY, board);
			possiblemove.put(LEFT, possiblemove.get(LEFT) + board[snakex - 1][snakey] + countEmptySquare());
		}

		if (snakex == width - 1) {
			possiblemove.put(RIGHT, -90);

		} else {
			floodEmptySpace(snakex + 1, snakey, EMPTY, board);
			possiblemove.put(RIGHT, possiblemove.get(RIGHT) + board[snakex + 1][snakey] + countEmptySquare());
		}
		return possiblemove;
	}

	/**
	 * Choose the best move
	 * 
	 * @param possiblemove map of possible move
	 * @return String for the move (up ,down, left or right)
	 */
	private String chooseBestMove(final Map<String, Integer> possiblemove) {
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
		if (apiversion == API_V1) {
			if (res.equals(UPWARD)) {
				res = DOWN;
			} else if (res.equals(DOWN)) {
				res = UPWARD;
			}
		}
		return res;
	}

	/**
	 * Function when receive the start request ( API version 0 )
	 * 
	 * Shouldn't be used anymore
	 * 
	 */
	@Override
	public Map<String, String> start(final JsonNode startRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("color", "#FF0000");
		response.put("headType", "sand-worm");
		response.put("tailType", "sharp");
		width = startRequest.get("board").get("width").asInt();
		height = startRequest.get("board").get("height").asInt();
		// board = new int[width][heigth];
		space = new int[width][height];

		return response;
	}

	/**
	 * Recursive function to "flood" positive value. It's assign the value to the
	 * board [ x ] [ y] then recall this function for adjacent square with value - 1
	 * 
	 * @param posX  the X position
	 * @param posY  the Y position
	 * @param value the value to assign
	 * @param board the board (array of value)
	 */
	private void floodPositive(final int posX, final int posY, final int value, final int[][] board) {
		if (board[posX][posY] >= 0 && board[posX][posY] < value) { // Only on positive square value and where the
																	// current value is lower than the value we want to
																	// assign
			board[posX][posY] = value;
			if (value > 0) { // Stop here if value is equal to 0
				if (posX > 0) {
					floodPositive(posX - 1, posY, value - 1, board); // spread the flood to the position X - 1, with the
																		// value - 1
				}
				if (posY > 0) {
					floodPositive(posX, posY - 1, value - 1, board); // spread the flood to the position Y - 1, with the
																		// value - 1
				}
				if (posY < height - 1) {
					floodPositive(posX, posY + 1, value - 1, board);// spread the flood to the position Y - 1, with the
																	// value - 1
				}

				if (posX < width - 1) {
					floodPositive(posX + 1, posY, value - 1, board); // spread the flood to the position X + 1, with the
																		// value - 1
				}
			}
		}
	}

	/**
	 * Recursive function to "flood" negative value. It's assign the value to the
	 * board [ x ] [ y] then recall this function for adjacent square with value -
	 * "floodEnemyGap "
	 * 
	 * @param posX  the X position
	 * @param posY  the Y position
	 * @param value the value to assign
	 * @param board the board (array of value)
	 */
	private void floodNegative(final int posX, final int posY, final int value, final int[][] board) {
		if (board[posX][posY] > -90) {
			board[posX][posY] = value < board[posX][posY] ? value : board[posX][posY];
			if (value < 0 - FLODDENEMYGAP) {
				if (posX > 0) {
					floodNegative(posX - 1, posY, value + FLODDENEMYGAP, board);
				}
				if (posY > 0) {
					floodNegative(posX, posY - 1, value + FLODDENEMYGAP, board);
				}
				if (posY < height - 1) {
					floodNegative(posX, posY + 1, value + FLODDENEMYGAP, board);
				}

				if (posX < width - 1) {
					floodNegative(posX + 1, posY, value + FLODDENEMYGAP, board);
				}
			}
		}
	}

	/**
	 * Recursive function to "flood" EMPTY value on positive square. It's assign the
	 * EMPTY to the space [ x ] [ y] then recall this function for adjacent square
	 * with EMPTY
	 * 
	 * @param posX  the X position
	 * @param posY  the Y position
	 * @param value the value to assign
	 * @param board the board (array of value)
	 */
	private void floodEmptySpace(final int posX, final int posY, final int value, final int[][] board) {
		if (board[posX][posY] >= 0 && space[posX][posY] < 1000) {
			space[posX][posY] = value;
			if (value > 0) {
				if (posX > 0) {
					floodEmptySpace(posX - 1, posY, value, board);
				}
				if (posY > 0) {
					floodEmptySpace(posX, posY - 1, value, board);
				}
				if (posY < height - 1) {
					floodEmptySpace(posX, posY + 1, value, board);
				}

				if (posX < width - 1) {
					floodEmptySpace(posX + 1, posY, value, board);
				}
			}
		}
	}

	/**
	 * Scan the space[][] to count empty square. And put square[][] back to 0 value
	 * 
	 * @return the count of empty space
	 */
	private int countEmptySquare() {
		int countEmpty = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				if (space[x][y] == EMPTY) {
					countEmpty++;
				}
				space[x][y] = 0;
			}
		}
		return countEmpty;
	}

	@Override
	protected String getFileConfig() {
		return fileConfig;
	}

}
