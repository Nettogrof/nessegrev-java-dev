package ai.nettogrof.battlesnake.junit;

import org.junit.jupiter.api.Test;
import ai.nettogrof.battlesnake.snakes.Snake;

/**
 * Junit class test for Basic
 * 
 * @author carl.lajeunesse
 *
 */
public class SnakeTest {

	/**
	 * Sole constructor. (For invocation by subclass constructors, typically
	 * implicit.)
	 */
	protected SnakeTest() {
	}

	/**
	 * Single unit test
	 */
	@Test
	public void testMain() {
		String[] param = { "Beta" };
		Snake.main(param);
	}

}
