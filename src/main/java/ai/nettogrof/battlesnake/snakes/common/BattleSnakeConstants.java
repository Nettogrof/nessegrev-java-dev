package ai.nettogrof.battlesnake.snakes.common;

/**
 * This class keep some constant value
 * 
 * @version Spring 2021
 */
public final class BattleSnakeConstants {

	/**
	 * Health lose when a snake is in hazard zone
	 */
	public final static int LOST_HAZARD = -15;
	
	/**
	 * Max Health 
	 */
	public final static int MAX_HEALTH = 100;

	/**
	 * Max score for a node
	 */
	public final static float MAX_SCORE = 99_999f;

	/**
	 * Invalid score used for creating a payoff matrix
	 */
	public final static float INVALID_SCORE = -500f;

	/**
	 * Stop expand a node when the score ratio is higher than that value
	 */
	public final static float STOP_EXPAND_LIMIT = 42;  // 42 is the answer of life.

	/**
	 * When a snake is in losing position, the snake gonna shout one of those line
	 * randomly.
	 */
	public final static String[] LOSE_SHOUT = { "I have a bad feeling about this", "help me obiwan you're my only hope",
			"Nooooo!!", "Sorry master, I failed" };

	/**
	 * When a snake is in winning position, the snake gonna shout one of those line
	 * randomly
	 */
	public final static String[] WIN_SHOUT = { "You gonna die", "I'm your father", "All your base belong to me",
			"42 is the answer of life", "Astalavista baby!" };

	/**
	 * Basic score assign to all snakes to avoid division by 0
	 */
	public final static float BASIC_SCORE = 0.0001f;

	/**
	 * Value for square controlled by two snakes
	 */
	public final static int SPLIT_AREA = -50;
	
	/**
	 * Value for square uncontrolled 
	 */
	public final static int EMPTY_AREA = -150;

	/**
	 * Value of a snake body
	 */
	public final static int SNAKE_BODY = -99;
	
	/**
	 * Minimun number of snake.  If number of snake smaller than this value, the game end
	 */
	public final static int MINIMUN_SNAKE = 2;
	
	/**
	 * Minimun number of snake.  If number of snake smaller than this value, the game end
	 */
	public final static int API_V1 = 1;
}
