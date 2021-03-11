package ai.nettogrof.battlesnake.snakes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.nettogrof.battlesnake.alpha.AlphaNode;
import ai.nettogrof.battlesnake.alpha.AlphaSearch;
import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;


public class AlphaSnake extends ABSnakeAI {
	private int width;
	private int heigth;
	
	public int timeout = 300;
	private boolean multiThread=false;
	private String losing ="I have a bad feeling about this";
	private String winning = "I'm your father";
	private int minusbuffer = 250;
	
	private AlphaNode lastRoot;
	protected static String fileConfig="Alpha.properties";
	public AlphaSnake() {
	}

	public AlphaSnake(Logger l, String gi) {
		super(l, gi);
		try (InputStream input = new FileInputStream(fileConfig)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            apiversion= Integer.parseInt(prop.getProperty("apiversion"));
            minusbuffer= Integer.parseInt(prop.getProperty("minusbuffer"));
            multiThread = Boolean.parseBoolean(prop.getProperty("multiThread"));
            
            String[] lose = {"I have a bad feeling about this","help me obiwan you're my only hope", "Nooooo!!","Sorry master, I failed"};
            String[] win = {"You gonna die", "I'm your father","All your base belong to me","42 is the answer of life","Astalavista baby!"};
            Random r = new Random();
            losing = lose[r.nextInt(lose.length)];
            winning = win[r.nextInt(win.length)];
           
            

        } catch (IOException ex) {
            ex.printStackTrace();
        }

	}

