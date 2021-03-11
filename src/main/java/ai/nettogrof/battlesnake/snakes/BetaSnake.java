package ai.nettogrof.battlesnake.snakes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.nettogrof.battlesnake.alpha.BetaNode;
import ai.nettogrof.battlesnake.alpha.BetaSearch;
import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.HazardInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;

public class BetaSnake extends ABSnakeAI {

	private transient BetaNode lastRoot;
	protected static String fileConfig = "Beta.properties";
	public static boolean challengeRoyale =false;
	public int api = 0;
	public static boolean squad = false;
	
	
	public BetaSnake() {

	}

	public BetaSnake(Logger l, String gi) {

		super(l, gi);
		fileConfig = "Beta.properties";
		try (InputStream input = new FileInputStream(getFileConfig())) {
			
			Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			// apiversion= Integer.parseInt(prop.getProperty("apiversion"));
		

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public Map<String, String> move(JsonNode moveRequest) {
		/*try {
			bw.write(JSON_MAPPER.writeValueAsString(moveRequest) + "\n");
		} catch (JsonProcessingException e1) {

			e1.printStackTrace();
		} catch (Exception e1) {

			e1.printStackTrace();
		}*/
		
		if ( moveRequest.get("you").has("head")) { 
			apiversion = 1; 
		}
		
		
		Long st = System.currentTimeMillis();
		Map<String, String> response = new HashMap<>();
		Map<String, Integer> possiblemove = new HashMap<>();
		possiblemove.put(UP, 0);
		possiblemove.put(DOWN, 0);
		possiblemove.put(LEFT, 0);
		possiblemove.put(RIGHT, 0);

		// String name = moveRequest.get("you").get("name").asText();

		// int turn = moveRequest.get("turn").asInt();

		final BetaNode root = genRoot(moveRequest);

		/*************************** NEW Multithread */
		if (multiThread) {
			new BetaSearch(root, width, heigth).generateChild();
			//final int cpu =2;
			
			final ArrayList<BetaSearch> lt = new ArrayList<>();
			/*final ArrayList<BetaNode> lw = new ArrayList<>();
			final ArrayList<BetaNode> lw2 = new ArrayList<>();
			
			for ( int i =0 ; i < root.getChild().size() ; i++) {
				if ( i % cpu ==0) {
					lw.add(root.getChild().get(i));
				}else {
					lw2.add(root.getChild().get(i));
				}
			}*/
			for (final BetaNode c : root.getChild()) {
				lt.add(new BetaSearch(c, width, heigth, st, timeout - minusbuffer));

			}
		/*	lt.add(new BetaSearch(lw, width, heigth, st, timeout - minusbuffer));
			lt.add(new BetaSearch(lw2, width, heigth, st, timeout - minusbuffer));
			*/
			for (final BetaSearch s : lt) {
				final Thread t = new Thread(s);
				t.setPriority(3);
				t.start();
				// System.out.println("start" + (System.currentTimeMillis() -st));
			}

			try {

				Thread.sleep(timeout - minusbuffer - 50);
			} catch (InterruptedException e) {

				LOG.error("Thread?!", e);
			}

			for (final BetaSearch search : lt) {
				search.stopSearching();
				// System.out.println("stop" + (System.currentTimeMillis() -st));
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {

				LOG.error("Thread?!", e);
			}
			root.updateScore();
		} else {
			final BetaSearch main = new BetaSearch(root, width, heigth, st, timeout - minusbuffer);
			main.run();
		}
		BetaNode winner = chooseBestMove(root);
		
		String res;
		if (winner == null && !root.getChild().isEmpty() ) {
			

				winner = root.getChild().get(0);
			
		}

		if (winner == null) {
			response.put("shout", losing);
			res = DOWN;
		} else {
			if (winner.getScoreRatio() < 0.001) {
				response.put("shout", losing);
				winner = lastChance(root);
			} else if (winner.getScoreRatio() > 8) {
				response.put("shout", winning);
				winner = finishHim(root, winner);
			}

			final int move = winner.getSnakes().get(0).getHead();

			final int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
			
			if (move/1000 < snakex) {
				res = LEFT;
			} else if (move/1000 > snakex) {
				res = RIGHT;
			} else if (move%1000 < moveRequest.get("you").withArray("body").get(0).get("y").asInt()) {
				res = UP;
			} else {
				res = DOWN;
			}
			LOG.info("api "+apiversion);
			if (apiversion == 1) {
				if (res.equals(UP)) {
					res = DOWN;
				} else if (res.equals(DOWN)) {
					res = UP;
				}
			}
		}
		response.put(MOVE, res);
		lastRoot = root;
				
		LOG.info("nb nodes" + root.getChildCount() + "  time: " + (System.currentTimeMillis() - st));
		nodeTotalCount += root.getChildCount();
		timeTotal += System.currentTimeMillis() - st;
		return response;
	}

		

	private BetaNode genRoot(final JsonNode moveRequest) {
		final JsonNode board = moveRequest.get("board");
		final FoodInfo food = new FoodInfo(board);

		final HazardInfo hazard = new HazardInfo(board);

		SnakeInfo[] snakes = new SnakeInfo[board.get("snakes").size()];

		final JsonNode me = moveRequest.get("you");
		snakes[0] = new SnakeInfo();
		snakes[0].setHealth((short) (me.get("health").asInt()));
		snakes[0].setName(me.get("name").asText());
		snakes[0].setSnake(me);
		if (me.get("squad") != null) {
		snakes[0].setSquad(me.get("squad").asText());
		}

		for (int i = 0, j = 1; i < snakes.length; i++, j++) {
			final JsonNode s = board.get("snakes").get(i);
			if (s.get("id").asText().equals(me.get("id").asText())) {
				j--;
			} else {
				snakes[j] = new SnakeInfo();
				snakes[j].setHealth((short) s.get("health").asInt());
				snakes[j].setName(s.get("name").asText());
				snakes[j].setSnake(s);
				if (s.get("squad") != null) {
				//	JsonNode de = s.get("squad");
					
				snakes[j].setSquad(s.get("squad").asText());
				}else {
					snakes[j].setSquad("");
				}
			}
		}
		if (lastRoot != null) {

			for (final BetaNode c : lastRoot.getChild()) {
				if (food.equals(c.getFood()) ) {
					ArrayList<SnakeInfo> csnake = c.getSnakes();
					boolean e = true;
					for (int i = 0 ; i < csnake.size() && e; i++) {
						e = csnake.get(i).equals(snakes[i]);
					}
					if (e) {
				
						return c;
					}

				}
			}
		}
		return new BetaNode(snakes, food, hazard);

	}

	@Override
	public Map<String, String> start(final JsonNode startRequest) {

		
		
		if (startRequest.get("ruleset") != null) {
			api = 1;
			if (startRequest.get("ruleset").get("name").asText().equals("squad")) {
				squad = true;
			}
		}

	
		

		final Map<String, String> response = new HashMap<>();
		response.put("color", "#216121");
		response.put("headType", "shac-gamer");
		response.put("tailType", "shac-coffee");
		width = startRequest.get("board").get("width").asInt();
		heigth = startRequest.get("board").get("height").asInt();
		// nbSnake = startRequest.get("board").get("snakes").size();
		try {
			timeout = startRequest.get("game").get("timeout").asInt();
		} catch (Exception e) {
			timeout = 500;
		}
		

		return response;
	}

	private BetaNode finishHim(final BetaNode root, final BetaNode winner) {
		
		BetaNode ret = null;
		
		
		final HashMap<Integer,Double> scoreCount = new HashMap<>();
		
		for (final BetaNode c : root.getChild()) {
			if (scoreCount.get(c.getSnakes().get(0).getHead()) == null) {
				scoreCount.put(c.getSnakes().get(0).getHead(), c.getScoreRatio());
			}else if(scoreCount.get(c.getSnakes().get(0).getHead()) > c.getScoreRatio()) {
				scoreCount.put(c.getSnakes().get(0).getHead(),c.getScoreRatio());
			}
			
		}
		
		int nb = Integer.MAX_VALUE;
		for (final BetaNode c : root.getChild()) {
			
			
			
			if (c.getChildCount() < nb && scoreCount.get(c.getSnakes().get(0).getHead()) > 100) {
				ret = c;
				nb = c.getChildCount();
			}
		}
		if (ret == null) {
			ret = winner;
		}
		return ret;
	}

	private BetaNode lastChance(final BetaNode root) {
		
		BetaNode ret = null;
		int nb = 0;
		final HashMap<Integer,Integer> headCount = new HashMap<>();
		
		for (final BetaNode c : root.getChild()) {
			if (headCount.get(c.getSnakes().get(0).getHead()) == null) {
				headCount.put(c.getSnakes().get(0).getHead(), c.getChildCount());
			}else {
				headCount.put(c.getSnakes().get(0).getHead(),headCount.get(c.getSnakes().get(0).getHead()) + c.getChildCount());
			}
			
		}
		
		for (final BetaNode c : root.getChild()) {
			
		
			 if ( headCount.get(c.getSnakes().get(0).getHead()) > nb ) {
				 nb =headCount.get(c.getSnakes().get(0).getHead());
				 ret = c;
			 }
		}
		

		return ret;
	}

	private BetaNode chooseBestMove(BetaNode root) {
		// double score =-200;
		final ArrayList<BetaNode> child = (ArrayList<BetaNode>) root.getChild();
		BetaNode winner = null;
		final ArrayList<Double> up = new ArrayList<>();
		final ArrayList<Double> down = new ArrayList<>();
		final ArrayList<Double> left = new ArrayList<>();
		final ArrayList<Double> right = new ArrayList<>();
		// ArrayList<Double> choice = new ArrayList<Double>();
		final int head = root.getSnakes().get(0).getHead();

		for (int i = 0; i < child.size(); i++) {
			// if (child.get(i).getSnakes()[0].alive) {
			final int move = child.get(i).getSnakes().get(0).getHead();

			if (move /1000 < head/1000) {
				left.add(child.get(i).getScoreRatio());
			}

			if (move/1000 > head/1000) {
				right.add(child.get(i).getScoreRatio());
			}
			if (move%1000 < head%1000) {
				up.add(child.get(i).getScoreRatio());
			}
			if (move%1000 > head%1000) {
				down.add(child.get(i).getScoreRatio());
			}
			// }
		}
		double wup = Double.MAX_VALUE;
		double wdown = Double.MAX_VALUE;
		double wleft = Double.MAX_VALUE;
		double wright = Double.MAX_VALUE;
		for (final Double v : up) {
			if (v < wup) {
				wup = v;
			}
		}
		for (final Double v : down) {
			if (v < wdown) {
				wdown = v;
			}
		}
		for (final Double v : left) {
			if (v < wleft) {
				wleft = v;
			}
		}
		for (final Double v : right) {
			if (v < wright) {
				wright = v;
			}
		}
		double choiceValue = -1;
		if (wup != Double.MAX_VALUE) {
			LOG.info("up" + wup);
			if (wup > choiceValue) {
				choiceValue = wup;
			}
		}
		if (wdown != Double.MAX_VALUE) {
			LOG.info("down" + wdown);
			if (wdown > choiceValue) {
				choiceValue = wdown;
			}
		}
		if (wleft != Double.MAX_VALUE) {
			LOG.info("left" + wleft);
			if (wleft > choiceValue) {
				choiceValue = wleft;
			}
		}
		if (wright != Double.MAX_VALUE) {
			LOG.info("right" + wright);
			if (wright > choiceValue) {
				choiceValue = wright;
			}
		}

		for (int i = 0; i < child.size(); i++) {
			final double c = child.get(i).getScoreRatio();
			if (c == choiceValue && child.get(i).getSnakes().get(0).isAlive()) {
				winner = child.get(i);
				i = child.size();
			}

		}

		return winner;
	}

	@Override
	protected void setFileConfig() {
		fileConfig = "Beta.properties";
	}

	public static Map<String, String> getInfo() {
		Map<String, String> response = new HashMap<>();
		try (InputStream input = new FileInputStream(fileConfig)) {

			Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			// get the property value and print it out

			response.put("apiversion", prop.getProperty("apiversion"));
			response.put("head", prop.getProperty("headType"));
			response.put("tail", prop.getProperty("tailType"));
			response.put("color", prop.getProperty("color"));
			response.put("author", "nettogrof");

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return response;
	}

	@Override
	protected String getFileConfig() {
		return fileConfig;
	}

	public static void main(String args[]) {
		ObjectMapper json = new ObjectMapper();

//		String test = " {\"game\":{\"id\":\"4b556697-3ad8-4e68-88e2-31f35ead0a62\",\"timeout\":500},\"turn\":110,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_grpvDDrHyfwRHCRkMbfQwm9f\",\"name\":\"Splishy Snake\",\"health\":70,\"body\":[{\"x\":5,\"y\":3},{\"x\":6,\"y\":3},{\"x\":7,\"y\":3},{\"x\":8,\"y\":3},{\"x\":8,\"y\":4},{\"x\":9,\"y\":4}],\"head\":{\"x\":5,\"y\":3},\"length\":6,\"shout\":\"\"},{\"id\":\"gs_mhwPMYtp6YhfRVbQKpKvkd4D\",\"name\":\"Madly going where no snake has gone before\",\"health\":95,\"body\":[{\"x\":8,\"y\":6},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":8,\"y\":10},{\"x\":8,\"y\":9},{\"x\":8,\"y\":8},{\"x\":8,\"y\":7},{\"x\":7,\"y\":7},{\"x\":7,\"y\":6}],\"head\":{\"x\":8,\"y\":6},\"length\":12,\"shout\":\"\"},{\"id\":\"gs_VjMcXdF4f9Hm9KPBgY7K8DgT\",\"name\":\"Nessegrev-beta\",\"health\":86,\"body\":[{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":8,\"y\":2},{\"x\":8,\"y\":1},{\"x\":8,\"y\":0},{\"x\":7,\"y\":0},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":5,\"y\":1}],\"head\":{\"x\":9,\"y\":1},\"length\":9,\"shout\":\"help me obiwan you're my only hope\"}],\"food\":[{\"x\":0,\"y\":0},{\"x\":10,\"y\":3}],\"hazards\":[]},\"you\":{\"id\":\"gs_VjMcXdF4f9Hm9KPBgY7K8DgT\",\"name\":\"Nessegrev-beta\",\"health\":86,\"body\":[{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":8,\"y\":2},{\"x\":8,\"y\":1},{\"x\":8,\"y\":0},{\"x\":7,\"y\":0},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":5,\"y\":1}],\"head\":{\"x\":9,\"y\":1},\"length\":9,\"shout\":\"help me obiwan you're my only hope\"}}";
		String test = "{\r\n" + 
				"	\"game\": {\r\n" + 
				"		\"id\": \"3f30dd62-d84b-4186-a58c-d0f58a0198c8\",\r\n" + 
				"		\"timeout\": 500,\r\n" + 
				"		\"ruleset\" :\"Standard\"\r\n" + 
				"	},\r\n" + 
				"	\"turn\": 1,\r\n" + 
				"	\"board\": {\r\n" + 
				"		\"height\": 7,\r\n" + 
				"		\"width\": 7,\r\n" + 
				"		\"food\": [\r\n" + 
				"			{\r\n" + 
				"				\"x\": 1,\r\n" + 
				"				\"y\": 1\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"x\": 2,\r\n" + 
				"				\"y\": 2\r\n" + 
				"			}\r\n" + 
				"		],\r\n" + 
				"		\"snakes\": [\r\n" + 
				"			{\r\n" + 
				"				\"id\": \"9de166a7-5022-43d6-a139-5b890bb9653c\",\r\n" + 
				"				\"name\": \"2\",\r\n" + 
				"				\"health\": 100,\r\n" + 
				"				\"body\": [\r\n" + 
				"					{\r\n" + 
				"						\"x\": 0,\r\n" + 
				"						\"y\": 2\r\n" + 
				"					},\r\n" + 
				"					{\r\n" + 
				"						\"x\": 0,\r\n" + 
				"						\"y\": 2\r\n" + 
				"					},\r\n" + 
				"					{\r\n" + 
				"						\"x\": 0,\r\n" + 
				"						\"y\": 2\r\n" + 
				"					}\r\n" + 
				"				],\r\n" + 
				"				\"latency\": 0,\r\n" + 
				"				\"head\": {\r\n" + 
				"					\"x\": 0,\r\n" + 
				"					\"y\": 2\r\n" + 
				"				},\r\n" + 
				"				\"length\": 3,\r\n" + 
				"				\"shout\": \"\",\r\n" + 
				"				\"squad\": \"\"\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"id\": \"8d58547c-58c9-42cd-8b7a-cf851cbb8f1a\",\r\n" + 
				"				\"name\": \"1\",\r\n" + 
				"				\"health\": 96,\r\n" + 
				"				\"body\": [\r\n" + 
				"					{\r\n" + 
				"						\"x\": 0,\r\n" + 
				"						\"y\": 0\r\n" + 
				"					},\r\n" + 
				"					{\r\n" + 
				"						\"x\": 0,\r\n" + 
				"						\"y\": 0\r\n" + 
				"					},\r\n" + 
				"					{\r\n" + 
				"						\"x\": 0,\r\n" + 
				"						\"y\": 0\r\n" + 
				"					}\r\n" + 
				"				],\r\n" + 
				"				\"latency\": 0,\r\n" + 
				"				\"head\": {\r\n" + 
				"					\"x\": 0,\r\n" + 
				"					\"y\": 0\r\n" + 
				"				},\r\n" + 
				"				\"length\": 3,\r\n" + 
				"				\"shout\": \"\",\r\n" + 
				"				\"squad\": \"\"\r\n" + 
				"			}\r\n" + 
				"		]\r\n" + 
				"	},\r\n" + 
				"	\"you\": {\r\n" + 
				"		\"id\": \"8d58547c-58c9-42cd-8b7a-cf851cbb8f1a\",\r\n" + 
				"		\"name\": \"Beta\",\r\n" + 
				"		\"health\": 96,\r\n" + 
				"		\"body\": [\r\n" + 
				"			{\r\n" + 
				"				\"x\": 0,\r\n" + 
				"				\"y\": 0\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"x\": 0,\r\n" + 
				"				\"y\": 0\r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"x\": 0,\r\n" + 
				"				\"y\": 0\r\n" + 
				"			}\r\n" + 
				"		],\r\n" + 
				"		\"latency\": 0,\r\n" + 
				"		\"head\": {\r\n" + 
				"			\"x\": 0,\r\n" + 
				"			\"y\": 0\r\n" + 
				"		},\r\n" + 
				"		\"length\": 3,\r\n" + 
				"		\"shout\": \"\",\r\n" + 
				"		\"squad\": \"\"\r\n" + 
				"	}\r\n" + 
				"}";
		try {
			JsonNode parsedRequest = json.readTree(test);
			int size = parsedRequest.get("board").get("height").asInt();
			BetaSnake t = new BetaSnake(LoggerFactory.getLogger(BetaSnake.class),"test");

			t.heigth = size;
			t.width = size;
			t.timeout = 5500;
			t.multiThread = false;
			

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			for (int i = 0; i < 2; i++) {
				System.out.println(t.move(parsedRequest));
				try {
					Thread.sleep(510);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
