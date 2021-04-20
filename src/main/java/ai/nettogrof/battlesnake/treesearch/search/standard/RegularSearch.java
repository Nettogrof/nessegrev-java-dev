package ai.nettogrof.battlesnake.treesearch.search.standard;

import ai.nettogrof.battlesnake.treesearch.node.AbstractNode;

public class RegularSearch extends AbstractStandardSearch  {

	

	public RegularSearch() {
		super();
	}

	public RegularSearch(final AbstractNode root, final int width, final int heigth) {
		super();
		this.root = root;
		this.width = width;
		this.heigth = heigth;
	

	}

	public RegularSearch(final AbstractNode root, final int width, final int heigth, final long starttime, final int timeout) {
		super();
		this.root = root;
		this.width = width;
		this.heigth = heigth;
		startTime = starttime;
		this.timeout = timeout;

		
	}

	
	@Override
	public void run() {
		
			for ( int i = 0 ; i < 100 ; i++) {
				generateChild(getSmallestChild(root));
			}
			long currentTime = System.currentTimeMillis() - startTime;
			while (cont && currentTime < timeout && root.exp ) { // && root.getScoreRatio() > 0
				final AbstractNode bestChild = getBestChild(root);
				generateChild(bestChild);
				generateChild(getSmallestChild(bestChild));
				generateChild(getSmallestChild(bestChild));
				generateChild(getSmallestChild(bestChild));
				generateChild(getSmallestChild(bestChild));
				generateChild(getSmallestChild(bestChild));
				generateChild(getSmallestChild(bestChild));
				currentTime = System.currentTimeMillis() - startTime;
				// root.updateScore();
				Thread.yield();

			}
		

	}

	
	

	

	

	

	
	
	

	
}
