package billabong.ai;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import billabong.model.Kangaroo;
import billabong.ai.model.LegalMove;
import billabong.model.BoardSquare;


public class MCTS {
	
	public MCTS(BoardSquare[][] boardArray, int team)
	{
		this.boardArray = boardArray;
		this.team = team;
	}
		BoardSquare[][] boardArray;
	    static Random r = new Random();
	    static int nActions = 5;
	    static double epsilon = 1e-6;
	    private int team;

	    MCTS[] children;
	    double nVisits, totValue;

	    public void selectAction() {
	        List<MCTS> visited = new LinkedList<MCTS>();
	        MCTS current = this;
	        visited.add(this);
	        
	        	while (!current.leaf()) {
	        		current = current.select();
	        		visited.add(current);
	        	}
	        
	        current.expand();
	        MCTS newNode = current.select();
	        visited.add(newNode);
	        double value = rollOut(newNode);
	        	for (MCTS node : visited) {
	        		// would need extra logic for n-player game
	        		node.updateStats(value);
	        	}
	    }

	    public void expand() {
	        children = new MCTS[nActions];
	        
	        BoardCopyer bc = new BoardCopyer();
	        BoardSquare[][] newBoard = bc.copy(boardArray);
	        MCTSmove mover = new MCTSmove(newBoard,team);
	        ArrayList<LegalMove> moves = mover.getList();
	        //Do we need to make a thorough copy every time?
	        
	        	for (int i=0; i<nActions; i++) {
	        		int randomIndex = (int)Math.random()*(moves.size()-1);
	        		LegalMove tempMove = moves.get(randomIndex);
	        		int tempOldX = tempMove.from.x;
	        		int tempOldY = tempMove.from.y;
	        		int tempNewX = tempMove.to.x;
	        		int tempNewY = tempMove.to.y;
	        		BoardSquare oldSquare = newBoard[tempOldX][tempOldY];
	        		BoardSquare newSquare = newBoard[tempNewX][tempNewY];
	        		Kangaroo tempKangaroo = oldSquare.getOccupant();
//	        		Square previousPosition = tempKangaroo.getPrevPosition();
	        		tempKangaroo.setX(tempNewX);
	        		tempKangaroo.setY(tempNewY);
	        		
	        		newSquare.setOccupant(tempKangaroo);
	        		oldSquare.setOccupant(null);
	        		System.out.println("MCTS coords are: " + tempOldX + "x "+ tempOldY + "y " + tempNewX + "x " + tempNewY + "y");
	        		children[i] = new MCTS(newBoard, team); //board state needs to be fed
//	        		tempKangaroo.setPosition(oldSquare);
//	        		tempKangaroo.setPrevPosition(previousPosition);
//	        		oldSquare.fill(tempKangaroo);
//	        		newSquare.empty();
	        		moves.remove(randomIndex);
	        	}
	    }

	    private MCTS select() {
	        MCTS selected = null;
	        double bestValue = Double.MIN_VALUE;
		        for (MCTS c : children) {
		            double uctValue = c.totValue / (c.nVisits + epsilon) +
		                       Math.sqrt(Math.log(nVisits+1) / (c.nVisits + epsilon)) +
		                           r.nextDouble() * epsilon;
		            // small random number to break ties randomly in unexpanded nodes
		            if (uctValue > bestValue) {
		                selected = c;
		                bestValue = uctValue;
		            }
		        }
		        
	        return selected;
	    }

	    public boolean leaf() {
	    	int one = 5;
	    	int two = 5;
	    	int three = 5;
	    	int four = 5;
	    	
	    	for(int i = 0; i < 16; i++)
	    	{
	    		for(int j = 0; j < 14; j++)
	    		{
	    			if(boardArray[i][j].isOccupied())
	    			{
	    				if(boardArray[i][j].getOccupant().getTeam().getTeamId() == 1)
	    				{
	    					one--;
	    				}
	    				if(boardArray[i][j].getOccupant().getTeam().getTeamId() == 2)
	    				{
	    					two--;
	    				}
	    				if(boardArray[i][j].getOccupant().getTeam().getTeamId() == 3)
	    				{
	    					three--;
	    				}
	    				if(boardArray[i][j].getOccupant().getTeam().getTeamId() == 4)
	    				{
	    					four--;
	    				}
	    				
	    			}
	    		}
	    	}
	    	
	    	if(one == 0 || two == 0 || three == 0 || four == 0) return true;
	    	else return false;
	    	
	    }

	    public double rollOut(MCTS tn) {
	        // ultimately a roll out will end in some value
	        // assume for now that it ends in a win or a loss
	        // and just return this at random
	    	int one = 5;
	    	int two = 5;
	    	int three = 5;
	    	int four = 5;
	    	
	    	for(int i = 0; i < 16; i++)
	    	{
	    		for(int j = 0; j < 14; j++)
	    		{
	    			if(boardArray[i][j].isOccupied())
	    			{
	    				if(boardArray[i][j].getOccupant().getTeam().getTeamId() == 1)
	    				{
	    					one--;
	    				}
	    				if(boardArray[i][j].getOccupant().getTeam().getTeamId() == 2)
	    				{
	    					two--;
	    				}
	    				if(boardArray[i][j].getOccupant().getTeam().getTeamId() == 3)
	    				{
	    					three--;
	    				}
	    				if(boardArray[i][j].getOccupant().getTeam().getTeamId() == 4)
	    				{
	    					four--;
	    				}
	    				
	    			}
	    		}
	    	}
	    	
	    	if(one == 0 && team == 1) return 1;
	    	if(two == 0 && team == 2) return 1;
	    	if(three == 0 && team == 3) return 1;
	    	if(four == 0 && team == 4) return 1;
	    	else return -1;
	        
	    }

	    public void updateStats(double value) {
	        nVisits++;
	        totValue += value;
	    }

	    public int arity() {
	        return children == null ? 0 : children.length;
	    }
}