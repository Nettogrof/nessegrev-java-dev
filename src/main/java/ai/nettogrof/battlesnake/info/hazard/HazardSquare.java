package ai.nettogrof.battlesnake.info.hazard;

import com.fasterxml.jackson.databind.JsonNode;

import gnu.trove.list.array.TIntArrayList;

/**
 * HazardSquad is the class that contain all the informations and related
 * methods to hazards, using square to search
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public class HazardSquare extends AbstractHazard {

	

	/**
	 * Single constructor need the jsonNode board
	 * 
	 * @param board JsonNode board element
	 */
	public HazardSquare(final JsonNode board) {
		super(board);
	}

	/**
	 * Take the json field food and convert it into the arraylist hazard
	 * 
	 * @param hazardsInfo JsonNode field hazard
	 */
	@Override
	protected void setInfo(final JsonNode hazardsInfo) {
		if (hazardsInfo != null) {
			for (final JsonNode hazardPos : hazardsInfo) {
				hazard.add(hazardPos.get("x").asInt() * 1000 + hazardPos.get("y").asInt());
			}
		}
	}

	/**
	 * Check if a particular square is an hazard square
	 * 
	 * @param posx Square position X
	 * @param posy Square position Y
	 * @return boolean if square is hazard
	 */
	@Override
	public boolean isHazard(final int posx, final int posy) {
		return hazard.contains(posx * 1000 + posy);
	}

	/**
	 * Check if a particular square is an hazard square (based on square formula)
	 * 
	 * @param pos the square position
	 * @return boolean if square is hazard
	 */
	@Override
	public boolean isHazard(final int pos) {
		return hazard.contains(pos);
	}

	/**
	 * Provide the arraylist of hazards.
	 * 
	 * @return Arraylist int of hazards
	 */
	@Override
	public TIntArrayList getListHazard() {
		return hazard;
	}

}
