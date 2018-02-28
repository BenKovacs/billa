package billabong.model.player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import billabong.model.Kangaroo;

public abstract class Player {
	
	protected static Color cols[] = {Color.white, Color.magenta, Color.yellow, Color.pink};
	
	private List<Kangaroo> kangaroos = new ArrayList<>();
	private int teamId = lastIteamId++;
	private static int lastIteamId = 1;
	private Color color;
	
	public Player() {
	}

	@Override
	public String toString() {
		return ""+teamId;
	}
	
	public List<Kangaroo> getKangaroos() {
		return kangaroos;
	}

	public int getTeamId() {
		return teamId;
	}

	public Color getColor() {
		return color;
	}

	public abstract void move();

	public Player clone(){
		Player clone = new AIPlayer();
		clone.setTeamId(this.getTeamId());
		clone.setColor(this.getColor());
		
		for (Kangaroo k: getKangaroos()){
			k.clone(clone);
		}
		
		return clone;
	}


	protected void setTeamId(int teamId2) {
		this.teamId = teamId2;
	}

	protected void setColor(Color color2) {
		this.color = color2;
	}
	
}
