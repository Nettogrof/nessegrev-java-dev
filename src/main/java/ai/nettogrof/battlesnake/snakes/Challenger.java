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
 * thinking. Please save yourself and don't look at this code If you do, you'll
 * may find funny comments, and crazy code...
 * 
 * @deprecated Old apiv0 and badly coded
 * @author carl.lajeunesse
 * @version Summer 2020
 */
@Deprecated
public final class Challenger extends AbstractSnakeAI {
	/**
	 * This is the path that challenger use in solo challenge. always trying to stay
	 * in square smaller than its length
	 */
	private static final int BOARD_ARRAY[][] = { { 4, 4, 44, 38, 36, 33, 34 }, { 4, 4, 43, 37, 35, 31, 32 },
			{ 6, 5, 49, 47, 48, 29, 30 }, { 8, 7, 39, 41, 45, 27, 28 }, { 10, 9, 40, 42, 46, 25, 26 },
			{ 12, 11, 15, 17, 19, 21, 23 }, { 14, 13, 16, 18, 20, 22, 24 } };

	/**
	 * Use board path to keep four snake alive. Why did I did that...
	 */
	private static final int FOURALIVE[][] = { { 0, 1, 2, 3 }, { 43, 6, 5, 4 }, { 42, 7, 8, 9 }, { 41, 12, 11, 10 },
			{ 40, 13, 14, 15 }, { 39, 18, 17, 16 }, { 38, 19, 20, 21 }, { 37, 24, 23, 22 }, { 36, 25, 26, 27 },
			{ 35, 32, 31, 28 }, { 34, 33, 30, 29 }

	};
	/**
	 * Use in the floodfill method.
	 */
	private static final int FLOODENEMYGAP = 25;

	/**
	 * board path use in duo mode for the 1st snake
	 */
	private static final int DUO_BOARD[][] = { { 6, 6, 29, 31, 33, 56, 99, 99, 99, 99, 99 },
			{ 6, 6, 30, 32, 34, 57, 99, 99, 99, 99, 99 }, { 6, 6, 54, 38, 37, 56, 99, 99, 99, 99, 99 },
			{ 8, 7, 53, 42, 41, 57, 99, 99, 99, 99, 99 }, { 10, 9, 49, 47, 48, 56, 99, 99, 99, 99, 99 },
			{ 12, 11, 50, 45, 46, 57, 99, 99, 99, 99, 99 }, { 14, 13, 52, 43, 44, 56, 99, 99, 99, 99, 99 },
			{ 16, 15, 51, 39, 40, 57, 99, 99, 99, 99, 99 }, { 18, 17, 55, 35, 36, 56, 99, 99, 99, 99, 99 },
			{ 20, 19, 23, 25, 27, 57, 99, 99, 99, 99, 99 }, { 22, 21, 24, 26, 28, 56, 99, 99, 99, 99, 99 } };
	/**
	 * board path use in duo mode for the 2nd snake, but doesn't seem right... yep I
	 * often copy-paste...
	 */
	private static final int DUO_BOARD2[][] = { { 6, 6, 29, 31, 99, 56, 27, 28, 29, 31, 33 },
			{ 6, 6, 30, 32, 99, 57, 25, 26, 30, 32, 34 }, { 6, 6, 54, 38, 99, 56, 23, 24, 49, 36, 35 },
			{ 8, 7, 53, 42, 99, 57, 21, 22, 50, 38, 37 }, { 10, 9, 49, 47, 99, 56, 19, 20, 51, 40, 39 },
			{ 12, 11, 50, 45, 99, 57, 17, 18, 52, 42, 41 }, { 14, 13, 52, 43, 99, 56, 15, 16, 53, 44, 43 },
			{ 16, 15, 51, 39, 99, 57, 13, 14, 54, 46, 45 }, { 18, 17, 55, 35, 99, 56, 11, 12, 55, 48, 49 },
			{ 20, 19, 23, 25, 99, 57, 10, 8, 6, 6, 6 }, { 22, 21, 24, 26, 99, 56, 9, 7, 6, 6, 6 } };

