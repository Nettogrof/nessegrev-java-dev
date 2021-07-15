/**
 * 
 */
package ai.nettogrof.battlesnake.snakes.common;

/**
 * This class keep some Evaluation constant value
 * 
 * @version Summer 2021
 */
public final class SnakeGeneticConstants {

	/**
	 * Stop expand a node when the score ratio is higher than that value
	 */
	public static float stopExpandLimit = 42; // 42 is the answer of life.

	/**
	 * Value if a FOOD is found in the controlled AREA
	 */
	public static int foodValueArea = 2;

	/**
	 * Value if a tail is found in the controlled AREA
	 */
	public static int tailValueArea = 5;

	/**
	 * Border score to subtract if head on the border of the board
	 */
	public static float borderScore = 0.4f;

	/**
	 * The Bias value for the Monte-Carlo Tree Search
	 */
	public static float mctsBias = 0.7f;

	/**
	 * @param foodValue the fOOD_VALUE_AREA to set
	 */
	public static void setFoodValue(final int foodValue) {
		foodValueArea = foodValue;
	}

	/**
	 * @param tailValue the tAIL_VALUE_AREA to set
	 */
	public static void setTailValue(final int tailValue) {
		tailValueArea = tailValue;
	}

	/**
	 * @param border the bORDER_SCORE to set
	 */
	public static void setBorderScore(final float border) {
		borderScore = border;
	}

	/**
	 * @param newMctsBias the mCTS_BIAS to set
	 */
	public static void setMCTS(final float newMctsBias) {
		mctsBias = newMctsBias;
	}

	/**
	 * @param sel Stop Expand Limit
	 */
	public static void setStopExpandLimit(final float sel) {
		stopExpandLimit = sel;
	}

}
