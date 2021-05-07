/**
 * 
 */
package ai.nettogrof.battlesnake.snakes;

import java.lang.reflect.Constructor;

import ai.nettogrof.battlesnake.treesearch.AbstractSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import ai.nettogrof.battlesnake.treesearch.search.fun.RightRoyaleSearch;
import ai.nettogrof.battlesnake.treesearch.search.fun.RightStandardSearch;

/**
 * Right snake. This class is the "Nessegrev-Mystery" snake on Battlesnake. This
 * snake was made just for fun for a Weekly Meet-up. This snake can turn right
 * or go straight.
 * 
 * @author carl.lajeunesse
 * @version Summer 2021
 */
public class RightSnake extends BetaSnake {

	/**
	 * Basic / unused constructor
	 */
	public RightSnake() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public RightSnake(final String gameId) {
		super(gameId);
		fileConfig = "Right.properties";
		setProperties();
	}

	@Override
	protected void setFileConfig() {
		fileConfig = "Right.properties";

	}

	@Override
	protected String getFileConfig() {
		return "Right.properties";
	}

	/**
	 * This method generate the search type
	 * 
	 * @return Abstract Search
	 * @throws ReflectiveOperationException In case of invalid search type
	 */
	@Override
	protected Constructor<? extends AbstractSearch> genSearchType() throws ReflectiveOperationException {

		if ("standard".equals(ruleset)) {
			return RightStandardSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class);
		}
		return RightRoyaleSearch.class.getConstructor(AbstractNode.class, int.class, int.class, long.class,
					int.class);
		

	}
//{"you":{"latency":"307.947","shout":"","body":[{"y":7,"x":1},{"y":8,"x":1},{"y":9,"x":1}],"id":"cec8a370-e35b-42c8-a3ef-2cdadbc6a578","health":98,"length":3,"name":"Mystery","head":{"y":7,"x":1}},"turn":2,"board":{"snakes":[{"latency":"307.947","shout":"","body":[{"y":7,"x":1},{"y":8,"x":1},{"y":9,"x":1}],"id":"cec8a370-e35b-42c8-a3ef-2cdadbc6a578","health":98,"length":3,"name":"Mystery","head":{"y":7,"x":1}},{"latency":"153.827","shout":"","body":[{"y":8,"x":4},{"y":8,"x":5},{"y":9,"x":5}],"id":"e5516089-4906-453b-9fec-d740c3638905","health":98,"length":3,"name":"Beta","head":{"y":8,"x":4}}],"width":11,"hazards":[],"height":11,"food":[{"y":10,"x":0},{"y":10,"x":6},{"y":5,"x":5},{"y":4,"x":0}]},"game":{"ruleset":{"name":"standard","version":"Mojave/3.1"},"timeout":400,"id":"d931ce16-8729-40d2-b7e7-1f43dcdb5e34"}} 
	/*
	 * public static void main(String args[]) { ObjectMapper json = new
	 * ObjectMapper(); String pretest =
	 * "{\"you\":{\"latency\":\"153.903\",\"shout\":\"\",\"body\":[{\"y\":7,\"x\":9},{\"y\":7,\"x\":8},{\"y\":8,\"x\":8},{\"y\":8,\"x\":9},{\"y\":9,\"x\":9}],\"id\":\"cec8a370-e35b-42c8-a3ef-2cdadbc6a578\",\"health\":93,\"length\":5,\"name\":\"Mystery\",\"head\":{\"y\":7,\"x\":9}},\"turn\":134,\"board\":{\"snakes\":[{\"latency\":\"153.903\",\"shout\":\"\",\"body\":[{\"y\":7,\"x\":9},{\"y\":7,\"x\":8},{\"y\":8,\"x\":8},{\"y\":8,\"x\":9},{\"y\":9,\"x\":9}],\"id\":\"cec8a370-e35b-42c8-a3ef-2cdadbc6a578\",\"health\":93,\"length\":5,\"name\":\"Mystery\",\"head\":{\"y\":7,\"x\":9}},{\"latency\":\"153.102\",\"shout\":\"\",\"body\":[{\"y\":4,\"x\":6},{\"y\":5,\"x\":6},{\"y\":5,\"x\":7},{\"y\":6,\"x\":7},{\"y\":6,\"x\":6},{\"y\":7,\"x\":6},{\"y\":7,\"x\":5},{\"y\":6,\"x\":5}],\"id\":\"e5516089-4906-453b-9fec-d740c3638905\",\"health\":91,\"length\":8,\"name\":\"Beta\",\"head\":{\"y\":4,\"x\":6}}],\"width\":11,\"hazards\":[],\"height\":11,\"food\":[{\"y\":0,\"x\":8},{\"y\":1,\"x\":2},{\"y\":9,\"x\":0},{\"y\":6,\"x\":1},{\"y\":1,\"x\":1},{\"y\":6,\"x\":10},{\"y\":10,\"x\":8},{\"y\":8,\"x\":0},{\"y\":0,\"x\":0},{\"y\":1,\"x\":6},{\"y\":5,\"x\":2},{\"y\":9,\"x\":1},{\"y\":9,\"x\":6}]},\"game\":{\"ruleset\":{\"name\":\"standard\",\"version\":\"Mojave/3.1\"},\"timeout\":400,\"id\":\"1f48fae2-8a69-4bad-9ce2-7bbb0031a454\"}} "
	 * ; try { JsonNode parsedRequest = json.readTree(pretest); int size =
	 * parsedRequest.get(BOARD).get("height").asInt(); MysterySnake t = new
	 * MysterySnake("test"); t.ruleset = "standard"; t.height = size; t.width =
	 * size; t.timeout = 500; t.multiThread = true; t.cpu_limit = 4;
	 * 
	 * try { t.searchType = t.genSearchType(); } catch (ReflectiveOperationException
	 * e1) {
	 * 
	 * e1.printStackTrace(); } try { Thread.sleep(100); } catch
	 * (InterruptedException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * System.out.println(t.move(parsedRequest)); // parsedRequest
	 * =json.readTree(test); // //System.out.println(t.move(parsedRequest)); try {
	 * Thread.sleep(510); } catch (InterruptedException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * } catch (IOException e) {
	 * 
	 * e.printStackTrace(); } }
	 */

}
