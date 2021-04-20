package ai.nettogrof.battlesnake.proofnumber;

import com.fasterxml.jackson.databind.JsonNode;

import gnu.trove.list.array.TIntArrayList;

/**
 * @author carl.lajeunesse
 *
 */
public class HazardInfo {
	/**
	 * 
	 */
	private transient final TIntArrayList hazard = new TIntArrayList();

	/**
	 * @param board
	 */
	public HazardInfo(final JsonNode board) {
		setInfo(board.get("hazards"));
	}

	/**
	 * @param board
	 */
	public void setHazard(final JsonNode board) {
		setInfo(board.get("hazards"));

	}

	/**
	 * @param hazardsInfo
	 */
	private void setInfo(final JsonNode hazardsInfo) {
		if (hazardsInfo != null) {
			for (int x = 0; x < hazardsInfo.size(); x++) {

				hazard.add(hazardsInfo.get(x).get("x").asInt() * 1000 + hazardsInfo.get(x).get("y").asInt());

			}
		}
	}

	/**
	 * @param posx
	 * @param posy
	 * @return
	 */
	public boolean isHazard(final int posx, final int posy) {

		return hazard.contains(posx * 1000 + posy);
	}

	/**
	 * @param pos
	 * @return
	 */
	public boolean isHazard(final int pos) {

		return hazard.contains(pos);
	}
	
	/**
	 * @return
	 */
	public TIntArrayList getListHazard() {
		return hazard;
	}

}
