package ai.nettogrof.battlesnake.snakes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


import com.fasterxml.jackson.databind.JsonNode;

public final class Challenger extends AbstractSnakeAI {
	private transient int maxturn = 9_999_999;
	
	
	private static final int BOARD[][] = 
			{ { 4, 4, 44, 38, 36, 33, 34 },
			{ 4, 4, 43, 37, 35, 31, 32 },
			{ 6, 5, 49, 47, 48, 29, 30 },
			{ 8, 7, 39, 41, 45, 27, 28 },
			{ 10, 9, 40, 42, 46, 25, 26 },
			{ 12, 11, 15, 17, 19, 21, 23 },
			{ 14, 13, 16, 18, 20, 22, 24 } };
	
	private static final int FOURALIVE[][] = {
			{0,1,2,3},
			{43,6,5,4},
			{42,7,8,9},
			{41,12,11,10},
			{40,13,14,15},
			{39,18,17,16},
			{38,19,20,21},
			{37,24,23,22},
			{36,25,26,27},
			{35,32,31,28},
			{34,33,30,29}
	
	};
	private transient int floodEnemyGap = 25;
	private static final int DUO_BOARD[][] = 
		 			{ 
					{ 6, 6, 29, 31, 33, 56, 99,99,99,99,99 },
					{ 6, 6, 30, 32, 34, 57, 99,99,99,99,99 },
					{ 6, 6, 54, 38, 37, 56, 99,99,99,99,99 },
					{ 8, 7, 53, 42, 41, 57, 99,99,99,99,99 },
					{ 10, 9, 49, 47, 48, 56, 99,99,99,99,99 },
					{ 12, 11, 50, 45, 46, 57, 99,99,99,99,99 },
					{ 14, 13, 52, 43, 44, 56, 99,99,99,99,99 },
					{ 16, 15, 51, 39, 40, 57, 99,99,99,99,99 },
					{ 18, 17, 55, 35, 36, 56, 99,99,99,99,99 },
					{ 20, 19, 23, 25, 27, 57, 99,99,99,99,99 },
					{ 22, 21, 24, 26, 28, 56, 99,99,99,99,99 }
					};
	private static final int DUO_BOARD2[][] = 
			{ 
				{ 6, 6, 29, 31, 99, 56, 27,28,29,31,33 },
				{ 6, 6, 30, 32, 99, 57, 25,26,30,32,34 },
				{ 6, 6, 54, 38, 99, 56, 23,24,49,36,35 },
				{ 8, 7, 53, 42, 99, 57, 21,22,50,38,37 },
				{ 10, 9, 49, 47, 99, 56, 19,20,51,40,39 },
				{ 12, 11, 50, 45, 99, 57, 17,18,52,42,41 },
				{ 14, 13, 52, 43, 99, 56, 15,16,53,44,43 },
				{ 16, 15, 51, 39, 99, 57, 13,14,54,46,45 },
				{ 18, 17, 55, 35, 99, 56, 11,12,55,48,49 },
				{ 20, 19, 23, 25, 99, 57, 10,8,6,6,6 },
				{ 22, 21, 24, 26, 99, 56, 9,7,6,6,6 }
			};
	

	private static final int GO[][] = { 
			{ 0, 0, 4,5, 6, 7,8 },
			{ 0, 0, 3, 12, 11, 10, 9 },
			{ 0, 0, 2, 13, 14,15, 16 },
			{ 0, 0, 1, 26, 25, 24, 17 },
			{ 0, 39, 40, 27, 28, 23, 18 },
			{ 37, 38, 33, 32, 29, 22, 19 },
			{ 36, 35, 34, 31, 30, 21, 20 } };
	private transient int duolenght = 14;
	private transient boolean startL;
	private transient boolean startR;
	private transient boolean secondL;
	private transient boolean secondR;
	private transient boolean thirdL;
	private transient boolean thirdR;
	private transient boolean pathFollow;
	private transient boolean flag = false;
	
	private transient boolean mirrorChallenge = false;
	private transient boolean battleTriple = false;


	private transient boolean keepFourBattlesnakeAlive;


	private transient int heigth;


	private transient int width;


	private transient int[][] space;
	
	/*private int apiversion;
	private int width;
	private int heigth;
	private int timeout;*/
	public Challenger() {
		// TODO Auto-generated constructor stub
	}

