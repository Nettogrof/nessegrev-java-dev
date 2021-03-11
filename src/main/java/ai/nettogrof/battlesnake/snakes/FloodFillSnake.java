package ai.nettogrof.battlesnake.snakes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class FloodFillSnake extends SnakeAI {

	//static int maxd = 99;
	//static int foodx = 99;
	//static int foody = 99;
	
	int[][] space;
	
	int heigth;
	int width;

	int floodEnemyBigger = -59;
	int floodEnemySmaller = 20;
	int foodValue = 115;
	int floodEnemyGap = 35;
	private int EMPTY = 5000;
	private int apiversion =0;

	public FloodFillSnake() {
		super();
	}

	public FloodFillSnake(Logger l, String gi) {
		super(l, gi);
	
		try (InputStream input = new FileInputStream("FloodFill.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            
           
        	apiversion = Integer.parseInt(prop.getProperty("apiversion"));
    		

            

        } catch (IOException ex) {
            ex.printStackTrace();
        }


	}

	
	/*  Function just to log the winner of the game */
	@Override
	public Map<String, String> end(JsonNode endRequest) {
		Map<String, String> response = new HashMap<>();

		try {
			LOG.info("Winner is : {}", endRequest.get("board").get("snakes").get(0).get("name").asText());
			
		} catch (NullPointerException e) {
			LOG.info("DRAW");
			
		}
		
		return response;
	}

	
	/**
	 * Move function is the main function, 
	 */
	@Override
	public Map<String, String> move(JsonNode moveRequest) {
		width = moveRequest.get("board").get("width").asInt();
		heigth = moveRequest.get("board").get("height").asInt();
		int[][]board = new int[width][heigth];
		
		
		// To determine if the game is under api version 1 or 0 ( default 0)
		if ( moveRequest.get("you").has("head")) {
			apiversion = 1;
		}
		
		//Check if it's a challenge
		boolean challenge = false;
		
		for (int i =0 ; i < moveRequest.get("board").get("snakes").size();i++) {
			for (int j =i+1 ; j < moveRequest.get("board").get("snakes").size();j++) {
				if (moveRequest.get("board").get("snakes").get(i).get("name").asText().equals(moveRequest.get("board").get("snakes").get(j).get("name").asText())) {
					challenge = true;
				}
			}
		}
		
		
		
		Map<String, String> response = new HashMap<>();
		Map<String, Integer> possiblemove = new HashMap<>();
		possiblemove.put("up", 0);
		possiblemove.put("down", 0);
		possiblemove.put("left", 0);
		possiblemove.put("right", 0);

		for (int x = 0; x < moveRequest.get("board").get("width").asInt(); x++) {
			for (int y = 0; y < moveRequest.get("board").get("height").asInt(); y++) {
				board[x][y] = 0;
				space[x][y] = 0;
				

			}
		}
		String name = moveRequest.get("you").get("name").asText();
		int mysnakelength = moveRequest.get("you").get("body").size();
		int turn = moveRequest.get("turn").asInt();
		int nbsnake = moveRequest.get("board").get("snakes").size();
		
		
		moveRequest.get("board").withArray("snakes").forEach(s -> {  //Foreach snake
			int enemylength = s.get("body").size();
			if (!s.get("name").asText().equals(name) && enemylength >= mysnakelength) {
				floodNegative(s.get("body").get(0).get("x").asInt(), s.get("body").get(0).get("y").asInt(),
						floodEnemyBigger,board);  //Floodfill negative value from enemy snake head if he's bigger ( to avoid to lost head-to-head)
				
			} else if (!s.get("name").asText().equals(name)) {
				floodPositive(s.get("body").get(0).get("x").asInt(), s.get("body").get(0).get("y").asInt(), floodEnemySmaller,board);// Floodfill positive value from enemy snake head if he's smaller ( to try to kill it)
				
			}else if (s.get("name").asText().equals(name) && !s.get("id").asText().equals(moveRequest.get("you").get("id").asText())) {
				floodNegative(s.get("body").get(0).get("x").asInt(), s.get("body").get(0).get("y").asInt(),
						floodEnemyBigger,board);  //floodfill negative value if the other snake have the same name  ( for challenge only) 
			}

			s.withArray("body").forEach(c -> {  // For each snake body part , put -99  to avoid to move into a snake body

				board[c.get("x").asInt()][c.get("y").asInt()] = -99;
				
			});

			if (s.get("health").asInt() < 100 && turn > 3) {
				board[s.get("body").get(enemylength - 1).get("x").asInt()][s.get("body").get(enemylength - 1).get("y")
						.asInt()] = 0;  // Put value = 0 for tail,  because usually you can move on tail  ( I didn't check if the snake have eaten or not)
				
			}

		});
		

		int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
		int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
		
		if (challenge) {  // for challenge only
			blockSide(snakex,board);
		}
		

		int health = moveRequest.get("you").get("health").asInt();
		
		moveRequest.get("board").withArray("food").forEach(f -> {
			if (health < 10 || nbsnake >1) {
				floodPositive(f.get("x").asInt(), f.get("y").asInt(), foodValue - health,board); // Floodfill positive value for food (  if snake is more hungry the value is higher)
			}else {
				floodNegative(f.get("x").asInt(), f.get("y").asInt(),
						floodEnemyBigger,board);  // For challenge, floodfill negative value if my snake have more than 10 health
			}
			
		});
		
		moveRequest.get("board").withArray("hazards").forEach(f -> { // For BattleRoyale only remove -25 for square in hazard
			board[f.get("x").asInt()][f.get("y").asInt()] -= 25;
			
		});

		double result[] = { 0.5, 0.5, 0.5, 0.5 };
		if (snakey == 0) {
			possiblemove.put("up", -90);
			result[0] = 0.0;
		} else {
			floodEmptySpace(snakex, snakey - 1, EMPTY,board);
			possiblemove.put("up", possiblemove.get("up") + board[snakex][snakey - 1] + countEmptySquare());
		}

		if (snakey == heigth - 1) {
			possiblemove.put("down", -90);
			result[1] = 0.0;
		} else {
			floodEmptySpace(snakex, snakey + 1, EMPTY,board);
			possiblemove.put("down", possiblemove.get("down") + board[snakex][snakey + 1] + countEmptySquare());
		}

		if (snakex == 0) {
			possiblemove.put("left", -90);
			result[2] = 0.0;
		} else {
			floodEmptySpace(snakex - 1, snakey, EMPTY,board);
			possiblemove.put("left", possiblemove.get("left") + board[snakex - 1][snakey] + countEmptySquare());
		}

		if (snakex == width - 1) {
			possiblemove.put("right", -90);
			result[3] = 0.0;
		} else {
			floodEmptySpace(snakex + 1, snakey, EMPTY,board);
			possiblemove.put("right", possiblemove.get("right") + board[snakex + 1][snakey] + countEmptySquare());
		}

		
		String res = "up";
		int value = possiblemove.get("up");

		if (possiblemove.get("down") > value) {
			value = possiblemove.get("down");
			res = "down";
		}

		if (possiblemove.get("left") > value) {
			value = possiblemove.get("left");
			res = "left";
		}
		if (possiblemove.get("right") > value) {
			value = possiblemove.get("right");
			res = "right";
		}
		if (apiversion  == 1) {
			if (res.equals("up")) {
				res = "down";
			}else if (res.equals("down")) {
				res = "up";
			}
		}
			

		response.put("move", res);

		return response;
	}

	
	/**
	 * Use in a challenge context, to limit my snake on one side of the board
	 * @param snakex
	 * @param board
	 */
	private void blockSide(int snakex, int[][] board) {
		if (snakex<6) {
			for (int i =0 ; i < 11; i++) {
			board[6][i]= -99;
			}
		}else {
			for (int i =0 ; i < 11; i++) {
				board[5][i]= -99;
				}
		}
		
	}

	
	/**
	 *  Function when receive the start request ( API version 0 )
	 *  
	 *  Shouldn't be used anymore
	 * 
	 */
	@Override
	public Map<String, String> start(JsonNode startRequest) {
		Map<String, String> response = new HashMap<>();
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
	 * @param x
	 * @param y
	 * @param value
	 * @param board
	 */
	private void floodPositive(int x, int y, int value,int[][] board) {
		if (board[x][y] >= 0 && board[x][y] < value) {  // Only on positive square value  and where the current value is lower than the value we want to assign 
			board[x][y] = value ;
			if (value > 0) {   // Stop here if value is equal to 0 
				if (x > 0) {
					floodPositive(x - 1, y, value - 1,board);  //spread the flood to the position X - 1, with the value - 1 
				}
				if (y > 0) {
					floodPositive(x, y - 1, value - 1,board); //spread the flood to the position Y - 1, with the value - 1 
				}
				if (y < heigth - 1) {
					floodPositive(x, y + 1, value - 1,board);//spread the flood to the position Y - 1, with the value - 1 
				}

				if (x < width - 1) {
					floodPositive(x + 1, y, value - 1,board); //spread the flood to the position X + 1, with the value - 1 
				}
			}
		}
	}

	
	/**
	 * Recursive function to "flood" negative  value. It's assign the value to the board [ x ] [ y]  then recall this function for adjacent square with value - "floodEnemyGap "
	 * @param x
	 * @param y
	 * @param value
	 * @param board
	 */
	private void floodNegative(int x, int y, int value,int[][] board) {
		if (board[x][y] > -90) {
			board[x][y] = (value < board[x][y] ? value : board[x][y]);
			if (value < 0 - floodEnemyGap) {
				if (x > 0) {
					floodNegative(x - 1, y, value + floodEnemyGap,board);
				}
				if (y > 0) {
					floodNegative(x, y - 1, value + floodEnemyGap,board);
				}
				if (y < heigth - 1) {
					floodNegative(x, y + 1, value + floodEnemyGap,board);
				}

				if (x < width - 1) {
					floodNegative(x + 1, y, value + floodEnemyGap,board);
				}
			}
		}
	}

	/**
	 * Recursive function to "flood" EMPTY value on positive square. It's assign the EMPTY to the space [ x ] [ y]  then recall this function for adjacent square with EMPTY
	 * @param x
	 * @param y
	 * @param value
	 * @param board
	 */
	private void floodEmptySpace(int x, int y, int value,int[][] board) {
		if (board[x][y] >= 0 && space[x][y] < 1000) {
			space[x][y] = value;
			if (value > 0) {
				if (x > 0) {
					floodEmptySpace(x - 1, y, value,board);
				}
				if (y > 0) {
					floodEmptySpace(x, y - 1, value,board);
				}
				if (y < heigth - 1) {
					floodEmptySpace(x, y + 1, value,board);
				}

				if (x < width - 1) {
					floodEmptySpace(x + 1, y, value,board);
				}
			}
		}
	}

	
	/**
	 * Scan the space[][]  to count empty square. And put square[][] back to 0 value
	 * @return
	 */
	private int countEmptySquare() {
		int c = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < heigth; y++) {

				if (space[x][y] == EMPTY) {
					c++;
				}
				space[x][y] = 0;
			}
		}
		return c;
	}

	
	/**
	 * Java main function used only for debugging 
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		ObjectMapper JSON_MAPPER = new ObjectMapper();
		String test = "{\"game\":{\"id\":\"a9d04cd2-344b-46a5-bd54-e8e7f2946155\"},\"turn\":1,\"board\":{\"height\":15,\"width\":15,\"food\":[{\"x\":2,\"y\":11}],\"snakes\":[{\"id\":\"e1e5b6d8-7451-41bf-bfa9-bd1241b64def\",\"name\":\"Basic\",\"health\":99,\"body\":[{\"x\":6,\"y\":13},{\"x\":7,\"y\":13},{\"x\":7,\"y\":13}]},{\"id\":\"7d7d1082-b640-4732-94d8-8dd10a49e681\",\"name\":\"Flood\",\"health\":99,\"body\":[{\"x\":6,\"y\":1},{\"x\":6,\"y\":0},{\"x\":6,\"y\":0}]}]},\"you\":{\"id\":\"7d7d1082-b640-4732-94d8-8dd10a49e681\",\"name\":\"Flood\",\"health\":99,\"body\":[{\"x\":6,\"y\":1},{\"x\":6,\"y\":0},{\"x\":6,\"y\":0}]}}";
		try {
			JsonNode parsedRequest = JSON_MAPPER.readTree(test);
			int size = parsedRequest.get("board").get("height").asInt();
			FloodFillSnake t = new FloodFillSnake();
			//t.board = new int[size][size];
			t.space = new int[size][size];
			t.heigth = size;
			t.width = size;
			t.move(parsedRequest);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	
	
	/**
	 * Function used to retrieve snake info ( api version, color, snake head, etc)
	 * @return  HashMap of properties
	 */
	public static Map<String, String> getInfo() {
		Map<String, String> response = new HashMap<>();
		try (InputStream input = new FileInputStream("FloodFill.properties")) {

            Properties prop = new Properties();

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
