package ai.nettogrof.battlesnake.proofnumber;

import com.fasterxml.jackson.databind.JsonNode;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

/**
 * FoodInfo is the class that contain all the informations and related methods to foods
 * @author carl.lajeunesse
 * @version  Spring 2021 
 */
public class FoodInfo {
	
	/**
	 * Arraylist (int) of all food position  (based on square formula)
	 */
	private transient  final TIntArrayList position = new TIntArrayList();

	
	/**
	 * Single constructor need the jsonNode board
	 * @param board  JsonNode board element
	 */
	public FoodInfo(final JsonNode board) {
		setFoodInfo(board.get("food"));
	}


	/**
	 * Get the shortest distance from the position provided to a food.  Doesn't check Snake bodies/hazard/etc 
	 * @param headX  The position X
	 * @param headY  The position Y
	 * @return int the number of square between the pos, and the nearest food
	 */
	public int getShortestDistance(final int headX, final int headY) {
		int shortDis = Integer.MAX_VALUE;
		final TIntIterator longIterator = position.iterator();
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
	 * Get the shortest distance from the position provided to a food.  Doesn't check Snake bodies/hazard/etc
	 * @param headPos The square position (based on square formula)
	 * @return int the number of square between the pos, and the nearest food
	 */
	public int getShortestDistance(final int headPos) {
		return getShortestDistance(headPos / 1000,  headPos % 1000);
		
	}

	
	/**
	 * Check if there's a food on a particular square
	 * @param squareX  Square position X
	 * @param squareY  Square position Y
	 * @return boolean if there's a food
	 */
	public boolean isFood(final int squareX, final int squareY) {

		return position.contains(squareX * 1000 + squareY);

	}
	
	/**
	 * Check if there's a food on a particular square (based on square formula)
	 * @param pos
	 * @return boolean if there's a food
	 */
	public boolean isFood(final int pos) {
		return position.contains(pos);
	}
	

	/**
	 * Take the json field food and convert it into the arraylist position
	 * 
	 * @param foodArray  JsonNode field food
	 */
	private void setFoodInfo(final JsonNode foodArray) {
		for (JsonNode foodPos : foodArray) {
			position.add(foodPos.get("x").asInt() * 1000 + foodPos.get("y").asInt());

		}

	}
		

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
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		return true;
	}

}
