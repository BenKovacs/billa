package billabong.mcts;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Position;

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
    	if(state == null){
    		System.out.println("fed state = null !!");
    	}
    	if(state.getBoard() == null){
    		System.out.println("getBoard of fed state is null !!");
    	}
        this.board = new GameBoard(state.getBoard());
        this.playerNo = state.getPlayerNo();
        this.visitCount = state.getVisitCount();
        this.winScore = state.getWinScore();
    }

    public State(GameBoard board) {
        this.board = new GameBoard(board);
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
        List<State> possibleStates = new ArrayList<>();
        List<LegalMove> availablePositions = this.board.getEmptyPositions();
        availablePositions.forEach(lm -> {
            State newState = new State(this.board);
            newState.setPlayerNo(3 - this.playerNo);
            newState.getBoard().doMove(lm);
            possibleStates.add(newState);
        });
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
        List<LegalMove> availablePositions = this.board.getEmptyPositions();
        int totalPossibilities = availablePositions.size();
        int selectRandom = (int) (Math.random() * ((totalPossibilities - 1) + 1));
        this.board.doMove(availablePositions.get(selectRandom));
    }

    void togglePlayer() {
        this.playerNo = 3 - this.playerNo;
    }
}