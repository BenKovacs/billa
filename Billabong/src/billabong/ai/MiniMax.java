package billabong.ai;

import java.util.ArrayList;
import java.util.List;

import billabong.ai.model.LegalMove;
import billabong.ai.model.MiniMaxNode;
import billabong.ai.MCTSmove;
import billabong.gui.ProgressBarPanel;
import billabong.model.BoardSquare;
import billabong.model.GameBoard;
import billabong.model.Kangaroo;
import billabong.model.player.Player;

public class MiniMax {

	private static MiniMax instance = null;
	
	
	//this is a singleton design pattern
	public static MiniMax getInstance() {
		if(instance == null){
			instance = new MiniMax();
		}
		return instance;
	}

	private MiniMax() {
	}
	
	MiniMaxNode root;
	private int nodeCount;
	private int processedCount;
	private Player currentPlayerTurn;

	/**
	 * Resets the MiniMax ready for the player next turn.
	 * @param gameBoard
	 */
	public void reset(GameBoard gameBoard) {
		root = new MiniMaxNode(gameBoard);
		root.getChildren().clear();
	}

	/**
	 * Calculates a depth for the next player
	 * @param nextPlayer
	 * @throws Exception
	 */
	public void calculateMoves(Player nextPlayer) throws Exception {
		this.processedCount = 0;
		processNode(root, nextPlayer);
		processedCount++;
		ProgressBarPanel.getInstance().setProgress(this.nodeCount, this.processedCount);
		updateUtilities();
	}
	
	
	private void updateUtilities() {
		double utility = -999999999;
		for (MiniMaxNode child:root.getChildren()){
			utility = Math.max(utility, getUtility(child));
		}
		root.utility = utility;
	}

	private double getUtility(MiniMaxNode child) {
		if (child.getChildren().isEmpty()){
			return child.utility;
		}else{
			if (child.getChildren().get(0).kangaMoving.getTeam().getTeamId() == this.currentPlayerTurn.getTeamId()){
				double utility = -999999999;
				for (MiniMaxNode grandChild: child.getChildren()){
					utility = Math.max(utility, getUtility(grandChild));
				}
				child.utility = utility;
				return utility;
			}else{
				double utility = 999999999;
				for (MiniMaxNode grandChild: child.getChildren()){
					utility = Math.min(utility, getUtility(grandChild));
				}
				child.utility = utility;
				return utility;
			}
		}
	}


	private void processNode(MiniMaxNode node, Player player) throws Exception {
		if (ProgressBarPanel.getInstance().isCancelPressed()){
			return;
		}
		if (node.getChildren().isEmpty()){
//			for (Kangaroo k: node.getBoard().getKangaroos()){
//				if (k.getTeam().getTeamId()!=player.getTeamId()){
//					continue;
//				}
				// Get the Moves (MiniMaxNodes)
//				List<MiniMaxNode> moves = getMoves(node, k);
				List<MiniMaxNode> moves = getEndMoves(node, player.getTeamId());
				// Add them the node.children
				node.getChildren().addAll(moves);
				// Reset the nodes utility to 0 as it will be recalculated using the minmax stuff
				node.utility = 0;
				
			}
//	}
	else{
			for (MiniMaxNode child: node.getChildren()){
				processNode(child, player);
			}
		}
		processedCount++;
		ProgressBarPanel.getInstance().setProgress(this.nodeCount, this.processedCount);
	}


	private List<MiniMaxNode> getMoves(MiniMaxNode node, Kangaroo k) throws Exception {
		List<MiniMaxNode> moves = new ArrayList<>();

		// Basic walk moves
		tryMove(moves, node.getBoard(), k, -1, -1, node.cumulativeScore);
		tryMove(moves, node.getBoard(), k, 0, -1, node.cumulativeScore);
		tryMove(moves, node.getBoard(), k, +1, -1, node.cumulativeScore);

		tryMove(moves, node.getBoard(), k, -1, 0, node.cumulativeScore);
		tryMove(moves, node.getBoard(), k, +1, 0, node.cumulativeScore);

		tryMove(moves, node.getBoard(), k, -1, +1, node.cumulativeScore);
		tryMove(moves, node.getBoard(), k, 0, +1, node.cumulativeScore);
		tryMove(moves, node.getBoard(), k, +1, +1, node.cumulativeScore);
		
		// Jump moves
		// TODO Implement > 1 jump moves at a time. Only 1 jump move is currently calculated and added to the list of Moves.
		for (Kangaroo other: node.getBoard().getKangaroos()){
			// Ignore if it is the same one
			if (other == k){
				continue;
			}
			
			// Get the delta x and y
			int dx = other.getX() - k.getX();
			int dy = other.getY() - k.getY();
			
			// Diagonal jumps are dx == dy
			// Vertical jumps are dx == 0 
			// horizontal jumps are dy == 0
			if (Math.abs(dx) == Math.abs(dy) || dx == 0  || dy == 0){
				if (
						clearBetween(node.getBoard(), k.getX(), k.getY(), other.getX()+dx, other.getY()+dy)
					){
					MiniMaxNode move = getMove(node.getBoard(), k, other.getX()+dx, other.getY()+dy, node.utility);
					move.move.jump = true;
					moves.add(move);
				}
			}
			// All else are illegal jump moves
		}
		return moves;
	}
	