	public Challenger( String gi) {
		super( gi);
		setFileConfig();
		try (InputStream input = new FileInputStream(getFileConfig())) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
          //  apiversion= Integer.parseInt(prop.getProperty("apiversion"));
            maxturn= Integer.parseInt(prop.getProperty("maxturn"));
            pathFollow = Boolean.parseBoolean(prop.getProperty("path"));
          
           
            

        } catch (IOException ex) {
        	log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());
        }
	}

	
	protected String getFileConfig() {
		return fileConfig;
	}

	

	@Override
	public Map<String, String> move(final JsonNode moveRequest) {
		
		Map<String, String> response = new ConcurrentHashMap<>();
		
		
		if (moveRequest.get("board").get("snakes").size() == 1) {
			if (moveRequest.get("board").get("width").asInt() == 7) {
				response = soloChallenge(moveRequest);
			}else {
				response =fourCornerChallenge(moveRequest);
			}
		}else {
			if (mirrorChallenge) {
				response = duoChallenge(moveRequest);
			}else if (battleTriple){
				response = tripleChallenge(moveRequest);
			}else if (keepFourBattlesnakeAlive) {
				response = keepFourSnakeAliveChallenge(moveRequest);
			}else {
				int count =0 ;
				for (int j =0 ; j < moveRequest.get("board").get("snakes").size();j++) {
					if (moveRequest.get("board").get("snakes").get(j).get("name").asText().equals(moveRequest.get("you").get("name").asText())) {
						count++;
					}
				}
				
				if (count ==2) {
					mirrorChallenge =true;
					response = duoChallenge(moveRequest);
				}else if ( count ==3 ) {
					battleTriple =true;
					response = tripleChallenge(moveRequest);
				}else if(count==4) {
					keepFourBattlesnakeAlive  = true;
					response = keepFourSnakeAliveChallenge(moveRequest);
				}
			}
		}
		
		return response;
	}

	private Map<String, String> keepFourSnakeAliveChallenge(final JsonNode moveRequest) {
		width = moveRequest.get("board").get("width").asInt();
		heigth = moveRequest.get("board").get("height").asInt();
		
		
		int id =0  ;
		
		for (int i =0 ; i < moveRequest.get("board").get("snakes").size();i++) {
			
				if (moveRequest.get("board").get("snakes").get(i).get("id").asText().equals(moveRequest.get("you").get("id").asText())) {
					id = i;
				}
			
		}
		
	
		
		final Map<String, String> response = new ConcurrentHashMap<>();
	
		response.put("shout",moveRequest.get("you").get("id").asText());
		
		

		final int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
		final int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
		
		if (snakey >= (id+1) * 4) {
			response.put("move",UPWARD);
			return response;
		}else if(snakey < id * 4) {
			response.put("move",DOWN);
			return response;
		}
		
		int pos = FOURALIVE[snakex][snakey%4];
		if (pos ==43) {
			pos =-1;
		}
		
		final int target = pos +1;
		response.put("shout", "TARGET " + target + " sid: " + moveRequest.get("you").get("id").asText());
		if (snakex !=0 && FOURALIVE[snakex -1][snakey%4] == target) {
			response.put("move",LEFT);
		}else if (snakey%4 !=0 && FOURALIVE[snakex][snakey%4 -1] == target) {
			response.put("move",UPWARD);
		}else if (snakey%4 != 3 && FOURALIVE[snakex][snakey%4 +1] == target) {
			response.put("move",DOWN);
		}else if (snakex !=10 && FOURALIVE[snakex +1][snakey%4] == target) {
			response.put("move",RIGHT);
		} 
	
		
		return response;
	}

	

	private Map<String, String> fourCornerChallenge(JsonNode moveRequest) {
		width = moveRequest.get("board").get("width").asInt();
		heigth = moveRequest.get("board").get("height").asInt();
		int[][]board = new int[width][heigth];
		space = new int[width][heigth];
		
	
	
		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		response.put("shout", moveRequest.get("you").get("id").asText());
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
		
		moveRequest.get("board").withArray("snakes").forEach(s -> {
			
			s.withArray("body").forEach(c -> {

				board[c.get("x").asInt()][c.get("y").asInt()] = -99;
				
			});

		

		});
		

		final int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
		final int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
		
		
		//maxd = 99;

		final int health = moveRequest.get("you").get("health").asInt();
		if ( moveRequest.get("you").get("length").asInt() < 70) {
		moveRequest.get("board").withArray("food").forEach(f -> {
			
				flood(f.get("x").asInt(), f.get("y").asInt(), 100 - health,board);
			
			
		});
		}
		
		moveRequest.get("board").withArray("hazards").forEach(f -> {
			board[f.get("x").asInt()][f.get("y").asInt()] -= 25;
			
		});
		
		flood(0, 0, health,board);
		flood(width-1, 0, health,board);
		flood(0, heigth-1, health,board);
		flood(width-1,heigth-1, health,board);
		String res = "up";
		if (	board[0][0]==-99 &&
				board[0][18]==-99 &&
				board[18][0]==-99 &&
				board[18][18]==-99) {
			if (snakey ==0) {
				response.put("move", UPWARD);
			}else if(snakey==18) {
				response.put("move", DOWN);
			}else if(snakex==0) {
				response.put("move", LEFT);
			}else if(snakex==18) {
				response.put("move", RIGHT);
			}else {
				res="continue";
			}
		}else {
			res="continue";
		}
		
		if (res.equalsIgnoreCase("continue")) {
			res="up";
			double result[] = { 0.5, 0.5, 0.5, 0.5 };
			if (snakey == 0) {
				possiblemove.put("up", -90);
				result[0] = 0.0;
			} else {
				space(snakex, snakey - 1, 5000,board);
				possiblemove.put("up", possiblemove.get("up") + board[snakex][snakey - 1] + count5000());
			}
	
			if (snakey == heigth - 1) {
				possiblemove.put("down", -90);
				result[1] = 0.0;
			} else {
				space(snakex, snakey + 1, 5000,board);
				possiblemove.put("down", possiblemove.get("down") + board[snakex][snakey + 1] + count5000());
			}
	
			if (snakex == 0) {
				possiblemove.put("left", -90);
				result[2] = 0.0;
			} else {
				space(snakex - 1, snakey, 5000,board);
				possiblemove.put("left", possiblemove.get("left") + board[snakex - 1][snakey] + count5000());
			}
	
			if (snakex == width - 1) {
				possiblemove.put("right", -90);
				result[3] = 0.0;
			} else {
				space(snakex + 1, snakey, 5000,board);
				possiblemove.put("right", possiblemove.get("right") + board[snakex + 1][snakey] + count5000());
			}
	
			
			
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
			
				
	
			response.put("move", res);
		}
		return response;
	}

	private Map<String, String> tripleChallenge(final JsonNode moveRequest) {
		Map<String, String> response = new ConcurrentHashMap<>();
		if (moveRequest.get("board").get("snakes").get(0).get("id").asText().equals(moveRequest.get("you").get("id").asText())) {
			response = moveTriplePlayer1(moveRequest);
		}else if (moveRequest.get("board").get("snakes").get(1).get("id").asText().equals(moveRequest.get("you").get("id").asText())) {
			response = moveTriplePlayer2(moveRequest);
		}else {
			response.put("move", "up");
		}
		return response;
	}

	private Map<String, String> moveTriplePlayer2(final JsonNode moveRequest) {
		width = moveRequest.get("board").get("width").asInt();
		heigth = moveRequest.get("board").get("height").asInt();
		int[][]board = new int[width][heigth];
		space = new int[width][heigth];
		
	
	
		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		response.put("shout", moveRequest.get("you").get("id").asText());
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
		final String id = moveRequest.get("you").get("id").asText();
		moveRequest.get("board").withArray("snakes").forEach(s -> {
			if (!s.get("id").asText().equals(id) ) {
				floodEnemy(s.get("body").get(0).get("x").asInt(), s.get("body").get(0).get("y").asInt(),
						-35,board);
				}
			
			s.withArray("body").forEach(c -> {

				board[c.get("x").asInt()][c.get("y").asInt()] = -99;
				
			});

		

		});
		

		final int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
		final int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
		
		
		//maxd = 99;

		final int health = moveRequest.get("you").get("health").asInt();
		moveRequest.get("board").withArray("food").forEach(f -> {
			if (health < 45) {
				flood(f.get("x").asInt(), f.get("y").asInt(), 100,board);
			}else {
				floodEnemy(f.get("x").asInt(), f.get("y").asInt(), -15,board);
				
			}
		
		
		});
		
		moveRequest.get("board").withArray("hazards").forEach(f -> {
			board[f.get("x").asInt()][f.get("y").asInt()] -= 25;
			
		});
		
		flood(6, 6, health/4,board);
		
		String res = "up";
	
		double result[] = { 0.5, 0.5, 0.5, 0.5 };
		if (snakey == 0) {
			possiblemove.put("up", -90);
			result[0] = 0.0;
		} else {
			space(snakex, snakey - 1, 5000,board);
			possiblemove.put("up", possiblemove.get("up") + board[snakex][snakey - 1] + count5000());
		}

		if (snakey == heigth - 1) {
			possiblemove.put("down", -90);
			result[1] = 0.0;
		} else {
			space(snakex, snakey + 1, 5000,board);
			possiblemove.put("down", possiblemove.get("down") + board[snakex][snakey + 1] + count5000());
		}

		if (snakex == 0) {
			possiblemove.put("left", -90);
			result[2] = 0.0;
		} else {
			space(snakex - 1, snakey, 5000,board);
			possiblemove.put("left", possiblemove.get("left") + board[snakex - 1][snakey] + count5000());
		}

		if (snakex == width - 1) {
			possiblemove.put("right", -90);
			result[3] = 0.0;
		} else {
			space(snakex + 1, snakey, 5000,board);
			possiblemove.put("right", possiblemove.get("right") + board[snakex + 1][snakey] + count5000());
		}

		
		
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
			
			res = "right";
		}
		
			

		response.put("move", res);
		
		return response;
	}

	private Map<String, String> moveTriplePlayer1(final JsonNode moveRequest) {
		width = moveRequest.get("board").get("width").asInt();
		heigth = moveRequest.get("board").get("height").asInt();
		int[][]board = new int[width][heigth];
		space = new int[width][heigth];
		
	
	
		final Map<String, String> response = new ConcurrentHashMap<>();
		final Map<String, Integer> possiblemove = new ConcurrentHashMap<>();
		response.put("shout", moveRequest.get("you").get("id").asText());
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
		final String id = moveRequest.get("you").get("id").asText();
		moveRequest.get("board").withArray("snakes").forEach(s -> {
			if (!s.get("id").asText().equals(id) ) {
			floodEnemy(s.get("body").get(0).get("x").asInt(), s.get("body").get(0).get("y").asInt(),
					-35,board);
			}
			s.withArray("body").forEach(c -> {

				board[c.get("x").asInt()][c.get("y").asInt()] = -99;
				
			});

		

		});
		

		final int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
		final int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
		
		
		//maxd = 99;

		final int health = moveRequest.get("you").get("health").asInt();
		moveRequest.get("board").withArray("food").forEach(f -> {
			if (health < 45) {
				flood(f.get("x").asInt(), f.get("y").asInt(), 100,board);
			}else {
				floodEnemy(f.get("x").asInt(), f.get("y").asInt(), -15,board);
				
			}
		});
		
		moveRequest.get("board").withArray("hazards").forEach(f -> {
			board[f.get("x").asInt()][f.get("y").asInt()] -= 25;
			
		});
		
		flood(5, 5, 14,board);
		
		String res = "up";
		double result[] = { 0.5, 0.5, 0.5, 0.5 };
		if (snakey == 0) {
			possiblemove.put("up", -90);
			result[0] = 0.0;
		} else {
			space(snakex, snakey - 1, 5000,board);
			possiblemove.put("up", possiblemove.get("up") + board[snakex][snakey - 1] + count5000());
		}

		if (snakey == heigth - 1) {
			possiblemove.put("down", -90);
			result[1] = 0.0;
		} else {
			space(snakex, snakey + 1, 5000,board);
			possiblemove.put("down", possiblemove.get("down") + board[snakex][snakey + 1] + count5000());
		}

		if (snakex == 0) {
			possiblemove.put("left", -90);
			result[2] = 0.0;
		} else {
			space(snakex - 1, snakey, 5000,board);
			possiblemove.put("left", possiblemove.get("left") + board[snakex - 1][snakey] + count5000());
		}

		if (snakex == width - 1) {
			possiblemove.put("right", -90);
			result[3] = 0.0;
		} else {
			space(snakex + 1, snakey, 5000,board);
			possiblemove.put("right", possiblemove.get("right") + board[snakex + 1][snakey] + count5000());
		}

		
		
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
			
			res = "right";
		}
		
			

		response.put("move", res);
		
		return response;
	}

	private Map<String, String> duoChallenge(final JsonNode moveRequest) {
		Map<String, String> response = new ConcurrentHashMap<>();
		String id1 = moveRequest.get("board").get("snakes").get(0).get("id").asText();
		String id2 =moveRequest.get("board").get("snakes").get(1).get("id").asText();
		
		if (id1.compareTo(id2) < 0 ){
			String temp = id1;
			id1 = id2;
			id2 = temp;
					
					
		}
		
		if (id1.equals(moveRequest.get("you").get("id").asText())) {
			response = movePlayer1(moveRequest);
		}else {
			response = movePlayer2(moveRequest);
		}
		
		return response;
	}
	
	

	private Map<String, String> movePlayer1(final JsonNode moveRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		final int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
		final int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
		
		
		
		
		
		if (snakex == 0 && snakey == 0 && !secondL) {
			startL = true;
		}else if(snakex == 0 && snakey == 0) {
			if (moveRequest.get("board").get("food").size()<25) {
				secondL=false;
			}else {
				thirdL=true;
			}
		}else if (snakex == 9 && snakey == 10) {
			secondL=true;
		}

		if (!startL || !secondL || !thirdL ) {
			if (!startL) {
				if (snakey > 0) {
					response.put(MOVESTR, UPWARD);
				} else if (snakex > 0) {
					response.put(MOVESTR, LEFT);
				}
			}else if (!secondL) {
				if (snakey < 10) {
					response.put(MOVESTR, DOWN);
				} else if (snakex < 10) {
					response.put(MOVESTR, RIGHT);
				}
			}else {
				if (snakey > 0) {
					response.put(MOVESTR, UPWARD);
				} else if (snakex > 0) {
					response.put(MOVESTR, LEFT);
				}
			}
		} else {
			
			final int turn = moveRequest.get("turn").asInt();
			if (turn > maxturn) {
				response.put(MOVESTR, UPWARD);
				return response;
			}
			int length = moveRequest.get("you").withArray("body").size();
			if (length % 2 != 0) {
				length++;
			}
			if (length < duolenght) {
				length = duolenght;
			}
			if (moveRequest.get("you").get("health").asInt() < length + 3) {
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
			for (final JsonNode bodyPart : moveRequest.get("you").get("body")) {
				space[bodyPart.get("x").asInt()][bodyPart.get("y").asInt()] = 99;
			}

			final int tailx = moveRequest.get("you").withArray("body")
					.get(moveRequest.get("you").withArray("body").size() - 1).get("x").asInt();
			final int taily = moveRequest.get("you").withArray("body")
					.get(moveRequest.get("you").withArray("body").size() - 1).get("y").asInt();
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
		final int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
		final int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
		
		
		if (snakex == 10 && snakey == 10 && !secondR) {
			startR = true;
		}else if(snakex == 10 && snakey == 10) {
			if (moveRequest.get("board").get("food").size()<25) {
				secondR=false;
			}else {
				thirdR=true;
			}
		}else if (snakex == 1 && snakey == 0) {
			secondR=true;
		}

		if (!startR || !secondR || !thirdR ) {
			if (!startR) {
				if (snakey < 10) {
					response.put(MOVESTR, DOWN);
				} else if (snakex < 10) {
					response.put(MOVESTR, RIGHT);
				}
			}else if (!secondR) {
				if (snakey > 0) {
					response.put(MOVESTR, UPWARD);
				} else if (snakex > 0) {
					response.put(MOVESTR, LEFT);
				}
			}else {
				if (snakey < 10) {
					response.put(MOVESTR, DOWN);
				} else if (snakex < 10) {
					response.put(MOVESTR, RIGHT);
				}
			}
		} else {
			
			final int turn = moveRequest.get("turn").asInt();
			if (turn > maxturn) {
				response.put(MOVESTR, UPWARD);
				return response;
			}
			
			
			int length = moveRequest.get("you").withArray("body").size();
			if (length % 2 != 0) {
				length++;
			}
			if (length < duolenght) {
				length = duolenght;
			}
			if (moveRequest.get("you").get("health").asInt() < length + 3) {
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
			for (final JsonNode bodyPart : moveRequest.get("you").get("body")) {
				space[bodyPart.get("x").asInt()][bodyPart.get("y").asInt()] = 99;
			}

			final int tailx = moveRequest.get("you").withArray("body")
					.get(moveRequest.get("you").withArray("body").size() - 1).get("x").asInt();
			final int taily = moveRequest.get("you").withArray("body")
					.get(moveRequest.get("you").withArray("body").size() - 1).get("y").asInt();
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
		final int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
		final int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
		
		
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
			final int turn = moveRequest.get("turn").asInt();
			if (turn > maxturn) {
				response.put(MOVESTR, UPWARD);
				return response;
			}
			int length = moveRequest.get("you").withArray("body").size();
			if (length % 2 != 0) {
				length++;
			}
			if (moveRequest.get("you").get("health").asInt() < length + 3) {
				length += 1;
			}

			int space[][] = new int[7][7];

			for (int x = 0; x < 7; x++) {
				for (int y = 0; y < 7; y++) {
					if (BOARD[x][y] > length) {
						space[x][y] = 99;
					} else {
						space[x][y] = 0;
					}
				}
			}
			for (final JsonNode bodyPart : moveRequest.get("you").get("body")) {
				space[bodyPart.get("x").asInt()][bodyPart.get("y").asInt()] = 99;
			}

			final int tailx = moveRequest.get("you").withArray("body")
					.get(moveRequest.get("you").withArray("body").size() - 1).get("x").asInt();
			final int taily = moveRequest.get("you").withArray("body")
					.get(moveRequest.get("you").withArray("body").size() - 1).get("y").asInt();
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
		final int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
		final int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();
		
		final int foodSize= moveRequest.get("board").get("food").size();
		if (foodSize == 39 && GO[snakex][snakey+1] == 1 && moveRequest.get("you").withArray("body").size()==10) {
			flag =true;
		}
		
		if (!flag ) {
			if (snakey ==0 && snakex>0) {
				response.put(MOVESTR, LEFT);
		
				
			}else if (snakey == 0) {
				response.put(MOVESTR, DOWN);
			}else if (snakey == 1 && snakex !=4) {
				response.put(MOVESTR, RIGHT);
			}else {
				response.put(MOVESTR, UPWARD);
			}
		}else {
			final int id = GO[snakex][snakey]+1;
			
			if (snakex <6 && GO[snakex+1][snakey] == id) {
				response.put(MOVESTR, RIGHT);
			}else if(snakex >0 && GO[snakex-1][snakey] == id) {
				response.put(MOVESTR, LEFT);
			}else if(snakey <6 &&  GO[snakex][snakey+1] == id) {
				response.put(MOVESTR, DOWN);
			}else {
				response.put(MOVESTR, UPWARD);
			}
		}
		return response;
	}


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
		fileConfig="Challenger.properties";

	}

	@Override
	public Map<String, String> end(final JsonNode endRequest) {
		return null;
	}
	
	private void space(final int x, final int y, final int value,final int[][] board) {
		if (board[x][y] >= 0 && space[x][y] < 1000) {
			space[x][y] = value;
			if (value > 0) {
				if (x > 0) {
					space(x - 1, y, value,board);
				}
				if (y > 0) {
					space(x, y - 1, value,board);
				}
				if (y < heigth - 1) {
					space(x, y + 1, value,board);
				}

				if (x < width - 1) {
					space(x + 1, y, value,board);
				}
			}
		}
	}
	
	private void flood(final int x, final int y, final int value, final int[][] board) {
		if (board[x][y] >= 0 && board[x][y] < value) {
			board[x][y] = value > board[x][y] ? value : board[x][y];
			if (value > 0) {
				if (x > 0) {
					flood(x - 1, y, value - 1,board);
				}
				if (y > 0) {
					flood(x, y - 1, value - 1,board);
				}
				if (y < heigth - 1) {
					flood(x, y + 1, value - 1,board);
				}

				if (x < width - 1) {
					flood(x + 1, y, value - 1,board);
				}
			}
		}
	}
	
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
	

	private void floodEnemy(final int x,final  int y,final  int value,final int[][] board) {
		if (board[x][y] > -90) {
			board[x][y] = value < board[x][y] ? value : board[x][y];
			if (value < 0 - floodEnemyGap) {
				if (x > 0) {
					floodEnemy(x - 1, y, value + floodEnemyGap,board);
				}
				if (y > 0) {
					floodEnemy(x, y - 1, value + floodEnemyGap,board);
				}
				if (y < heigth - 1) {
					floodEnemy(x, y + 1, value + floodEnemyGap,board);
				}

				if (x < width - 1) {
					floodEnemy(x + 1, y, value + floodEnemyGap,board);
				}
			}
		}
	}
	
	public static Map<String, String> getInfo() {
		final Map<String, String> response = new ConcurrentHashMap<>();
		try (InputStream input = new FileInputStream(fileConfig)) {

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
	
	
	/*private void floodEnemy(int x, int y, int value,int[][] board) {
		if (board[x][y] > -90) {
			board[x][y] = (value < board[x][y] ? value : board[x][y]);
			if (value < 0 - 2) {
				if (x > 0) {
					floodEnemy(x - 1, y, value + 2,board);
				}
				if (y > 0) {
					floodEnemy(x, y - 1, value + 2,board);
				}
				if (y < heigth - 1) {
					floodEnemy(x, y + 1, value + 2,board);
				}

				if (x < width - 1) {
					floodEnemy(x + 1, y, value + 2,board);
				}
			}
		}
	}*/
	
	/*
	public static void main(String args[]) {
		ObjectMapper JSON_MAPPER = new ObjectMapper();
		String test = "{\"game\":{\"id\":\"9c5a55e9-f586-4eb2-8993-22fed449783a\"},\"turn\":113,\"board\":{\"height\":19,\"width\":11,\"food\":[{\"x\":7,\"y\":16},{\"x\":2,\"y\":15},{\"x\":0,\"y\":17},{\"x\":3,\"y\":16},{\"x\":3,\"y\":17},{\"x\":4,\"y\":15},{\"x\":5,\"y\":15},{\"x\":8,\"y\":15},{\"x\":8,\"y\":11},{\"x\":7,\"y\":12},{\"x\":6,\"y\":11},{\"x\":10,\"y\":7},{\"x\":2,\"y\":6},{\"x\":9,\"y\":9},{\"x\":0,\"y\":11},{\"x\":2,\"y\":17},{\"x\":1,\"y\":16},{\"x\":7,\"y\":10},{\"x\":10,\"y\":16},{\"x\":7,\"y\":8},{\"x\":6,\"y\":14},{\"x\":9,\"y\":17}],\"snakes\":[{\"id\":\"2cc39ad7-42b4-4bd4-ab33-7409fac0ea3a\",\"name\":\"chall\",\"health\":69,\"body\":[{\"x\":8,\"y\":0},{\"x\":8,\"y\":1},{\"x\":8,\"y\":2},{\"x\":8,\"y\":3},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":7,\"y\":0},{\"x\":6,\"y\":0},{\"x\":6,\"y\":1}]},{\"id\":\"58951289-fcf3-4f0b-b4ec-ad72aff2a5fa\",\"name\":\"chall\",\"health\":99,\"body\":[{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":9,\"y\":5},{\"x\":8,\"y\":5},{\"x\":7,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":6},{\"x\":6,\"y\":7},{\"x\":5,\"y\":7},{\"x\":5,\"y\":6},{\"x\":5,\"y\":5}]},{\"id\":\"3e28d7e8-dae3-4175-a379-acf875b7181f\",\"name\":\"chall\",\"health\":10,\"body\":[{\"x\":5,\"y\":11},{\"x\":5,\"y\":10},{\"x\":5,\"y\":9},{\"x\":5,\"y\":8},{\"x\":4,\"y\":8}]}]},\"you\":{\"id\":\"3e28d7e8-dae3-4175-a379-acf875b7181f\",\"name\":\"chall\",\"health\":10,\"body\":[{\"x\":5,\"y\":11},{\"x\":5,\"y\":10},{\"x\":5,\"y\":9},{\"x\":5,\"y\":8},{\"x\":4,\"y\":8}]}}";
		try {
			JsonNode parsedRequest = JSON_MAPPER.readTree(test);
		//	int size = parsedRequest.get("board").get("height").asInt();
			Challenger t = new Challenger();
			//t.board = new int[size][size];
			//t.space = new int[size][size];
			//t.heigth = size;
			//t.width = size;
			t.keepFourBattlesnakeAlive=true;
			t.move(parsedRequest);
		} catch (IOException e) {
			log.atWarning().log(e.getMessage() + "\n" + e.getStackTrace());
		}
	}*/
}