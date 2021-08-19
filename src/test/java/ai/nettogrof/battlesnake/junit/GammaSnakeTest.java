package ai.nettogrof.battlesnake.junit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.nettogrof.battlesnake.snakes.GammaSnake;

/**
 * @author carl.lajeunesse
 *
 */
public class GammaSnakeTest {
	/**
	 * 
	 */
	private final transient ObjectMapper json = new ObjectMapper();

	/**
	 * 
	 */
	private final static String PRETEST = " {\"game\":{\"id\":\"97da1f9a-67f9-4fa9-87cd-fa922b94d057\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":130,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_XQhBg33RYXxHdffpdFdpJctD\",\"name\":\"snakespeare\",\"latency\":\"4\",\"health\":96,\"body\":[{\"x\":3,\"y\":7},{\"x\":4,\"y\":7},{\"x\":4,\"y\":8},{\"x\":5,\"y\":8},{\"x\":6,\"y\":8},{\"x\":6,\"y\":7},{\"x\":6,\"y\":6},{\"x\":5,\"y\":6}],\"head\":{\"x\":3,\"y\":7},\"length\":8,\"shout\":\"\"},{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"351\",\"health\":86,\"body\":[{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2}],\"head\":{\"x\":2,\"y\":6},\"length\":14,\"shout\":\"\"}],\"food\":[{\"x\":5,\"y\":10},{\"x\":6,\"y\":9},{\"x\":9,\"y\":9},{\"x\":9,\"y\":6},{\"x\":7,\"y\":10},{\"x\":1,\"y\":6},{\"x\":4,\"y\":10}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"351\",\"health\":86,\"body\":[{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2}],\"head\":{\"x\":2,\"y\":6},\"length\":14,\"shout\":\"\"}}";

	/**
	 * @throws JsonMappingException    ex
	 * @throws JsonProcessingException ex
	 * @throws InterruptedException    ex
	 */
	@Test
	public void startGamma() throws JsonMappingException, JsonProcessingException, InterruptedException {

		final JsonNode parsedRequest = json.readTree(PRETEST);

		final GammaSnake snakeAi = new GammaSnake("test");

		snakeAi.setMultiThread(true);
		snakeAi.setCpuLimit(4);
		assertTrue(snakeAi.start(parsedRequest).toString().contains("apiversion=1"), "Invalid response");

	}

