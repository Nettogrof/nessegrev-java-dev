package ai.nettogrof.battlesnake.junit;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.nettogrof.battlesnake.snakes.Snake;

public class SnakeTest {

	/**
	 * @throws JsonMappingException ex
	 * @throws JsonProcessingException ex
	 * @throws InterruptedException ex
	 */
	@Test
	public void testMain() {
		String[] param = {"Beta"};
		Snake.main(param);
	}

}
