package ai.nettogrof.battlesnake.junit;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.util.VisibleForTesting;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.nettogrof.battlesnake.snakes.RightSnake;
import ai.nettogrof.battlesnake.treesearch.search.fun.LimitedMoveRoyaleSearch;

/**
 * Junit class test for Right Snake
 * 
 * @author carl.lajeunesse
 *
 */
class RightSnakeTest {
	/**
	 * 
	 */
	private final ObjectMapper json = new ObjectMapper();

	/**
	 * 
	 */
	private static final String PRETEST = "{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":130,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_XQhBg33RYXxHdffpdFdpJctD\",\"name\":\"snakespeare\",\"latency\":\"4\",\"health\":96,\"body\":[{\"x\":3,\"y\":7},{\"x\":4,\"y\":7},{\"x\":4,\"y\":8},{\"x\":5,\"y\":8},{\"x\":6,\"y\":8},{\"x\":6,\"y\":7},{\"x\":6,\"y\":6},{\"x\":5,\"y\":6}],\"head\":{\"x\":3,\"y\":7},\"length\":8,\"shout\":\"\"},{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"351\",\"health\":86,\"body\":[{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2}],\"head\":{\"x\":2,\"y\":6},\"length\":14,\"shout\":\"\"}],\"food\":[{\"x\":5,\"y\":10},{\"x\":6,\"y\":9},{\"x\":9,\"y\":9},{\"x\":9,\"y\":6},{\"x\":7,\"y\":10},{\"x\":1,\"y\":6},{\"x\":4,\"y\":10}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_B8qmywFJvy3jt8v7Ywp4mCTP\",\"name\":\"Nessegrev-gamma\",\"latency\":\"351\",\"health\":86,\"body\":[{\"x\":2,\"y\":6},{\"x\":3,\"y\":6},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":5,\"y\":5},{\"x\":6,\"y\":5},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":7,\"y\":3},{\"x\":7,\"y\":2},{\"x\":7,\"y\":1},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":5,\"y\":2}],\"head\":{\"x\":2,\"y\":6},\"length\":14,\"shout\":\"\"}}";

	/**
	 * Sole constructor. (For invocation by subclass constructors, typically
	 * implicit.)
	 */
	protected RightSnakeTest() {
		//useless constructor
	}

	/**
	 * Start Right snake test
	 * 
	 * @throws JsonMappingException    ex if test is invalid
	 * @throws JsonProcessingException ex if test is invalid
	 * @throws InterruptedException    ex if stopped
	 */
	@Test
	@VisibleForTesting
	/* default */ void startRight() throws JsonMappingException, JsonProcessingException, InterruptedException {

		final JsonNode parsedRequest = json.readTree(PRETEST);

		final RightSnake snakeAi = new RightSnake("test");

		snakeAi.setMultiThread(true);
		snakeAi.setCpuLimit(4);
		assertTrue(snakeAi.start(parsedRequest).toString().contains("apiversion=1"), "Invalid response");

	}

