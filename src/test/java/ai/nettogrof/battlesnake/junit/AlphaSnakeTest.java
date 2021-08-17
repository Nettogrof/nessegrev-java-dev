package ai.nettogrof.battlesnake.junit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.nettogrof.battlesnake.snakes.AlphaSnake;


/**
 * @author carl.lajeunesse
 *
 */
@SuppressWarnings("deprecation")
public class AlphaSnakeTest {
	/**
	 * 
	 */
	ObjectMapper json = new ObjectMapper();

	

	/**
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 * @throws InterruptedException
	 */
	@Test
	void startAlpha() throws JsonMappingException, JsonProcessingException, InterruptedException {

		String pretest = " {\"game\":{\"id\":\"97da1f9a-67f9-4fa9-87cd-fa922b94d057\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":130,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_XQhBg33RYXxHdffpdFdpJctD\",\"name\":\"snakespeare\",\"latency\":\"4\",\"health\":96,\"body\":[{\"x\":3,\"y\":7},{\"x\":4,\"y\":7},{\"x\":4,\"y\":8},{\"x\":5,\"y\":8},{\"x\":6,\"y\":8},{\"x\":6,\"y\":7},{\"x\":6,\"y\":6},{\"x\":5,\"y\":6}],\"head\":{\"x\":3,\"y\":7},\"length\":8,\"shout\":\"\"},{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"351\",\"health\":86,\"body\":[{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2}],\"head\":{\"x\":2,\"y\":6},\"length\":14,\"shout\":\"\"}],\"food\":[{\"x\":5,\"y\":10},{\"x\":6,\"y\":9},{\"x\":9,\"y\":9},{\"x\":9,\"y\":6},{\"x\":7,\"y\":10},{\"x\":1,\"y\":6},{\"x\":4,\"y\":10}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"351\",\"health\":86,\"body\":[{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2}],\"head\":{\"x\":2,\"y\":6},\"length\":14,\"shout\":\"\"}}";

		JsonNode parsedRequest = json.readTree(pretest);

		AlphaSnake t = new AlphaSnake("test");

		t.setMultiThread(true);
		t.setCpuLimit(4);
		System.out.println(t.start(parsedRequest));
		assertTrue(t.start(parsedRequest).toString().contains("headType=shac-gamer"));
		
	}

