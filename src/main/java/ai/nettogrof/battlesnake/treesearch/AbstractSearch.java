package ai.nettogrof.battlesnake.treesearch;

import java.util.ArrayList;
import java.util.List;

import ai.nettogrof.battlesnake.proofnumber.SnakeInfo;
import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;
import gnu.trove.list.array.TFloatArrayList;

public abstract class AbstractSearch implements Runnable {
	protected transient boolean cont = true;
	protected transient AbstractNode root;

	protected transient int heigth;
	protected transient int width;
	protected transient int timeout = 250;
	protected transient long startTime;
	
	public AbstractSearch() {
		//basic constructor
	}

	@Override
	public abstract void run();

	public void stopSearching() {
		cont = false;
	}

	protected abstract void kill(SnakeInfo death, List<SnakeInfo> all);

	public void generateChild() {
		generateChild(root);
	}

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

	protected ArrayList<ArrayList<SnakeInfo>> merge(final ArrayList<ArrayList<SnakeInfo>> list,
			final List<SnakeInfo> snakes) {
		ArrayList<ArrayList<SnakeInfo>> ret;
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
				for (int i = 0; i < snakes.size(); i++) {
					// 2 3 4

					for (final ArrayList<SnakeInfo> s : list) {
						final ArrayList<SnakeInfo> merged = new ArrayList<>(s.size() + 1);
						for (final SnakeInfo si : s) {
							merged.add(si.cloneSnake());
						}

						merged.add(snakes.get(i).cloneSnake());
						ret.add(merged);
					}

				}
			}

		}

		return ret;
	}


	protected abstract SnakeInfo createSnakeInfo(SnakeInfo snakeInfo, int newHead, AbstractNode node);

	protected ArrayList<SnakeInfo> multi(final SnakeInfo snakeInfo, final AbstractNode node, final List<SnakeInfo> all) {
		final ArrayList<SnakeInfo> ret = new ArrayList<>();

		if (snakeInfo.isAlive()) {
			final int head = snakeInfo.getHead();
			int newhead;
			if (head / 1000 > 0) {

				newhead = head - 1000;
				if (freeSpace(newhead, all, snakeInfo)) {
					ret.add(createSnakeInfo(snakeInfo, newhead, node));
				}

			}

			if (head / 1000 < width - 1) {

				newhead = head + 1000;
				if (freeSpace(newhead, all, snakeInfo)) {
					ret.add(createSnakeInfo(snakeInfo, newhead, node));
				}

			}

			if (head % 1000 > 0) {

				newhead = head - 1;
				if (freeSpace(newhead, all, snakeInfo)) {
					ret.add(createSnakeInfo(snakeInfo, newhead, node));
				}

			}

			if (head % 1000 < heigth - 1) {

				newhead = head + 1;
				if (freeSpace(newhead, all, snakeInfo)) {
					ret.add(createSnakeInfo(snakeInfo, newhead, node));
				}

			}
		}

		return ret;
	}

	protected abstract boolean freeSpace(final int square, final List<SnakeInfo> all, SnakeInfo yourSnake);

	protected void generateChild(final AbstractNode node) {
		if (node.getChild().size() > 0) {

			node.exp = false;

		} else {

			final List<SnakeInfo> current = node.getSnakes();

			final ArrayList<SnakeInfo> alphaMove = multi(current.get(0), node, current);
			node.possibleMove = alphaMove.size();

			if (alphaMove.isEmpty()) {
				node.getSnakes().get(0).die();
				node.exp = false;
				node.score[0] = 0;
				node.updateScoreRatio();

			} else {
				ArrayList<ArrayList<SnakeInfo>> moves = new ArrayList<>();
				final int nbSnake = current.size();
				moves = merge(moves, alphaMove);

				for (int i = 1; i < nbSnake; i++) {
					moves = merge(moves, multi(current.get(i), node, current));
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
			
		}
	}
	
	protected AbstractNode getSmallestChild(final AbstractNode node) {
		if (node.getChild().isEmpty()) {
			return node;
		} else {
			node.updateScore();
			AbstractNode smallChild = null;
			if (node.getSnakes().size() == 1) {

				float maxR = -1000;

				for (final AbstractNode c : node.getChild()) {
					if (c.getScoreRatio() > maxR) {
						maxR = c.getScoreRatio();
						smallChild = c;
					}

				}

			} else  {

				int countChild = Integer.MAX_VALUE;
				for (final AbstractNode childNode : node.getChild()) {
					if (childNode.getChildCount() < countChild && childNode.exp) {
						countChild = childNode.getChildCount();
						smallChild = childNode;
					}
				}

			}
			if (smallChild == null) {
				// System.out.println("exp"+exp);
				node.exp = false;
				return node;
			}

			return getSmallestChild(smallChild);
		}
	}
	
	protected List<AbstractNode> getBestPath(AbstractNode node) {

			if (node.getChild().isEmpty()) {
				final List<AbstractNode> list = new ArrayList<>();
				list.add(node);
				return list;
			}
		
			AbstractNode winner = null;
			final TFloatArrayList upward = new TFloatArrayList();
			final TFloatArrayList down = new TFloatArrayList();
			final TFloatArrayList left = new TFloatArrayList();
			final TFloatArrayList right = new TFloatArrayList();

			fillList(upward, down, left, right, node);
			

			final float choiceValue = getbestChildValue(upward, down, left, right);

			for (int i = 0; i < node.getChild().size() && winner == null; i++) {
				final AbstractNode childNode = node.getChild().get(i);
				if (childNode.getScoreRatio() == choiceValue && childNode.exp) {
					winner = childNode;
				}

			}
			final List<AbstractNode> list = (winner == null) ? new ArrayList<>() : getBestPath(winner);
			list.add(node);
			return list;
		
	}
	
	private void fillList(final TFloatArrayList upward,final TFloatArrayList down,final TFloatArrayList left,final TFloatArrayList right, AbstractNode node) {
		final int head = node.getSnakes().get(0).getHead();

		for (int i = 0; i < node.getChild().size(); i++) {
			if (node.getChild().get(i).exp) {
				final int move = node.getChild().get(i).getSnakes().get(0).getHead();

				if (move / 1000 < head / 1000) {
					left.add(node.getChild().get(i).getScoreRatio());
				} else if (move / 1000 > head / 1000) {
					right.add(node.getChild().get(i).getScoreRatio());
				} else if (move % 1000 < head % 1000) {
					down.add(node.getChild().get(i).getScoreRatio());
				} else {
					upward.add(node.getChild().get(i).getScoreRatio());
				}
			}
		}
		
	}
	
	protected float getbestChildValue(final TFloatArrayList upward, final TFloatArrayList down,
			final TFloatArrayList left, final TFloatArrayList right) {
		float temp;
		float choiceValue = Float.MIN_VALUE;
		if (!upward.isEmpty()) {
			choiceValue = upward.min();

		}

		if (!down.isEmpty()) {
			temp = down.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}

		if (!left.isEmpty()) {
			temp = left.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}

		if (!right.isEmpty()) {
			temp = right.min();
			if (temp > choiceValue) {
				choiceValue = temp;
			}
		}
		return choiceValue;
	}
	
	public AbstractNode getBestChild(AbstractNode node) {
		// double score =-200;
		if (node.getChild().isEmpty()) {
			return node;
		}
		node.updateScore();
		AbstractNode winner = null;
		final TFloatArrayList upward = new TFloatArrayList();
		final TFloatArrayList down = new TFloatArrayList();
		final TFloatArrayList left = new TFloatArrayList();
		final TFloatArrayList right = new TFloatArrayList();
		fillList(upward, down, left, right, node);

		final float choiceValue = getbestChildValue(upward, down, left, right);

		for (int i = 0; i < node.getChild().size() && winner == null; i++) {
			final AbstractNode childNode = node.getChild().get(i);
			if (childNode.getScoreRatio() == choiceValue && childNode.getSnakes().get(0).isAlive()) {
				winner = childNode;
			}

		}
		if (winner == null) {
			node.exp = false;
			return node;
			
		}

		return getBestChild(winner);
	}
	
	


}
