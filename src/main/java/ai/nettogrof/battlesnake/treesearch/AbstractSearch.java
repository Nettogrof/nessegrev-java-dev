package ai.nettogrof.battlesnake.treesearch;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.info.GameRuleset;
import ai.nettogrof.battlesnake.info.SnakeInfo;
import ai.nettogrof.battlesnake.snakes.common.BattleSnakeConstants;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

/**
 * This abstract search class is the based of all search class, provide basic
 * method use in any search.
 * 
 * @author carl.lajeunesse
 * @version Spring 2021
 */
public abstract class AbstractSearch implements Runnable {

	/**
	 * Control variable to continue the search or not
	 */
	protected boolean cont;

	/**
	 * Root node for the search
	 */
	protected AbstractNode root;

	/**
	 * Board height
	 */
	protected int height;

	/**
	 * Board width
	 */
	protected int width;

	/**
	 * Time allowed for the search
	 */
	protected int timeout = 250;

	/**
	 * Starting time for the search in millisecond
	 */
	protected long startTime;

	/**
	 * Object containing Game Rules
	 */
	protected GameRuleset rules;

	/**
	 * Basic Constructor
	 */
	protected AbstractSearch() {
		cont = true;
	}

	/**
	 * This method is used to stop the search
	 */
	public void stopSearching() {
		cont = false;
	}

	/**
	 * This abstract method will be use to "kill" a snake
	 * 
	 * @param death SnakeInfo of the snake to kill
	 * @param all   List of all snakeinfo
	 */
	protected abstract void kill(SnakeInfo death, List<SnakeInfo> all);

	/**
	 * This method is used to generate child node from the root. Mostly used for
	 * multithreading
	 */
	public void generateChild() {
		generateChild(root);
	}

	/**
	 * This method check if there's a head-to-head collision. Shorter snake die, and
	 * if both snakes are the same length both dies
	 * 
	 * @param moves List of all possible move
	 */
	protected void checkHeadToHead(final List<ArrayList<SnakeInfo>> moves) {
		for (final ArrayList<SnakeInfo> move : moves) {

			for (int i = 0; i < move.size() - 1; i++) {
				for (int j = i + 1; j < move.size(); j++) {

					if (move.get(i).getHead() == move.get(j).getHead()) {
						final int firstLength = move.get(i).getSnakeBody().size();
						final int secondLength = move.get(j).getSnakeBody().size();

						if (firstLength > secondLength) {
							kill(move.get(j), move);

						} else if (firstLength == secondLength) {
							kill(move.get(i), move);
							kill(move.get(j), move);
						} else {
							kill(move.get(i), move);
						}
					}
				}
			}
		}
	}

	/**
	 * This method merge previous snake move (list) , with new snake move
	 * 
	 * @param list   Previous list
	 * @param snakes New move list
	 * @return List of List of move
	 */
	protected List<ArrayList<SnakeInfo>> merge(final List<ArrayList<SnakeInfo>> list, final List<SnakeInfo> snakes) {
		List<ArrayList<SnakeInfo>> ret;
		if (snakes.isEmpty()) {
			ret = list;

		} else {
			ret = new ArrayList<>();
			if (list.isEmpty()) {

				for (final SnakeInfo si : snakes) {
					final ArrayList<SnakeInfo> merged = new ArrayList<>(1);
					merged.add(si.cloneSnake());
					ret.add(merged);
				}

			} else {
				for (final SnakeInfo snakeinfo : snakes) {
					for (final ArrayList<SnakeInfo> s : list) {
						final ArrayList<SnakeInfo> merged = new ArrayList<>(s.size() + 1);
						for (final SnakeInfo si : s) {
							merged.add(si.cloneSnake());
						}

						merged.add(snakeinfo.cloneSnake());
						ret.add(merged);
					}

				}
			}
		}
		return ret;
	}

	/**
	 * Create new SnakeInfo based on the current node and the new head square
	 * 
	 * @param snakeInfo previous snakeInfo
	 * @param newHead   New head square
	 * @param node      Previous node
	 * @return new SnakeInfo
	 */
	protected abstract SnakeInfo createSnakeInfo(SnakeInfo snakeInfo, int newHead, AbstractNode node);

	/**
	 * Generate all moves possible for a snake given.
	 * 
	 * @param snakeInfo Information about the snake
	 * @param node      Parent node
	 * @param allSnakes List of all snakes
	 * @return list of snakeinfo
	 */
	protected List<SnakeInfo> generateSnakeInfoDestination(final SnakeInfo snakeInfo, final AbstractNode node,
			final List<SnakeInfo> allSnakes) {
		final ArrayList<SnakeInfo> listNewSnakeInfo = new ArrayList<>();

		if (snakeInfo.isAlive()) {
			moveSnake(snakeInfo, node, allSnakes, listNewSnakeInfo);

		}
		return listNewSnakeInfo;
	}

