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
 * Challenger snake. This class is the "Nessegrev-challenge" snake on
 * Battlesnake. This snake was made only to hard-code few rule to complete some
 * challenge. Nothing to see here. It a mess, it gonna stay like that.
 * 
 * Probably still in API v0, and this is mostly short test, hand made, without
 * thinking. Please save yourself and don't look at this code
 * 
 * @author carl.lajeunesse
 * @version Summer 2020
 */
public final class Challenger extends AbstractSnakeAI {
	private transient int maxturn = 9_999_999;

	private static final int BOARD_ARRAY[][] = { { 4, 4, 44, 38, 36, 33, 34 }, { 4, 4, 43, 37, 35, 31, 32 },
			{ 6, 5, 49, 47, 48, 29, 30 }, { 8, 7, 39, 41, 45, 27, 28 }, { 10, 9, 40, 42, 46, 25, 26 },
			{ 12, 11, 15, 17, 19, 21, 23 }, { 14, 13, 16, 18, 20, 22, 24 } };

	private static final int FOURALIVE[][] = { { 0, 1, 2, 3 }, { 43, 6, 5, 4 }, { 42, 7, 8, 9 }, { 41, 12, 11, 10 },
			{ 40, 13, 14, 15 }, { 39, 18, 17, 16 }, { 38, 19, 20, 21 }, { 37, 24, 23, 22 }, { 36, 25, 26, 27 },
			{ 35, 32, 31, 28 }, { 34, 33, 30, 29 }

	};
	private static final int FLOODENEMYGAP = 25;
	private static final int DUO_BOARD[][] = { { 6, 6, 29, 31, 33, 56, 99, 99, 99, 99, 99 },
			{ 6, 6, 30, 32, 34, 57, 99, 99, 99, 99, 99 }, { 6, 6, 54, 38, 37, 56, 99, 99, 99, 99, 99 },
			{ 8, 7, 53, 42, 41, 57, 99, 99, 99, 99, 99 }, { 10, 9, 49, 47, 48, 56, 99, 99, 99, 99, 99 },
			{ 12, 11, 50, 45, 46, 57, 99, 99, 99, 99, 99 }, { 14, 13, 52, 43, 44, 56, 99, 99, 99, 99, 99 },
			{ 16, 15, 51, 39, 40, 57, 99, 99, 99, 99, 99 }, { 18, 17, 55, 35, 36, 56, 99, 99, 99, 99, 99 },
			{ 20, 19, 23, 25, 27, 57, 99, 99, 99, 99, 99 }, { 22, 21, 24, 26, 28, 56, 99, 99, 99, 99, 99 } };
	private static final int DUO_BOARD2[][] = { { 6, 6, 29, 31, 99, 56, 27, 28, 29, 31, 33 },
			{ 6, 6, 30, 32, 99, 57, 25, 26, 30, 32, 34 }, { 6, 6, 54, 38, 99, 56, 23, 24, 49, 36, 35 },
			{ 8, 7, 53, 42, 99, 57, 21, 22, 50, 38, 37 }, { 10, 9, 49, 47, 99, 56, 19, 20, 51, 40, 39 },
			{ 12, 11, 50, 45, 99, 57, 17, 18, 52, 42, 41 }, { 14, 13, 52, 43, 99, 56, 15, 16, 53, 44, 43 },
			{ 16, 15, 51, 39, 99, 57, 13, 14, 54, 46, 45 }, { 18, 17, 55, 35, 99, 56, 11, 12, 55, 48, 49 },
			{ 20, 19, 23, 25, 99, 57, 10, 8, 6, 6, 6 }, { 22, 21, 24, 26, 99, 56, 9, 7, 6, 6, 6 } };

	private static final int pathMoveArray[][] = { { 0, 0, 4, 5, 6, 7, 8 }, { 0, 0, 3, 12, 11, 10, 9 },
			{ 0, 0, 2, 13, 14, 15, 16 }, { 0, 0, 1, 26, 25, 24, 17 }, { 0, 39, 40, 27, 28, 23, 18 },
			{ 37, 38, 33, 32, 29, 22, 19 }, { 36, 35, 34, 31, 30, 21, 20 } };
	private static final int DOULENGTH = 14;
	private transient boolean startL;
	private transient boolean startR;
	private transient boolean secondL;
	private transient boolean secondR;
	private transient boolean thirdL;
	private transient boolean thirdR;
	private transient boolean pathFollow;
	private transient boolean flag;

	private transient boolean mirrorChallenge;
	private transient boolean battleTriple;

	private transient boolean keepFourSnakeAlive;

	private transient int heigth;

	private transient int width;

