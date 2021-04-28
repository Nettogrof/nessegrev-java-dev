/**
 * 
 */
package ai.nettogrof.battlesnake.info;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Data related to a Snake in squad modes
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 * 
 */
public class SnakeInfoSquad extends SnakeInfo {

	/**
	 * Name of the squad of the current snake
	 */
	private String squad = "";

	/**
	 * Basic constructor
	 */
	public SnakeInfoSquad() {
		super();
	}

	/**
	 * Constructor with the Json field
	 * 
	 * @param snakeInfo Json field
	 */
	public SnakeInfoSquad(final JsonNode snakeInfo) {
		super(snakeInfo);
		if (snakeInfo.get("squad") != null) {
			squad = snakeInfo.get("squad").asText();
		}

	}

	/**
	 * Constructor with all informations
	 * 
	 * @param prevSnakeInfo Same snake on the previous move
	 * @param moveSquare    The destination square of the snake move
	 * @param eat           boolean is destination square a food
	 * @param hazard        boolean is destination square a hazard
	 */
	public SnakeInfoSquad(final SnakeInfoSquad prevSnakeInfo, final int moveSquare, final boolean eat,
			final boolean hazard) {
		super(prevSnakeInfo, moveSquare, eat, hazard);
		squad = prevSnakeInfo.getSquad();

	}

	/**
	 * Constructor with all informations except hazard ( use in non-royale mode)
	 * 
	 * @param prevSnakeInfo Same snake on the previous move
	 * @param moveSquare    The destination square of the snake move
	 * @param eat           boolean Is destination square a food
	 */
	public SnakeInfoSquad(final SnakeInfoSquad prevSnakeInfo, final int moveSquare, final boolean eat) {
		super(prevSnakeInfo, moveSquare, eat);
		squad = prevSnakeInfo.getSquad();

	}

	/**
	 * Gets the squad name
	 * 
	 * @return the squad name
	 */
	public String getSquad() {
		return squad;
	}

	/**
	 * Fill the squad name, if use in a non-squad mode, squad name should be left
	 * empty
	 * 
	 * @param squad the squad to set
	 */
	public void setSquad(final String squad) {
		this.squad = squad;
	}

	/**
	 * Equals method
	 * 
	 * @param other other SnakeinfoSquad
	 * @return true if equals
	 */
	public boolean equals(final SnakeInfoSquad other) {
		if (!name.equals(other.name)) {
			return false;
		}
		if (health != other.getHealth()) {
			return false;
		}

		if (snakeBody.size() != other.getSnakeBody().size()) {
			return false;
		}
		if (getHead() != other.getHead()) {
			return false;
		}

		if (getSquad().equals(other.getSquad())) {
			return false;
		}

		return getTail() == other.getTail();
	}

	/**
	 *
	 */
	@Override
	public int hashCode() {
		return Objects.hash(alive, eat, health, name, snakeBody, squad);
	}

	/**
	 * Check if there will be a snake on square on next move, if it's the same squad
	 * should return false.
	 * 
	 * @param pos   int square (based on square formula)
	 * @param squad String name of the squad
	 * @return If there's a snake body
	 */
	public boolean isSnake(final int pos, final String squad) {
		if (!"".equals(squad) && squad.equals(this.squad)) {
			return false;
		}

		return isSnake(pos);
	}

}
