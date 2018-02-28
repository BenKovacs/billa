package billabong.ai;

import java.util.ArrayList;

import billabong.model.Kangaroo;
import billabong.ai.model.LegalMove;
import billabong.model.BoardSquare;

public class MCTSmove {

	private ArrayList<LegalMove> finalMovesList;
	private BoardSquare[][] b;
	
	
	public MCTSmove(BoardSquare[][] b, int team)
	{	
		this.b = b;
		finalMovesList = new ArrayList<LegalMove>();
		for(int x = 0; x < 16; x++){
			for(int y = 0; y < 14; y++){

				if(b[x][y].isOccupied() && b[x][y].getOccupant().getTeam().getTeamId() == team)
				{
					finalLegalMoves(b[x][y].getOccupant(), x, y);
				}
				
			}
		}
	}
	
	public void finalLegalMoves(Kangaroo current, int i, int j)
	{
		LegalChecker le = new LegalChecker();
		
		for(int x = 0; x < 16; x++){
			for(int y = 0; y < 14; y++){
				if(le.checkLegal(b,i,j,x,y)){
					if(Math.abs(x-i)== 1 || Math.abs(y-j) == 1 )
					{
						LegalMove t = new LegalMove(i,j,x,y,current);
						finalMovesList.add(t);
					}
					
					else 
					{
						LegalMove t = new LegalMove(i,j,x,y,current);
						finalMovesList.add(t);
						checkJump(current, i, j, x, y);
					}
					
					//System.out.println("LegalMove " + y + " " + x + " added to list");
				}
			}
		}	
		
	}
	
	
	public void checkJump(Kangaroo current, int i, int j, int k, int l)
	{
		LegalChecker le = new LegalChecker();

		
		for(int x = 0; x < 16; x++){
			for(int y = 0; y < 14; y++){
				if(le.checkLegal(b,k, l, x, y)){
					if(!(Math.abs(x-i)== 1 || Math.abs(y-j) == 1) && (x!=i || y!=j))
					{		
						if(finalMovesList.size()<1000)
						{
							LegalMove t = new LegalMove(k,l,x,y,current);
							finalMovesList.add(t);
							checkJump(current, k, l, x, y);
						}
					}
					
					//System.out.println("LegalMove " + y + " " + x + " added to list");
				}
			}
		}	
	}
	
	public LegalMove getLegalMove()
	{
		int select = (int)Math.random()*(finalMovesList.size()-1);
		return finalMovesList.get(select);
	}
	
	public ArrayList<LegalMove> getList()
	{
		return finalMovesList;
	}
}