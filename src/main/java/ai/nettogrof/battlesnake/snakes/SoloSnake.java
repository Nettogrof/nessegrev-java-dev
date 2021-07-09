/**
 * 
 */
package ai.nettogrof.battlesnake.snakes;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;

import ai.nettogrof.battlesnake.info.FoodInfo;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.AbstractSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.standard.RegularSearch;
import ai.nettogrof.battlesnake.treesearch.search.standard.challenge.SoloNode;

/**
 * Challenge Snake v2,  this snake is for new challenges
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class SoloSnake extends AbstractTreeSearchSnakeAI {

	/**
	 * Basic / unused constructor
	 */
	public SoloSnake() {
		super();
		fileConfig = "Solo.properties";
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public SoloSnake(final String gameId) {
		super(gameId);
		fileConfig = "Solo.properties";
		setProperties();
	}
	
	/**
	 * This method was used in API v0 to retrieve snake info, but in API v1 the
	 * method is call but Battlesnake doesn't need a response. Beta snake is
	 * compatible for both API version that why it's return snake info
	 * 
	 * @param startRequest Json call received
	 * @return map that can be empty because it will be ignore by BattleSnake server
	 */
	@Override
	public Map<String, String> start(final JsonNode startRequest) {
		if (startRequest.get("ruleset") == null) {
			timeout = 500;
		} else {
			apiversion = 1;
			timeout = startRequest.get("game").get("timeout").asInt();
		}
		ruleset = "standard"; // Gamma Snake, play only standard game.
		try {
			searchType = genSearchType();
		} catch (ReflectiveOperationException e) {
			log.atWarning().log(e.getMessage() + "\n" + e.getStackTrace());
		}

		final Map<String, String> response = new ConcurrentHashMap<>();
		response.put("color", "#216121");
		response.put("headType", "shac-gamer");
		response.put("tailType", "shac-coffee");
		width = startRequest.get(BOARD).get(WIDTH_FIELD).asInt();
		height = startRequest.get(BOARD).get(HEIGHT_FIELD).asInt();

		return response;
	}

	@Override
	protected AbstractNode genRoot(final JsonNode moveRequest) {
		final JsonNode board = moveRequest.get(BOARD);
		final FoodInfo food = new FoodInfo(board);

		final List<SnakeInfo> snakes = new ArrayList<>();
		final JsonNode gammaSnake = moveRequest.get(YOU);

		snakes.add(new SnakeInfo(gammaSnake));
		for (int i = 0; i < board.get(SNAKES).size(); i++) {
			final JsonNode currentSnake = board.get(SNAKES).get(i);
			if (!currentSnake.get("id").asText().equals(gammaSnake.get("id").asText())) {
				snakes.add(new SnakeInfo(currentSnake));
			}
		}

		final AbstractNode oldChild = findChildNewRoot(snakes, food, null);
		return oldChild == null ? genNode(snakes, food) : oldChild;
	}

	
	/**
	 * This method generate the root node type
	 * 
	 * @param snakes List of snakes
	 * @param food   Food Information
	 * @return Abstract node
	 */
	private AbstractNode genNode(final List<SnakeInfo> snakes,final FoodInfo food) {
		AbstractNode.width = width;
		AbstractNode.height = height;
		return new SoloNode(snakes, food);
	}

	@Override
	protected void setFileConfig() {
		fileConfig = "Solo.properties";

	}

	@Override
	protected String getFileConfig() {
		
		return fileConfig;
	}
	
	
	/**
	 * This method generate the search type
	 * 
	 * @return Abstract Search
	 * @throws ReflectiveOperationException In case of invalid search type
	 */
	@Override
	protected Constructor<? extends AbstractSearch> genSearchType() throws ReflectiveOperationException {

		
			return RegularSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class, int.class);
		

	}
	// {"game":{"id":"cc06b47c-3c6d-4604-a484-d3106afec4ec","ruleset":{"name":"solo","version":"v1.0.17"},"timeout":500},"turn":0,"board":{"height":7,"width":7,"snakes":[{"id":"gs_SBmcd77VmGqpf4HQhQcTVfpX","name":"SoloChallenger","latency":"","health":100,"body":[{"x":1,"y":5},{"x":1,"y":5},{"x":1,"y":5}],"head":{"x":1,"y":5},"length":3,"shout":""}],"food":[{"x":2,"y":4},{"x":3,"y":3}],"hazards":[]},"you":{"id":"gs_SBmcd77VmGqpf4HQhQcTVfpX","name":"SoloChallenger","latency":"","health":100,"body":[{"x":1,"y":5},{"x":1,"y":5},{"x":1,"y":5}],"head":{"x":1,"y":5},"length":3,"shout":""}}
	/*
	  public static void main(String args[]) { 
		  ObjectMapper json = new ObjectMapper(); String pretest =
	  "{\"game\":{\"id\":\"24863f8f-ea3a-4485-88ed-aaa5075b1868\",\"ruleset\":{\"name\":\"solo\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":0,\"board\":{\"height\":7,\"width\":7,\"snakes\":[{\"id\":\"gs_qdbxHqtChkCGbgH9GvY9yTdJ\",\"name\":\"SoloChallenger\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":1,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":5}],\"head\":{\"x\":1,\"y\":5},\"length\":3,\"shout\":\"\"}],\"food\":[{\"x\":2,\"y\":4},{\"x\":3,\"y\":3}],\"hazards\":[]},\"you\":{\"id\":\"gs_qdbxHqtChkCGbgH9GvY9yTdJ\",\"name\":\"SoloChallenger\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":1,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":5}],\"head\":{\"x\":1,\"y\":5},\"length\":3,\"shout\":\"\"}}"
	  ; 
		   String test = "{\"game\":{\"id\":\"24863f8f-ea3a-4485-88ed-aaa5075b1868\",\"ruleset\":{\"name\":\"solo\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":0,\"board\":{\"height\":7,\"width\":7,\"snakes\":[{\"id\":\"gs_qdbxHqtChkCGbgH9GvY9yTdJ\",\"name\":\"SoloChallenger\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":1,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":5}],\"head\":{\"x\":1,\"y\":5},\"length\":3,\"shout\":\"\"}],\"food\":[{\"x\":2,\"y\":4},{\"x\":3,\"y\":3}],\"hazards\":[]},\"you\":{\"id\":\"gs_qdbxHqtChkCGbgH9GvY9yTdJ\",\"name\":\"SoloChallenger\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":1,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":5}],\"head\":{\"x\":1,\"y\":5},\"length\":3,\"shout\":\"\"}}"
	  ;
	  
	  try { JsonNode parsedRequest = json.readTree(test); int size =
	  parsedRequest.get(BOARD).get("height").asInt(); 
	  SoloSnake t = new SoloSnake("test");
	  t.ruleset = "solo"; t.height = size; t.width =
	  size; t.timeout = 500; t.multiThread = false;
	  t.cpuLimit=4;
	  
	  try { t.searchType=t.genSearchType(); } catch (ReflectiveOperationException
	  e1) {
	  
	  e1.printStackTrace(); } try { Thread.sleep(100); } catch
	  (InterruptedException e) {
	  
	  e.printStackTrace(); }
	  
	
	  System.out.println(t.start(parsedRequest)); 
	  parsedRequest =	  json.readTree(pretest);
	  System.out.println(t.move(parsedRequest));
	  try {
	  Thread.sleep(510); } catch (InterruptedException e) {
	  
	  e.printStackTrace(); }
	  
	  } catch (IOException e) {
	  
	  e.printStackTrace(); } }
	 
*/
}
