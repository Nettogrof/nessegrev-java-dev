package ai.nettogrof.battlesnake.proofnumber;

import com.fasterxml.jackson.databind.JsonNode;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

/**
 * @author carl.lajeunesse
 *
 */
public class FoodInfo {
	//private short[][] food;
	/**
	 * 
	 */
	private transient  final TIntArrayList integers = new TIntArrayList();

	/*public FoodInfo() {

	}*/

	/**
	 * @param board
	 */
	public FoodInfo(final JsonNode board) {
		setFoodInfo(board.get("food"));
	}

	/*public short[][] getFood() {
		return food;
	}
*/
	/**
	 * @param headX
	 * @param headY
	 * @return
	 */
	public int getShortestDistance(final int headX, final int headY) {
		int shortDis = Integer.MAX_VALUE;
		final TIntIterator longIterator = integers.iterator();
		while (longIterator.hasNext()) {
			final int foodpos = longIterator.next();
			final int targetx = foodpos / 1000;
			final int targety = foodpos % 1000;

			if (Math.abs(targetx - headX) + Math.abs(targety - headY) < shortDis) {
				shortDis = Math.abs(targetx - headX) + Math.abs(targety - headY);

			}
		}

		return shortDis;

	}

	/**
	 * @param board
	 */
	public void setFood(final JsonNode board) {
		setFoodInfo(board.get("food"));
	}

	/**
	 * @param squareX
	 * @param squareY
	 * @return
	 */
	public boolean isFood(final int squareX, final int squareY) {

		return integers.contains(squareX * 1000 + squareY);

	}
	/**
	 * @param pos
	 * @return
	 */
	public boolean isFood(final int pos) {

		return integers.contains(pos);

	}

	/**
	 * @param foodI
	 */
	private void setFoodInfo(final JsonNode foodI) {

		for (int x = 0; x < foodI.size(); x++) {

			integers.add(foodI.get(x).get("x").asInt() * 1000 + foodI.get(x).get("y").asInt());

		}

	}
	
	
	/*public boolean equals(final FoodInfo original) {
		if (integers.size() != original.integers.size()) {
			return false;
		}
		for (int x = 0 ; x <integers.size() ; x++ ) {
			if (!original.isFood(integers.get(x))) {
				return false;
			}
		}
		return true;
	}*/

	

	/**
	 *
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FoodInfo other = (FoodInfo) obj;
		if (integers == null) {
			if (other.integers != null) {
				return false;
			}
		} else if (!integers.equals(other.integers)) {
			return false;
		}
		return true;
	}

}
