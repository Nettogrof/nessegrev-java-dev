package ai.nettogrof.battlesnake.proofnumber;



import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.flogger.FluentLogger;

import gnu.trove.list.array.TIntArrayList;

/**
 *  Data related to a Snake
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
	protected boolean eat = Boolean.FALSE;
	
	/**
	 * If the snake is still alive
	 */
	protected boolean alive = true;
		
	
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
		for (JsonNode bodyPos: snakeInfo.get("body") ) {
			snakeBody.add(bodyPos.get("x").asInt()*1000+ bodyPos.get("y").asInt());

		}

	}

	
	
	
	
	/**
	 * @param pos
	 * @return
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
	 * @return
	 */
	public int getHead() {

		return snakeBody.getQuick(0);

	}

	/**
	 * @return
	 */
	public int getTail() {
		return snakeBody.getQuick(snakeBody.size()-1);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @param health
	 */
	public void setHealth( final int health) {
		if (health == 100) {
			eat = true;
		}
		this.health = health;
	}

	/**
	 * @return
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
	 *
	 */
	@Override
	public SnakeInfo clone() throws CloneNotSupportedException {
		return (SnakeInfo) super.clone();
	}

	/**
	 * @return
	 */
	public boolean isAlive() {
		return alive;
	}

	
	/**
	 * @return
	 */
	public boolean isEat() {
		return eat;
	}


	/**
	 * @param eat
	 */
	public void setEat(final boolean eat) {
		this.eat = eat;
	}


	/**
	 * @param alive
	 */
	public void setAlive(final boolean alive) {
		this.alive = alive;
	}


	/**
	 * @param other
	 * @return
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

/*
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
		final SnakeInfo other = (SnakeInfo) obj;
		return alive == other.alive && eat == other.eat && health == other.health && Objects.equals(name, other.name)
				&& Objects.equals(snakebody, other.snakebody) && Objects.equals(squad, other.squad);
	}*/
	

	/*
	 * public void setAlive(boolean alive) { this.alive = alive; }
	 */
}
