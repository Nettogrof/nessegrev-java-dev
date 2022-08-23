package ai.nettogrof.battlesnake.info;

/**
 * Data related to a Snake in non-squad modes
 * 
 * @author carl.lajeunesse
 * @version Summer 2022
 * 
 */
public class BoardInfo {

	/**
	 * Board height
	 */
	protected final int height;

	/**
	 * Board width
	 */
	protected final int width;

	/**
	 * Simple constructor
	 * 
	 * @param height Board height
	 * @param width  Board width
	 */
	public BoardInfo(final int height, final int width) {
		this.height = height;
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

}
