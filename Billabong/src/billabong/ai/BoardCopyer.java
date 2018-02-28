package billabong.ai;

import billabong.model.Kangaroo;
import billabong.model.BoardSquare;

public class BoardCopyer {

	public BoardSquare[][] copy(BoardSquare[][] b)
	{
		BoardSquare[][] temp = new BoardSquare[16][14];
		for(int i = 0; i < 16; i++)
		{
			for(int j = 0; j < 14; j++)
			{
				temp[i][j] = new BoardSquare(i,j, 0);
			}
		}	
		
		for(int i = 6; i < 10; i++)
		{
			for(int j = 6; j < 8; j++)
			{
				temp[i][j].setWater(true);
			}
		}
		
		for(int i = 0; i < 16; i++)
		{
			for(int j = 0; j < 14; j++)
			{
				if(b[i][j].isOccupied() && !b[i][j].isWater())
				{
					Kangaroo tk = b[i][j].getOccupant();
					Kangaroo cop = tk.clone(tk.getTeam());
					
					temp[i][j].setOccupant(cop);
				}
			}
		}
		
		return temp;
	}
	
	public Kangaroo kangaCopy(Kangaroo o)
	{
		Kangaroo te = o.clone(o.getTeam());
		
		return te;
	}
}