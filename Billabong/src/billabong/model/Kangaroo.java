package billabong.model;

import billabong.model.player.Player;

public class Kangaroo {
	private static int lastId = 1;
	private Player team;
	private int id = lastId++;
	private int y;
	private int x;
	private int lapCounter =0;
	
	/**
	 * Creates a new Kangaroo, initialises its location to x,y and adds it to the player 
	 * @param t
	 * @param x
	 * @param y
	 */
	public Kangaroo(Player t, int x, int y) {
		this.team = t;
		this.x = x;
		this.y = y;
		this.team.getKangaroos().add(this);

	}

	public Player getTeam() {
		return team;
	}

	public int getId() {
		return id;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setId(int id2) {
		this.id = id2;
	}
	
	public Kangaroo clone(Player p){
		Kangaroo k = new Kangaroo(p, this.x, this.y);
		k.id = this.id;
		return k;
	}
	
	@Override
	public String toString() {
		return id+" ["+x+","+y+"]";
	}

	public void incrementLapCounter() {
		this.lapCounter  ++;
	}

	public void decrementLapCounter() {
		this.lapCounter --;
	}

	public int getLapCounter() {
		return lapCounter;
	}
}
