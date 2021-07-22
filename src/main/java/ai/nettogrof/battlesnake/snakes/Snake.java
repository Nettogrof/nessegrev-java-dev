package ai.nettogrof.battlesnake.snakes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.nettogrof.battlesnake.snakes.common.CorsFilterUtils;
import ai.nettogrof.battlesnake.snakes.common.SnakeGeneticConstants;

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
 * Snake server that deals with requests from the snake engine. This class route
 * the incoming transaction to the right object/method.
 * 
 * Based on the starter-snake-java of Battlesnake
 * 
 * @version Spring 2021
 */
public final class Snake {
	/**
	 * Mapper to transform JSON into java object
	 */
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	/**
	 * Handle the API calls
	 */
	private static final Handler HANDLER = new Handler();

	/**
	 * Logger
	 */
	private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

	/**
	 * Map of current running bots
	 */
	private static Map<String, AbstractSnakeAI> bots = new ConcurrentHashMap<>();

	/**
	 * SnakeType define by program parameter Default: undefined
	 */
	private static String snakeType = "undefined";

	/**
	 * Port number define by config file or program parameter. Default: 8081
	 */
	private static String port = "8081";

	/**
	 * Unused constrictor
	 */
	private Snake() {
	}

	/**
	 * Main entry point.
	 *
	 * @param args first parameter snakeType (required), second parameter port
	 *             (optional)
	 */
	public static void main(final String[] args) {

		if (args.length == 1) {
			snakeType = args[0];
			loadProperties(snakeType);
		} else {
			LOG.atInfo().log("Must provide java args  SnakeType");
		}

		LOG.atInfo().log("Using  port: " + port);

		port(Integer.parseInt(port));
		CorsFilterUtils.apply();

		get("/", HANDLER::process, JSON_MAPPER::writeValueAsString);
		post("/start", HANDLER::process, JSON_MAPPER::writeValueAsString);
		post("/ping", HANDLER::process, JSON_MAPPER::writeValueAsString);
		post("/move", HANDLER::process, JSON_MAPPER::writeValueAsString);
		post("/end", HANDLER::process, JSON_MAPPER::writeValueAsString);

		loadEvaluationValue();
	}

	/**
	 * Load Snake properties to get port number
	 * 
	 * @param snakeType Which type of snake
	 */
	private static void loadProperties(final String snakeType) {
		try (InputStream input = Files.newInputStream(Paths.get(snakeType + ".properties"))) {

			final Properties prop = new Properties();

			prop.load(input);

			port = prop.getProperty("port");
		} catch (IOException ex) {
			LOG.atWarning().log(ex.getMessage());
		}
	}

	/**
	 * Load Snake properties to get port number
	 * 
	 * @param snakeType Which type of snake
	 */
	private static void loadEvaluationValue() {
		try (InputStream input = Files.newInputStream(Paths.get("evaluation.properties"))) {

			final Properties prop = new Properties();

			prop.load(input);

			SnakeGeneticConstants.setStopExpandLimit(Float.parseFloat(prop.getProperty("stopExpandLimit")));
			SnakeGeneticConstants.setBorderScore(Float.parseFloat(prop.getProperty("borderScore")));
			SnakeGeneticConstants.setMCTS(Float.parseFloat(prop.getProperty("mctsBias")));
			SnakeGeneticConstants.setFoodValue(Integer.parseInt(prop.getProperty("foodValue")));
			SnakeGeneticConstants.setTailValue(Integer.parseInt(prop.getProperty("tailValue")));
			LOG.atInfo().log("Evalution Value loaded successfully");
		} catch (IOException ex) {
			LOG.atWarning().log("Issue with the evaluation.properties file");
		}
	}

	/**
	 * Handler class for dealing with the routes set up in the main method.
	 */
	public static class Handler {

		/**
		 * Empty response
		 */
		private static final Map<String, String> EMPTY = new ConcurrentHashMap<>();

		/**
		 * Generic processor that prints out the request and response from the methods.
		 *
		 * @param req Request received
		 * @param res Response object
		 * @return map of field,value
		 */
		public Map<String, String> process(final Request req, final Response res) {

			try {

				final String uri = req.uri();
				LOG.atInfo().log("%s called with: %s", uri, req.body());
				Map<String, String> snakeResponse;
				final JsonNode parsedRequest = JSON_MAPPER.readTree(req.body());
				switch (uri) {

				case "/ping":
					snakeResponse = ping();
					break;
				case "/":
					snakeResponse = root();
					break;
				case "/start":
					snakeResponse = start(parsedRequest);
					break;
				case "/move":
					snakeResponse = move(parsedRequest);
					break;
				case "/end":
					snakeResponse = end(parsedRequest);
					break;
				default:
					throw new IllegalAccessError("Strange call made to the snake: " + uri);
				}

				LOG.atInfo().log("Responding with: %s", JSON_MAPPER.writeValueAsString(snakeResponse));
				return snakeResponse;
			} catch (IOException e) {
				LOG.atWarning().log("Something went wrong!", e);
				return null;
			}
		}

