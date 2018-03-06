package billabong.mcts;

import java.util.List;

import billabong.ai.model.LegalMove;
import billabong.model.GameBoard;

public class MonteCarloTreeSearch {

    private static final int WIN_SCORE = 10;
    private int level;
    private int oponent;
    private int PN;
    boolean DEBUG1 = false;
    boolean DEBUG2 = false;
    boolean DEBUG3 = false;
    boolean DEBUG4 = false;
    
    public MonteCarloTreeSearch(){
    	level = 3 ;
    }

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

    public LegalMove findNextMove(GameBoard board, int playerNo) {
        long start = System.currentTimeMillis();
        long end = start + 60 * getMillisForCurrentLevel();
        playerNo++;
        oponent = getNextPlayer(playerNo);
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
        return winnerNode.getState().getLm();
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
    			x = x + " current player is " + newNode.getState().getPlayerNo() +  System.lineSeparator() ;
    		}
            System.out.println(x);
            }
            ////////////////////////////////////
            newNode.getState().setPlayerNo(node.getState().getOpponent());
            newNode.setLm(newNode.getState().getLm());
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
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getPlayerNo() == playerNo)
                tempNode.getState().addScore(WIN_SCORE);
            ////////////////////////////////////
            if (DEBUG4) {
            String x = "";
            for(int i = 0; i < tempNode.getState().getBoard().getKangaroos().size(); i++){
    			x = x + " Phase 4 Backpropagation  " + tempNode.getState().getBoard().getKangaroos().get(i).getX() + " x and y " + tempNode.getState().getBoard().getKangaroos().get(i).getY()  + "of the " + i + "th kanga";
    			x = x + " current player is " + tempNode.getState().getPlayerNo();
    			x = x + " amount of kangaroos " + tempNode.getState().getBoard().getKangaroos().size() + System.lineSeparator();
    			

    		}
            //System.out.println(x);
            }
            ////////////////////////////////////
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
            tempState.togglePlayer();
            tempState.randomPlay();
            ///////////////////////////////////
            if (DEBUG3) {
            String x = " " ;
            for(int i = 0; i < tempState.getBoard().getKangaroos().size(); i++){
    			x = x + " Phase 3 Simulation  " + tempState.getBoard().getKangaroos().get(i).getX() + " x and y " + tempState.getBoard().getKangaroos().get(i).getY()  + "of the " + i + "th kanga";
    			x = x + " move made by " + tempState.getPlayerNo();
    			System.lineSeparator();
    		}
            //System.out.println(x);
            }
            ///////////////////////////////////
            boardStatus = tempState.getBoard().checkStatus();
            //cntr++;
            //System.out.println(cntr + "iterations");

        }
        System.out.println("finished 3rd phase");
        return boardStatus;
    }
    
    public int getNextPlayer(int p) {
   		if (p < PN) {
    		//System.out.println("got the player number " + (p+1));
    		return p+1;
    	}
    	//System.out.println("got the player number " + 1);
    	return 1;
    }

}