	/**
	 * @throws JsonProcessingException
	 * @throws JsonMappingException
	 * @throws ReflectiveOperationException
	 * @throws InterruptedException
	 * 
	 */
	@Test
	void mainAlphaTest()
			throws JsonMappingException, JsonProcessingException, ReflectiveOperationException, InterruptedException {

		String pretest = " {\"game\":{\"id\":\"97da1f9a-67f9-4fa9-87cd-fa922b94d057\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":130,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_XQhBg33RYXxHdffpdFdpJctD\",\"name\":\"snakespeare\",\"latency\":\"4\",\"health\":96,\"body\":[{\"x\":3,\"y\":7},{\"x\":4,\"y\":7},{\"x\":4,\"y\":8},{\"x\":5,\"y\":8},{\"x\":6,\"y\":8},{\"x\":6,\"y\":7},{\"x\":6,\"y\":6},{\"x\":5,\"y\":6}],\"head\":{\"x\":3,\"y\":7},\"length\":8,\"shout\":\"\"},{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"351\",\"health\":86,\"body\":[{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2}],\"head\":{\"x\":2,\"y\":6},\"length\":14,\"shout\":\"\"}],\"food\":[{\"x\":5,\"y\":10},{\"x\":6,\"y\":9},{\"x\":9,\"y\":9},{\"x\":9,\"y\":6},{\"x\":7,\"y\":10},{\"x\":1,\"y\":6},{\"x\":4,\"y\":10}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"351\",\"health\":86,\"body\":[{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2}],\"head\":{\"x\":2,\"y\":6},\"length\":14,\"shout\":\"\"}}";

		String test[] = {
				"{\"game\":{\"id\":\"335daf4b-5e71-4bae-b907-7ec5e95dc500\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":259,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_PgKdYq7GJkct7cGVvRRF69D9\",\"name\":\"Nessegrev-gamma\",\"latency\":\"401\",\"health\":17,\"body\":[{\"x\":3,\"y\":6},{\"x\":2,\"y\":6},{\"x\":2,\"y\":5},{\"x\":2,\"y\":4},{\"x\":2,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":4,\"y\":4},{\"x\":5,\"y\":4},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":7}],\"head\":{\"x\":3,\"y\":6},\"length\":17,\"shout\":\"\"},{\"id\":\"gs_8j6wrXGpD7TPfDfjTQ4MmG7T\",\"name\":\"SneakySnake\",\"latency\":\"283\",\"health\":69,\"body\":[{\"x\":10,\"y\":9},{\"x\":10,\"y\":8},{\"x\":10,\"y\":7},{\"x\":10,\"y\":6},{\"x\":10,\"y\":5},{\"x\":10,\"y\":4},{\"x\":10,\"y\":3},{\"x\":10,\"y\":2},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":8,\"y\":8},{\"x\":7,\"y\":8},{\"x\":6,\"y\":8},{\"x\":5,\"y\":8},{\"x\":4,\"y\":8},{\"x\":3,\"y\":8},{\"x\":2,\"y\":8},{\"x\":1,\"y\":8},{\"x\":1,\"y\":7},{\"x\":1,\"y\":6}],\"head\":{\"x\":10,\"y\":9},\"length\":25,\"shout\":\"\"}],\"food\":[{\"x\":0,\"y\":1},{\"x\":8,\"y\":9},{\"x\":9,\"y\":10},{\"x\":0,\"y\":10},{\"x\":7,\"y\":9},{\"x\":1,\"y\":10},{\"x\":6,\"y\":9},{\"x\":3,\"y\":7}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":1},{\"x\":1,\"y\":2},{\"x\":1,\"y\":3},{\"x\":1,\"y\":4},{\"x\":1,\"y\":5},{\"x\":1,\"y\":6},{\"x\":1,\"y\":7},{\"x\":1,\"y\":8},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":1},{\"x\":2,\"y\":2},{\"x\":2,\"y\":3},{\"x\":2,\"y\":4},{\"x\":2,\"y\":5},{\"x\":2,\"y\":6},{\"x\":2,\"y\":7},{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":1},{\"x\":3,\"y\":2},{\"x\":3,\"y\":3},{\"x\":3,\"y\":8},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":1},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":8},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":1},{\"x\":5,\"y\":2},{\"x\":5,\"y\":3},{\"x\":5,\"y\":8},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":6,\"y\":3},{\"x\":6,\"y\":8},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":1},{\"x\":7,\"y\":2},{\"x\":7,\"y\":3},{\"x\":7,\"y\":8},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":1},{\"x\":8,\"y\":2},{\"x\":8,\"y\":3},{\"x\":8,\"y\":8},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_PgKdYq7GJkct7cGVvRRF69D9\",\"name\":\"Nessegrev-gamma\",\"latency\":\"401\",\"health\":17,\"body\":[{\"x\":3,\"y\":6},{\"x\":2,\"y\":6},{\"x\":2,\"y\":5},{\"x\":2,\"y\":4},{\"x\":2,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":4,\"y\":4},{\"x\":5,\"y\":4},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":7}],\"head\":{\"x\":3,\"y\":6},\"length\":17,\"shout\":\"\"}}",
				"{\"game\":{\"id\":\"335daf4b-5e71-4bae-b907-7ec5e95dc500\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.17\"},\"timeout\":500},\"turn\":260,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_PgKdYq7GJkct7cGVvRRF69D9\",\"name\":\"Nessegrev-gamma\",\"latency\":\"401\",\"health\":100,\"body\":[{\"x\":3,\"y\":7},{\"x\":3,\"y\":6},{\"x\":2,\"y\":6},{\"x\":2,\"y\":5},{\"x\":2,\"y\":4},{\"x\":2,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":4,\"y\":4},{\"x\":5,\"y\":4},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":6}],\"head\":{\"x\":3,\"y\":7},\"length\":18,\"shout\":\"\"},{\"id\":\"gs_8j6wrXGpD7TPfDfjTQ4MmG7T\",\"name\":\"SneakySnake\",\"latency\":\"283\",\"health\":53,\"body\":[{\"x\":10,\"y\":10},{\"x\":10,\"y\":9},{\"x\":10,\"y\":8},{\"x\":10,\"y\":7},{\"x\":10,\"y\":6},{\"x\":10,\"y\":5},{\"x\":10,\"y\":4},{\"x\":10,\"y\":3},{\"x\":10,\"y\":2},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":8,\"y\":8},{\"x\":7,\"y\":8},{\"x\":6,\"y\":8},{\"x\":5,\"y\":8},{\"x\":4,\"y\":8},{\"x\":3,\"y\":8},{\"x\":2,\"y\":8},{\"x\":1,\"y\":8},{\"x\":1,\"y\":7}],\"head\":{\"x\":10,\"y\":10},\"length\":25,\"shout\":\"\"}],\"food\":[{\"x\":0,\"y\":1},{\"x\":8,\"y\":9},{\"x\":9,\"y\":10},{\"x\":0,\"y\":10},{\"x\":7,\"y\":9},{\"x\":1,\"y\":10},{\"x\":6,\"y\":9}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":1},{\"x\":1,\"y\":2},{\"x\":1,\"y\":3},{\"x\":1,\"y\":4},{\"x\":1,\"y\":5},{\"x\":1,\"y\":6},{\"x\":1,\"y\":7},{\"x\":1,\"y\":8},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":1},{\"x\":2,\"y\":2},{\"x\":2,\"y\":3},{\"x\":2,\"y\":4},{\"x\":2,\"y\":5},{\"x\":2,\"y\":6},{\"x\":2,\"y\":7},{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":1},{\"x\":3,\"y\":2},{\"x\":3,\"y\":3},{\"x\":3,\"y\":8},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":1},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":8},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":1},{\"x\":5,\"y\":2},{\"x\":5,\"y\":3},{\"x\":5,\"y\":8},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":6,\"y\":3},{\"x\":6,\"y\":8},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":1},{\"x\":7,\"y\":2},{\"x\":7,\"y\":3},{\"x\":7,\"y\":8},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":1},{\"x\":8,\"y\":2},{\"x\":8,\"y\":3},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":7},{\"x\":8,\"y\":8},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_PgKdYq7GJkct7cGVvRRF69D9\",\"name\":\"Nessegrev-gamma\",\"latency\":\"401\",\"health\":100,\"body\":[{\"x\":3,\"y\":7},{\"x\":3,\"y\":6},{\"x\":2,\"y\":6},{\"x\":2,\"y\":5},{\"x\":2,\"y\":4},{\"x\":2,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":4,\"y\":4},{\"x\":5,\"y\":4},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":6}],\"head\":{\"x\":3,\"y\":7},\"length\":18,\"shout\":\"\"}}"

		};

		JsonNode parsedRequest = json.readTree(pretest);

		AlphaSnake t = new AlphaSnake("test");

		t.setMultiThread(true);
		t.setCpuLimit(4);

		Thread.sleep(100);

		assertTrue(t.start(parsedRequest).toString().contains("headType=shac-gamer"));
		for (int i = 0; i < test.length; i++) {
			parsedRequest = json.readTree(test[i]);
			System.out.println(t.move(parsedRequest));

		}
		assertNotNull(t.end(parsedRequest));

	}

	
}