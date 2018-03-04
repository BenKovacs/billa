package billabong.mcts;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Position;

import billabong.ai.MCTSmove;
import billabong.ai.MiniMax;
import billabong.ai.model.LegalMove;
import billabong.model.GameBoard;


public class State {
    private GameBoard board;
    private int playerNo;
    private int visitCount;
    private double winScore;

    public State() {
        board = new GameBoard(16,14);
    }

    public State(State state) {
        //this.board = new GameBoard(state.getBoard());
        this.board = new GameBoard(16,14);
        this.board = board.clone(state.getBoard());
        this.playerNo = state.getPlayerNo();
        this.visitCount = state.getVisitCount();
        this.winScore = state.getWinScore();
    }

    public State(GameBoard b) {
    	//System.out.println("new state with board is created");
    	//System.out.println("board copied from " + b.getHeight());
        this.board = new GameBoard(16,14);
        this.board = board.clone(b);
        //System.out.println("new board " + this.board.getHeight());
        
    }

    GameBoard getBoard() {
        return board;
    }

    void setBoard(GameBoard board) {
        this.board = board;
    }

    int getPlayerNo() {
        return playerNo;
    }

    void setPlayerNo(int playerNo) {
        this.playerNo = playerNo;
    }

    int getOpponent() {
        return 3 - playerNo;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    double getWinScore() {
        return winScore;
    }

    void setWinScore(double winScore) {
        this.winScore = winScore;
    }

    public List<State> getAllPossibleStates() {
    	/*//System.out.println("kkk " + board.getHeight());
        List<State> possibleStates = new ArrayList<>();
        List<LegalMove> availablePositions = board.getEmptyPositions(this.playerNo);
        availablePositions.forEach(lm -> {
        	//System.out.println("this.board " + this.board.getHeight());
            State newState = new State(board);
            //System.out.println("newState " + newState.getBoard().getHeight());
            newState.setPlayerNo(3 - this.playerNo);
            newState.getBoard().move(lm.kangaroo,lm.to.x,lm.to.y);
            possibleStates.add(newState);
        });*/
    	List<State> possibleStates = new ArrayList<State>() ;
    	
    	MCTSmove mctsm = new MCTSmove(board.getBs(), playerNo) ;
    	
    	ArrayList<LegalMove> lmList = new ArrayList<LegalMove>() ;
    	
    	lmList = mctsm.getList() ;
    	
    	for(int i = 0 ; i < lmList.size() ; i++){
    		GameBoard b = new GameBoard(this.getBoard()) ;
    		b.doMove(lmList.get(i)) ;
    		State s = new State(b) ;
    		possibleStates.add(s) ;
    	}
        return possibleStates;
    }

    void incrementVisit() {
        this.visitCount++;
    }

    void addScore(double score) {
        if (this.winScore != Integer.MIN_VALUE)
            this.winScore += score;
    }

    void randomPlay() {
        List<LegalMove> availablePositions = this.board.getEmptyPositions(this.playerNo);
        int totalPossibilities = availablePositions.size();
        int selectRandom = (int) (Math.random() * ((totalPossibilities - 1) + 1));
        this.board.move(availablePositions.get(selectRandom).kangaroo,availablePositions.get(selectRandom).to.x,availablePositions.get(selectRandom).to.y);
        if(lapping(availablePositions.get(selectRandom).from.x, availablePositions.get(selectRandom).from.y, availablePositions.get(selectRandom).to.x,availablePositions.get(selectRandom).to.y)== true){
        	availablePositions.get(selectRandom).kangaroo.incrementLapCounter();
		}
        if(unLapping(availablePositions.get(selectRandom).from.x, availablePositions.get(selectRandom).from.y, availablePositions.get(selectRandom).to.x,availablePositions.get(selectRandom).to.y)== true){
        	availablePositions.get(selectRandom).kangaroo.decrementLapCounter();
		}
        if (availablePositions.get(selectRandom).kangaroo.getLapCounter() > 2) {
			// Done the laps
			// Remove the kanga from the game
			board.removeKangarooFromPlay(availablePositions.get(selectRandom).kangaroo);
			//bp.repaint();
			
		} 
        //System.out.println("Random move positions " + availablePositions.get(selectRandom).from + " " + availablePositions.get(selectRandom).to);
        //System.out.println("kanga lapcounter = " + availablePositions.get(selectRandom).kangaroo.getLapCounter());
    }
    
    public boolean lapping(int x, int y, int tx, int ty) {
		if (y>7 && ty>7){
			if (x>7 && tx<8){
				return true;
			}
		}
		return false;
	}
    
    public boolean unLapping(int x, int y, int tx, int ty) {
		if (y>7 && ty>7){
			if (tx>7 && x<8){
				return true;
			}
		}
		return false;
	}

    void togglePlayer() {
        this.playerNo = 3 - this.playerNo;
        //this.board.setCurrentPlayer(this.playerNo);
        //System.out.println("toogled to player " + playerNo);
    }
}