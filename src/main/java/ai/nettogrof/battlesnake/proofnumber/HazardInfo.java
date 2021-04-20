package ai.nettogrof.battlesnake.proofnumber;

import com.fasterxml.jackson.databind.JsonNode;
import gnu.trove.list.array.TIntArrayList;

/**
 *  HazardInfo is the class that contain all the informations and related methods to hazards
 * @author carl.lajeunesse
 * @version  Spring 2021 
 */
public class HazardInfo {
	
	/**
	 * Arraylist (int) of all hazard position  (based on square formula)
	 */
	private transient final TIntArrayList hazard = new TIntArrayList();

	/**
	 * Single constructor need the jsonNode board
	 * @param board  JsonNode board element
	 */
	public HazardInfo(final JsonNode board) {
		setInfo(board.get("hazards"));
	}

	
	/**
	 *  Take the json field food and convert it into the arraylist hazard
	 * @param hazardsInfo JsonNode field hazard
	 */
	private void setInfo(final JsonNode hazardsInfo) {
		if (hazardsInfo != null) {
			for (JsonNode hazardPos : hazardsInfo) {
				hazard.add(hazardPos.get("x").asInt() * 1000 + hazardPos.get("y").asInt());
			}
		}
	}

	/**
	 * Check if a particular square is an hazard square
	 * @param posx Square position X
	 * @param posy Square position Y
	 * @return boolean if square is hazard
	 */
	public boolean isHazard(final int posx, final int posy) {
		return hazard.contains(posx * 1000 + posy);
	}

	/**
	 * Check if a particular square is an hazard square  (based on square formula)
	 * @param pos
	 * @return boolean if square is hazard
	 */
	public boolean isHazard(final int pos) {
		return hazard.contains(pos);
	}
	
	/**
	 * Provide the arraylist of hazards.
	 * @return Arraylist int of hazards
	 */
	public TIntArrayList getListHazard() {
		return hazard;
	}

}
