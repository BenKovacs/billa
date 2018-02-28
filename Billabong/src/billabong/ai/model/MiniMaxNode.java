package billabong.ai.model;

import java.util.ArrayList;
import java.util.List;

import billabong.model.GameBoard;
import billabong.model.Kangaroo;

public class MiniMaxNode{

		public int originY;
		public int originX;
		public Kangaroo kangaMoving;
		public MiniMaxNode parent;
		public LegalMove move = null;
		public double score;
		public double cumulativeScore;
		public double utility = 0;
//		int level;
		
		private GameBoard board;
		private List<MiniMaxNode> children = new ArrayList<>();

		public MiniMaxNode(GameBoard gameBoard) {
			this.setBoard(gameBoard);
//			this.level = i;
		}

		@Override
		public String toString() {
			if (move !=null){
				return move+" "+utility;
			}else{
				return "Root "+utility;
			}
		}

		public GameBoard getBoard() {
			return board;
		}

		public void setBoard(GameBoard board) {
			this.board = board;
		}

		public List<MiniMaxNode> getChildren() {
			return children;
		}

		public void setChildren(List<MiniMaxNode> children) {
			this.children = children;
		}
	}
