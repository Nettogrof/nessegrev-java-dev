package ai.nettogrof.battlesnake.junit;

import org.junit.jupiter.api.Test;
import ai.nettogrof.battlesnake.snakes.Snake;
/**
 * @author carl.lajeunesse
 *
 */
public class SnakeTest {

	/**
	 * Single unit test
	 */
	@Test
	public void testMain() {
		String[] param = { "Beta" };
		Snake.main(param);
	}

}
