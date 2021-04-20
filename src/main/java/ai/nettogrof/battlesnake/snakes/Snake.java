package ai.nettogrof.battlesnake.snakes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.nettogrof.battlesnake.snakes.common.CorsFilter;

import com.google.common.flogger.FluentLogger;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
public final class Snake {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Handler HANDLER = new Handler();
    private static final FluentLogger LOG = FluentLogger.forEnclosingClass();
    private static Map<String, AbstractSnakeAI> bots = new ConcurrentHashMap<>();
    public static String snakeType="FloodFill";
    
   
    private static String port = "8081";
    
    
    private Snake() {}
    
    /**
     * Main entry point.
     *
     * @param args are ignored.
     */
    public static void main(final String[] args) {
    	
    	
    	if (args.length ==2) {
	    	
	    	snakeType = args[0];
	        port = args[1];
	    	
    	} else if(args.length ==1) {
    		snakeType = args[0];
    		loadProperties(snakeType);
    	}else {
    		 LOG.atInfo().log("Must provide java args  SnakeType");
    	}
    	
    	
    	
        if (port == null) {
        	 port = "8081";
        	 
        	 LOG.atInfo().log("Using default port: " + port);
             
            
        } else {
        	LOG.atInfo().log("Using system provide port: " + port);
        }
        port(Integer.parseInt(port));
        CorsFilter.apply();
        
        get("/",  HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/start", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/ping", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/move", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/end", HANDLER::process, JSON_MAPPER::writeValueAsString);
       
    }
    
    private static void loadProperties(final String snakeType) {
		try (InputStream input = Files.newInputStream(Paths.get(snakeType+".properties"))) {

            final Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            port = prop.getProperty("port");
          
          
            

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
        public Map<String, String> process(final Request req,final Response res) {
        	
            try {
              
                final String uri = req.uri();
                LOG.atInfo().log("%s called with: %s", uri, req.body());
                Map<String, String> snakeResponse;
                final JsonNode parsedRequest = JSON_MAPPER.readTree(req.body());
                switch(uri) {
               
                case "/ping" :snakeResponse = ping(); break;
                case "/" :  snakeResponse = root(); break;
                case "/start" : snakeResponse = start(parsedRequest); break;
                case "/move" : snakeResponse = move(parsedRequest); break;
                case "/end":  snakeResponse = end(parsedRequest); break;
                default:throw new IllegalAccessError("Strange call made to the snake: " + uri);
                }
              
                LOG.atInfo().log("Responding with: %s", JSON_MAPPER.writeValueAsString(snakeResponse));
                return snakeResponse;
            } catch (IOException e) {
            	LOG.atWarning().log("Something went wrong!", e);
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
        public Map<String, String> start(final JsonNode startRequest) {
        	final String gameId= startRequest.get("game").get("id").asText();
        	switch(snakeType) {
        	case "FloodFill" : bots.put(gameId, new FloodFillSnake(gameId ));break;
        	case "Basic" : bots.put(gameId, new BasicSnake(gameId ));break;
        	case "Alpha": bots.put(gameId, new AlphaSnake(gameId ));break;
        	case "Beta": bots.put(gameId, new BetaSnake(gameId ));break;
        	case "Gamma": bots.put(gameId, new GammaSnake(gameId ));break;
        	case "Challenger": bots.put(gameId, new Challenger(gameId ));break;
        	default: bots.put(gameId, new BetaSnake(gameId ));break;
        	}
        	
        
            
            return bots.get(gameId).start(startRequest);
        }

        /**
         * /move is called by the engine for each turn the snake has.
         *
         * @param moveRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return a response back to the engine containing snake movement values.
         */
        public Map<String, String> move(final JsonNode moveRequest) {     
        	final String gameId = moveRequest.get("game").get("id").asText();
        		AbstractSnakeAI bot = bots.get(gameId);
        		if (bot ==null) {
        			bots.put(gameId, new BetaSnake(gameId));
        			 bot = bots.get(gameId);
        		}
        	 return bot.move(moveRequest);
        
        }

        /**
         * /end is called by the engine when a game is complete.
         *
         * @param endRequest a map containing the JSON sent to this snake. See the spec for details of what this contains.
         * @return responses back to the engine are ignored.
         */
        public Map<String, String> end(final JsonNode endRequest) {
        	final String gameId = endRequest.get("game").get("id").asText();
        	final Map<String, String> res = bots.get(gameId).end(endRequest);
        	bots.remove(gameId);
        	System.gc();
        	return res;
            
           
        }
    }

}