	@Override
	public Map<String, String> move(JsonNode moveRequest) {
		Long st = System.currentTimeMillis();
		Map<String, String> response = new HashMap<>();
		Map<String, Integer> possiblemove = new HashMap<>();
		possiblemove.put("up", 0);
		possiblemove.put("down", 0);
		possiblemove.put("left", 0);
		possiblemove.put("right", 0);

		// String name = moveRequest.get("you").get("name").asText();

		// int turn = moveRequest.get("turn").asInt();

		int snakex = moveRequest.get("you").withArray("body").get(0).get("x").asInt();
		int snakey = moveRequest.get("you").withArray("body").get(0).get("y").asInt();

		FoodInfo food = new FoodInfo(moveRequest.get("board"));
		SnakeInfo[] snakes = new SnakeInfo[moveRequest.get("board").get("snakes").size()];
		
	
		JsonNode me = moveRequest.get("you");
		snakes[0] = new SnakeInfo();
		snakes[0].setHealth((short) me.get("health").asInt());
		snakes[0].setName(me.get("name").asText());
		snakes[0].setSnake(me);

		for (int i = 0, j = 1; i < snakes.length; i++, j++) {
			JsonNode s = moveRequest.get("board").get("snakes").get(i);
			if (s.get("name").asText().equals(me.get("name").asText())) {
				j--;
			} else {
				snakes[j] = new SnakeInfo();
				snakes[j].setHealth((short) s.get("health").asInt());
				snakes[j].setName(s.get("name").asText());
				snakes[j].setSnake(s);
			}
		}
		
		AlphaNode root = null;
		if (lastRoot != null) {
			
			for (AlphaNode c : lastRoot.getChild()) {
				if (food.equals(c.getFood())) {
					if (Arrays.deepEquals(c.getSnakes().toArray(), snakes)) {
						root = c;
						lastRoot=null;
						break;
							
						
					}
				}
			}
		}
		if(root == null) {
			root = new AlphaNode(snakes, food);
		}
		
		// boolean multichoice = true;

		/********
		 * Old single thread int count = 0; while (System.currentTimeMillis() - st <
		 * timeout) { count++; if (count == 400000) { System.out.println("120k"); }
		 * CNode bm = root.getBestChild(count % 100 ==0); generateChild(bm);
		 * root.updateScore();
		 * 
		 * } System.out.println("Count:"+count);
		 */

		/*************************** NEW Multithread */
		if(multiThread) {
			new AlphaSearch(root,width,heigth).generateChild();
		//	MoveGenerator.generateChild(root,width,heigth);
			ArrayList<AlphaSearch> lt = new ArrayList<AlphaSearch>();
			for (AlphaNode c : root.getChild()) {
				lt.add(new AlphaSearch(c, width, heigth,st, timeout-minusbuffer));
	
			}
	
			for (AlphaSearch s : lt) {
				Thread t = new Thread(s);
				t.setPriority(1);
				t.start();
				//System.out.println("start" + (System.currentTimeMillis() -st));
			}
	
			try {
		
				Thread.sleep(timeout-minusbuffer-50);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		
			for (AlphaSearch s : lt) {
				s.stopSearching();
				//System.out.println("stop" + (System.currentTimeMillis() -st));
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			root.updateScore();
		}else {
			AlphaSearch main = new AlphaSearch(root,width,heigth,st,timeout-minusbuffer);
			main.run();
		}
		AlphaNode winner = (AlphaNode) chooseBestMove(root);
		
		String res = "";
		if (winner == null) {
			if (!root.getChild().isEmpty()) {
				
			
				winner = (AlphaNode) root.getChild().get(0);
			}
		}
		
		if (winner  != null) {

		if (winner.getScoreRatio() == 0) {
			response.put("shout", losing);
			winner = (AlphaNode) lastChance(root);
		}
		
		if (winner.getScoreRatio() > 100) {
			response.put("shout", winning);
			winner = (AlphaNode) finishHim(root,winner);
		}
		
		int move = winner.getSnakes().get(0).getHead();
		if (move /1000 < snakex) {
			res = "left";
		}else if (move/1000 > snakex) {
			res = "right";
		}else if (move%1000 < snakey) {
			res = "up";
		}else if (move%1000 > snakey) {
			res = "down";
		}
		
		
		/* for api 1 */
		if (res.equals("up")) {
			res = "down";
		}else if (res.equals("down")) {
			res = "up";
		}
	
		
		}else {
			response.put("shout", losing);
			res ="down";
		}
		response.put("move", res);
		lastRoot = root;
		
		System.out.println("nb nodes" + root.getChildCount() + "  time: " + (System.currentTimeMillis() - st));
		nodeTotalCount += root.getChildCount();
		timeTotal += (System.currentTimeMillis() - st);
		return response;
	}
	

	

	private AlphaNode finishHim(AlphaNode root, AlphaNode winner) {
		AlphaNode ret = null;
		int nb = Integer.MAX_VALUE;
		for (AlphaNode c : root.getChild()) {
			if ( c.getChildCount() < nb && c.getScoreRatio()> 100) {
				ret = c;
				nb = c.getChildCount();
			}
		}
		if (ret == null ) {
			ret = winner;
		}
		return ret;
	}

	private AlphaNode lastChance(AlphaNode root) {
		AlphaNode ret = null;
		int nb =0;
		for (AlphaNode c : root.getChild()) {
			if ( c.getChildCount() > nb) {
				ret = c;
				nb = c.getChildCount();
			}
		}
		
		return ret;
	}

	@Override
	public Map<String, String> start(JsonNode startRequest) {
		Map<String, String> response = new HashMap<>();
		response.put("color", "#212121");
		response.put("headType", "shac-gamer");
		response.put("tailType", "shac-coffee");
		width = startRequest.get("board").get("width").asInt();
		heigth = startRequest.get("board").get("height").asInt();
	//	nbSnake = startRequest.get("board").get("snakes").size();
		try {
		timeout = startRequest.get("game").get("timeout").asInt();
		}catch (Exception e) {
			timeout = 500;
		};
		
		//timeout = timeout-200;

		return response;
	}

	

	private AlphaNode chooseBestMove(AlphaNode root) {
		// double score =-200;
		ArrayList<AlphaNode> child = root.getChild();
		AlphaNode winner = null;
		ArrayList<Double> up = new ArrayList<Double>();
		ArrayList<Double> down = new ArrayList<Double>();
		ArrayList<Double> left = new ArrayList<Double>();
		ArrayList<Double> right = new ArrayList<Double>();
		// ArrayList<Double> choice = new ArrayList<Double>();
		int head = root.getSnakes().get(0).getHead();

		for (int i = 0; i < child.size(); i++) {
			// if (child.get(i).getSnakes()[0].alive) {
			int move = child.get(i).getSnakes().get(0).getHead();

			if (move/1000 < head/1000) {
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
		for (Double v : up) {
			if (v < wup) {
				wup = v;
			}
		}
		for (Double v : down) {
			if (v < wdown) {
				wdown = v;
			}
		}
		for (Double v : left) {
			if (v < wleft) {
				wleft = v;
			}
		}
		for (Double v : right) {
			if (v < wright) {
				wright = v;
			}
		}
		double choiceValue = Double.MIN_VALUE;
		if (wup != Double.MAX_VALUE) {
			System.out.println("up" + wup);
			if (wup > choiceValue) {
				choiceValue = wup;
			}
		}
		if (wdown != Double.MAX_VALUE) {
			System.out.println("down" + wdown);
			if (wdown > choiceValue) {
				choiceValue = wdown;
			}
		}
		if (wleft != Double.MAX_VALUE) {
			System.out.println("left" + wleft);
			if (wleft > choiceValue) {
				choiceValue = wleft;
			}
		}
		if (wright != Double.MAX_VALUE) {
			System.out.println("right" + wright);
			if (wright > choiceValue) {
				choiceValue = wright;
			}
		}

		for (int i = 0; i < child.size(); i++) {
			double c = child.get(i).getScoreRatio();
			if (c == choiceValue && child.get(i).getSnakes().get(0).isAlive()) {
				winner = child.get(i);
				i = child.size();
			}
			
			
		}

		
		return winner;
	}

	public static void main(String args[]) {
		ObjectMapper JSON_MAPPER = new ObjectMapper();

//		String test = "{\"game\":{\"id\":\"1958e8e0-c0bb-4d4e-94e0-a2ac52a2ee79\"},\"turn\":14,\"board\":{\"height\":7,\"width\":7,\"food\":[{\"x\":1,\"y\":3},{\"x\":0,\"y\":3},{\"x\":1,\"y\":1},{\"x\":4,\"y\":5},{\"x\":2,\"y\":1},{\"x\":0,\"y\":1},{\"x\":1,\"y\":6}],\"snakes\":[{\"id\":\"b58f9a3a-099a-4e18-88e4-8554c2e67b2b\",\"name\":\"Basic\",\"health\":100,\"body\":[{\"x\":3,\"y\":1},{\"x\":3,\"y\":0},{\"x\":4,\"y\":0},{\"x\":5,\"y\":0},{\"x\":6,\"y\":0},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":3,\"y\":2}]},{\"id\":\"617e9e2a-d04e-467d-9702-e09a286bfa34\",\"name\":\"Flood\",\"health\":99,\"body\":[{\"x\":5,\"y\":3},{\"x\":6,\"y\":3},{\"x\":6,\"y\":4},{\"x\":6,\"y\":5},{\"x\":6,\"y\":6},{\"x\":5,\"y\":6},{\"x\":5,\"y\":5},{\"x\":5,\"y\":4}]},{\"id\":\"92a92c7d-e932-4926-8405-6ce6dd492aa4\",\"name\":\"Alpha\",\"health\":96,\"body\":[{\"x\":4,\"y\":4},{\"x\":4,\"y\":3},{\"x\":3,\"y\":3},{\"x\":2,\"y\":3},{\"x\":2,\"y\":4},{\"x\":1,\"y\":4},{\"x\":0,\"y\":4}]}]},\"you\":{\"id\":\"92a92c7d-e932-4926-8405-6ce6dd492aa4\",\"name\":\"Alpha\",\"health\":96,\"body\":[{\"x\":4,\"y\":4},{\"x\":4,\"y\":3},{\"x\":3,\"y\":3},{\"x\":2,\"y\":3},{\"x\":2,\"y\":4},{\"x\":1,\"y\":4},{\"x\":0,\"y\":4}]}}";// {"game":{"id":"e961706c-6cfb-4367-9f05-b53d3fe6f3f5"},"turn":40,"board":{"height":11,"width":11,"food":[{"x":7,"y":10},{"x":2,"y":4},{"x":0,"y":9},{"x":8,"y":5},{"x":7,"y":8},{"x":1,"y":6},{"x":5,"y":6},{"x":8,"y":10},{"x":10,"y":5},{"x":8,"y":2}],"snakes":[{"id":"bf3f98e1-e434-4a58-bf98-6e05d626db27","name":"pn","health":100,"body":[{"x":2,"y":0},{"x":2,"y":1},{"x":1,"y":1},{"x":0,"y":1},{"x":0,"y":2},{"x":1,"y":2},{"x":1,"y":2}]}]},"you":{"id":"bf3f98e1-e434-4a58-bf98-6e05d626db27","name":"pn","health":100,"body":[{"x":2,"y":0},{"x":2,"y":1},{"x":1,"y":1},{"x":0,"y":1},{"x":0,"y":2},{"x":1,"y":2},{"x":1,"y":2}]}}
		String test = "{\"game\":{\"id\":\"5c8f3a2b-e917-4c97-8ebe-f129a470f7b1\",\"timeout\":500},\"turn\":142,\"board\":{\"height\":11,\"width\":11,\"food\":[{\"x\":1,\"y\":9}],\"snakes\":[{\"id\":\"gs_p7KtVK7mvj7QCVvBtWphMBBS\",\"name\":\"Nessegrev\",\"health\":84,\"body\":[{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":4,\"y\":5},{\"x\":3,\"y\":5},{\"x\":2,\"y\":5},{\"x\":2,\"y\":6},{\"x\":2,\"y\":7},{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":3,\"y\":9},{\"x\":3,\"y\":8},{\"x\":3,\"y\":7},{\"x\":3,\"y\":6},{\"x\":4,\"y\":6}],\"head\":{\"x\":4,\"y\":2},\"length\":15,\"shout\":\"\"},{\"id\":\"gs_gVf8yppRbgVD87yyYfhx4bvF\",\"name\":\"Ravioli\",\"health\":86,\"body\":[{\"x\":0,\"y\":8},{\"x\":0,\"y\":7},{\"x\":0,\"y\":6},{\"x\":0,\"y\":5},{\"x\":0,\"y\":4},{\"x\":1,\"y\":4},{\"x\":2,\"y\":4},{\"x\":2,\"y\":3},{\"x\":1,\"y\":3},{\"x\":1,\"y\":2},{\"x\":1,\"y\":1},{\"x\":2,\"y\":1},{\"x\":2,\"y\":2},{\"x\":3,\"y\":2}],\"head\":{\"x\":0,\"y\":8},\"length\":14,\"shout\":\"\"}]},\"you\":{\"id\":\"gs_p7KtVK7mvj7QCVvBtWphMBBS\",\"name\":\"Nessegrev\",\"health\":84,\"body\":[{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":4,\"y\":5},{\"x\":3,\"y\":5},{\"x\":2,\"y\":5},{\"x\":2,\"y\":6},{\"x\":2,\"y\":7},{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":3,\"y\":9},{\"x\":3,\"y\":8},{\"x\":3,\"y\":7},{\"x\":3,\"y\":6},{\"x\":4,\"y\":6}],\"head\":{\"x\":4,\"y\":2},\"length\":15,\"shout\":\"\"}}";
		try {
			JsonNode parsedRequest = JSON_MAPPER.readTree(test);
			int size = parsedRequest.get("board").get("height").asInt();
			AlphaSnake t = new AlphaSnake();

			t.heigth = size;
			t.width = size;
			t.timeout = 500;
			t.multiThread = false;
			//t.nbSnake = 3;
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			System.out.println(t.move(parsedRequest));

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	protected void setFileConfig() {
		fileConfig="Alpha.properties";
		
	}
	
	 public static Map<String, String> getInfo() {
			Map<String, String> response = new HashMap<>();
			try (InputStream input = new FileInputStream(fileConfig)) {

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

	@Override
	protected String getFileConfig() {
		return fileConfig;
	}

	

}
