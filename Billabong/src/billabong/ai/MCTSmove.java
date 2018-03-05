package billabong.ai;

import java.util.ArrayList;

import billabong.model.Kangaroo;
import billabong.ai.model.LegalMove;
import billabong.model.BoardSquare;

public class MCTSmove {

	private ArrayList<LegalMove> finalMovesList;
	private BoardSquare[][] board;
	int jumpcounter = 0;
	
	public MCTSmove(BoardSquare[][] board, int team)
	{	
		this.board = board;
		finalMovesList = new ArrayList<LegalMove>();
		int cnt = 0;
		for(int x = 0; x < 16; x++){
			for(int y = 0; y < 14; y++){

				if(board[x][y].isOccupied() && board[x][y].getOccupant().getTeam().getTeamId() == team)
				{	cnt++;
					System.out.println(cnt +" kangaroos analysed");
					finalLegalMoves(board[x][y].getOccupant(), x, y);
				}
				
			}
		}
	}
	
	public boolean unlap(int x, int y, int tx, int ty)
	{
		if (y>7 && ty<7 && tx>10){
			if (tx>7 && x<8){
				return true;
			}
		}
		else if(y>5 && ty>5)
		{
			if(tx>7 && x<8)
			{
				return true;
			}
		}
		return false;
	}
	
	public void finalLegalMoves(Kangaroo current, int i, int j)
	{
		LegalChecker le = new LegalChecker();
		
		for(int x = 0; x < 16; x++){
			for(int y = 0; y < 14; y++){
				if(le.checkLegal(board,i,j,x,y)){
					if(Math.abs(x-i)== 1 || Math.abs(y-j) == 1 )
					{	System.out.println("move added");
						LegalMove t = new LegalMove(i,j,x,y,current);
						finalMovesList.add(t);
					}
					
					else 
					{
						System.out.println("move added");
						if(!unlap(i,j,x,y))
						{
							LegalMove t = new LegalMove(i,j,x,y,current);
							finalMovesList.add(t);
							checkJump(current,i, j, i, j, x, y);
						}
					}
					
					//System.out.println("LegalMove " + y + " " + x + " added to list");
				}
			}
		}	
		System.out.println();
		System.out.println();
	}
	
	
	public void checkJump(Kangaroo current,int a, int b, int i, int j, int k, int l)
	{	System.out.println("Jump");
		LegalChecker le = new LegalChecker();

		
		for(int x = 0; x < 16; x++){
			for(int y = 0; y < 14; y++){
				if(le.checkLegal(board, k, l, x, y)){
					
					if(!(Math.abs(x-k)== 1 || Math.abs(y-l) == 1) && !(x==i && y==j) && !(x==k && y==l) && !(x==a && y==b))
					{	
					
						if(finalMovesList.size()<500)
						{	System.out.println("move added");
							LegalMove t = new LegalMove(a,b,x,y,current);
							finalMovesList.add(t);
							checkJump(current,a,b, k, l, x, y);
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
