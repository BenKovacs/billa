package billabong.mcts;

import java.util.List;

import billabong.model.GameBoard;

public class MonteCarloTreeSearch {

    private static final int WIN_SCORE = 10;
    private int level;
    private int oponent;
    private int PN;
    boolean DEBUG1 = true;
    boolean DEBUG2 = true;
    boolean DEBUG3 = true;
    boolean DEBUG4 = true;

    public MonteCarloTreeSearch(int PlayerNumber) {
        this.level = 3;
        PN = PlayerNumber;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    private int getMillisForCurrentLevel() {
        return 2 * (this.level - 1) + 1;
    }

    public GameBoard findNextMove(GameBoard board, int playerNo) {
        long start = System.currentTimeMillis();
        long end = start + 60 * getMillisForCurrentLevel();

        oponent = getNextPlayer(playerNo+1);
        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);
        rootNode.getState().setPlayerNo(oponent);

        while (System.currentTimeMillis() < end) {
            // Phase 1 - Selection
            Node promisingNode = selectPromisingNode(rootNode);
            ///////////////////////////////////
            if (DEBUG1) {
            String x = " " ;
            for(int i = 0; i < promisingNode.getState().getBoard().getKangaroos().size(); i++){
    			x = x + " Phase 1 Selection  " + promisingNode.getState().getBoard().getKangaroos().get(i).getX() + " x and y " + promisingNode.getState().getBoard().getKangaroos().get(i).getY()  + "of the " + i + "th kanga";
    			x = x + " current player is " + promisingNode.getState().getPlayerNo();
    			System.lineSeparator();
    		}
            System.out.println(x);
            }
            ///////////////////////////////////
            // Phase 2 - Expansion
            if (promisingNode.getState().getBoard().isOver() == false)
                expandNode(promisingNode);

            // Phase 3 - Simulation
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            // Phase 4 - Update            
            backPropogation(nodeToExplore, playoutResult);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        /*String x = " " ;
        for(int i = 0; i < winnerNode.getState().getBoard().getKangaroos().size(); i++){
			x = x + " winnerNode check yooo  " + winnerNode.getState().getBoard().getKangaroos().get(i).getX() + " x and y " + winnerNode.getState().getBoard().getKangaroos().get(i).getY()  + "of the " + i + "th kanga";
			System.lineSeparator();
		}
        System.out.println(x);*/
        return winnerNode.getState().getBoard();
    }

    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node.getChildArray().size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    private void expandNode(Node node) {
        List<State> possibleStates = node.getState().getAllPossibleStates();
        possibleStates.forEach(state -> {
            Node newNode = new Node(state);
            newNode.setParent(node);
            ////////////////////////////////////
            if (DEBUG2) {
            String x = "";
            for(int i = 0; i < newNode.getState().getBoard().getKangaroos().size(); i++){
    			x = x + " Phase 2 Expansion  " + newNode.getState().getBoard().getKangaroos().get(i).getX() + " x and y " + newNode.getState().getBoard().getKangaroos().get(i).getY()  + "of the " + i + "th kanga";
    			x = x + " current player is " + newNode.getState().getPlayerNo();
    			System.lineSeparator();
    		}
            System.out.println(x);
            }
            ////////////////////////////////////
            newNode.getState().setPlayerNo(node.getState().getOpponent());
            node.getChildArray().add(newNode);
            /*String x = " " ;
            for(int i = 0; i < newNode.getState().getBoard().getKangaroos().size(); i++){
    			x = x + " winnerNode check yooo  " + newNode.getState().getBoard().getKangaroos().get(i).getX() + " x and y " + newNode.getState().getBoard().getKangaroos().get(i).getY()  + "of the " + i + "th kanga";
    			System.lineSeparator();
    		}
            System.out.println(x);*/
        });
    }

    private void backPropogation(Node nodeToExplore, int playerNo) {
        Node tempNode = nodeToExplore;
        
        while (tempNode != null) {
        	//System.out.println("looping bitch");
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getPlayerNo() == playerNo)
                tempNode.getState().addScore(WIN_SCORE);
            tempNode = tempNode.getParent();    
        }      
    }

    private int simulateRandomPlayout(Node node) {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
        int boardStatus = tempState.getBoard().checkStatus();
        if (boardStatus == oponent) {
            System.out.println("boardstatus = oponent");
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return boardStatus;
        }
        //int cntr = 0;
        while (tempState.getBoard().isOver() == false) {
            //System.out.println("isover = false");
            tempState.togglePlayer();
            tempState.randomPlay();
            ///////////////////////////////////
            if (DEBUG3) {
            String x = " " ;
            for(int i = 0; i < tempState.getBoard().getKangaroos().size(); i++){
    			x = x + " Phase 3 Simulation  " + tempState.getBoard().getKangaroos().get(i).getX() + " x and y " + tempState.getBoard().getKangaroos().get(i).getY()  + "of the " + i + "th kanga";
    			x = x + " current player is " + tempState.getPlayerNo();
    			System.lineSeparator();
    		}
            System.out.println(x);
            }
            ///////////////////////////////////
            boardStatus = tempState.getBoard().checkStatus();
            /*String x = " " ;
            for(int i = 0; i < tempState.getBoard().getKangaroos().size(); i++){
    			x = x + " winnerNode check yooo  " + tempState.getBoard().getKangaroos().get(i).getX() + " x and y " + tempState.getBoard().getKangaroos().get(i).getY()  + "of the " + i + "th kanga";
    			System.lineSeparator();
    		}
            System.out.println(x);
*/
            //cntr++;
            //System.out.println(cntr + "iterations");

        }
        System.out.println("finished 3rd phase");
        return boardStatus;
    }
    
    public int getNextPlayer(int p) {
   		if (p+1 <= PN) {
    		return p+1;
    	}
    	return 1;
    }

}