	/**
	 * Generate move of a snake
	 * 
	 * @param snakeInfo        Current snake
	 * @param node             Current node to add child node
	 * @param allSnakes        List of all snakes to check body collision
	 * @param listNewSnakeInfo fill that list with new snake position
	 */
	protected void moveSnake(final SnakeInfo snakeInfo, final AbstractNode node, final List<SnakeInfo> allSnakes,
			final List<SnakeInfo> listNewSnakeInfo) {
		final int head = snakeInfo.getHead();

		if (head / 1000 > 0) {
			addMove(head - 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
		}

		if (head / 1000 < width - 1) {
			addMove(head + 1000, allSnakes, snakeInfo, node, listNewSnakeInfo);
		}

		if (head % 1000 > 0) {
			addMove(head - 1, allSnakes, snakeInfo, node, listNewSnakeInfo);
		}
		if (head % 1000 < height - 1) {
			addMove(head + 1, allSnakes, snakeInfo, node, listNewSnakeInfo);
		}

	}

	/**
	 * This method add move to the list if the snake can move in the new head
	 * position.
	 * 
	 * @param newhead          New head
	 * @param allSnakes        List of all snakes
	 * @param snakeInfo        Information about the snake
	 * @param node             Parent node
	 * @param listNewSnakeInfo Current list of snakeinfo
	 */
	protected void addMove(final int newhead, final List<SnakeInfo> allSnakes, final SnakeInfo snakeInfo,
			final AbstractNode node, final List<SnakeInfo> listNewSnakeInfo) {
		if (freeSpace(newhead, allSnakes, snakeInfo)) {
			listNewSnakeInfo.add(createSnakeInfo(snakeInfo, newhead, node));
		}

	}

	/**
	 * Check if the snake can move on the square
	 * 
	 * @param square       the int sqaure
	 * @param allSnakes    List of all snakes
	 * @param currentSnake current Snake
	 * @return boolean free to move on that square
	 */
	protected abstract boolean freeSpace(final int square, final List<SnakeInfo> allSnakes, SnakeInfo currentSnake);

	/**
	 * Expand / Generate child from a node
	 * 
	 * @param node the Abstractnode to be expand
	 */
	protected void generateChild(final AbstractNode node) {
		if (node.getChild().isEmpty()) {

			final List<SnakeInfo> current = node.getSnakes();
			final List<SnakeInfo> alphaMove = generateSnakeInfoDestination(current.get(0), node, current);
			node.possibleMove = alphaMove.size();

			if (alphaMove.isEmpty()) {
				node.getSnakes().get(0).die();
				node.exp = false;
				node.score[0] = 0;
				node.updateScoreRatio();

			} else {
				List<ArrayList<SnakeInfo>> moves = new ArrayList<>();
				final int nbSnake = current.size();
				moves = merge(moves, alphaMove);

				for (int i = 1; i < nbSnake; i++) {
					moves = merge(moves, generateSnakeInfoDestination(current.get(i), node, current));
				}

				checkHeadToHead(moves);
				boolean stillAlive = false;
				for (final ArrayList<SnakeInfo> move : moves) {

					if (move.get(0).isAlive()) {
						node.addChild(node.createNode(move, node));
						stillAlive = true;
					} else {

						final AbstractNode lostMove = node.createNode(move, node);
						lostMove.score[0] = 0;
						lostMove.updateScoreRatio();
						node.addChild(lostMove);
					}
				}
				if (!stillAlive) {
					node.getSnakes().get(0).die();
					node.exp = false;
					node.score[0] = 0;
					node.updateScoreRatio();
				}
			}
		} else {
			node.exp = false;
		}
	}

	/**
	 * Get the leaf from the smallest branch
	 * 
	 * @param node Root node
	 * @return leaf node
	 */
	protected AbstractNode getSmallestChild(final AbstractNode node) {
		if (node.getChild().isEmpty()) {
			return node;
		} else {
			node.updateScore();
			AbstractNode smallChild = null;
			if (node.getSnakes().size() < BattleSnakeConstants.MINIMUN_SNAKE) {

				float maxR = -1000;

				for (final AbstractNode childNode : node.getChild()) {
					if (childNode.getScoreRatio() > maxR) {
						maxR = childNode.getScoreRatio();
						smallChild = childNode;
					}

				}

			} else {

				int countChild = Integer.MAX_VALUE;
				for (final AbstractNode childNode : node.getChild()) {
					if (childNode.getChildCount() < countChild && childNode.exp) {
						countChild = childNode.getChildCount();
						smallChild = childNode;
					}
				}

			}
			if (smallChild == null) {
				node.exp = false;
				return node;
			}

			return getSmallestChild(smallChild);
		}
	}

}