	/**
	 * Look like board_array[][] but minus 4 why I don't know... I'm already lost.
	 */
	private static final int pathMoveArray[][] = { { 0, 0, 4, 5, 6, 7, 8 }, { 0, 0, 3, 12, 11, 10, 9 },
			{ 0, 0, 2, 13, 14, 15, 16 }, { 0, 0, 1, 26, 25, 24, 17 }, { 0, 39, 40, 27, 28, 23, 18 },
			{ 37, 38, 33, 32, 29, 22, 19 }, { 36, 35, 34, 31, 30, 21, 20 } };
	/**
	 * in duo mode, snakes was dying quick, so instead of changing the code or the
	 * behavior, this variable is used just to lie the length of snake to itself.
	 */
	private static final int DOULENGTH = 14;

	/**
	 * I think it was to check if the snake have reach top-left corner once
	 */
	private transient boolean startL;

	/**
	 * I think it was to check if the second snake have reach top-left corner once
	 */
	private transient boolean startR;

	/**
	 * I think I was messing around
	 */
	private transient boolean secondL;
	/**
	 * I think clearly that is not longer comprehensive
	 */
	private transient boolean secondR;
	/**
	 * A third time ?! What the f....
	 */
	private transient boolean thirdL;

	/**
	 * Ok I surrender... I'm going to asylum
	 */
	private transient boolean thirdR;

	/**
	 * boolean to check path follow ( I know that does't help to understand, but I
	 * can't do better)
	 */
	private transient boolean pathFollow;
	/**
	 * a flag that check if the board was full of food
	 */
	private transient boolean flag;

	/**
	 * is it the mirror Challenge
	 */
	private transient boolean mirrorChallenge;
	/**
	 * Triple snakes !!
	 */
	private transient boolean battleTriple;

	/**
	 * Keep all four snake alive challenge
	 */
	private transient boolean fourSnakeAlive;

	/**
	 * Oh that one I know !! the height of the board... (yes I know the typo)
	 */
	private transient int height;

	/**
	 * Another easy one , the width of the board
	 */
	private transient int width;

	/**
	 * Board array of free space...
	 */
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
			prop.load(input);

