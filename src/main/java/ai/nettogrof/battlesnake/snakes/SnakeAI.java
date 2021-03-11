package ai.nettogrof.battlesnake.snakes;

import java.util.Map;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class SnakeAI {
	 String gameId = new String();
	 public abstract Map<String, String> end(JsonNode endRequest) ;
	 Logger LOG;
	 
	 public abstract Map<String, String> move(JsonNode moveRequest);
	 public abstract Map<String, String> start(JsonNode startRequest);
	 //public abstract Map<String, String> root(JsonNode startRequest);
	 //public static abstract Map<String, String> getInfo();
	 public final static String UP = "up";
	 public final static String DOWN = "down";
	 public final static String LEFT = "left";
	 public final static String RIGHT = "right";
	 public final static String MOVE = "move";
	 public static String fileConfig;
	 public SnakeAI() {
	}
	 public SnakeAI(Logger l, String gi) {
		 gameId=gi;
		 LOG=l;
	}
	 
	 public String getId() {
    	 return gameId;
     }
	 
	 public void ping() {};
	 protected abstract void setFileConfig();
	
	 
	
}
