package billabong.mcts;

import java.util.List;

import billabong.model.GameBoard;

public class MonteCarloTreeSearch {

    private static final int WIN_SCORE = 10;
    private int level;
    private int oponent;
    private int PN;

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

        oponent = getNextPlayer(playerNo);
        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);
        rootNode.getState().setPlayerNo(oponent);

        while (System.currentTimeMillis() < end) {
            // Phase 1 - Selection
            Node promisingNode = selectPromisingNode(rootNode);
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
            newNode.getState().setPlayerNo(node.getState().getOpponent());
            node.getChildArray().add(newNode);
        });
    }

    private void backPropogation(Node nodeToExplore, int playerNo) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
        	System.out.println("looping bitch");
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getPlayerNo() == playerNo)
                tempNode.getState().addScore(WIN_SCORE);
            /*String x = " " ;
            for(int i = 0; i < tempNode.getState().getBoard().getKangaroos().size(); i++){
    			x = x + " winnerNode check yooo  " + tempNode.getState().getBoard().getKangaroos().get(i).getX() + " x and y " + tempNode.getState().getBoard().getKangaroos().get(i).getY()  + "of the " + i + "th kanga";
    			System.lineSeparator();
    		}
            System.out.println(x);*/
            
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
            boardStatus = tempState.getBoard().checkStatus();

            //cntr++;
            //System.out.println(cntr + "iterations");

        }
        System.out.println("finished this shit");
        return boardStatus;
    }
    
    public int getNextPlayer(int p) {
   		if (p+1 <= PN) {
    		return p+1;
    	}
    	return 1;
    }

}