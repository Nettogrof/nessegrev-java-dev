package ai.nettogrof.battlesnake.snakes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;

public class FloodFillSnake extends AbstractSnakeAI {

	
	private transient int[][] space;
	
	private transient int heigth;
	private transient int width;

	private final static int FLOODENEMYBIGGER = -59;
	private final static int FLODDENEMYSMALLER = 20;
	private final static int FOODVALUE = 115;
	private final static int FLODDENEMYGAP = 35;
	private final static int EMPTY = 5000;
	private transient int apiversion;

	public FloodFillSnake() {
		super();
	}

	public FloodFillSnake(final String gameId) {
		super(gameId);
	
		try (InputStream input =  Files.newInputStream(Paths.get("FloodFill.properties"))) {

            final Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            
           
        	apiversion = Integer.parseInt(prop.getProperty("apiversion"));
    		

            

        } catch (IOException ex) {
            ex.printStackTrace();
        }


	}

	
	/**
	 * Move function is the main function, 
	 */
	@Override
	public Map<String, String> move(final JsonNode moveRequest) {
		final JsonNode boardJson = moveRequest.get("board");
		width = boardJson.get("width").asInt();
		heigth = boardJson.get("height").asInt();
		int[][]board = new int[width][heigth];
		final JsonNode you = moveRequest.get("you");
		
		
		// To determine if the game is under api version 1 or 0 ( default 0)
		if ( you.has("head")) {
			apiversion = 1;
		}
		
	
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < heigth; y++) {
				board[x][y] = 0;
				space[x][y] = 0;
				

			}
		}
		final String yourName = you.get("name").asText();
		final int mysnakelength = you.get(BODY).size();
		
		
		boardJson.withArray("snakes").forEach(s -> {  //Foreach snake
			final JsonNode snakeBody =  s.get(BODY);
			final int enemylength = snakeBody.size();
			final String snakeName = s.get("name").asText();
			final boolean isYourSnake = snakeName.equals(yourName);
			
			if (!isYourSnake && enemylength >= mysnakelength) {
				floodNegative(snakeBody.get(0).get("x").asInt(), snakeBody.get(0).get("y").asInt(),
						FLOODENEMYBIGGER,board);  //Floodfill negative value from enemy snake head if he's bigger ( to avoid to lost head-to-head)
				
			} else if (!isYourSnake) {
				floodPositive(snakeBody.get(0).get("x").asInt(), snakeBody.get(0).get("y").asInt(), FLODDENEMYSMALLER,board);// Floodfill positive value from enemy snake head if he's smaller ( to try to kill it)
				
			}

			s.withArray(BODY).forEach(c -> {  // For each snake body part , put -99  to avoid to move into a snake body

				board[c.get("x").asInt()][c.get("y").asInt()] = -99;
				
			});

			if (s.get("health").asInt() < 100 && moveRequest.get("turn").asInt() > 3) {
				board[snakeBody.get(enemylength - 1).get("x").asInt()][snakeBody.get(enemylength - 1).get("y").asInt()] = 0;  // Put value = 0 for tail,  because usually you can move on tail  ( I didn't check if the snake have eaten or not)
			}

		});
		
		addFoodValue(board,you, boardJson);
		addHazardsValue(board,boardJson);
		
		
		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("move", chooseBestMove(checkPossibleMove(you,board)));

		return response;
	}

		
	private void addHazardsValue(final int[][] board,final JsonNode boardJson) {
		boardJson.withArray("hazards").forEach(f -> { // For BattleRoyale only remove -25 for square in hazard
			board[f.get("x").asInt()][f.get("y").asInt()] -= 25;
			
		});
		
	}

	private void addFoodValue(final int[][] board,final JsonNode you,final JsonNode boardJson) {

		final int health = you.get("health").asInt();
		final int nbsnake = boardJson.get("snakes").size();
		boardJson.withArray("food").forEach(f -> {
			if (health < 10 || nbsnake >1) {
				floodPositive(f.get("x").asInt(), f.get("y").asInt(), FOODVALUE - health,board); // Floodfill positive value for food (  if snake is more hungry the value is higher)
			}else {
				floodNegative(f.get("x").asInt(), f.get("y").asInt(),
						FLOODENEMYBIGGER,board);  // For challenge, floodfill negative value if my snake have more than 10 health
			}
			
		});
		
	}

	private Map<String, Integer> checkPossibleMove(final JsonNode you,final int[][] board) {
		final int snakex = you.withArray(BODY).get(0).get("x").asInt();
		final int snakey = you.withArray(BODY).get(0).get("y").asInt();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		possiblemove.put(UPWARD, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);
		
		if (snakey == 0) {
			possiblemove.put(UPWARD, -90);
		
		} else {
			floodEmptySpace(snakex, snakey - 1, EMPTY,board);
			possiblemove.put(UPWARD, possiblemove.get(UPWARD) + board[snakex][snakey - 1] + countEmptySquare());
		}

		if (snakey == heigth - 1) {
			possiblemove.put(DOWN, -90);
			
		} else {
			floodEmptySpace(snakex, snakey + 1, EMPTY,board);
			possiblemove.put(DOWN, possiblemove.get(DOWN) + board[snakex][snakey + 1] + countEmptySquare());
		}

		if (snakex == 0) {
			possiblemove.put(LEFT, -90);
			
		} else {
			floodEmptySpace(snakex - 1, snakey, EMPTY,board);
			possiblemove.put(LEFT, possiblemove.get(LEFT) + board[snakex - 1][snakey] + countEmptySquare());
		}

		if (snakex == width - 1) {
			possiblemove.put(RIGHT, -90);
			
		} else {
			floodEmptySpace(snakex + 1, snakey, EMPTY,board);
			possiblemove.put(RIGHT, possiblemove.get(RIGHT) + board[snakex + 1][snakey] + countEmptySquare());
		}
		return possiblemove;
	}

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
		if (apiversion  == 1) {
			if (res.equals(UPWARD)) {
				res = DOWN;
			}else if (res.equals(DOWN)) {
				res = UPWARD;
			}
		}
		return res;
	}

	/**
	 *  Function when receive the start request ( API version 0 )
	 *  
	 *  Shouldn't be used anymore
	 * 
	 */
	@Override
	public Map<String, String> start(final JsonNode startRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("color", "#FF0000");
		response.put("headType", "sand-worm");
		response.put("tailType", "sharp");
		width = startRequest.get("board").get("width").asInt();
		heigth = startRequest.get("board").get("height").asInt();
		//board = new int[width][heigth];
		space = new int[width][heigth];
		
		return response;
	}

	/**
	 * Recursive function to "flood" positive value. It's assign the value to the board [ x ] [ y]  then recall this function for adjacent square with value - 1
	 * @param posX
	 * @param posY
	 * @param value
	 * @param board
	 */
	private void floodPositive(final int posX,final int posY,final int value,final int[][] board) {
		if (board[posX][posY] >= 0 && board[posX][posY] < value) {  // Only on positive square value  and where the current value is lower than the value we want to assign 
			board[posX][posY] = value ;
			if (value > 0) {   // Stop here if value is equal to 0 
				if (posX > 0) {
					floodPositive(posX - 1, posY, value - 1,board);  //spread the flood to the position X - 1, with the value - 1 
				}
				if (posY > 0) {
					floodPositive(posX, posY - 1, value - 1,board); //spread the flood to the position Y - 1, with the value - 1 
				}
				if (posY < heigth - 1) {
					floodPositive(posX, posY + 1, value - 1,board);//spread the flood to the position Y - 1, with the value - 1 
				}

				if (posX < width - 1) {
					floodPositive(posX + 1, posY, value - 1,board); //spread the flood to the position X + 1, with the value - 1 
				}
			}
		}
	}

	
	/**
	 * Recursive function to "flood" negative  value. It's assign the value to the board [ x ] [ y]  then recall this function for adjacent square with value - "floodEnemyGap "
	 * @param posX
	 * @param posY
	 * @param value
	 * @param board
	 */
	private void floodNegative(final int posX,final int posY,final int value,final int[][] board) {
		if (board[posX][posY] > -90) {
			board[posX][posY] = value < board[posX][posY] ? value : board[posX][posY];
			if (value < 0 - FLODDENEMYGAP) {
				if (posX > 0) {
					floodNegative(posX - 1, posY, value + FLODDENEMYGAP,board);
				}
				if (posY > 0) {
					floodNegative(posX, posY - 1, value + FLODDENEMYGAP,board);
				}
				if (posY < heigth - 1) {
					floodNegative(posX, posY + 1, value + FLODDENEMYGAP,board);
				}

				if (posX < width - 1) {
					floodNegative(posX + 1, posY, value + FLODDENEMYGAP,board);
				}
			}
		}
	}

	/**
	 * Recursive function to "flood" EMPTY value on positive square. It's assign the EMPTY to the space [ x ] [ y]  then recall this function for adjacent square with EMPTY
	 * @param posX
	 * @param posY
	 * @param value
	 * @param board
	 */
	private void floodEmptySpace(final int posX,final int posY,final int value,final int[][] board) {
		if (board[posX][posY] >= 0 && space[posX][posY] < 1000) {
			space[posX][posY] = value;
			if (value > 0) {
				if (posX > 0) {
					floodEmptySpace(posX - 1, posY, value,board);
				}
				if (posY > 0) {
					floodEmptySpace(posX, posY - 1, value,board);
				}
				if (posY < heigth - 1) {
					floodEmptySpace(posX, posY + 1, value,board);
				}

				if (posX < width - 1) {
					floodEmptySpace(posX + 1, posY, value,board);
				}
			}
		}
	}

	
	/**
	 * Scan the space[][]  to count empty square. And put square[][] back to 0 value
	 * @return
	 */
	private int countEmptySquare() {
		int countEmpty = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < heigth; y++) {

				if (space[x][y] == EMPTY) {
					countEmpty++;
				}
				space[x][y] = 0;
			}
		}
		return countEmpty;
	}

		
	
	/**
	 * Function used to retrieve snake info ( api version, color, snake head, etc)
	 * @return  HashMap of properties
	 */
	public static Map<String, String> getInfo() {
		final Map<String, String> response = new ConcurrentHashMap<>();
		try (InputStream input =  Files.newInputStream(Paths.get("FloodFill.properties"))) {

            final Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            
           
        	response.put("apiversion", prop.getProperty("apiversion"));
    		response.put("head",  prop.getProperty("headType"));
    		response.put("tail",  prop.getProperty("tailType"));
    		response.put("color",  prop.getProperty("color"));
    		response.put("author", "nettogrof");

            

        } catch (IOException ex) {
            ex.printStackTrace();
        }

		return response;
	}

	/**
	 * Not used
	 */
	@Override
	protected void setFileConfig() {
		fileConfig="FloodFill.properties";
		
	}

	

}