	/**
	 * Royale Right tests
	 * 
	 * @throws JsonProcessingException      ex if test is invalid
	 * @throws JsonMappingException         ex if test is invalid
	 * @throws ReflectiveOperationException ex if test is invalid
	 * @throws InterruptedException         ex if stopped
	 * 
	 */
	@Test
	@VisibleForTesting
	/* default */ void royaleRightTest()
			throws JsonMappingException, JsonProcessingException, ReflectiveOperationException, InterruptedException {

		final String test[] = {
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":259,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_PgKdYq7GJkct7cGVvRRF69D9\",\"name\":\"Nessegrev-gamma\",\"latency\":\"401\",\"health\":17,\"body\":[{\"x\":3,\"y\":6},{\"x\":2,\"y\":6},{\"x\":2,\"y\":5},{\"x\":2,\"y\":4},{\"x\":2,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":4,\"y\":4},{\"x\":5,\"y\":4},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":7}],\"head\":{\"x\":3,\"y\":6},\"length\":17,\"shout\":\"\"},{\"id\":\"gs_8j6wrXGpD7TPfDfjTQ4MmG7T\",\"name\":\"SneakySnake\",\"latency\":\"283\",\"health\":69,\"body\":[{\"x\":10,\"y\":9},{\"x\":10,\"y\":8},{\"x\":10,\"y\":7},{\"x\":10,\"y\":6},{\"x\":10,\"y\":5},{\"x\":10,\"y\":4},{\"x\":10,\"y\":3},{\"x\":10,\"y\":2},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":8,\"y\":8},{\"x\":7,\"y\":8},{\"x\":6,\"y\":8},{\"x\":5,\"y\":8},{\"x\":4,\"y\":8},{\"x\":3,\"y\":8},{\"x\":2,\"y\":8},{\"x\":1,\"y\":8},{\"x\":1,\"y\":7},{\"x\":1,\"y\":6}],\"head\":{\"x\":10,\"y\":9},\"length\":25,\"shout\":\"\"}],\"food\":[{\"x\":0,\"y\":1},{\"x\":8,\"y\":9},{\"x\":9,\"y\":10},{\"x\":0,\"y\":10},{\"x\":7,\"y\":9},{\"x\":1,\"y\":10},{\"x\":6,\"y\":9},{\"x\":3,\"y\":7}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":1},{\"x\":1,\"y\":2},{\"x\":1,\"y\":3},{\"x\":1,\"y\":4},{\"x\":1,\"y\":5},{\"x\":1,\"y\":6},{\"x\":1,\"y\":7},{\"x\":1,\"y\":8},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":1},{\"x\":2,\"y\":2},{\"x\":2,\"y\":3},{\"x\":2,\"y\":4},{\"x\":2,\"y\":5},{\"x\":2,\"y\":6},{\"x\":2,\"y\":7},{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":1},{\"x\":3,\"y\":2},{\"x\":3,\"y\":3},{\"x\":3,\"y\":8},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":1},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":8},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":1},{\"x\":5,\"y\":2},{\"x\":5,\"y\":3},{\"x\":5,\"y\":8},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":6,\"y\":3},{\"x\":6,\"y\":8},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":1},{\"x\":7,\"y\":2},{\"x\":7,\"y\":3},{\"x\":7,\"y\":8},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":1},{\"x\":8,\"y\":2},{\"x\":8,\"y\":3},{\"x\":8,\"y\":8},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_PgKdYq7GJkct7cGVvRRF69D9\",\"name\":\"Nessegrev-gamma\",\"latency\":\"401\",\"health\":17,\"body\":[{\"x\":3,\"y\":6},{\"x\":2,\"y\":6},{\"x\":2,\"y\":5},{\"x\":2,\"y\":4},{\"x\":2,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":4,\"y\":4},{\"x\":5,\"y\":4},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":7}],\"head\":{\"x\":3,\"y\":6},\"length\":17,\"shout\":\"\"}}",
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":260,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_PgKdYq7GJkct7cGVvRRF69D9\",\"name\":\"Nessegrev-gamma\",\"latency\":\"401\",\"health\":100,\"body\":[{\"x\":3,\"y\":7},{\"x\":3,\"y\":6},{\"x\":2,\"y\":6},{\"x\":2,\"y\":5},{\"x\":2,\"y\":4},{\"x\":2,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":4,\"y\":4},{\"x\":5,\"y\":4},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":6}],\"head\":{\"x\":3,\"y\":7},\"length\":18,\"shout\":\"\"},{\"id\":\"gs_8j6wrXGpD7TPfDfjTQ4MmG7T\",\"name\":\"SneakySnake\",\"latency\":\"283\",\"health\":53,\"body\":[{\"x\":10,\"y\":10},{\"x\":10,\"y\":9},{\"x\":10,\"y\":8},{\"x\":10,\"y\":7},{\"x\":10,\"y\":6},{\"x\":10,\"y\":5},{\"x\":10,\"y\":4},{\"x\":10,\"y\":3},{\"x\":10,\"y\":2},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":8,\"y\":8},{\"x\":7,\"y\":8},{\"x\":6,\"y\":8},{\"x\":5,\"y\":8},{\"x\":4,\"y\":8},{\"x\":3,\"y\":8},{\"x\":2,\"y\":8},{\"x\":1,\"y\":8},{\"x\":1,\"y\":7}],\"head\":{\"x\":10,\"y\":10},\"length\":25,\"shout\":\"\"}],\"food\":[{\"x\":0,\"y\":1},{\"x\":8,\"y\":9},{\"x\":9,\"y\":10},{\"x\":0,\"y\":10},{\"x\":7,\"y\":9},{\"x\":1,\"y\":10},{\"x\":6,\"y\":9}],\"hazards\":[{\"x\":0,\"y\":0},{\"x\":0,\"y\":1},{\"x\":0,\"y\":2},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":0,\"y\":6},{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10},{\"x\":1,\"y\":0},{\"x\":1,\"y\":1},{\"x\":1,\"y\":2},{\"x\":1,\"y\":3},{\"x\":1,\"y\":4},{\"x\":1,\"y\":5},{\"x\":1,\"y\":6},{\"x\":1,\"y\":7},{\"x\":1,\"y\":8},{\"x\":1,\"y\":9},{\"x\":1,\"y\":10},{\"x\":2,\"y\":0},{\"x\":2,\"y\":1},{\"x\":2,\"y\":2},{\"x\":2,\"y\":3},{\"x\":2,\"y\":4},{\"x\":2,\"y\":5},{\"x\":2,\"y\":6},{\"x\":2,\"y\":7},{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":2,\"y\":10},{\"x\":3,\"y\":0},{\"x\":3,\"y\":1},{\"x\":3,\"y\":2},{\"x\":3,\"y\":3},{\"x\":3,\"y\":8},{\"x\":3,\"y\":9},{\"x\":3,\"y\":10},{\"x\":4,\"y\":0},{\"x\":4,\"y\":1},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":8},{\"x\":4,\"y\":9},{\"x\":4,\"y\":10},{\"x\":5,\"y\":0},{\"x\":5,\"y\":1},{\"x\":5,\"y\":2},{\"x\":5,\"y\":3},{\"x\":5,\"y\":8},{\"x\":5,\"y\":9},{\"x\":5,\"y\":10},{\"x\":6,\"y\":0},{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":6,\"y\":3},{\"x\":6,\"y\":8},{\"x\":6,\"y\":9},{\"x\":6,\"y\":10},{\"x\":7,\"y\":0},{\"x\":7,\"y\":1},{\"x\":7,\"y\":2},{\"x\":7,\"y\":3},{\"x\":7,\"y\":8},{\"x\":7,\"y\":9},{\"x\":7,\"y\":10},{\"x\":8,\"y\":0},{\"x\":8,\"y\":1},{\"x\":8,\"y\":2},{\"x\":8,\"y\":3},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":7},{\"x\":8,\"y\":8},{\"x\":8,\"y\":9},{\"x\":8,\"y\":10},{\"x\":9,\"y\":0},{\"x\":9,\"y\":1},{\"x\":9,\"y\":2},{\"x\":9,\"y\":3},{\"x\":9,\"y\":4},{\"x\":9,\"y\":5},{\"x\":9,\"y\":6},{\"x\":9,\"y\":7},{\"x\":9,\"y\":8},{\"x\":9,\"y\":9},{\"x\":9,\"y\":10},{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_PgKdYq7GJkct7cGVvRRF69D9\",\"name\":\"Nessegrev-gamma\",\"latency\":\"401\",\"health\":100,\"body\":[{\"x\":3,\"y\":7},{\"x\":3,\"y\":6},{\"x\":2,\"y\":6},{\"x\":2,\"y\":5},{\"x\":2,\"y\":4},{\"x\":2,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":4,\"y\":5},{\"x\":4,\"y\":4},{\"x\":5,\"y\":4},{\"x\":6,\"y\":4},{\"x\":7,\"y\":4},{\"x\":8,\"y\":4},{\"x\":8,\"y\":5},{\"x\":8,\"y\":6},{\"x\":8,\"y\":6}],\"head\":{\"x\":3,\"y\":7},\"length\":18,\"shout\":\"\"}}",
				"{\"game\":{\"id\":\"ba7c56d3-ddad-4d4e-a702-a26e436cb1a6\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.20\",\"settings\":{\"foodSpawnChance\":15,\"minimumFood\":1,\"hazardDamagePerTurn\":15,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":500},\"turn\":0,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_rPfBBQQSd8ddQtpGh93X7HrS\",\"name\":\"Nessegrev-beta\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":5,\"y\":9},{\"x\":5,\"y\":9},{\"x\":5,\"y\":9}],\"head\":{\"x\":5,\"y\":9},\"length\":3,\"shout\":\"\"},{\"id\":\"gs_CBcYP44xTxjbWFpgjRrWKyfC\",\"name\":\"Nessegrev-drunk\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":1,\"y\":9},{\"x\":1,\"y\":9},{\"x\":1,\"y\":9}],\"head\":{\"x\":1,\"y\":9},\"length\":3,\"shout\":\"\"},{\"id\":\"gs_D3FmQKHtqBKCJ4SCdQSdTRqD\",\"name\":\"Nessegrev-Lefty\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":5,\"y\":1},{\"x\":5,\"y\":1},{\"x\":5,\"y\":1}],\"head\":{\"x\":5,\"y\":1},\"length\":3,\"shout\":\"\"},{\"id\":\"gs_C9JFDkryGKKx6KqK9htJPbjC\",\"name\":\"Nessegrev-Righty\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":9,\"y\":1},{\"x\":9,\"y\":1},{\"x\":9,\"y\":1}],\"head\":{\"x\":9,\"y\":1},\"length\":3,\"shout\":\"\"}],\"food\":[{\"x\":6,\"y\":8},{\"x\":0,\"y\":10},{\"x\":4,\"y\":2},{\"x\":10,\"y\":2},{\"x\":5,\"y\":5}],\"hazards\":[]},\"you\":{\"id\":\"gs_rPfBBQQSd8ddQtpGh93X7HrS\",\"name\":\"Nessegrev-beta\",\"latency\":\"\",\"health\":100,\"body\":[{\"x\":5,\"y\":9},{\"x\":5,\"y\":9},{\"x\":5,\"y\":9}],\"head\":{\"x\":5,\"y\":9},\"length\":3,\"shout\":\"\"}}",
				"{\"game\":{\"id\":\"ba7c56d3-ddad-4d4e-a702-a26e436cb1a6\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.20\",\"settings\":{\"foodSpawnChance\":15,\"minimumFood\":1,\"hazardDamagePerTurn\":15,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":500},\"turn\":39,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_rPfBBQQSd8ddQtpGh93X7HrS\",\"name\":\"Nessegrev-beta\",\"latency\":\"351\",\"health\":96,\"body\":[{\"x\":2,\"y\":3},{\"x\":1,\"y\":3},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":4}],\"head\":{\"x\":2,\"y\":3},\"length\":7,\"shout\":\"\"},{\"id\":\"gs_CBcYP44xTxjbWFpgjRrWKyfC\",\"name\":\"Nessegrev-drunk\",\"latency\":\"260\",\"health\":63,\"body\":[{\"x\":2,\"y\":7},{\"x\":2,\"y\":8},{\"x\":2,\"y\":9},{\"x\":3,\"y\":9}],\"head\":{\"x\":2,\"y\":7},\"length\":4,\"shout\":\"\"}],\"food\":[{\"x\":10,\"y\":2},{\"x\":1,\"y\":0},{\"x\":8,\"y\":4}],\"hazards\":[{\"x\":10,\"y\":0},{\"x\":10,\"y\":1},{\"x\":10,\"y\":2},{\"x\":10,\"y\":3},{\"x\":10,\"y\":4},{\"x\":10,\"y\":5},{\"x\":10,\"y\":6},{\"x\":10,\"y\":7},{\"x\":10,\"y\":8},{\"x\":10,\"y\":9},{\"x\":10,\"y\":10}]},\"you\":{\"id\":\"gs_rPfBBQQSd8ddQtpGh93X7HrS\",\"name\":\"Nessegrev-beta\",\"latency\":\"351\",\"health\":96,\"body\":[{\"x\":2,\"y\":3},{\"x\":1,\"y\":3},{\"x\":0,\"y\":3},{\"x\":0,\"y\":4},{\"x\":0,\"y\":5},{\"x\":1,\"y\":5},{\"x\":1,\"y\":4}],\"head\":{\"x\":2,\"y\":3},\"length\":7,\"shout\":\"\"}}",
				"{\"game\":{\"id\":\"ba7c56d3-ddad-4d4e-a702-a26e436cb1a6\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.20\",\"settings\":{\"foodSpawnChance\":15,\"minimumFood\":1,\"hazardDamagePerTurn\":15,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":500},\"turn\":5,\"board\":{\"height\":11,\"width\":11,\"snakes\":[{\"id\":\"gs_rPfBBQQSd8ddQtpGh93X7HrS\",\"name\":\"Nessegrev-beta\",\"latency\":\"352\",\"health\":97,\"body\":[{\"x\":8,\"y\":7},{\"x\":7,\"y\":7},{\"x\":7,\"y\":8},{\"x\":6,\"y\":8}],\"head\":{\"x\":8,\"y\":7},\"length\":4,\"shout\":\"\"},{\"id\":\"gs_CBcYP44xTxjbWFpgjRrWKyfC\",\"name\":\"Nessegrev-drunk\",\"latency\":\"261\",\"health\":97,\"body\":[{\"x\":0,\"y\":7},{\"x\":0,\"y\":8},{\"x\":0,\"y\":9},{\"x\":0,\"y\":10}],\"head\":{\"x\":0,\"y\":7},\"length\":4,\"shout\":\"\"},{\"id\":\"gs_D3FmQKHtqBKCJ4SCdQSdTRqD\",\"name\":\"Nessegrev-Lefty\",\"latency\":\"260\",\"health\":97,\"body\":[{\"x\":6,\"y\":1},{\"x\":5,\"y\":1},{\"x\":4,\"y\":1},{\"x\":4,\"y\":2}],\"head\":{\"x\":6,\"y\":1},\"length\":4,\"shout\":\"\"},{\"id\":\"gs_C9JFDkryGKKx6KqK9htJPbjC\",\"name\":\"Nessegrev-Righty\",\"latency\":\"0\",\"health\":95,\"body\":[{\"x\":9,\"y\":6},{\"x\":9,\"y\":5},{\"x\":9,\"y\":4}],\"head\":{\"x\":9,\"y\":6},\"length\":3,\"shout\":\"\"}],\"food\":[{\"x\":10,\"y\":2},{\"x\":5,\"y\":5}],\"hazards\":[]},\"you\":{\"id\":\"gs_rPfBBQQSd8ddQtpGh93X7HrS\",\"name\":\"Nessegrev-beta\",\"latency\":\"352\",\"health\":97,\"body\":[{\"x\":8,\"y\":7},{\"x\":7,\"y\":7},{\"x\":7,\"y\":8},{\"x\":6,\"y\":8}],\"head\":{\"x\":8,\"y\":7},\"length\":4,\"shout\":\"\"}}"

		};

		JsonNode parsedRequest = json.readTree(PRETEST);

		final RightSnake snakeAi = new RightSnake("test");

		snakeAi.setMultiThread(true);
		snakeAi.setCpuLimit(4);

		snakeAi.start(parsedRequest);
		for (final String uniqueTest : test) {
			parsedRequest = json.readTree(uniqueTest);
			snakeAi.move(parsedRequest);
		}
		LimitedMoveRoyaleSearch.setLeftOnly();
		for (final String uniqueTest : test) {
			parsedRequest = json.readTree(uniqueTest);
			snakeAi.move(parsedRequest);
		}
		LimitedMoveRoyaleSearch.setJustTurn();
		for (final String uniqueTest : test) {
			parsedRequest = json.readTree(uniqueTest);
			snakeAi.move(parsedRequest);
		}

		assertNotNull(snakeAi.end(parsedRequest), "End methos should return a object");

	}

	/**
	 * Standard Right tests
	 * 
	 * @throws JsonProcessingException      ex if test is invalid
	 * @throws JsonMappingException         ex if test is invalid
	 * @throws ReflectiveOperationException ex if test is invalid
	 * @throws InterruptedException         ex if stopped
	 * 
	 */
	@Test
	@VisibleForTesting
	/* default */ void standardRightTest()
			throws JsonMappingException, JsonProcessingException, ReflectiveOperationException, InterruptedException {

		final String test[] = {
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":0,\"y\":6},{\"x\":0,\"y\":5},{\"x\":0,\"y\":4},{\"x\":0,\"y\":3},{\"x\":0,\"y\":2},{\"x\":0,\"y\":1},{\"x\":0,\"y\":0}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":0,\"y\":6},{\"x\":0,\"y\":5},{\"x\":0,\"y\":4},{\"x\":0,\"y\":3},{\"x\":0,\"y\":2},{\"x\":0,\"y\":1},{\"x\":0,\"y\":0}]}]}}",
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":3,\"y\":3},{\"x\":3,\"y\":2},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":3,\"y\":4},{\"x\":2,\"y\":4}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":3,\"y\":3},{\"x\":3,\"y\":2},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":3,\"y\":4},{\"x\":2,\"y\":4}]}]}}",
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":200,\"you\":{\"head\":{\"x\":3,\"y\":0},\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":3,\"y\":0},{\"x\":3,\"y\":1},{\"x\":3,\"y\":2}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":0},\"body\":[{\"x\":3,\"y\":0},{\"x\":3,\"y\":1},{\"x\":3,\"y\":2}]}]}}",
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},{\"health\":100,\"id\":\"#FFddd2\",\"name\":\"#FFddd2\",\"body\":[{\"x\":6,\"y\":1},{\"x\":6,\"y\":2},{\"x\":6,\"y\":3},{\"x\":5,\"y\":3},{\"x\":4,\"y\":3},{\"x\":3,\"y\":3},{\"x\":2,\"y\":3},{\"x\":1,\"y\":3},{\"x\":0,\"y\":3}]}]}}",
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":5,\"y\":2},\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":5,\"y\":2},\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},{\"health\":100,\"id\":\"#FFddd2\",\"name\":\"#FFddd2\",\"head\":{\"x\":6,\"y\":3},\"body\":[{\"x\":6,\"y\":3},{\"x\":5,\"y\":3},{\"x\":4,\"y\":3},{\"x\":3,\"y\":3},{\"x\":2,\"y\":3},{\"x\":1,\"y\":3},{\"x\":0,\"y\":3}]}]}}",
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":5,\"y\":2},\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":5,\"y\":2},\"body\":[{\"x\":5,\"y\":2},{\"x\":4,\"y\":2},{\"x\":3,\"y\":2},{\"x\":2,\"y\":2}]},{\"health\":100,\"id\":\"#FFddd2\",\"name\":\"#FFddd2\",\"head\":{\"x\":6,\"y\":3},\"body\":[{\"x\":6,\"y\":3},{\"x\":5,\"y\":3},{\"x\":4,\"y\":3}]}]}}",
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":2},\"body\":[{\"x\":3,\"y\":2},{\"x\":3,\"y\":1},{\"x\":3,\"y\":0}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":2},\"body\":[{\"x\":3,\"y\":2},{\"x\":3,\"y\":1},{\"x\":3,\"y\":0}]},{\"health\":100,\"id\":\"#FFddd2\",\"name\":\"#FFddd2\",\"head\":{\"x\":3,\"y\":4},\"body\":[{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":3,\"y\":6}]}]}}",
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":2},\"body\":[{\"x\":3,\"y\":2},{\"x\":3,\"y\":1},{\"x\":3,\"y\":0}]},\"board\":{\"food\":[{\"x\":3,\"y\":3}],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":2},\"body\":[{\"x\":3,\"y\":2},{\"x\":3,\"y\":1},{\"x\":3,\"y\":0}]},{\"health\":100,\"id\":\"#FFddd2\",\"name\":\"#FFddd2\",\"head\":{\"x\":3,\"y\":4},\"body\":[{\"x\":3,\"y\":4},{\"x\":3,\"y\":5},{\"x\":3,\"y\":6},{\"x\":2,\"y\":6}]},{\"health\":100,\"id\":\"#FF1703\",\"name\":\"#FF1703\",\"head\":{\"x\":4,\"y\":3},\"body\":[{\"x\":4,\"y\":3},{\"x\":5,\"y\":3},{\"x\":6,\"y\":3},{\"x\":6,\"y\":2}]}]}}",
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":3},\"body\":[{\"x\":3,\"y\":3},{\"x\":2,\"y\":3},{\"x\":2,\"y\":2},{\"x\":3,\"y\":2},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":3,\"y\":4},{\"x\":2,\"y\":4}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":3},\"body\":[{\"x\":3,\"y\":3},{\"x\":2,\"y\":3},{\"x\":2,\"y\":2},{\"x\":3,\"y\":2},{\"x\":4,\"y\":2},{\"x\":4,\"y\":3},{\"x\":4,\"y\":4},{\"x\":3,\"y\":4},{\"x\":2,\"y\":4}]}]}}",
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":3},\"body\":[{\"x\":3,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":3}]},\"board\":{\"food\":[{\"x\":1,\"y\":1}],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":3},\"body\":[{\"x\":3,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":3}]}]}}",
				"{\"game\":{\"id\":\"dff884b3-d855-44d7-a1b7-d63076207807\",\"ruleset\":{\"name\":\"royale\",\"version\":\"v1.0.25\",\"settings\":{\"foodSpawnChance\":20,\"minimumFood\":1,\"hazardDamagePerTurn\":14,\"royale\":{\"shrinkEveryNTurns\":25},\"squad\":{\"allowBodyCollisions\":false,\"sharedElimination\":false,\"sharedHealth\":false,\"sharedLength\":false}}},\"timeout\":600,\"source\":\"arena\"},\"turn\":200,\"you\":{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":3},\"body\":[{\"x\":3,\"y\":3},{\"x\":3,\"y\":3},{\"x\":3,\"y\":3}]},\"board\":{\"food\":[],\"height\":7,\"width\":7,\"snakes\":[{\"health\":100,\"id\":\"you\",\"name\":\"#22aa34\",\"head\":{\"x\":3,\"y\":3},\"body\":[{\"x\":3,\"y\":3}]}]}}"

		};

		JsonNode parsedRequest = json.readTree(test[0]);

		final RightSnake snakeAi = new RightSnake("test");

		snakeAi.setMultiThread(true);
		snakeAi.setCpuLimit(4);

		snakeAi.start(parsedRequest);
		for (final String uniqueTest : test) {

			parsedRequest = json.readTree(uniqueTest);
			snakeAi.move(parsedRequest);

		}

		assertNotNull(snakeAi.end(parsedRequest), "End methos should return a object");

	}

}
