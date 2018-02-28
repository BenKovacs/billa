package billabong.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import billabong.ai.MiniMax;
import billabong.ai.model.MiniMaxNode;
import billabong.model.GameBoard;
import billabong.model.Kangaroo;
import billabong.model.TurnState;
import billabong.model.player.AIPlayer;
import billabong.model.player.HumanPlayer;
import billabong.model.player.Player;

public class MainApp implements ActionListener {

	private JFrame frame;
	private int numHuman;
	private int numAI;
	private int numKangas;
	private ArrayList<Player> players;
	private GameBoard gb;
	private MiniMaxPanel mp;
	private int currentPlayer = 0;
	private int depth = 0;
	private int startPlayer = 0;
	private int aiDepth;
	private boolean debugMode;
	private JSplitPane splitPane;
	private JPanel panel;
	private JLabel lblStartPlayerCurrent;
	private BoardPanel bp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp window = new MainApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainApp(){
		initialize();
		Settings settings = new Settings();
		// The settings Dialog is Modal so this thread will pause after setting it to be visible until it is set invisible by the dialog.
		settings.setVisible(true);
		if (settings.isCancelSelected()){
			System.exit(0);
		}
		// TODO add default Close Operation.(when we press exit the game opens, not good).
		numHuman = settings.getNumberOfHumanPlayers();
		numAI = settings.getNumberOfAIPlayers();
		numKangas = settings.getNumberOfKangaroos();
		aiDepth = settings.getDepthLevel();
		
		debugMode = settings.isDebugMode();
		
		// TODO Implement getting the board size and setting the board size. This will require some clever use of global variables as 16 and 14 are used everywhere
		// TODO Perhaps initialise the grid to a size and then everywhere should be changed to loop based on the length of the array and not hardcoded size 
		
		initializeBoard();
		
	}