			pathFollow = Boolean.parseBoolean(prop.getProperty("path"));

		} catch (IOException ex) {
			log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());
		}
	}

	@Override
	public Map<String, String> move(final JsonNode moveRequest) {

		// Trying to figure which challenge it is and choose the right method
		if (moveRequest.get(BOARD).get(SNAKES).size() == 1) {
			
			if (moveRequest.get(BOARD).get(BODY).asInt() == 7) {
				return soloChallenge(moveRequest);
			} else {
				return fourCornerChallenge(moveRequest);
			}
		} else {
			return otherChallenge(moveRequest);
			
		}
	}

	/**
	 * Other challenge aka  not solo...
	 * @param moveRequest
	 * @return the battlesnake response
	 */
	private Map<String, String> otherChallenge(final JsonNode moveRequest) {
		
		if (mirrorChallenge) {
			return duoChallenge(moveRequest);
		} else if (battleTriple) {
			return tripleChallenge(moveRequest);
		} else if (fourSnakeAlive) {
			return keepFourSnakeAliveChallenge(moveRequest);
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
				return duoChallenge(moveRequest);
			} else if (count == 3) {
				battleTriple = true;
				return tripleChallenge(moveRequest);
			} else {
				fourSnakeAlive = true;
				return keepFourSnakeAliveChallenge(moveRequest);
			}
		}
		
	}

	/**
	 * Keep 4 snakes live challenge
	 * 
	 * @param moveRequest Move request
	 * @return map response for battlesnake
	 */
	private Map<String, String> keepFourSnakeAliveChallenge(final JsonNode moveRequest) {
		width = moveRequest.get(BOARD).get(BODY).asInt();
		height = moveRequest.get(BOARD).get(HEIGHT_FIELD).asInt();

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

	/**
	 * 4 corner challenge
	 * 
	 * @param moveRequest Move request
	 * @return map response for battlesnake
	 */
	private Map<String, String> fourCornerChallenge(final JsonNode moveRequest) {
		width = moveRequest.get(BOARD).get(BODY).asInt();
		height = moveRequest.get(BOARD).get(HEIGHT_FIELD).asInt();
		int[][] board = new int[width][height];
		space = new int[width][height];

		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		
		possiblemove.put(UPWARD, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
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
		final int health = moveRequest.get(YOU).get(HEALTH).asInt();
		if (moveRequest.get(YOU).get("length").asInt() < 70) {
			moveRequest.get(BOARD).withArray("food").forEach(f -> {

				flood(f.get("x").asInt(), f.get("y").asInt(), 100 - health, board);

			});
		}

		flood(0, 0, health, board);
		flood(width - 1, 0, health, board);
		flood(0, height - 1, health, board);
		flood(width - 1, height - 1, health, board);
		
		if (board[0][0] == -99 && board[0][18] == -99 && board[18][0] == -99 && board[18][18] == -99) {
			if (snakey == 0) {
				response.put(MOVESTR, UPWARD);
			} else if (snakey == 18) {
				response.put(MOVESTR, DOWN);
			} else if (snakex == 0) {
				response.put(MOVESTR, LEFT);
			} else if (snakex == 18) {
				response.put(MOVESTR, RIGHT);
			}
		} else {		
			response.put(MOVESTR, getResponseString(possiblemove,snakex,snakey,board));
		}
		return response;
	}

	/**
	 * Keep 3 snakes live challenge, yes I kill the 3rd snakes. Don't ask why...
	 * 
	 * @param moveRequest Move request
	 * @return map response for battlesnake
	 */
	private Map<String, String> tripleChallenge(final JsonNode moveRequest) {
		Map<String, String> response = new ConcurrentHashMap<>();
		if (moveRequest.get(BOARD).get(SNAKES).get(0).get("id").asText()
				.equals(moveRequest.get(YOU).get("id").asText())) {
			response = moveTriplePlayer(moveRequest,5,5);
		} else if (moveRequest.get(BOARD).get(SNAKES).get(1).get("id").asText()
				.equals(moveRequest.get(YOU).get("id").asText())) {
			response = moveTriplePlayer(moveRequest,6,6);
		} else {
			response.put(MOVESTR, UPWARD);
		}
		return response;
	}

	/**
	 * Keep 3 snakes live challenge , player 2 move
	 * 
	 * @param moveRequest Move request
	 * @param floodx    x position for the floodfill
	 * @param floody    y position for the floodfill
	 * @return map response for battlesnake
	 */
	private Map<String, String> moveTriplePlayer(final JsonNode moveRequest , final int floodx, final int floody) {
		width = moveRequest.get(BOARD).get(WIDTH_FIELD).asInt();
		height = moveRequest.get(BOARD).get(HEIGHT_FIELD).asInt();
		int[][] board = new int[width][height];
		space = new int[width][height];

		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		response.put("shout", moveRequest.get(YOU).get("id").asText());
		possiblemove.put(UPWARD, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
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

		flood(floodx, floody, health / 4, board);

		response.put(MOVESTR, getResponseString(possiblemove,snakex,snakey,board));

		return response;
	}

	

	/**
	 * Generate the move reponse....
	 * @param possiblemove  possible move value
	 * @param snakex  snake head x 
	 * @param snakey snake head y
	 * @param board  board array
	 * @return  move string
 	 */
	private String getResponseString(final Map<String, Integer> possiblemove,final int snakex,final int snakey,final int[][] board) {
		String res=UPWARD;

		if (snakey == 0) {
			possiblemove.put(UPWARD, -90);

		} else {
			computeSpace(snakex, snakey - 1, 5000, board);
			possiblemove.put(UPWARD, possiblemove.get(UPWARD) + board[snakex][snakey - 1] + count5000());
		}

		if (snakey == height - 1) {
			possiblemove.put(DOWN, -90);

		} else {
			computeSpace(snakex, snakey + 1, 5000, board);
			possiblemove.put(DOWN, possiblemove.get(DOWN) + board[snakex][snakey + 1] + count5000());
		}

		if (snakex == 0) {
			possiblemove.put(LEFT, -90);

		} else {
			computeSpace(snakex - 1, snakey, 5000, board);
			possiblemove.put(LEFT, possiblemove.get(LEFT) + board[snakex - 1][snakey] + count5000());
		}

		if (snakex == width - 1) {
			possiblemove.put(RIGHT, -90);

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
		return res;
	}

	/**
	 * Duo challenge
	 * 
	 * @param moveRequest Move request
	 * @return map response for battlesnake
	 */
	private Map<String, String> duoChallenge(final JsonNode moveRequest) {
		Map<String, String> response;
		String id1 = moveRequest.get(BOARD).get(SNAKES).get(0).get("id").asText();
		final String id2 = moveRequest.get(BOARD).get(SNAKES).get(1).get("id").asText();

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

	/**
	 * Duo challenge player 1 (Mario !!)
	 * 
	 * @param moveRequest Move request
	 * @return map response for battlesnake
	 */
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
			} else if (secondL) {
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

			if (res == null) {
				if (maybe == null){
					if (snakey > 0) {
						response.put(MOVESTR, UPWARD);
					} else if (snakex > 0) {
						response.put(MOVESTR, LEFT);
					}
				}else{
					response.put(MOVESTR, maybe);
				}
			} else {

				response.put(MOVESTR, res);
				
			}
		}
		return response;
	}

	/**
	 * Duo challenge player 2 ( Luigi !!)
	 * 
	 * @param moveRequest Move request
	 * @return map response for battlesnake
	 */
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
			} else if (secondR) {
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

	/**
	 * All by myself!! -Celine Dion
	 * 
	 * @param moveRequest Move request
	 * @return map response for battlesnake
	 */
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

			if (res == null) {
				if (maybe == null){
					if (snakey > 0) {
						response.put(MOVESTR, UPWARD);
					} else if (snakex > 0) {
						response.put(MOVESTR, LEFT);
					}
				}else{
					response.put(MOVESTR, maybe);
				}
			} else {
				response.put(MOVESTR, res);
				
			}
		}
		return response;
	}

	/**
	 * Path move... also as clear as move path
	 * 
	 * @param moveRequest Move request
	 * @return map response for battlesnake
	 */
	private Map<String, String> pathMove(final JsonNode moveRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		final int snakex = moveRequest.get(YOU).withArray(BODY).get(0).get("x").asInt();
		final int snakey = moveRequest.get(YOU).withArray(BODY).get(0).get("y").asInt();

		final int foodSize = moveRequest.get(BOARD).get("food").size();
		if (foodSize == 39 && pathMoveArray[snakex][snakey + 1] == 1
				&& moveRequest.get(YOU).withArray(BODY).size() == 10) {
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
	 * This snake was in API v0 so ... that why
	 * 
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
	 * Recursive function to "flood" value. It's assign the value to the board [ x ]
	 * [ y] then recall this function for adjacent square with the same value
	 * 
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
				if (posY < height - 1) {
					computeSpace(posX, posY + 1, value, board);
				}

				if (posX < width - 1) {
					computeSpace(posX + 1, posY, value, board);
				}
			}
		}
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
				if (posY < height - 1) {
					flood(posX, posY + 1, value - 1, board);
				}

				if (posX < width - 1) {
					flood(posX + 1, posY, value - 1, board);
				}
			}
		}
	}

	/**
	 * Scan the space[][] to count empty square. And put square[][] back to 0 value
	 * why count 5000... I don't remember
	 * 
	 * @return the count of empty space
	 */
	private int count5000() {
		int countNb = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				if (space[x][y] == 5000) {
					countNb++;
				}
				space[x][y] = 0;
			}
		}
		return countNb;
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
				if (posY < height - 1) {
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
	 * 
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
