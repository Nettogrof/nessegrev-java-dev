package ai.nettogrof.battlesnake.snakes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstant;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import gnu.trove.list.array.TFloatArrayList;

public abstract class AbstractTreeSearchSnakeAI extends AbstractSnakeAI{
	protected int width;
	protected int heigth;
	
	public int timeout = 300;
	protected transient int apiversion ;
	protected transient boolean multiThread;
	protected transient String losing ="I have a bad feeling about this";
	protected transient String winning = "I'm your father";
	protected transient int minusbuffer = 250;
	protected transient long nodeTotalCount;
	protected transient long timeTotal;
	public transient int api;
	protected static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	/**
	 * 
	 */
	protected  transient AbstractNode lastRoot;
	
	public AbstractTreeSearchSnakeAI() {
		super();
	}

	public AbstractTreeSearchSnakeAI(final String gameId) {
		super( gameId);
		setFileConfig();
		try (InputStream input =Files.newInputStream(Paths.get(getFileConfig()))) {

            final Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            apiversion= Integer.parseInt(prop.getProperty("apiversion"));
            minusbuffer= Integer.parseInt(prop.getProperty("minusbuffer"));
            multiThread = Boolean.parseBoolean(prop.getProperty("multiThread"));
            
           
            final Random rand = new Random();
            losing = BattleSnakeConstant.LOSE_SHOUT[rand.nextInt(BattleSnakeConstant.LOSE_SHOUT.length)];
            winning = BattleSnakeConstant.WIN_SHOUT[rand.nextInt(BattleSnakeConstant.WIN_SHOUT.length)];
           
            

        } catch (IOException ex) {
        	log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());          
        }
	}
	
	

	protected abstract String getFileConfig();

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
			response.put("author", "nettogrof");

		} catch (IOException ex) {
			log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());      
		}

		return response;
	}
	
	@Override
	public Map<String, String> end(final JsonNode endRequest) {
		final Map<String, String> response = new ConcurrentHashMap<>();

		if (endRequest.get("board").get("snakes").size() > 0){
			log.atInfo().log("Winner is : %s", endRequest.get("board").get("snakes").get(0).get("name").asText());
			
		} else {
			log.atInfo().log("DRAW");

		}
				
		log.atInfo().log("Average node/s : "+(nodeTotalCount/timeTotal*1000));

		return response;
	}
	
	protected AbstractNode finishHim(final AbstractNode root, final AbstractNode winner) {

		AbstractNode ret = null;

		final ConcurrentHashMap<Integer, Float> scoreCount = new ConcurrentHashMap<>();

		for (final AbstractNode c : root.getChild()) {
			if (scoreCount.get(c.getSnakes().get(0).getHead()) == null) {
				scoreCount.put(c.getSnakes().get(0).getHead(), c.getScoreRatio());
			} else if (scoreCount.get(c.getSnakes().get(0).getHead()) > c.getScoreRatio()) {
				scoreCount.put(c.getSnakes().get(0).getHead(), c.getScoreRatio());
			}

		}

		int numberChild = Integer.MAX_VALUE;
		for (final AbstractNode c : root.getChild()) {

			if (c.getChildCount() < numberChild && scoreCount.get(c.getSnakes().get(0).getHead()) > 100) {
				ret = c;
				numberChild = c.getChildCount();
			}
		}
		if (ret == null) {
			ret = winner;
		}
		return ret;
	}
	
	protected AbstractNode lastChance(final AbstractNode root) {

		AbstractNode ret = null;
		float score = 0;
		

		for (final AbstractNode c : root.getChild()) {

			if (c.getScoreRatio() > score) {
				score = c.getScoreRatio();
				ret = c;
			}
		}
		if (ret == null) {
			int numberChild =0;
			for (final AbstractNode c : root.getChild()) {

				if (c.getChildCount() > numberChild) {
					numberChild = c.getChildCount();
					ret = c;
				}
			}
		}
		
		if (ret == null) {
			return root.getChild().get(0);
		}

		return ret;
	}
	
	protected AbstractNode chooseBestMove(final AbstractNode root) {
		// double score =-200;
		final List<AbstractNode> child =   root.getChild();
		AbstractNode winner = null;
		final TFloatArrayList upward = new TFloatArrayList();
		final TFloatArrayList down = new TFloatArrayList();
		final TFloatArrayList left = new TFloatArrayList();
		final TFloatArrayList right = new TFloatArrayList();
		fillList(upward,down,left,right,root);
		
		float choiceValue = -1;
		int move =0;
		final StringBuilder logtext = new StringBuilder();
		final int head = root.getSnakes().get(0).getHead();
		if (!upward.isEmpty()) {
			logtext.append("\nup ").append(upward.min());
			if (upward.min() > choiceValue) {
				choiceValue = upward.min();
				move = head+1;
			}
		}
		if (!down.isEmpty()) {
			logtext.append("\ndown ").append(down.min());
			if (down.min() > choiceValue) {
				choiceValue = down.min();
				move = head-1;
			}
		}
		if (!left.isEmpty()) {
			logtext.append("\nleft ").append( left.min());
			if ( left.min()> choiceValue) {
				choiceValue = left.min();
				move = head-1000;
			}
		}
		if (!right.isEmpty()) {
			logtext.append("\nright ").append(right.min());
			if (right.min() > choiceValue) {
				choiceValue = right.min();
				move = head+1000;
			}
		}
		
		log.atInfo().log(logtext.toString());

		for (int i = 0; i < child.size() && winner==null; i++) {
			final float childScoreRatio = child.get(i).getScoreRatio();
			if (childScoreRatio == choiceValue && child.get(i).getSnakes().get(0).isAlive() && move == child.get(i).getSnakes().get(0).getHead() ) {
				winner =  child.get(i);
			
			}

		}

		return winner;
	}
	
	protected float getbestChildValue(final TFloatArrayList upward, final TFloatArrayList down,
			final TFloatArrayList left, final TFloatArrayList right) {
		float temp;
		float choiceValue = Float.MIN_VALUE;
		if (!upward.isEmpty()) {
			choiceValue = upward.min();

		}

		if (!down.isEmpty()) {
			temp = down.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}

		if (!left.isEmpty()) {
			temp = left.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}

		if (!right.isEmpty()) {
			temp = right.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}
		return choiceValue;
	}
	
	private void fillList(final TFloatArrayList upward,final TFloatArrayList down,final TFloatArrayList left,final TFloatArrayList right,final AbstractNode node) {
		final int head = node.getSnakes().get(0).getHead();

		for (int i = 0; i < node.getChild().size(); i++) {
			if (node.getChild().get(i).exp) {
				final int move = node.getChild().get(i).getSnakes().get(0).getHead();

				if (move / 1000 < head / 1000) {
					left.add(node.getChild().get(i).getScoreRatio());
				} else if (move / 1000 > head / 1000) {
					right.add(node.getChild().get(i).getScoreRatio());
				} else if (move % 1000 < head % 1000) {
					down.add(node.getChild().get(i).getScoreRatio());
				} else {
					upward.add(node.getChild().get(i).getScoreRatio());
				}
			}
		}
		
	}
	
	/**
	 * @param winner
	 * @param root
	 * @param head
	 * @return
	 */
	protected Map<String, String> generateResponse(final AbstractNode winner,final AbstractNode root,final JsonNode head) {
		final Map<String, String> response = new ConcurrentHashMap<>();
		String res;
		if (winner == null) {
			response.put("shout", losing);
			res = DOWN;
		} else {
			AbstractNode choosenNode = winner;
			if (winner.getScoreRatio() < 0.001) {
				response.put("shout", losing);
				choosenNode = lastChance(root);
			} else if (winner.getScoreRatio() > 8) {
				response.put("shout", winning);
				choosenNode = finishHim(root, winner);
			}

			final int move = choosenNode.getSnakes().get(0).getHead();

			final int snakex = head.get("x").asInt();
			
			if (move/1000 < snakex) {
				res = LEFT;
			} else if (move/1000 > snakex) {
				res = RIGHT;
			} else if (move%1000 < head.get("y").asInt()) {
				res = api == 1 ? DOWN : UPWARD	;
			} else {
				res = api == 1 ? UPWARD : DOWN;
			}
		
			
		}
		response.put(MOVESTR, res);
		return response;
	}

}
