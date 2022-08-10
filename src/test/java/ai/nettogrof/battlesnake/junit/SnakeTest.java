package ai.nettogrof.battlesnake.junit;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ai.nettogrof.battlesnake.snakes.Snake;

public class SnakeTest {

	/**
	 * 
	 */
	@Test
	public void testMain() {
		String[] param = {"Beta"};
		Snake.main(param);
	}

}
