/**
 * 
 */
package ai.nettogrof.battlesnake.proofnumber;

import java.util.Objects;

/**
 * @author carl.lajeunesse
 *
 */
public class SnakeInfoSquad extends SnakeInfo {

	
	private String squad = "";
	
	/**
	 * 
	 */
	public SnakeInfoSquad() {
		super();
	}

	/**
	 * @param prevSnakeInfo
	 * @param moveSquare
	 * @param eat
	 * @param hazard
	 */
	public SnakeInfoSquad(final SnakeInfoSquad prevSnakeInfo,final int moveSquare,final boolean eat,final boolean hazard) {
		super(prevSnakeInfo, moveSquare, eat, hazard);
		squad = prevSnakeInfo.getSquad();
		
	}

	/**
	 * @param prevSnakeInfo
	 * @param moveSquare
	 * @param eat
	 */
	public SnakeInfoSquad(final SnakeInfoSquad prevSnakeInfo,final int moveSquare,final boolean eat) {
		super(prevSnakeInfo, moveSquare, eat);
		squad = prevSnakeInfo.getSquad();
		
	}
	
	/**
	 * @return the squad
	 */
	/**
	 * @return
	 */
	public String getSquad() {
		return squad;
	}

	/**
	 * @param squad the squad to set
	 */
	/**
	 * @param squad
	 */
	public void setSquad(final String squad) {
		this.squad = squad;
	}
	
	/**
	 *
	 */
	@Override
	public int hashCode() {
		return Objects.hash(alive, eat, health, name, snakeBody,squad);
	}
	
	/**
	 * @param pos
	 * @param squad
	 * @return
	 */
	public boolean isSnake( final int pos,final String squad) {
		if (!"".equals(squad) && squad.equals(this.squad)) {
			return false;
		}
		
		return isSnake(pos);
	}


}
