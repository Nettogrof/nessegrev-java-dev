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

import ai.nettogrof.battlesnake.alpha.GammaNode;
import ai.nettogrof.battlesnake.alpha.GammaSearch;
import ai.nettogrof.battlesnake.proofnumber.FoodInfo;
import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;

public class GammaSnake extends ABSnakeAI {

	private transient GammaNode lastRoot;
	protected static String fileConfig = "Gamma.properties";
	
	public int api = 0;
	
	
	
	public GammaSnake() {

	}

	public GammaSnake(Logger l, String gi) {

		super(l, gi);
		fileConfig = "Gamma.properties";
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
			api = 1;
			
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

		final GammaNode root = genRoot(moveRequest);

		/*************************** NEW Multithread */
		if (multiThread) {
			new GammaSearch(root, width, heigth).generateChild();
			//final int cpu =2;
			
			final ArrayList<GammaSearch> lt = new ArrayList<>();
		//	final ArrayList<GammaNode> lw = new ArrayList<>();
			//final ArrayList<GammaNode> lw2 = new ArrayList<>();
			
			/*for ( int i =0 ; i < root.getChild().size() ; i++) {
				
				if ( i % cpu ==0) {
					lw.add(root.getChild().get(i));
				}else {
					lw2.add(root.getChild().get(i));
				}
			}*/
			for (final GammaNode c : root.getChild()) {
				lt.add(new GammaSearch(c, width, heigth, st, timeout - minusbuffer));

			}
		/*	lt.add(new GammaSearch(lw, width, heigth, st, timeout - minusbuffer));
			lt.add(new GammaSearch(lw2, width, heigth, st, timeout - minusbuffer));
			*/
			for (final GammaSearch s : lt) {
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

			for (final GammaSearch search : lt) {
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
			final GammaSearch main = new GammaSearch(root, width, heigth, st, timeout - minusbuffer);
			main.run();
		}
		GammaNode winner = chooseBestMove(root);
		
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
			LOG.info("api "+api);
			if (api == 1) {
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

		

	private GammaNode genRoot(final JsonNode moveRequest) {
		final JsonNode board = moveRequest.get("board");
		final FoodInfo food = new FoodInfo(board);

	

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

			for (final GammaNode c : lastRoot.getChild()) {
				
			
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
		return new GammaNode(snakes, food, false);

	}

	@Override
	public Map<String, String> start(final JsonNode startRequest) {

		
		
		if (startRequest.get("ruleset") != null) {
			api = 1;
			
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

	private GammaNode finishHim(final GammaNode root, final GammaNode winner) {
		
		GammaNode ret = null;
		
		
		final HashMap<Integer,Double> scoreCount = new HashMap<>();
		
		for (final GammaNode c : root.getChild()) {
			if (scoreCount.get(c.getSnakes().get(0).getHead()) == null) {
				scoreCount.put(c.getSnakes().get(0).getHead(), c.getScoreRatio());
			}else if(scoreCount.get(c.getSnakes().get(0).getHead()) > c.getScoreRatio()) {
				scoreCount.put(c.getSnakes().get(0).getHead(),c.getScoreRatio());
			}
			
		}
		
		int nb = Integer.MAX_VALUE;
		for (final GammaNode c : root.getChild()) {
			
			
			
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

	private GammaNode lastChance(final GammaNode root) {
		
		GammaNode ret = null;
		int nb = 0;
		final HashMap<Integer,Integer> headCount = new HashMap<>();
		
		for (final GammaNode c : root.getChild()) {
			if (headCount.get(c.getSnakes().get(0).getHead()) == null) {
				headCount.put(c.getSnakes().get(0).getHead(), c.getChildCount());
			}else {
				headCount.put(c.getSnakes().get(0).getHead(),headCount.get(c.getSnakes().get(0).getHead()) + c.getChildCount());
			}
			
		}
		
		for (final GammaNode c : root.getChild()) {
			
		
			 if ( headCount.get(c.getSnakes().get(0).getHead()) > nb ) {
				 nb =headCount.get(c.getSnakes().get(0).getHead());
				 ret = c;
			 }
		}
		

		return ret;
	}

	private GammaNode chooseBestMove(GammaNode root) {
		// double score =-200;
		final ArrayList<GammaNode> child = (ArrayList<GammaNode>) root.getChild();
		GammaNode winner = null;
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
		int move =0;
		if (wup != Double.MAX_VALUE) {
			LOG.info("down" + wup);
			if (wup > choiceValue) {
				choiceValue = wup;
				move = head-1;
			}
		}
		if (wdown != Double.MAX_VALUE) {
			LOG.info("up" + wdown);
			if (wdown > choiceValue) {
				choiceValue = wdown;
				move = head+1;
			}
		}
		if (wleft != Double.MAX_VALUE) {
			LOG.info("left" + wleft);
			if (wleft > choiceValue) {
				choiceValue = wleft;
				move = head-1000;
			}
		}
		if (wright != Double.MAX_VALUE) {
			LOG.info("right" + wright);
			if (wright > choiceValue) {
				choiceValue = wright;
				move = head+1000;
			}
		}

		for (int i = 0; i < child.size(); i++) {
			final double c = child.get(i).getScoreRatio();
			if (c == choiceValue && child.get(i).getSnakes().get(0).isAlive() && move == child.get(i).getSnakes().get(0).getHead() ) {
				winner = child.get(i);
				i = child.size();
			}

		}

		return winner;
	}
/*
	private GammaNode chooseBestMove(GammaNode root) {
		// double score =-200;
		final ArrayList<GammaNode> child = (ArrayList<GammaNode>) root.getChild();
		GammaNode winner = null;
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
		int move =0;
		if (wup != Double.MAX_VALUE) {
			LOG.info("up" + wup);
			if (wup > choiceValue) {
				choiceValue = wup;
				move = head-1;
			}
		}
		if (wdown != Double.MAX_VALUE) {
			LOG.info("down" + wdown);
			if (wdown > choiceValue) {
				choiceValue = wdown;
				move = head+1;
			}
		}
		if (wleft != Double.MAX_VALUE) {
			LOG.info("left" + wleft);
			if (wleft > choiceValue) {
				choiceValue = wleft;
				move = head-1000;
			}
		}
		if (wright != Double.MAX_VALUE) {
			LOG.info("right" + wright);
			if (wright > choiceValue) {
				choiceValue = wright;
				move = head+1000;
			}
		}
	
		for (int i = 0; i < child.size(); i++) {
			final double c = child.get(i).getScoreRatio();
			if (c == choiceValue && child.get(i).getSnakes().get(0).isAlive() && move == child.get(i).getSnakes().get(0).getHead() ) {
				winner = child.get(i);
				i = child.size();
			}
	
		}
	
		return winner;
	}
*/
	@Override
	protected void setFileConfig() {
		fileConfig = "Gamma.properties";
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

		//String test = " {\"you\":{\"latency\":\"354.294\",\"shout\":\"\",\"body\":[{\"y\":8,\"x\":3},{\"y\":8,\"x\":4},{\"y\":7,\"x\":4},{\"y\":6,\"x\":4},{\"y\":6,\"x\":5},{\"y\":6,\"x\":6},{\"y\":5,\"x\":6},{\"y\":5,\"x\":7},{\"y\":5,\"x\":8},{\"y\":4,\"x\":8},{\"y\":3,\"x\":8},{\"y\":2,\"x\":8},{\"y\":2,\"x\":7},{\"y\":1,\"x\":7},{\"y\":1,\"x\":8},{\"y\":1,\"x\":9},{\"y\":2,\"x\":9},{\"y\":2,\"x\":10},{\"y\":3,\"x\":10},{\"y\":3,\"x\":9},{\"y\":4,\"x\":9},{\"y\":5,\"x\":9},{\"y\":6,\"x\":9},{\"y\":7,\"x\":9},{\"y\":8,\"x\":9},{\"y\":9,\"x\":9},{\"y\":9,\"x\":8},{\"y\":9,\"x\":7},{\"y\":9,\"x\":6},{\"y\":9,\"x\":5},{\"y\":8,\"x\":5},{\"y\":8,\"x\":6},{\"y\":8,\"x\":7}],\"id\":\"48f46155-c035-4a0e-a2b8-8e8734d5a8e8\",\"health\":43,\"length\":33,\"name\":\"gamme\",\"head\":{\"y\":8,\"x\":3}},\"turn\":479,\"board\":{\"snakes\":[{\"latency\":\"354.294\",\"shout\":\"\",\"body\":[{\"y\":8,\"x\":3},{\"y\":8,\"x\":4},{\"y\":7,\"x\":4},{\"y\":6,\"x\":4},{\"y\":6,\"x\":5},{\"y\":6,\"x\":6},{\"y\":5,\"x\":6},{\"y\":5,\"x\":7},{\"y\":5,\"x\":8},{\"y\":4,\"x\":8},{\"y\":3,\"x\":8},{\"y\":2,\"x\":8},{\"y\":2,\"x\":7},{\"y\":1,\"x\":7},{\"y\":1,\"x\":8},{\"y\":1,\"x\":9},{\"y\":2,\"x\":9},{\"y\":2,\"x\":10},{\"y\":3,\"x\":10},{\"y\":3,\"x\":9},{\"y\":4,\"x\":9},{\"y\":5,\"x\":9},{\"y\":6,\"x\":9},{\"y\":7,\"x\":9},{\"y\":8,\"x\":9},{\"y\":9,\"x\":9},{\"y\":9,\"x\":8},{\"y\":9,\"x\":7},{\"y\":9,\"x\":6},{\"y\":9,\"x\":5},{\"y\":8,\"x\":5},{\"y\":8,\"x\":6},{\"y\":8,\"x\":7}],\"id\":\"48f46155-c035-4a0e-a2b8-8e8734d5a8e8\",\"health\":43,\"length\":33,\"name\":\"gamme\",\"head\":{\"y\":8,\"x\":3}},{\"latency\":\"358.727\",\"shout\":\"\",\"body\":[{\"y\":8,\"x\":1},{\"y\":8,\"x\":2},{\"y\":9,\"x\":2},{\"y\":9,\"x\":1},{\"y\":10,\"x\":1},{\"y\":10,\"x\":0},{\"y\":9,\"x\":0},{\"y\":8,\"x\":0},{\"y\":7,\"x\":0},{\"y\":6,\"x\":0},{\"y\":5,\"x\":0},{\"y\":4,\"x\":0},{\"y\":3,\"x\":0},{\"y\":2,\"x\":0},{\"y\":1,\"x\":0},{\"y\":1,\"x\":1},{\"y\":0,\"x\":1},{\"y\":0,\"x\":2},{\"y\":0,\"x\":3},{\"y\":0,\"x\":4},{\"y\":0,\"x\":5},{\"y\":1,\"x\":5},{\"y\":2,\"x\":5},{\"y\":2,\"x\":4},{\"y\":2,\"x\":3},{\"y\":3,\"x\":3},{\"y\":4,\"x\":3},{\"y\":4,\"x\":2},{\"y\":5,\"x\":2},{\"y\":6,\"x\":2},{\"y\":6,\"x\":3},{\"y\":7,\"x\":3}],\"id\":\"7b64b8e1-928c-40e3-abd1-2e6e83f6c464\",\"health\":52,\"length\":32,\"name\":\"Old\",\"head\":{\"y\":8,\"x\":1}}],\"width\":11,\"hazards\":[],\"height\":11,\"food\":[{\"y\":1,\"x\":4},{\"y\":0,\"x\":10},{\"y\":0,\"x\":7},{\"y\":6,\"x\":8}]},\"game\":{\"ruleset\":{\"name\":\"standard\",\"version\":\"Mojave/3.1\"},\"timeout\":500,\"id\":\"902a7f7f-3a2d-4b13-bfe7-070e16c5f7ab\"}}";
		String test = "{\"you\":{\"latency\":\"434.595\",\"shout\":\"\",\"body\":[{\"y\":6,\"x\":9},{\"y\":6,\"x\":10},{\"y\":7,\"x\":10},{\"y\":7,\"x\":11},{\"y\":8,\"x\":11},{\"y\":9,\"x\":11},{\"y\":10,\"x\":11},{\"y\":11,\"x\":11},{\"y\":11,\"x\":12},{\"y\":12,\"x\":12},{\"y\":12,\"x\":13},{\"y\":13,\"x\":13},{\"y\":14,\"x\":13},{\"y\":14,\"x\":14},{\"y\":14,\"x\":15},{\"y\":15,\"x\":15},{\"y\":15,\"x\":14},{\"y\":15,\"x\":13},{\"y\":16,\"x\":13}],\"id\":\"48f46155-c035-4a0e-a2b8-8e8734d5a8e8\",\"health\":87,\"length\":19,\"name\":\"gamme\",\"head\":{\"y\":6,\"x\":9}},\"turn\":373,\"board\":{\"snakes\":[{\"latency\":\"434.595\",\"shout\":\"\",\"body\":[{\"y\":6,\"x\":9},{\"y\":6,\"x\":10},{\"y\":7,\"x\":10},{\"y\":7,\"x\":11},{\"y\":8,\"x\":11},{\"y\":9,\"x\":11},{\"y\":10,\"x\":11},{\"y\":11,\"x\":11},{\"y\":11,\"x\":12},{\"y\":12,\"x\":12},{\"y\":12,\"x\":13},{\"y\":13,\"x\":13},{\"y\":14,\"x\":13},{\"y\":14,\"x\":14},{\"y\":14,\"x\":15},{\"y\":15,\"x\":15},{\"y\":15,\"x\":14},{\"y\":15,\"x\":13},{\"y\":16,\"x\":13}],\"id\":\"48f46155-c035-4a0e-a2b8-8e8734d5a8e8\",\"health\":87,\"length\":19,\"name\":\"gamme\",\"head\":{\"y\":6,\"x\":9}},{\"latency\":\"0.12375100003555\",\"body\":[{\"y\":7,\"x\":6},{\"y\":7,\"x\":5},{\"y\":8,\"x\":5},{\"y\":9,\"x\":5},{\"y\":10,\"x\":5},{\"y\":11,\"x\":5},{\"y\":12,\"x\":5},{\"y\":13,\"x\":5},{\"y\":14,\"x\":5},{\"y\":15,\"x\":5},{\"y\":15,\"x\":6},{\"y\":14,\"x\":6},{\"y\":14,\"x\":7},{\"y\":14,\"x\":8},{\"y\":15,\"x\":8},{\"y\":16,\"x\":8},{\"y\":17,\"x\":8},{\"y\":17,\"x\":9},{\"y\":17,\"x\":10},{\"y\":16,\"x\":10}],\"id\":\"3f978173-0f82-4052-af28-8c537d4fd65b\",\"health\":82,\"length\":20,\"name\":\"Robo\",\"head\":{\"y\":7,\"x\":6}}],\"width\":19,\"hazards\":[],\"height\":19,\"food\":[{\"y\":2,\"x\":0},{\"y\":17,\"x\":2},{\"y\":18,\"x\":18},{\"y\":0,\"x\":10},{\"y\":0,\"x\":13},{\"y\":16,\"x\":18},{\"y\":1,\"x\":12},{\"y\":5,\"x\":17},{\"y\":17,\"x\":3},{\"y\":9,\"x\":1},{\"y\":16,\"x\":0},{\"y\":18,\"x\":11},{\"y\":17,\"x\":17},{\"y\":18,\"x\":1},{\"y\":0,\"x\":14},{\"y\":17,\"x\":11},{\"y\":6,\"x\":2},{\"y\":10,\"x\":1},{\"y\":16,\"x\":1},{\"y\":0,\"x\":8},{\"y\":3,\"x\":2},{\"y\":15,\"x\":12},{\"y\":10,\"x\":14},{\"y\":1,\"x\":8},{\"y\":11,\"x\":15},{\"y\":6,\"x\":17},{\"y\":9,\"x\":16},{\"y\":18,\"x\":16},{\"y\":14,\"x\":2}]},\"game\":{\"ruleset\":{\"name\":\"standard\",\"version\":\"Mojave/3.1\"},\"timeout\":500,\"id\":\"5c8095ef-cbf9-4819-8f27-ef6cdfc11dec\"}}";
	//	String second="{\"you\":{\"latency\":\"202.591\",\"shout\":\"\",\"body\":[{\"y\":10,\"x\":8},{\"y\":10,\"x\":9},{\"y\":10,\"x\":10},{\"y\":9,\"x\":10},{\"y\":9,\"x\":9},{\"y\":9,\"x\":8},{\"y\":9,\"x\":7},{\"y\":9,\"x\":6},{\"y\":8,\"x\":6},{\"y\":8,\"x\":5},{\"y\":9,\"x\":5},{\"y\":9,\"x\":4},{\"y\":8,\"x\":4},{\"y\":8,\"x\":3},{\"y\":8,\"x\":2},{\"y\":8,\"x\":1},{\"y\":7,\"x\":1},{\"y\":6,\"x\":1},{\"y\":6,\"x\":2},{\"y\":6,\"x\":3},{\"y\":5,\"x\":3},{\"y\":5,\"x\":4}],\"id\":\"48f46155-c035-4a0e-a2b8-8e8734d5a8e8\",\"health\":92,\"length\":22,\"name\":\"gamme\",\"head\":{\"y\":10,\"x\":8}},\"turn\":390,\"board\":{\"snakes\":[{\"latency\":\"202.591\",\"shout\":\"\",\"body\":[{\"y\":10,\"x\":8},{\"y\":10,\"x\":9},{\"y\":10,\"x\":10},{\"y\":9,\"x\":10},{\"y\":9,\"x\":9},{\"y\":9,\"x\":8},{\"y\":9,\"x\":7},{\"y\":9,\"x\":6},{\"y\":8,\"x\":6},{\"y\":8,\"x\":5},{\"y\":9,\"x\":5},{\"y\":9,\"x\":4},{\"y\":8,\"x\":4},{\"y\":8,\"x\":3},{\"y\":8,\"x\":2},{\"y\":8,\"x\":1},{\"y\":7,\"x\":1},{\"y\":6,\"x\":1},{\"y\":6,\"x\":2},{\"y\":6,\"x\":3},{\"y\":5,\"x\":3},{\"y\":5,\"x\":4}],\"id\":\"48f46155-c035-4a0e-a2b8-8e8734d5a8e8\",\"health\":92,\"length\":22,\"name\":\"gamme\",\"head\":{\"y\":10,\"x\":8}},{\"latency\":\"205.698\",\"shout\":\"\",\"body\":[{\"y\":7,\"x\":9},{\"y\":7,\"x\":10},{\"y\":6,\"x\":10},{\"y\":6,\"x\":9},{\"y\":5,\"x\":9},{\"y\":5,\"x\":8},{\"y\":6,\"x\":8},{\"y\":6,\"x\":7},{\"y\":5,\"x\":7},{\"y\":5,\"x\":6},{\"y\":4,\"x\":6},{\"y\":4,\"x\":7},{\"y\":3,\"x\":7},{\"y\":2,\"x\":7},{\"y\":2,\"x\":6},{\"y\":2,\"x\":5},{\"y\":3,\"x\":5},{\"y\":3,\"x\":4},{\"y\":4,\"x\":4},{\"y\":4,\"x\":3},{\"y\":3,\"x\":3},{\"y\":3,\"x\":2},{\"y\":3,\"x\":1},{\"y\":2,\"x\":1},{\"y\":2,\"x\":2},{\"y\":1,\"x\":2},{\"y\":1,\"x\":3},{\"y\":1,\"x\":4},{\"y\":0,\"x\":4},{\"y\":0,\"x\":5},{\"y\":0,\"x\":6},{\"y\":0,\"x\":7},{\"y\":0,\"x\":8},{\"y\":0,\"x\":9},{\"y\":1,\"x\":9},{\"y\":1,\"x\":8},{\"y\":2,\"x\":8},{\"y\":2,\"x\":9}],\"id\":\"f196374a-3c90-4dd3-acbc-e2e058f234da\",\"health\":83,\"length\":38,\"name\":\"Old\",\"head\":{\"y\":7,\"x\":9}}],\"width\":11,\"hazards\":[],\"height\":11,\"food\":[{\"y\":10,\"x\":0},{\"y\":7,\"x\":0},{\"y\":0,\"x\":1},{\"y\":3,\"x\":0},{\"y\":7,\"x\":2},{\"y\":2,\"x\":3},{\"y\":3,\"x\":8}]},\"game\":{\"ruleset\":{\"name\":\"standard\",\"version\":\"Mojave/3.1\"},\"timeout\":500,\"id\":\"120b295f-e99e-4b2e-8bc6-0eebcb233ef5\"}}";
		try {
			JsonNode parsedRequest = json.readTree(test);
			int size = parsedRequest.get("board").get("height").asInt();
			GammaSnake t = new GammaSnake(LoggerFactory.getLogger(GammaSnake.class),"test");

			t.heigth = size;
			t.width = size;
			t.timeout = 100000;
			t.multiThread = true;
			

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
				System.out.println(t.move(parsedRequest));
				try {
					Thread.sleep(510);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			//	System.out.println(t.move(json.readTree(second)));

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
