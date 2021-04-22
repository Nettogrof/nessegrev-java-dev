package ai.nettogrof.battlesnake.info;



import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.flogger.FluentLogger;

import gnu.trove.list.array.TIntArrayList;

/**
 *  Data related to a Snake in non-squad modes
 * 
 * @author carl.lajeunesse
 * @version  Spring 2021 
 * 
 */
public class SnakeInfo implements Cloneable {
	
	/**
	 * Logging object
	 */
	protected static transient FluentLogger log = FluentLogger.forEnclosingClass();
	
	/**
	 *  Arraylist of the Snake Body.  Starting at the head. This array of Integer of the square (based on square formula)
	 */
	protected final TIntArrayList snakeBody ;
	
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
	protected transient boolean eat ;
	
	/**
	 * If the snake is still alive
	 */
	protected transient boolean alive = true;
		
	
	/**
	 * Basic constructor with a empty body.
	 */
	public SnakeInfo() {
		snakeBody = new TIntArrayList();
	}

		
	/**
	 * Constructor with all informations
	 * @param prevSnakeInfo Same snake on the previous move
	 * @param moveSquare The destination square of the snake move
	 * @param eat  Is destination square a food 
	 * @param hazard Is destination square a hazard
	 */
	public SnakeInfo(final SnakeInfo prevSnakeInfo,final int moveSquare,final boolean eat,final boolean hazard) {
		if (eat) {
			health = 100;
			this.eat = true;
		} else {
			health = prevSnakeInfo.getHealth() - 1;
		}

		snakeBody = new TIntArrayList(prevSnakeInfo.getSnakeBody());
		snakeBody.insert(0,moveSquare);
		if (!prevSnakeInfo.eat) {
			snakeBody.removeAt(snakeBody.size()-1);
		}
		
		
		if (hazard) {
			health-=15;
		}
		if (health <= 0) {
			alive =false;
		}
		name = prevSnakeInfo.getName();
		
	}
	
	/**
	 * Constructor with all informations except hazard  ( use in non-royale mode) 
	 * @param prevSnakeInfo Same snake on the previous move
	 * @param moveSquare The destination square of the snake move
	 * @param eat  Is destination square a food 
	 */
	public SnakeInfo(final SnakeInfo prevSnakeInfo,final int moveSquare,final boolean eat) {
		if (eat) {
			health = 100;
			this.eat = true;
		} else {
			health = prevSnakeInfo.getHealth() - 1;
		}

		snakeBody = new TIntArrayList(prevSnakeInfo.getSnakeBody());
		snakeBody.insert(0,moveSquare);
		
		snakeBody.removeAt(snakeBody.size()-1);
		if (eat) {
			snakeBody.insert(snakeBody.size(), snakeBody.getQuick(snakeBody.size()-1));
		}
		
		if (health <= 0) {
			alive =false;
		}
		name = prevSnakeInfo.getName();
		
	}

	/**
	 * Get the full snake bodies
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
	 * Set the snake bodies
	 * @param snakeInfo JsonNode Snake field
	 */
	public void setSnake(final JsonNode snakeInfo) {
		for (final JsonNode bodyPos: snakeInfo.get("body") ) {
			snakeBody.add(bodyPos.get("x").asInt()*1000+ bodyPos.get("y").asInt());
		}

	}

	
	
	
	
	/**
	 * Check if there will be a snake on square on next move
	 * @param pos   int square  (based on square formula)
	 * @return If there's a snake body
	 */
	public boolean isSnake( final int pos) {
		
		
		if (eat) {
			return snakeBody.contains(pos);
		}else {
			if ( snakeBody.contains(pos)) {
				return snakeBody.indexOf(pos) < snakeBody.size() -1;
			}else {
				return false;
			}
			  
		}
	}

	/**
	 * Get the square of the snake head ( first element of the snakeBody)
	 * @return int square  (based on square formula)
	 */
	public int getHead() {
		return snakeBody.getQuick(0);
	}

	/**
	 * Get the square of the snake head ( last element of the snakeBody)
	 * @return int square  (based on square formula)
	 */
	public int getTail() {
		return snakeBody.getQuick(snakeBody.size()-1);
	}

	/**
	 * Get the snake's name
	 * @return String name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the snake's name
	 * @param name Snake name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the health of the snake
	 * @return int health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Set the health of the snake
	 * @param health  health of the snake
	 */
	public void setHealth( final int health) {
		if (health == 100) {
			eat = true;
		}
		this.health = health;
	}

	/**
	 * Clone the snake
	 * @return the new object clone 
	 */
	public SnakeInfo cloneSnake() {
		SnakeInfo clonedSnake = null ;
		try {
			clonedSnake = clone();
		} catch (CloneNotSupportedException e) {
		
			log.atWarning().log(e.getMessage() + "\n" + e.getStackTrace());
			
		}
		return clonedSnake;

	}

	/**
	 * Clone the snake
	 * @return the new object clone 
	 */
	@Override
	public SnakeInfo clone() throws CloneNotSupportedException {
		return (SnakeInfo) super.clone();
	}

	/**
	 * Return if the snake is alive or not
	 * @return boolean 
	 */
	public boolean isAlive() {
		return alive;
	}

	
	/**
	 * Return if the snake just eat.
	 * @return boolean
	 */
	public boolean isEat() {
		return eat;
	}

	/**
	 * Equals method
	 * @param other  other Snakeinfo
	 * @return true if equals
	 */
	public boolean equals(final SnakeInfo other) {
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