		/**
		 * /ping is called by the play application during the tournament or on
		 * play.battlesnake.io to make sure your snake is still alive. API v0
		 *
		 * @return an empty response.
		 */
		public Map<String, String> ping() {
			bots.forEach((s, snake) -> {
				snake.ping();
			});
			return EMPTY;
		}

		/**
		 * / is called by the play application during the tournament or on
		 * play.battlesnake.io to make sure your snake is still alive.
		 *
		 * RootRequest a map containing the JSON sent to this snake. See the spec for
		 * details of what this contains.
		 * 
		 * @return apiversion:string - Battlesnake API Version implemented by this
		 *         Battlesnake author:string - Optional username of the Battlesnake’s
		 *         author head:string - Optional custom head for this Battlesnake
		 *         tail:string - Optional custom tail for this Battlesnake color:string
		 *         - Optional custom color for this Battlesnake .
		 */
		public Map<String, String> root() {
			switch (snakeType) {
			case "FloodFill":
				return new FloodFillSnake().getInfo();
			/*
			 * case "Alpha": return new AlphaSnake().getInfo();
			 */
			case "Beta":
				return new BetaSnake().getInfo();
			case "Gamma":
				return new GammaSnake().getInfo();
			case "Basic":
				return new BasicSnake().getInfo();
			case "Right":
				return new RightSnake().getInfo();
			case "Left":
				return new LeftSnake().getInfo();
			case "JustTurn":
				return new JustTurnSnake().getInfo();
			/*
			 * case "Challenger": return new Challenger().getInfo();
			 */
			case "Solo":
				return new SoloSnake().getInfo();
			default:
				return new BasicSnake().getInfo();
			}

		}

		/**
		 * /start is called by the engine when a game is first run.
		 *
		 * @param startRequest a map containing the JSON sent to this snake.
		 * @return a response back to the engine containing the snake setup values.
		 */
		public Map<String, String> start(final JsonNode startRequest) {
			final String gameId = startRequest.get("game").get("id").asText();
			switch (snakeType) {
			case "FloodFill":
				bots.put(gameId, new FloodFillSnake(gameId));
				break;
			case "Basic":
				bots.put(gameId, new BasicSnake(gameId));
				break;
			/*
			 * case "Alpha": bots.put(gameId, new AlphaSnake(gameId)); break;
			 */
			case "Beta":
				bots.put(gameId, new BetaSnake(gameId));
				break;
			case "Gamma":
				bots.put(gameId, new GammaSnake(gameId));
				break;
			case "Right":
				bots.put(gameId, new RightSnake(gameId));
				break;
			case "Left":
				bots.put(gameId, new LeftSnake(gameId));
				break;
			case "JustTurn":
				bots.put(gameId, new JustTurnSnake(gameId));
				break;
			/*
			 * case "Challenger": bots.put(gameId, new Challenger(gameId)); break;
			 */
			case "Solo":
				bots.put(gameId, new SoloSnake(gameId));
				break;
			default:
				LOG.atWarning().log("No snake type defined,  using Beta as default!");
				bots.put(gameId, new BetaSnake(gameId));
				break;
			}

			return bots.get(gameId).start(startRequest);
		}

		/**
		 * /move is called by the engine for each turn the snake has.
		 *
		 * @param moveRequest a map containing the JSON sent to this snake. See the spec
		 *                    for details of what this contains.
		 * @return a response back to the engine containing snake movement values.
		 */
		public Map<String, String> move(final JsonNode moveRequest) {
			final String gameId = moveRequest.get("game").get("id").asText();
			AbstractSnakeAI bot = bots.get(gameId);
			if (bot == null) {
				bots.put(gameId, new BetaSnake(gameId));
				bot = bots.get(gameId);
			}
			return bot.move(moveRequest);

		}

		/**
		 * /end is called by the engine when a game is complete.
		 *
		 * @param endRequest a map containing the JSON sent to this snake. See the spec
		 *                   for details of what this contains.
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
