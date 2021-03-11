package ai.nettogrof.battlesnake.proofnumber;



import com.fasterxml.jackson.databind.JsonNode;

import gnu.trove.list.array.TIntArrayList;

public class SnakeInfo implements Cloneable {
	
	
	private final transient TIntArrayList snakebody ;
	
	private String name;
	private int health;
	public transient boolean eat = Boolean.FALSE;
	private transient boolean alive = true;
	private String squad = "";
	
	public SnakeInfo() {
		snakebody = new TIntArrayList();
	}

		
	public SnakeInfo(final SnakeInfo sn,final int m,final boolean eat,final boolean hazard) {
		if (eat) {
			health = 100;
			this.eat = true;
		} else {
			health = sn.getHealth() - 1;
		}

		snakebody = new TIntArrayList(sn.getSnakeBody());
		snakebody.insert(0,m);
		if (!eat) {
						
			snakebody.removeAt(snakebody.size()-1);
		}
		
		
		if (hazard) {
			health-=15;
		}
		if (health <= 0) {
			alive =false;
		}
		name = sn.getName();
		
		squad = sn.getSquad();
	}
	
	public SnakeInfo(final SnakeInfo sn,final int m,final boolean eat) {
		if (eat) {
			health = 100;
			this.eat = true;
		} else {
			health = sn.getHealth() - 1;
		}

		snakebody = new TIntArrayList(sn.getSnakeBody());
		snakebody.insert(0,m);
		if (!eat) {
						
			snakebody.removeAt(snakebody.size()-1);
		}
		
		if (health <= 0) {
			alive =false;
		}
		name = sn.getName();
		
		squad = sn.getSquad();
	}

	public TIntArrayList getSnakeBody() {

		return snakebody;

	}

	public void die() {

		alive = false;
	}

	public void setSnake(final JsonNode snakeI) {
		
		

		for (int x = 0; x < snakeI.get("body").size(); x++) {
			snakebody.add(snakeI.get("body").get(x).get("x").asInt()*1000+ snakeI.get("body").get(x).get("y").asInt());

		}

	}

	
	
	public boolean isSnake( final int pos, String squad) {
		if (!squad.equals("")) {
			if(squad.equals(this.squad)) {
				return false;
			}
		}
		
		if (eat) {
			return snakebody.contains(pos);
		}else {
			if ( snakebody.contains(pos)) {
				return snakebody.indexOf(pos) < snakebody.size() -1;
			}else {
				return false;
			}
			  
		}
		
		
		
	}
	
	public boolean isSnake( final int pos) {
		
		
		if (eat) {
			return snakebody.contains(pos);
		}else {
			if ( snakebody.contains(pos)) {
				return snakebody.indexOf(pos) < snakebody.size() -1;
			}else {
				return false;
			}
			  
		}
		
		
		
	}

	public int getHead() {

		return snakebody.get(0);

	}

	public int getTail() {
		return snakebody.get(snakebody.size()-1);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth( final int health) {
		if (health == 100) {
			eat = true;
		}
		this.health = health;
	}

	public SnakeInfo cloneSnake() {
		SnakeInfo cl = null ;
		try {
			cl = clone();
		} catch (CloneNotSupportedException e) {
		
			e.printStackTrace();
			
		}
		return cl;

	}

	@Override
	public SnakeInfo clone() throws CloneNotSupportedException {
		return (SnakeInfo) super.clone();
	}

	public boolean isAlive() {
		return alive;
	}

	/**
	 * @return the squad
	 */
	public String getSquad() {
		return squad;
	}

	/**
	 * @param squad the squad to set
	 */
	public void setSquad(String squad) {
		this.squad = squad;
	}

	public boolean equals(SnakeInfo b) {
		if (!name.equals(b.name)) {
			return false;
		}
		if (health != b.getHealth()) {
			return false;
		}
		
		if (snakebody.size() != b.getSnakeBody().size()) {
			return false;
		}
		if (getHead() != b.getHead()) {
			return false;
		}
		if (getTail() != b.getTail()) {
			return false;
		}
		
		return true;
	}
	

	/*
	 * public void setAlive(boolean alive) { this.alive = alive; }
	 */
}
