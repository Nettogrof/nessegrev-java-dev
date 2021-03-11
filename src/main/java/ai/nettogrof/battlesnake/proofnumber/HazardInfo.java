package ai.nettogrof.battlesnake.proofnumber;

import com.fasterxml.jackson.databind.JsonNode;

import gnu.trove.list.array.TIntArrayList;

public class HazardInfo {
	private transient final TIntArrayList hazard = new TIntArrayList();

	public HazardInfo(final JsonNode board) {
		setInfo(board.get("hazards"));
	}

	public void setHazard(final JsonNode board) {
		setInfo(board.get("hazards"));

	}

	private void setInfo(final JsonNode hazardsInfo) {
		if (hazardsInfo != null) {
			for (int x = 0; x < hazardsInfo.size(); x++) {

				hazard.add(hazardsInfo.get(x).get("x").asInt() * 1000 + hazardsInfo.get(x).get("y").asInt());

			}
		}
	}

	public boolean isHazard(final int posx, final int posy) {

		return hazard.contains(posx * 1000 + posy);
	}

	public boolean isHazard(final int pos) {

		return hazard.contains(pos);
	}

}
