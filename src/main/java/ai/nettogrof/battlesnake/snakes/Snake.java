package ai.nettogrof.battlesnake.snakes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.nettogrof.battlesnake.snakes.common.CorsFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

/**
 * Snake server that deals with requests from the snake engine.
 * Just boiler plate code.  See the readme to get started.
 * It follows the spec here:
 * https://github.com/battlesnakeio/docs/tree/master/apis/snake
 */
public class Snake {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Handler HANDLER = new Handler();
    private static final Logger LOG = LoggerFactory.getLogger(Snake.class);
    private static Map<String, SnakeAI> bots = new ConcurrentHashMap<>();
    public static String snakeType="FloodFill";
    
    private static String color ;
    private static String headType;
    private static String tailType;
    private static String port = "8081";
    /**
     * Main entry point.
     *
     * @param args are ignored.
     */
    public static void main(String[] args) {
    	
    	
    	if (args.length ==2) {
	    	
	    	snakeType = args[0];
	        port = args[1];
	    	
    	} else if(args.length ==1) {
    		snakeType = args[0];
    		loadProperties(snakeType);
    	}else {
    		System.out.println("Must provide java args  SnakeType");
    	}
    	
    	
    	
        if (port == null) {
        	 LOG.info("Using default port: {}", port);
             port = "8081";
            
        } else {
        	LOG.info("Found system provided port: {}", port);
        }
        port(Integer.parseInt(port));
        CorsFilter.apply();
        
        get("/",  HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/start", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/ping", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/move", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/end", HANDLER::process, JSON_MAPPER::writeValueAsString);
       
    }
    private static void loadProperties(String snakeType) {
		try (InputStream input = new FileInputStream(snakeType+".properties")) {

            final Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            port = prop.getProperty("port");
            color =prop.getProperty("color");
            headType =prop.getProperty("headType");
            tailType =prop.getProperty("tailType");
          
            

        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
    /**
     * Handler class for dealing with the routes set up in the main method.
     */
    public static class Handler {

        /**
         * For the ping request
         */
        private static final Map<String, String> EMPTY = new ConcurrentHashMap<>();
      
        /**
         * Generic processor that prints out the request and response from the methods.
         *
         * @param req
         * @param res
         * @return
         */
        public Map<String, String> process(Request req, Response res) {
        	
            try {
                JsonNode parsedRequest = JSON_MAPPER.readTree(req.body());
                String uri = req.uri();
                LOG.info("{} called with: {}", uri, req.body());
                Map<String, String> snakeResponse;
                if (uri.equals("/start")) {
                    snakeResponse = start(parsedRequest);
                    if (color != null) {
                    	snakeResponse.remove("color");
                    	snakeResponse.put("color", color);
                    }
                    if (headType != null) {
                    	snakeResponse.remove("headType");
                    	snakeResponse.put("headType", headType);
                    }
                    if (tailType != null) {
                    	snakeResponse.remove("tailType");
                    	snakeResponse.put("tailType", tailType);
                    }
                } else if (uri.equals("/ping")) {
                    snakeResponse = ping();
                }else if (uri.equals("/")) {
                    snakeResponse = root();
                } else if (uri.equals("/move")) {
                    snakeResponse = move(parsedRequest);
                } else if (uri.equals("/end")) {
                	
                    snakeResponse = end(parsedRequest);
                } else {
                    throw new IllegalAccessError("Strange call made to the snake: " + uri);
                }
                LOG.info("Responding with: {}", JSON_MAPPER.writeValueAsString(snakeResponse));
                return snakeResponse;
            } catch (IOException e) {
                LOG.warn("Something went wrong!", e);
                return null;
            }
        }

        /**
         * /ping is called by the play application during the tournament or on play.battlesnake.io to make sure your
         * snake is still alive.
         *
         * @param pingRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return an empty response.
         */
        public Map<String, String> ping() {
        	bots.forEach((s,snake)->{
        		snake.ping();
        	});
            return EMPTY;
        }
        
        /**
         * / is called by the play application during the tournament or on play.battlesnake.io to make sure your
         * snake is still alive.
         *
         * @param RootRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return apiversion:string - Battlesnake API Version implemented by this Battlesnake
author:string - Optional username of the Battlesnake’s author
head:string - Optional custom head for this Battlesnake
tail:string - Optional custom tail for this Battlesnake
color:string - Optional custom color for this Battlesnake
.
         */
        public Map<String, String> root() {
        	switch(snakeType) {
        	case "FloodFill" : return FloodFillSnake.getInfo();
        	
        	//case "RL" : bots.put(startRequest.get("game").get("id").asText(), new RL(LOG,startRequest.get("game").get("id").asText() ));break;
        //	case "Neural" : bots.put(startRequest.get("game").get("id").asText(), new NNSnake(LOG,startRequest.get("game").get("id").asText() ));break;
        	case "Alpha": return AlphaSnake.getInfo();
        	case "Beta": return BetaSnake.getInfo();
        	case "Gamma": return GammaSnake.getInfo();
        	case "Basic" : return BasicSnake.getInfo();
        	case "Challenger" : return Challenger.getInfo();
        	default : return BasicSnake.getInfo();
        	}
       
        }

        /**
         * /start is called by the engine when a game is first run.
         *
         * @param startRequest a map containing the JSON sent to this snake. 
         * @return a response back to the engine containing the snake setup values.
         */
        public Map<String, String> start(JsonNode startRequest) {
        	switch(snakeType) {
        	case "FloodFill" : bots.put(startRequest.get("game").get("id").asText(), new FloodFillSnake(LOG,startRequest.get("game").get("id").asText() ));break;
        	case "Basic" : bots.put(startRequest.get("game").get("id").asText(), new BasicSnake(LOG,startRequest.get("game").get("id").asText() ));break;
        	case "RL" : bots.put(startRequest.get("game").get("id").asText(), new RL(LOG,startRequest.get("game").get("id").asText() ));break;
        	case "Neural" : bots.put(startRequest.get("game").get("id").asText(), new NNSnake(LOG,startRequest.get("game").get("id").asText() ));break;
        	case "Alpha": bots.put(startRequest.get("game").get("id").asText(), new AlphaSnake(LOG,startRequest.get("game").get("id").asText() ));break;
        	case "Beta": bots.put(startRequest.get("game").get("id").asText(), new BetaSnake(LOG,startRequest.get("game").get("id").asText() ));break;
        	case "Gamma": bots.put(startRequest.get("game").get("id").asText(), new GammaSnake(LOG,startRequest.get("game").get("id").asText() ));break;
        	case "Challenger": bots.put(startRequest.get("game").get("id").asText(), new Challenger(LOG,startRequest.get("game").get("id").asText() ));break;
        	}
        	
        
            
            return bots.get(startRequest.get("game").get("id").asText()).start(startRequest);
        }

        /**
         * /move is called by the engine for each turn the snake has.
         *
         * @param moveRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return a response back to the engine containing snake movement values.
         */
        public Map<String, String> move(JsonNode moveRequest) {     
        	
        		SnakeAI m = bots.get(moveRequest.get("game").get("id").asText());
        		if (m ==null) {
        			bots.put(moveRequest.get("game").get("id").asText(), new BetaSnake(LOG,moveRequest.get("game").get("id").asText() ));
        			 m = bots.get(moveRequest.get("game").get("id").asText());
        		}
        	 return m.move(moveRequest);
        
        }

        /**
         * /end is called by the engine when a game is complete.
         *
         * @param endRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return responses back to the engine are ignored.
         */
        public Map<String, String> end(JsonNode endRequest) {
        	
        	Map<String, String> res = bots.get(endRequest.get("game").get("id").asText()).end(endRequest);
        	bots.remove(endRequest.get("game").get("id").asText());
        	System.gc();
        	return res;
            
           
        }
    }

}