	private List<MiniMaxNode> getEndMoves(MiniMaxNode node, int team){
		List<MiniMaxNode> moves = new ArrayList<>();
		MCTSmove endstate = new MCTSmove(node.getBoard().getboard(), team);
		List<LegalMove> allMoves = endstate.getList();
		for(int i = 0; i< allMoves.size(); i++)
		{
			LegalMove tempMove = allMoves.get(i);
			MiniMaxNode tempNode = new MiniMaxNode(node.getBoard());
			tempNode.originX = tempMove.from.x;
			tempNode.originY = tempMove.from.y;
			tempNode.move = tempMove;
			tempNode.kangaMoving = tempMove.kangaroo;
			if(Math.abs(tempMove.from.x - tempMove.to.x) > 1 || Math.abs(tempMove.from.y - tempMove.to.y) > 1)
			{
				tempNode.move.jump = true;
			}	
			int dx = tempMove.to.x - tempMove.from.x;
			int dy = tempMove.to.y - tempMove.from.y;
			//moves.add(tempNode);
			try {
				
				tryMove(moves, node.getBoard(), tempMove.kangaroo, dx, dy, node.cumulativeScore);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//moves.add(tempNode);
		}
		
		return moves;
	}


	/**
	 * checks every square between fx and tx and counts how many are occupied.<br>
	 * If only 2 are occupied then return true (the fx and fy will be the kangaroo jumping) and midway to the tx ty should be the kangaroo being jumped.<br>
	 * The method will return false if the square being jumped over is water.
	 * @param board
	 * @param fx
	 * @param fy
	 * @param tx
	 * @param ty
	 * @return true if a square is occupied false if not occupied
	 */
	private boolean clearBetween(GameBoard board, int fx, int fy, int tx, int ty) {
		try{
			if (board.getBoardSquare(tx, ty).isOccupied()){
				return false;
			}
			if (board.getBoardSquare(tx, ty).isWater()){
				return false;
			}
		}catch(Exception e){
			return false;
		}
		try{
			int count = 0;
			int dx = tx-fx;
			int dy = ty-fy;
			
			// Diagonals
			if (Math.abs(dx) == Math.abs(dy)){
				// Down and right
				if (dx>0 & dy>0){
					int dist = Math.abs(dx);
					for (int i=0; i<dist; i++){
						if (board.getBoardSquare(fx+i, fy+i).isWater()){
							return false;
						}
						if (board.getBoardSquare(fx+i, fy+i).isOccupied()){
							count ++;
						}
					}
				}
				// Up and Right
				if (dx>0 & dy<0){
					int dist = Math.abs(dx);
					for (int i=0; i<dist; i++){
						if (board.getBoardSquare(fx+i, fy-i).isWater()){
							return false;
						}
						if (board.getBoardSquare(fx+i, fy-i).isOccupied()){
							count ++;
						}
					}
				}
				// Down and left
				if (dx<0 & dy>0){
					int dist = Math.abs(dx);
					for (int i=0; i<dist; i++){
						if (board.getBoardSquare(fx-i, fy+i).isWater()){
							return false;
						}
						if (board.getBoardSquare(fx-i, fy+i).isOccupied()){
							count ++;
						}
					}
				}
				// Up and Left
				if (dx<0 & dy<0){
					int dist = Math.abs(dx);
					for (int i=0; i<dist; i++){
						if (board.getBoardSquare(fx-i, fy-i).isWater()){
							return false;
						}
						if (board.getBoardSquare(fx-i, fy-i).isOccupied()){
							count ++;
						}
					}
				}
				
			}else{
				// Vertical
				if (dx == 0){
					int dist = Math.abs(ty-fy);
					if (ty>fy){
						for (int i=0; i<=dist; i++){
							if (board.getBoardSquare(fx, fy+i).isWater()){
								return false;
							}
							if (board.getBoardSquare(fx, fy+i).isOccupied()){
								count ++;
							}
						}
					}else{
						for (int i=0; i<=dist; i++){
							if (board.getBoardSquare(fx, fy+-i).isWater()){
								return false;
							}
							if (board.getBoardSquare(tx, fy-i).isOccupied()){
								count ++;
							}
						}
					}
				}
				// Horizontal
				if (dy == 0){
					int dist = Math.abs(tx-fx);
					if (tx>fx){
						for (int i=0; i<=dist; i++){
							if (board.getBoardSquare(fx+i, fy).isWater()){
								return false;
							}
							if (board.getBoardSquare(fx+i, fy).isOccupied()){
								count ++;
							}
						}
					}else{
						for (int i=0; i<=dist; i++){
							if (board.getBoardSquare(fx-i, fy).isWater()){
								return false;
							}
							if (board.getBoardSquare(fx-i, fy).isOccupied()){
								count ++;
							}
						}
					}
				}
			}
			
			return count == 2;
		}catch(IndexOutOfBoundsException e){
			// FIXME Lazy coding ...If the x,y being checked is off the board then catch the index out of bounds and return false;
			return false;
		}
	}

	private void tryMove(List<MiniMaxNode> moves, GameBoard board, Kangaroo k, int x, int y, double cumulativeScore) throws Exception {
		if (true){
			MiniMaxNode move = getMove(board, k, k.getX()+x, k.getY()+y, cumulativeScore);
			moves.add(move);
		}
	}

	private MiniMaxNode getMove(GameBoard board, Kangaroo k, int tx, int ty, double cumulativeScore) throws Exception {

		GameBoard cBoard = (GameBoard) board.clone();

		MiniMaxNode move = new MiniMaxNode(cBoard);
		LegalMove lm = new LegalMove(k.getX(), k.getY(), tx, ty, k);
		
		// Need to find the kanga in the cloned board and update him/ her
		Kangaroo ckToMove = null;
		for (Kangaroo ck: cBoard.getKangaroos()){
			if (ck.getId() == k.getId()){
				ckToMove = ck;
				break;
			}
		}
		if(ckToMove == null){
			throw new Exception("Cloned kanga not found in cloned board - WTF!");
		}else{
			cBoard.move(ckToMove, tx,ty);
		}
		move.kangaMoving = ckToMove;
		move.move = lm;
		move.score = Diffuser.getInstance().weights[tx][ty]-Diffuser.getInstance().weights[k.getX()][k.getY()];
		if (lapping(k.getX(), k.getY(), tx, ty)){
			move.score += 360;
		}
		if (unLapping(k.getX(), k.getY(), tx, ty)){
			move.score -= 99999999;
		}
		if (k.getTeam().getTeamId() != this.currentPlayerTurn.getTeamId()){
			move.score = - 0.6*move.score;
		}
		move.cumulativeScore = cumulativeScore + move.score;
		move.utility = move.cumulativeScore;
		return move;
	}

	public boolean unLapping(int x, int y, int tx, int ty) {
		if (y>7 && ty>7){
			if (tx>7 && x<8){
				return true;
			}
		}
		return false;
	}

	public boolean lapping(int x, int y, int tx, int ty) {
		if (y>7 && ty>7){
			if (x>7 && tx<8){
				return true;
			}
		}
		return false;
	}

	private boolean legalMove(GameBoard board, Kangaroo k, int x, int y) {
		try{
			BoardSquare bs = board.getBoardSquare(k.getX()+x, k.getY()+y);
			if (bs.isOccupied() || bs.isWater()){
				return false;
			}else{
				return true;
			}
		}catch(IndexOutOfBoundsException e){
			return false; 
		}
	}

	public void initialise(GameBoard gb) {
		root = new MiniMaxNode(gb);
	}

	public MiniMaxNode getRoot() {
		return root;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public void setCurrentPlayerTurn(Player player) {
		this.currentPlayerTurn = player;
	}

	/**
	 * Finds the best move. This will be the child with the utility = root utility.<br>
	 * It is possible in games with a large number of kangaroos that more than 1 move has the best utility. In such tied cases the method will return 
	 * the best scoring one. 
	 * @return
	 */
	public MiniMaxNode getBestMove() {
		MiniMaxNode best = null;
		double bestScore = -999999;
		
		for (MiniMaxNode child: root.getChildren()){
			if (child.utility == root.utility){
				if (child.score > bestScore){
					bestScore = child.score;
					best = child;
				}
			}
		}
		return best;
	}

}
