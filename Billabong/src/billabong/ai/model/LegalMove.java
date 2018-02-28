package billabong.ai.model;

import java.awt.Point;

import billabong.model.Kangaroo;

public class LegalMove{
	public LegalMove(int fx, int fy, int tx, int ty, Kangaroo kanga) {
		from = new Point(fx,fy);
		to = new Point(tx,ty);
		kangaroo = kanga;
	}
	public Kangaroo kangaroo;
	public Point from;
	public Point to;
	public boolean jump;
	@Override
	public String toString() {
		return kangaroo.getTeam().getTeamId()+"-"+kangaroo.getId()+": "+from.x+","+from.y+"->"+to.x+","+to.y+" jump:"+ jump;
	}
}
