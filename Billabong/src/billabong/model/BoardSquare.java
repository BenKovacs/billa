package billabong.model;

public class BoardSquare {
	int x;
	int y;
	private boolean water;
	private boolean occupied;
	private Kangaroo occupant;
	private double weight;

	public BoardSquare(int x, int y, double weight) {
		super();
		this.x = x;
		this.y = y;
		this.weight = weight;
	}

	public void setWater(boolean b) {
		this.water = true;
	}

	public boolean isWater() {
		return water;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public Kangaroo getOccupant() {
		return occupant;
	}

	public void setOccupant(Kangaroo occupant) {
		this.occupant = occupant;
		this.occupied = occupant != null;
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		if (occupied){
			return "["+x+","+y+"] "+occupant;
		}else{
			return "["+x+","+y+"] ";
		}
	}

}
