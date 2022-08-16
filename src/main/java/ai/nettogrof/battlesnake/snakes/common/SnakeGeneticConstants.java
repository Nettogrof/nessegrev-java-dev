/**
 * 
 */
package ai.nettogrof.battlesnake.snakes.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import com.google.common.flogger.FluentLogger;

/**
 * This class keep some Evaluation constant value
 * 
 * @version Summer 2021
 */
public final class SnakeGeneticConstants {
	
	/**
	 * Logger
	 */
	private static final FluentLogger LOG = FluentLogger.forEnclosingClass();

	/**
	 * Stop expand a node when the score ratio is higher than that value
	 */
	private static float stopExpandLimit = 42; // 42 is the answer of life.

	/**
	 * Value if a FOOD is found in the controlled AREA
	 */
	private static int foodValueArea = 2;

	/**
	 * Value if a tail is found in the controlled AREA
	 */
	private static int tailValueArea = 5;

	/**
	 * Border score to subtract if head on the border of the board
	 */
	private static float borderScore = 0.4f;

	

	/**
	 * Hazard score to subtract if head in hazard
	 */
	private static float hazardScore = 0.9f;

	/**
	 * The Bias value for the Monte-Carlo Tree Search
	 */
	private static float mctsBias = 0.7f;

	/**
	 * Private constructor because this is a utility class
	 */
	private SnakeGeneticConstants() {
	}

	/**
	 * Set Food Value
	 * @param foodValue the fOOD_VALUE_AREA to set
	 */
	public static void setFoodValue(final int foodValue) {
		foodValueArea = foodValue;
	}

	/**
	 * Set tail value
	 * @param tailValue the tail value  to set
	 */
	public static void setTailValueArea(final int tailValue) {
		tailValueArea = tailValue;
	}

	/**
	 * Set Border score
	 * @param border the bORDER_SCORE to set
	 */
	public static void setBorderScore(final float border) {
		borderScore = border;
	}

	/**
	 * set Hazard score for evalution
	 * @param hazard the hazardScore to set
	 */
	public static void setHazardScore(final float hazard) {
		hazardScore = hazard;
	}

	/**
	 * Set mcts bias
	 * @param newMctsBias the mCTS_BIAS to set
	 */
	public static void setMCTS(final float newMctsBias) {
		mctsBias = newMctsBias;
	}

	/**
	 * set stop expand list
	 * @param sel Stop Expand Limit
	 */
	public static void setStopExpandLimit(final float sel) {
		stopExpandLimit = sel;
	}
	
	/**
	 * @return the stopExpandLimit
	 */
	public static float getStopExpandLimit() {
		return stopExpandLimit;
	}

	/**
	 * @return the foodValueArea
	 */
	public static int getFoodValueArea() {
		return foodValueArea;
	}

	/**
	 * @return the tailValueArea
	 */
	public static int getTailValueArea() {
		return tailValueArea;
	}

	/**
	 * @return the borderScore
	 */
	public static float getBorderScore() {
		return borderScore;
	}

	/**
	 * @return the hazardScore
	 */
	public static float getHazardScore() {
		return hazardScore;
	}

	/**
	 * @return the mctsBias
	 */
	public static float getMctsBias() {
		return mctsBias;
	}
	
	/**
	 * Load evaluation properties to get genetic value
	 */
	public static void loadEvaluationValue() {
		try (InputStream input = Files.newInputStream(Paths.get("evaluation.properties"))) {

			final Properties prop = new Properties();

			prop.load(input);

			setStopExpandLimit(Float.parseFloat(prop.getProperty("stopExpandLimit")));
			setBorderScore(Float.parseFloat(prop.getProperty("borderScore")));
			setMCTS(Float.parseFloat(prop.getProperty("mctsBias")));
			setFoodValue(Integer.parseInt(prop.getProperty("foodValue")));
			setTailValueArea(Integer.parseInt(prop.getProperty("tailValue")));
			LOG.atInfo().log("Evalution Value loaded successfully");
		} catch (IOException ex) {
			LOG.atWarning().log("Issue with the evaluation.properties file");
		}
	}

}