	private void initializeBoard() {
		gb = new GameBoard(16,14);
		createPlayers();

		frame.getContentPane().setLayout(new BorderLayout());
		
		splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		bp = new BoardPanel(gb);
		splitPane.setLeftComponent(bp);
		
		mp = new MiniMaxPanel(this);
		splitPane.setRightComponent(mp);
		splitPane.setDividerLocation(300);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 30));
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		lblStartPlayerCurrent = new JLabel("Start Player: Current Player:");
		panel.add(lblStartPlayerCurrent);
		
		mp.setDebugMode(debugMode);

	}

	private void createPlayers() {
		players = new ArrayList<>();
		
		LinkedList<Point> points = new LinkedList<>(); 
		//team 1.
		points.add(new Point(10,7));
		points.add(new Point(15,7));
		points.add(new Point(14,8));
		points.add(new Point(11,10));
		points.add(new Point(10,12));
		//team 2
		points.add(new Point(11,7));
		points.add(new Point(10,8));
		points.add(new Point(15,8));
		points.add(new Point(12,10));
		points.add(new Point(14,12));
		//team 3
		points.add(new Point(12,7));
		points.add(new Point(11,8));
		points.add(new Point(12,9));
		points.add(new Point(13,10));
		points.add(new Point(11,12));
		//team 4
		points.add(new Point(13,7));
		points.add(new Point(12,8));
		points.add(new Point(14,9));
		points.add(new Point(15,10));
		points.add(new Point(11,13));
		

		for (int i=0;i<numHuman; i++){
			Player p = new HumanPlayer();
			players.add(p);
			for (int j=0; j<numKangas; j++){
				Point pt = (Point) points.remove();
				new Kangaroo(p, pt.x, pt.y);
			}
			gb.addPlayer(p);
		}
		for (int i=0;i<numAI; i++){
			Player p = new AIPlayer();
			players.add(p);
			for (int j=0; j<numKangas; j++){
				Point pt = (Point) points.remove();
				new Kangaroo(p, pt.x, pt.y);
			}
			gb.addPlayer(p);
		}

		MiniMax.getInstance().setCurrentPlayerTurn(this.gb.getPlayers().get(currentPlayer));
		MiniMax.getInstance().initialise(this.gb);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				try{
					splitPane.setDividerLocation(frame.getWidth()-400);
				}catch(Exception e){}
			}
		});
		frame.setBounds(100, 100, 927, 473);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		case "turn":
			doExecuteTurn();
			break;
		case "calculate":
			doCalculateMoves();
			break;
		case "move":
			doMove();
			break;
		}
	}

	private void doExecuteTurn() {

		// TODO Remove any referee from the board

		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				MiniMax.getInstance().reset(gb);
				MiniMax.getInstance().setCurrentPlayerTurn(gb.getPlayers().get(currentPlayer));

				long start = Calendar.getInstance().getTimeInMillis();
				for (int i=0; i< aiDepth; i++){
					try {
						ProgressBarPanel.getInstance().initialise();
						Player p = players.get(currentPlayer);
						p.move();
						depth ++;
						currentPlayer ++;
						if (currentPlayer == players.size()){
							currentPlayer = 0;
						}
						lblStartPlayerCurrent.setText("Player Turn: "+startPlayer+" Current Player: "+currentPlayer);
						long end = Calendar.getInstance().getTimeInMillis();
						mp.setState(TurnState.MovesFound, end-start);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				long end = Calendar.getInstance().getTimeInMillis();
				System.out.println("Finished calcing moves in "+(end-start)+" ms");
				mp.setState(TurnState.DepthComplete, end-start);
				
				doMove();
			}
		};
		
		new Thread(r).start();
	}

	private void doCalculateMoves() {
		
		ProgressBarPanel.getInstance().initialise();
		Player p = players.get(currentPlayer);

		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					long start = Calendar.getInstance().getTimeInMillis();
					p.move();
					long end = Calendar.getInstance().getTimeInMillis();
					System.out.println("Finished calcing moves in "+(end-start)+" ms");
					depth ++;
					currentPlayer ++;
					if (currentPlayer == players.size()){
						currentPlayer = 0;
					}
					lblStartPlayerCurrent.setText("Player Turn: "+(startPlayer+1)+" Current Player: "+(currentPlayer+1));
					if (depth == aiDepth){
						mp.setState(TurnState.DepthComplete, end-start);
					}else{
						mp.setState(TurnState.MovesFound, end-start);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(r).start();
	}

	
	private void doMove() {
		
		MiniMaxNode bestMove = MiniMax.getInstance().getBestMove();
		
		Kangaroo k = bestMove.move.kangaroo;
		int tx = bestMove.move.to.x;
		int ty = bestMove.move.to.y;
		
		if(MiniMax.getInstance().lapping(k.getX(), k.getY(), tx, ty)== true){
			k.incrementLapCounter();
		}
		if(MiniMax.getInstance().unLapping(k.getX(), k.getY(), tx, ty)== true){
			k.decrementLapCounter();
		}

		if (k.getLapCounter() >= 2) {
			// Done the laps
			// Remove the kanga from the game
			gb.removeKangarooFromPlay(k);
			bp.repaint();
			return;
		} 
		
		gb.move(k, tx, ty);
		
		// TODO if the bestMove is a jump move then place a referee in the originating square
		bp.repaint();
		
		mp.setState(TurnState.Idle,0);
		startPlayer ++;
		depth = 0;
		if (startPlayer == players.size()){
			startPlayer = 0;
		}
		currentPlayer = startPlayer;
		lblStartPlayerCurrent.setText("Player Turn: "+startPlayer+" Current Player: "+currentPlayer);
		
		MiniMax.getInstance().reset(this.gb);
		MiniMax.getInstance().setCurrentPlayerTurn(this.gb.getPlayers().get(currentPlayer));
	}

	public void showBoard(GameBoard board) {
		bp.showBoard(board);
	}

	public void resetBoard() {
		bp.showBoard(gb);
	}

}
