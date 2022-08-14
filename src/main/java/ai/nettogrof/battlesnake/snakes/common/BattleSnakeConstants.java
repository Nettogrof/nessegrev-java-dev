package ai.nettogrof.battlesnake.snakes.common;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * This class keep some constant value
 * 
 * @version Spring 2021
 */
public final class BattleSnakeConstants {

	/**
	 * Max Health
	 */
	public static final int MAX_HEALTH = 100;

	/**
	 * Max score for a node
	 */
	public static final float MAX_SCORE = 99_999f;

	/**
	 * Invalid score used for creating a payoff matrix
	 */
	public static final float INVALID_SCORE = -500f;

	/**
	 * When a snake is in losing position, the snake gonna shout one of those line
	 * randomly.
	 */
	private static final String[] LOSE_SHOUT = { "I have a bad feeling about this",
			"help me obiwan you're my only hope", "Nooooo!!", "Sorry master, I failed" };

	/**
	 * When a snake is in winning position, the snake gonna shout one of those line
	 * randomly
	 */
	private static final String[] WIN_SHOUT = { "You gonna die", "I'm your father", "All your base belong to me",
			"42 is the answer of life", "Astalavista baby!" };

	/**
	 * Basic score assign to all snakes to avoid division by 0
	 */
	public static final float BASIC_SCORE = 0.0001f;

	/**
	 * Value for square controlled by two snakes
	 */
	public static final int SPLIT_AREA = -50;

	/**
	 * Value for square uncontrolled
	 */
	public static final int EMPTY_AREA = -150;

	/**
	 * Value of a snake body
	 */
	public static final int SNAKE_BODY = -99;

	/**
	 * Minimum number of snake. If number of snake smaller than this value, the game
	 * end
	 */
	public static final int MINIMUN_SNAKE = 2;

	/**
	 * Current API version used by snakes
	 */
	public static final int API_V1 = 1;

	/**
	 * Current API version used by snakes
	 */
	public static final int SINGLE_SNAKE = 1;

	/**
	 * Sole constructor. (For invocation by subclass constructors, typically
	 * implicit.)
	 */
	private BattleSnakeConstants() {
	}

	/**
	 * Get Winning shout
	 * 
	 * @return winning shout
	 */
	public static String getWinShout() {

		try {
			final Random rand = SecureRandom.getInstanceStrong();
			return WIN_SHOUT[rand.nextInt(BattleSnakeConstants.WIN_SHOUT.length)];
		} catch (NoSuchAlgorithmException ex) {
			return WIN_SHOUT[0];
		}

	}

	/**
	 * Get losing shout
	 * 
	 * @return losing shout
	 */
	public static String getLostShout() {

		try {
			final Random rand = SecureRandom.getInstanceStrong();
			return LOSE_SHOUT[rand.nextInt(BattleSnakeConstants.LOSE_SHOUT.length)];
		} catch (NoSuchAlgorithmException ex) {
			return LOSE_SHOUT[0];
		}

	}

}