	private transient int[][] space;

	/**
	 * Basic unused constructor
	 */
	public Challenger() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public Challenger(final String gameId) {
		super(gameId);
		setFileConfig();
		try (InputStream input = Files.newInputStream(Paths.get(getFileConfig()))) {

			final Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			
			maxturn = Integer.parseInt(prop.getProperty("maxturn"));
			pathFollow = Boolean.parseBoolean(prop.getProperty("path"));

		} catch (IOException ex) {
			log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());
		}
	}

	
	@Override
	public Map<String, String> move(final JsonNode moveRequest) {

		Map<String, String> response = new ConcurrentHashMap<>();

		//Trying to figure which challenge it is and choose the right method
		if (moveRequest.get(BOARD).get(SNAKES).size() == 1) {
			if (moveRequest.get(BOARD).get(BODY).asInt() == 7) {
				response = soloChallenge(moveRequest);
			} else {
				response = fourCornerChallenge(moveRequest);
			}
		} else {
			if (mirrorChallenge) {
				response = duoChallenge(moveRequest);
			} else if (battleTriple) {
				response = tripleChallenge(moveRequest);
			} else if (keepFourSnakeAlive) {
				response = keepFourSnakeAliveChallenge(moveRequest);
			} else {
				int count = 0;
				for (int j = 0; j < moveRequest.get(BOARD).get(SNAKES).size(); j++) {
					if (moveRequest.get(BOARD).get(SNAKES).get(j).get(NAME).asText()
							.equals(moveRequest.get(YOU).get(NAME).asText())) {
						count++;
					}
				}

				if (count == 2) {
					mirrorChallenge = true;
					response = duoChallenge(moveRequest);
				} else if (count == 3) {
					battleTriple = true;
					response = tripleChallenge(moveRequest);
				} else if (count == 4) {
					keepFourSnakeAlive = true;
					response = keepFourSnakeAliveChallenge(moveRequest);
				}
			}
		}

		return response;
	}

	private Map<String, String> keepFourSnakeAliveChallenge(final JsonNode moveRequest) {
		width = moveRequest.get(BOARD).get(BODY).asInt();
		heigth = moveRequest.get(BOARD).get(HEIGHT_FIELD).asInt();

		int snakeId = 0;

		for (int i = 0; i < moveRequest.get(BOARD).get(SNAKES).size(); i++) {

			if (moveRequest.get(BOARD).get(SNAKES).get(i).get("id").asText()
					.equals(moveRequest.get(YOU).get("id").asText())) {
				snakeId = i;
			}

		}

		final Map<String, String> response = new ConcurrentHashMap<>();

		
		final int snakey = moveRequest.get(YOU).withArray(BODY).get(0).get("y").asInt();

		if (snakey >= (snakeId + 1) * 4) {
			response.put(MOVESTR, UPWARD);
			return response;
		} else if (snakey < snakeId * 4) {
			response.put(MOVESTR, DOWN);
			return response;
		}
		final int snakex = moveRequest.get(YOU).withArray(BODY).get(0).get("x").asInt();
		int pos = FOURALIVE[snakex][snakey % 4];
		if (pos == 43) {
			pos = -1;
		}

		final int target = pos + 1;
		response.put("shout", "TARGET " + target + " sid: " + moveRequest.get(YOU).get("id").asText());
		if (snakex != 0 && FOURALIVE[snakex - 1][snakey % 4] == target) {
			response.put(MOVESTR, LEFT);
		} else if (snakey % 4 != 0 && FOURALIVE[snakex][snakey % 4 - 1] == target) {
			response.put(MOVESTR, UPWARD);
		} else if (snakey % 4 != 3 && FOURALIVE[snakex][snakey % 4 + 1] == target) {
			response.put(MOVESTR, DOWN);
		} else if (snakex != 10 && FOURALIVE[snakex + 1][snakey % 4] == target) {
			response.put(MOVESTR, RIGHT);
		}

		return response;
	}

	private Map<String, String> fourCornerChallenge(final JsonNode moveRequest) {
		width = moveRequest.get(BOARD).get(BODY).asInt();
		heigth = moveRequest.get(BOARD).get(HEIGHT_FIELD).asInt();
		int[][] board = new int[width][heigth];
		space = new int[width][heigth];

		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		response.put("shout", moveRequest.get(YOU).get("id").asText());
		possiblemove.put(UPWARD, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);

		for (int x = 0; x < moveRequest.get(BOARD).get(BODY).asInt(); x++) {
			for (int y = 0; y < moveRequest.get(BOARD).get(HEIGHT_FIELD).asInt(); y++) {
				board[x][y] = 0;
				space[x][y] = 0;

			}
		}

		moveRequest.get(BOARD).withArray(SNAKES).forEach(s -> {

			s.withArray(BODY).forEach(c -> {

				board[c.get("x").asInt()][c.get("y").asInt()] = -99;

			});

		});

		final int snakex = moveRequest.get(YOU).withArray(BODY).get(0).get("x").asInt();
		final int snakey = moveRequest.get(YOU).withArray(BODY).get(0).get("y").asInt();

		// maxd = 99;

		final int health = moveRequest.get(YOU).get(HEALTH).asInt();
		if (moveRequest.get(YOU).get("length").asInt() < 70) {
			moveRequest.get(BOARD).withArray("food").forEach(f -> {

				flood(f.get("x").asInt(), f.get("y").asInt(), 100 - health, board);

			});
		}

		moveRequest.get(BOARD).withArray("hazards").forEach(f -> {
			board[f.get("x").asInt()][f.get("y").asInt()] -= 25;

		});

		flood(0, 0, health, board);
		flood(width - 1, 0, health, board);
		flood(0, heigth - 1, health, board);
		flood(width - 1, heigth - 1, health, board);
		String res = UPWARD;
		if (board[0][0] == -99 && board[0][18] == -99 && board[18][0] == -99 && board[18][18] == -99) {
			if (snakey == 0) {
				response.put(MOVESTR, UPWARD);
			} else if (snakey == 18) {
				response.put(MOVESTR, DOWN);
			} else if (snakex == 0) {
				response.put(MOVESTR, LEFT);
			} else if (snakex == 18) {
				response.put(MOVESTR, RIGHT);
			} else {
				res = "continue";
			}
		} else {
			res = "continue";
		}

		if ("continue".equalsIgnoreCase(res)) {
			res = UPWARD;
			double result[] = { 0.5, 0.5, 0.5, 0.5 };
			if (snakey == 0) {
				possiblemove.put(UPWARD, -90);
				result[0] = 0.0;
			} else {
				computeSpace(snakex, snakey - 1, 5000, board);
				possiblemove.put(UPWARD, possiblemove.get(UPWARD) + board[snakex][snakey - 1] + count5000());
			}

			if (snakey == heigth - 1) {
				possiblemove.put(DOWN, -90);
				result[1] = 0.0;
			} else {
				computeSpace(snakex, snakey + 1, 5000, board);
				possiblemove.put(DOWN, possiblemove.get(DOWN) + board[snakex][snakey + 1] + count5000());
			}

			if (snakex == 0) {
				possiblemove.put(LEFT, -90);
				result[2] = 0.0;
			} else {
				computeSpace(snakex - 1, snakey, 5000, board);
				possiblemove.put(LEFT, possiblemove.get(LEFT) + board[snakex - 1][snakey] + count5000());
			}

			if (snakex == width - 1) {
				possiblemove.put(RIGHT, -90);
				result[3] = 0.0;
			} else {
				computeSpace(snakex + 1, snakey, 5000, board);
				possiblemove.put(RIGHT, possiblemove.get(RIGHT) + board[snakex + 1][snakey] + count5000());
			}

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

			response.put(MOVESTR, res);
		}
		return response;
	}

	private Map<String, String> tripleChallenge(final JsonNode moveRequest) {
		Map<String, String> response = new ConcurrentHashMap<>();
		if (moveRequest.get(BOARD).get(SNAKES).get(0).get("id").asText()
				.equals(moveRequest.get(YOU).get("id").asText())) {
			response = moveTriplePlayer1(moveRequest);
		} else if (moveRequest.get(BOARD).get(SNAKES).get(1).get("id").asText()
				.equals(moveRequest.get(YOU).get("id").asText())) {
			response = moveTriplePlayer2(moveRequest);
		} else {
			response.put(MOVESTR, UPWARD);
		}
		return response;
	}

	private Map<String, String> moveTriplePlayer2(final JsonNode moveRequest) {
		width = moveRequest.get(BOARD).get(BODY).asInt();
		heigth = moveRequest.get(BOARD).get(HEIGHT_FIELD).asInt();
		int[][] board = new int[width][heigth];
		space = new int[width][heigth];

		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		response.put("shout", moveRequest.get(YOU).get("id").asText());
		possiblemove.put(UPWARD, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);

		for (int x = 0; x < moveRequest.get(BOARD).get(BODY).asInt(); x++) {
			for (int y = 0; y < moveRequest.get(BOARD).get(HEIGHT_FIELD).asInt(); y++) {
				board[x][y] = 0;
				space[x][y] = 0;

			}
		}
		final String snakeId = moveRequest.get(YOU).get("id").asText();
		moveRequest.get(BOARD).withArray(SNAKES).forEach(s -> {
			if (!s.get("id").asText().equals(snakeId)) {
				floodEnemy(s.get(BODY).get(0).get("x").asInt(), s.get(BODY).get(0).get("y").asInt(), -35, board);
			}

			s.withArray(BODY).forEach(c -> {
				board[c.get("x").asInt()][c.get("y").asInt()] = -99;
			});

		});

		final int snakex = moveRequest.get(YOU).withArray(BODY).get(0).get("x").asInt();
		final int snakey = moveRequest.get(YOU).withArray(BODY).get(0).get("y").asInt();

		// maxd = 99;

		final int health = moveRequest.get(YOU).get(HEALTH).asInt();
		moveRequest.get(BOARD).withArray("food").forEach(f -> {
			if (health < 45) {
				flood(f.get("x").asInt(), f.get("y").asInt(), 100, board);
			} else {
				floodEnemy(f.get("x").asInt(), f.get("y").asInt(), -15, board);

			}

		});

		moveRequest.get(BOARD).withArray("hazards").forEach(f -> {
			board[f.get("x").asInt()][f.get("y").asInt()] -= 25;

		});

		flood(6, 6, health / 4, board);

		String res = UPWARD;

		double result[] = { 0.5, 0.5, 0.5, 0.5 };
		if (snakey == 0) {
			possiblemove.put(UPWARD, -90);
			result[0] = 0.0;
		} else {
			computeSpace(snakex, snakey - 1, 5000, board);
			possiblemove.put(UPWARD, possiblemove.get(UPWARD) + board[snakex][snakey - 1] + count5000());
		}

		if (snakey == heigth - 1) {
			possiblemove.put(DOWN, -90);
			result[1] = 0.0;
		} else {
			computeSpace(snakex, snakey + 1, 5000, board);
			possiblemove.put(DOWN, possiblemove.get(DOWN) + board[snakex][snakey + 1] + count5000());
		}

		if (snakex == 0) {
			possiblemove.put(LEFT, -90);
			result[2] = 0.0;
		} else {
			computeSpace(snakex - 1, snakey, 5000, board);
			possiblemove.put(LEFT, possiblemove.get(LEFT) + board[snakex - 1][snakey] + count5000());
		}

		if (snakex == width - 1) {
			possiblemove.put(RIGHT, -90);
			result[3] = 0.0;
		} else {
			computeSpace(snakex + 1, snakey, 5000, board);
			possiblemove.put(RIGHT, possiblemove.get(RIGHT) + board[snakex + 1][snakey] + count5000());
		}

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

		response.put(MOVESTR, res);

		return response;
	}

	private Map<String, String> moveTriplePlayer1(final JsonNode moveRequest) {
		width = moveRequest.get(BOARD).get(BODY).asInt();
		heigth = moveRequest.get(BOARD).get(HEIGHT_FIELD).asInt();
		int[][] board = new int[width][heigth];
		space = new int[width][heigth];

		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		response.put("shout", moveRequest.get(YOU).get("id").asText());
		possiblemove.put(UPWARD, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);

		for (int x = 0; x < moveRequest.get(BOARD).get(BODY).asInt(); x++) {
			for (int y = 0; y < moveRequest.get(BOARD).get(HEIGHT_FIELD).asInt(); y++) {
				board[x][y] = 0;
				space[x][y] = 0;
			}
		}
		final String snakeId = moveRequest.get(YOU).get("id").asText();
		moveRequest.get(BOARD).withArray(SNAKES).forEach(s -> {
			if (!s.get("id").asText().equals(snakeId)) {
				floodEnemy(s.get(BODY).get(0).get("x").asInt(), s.get(BODY).get(0).get("y").asInt(), -35, board);
			}
			s.withArray(BODY).forEach(c -> {

				board[c.get("x").asInt()][c.get("y").asInt()] = -99;

			});

		});

		final int snakex = moveRequest.get(YOU).withArray(BODY).get(0).get("x").asInt();
		final int snakey = moveRequest.get(YOU).withArray(BODY).get(0).get("y").asInt();

		// maxd = 99;

		final int health = moveRequest.get(YOU).get(HEALTH).asInt();
		moveRequest.get(BOARD).withArray("food").forEach(f -> {
			if (health < 45) {
				flood(f.get("x").asInt(), f.get("y").asInt(), 100, board);
			} else {
				floodEnemy(f.get("x").asInt(), f.get("y").asInt(), -15, board);

			}
		});

		moveRequest.get(BOARD).withArray("hazards").forEach(f -> {
			board[f.get("x").asInt()][f.get("y").asInt()] -= 25;

		});

		flood(5, 5, 14, board);

		String res = UPWARD;
		double result[] = { 0.5, 0.5, 0.5, 0.5 };
		if (snakey == 0) {
			possiblemove.put(UPWARD, -90);
			result[0] = 0.0;
		} else {
			computeSpace(snakex, snakey - 1, 5000, board);
			possiblemove.put(UPWARD, possiblemove.get(UPWARD) + board[snakex][snakey - 1] + count5000());
		}

		if (snakey == heigth - 1) {
			possiblemove.put(DOWN, -90);
			result[1] = 0.0;
		} else {
			computeSpace(snakex, snakey + 1, 5000, board);
			possiblemove.put(DOWN, possiblemove.get(DOWN) + board[snakex][snakey + 1] + count5000());
		}

		if (snakex == 0) {
			possiblemove.put(LEFT, -90);
			result[2] = 0.0;
		} else {
			computeSpace(snakex - 1, snakey, 5000, board);
			possiblemove.put(LEFT, possiblemove.get(LEFT) + board[snakex - 1][snakey] + count5000());
		}

		if (snakex == width - 1) {
			possiblemove.put(RIGHT, -90);
			result[3] = 0.0;
		} else {
			computeSpace(snakex + 1, snakey, 5000, board);
			possiblemove.put(RIGHT, possiblemove.get(RIGHT) + board[snakex + 1][snakey] + count5000());
		}

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

		response.put(MOVESTR, res);

		return response;
	}

	private Map<String, String> duoChallenge(final JsonNode moveRequest) {
		Map<String, String> response;
		String id1 = moveRequest.get(BOARD).get(SNAKES).get(0).get("id").asText();
		String id2 = moveRequest.get(BOARD).get(SNAKES).get(1).get("id").asText();

		if (id1.compareTo(id2) < 0) {
			
			id1 = id2;
		}

		if (id1.equals(moveRequest.get(YOU).get("id").asText())) {
			response = movePlayer1(moveRequest);
		} else {
			response = movePlayer2(moveRequest);
		}

		return response;
	}

	private Map<String, String> movePlayer1(final JsonNode moveRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		final int snakex = moveRequest.get(YOU).withArray(BODY).get(0).get("x").asInt();
		final int snakey = moveRequest.get(YOU).withArray(BODY).get(0).get("y").asInt();

		if (snakex == 0 && snakey == 0 && !secondL) {
			startL = true;
		} else if (snakex == 0 && snakey == 0) {
			if (moveRequest.get(BOARD).get("food").size() < 25) {
				secondL = false;
			} else {
				thirdL = true;
			}
		} else if (snakex == 9 && snakey == 10) {
			secondL = true;
		}

		// If you read this comment, I told you not to look at this mess...
		if (!startL || !secondL || !thirdL) {
			if (!startL) {
				if (snakey > 0) {
					response.put(MOVESTR, UPWARD);
				} else if (snakex > 0) {
					response.put(MOVESTR, LEFT);
				}
			} else if (!secondL) {
				if (snakey < 10) {
					response.put(MOVESTR, DOWN);
				} else if (snakex < 10) {
					response.put(MOVESTR, RIGHT);
				}
			} else {
				if (snakey > 0) {
					response.put(MOVESTR, UPWARD);
				} else if (snakex > 0) {
					response.put(MOVESTR, LEFT);
				}
			}
		} else {

			final int turn = moveRequest.get(TURN).asInt();
			if (turn > maxturn) {
				response.put(MOVESTR, UPWARD);
				return response;
			}
			int length = moveRequest.get(YOU).withArray(BODY).size();
			if (length % 2 != 0) {
				length++;
			}
			if (length < DOULENGTH) {
				length = DOULENGTH;
			}
			if (moveRequest.get(YOU).get(HEALTH).asInt() < length + 3) {
				length += 1;
			}

			int space[][] = new int[11][11];

			for (int x = 0; x < 11; x++) {
				for (int y = 0; y < 11; y++) {
					if (DUO_BOARD[x][y] > length) {
						space[x][y] = 99;
					} else {
						space[x][y] = 0;
					}
				}
			}
			for (final JsonNode bodyPart : moveRequest.get(YOU).get(BODY)) {
				space[bodyPart.get("x").asInt()][bodyPart.get("y").asInt()] = 99;
			}

			final int tailx = moveRequest.get(YOU).withArray(BODY).get(moveRequest.get(YOU).withArray(BODY).size() - 1)
					.get("x").asInt();
			final int taily = moveRequest.get(YOU).withArray(BODY).get(moveRequest.get(YOU).withArray(BODY).size() - 1)
					.get("y").asInt();
			space[tailx][taily] = 5;

			String res = null;
			String maybe = null;
			if (snakex < 10 && res == null) {
				if (space[snakex + 1][snakey] == 0) {
					res = RIGHT;
				} else if (space[snakex + 1][snakey] == 5) {
					maybe = RIGHT;
				}

			}
			if (snakex > 0 && res == null) {
				if (space[snakex - 1][snakey] == 0) {
					res = LEFT;
				} else if (space[snakex - 1][snakey] == 5) {
					maybe = LEFT;
				}
			}
			if (snakey > 0) {
				if (space[snakex][snakey - 1] == 0 && res == null) {
					res = UPWARD;
				} else if (space[snakex][snakey - 1] == 5) {
					maybe = UPWARD;
				}
			}

			if (snakey < 10 && res == null) {
				if (space[snakex][snakey + 1] == 0) {
					res = DOWN;
				} else if (space[snakex][snakey + 1] == 5) {
					maybe = DOWN;
				}
			}

			if (res != null) {
				response.put(MOVESTR, res);
			} else if (maybe != null) {
				response.put(MOVESTR, maybe);
			} else {
				if (snakey > 0) {
					response.put(MOVESTR, UPWARD);
				} else if (snakex > 0) {
					response.put(MOVESTR, LEFT);
				}
			}
		}
		return response;
	}

	private Map<String, String> movePlayer2(final JsonNode moveRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		final int snakex = moveRequest.get(YOU).withArray(BODY).get(0).get("x").asInt();
		final int snakey = moveRequest.get(YOU).withArray(BODY).get(0).get("y").asInt();

		if (snakex == 10 && snakey == 10 && !secondR) {
			startR = true;
		} else if (snakex == 10 && snakey == 10) {
			if (moveRequest.get(BOARD).get("food").size() < 25) {
				secondR = false;
			} else {
				thirdR = true;
			}
		} else if (snakex == 1 && snakey == 0) {
			secondR = true;
		}

		if (!startR || !secondR || !thirdR) {
			if (!startR) {
				if (snakey < 10) {
					response.put(MOVESTR, DOWN);
				} else if (snakex < 10) {
					response.put(MOVESTR, RIGHT);
				}
			} else if (!secondR) {
				if (snakey > 0) {
					response.put(MOVESTR, UPWARD);
				} else if (snakex > 0) {
					response.put(MOVESTR, LEFT);
				}
			} else {
				if (snakey < 10) {
					response.put(MOVESTR, DOWN);
				} else if (snakex < 10) {
					response.put(MOVESTR, RIGHT);
				}
			}
		} else {

			final int turn = moveRequest.get(TURN).asInt();
			if (turn > maxturn) {
				response.put(MOVESTR, UPWARD);
				return response;
			}

			int length = moveRequest.get(YOU).withArray(BODY).size();
			if (length % 2 != 0) {
				length++;
			}
			if (length < DOULENGTH) {
				length = DOULENGTH;
			}
			if (moveRequest.get(YOU).get(HEALTH).asInt() < length + 3) {
				length += 1;
			}

			int space[][] = new int[11][11];

			for (int x = 0; x < 11; x++) {
				for (int y = 0; y < 11; y++) {
					if (DUO_BOARD2[x][y] > length) {
						space[x][y] = 99;
					} else {
						space[x][y] = 0;
					}
				}
			}
			for (final JsonNode bodyPart : moveRequest.get(YOU).get(BODY)) {
				space[bodyPart.get("x").asInt()][bodyPart.get("y").asInt()] = 99;
			}

			final int tailx = moveRequest.get(YOU).withArray(BODY).get(moveRequest.get(YOU).withArray(BODY).size() - 1)
					.get("x").asInt();
			final int taily = moveRequest.get(YOU).withArray(BODY).get(moveRequest.get(YOU).withArray(BODY).size() - 1)
					.get("y").asInt();
			space[tailx][taily] = 5;

			String res = null;
			String maybe = null;
			if (snakey > 0) {
				if (space[snakex][snakey - 1] == 0) {
					res = UPWARD;
				} else if (space[snakex][snakey - 1] == 5) {
					maybe = UPWARD;
				}
			}

			if (snakex > 0 && res == null) {
				if (space[snakex - 1][snakey] == 0) {
					res = LEFT;
				} else if (space[snakex - 1][snakey] == 5) {
					maybe = LEFT;
				}
			}

			if (snakey < 10 && res == null) {
				if (space[snakex][snakey + 1] == 0) {
					res = DOWN;
				} else if (space[snakex][snakey + 1] == 5) {
					maybe = DOWN;
				}
			}

			if (snakex < 10 && res == null) {
				if (space[snakex + 1][snakey] == 0) {
					res = RIGHT;
				} else if (space[snakex + 1][snakey] == 5) {
					maybe = RIGHT;
				}

			}

			if (res != null) {
				response.put(MOVESTR, res);
			} else if (maybe != null) {
				response.put(MOVESTR, maybe);
			} else {
				if (snakey < 10) {
					response.put(MOVESTR, DOWN);
				} else if (snakex < 10) {
					response.put(MOVESTR, RIGHT);
				}
			}
		}
		return response;
	}

	private Map<String, String> soloChallenge(final JsonNode moveRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		final int snakex = moveRequest.get(YOU).withArray(BODY).get(0).get("x").asInt();
		final int snakey = moveRequest.get(YOU).withArray(BODY).get(0).get("y").asInt();

		if (snakex == 0 && snakey == 0) {
			startL = true;
		}

		if (!startL) {
			if (snakey > 0) {
				response.put(MOVESTR, UPWARD);
			} else if (snakex > 0) {
				response.put(MOVESTR, LEFT);
			}
		} else {
			if (pathFollow) {
				return pathMove(moveRequest);
			}
			final int turn = moveRequest.get(TURN).asInt();
			if (turn > maxturn) {
				response.put(MOVESTR, UPWARD);
				return response;
			}
			int length = moveRequest.get(YOU).withArray(BODY).size();
			if (length % 2 != 0) {
				length++;
			}
			if (moveRequest.get(YOU).get(HEALTH).asInt() < length + 3) {
				length += 1;
			}

			int space[][] = new int[7][7];

			for (int x = 0; x < 7; x++) {
				for (int y = 0; y < 7; y++) {
					if (BOARD_ARRAY[x][y] > length) {
						space[x][y] = 99;
					} else {
						space[x][y] = 0;
					}
				}
			}
			for (final JsonNode bodyPart : moveRequest.get(YOU).get(BODY)) {
				space[bodyPart.get("x").asInt()][bodyPart.get("y").asInt()] = 99;
			}

			final int tailx = moveRequest.get(YOU).withArray(BODY).get(moveRequest.get(YOU).withArray(BODY).size() - 1)
					.get("x").asInt();
			final int taily = moveRequest.get(YOU).withArray(BODY).get(moveRequest.get(YOU).withArray(BODY).size() - 1)
					.get("y").asInt();
			space[tailx][taily] = 5;

			String res = null;
			String maybe = null;
			if (snakey > 0) {
				if (space[snakex][snakey - 1] == 0) {
					res = UPWARD;
				} else if (space[snakex][snakey - 1] == 5) {
					maybe = UPWARD;
				}
			}

			if (snakey < 6 && res == null) {
				if (space[snakex][snakey + 1] == 0) {
					res = DOWN;
				} else if (space[snakex][snakey + 1] == 5) {
					maybe = DOWN;
				}
			}

			if (snakex > 0 && res == null) {
				if (space[snakex - 1][snakey] == 0) {
					res = LEFT;
				} else if (space[snakex - 1][snakey] == 5) {
					maybe = LEFT;
				}
			}

			if (snakex < 6 && res == null) {
				if (space[snakex + 1][snakey] == 0) {
					res = RIGHT;
				} else if (space[snakex + 1][snakey] == 5) {
					maybe = RIGHT;
				}

			}

			if (res != null) {
				response.put(MOVESTR, res);
			} else if (maybe != null) {
				response.put(MOVESTR, maybe);
			} else {
				if (snakey > 0) {
					response.put(MOVESTR, UPWARD);
				} else if (snakex > 0) {
					response.put(MOVESTR, LEFT);
				}
			}
		}
		return response;
	}

	private Map<String, String> pathMove(final JsonNode moveRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		final int snakex = moveRequest.get(YOU).withArray(BODY).get(0).get("x").asInt();
		final int snakey = moveRequest.get(YOU).withArray(BODY).get(0).get("y").asInt();

		final int foodSize = moveRequest.get(BOARD).get("food").size();
		if (foodSize == 39 && pathMoveArray[snakex][snakey + 1] == 1 && moveRequest.get(YOU).withArray(BODY).size() == 10) {
			flag = true;
		}

		if (flag) {
			final int snakeId = pathMoveArray[snakex][snakey] + 1;

			if (snakex < 6 && pathMoveArray[snakex + 1][snakey] == snakeId) {
				response.put(MOVESTR, RIGHT);
			} else if (snakex > 0 && pathMoveArray[snakex - 1][snakey] == snakeId) {
				response.put(MOVESTR, LEFT);
			} else if (snakey < 6 && pathMoveArray[snakex][snakey + 1] == snakeId) {
				response.put(MOVESTR, DOWN);
			} else {
				response.put(MOVESTR, UPWARD);
			}
		} else {
			if (snakey == 0 && snakex > 0) {
				response.put(MOVESTR, LEFT);
			} else if (snakey == 0) {
				response.put(MOVESTR, DOWN);
			} else if (snakey == 1 && snakex != 4) {
				response.put(MOVESTR, RIGHT);
			} else {
				response.put(MOVESTR, UPWARD);
			}
		}
		return response;
	}

	/**
	 * This snake was in API v0  so ...  that why
	 * @return snake info
	 */
	@Override
	public Map<String, String> start(final JsonNode startRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("color", "#000000");
		response.put("headType", "shac-gamer");
		response.put("tailType", "shac-coffee");
		return response;
	}

	@Override
	protected void setFileConfig() {
		fileConfig = "Challenger.properties";

	}

	/**
	 * Recursive function to "flood"  value. It's assign the value to the board [ x ] [ y]  then recall this function for adjacent square with the same value
	 * @param posX  the X position
	 * @param posY  the Y position
	 * @param value the value to assign
	 * @param board the board (array of value)
	 */
	private void computeSpace(final int posX, final int posY, final int value, final int[][] board) {
		if (board[posX][posY] >= 0 && space[posX][posY] < 1000) {
			space[posX][posY] = value;
			if (value > 0) {
				if (posX > 0) {
					computeSpace(posX - 1, posY, value, board);
				}
				if (posY > 0) {
					computeSpace(posX, posY - 1, value, board);
				}
				if (posY < heigth - 1) {
					computeSpace(posX, posY + 1, value, board);
				}

				if (posX < width - 1) {
					computeSpace(posX + 1, posY, value, board);
				}
			}
		}
	}

	/**
	 * Recursive function to "flood" positive value. It's assign the value to the board [ x ] [ y]  then recall this function for adjacent square with value - 1
	 * @param posX  the X position
	 * @param posY  the Y position
	 * @param value the value to assign
	 * @param board the board (array of value)
	 */
	private void flood(final int posX, final int posY, final int value, final int[][] board) {
		if (board[posX][posY] >= 0 && board[posX][posY] < value) {
			board[posX][posY] = value > board[posX][posY] ? value : board[posX][posY];
			if (value > 0) {
				if (posX > 0) {
					flood(posX - 1, posY, value - 1, board);
				}
				if (posY > 0) {
					flood(posX, posY - 1, value - 1, board);
				}
				if (posY < heigth - 1) {
					flood(posX, posY + 1, value - 1, board);
				}

				if (posX < width - 1) {
					flood(posX + 1, posY, value - 1, board);
				}
			}
		}
	}

	/**
	 * Scan the space[][]  to count empty square. And put square[][] back to 0 value
	 * why count 5000...   I don't remember
	 * @return the count of empty space
	 */
	private int count5000() {
		int countNb = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < heigth; y++) {

				if (space[x][y] == 5000) {
					countNb++;
				}
				space[x][y] = 0;
			}
		}
		return countNb;
	}
	
	/**
	 * Recursive function to "flood" negative  value. It's assign the value to the board [ x ] [ y]  then recall this function for adjacent square with value - "floodEnemyGap "
	 * @param posX  the X position
	 * @param posY  the Y position
	 * @param value the value to assign
	 * @param board the board (array of value)
	 */
	private void floodEnemy(final int posX, final int posY, final int value, final int[][] board) {
		if (board[posX][posY] > -90) {
			board[posX][posY] = value < board[posX][posY] ? value : board[posX][posY];
			if (value < 0 - FLOODENEMYGAP) {
				if (posX > 0) {
					floodEnemy(posX - 1, posY, value + FLOODENEMYGAP, board);
				}
				if (posY > 0) {
					floodEnemy(posX, posY - 1, value + FLOODENEMYGAP, board);
				}
				if (posY < heigth - 1) {
					floodEnemy(posX, posY + 1, value + FLOODENEMYGAP, board);
				}

				if (posX < width - 1) {
					floodEnemy(posX + 1, posY, value + FLOODENEMYGAP, board);
				}
			}
		}
	}

	/**
	 * Returns snake info neede by battlesnake
	 * @return map of info
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

		} catch (IOException ex) {
			log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());
		}

		return response;
	}

	// If you read everything until this point, I strongly suggest to get
	// "Neuralyzer" by a Men in Black
}
