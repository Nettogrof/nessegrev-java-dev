package ai.nettogrof.battlesnake.info;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.flogger.FluentLogger;

import gnu.trove.list.array.TIntArrayList;

import static ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants.MAX_HEALTH;

/**
 * Data related to a Snake in non-squad modes
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 * 
 */
public class SnakeInfo {

	/**
	 * Logging object
	 */
	protected static FluentLogger log = FluentLogger.forEnclosingClass();

	/**
	 * Arraylist of the Snake Body. Starting at the head. This array of Integer of
	 * the square (based on square formula)
	 */
	protected final TIntArrayList snakeBody;

	/**
	 * Name of the snake
	 */
	protected String name;

	/**
	 * Current health of the snake
	 */
	protected int health;

	/**
	 * Does the snake just eat
	 */
	protected boolean eat;

	/**
	 * If the snake is still alive
	 */
	protected boolean alive = true;

	/**
	 * Name of the squad of the current snake
	 */
	private final String squad;

	/**
	 * Basic constructor with a empty body.
	 */
	public SnakeInfo() {
		snakeBody = new TIntArrayList();
		squad = "";
	}

	/**
	 * copy constructor .
	 * 
	 * @param snakeInfo the snake info to copy
	 */
	public SnakeInfo(final SnakeInfo snakeInfo) {
		this.name = snakeInfo.name;
		this.squad = snakeInfo.squad;
		this.alive = snakeInfo.alive;
		this.eat = snakeInfo.eat;
		this.health = snakeInfo.health;
		this.snakeBody = new TIntArrayList(snakeInfo.getSnakeBody());
	}

	/**
	 * Constructor with the Json field
	 * 
	 * @param snakeInfo Json field
	 */
	public SnakeInfo(final JsonNode snakeInfo) {
		snakeBody = new TIntArrayList();
		health = snakeInfo.get("health").asInt();
		name = snakeInfo.get("name").asText();
		for (final JsonNode bodyPos : snakeInfo.get("body")) {
			snakeBody.add(bodyPos.get("x").asInt() * 1000 + bodyPos.get("y").asInt());
		}
		if (snakeInfo.get("squad") == null) {
			squad = "";

		} else {
			squad = snakeInfo.get("squad").asText();
		}
	}

	/**
	 * Constructor with all informations
	 * 
	 * @param prevSnakeInfo Same snake on the previous move
	 * @param moveSquare    The destination square of the snake move
	 * @param eat           Is destination square a food
	 * @param hazard        Is destination square a hazard
	 * @param rules         Game ruleset
	 */
	public SnakeInfo(final SnakeInfo prevSnakeInfo, final int moveSquare, final boolean eat, final boolean hazard,
			final GameRuleset rules) {

		if (eat) {
			health = MAX_HEALTH;
			this.eat = true;
		} else {
			health = prevSnakeInfo.getHealth() - 1;
			if (hazard) {
				health -= rules.getHazardDamage();
			}

		}

		snakeBody = new TIntArrayList(prevSnakeInfo.getSnakeBody());
		snakeBody.insert(0, moveSquare);
		if (!prevSnakeInfo.eat) {
			snakeBody.removeAt(snakeBody.size() - 1);
		}

		if (health <= 0) {
			alive = false;
		}
		name = prevSnakeInfo.getName();
		squad = prevSnakeInfo.getSquad();

	}

	/**
	 * @return squad
	 */
	public String getSquad() {
		return squad;
	}

	/**
	 * Constructor with all informations except hazard ( use in non-royale mode)
	 * 
	 * @param prevSnakeInfo Same snake on the previous move
	 * @param moveSquare    The destination square of the snake move
	 * @param eat           Is destination square a food
	 */
	public SnakeInfo(final SnakeInfo prevSnakeInfo, final int moveSquare, final boolean eat) {
		if (eat) {
			health = MAX_HEALTH;
			this.eat = true;
		} else {
			health = prevSnakeInfo.getHealth() - 1;
		}

		snakeBody = new TIntArrayList(prevSnakeInfo.getSnakeBody());
		snakeBody.insert(0, moveSquare);

		snakeBody.removeAt(snakeBody.size() - 1);
		if (eat) {
			snakeBody.insert(snakeBody.size(), snakeBody.getQuick(snakeBody.size() - 1));
		}

		if (health <= 0) {
			alive = false;
		}
		name = prevSnakeInfo.getName();
		squad = prevSnakeInfo.getSquad();

	}

	/**
	 * Get the full snake bodies
	 * 
	 * @return arraylist int of the snake's bodies
	 */
	public TIntArrayList getSnakeBody() {
		return snakeBody;
	}

	/**
	 * Kill the snake
	 */
	public void die() {
		alive = false;
	}

	/**
	 * Check if there will be a snake on square on next move
	 * 
	 * @param pos int square (based on square formula)
	 * @return If there's a snake body
	 */
	public boolean isSnake(final int pos) {

		if (eat) {
			return snakeBody.contains(pos);
		} else {
			if (snakeBody.contains(pos)) {
				return snakeBody.indexOf(pos) < snakeBody.size() - 1;
			} else {
				return false;
			}

		}
	}

	/**
	 * Check if there will be a snake on square on next move
	 * 
	 * @param pos   int square (based on square formula)
	 * @param squad Squad name
	 * @return If there's a snake body
	 */
	public boolean isSnake(final int pos, final String squad) {
		if (this.squad.equals(squad)) {
			if (eat) {
				return snakeBody.contains(pos);
			} else {
				if (snakeBody.contains(pos)) {
					return snakeBody.indexOf(pos) < snakeBody.size() - 1;
				} else {
					return false;
				}

			}
		}
		return false;
	}

	/**
	 * Get the square of the snake head ( first element of the snakeBody)
	 * 
	 * @return int square (based on square formula)
	 */
	public int getHead() {
		return snakeBody.getQuick(0);
	}

	/**
	 * Get the square of the snake head ( last element of the snakeBody)
	 * 
	 * @return int square (based on square formula)
	 */
	public int getTail() {
		return snakeBody.getQuick(snakeBody.size() - 1);
	}

	/**
	 * Get the snake's name
	 * 
	 * @return String name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the health of the snake
	 * 
	 * @return int health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Return if the snake is alive or not
	 * 
	 * @return boolean
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Equals method
	 * 
	 * @param obj other Snakeinfo
	 * @return true if equals
	 */
	@Override
	public boolean equals(final Object obj) {

		// If the object is compared with itself then return true
		if (obj == this) {
			return true;
		}

		/*
		 * Check if o is an instance of Complex or not "null instanceof [type]" also
		 * returns false
		 */
		if (!(obj instanceof SnakeInfo)) {
			return false;
		}
		final SnakeInfo other = (SnakeInfo) obj;
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

		return getTail() == other.getTail();
	}

	/**
	 *
	 */
	@Override
	public int hashCode() {
		return Objects.hash(alive, eat, health, name, snakeBody);
	}

}
