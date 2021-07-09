/**
 * 
 */
package ai.nettogrof.battlesnake.snakes.common;

/**
 * @author carl.lajeunesse
 *
 */
public class SnakeGeneticValue {

	/**
	 * Value if a FOOD is found in the controlled AREA
	 */
	public static int FOOD_VALUE_AREA = 2;

	/**
	 * Value if a tail is found in the controlled AREA
	 */
	public static int TAIL_VALUE_AREA = 5;

	/**
	 * Border score to subtract if head on the border of the board
	 */
	public static float BORDER_SCORE = 0.4f;

	/**
	 * The Bias value for the Monte-Carlo Tree Search
	 */
	public static float MCTS_BIAS = 0.7f;
	
	/**
	 * @param foodValue the fOOD_VALUE_AREA to set
	 */
	public static void setFOOD_VALUE_AREA(int foodValue) {
		FOOD_VALUE_AREA = foodValue;
	}

	/**
	 * @param tailValue the tAIL_VALUE_AREA to set
	 */
	public static void setTAIL_VALUE_AREA(int tailValue) {
		TAIL_VALUE_AREA = tailValue;
	}

	/**
	 * @param borderScore the bORDER_SCORE to set
	 */
	public static void setBORDER_SCORE(float borderScore) {
		BORDER_SCORE = borderScore;
	}

	/**
	 * @param mctsBias the mCTS_BIAS to set
	 */
	public static void setMCTS_BIAS(float mctsBias) {
		MCTS_BIAS = mctsBias;
	}

}