	/**
	 * @throws JsonProcessingException      ex
	 * @throws JsonMappingException         ex
	 * @throws ReflectiveOperationException ex
	 * @throws InterruptedException         ex
	 * 
	 */
	@Test
	public void standardGammaTest()
			throws JsonMappingException, JsonProcessingException, ReflectiveOperationException, InterruptedException {

		final String test[] = {
				"{\"game\":{\"id\":\"12345\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v.1.2.3\"},\"timeout\":500},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":0,\"y\":6},{\"x\":0,\"y\":5},{\"x\":0,\"y\":4},{\"x\":0,\"y\":3},{\"x\":0,\"y\":2},{\"x\":0,\"y\":1},{\"x\":0,\"y\":0}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":0,\"y\":6},{\"x\":0,\"y\":5},{\"x\":0,\"y\":4},{\"x\":0,\"y\":3},{\"x\":0,\"y\":2},{\"x\":0,\"y\":1},{\"x\":0,\"y\":0}]}]}}",
				"{\"game\":{\"id\":\"12345\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v.1.2.3\"},\"timeout\":500},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":3,\"y\":3},{\"x\":3,\"y\":2},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":3,\"y\":4},{\"x\":2,\"y\":4}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":3,\"y\":3},{\"x\":3,\"y\":2},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":3,\"y\":4},{\"x\":2,\"y\":4}]}]}}",
				"{\"game\":{\"id\":\"12345\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v.1.2.3\"},\"timeout\":500},\"turn\":200,\"you\":{\"head\":{\"x\":3,\"y\":0},\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":3,\"y\":0},{\"x\":3,\"y\":1},{\"x\":3,\"y\":2}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":0},\"body\":[{\"x\":3,\"y\":0},{\"x\":3,\"y\":1},{\"x\":3,\"y\":2}]}]}}",
				"{\"game\":{\"id\":\"12345\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v.1.2.3\"},\"timeout\":500},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},{\"health\":100,\"id\":\"#FFddd2\",\"name\":\"#FFddd2\",\"body\":[{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":6,\"y\":3},{\"x\":5,\"y\":3},{\"x\":4,\"y\":3},{\"x\":3,\"y\":3},{\"x\":2,\"y\":3},{\"x\":1,\"y\":3},{\"x\":0,\"y\":3}]}]}}",
				"{\"game\":{\"id\":\"12345\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v.1.2.3\"},\"timeout\":500},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":5,\"y\":2},\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":5,\"y\":2},\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},{\"health\":100,\"id\":\"#FFddd2\",\"name\":\"#FFddd2\",\"head\":{\"x\":6,\"y\":3},\"body\":[{\"x\":6,\"y\":3},{\"x\":5,\"y\":3},{\"x\":4,\"y\":3},{\"x\":3,\"y\":3},{\"x\":2,\"y\":3},{\"x\":1,\"y\":3},{\"x\":0,\"y\":3}]}]}}",
				"{\"game\":{\"id\":\"12345\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v.1.2.3\"},\"timeout\":500},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":5,\"y\":2},\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":5,\"y\":2},\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},{\"health\":100,\"id\":\"#FFddd2\",\"name\":\"#FFddd2\",\"head\":{\"x\":6,\"y\":3},\"body\":[{\"x\":6,\"y\":3},{\"x\":5,\"y\":3},{\"x\":4,\"y\":3}]}]}}",
				"{\"game\":{\"id\":\"12345\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v.1.2.3\"},\"timeout\":500},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":2},\"body\":[{\"x\":3,\"y\":2},{\"x\":3,\"y\":1},{\"x\":3,\"y\":0}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":2},\"body\":[{\"x\":3,\"y\":2},{\"x\":3,\"y\":1},{\"x\":3,\"y\":0}]},{\"health\":100,\"id\":\"#FFddd2\",\"name\":\"#FFddd2\",\"head\":{\"x\":3,\"y\":4},\"body\":[{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":3,\"y\":6}]}]}}",
				"{\"game\":{\"id\":\"12345\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v.1.2.3\"},\"timeout\":500},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":2},\"body\":[{\"x\":3,\"y\":2},{\"x\":3,\"y\":1},{\"x\":3,\"y\":0}]},\"board\":{\"food\":[{\"x\":3,\"y\":3}],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":2},\"body\":[{\"x\":3,\"y\":2},{\"x\":3,\"y\":1},{\"x\":3,\"y\":0}]},{\"health\":100,\"id\":\"#FFddd2\",\"name\":\"#FFddd2\",\"head\":{\"x\":3,\"y\":4},\"body\":[{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":3,\"y\":6},{\"x\":2,\"y\":6}]},{\"health\":100,\"id\":\"#FF1703\",\"name\":\"#FF1703\",\"head\":{\"x\":4,\"y\":3},\"body\":[{\"x\":4,\"y\":3},{\"x\":5,\"y\":3},{\"x\":6,\"y\":3},{\"x\":6,\"y\":2}]}]}}",
				"{\"game\":{\"id\":\"12345\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v.1.2.3\"},\"timeout\":500},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":3},\"body\":[{\"x\":3,\"y\":3},{\"x\":2,\"y\":3},{\"x\":2,\"y\":2},{\"x\":3,\"y\":2},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":3,\"y\":4},{\"x\":2,\"y\":4}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":3},\"body\":[{\"x\":3,\"y\":3},{\"x\":2,\"y\":3},{\"x\":2,\"y\":2},{\"x\":3,\"y\":2},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":3,\"y\":4},{\"x\":2,\"y\":4}]}]}}",
				"{\"game\":{\"id\":\"12345\",\"ruleset\":{\"name\":\"standard\",\"version\":\"v.1.2.3\"},\"timeout\":500},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":3},\"body\":[{\"x\":3,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":3}]},\"board\":{\"food\":[{\"x\":1,\"y\":1}],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":3},\"body\":[{\"x\":3,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":3}]}]}}"

		};

		JsonNode parsedRequest = json.readTree(test[0]);

		final GammaSnake snakeAi = new GammaSnake("test");

		snakeAi.setMultiThread(true);
		snakeAi.setCpuLimit(4);

		Thread.sleep(100);

		snakeAi.start(parsedRequest);
		for (final String uniqueTest : test) {

			parsedRequest = json.readTree(uniqueTest);
			snakeAi.move(parsedRequest);

		}

		assertNotNull(snakeAi.end(parsedRequest), "End methos should return a object");

	}

}
