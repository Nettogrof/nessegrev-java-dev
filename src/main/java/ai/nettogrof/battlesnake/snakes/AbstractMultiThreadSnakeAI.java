/**
 * 
 */
package ai.nettogrof.battlesnake.snakes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants;
import ai.nettogrof.battlesnake.treesearch.AbstractSearch;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * Any snake using a multithread tree-search could extend this abstract class it contains
 * basic constant fields, related to the field name in json call from
 * BattleSnake. Also some method that any snake must implements.
 * 
 * @author carl.lajeunesse
 * @version Summer 2022
 */
public abstract class AbstractMultiThreadSnakeAI extends AbstractTreeSearchSnakeAI {

	/**
	 * Boolean if multithread is use by the snake value define by the config file
	 */
	protected transient boolean multiThread;

	/**
	 * Number of CPU / thread permit
	 */
	protected transient int cpuLimit = 2;

	/**
	 * Basic and unused constructor
	 */
	public AbstractMultiThreadSnakeAI() {
		super();
	}

	/**
	 * Constructor with the gameid,
	 * 
	 * @param gameId String of the gameid field receive in the start request.
	 */
	public AbstractMultiThreadSnakeAI(String gameId) {
		super(gameId);
	}

	/**
	 * Set Multithread flag
	 * 
	 * @param multiThread the multiThread to set
	 */
	public void setMultiThread(final boolean multiThread) {
		this.multiThread = multiThread;
	}

	/**
	 * Set the CPU limit count
	 * 
	 * @param cpuLimit the cpuLimit to set
	 */
	public void setCpuLimit(final int cpuLimit) {
		this.cpuLimit = cpuLimit;
	}

	/**
	 * Expand the base list of node until reaching CPU limit
	 * 
	 * @param nodelist     List of node that gonna to "rooted" in multithread search
	 * @param expandedlist List of node to be updated after search
	 * @throws ReflectiveOperationException In case of invalid search type
	 */
	protected void expand(final List<AbstractNode> nodelist, final List<AbstractNode> expandedlist,
			final GameRuleset rules) throws ReflectiveOperationException {
		boolean cont = true;
		while (cont) {
			if (nodelist.isEmpty()) {
				cont = false;
			} else {
				searchType.newInstance(nodelist.get(0), width, height, 0, 0, rules).generateChild();
				if (nodelist.size() - 1 + nodelist.get(0).getChild().size() < cpuLimit) {
					final AbstractNode oldroot = nodelist.remove(0);
					expandedlist.add(oldroot);

					for (final AbstractNode c : oldroot.getChild()) {
						nodelist.add(c);
					}
					cont = true;
				} else {
					cont = false;
				}
			}
		}

	}

	/**
	 * Execute the tree search
	 * 
	 * @param root      The root node
	 * @param startTime The start time in millisecond
	 * @throws ReflectiveOperationException In case of invalid search type
	 */
	@Override
	protected void treeSearch(final AbstractNode root, final Long startTime, final GameRuleset rules)
			throws ReflectiveOperationException {
		if (multiThread && root.getSnakes().size() < 5) {
			final ArrayList<AbstractNode> nodelist = new ArrayList<>();
			final ArrayList<AbstractNode> expandedlist = new ArrayList<>();
			nodelist.add(root);
			expand(nodelist, expandedlist, rules);

			final ArrayList<AbstractSearch> listSearchThread = new ArrayList<>();

			for (final AbstractNode c : root.getChild()) {
				listSearchThread.add(searchType.newInstance(c, width, height, startTime, timeout - minusbuffer, rules));

			}

			for (final AbstractSearch s : listSearchThread) {
				startThread(s);
			}

			try {

				Thread.sleep(timeout - minusbuffer - 50);
			} catch (InterruptedException e) {

				log.atSevere().log("Thread?!", e);
			}

			for (final AbstractSearch search : listSearchThread) {
				search.stopSearching();

			}

			for (final AbstractNode c : nodelist) {
				c.updateScore();
			}

			for (int i = expandedlist.size() - 1; i >= 0; i--) {
				expandedlist.get(i).updateScore();
			}
			log.atInfo().log("Nb Thread: " + nodelist.size());
		} else {
			// Single thread
			final AbstractSearch main = searchType.newInstance(root, width, height, startTime, timeout - minusbuffer,
					rules);

			if (main == null) {
				log.atSevere().log("Unable to find Search Type ");
			} else {
				main.run();
			}

		}
	}

	/**
	 * Set the properties to the snake object
	 */
	@Override
	protected void setProperties() {
		try (InputStream input = Files.newInputStream(Paths.get(fileConfig))) {

			final Properties prop = new Properties();

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			apiversion = Integer.parseInt(prop.getProperty("apiversion"));
			minusbuffer = Integer.parseInt(prop.getProperty("minusbuffer"));
			multiThread = Boolean.parseBoolean(prop.getProperty("multiThread"));
			cpuLimit = Integer.parseInt(prop.getProperty("cpu"));

			final Random rand = new Random();
			losing = BattleSnakeConstants.LOSE_SHOUT[rand.nextInt(BattleSnakeConstants.LOSE_SHOUT.length)];
			winning = BattleSnakeConstants.WIN_SHOUT[rand.nextInt(BattleSnakeConstants.WIN_SHOUT.length)];

		} catch (IOException ex) {
			log.atWarning().log(ex.getMessage() + "\n" + ex.getStackTrace());
		}
	}

	/**
	 * Start the thread search
	 * 
	 * @param search the search to be executed
	 */
	protected void startThread(final AbstractSearch search) {
		final Thread subThread = new Thread(search);
		subThread.setPriority(3);
		subThread.start();
	}
}
