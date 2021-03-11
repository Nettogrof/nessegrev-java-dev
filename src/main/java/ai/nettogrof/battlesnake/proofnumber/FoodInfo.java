package ai.nettogrof.battlesnake.proofnumber;

import com.fasterxml.jackson.databind.JsonNode;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

public class FoodInfo {
	//private short[][] food;
	private transient  final TIntArrayList integers = new TIntArrayList();

	/*public FoodInfo() {

	}*/

	public FoodInfo(final JsonNode board) {
		setFoodInfo(board.get("food"));
	}

	/*public short[][] getFood() {
		return food;
	}
*/
	public int getShortestDistance(final int x, final int y) {
		int shortDis = Integer.MAX_VALUE;
		final TIntIterator longIterator = integers.iterator();
		while (longIterator.hasNext()) {
			final int it = longIterator.next();
			final int tx = it / 1000;
			final int ty = it % 1000;

			if (Math.abs(tx - x) + Math.abs(ty - y) < shortDis) {
				shortDis = Math.abs(tx - x) + Math.abs(ty - y);

			}
		}

		return shortDis;

	}

	public void setFood(final JsonNode board) {
		setFoodInfo(board.get("food"));
	}

	public boolean isFood(final int x, final int y) {

		return integers.contains(x * 1000 + y);

	}
	public boolean isFood(final int pos) {

		return integers.contains(pos);

	}

	private void setFoodInfo(final JsonNode foodI) {

		for (int x = 0; x < foodI.size(); x++) {

			integers.add(foodI.get(x).get("x").asInt() * 1000 + foodI.get(x).get("y").asInt());

		}

	}
	
	public boolean equals(FoodInfo b) {
		if (integers.size() != b.integers.size()) {
			return false;
		}
		for (int x = 0 ; x <integers.size() ; x++ ) {
			if (!b.isFood(integers.get(x))) {
				return false;
			}
		}
		return true;
	}

}
