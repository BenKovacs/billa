package billabong.ai;

import billabong.model.BoardSquare;


public class LegalChecker {
	
	
	public static Boolean checkLegal(BoardSquare[][] boardCopy, int ox, int oy, int dx, int dy)
	{
		//if(dx>13 || dy > 15) return false;
		int deltaX = dx-ox;
		int deltaY = dy-oy;
		BoardSquare dest = boardCopy[dx][dy];
		
		if((Math.abs(deltaX) == Math.abs(deltaY) || deltaX == 0 || deltaY == 0) && !(dest.isOccupied() || dest.isWater()))
		{
			
			//System.out.println("delta = diagonal or 0");
			
			if(Math.abs(deltaX) <=1 && Math.abs(deltaY) <=1)
			{
				return true;
			}
		
			else  
			{   
				//BoardSquare[][] boardCopy = Main.getState().getLoop().getBoard().getBoardArray();
				
				int midBoardSquare_x = ox + ((dx-ox)/2);
				int midBoardSquare_y = oy + ((dy-oy)/2);
			
				BoardSquare midBoardSquare = boardCopy[midBoardSquare_x][midBoardSquare_y];
				//System.out.println("MidBoardSquare found");
				if(midBoardSquare.isOccupied() && onlyOne(boardCopy, ox, oy, dx, dy))
				{
					return true;
				}
				
			}
			
			return false;
		}
		
		else return false;
	}
	
	public static boolean onlyOne(BoardSquare[][] boardCopy, int ox, int oy, int dx, int dy)
	{	//System.out.println("Only One Tried");
		//current x&y
		
		int deltaX = dx - ox;
		int deltaY = dy - oy;
		
		
		int cx = ox;
		int cy = oy;
		int middleCounter = 0;
		
		if(deltaX%2==0 && deltaY%2==0)
		{
		
		//if diagonal
		if(Math.abs(deltaX) == Math.abs(deltaY))
		{
			
			
			if(deltaX<0 && deltaY>0)
			{
				while(cx!=dx && cy != dy )
				{
					cx--;
					cy++;
					
					if(boardCopy[cx][cy].isOccupied())
					{	
						middleCounter++;
					}
				}
			}
			
			if(deltaX>0 && deltaY<0)
			{
				while(cx!=dx && cy != dy )
				{
					cx++;
					cy--;
					
					if(boardCopy[cy][cx].isOccupied())
					{	
						middleCounter++;
					}
				}
			}
			
			if(deltaX>0 && deltaY>0)
			{
				while(cx!=dx && cy != dy )
				{
					cx++;
					cy++;
				
					if(boardCopy[cy][cx].isOccupied())
					{	
						middleCounter++;
					}
				}
			}
			
			if(deltaX<0 && deltaY<0)
			{
				while(cx!=dx && cy != dy )
				{
					cx--;
					cy--;
					
					if(boardCopy[cy][cx].isOccupied())
					{	
						middleCounter++;
					}
				}
			}
			
			
			if(middleCounter == 1){
				//System.out.println("only one tried successful");
				return true;
				
			}
			
			else {
				//System.out.println("only one tried fail" + middleCounter);
				return false;
			}
			
		}
		
		//if not diagonal
		else
		{
			if(deltaX == 0 && deltaY > 0)
			{
				while(cy!=dy)
				{
					cy++;
					
					if(boardCopy[cx][cy].isOccupied())
					{	
						middleCounter++;
					}
				}
			}
		
			if(deltaX == 0 && deltaY < 0)
			{
				while(cy!=dy)
				{
					cy--;
				
					if(boardCopy[cy][cx].isOccupied())
					{	
						middleCounter++;
					}
				}
			}
		
			if(deltaX > 0 && deltaY == 0)
			{
				while(cx!=dx)
				{
					cx++;
					
					if(boardCopy[cy][cx].isOccupied())
					{	
						
						middleCounter++;
					}
				}
			}
		
			if(deltaX < 0 && deltaY == 0)
			{
				while(cx!=dx)
				{
					cx--;
					
					if(boardCopy[cx][cy].isOccupied()&& !boardCopy[cx][cy].isWater())
					{	
						middleCounter++;
					}
				}
			}
			
			if(middleCounter == 1) return true;
			
			else return false;
			
		}
		
		}
		return false; 
	}
}