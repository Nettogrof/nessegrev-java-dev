package ai.nettogrof.battlesnake.info.hazard;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Abstract hazard class
 * 
 * @author carl.lajeunesse
 * @version Fall 2021
 *
 */
public abstract class AbstractHazard {

	/**
	 * Basic constructor
	 */
	public AbstractHazard() {
		// Basic constructor
	}

	/**
	 * Single constructor need the jsonNode board
	 * 
	 * @param board JsonNode board element
	 */
	public AbstractHazard(final JsonNode board) {
		setInfo(board.get("hazards"));
	}

	/**
	 * Check if a particular square is an hazard square
	 * 
	 * @param posx Square position X
	 * @param posy Square position Y
	 * @return boolean if square is hazard
	 */
	public abstract boolean isHazard(final int posx, final int posy);

	/**
	 * Check if a particular square is an hazard square (based on square formula)
	 * 
	 * @param pos the square position
	 * @return boolean if square is hazard
	 */
	public abstract boolean isHazard(final int pos);

	/**
	 * Take the json field food and convert it into the arraylist hazard
	 * 
	 * @param hazardsInfo JsonNode field hazard
	 */
	protected abstract void setInfo(final JsonNode hazardsInfo);
}
