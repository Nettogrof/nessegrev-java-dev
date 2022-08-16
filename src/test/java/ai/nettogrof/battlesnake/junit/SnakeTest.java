package ai.nettogrof.battlesnake.junit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.assertj.core.util.VisibleForTesting;
import org.junit.jupiter.api.Test;
import ai.nettogrof.battlesnake.snakes.Snake;

/**
 * Junit class test for Basic
 * 
 * @author carl.lajeunesse
 *
 */
class SnakeTest {

	/**
	 * Sole constructor. (For invocation by subclass constructors, typically
	 * implicit.)
	 */
	protected SnakeTest() {
		//useless constructor
	}

	/**
	 * Single unit test
	 */
	@Test
	@VisibleForTesting
	/* default */ void testMain() {
		final String[] param = { "Beta" };
		Snake.main(param);
		assertNotNull(param[0],"WTF");
	}

}
