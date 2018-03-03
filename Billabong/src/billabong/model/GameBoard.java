package billabong.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.Position;


import billabong.ai.Diffuser;
import billabong.ai.LegalChecker;
import billabong.ai.MiniMax;
import billabong.ai.model.LegalMove;
import billabong.model.player.Player;

public class GameBoard {
	int width;
	int height;
	private BoardSquare[][] bs;
	private List<Kangaroo> kangaroos = new ArrayList<>(); // just an ease of access to all the kangaroos for all the players on the gameboard
	private List<Player> players = new ArrayList<>();
	private int tx, ty, tnx, tny = -1;
	
	public BoardSquare[][] getboard(){
		
		return bs ;
	}
	
	public GameBoard(GameBoard g){
		clone(g) ;
	}

	public GameBoard(int width, int height) {
		super();
		this.width = width;
		this.height = height;

		Diffuser diff = Diffuser.getInstance();

		bs = new BoardSquare[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				bs[x][y] = new BoardSquare(x, y, diff.getWeight(x, y));
			}
		}

		bs[6][6].setWater(true);
		bs[7][6].setWater(true);
		bs[8][6].setWater(true);
		bs[9][6].setWater(true);
		bs[6][7].setWater(true);
		bs[7][7].setWater(true);
		bs[8][7].setWater(true);
		bs[9][7].setWater(true);

	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public BoardSquare getBoardSquare(int x, int y) {
		return bs[x][y];
	}

	public BoardSquare[][] getBs() {
		return bs;
	}
	
	public List<LegalMove> getEmptyPositions(){
		
		List<LegalMove> list = new LinkedList<LegalMove>() ;
		Kangaroo current; 
				
		for(int i = 0 ; i < 16; i++){
			for(int j = 0 ; j < 14; j++){
				if(bs[i][j].isOccupied() && bs[i][j].getOccupant().getTeam().getTeamId() == 1 ){ /// need to get current plaer somehow to check 
					current = bs[i][j].getOccupant() ;
					for(int x = 0 ; x < 16; x++){
						for(int y = 0; y < 14; y++){
							if(tx!=-1 && ((tx == i && ty == j && tnx == x && tny == y)||(tnx == i && tny == j && tx == x && ty == y)) )
							{
								System.out.println( "Move is not added to movelist!" );
							}
							 
							else if(LegalChecker.checkLegal(bs, i, j, x, y)){
								LegalMove m = new LegalMove(i, j, x, y, current) ;
								list.add(m) ;
								//System.out.println("move " + y + " " + x + " added to list");
							}
						}
					}
				}
			}
		}
		return list ;
	}

	/**
	 * Adds the kangaroo to the the boardsquare as specified by the kangaroos x
	 * and y.<br>
	 * Add the kangaroo to the list of kangaroos
	 * 
	 * @param k
	 */
	private void addKangaroo(Kangaroo kanga) {
		getBoardSquare(kanga.getX(), kanga.getY()).setOccupant(kanga);
		this.kangaroos.add(kanga);
	}
	
	public List<Kangaroo> getKangaroos() {
		return this.kangaroos;
	}

	public GameBoard clone() {
		GameBoard clone = new GameBoard(getWidth(), getHeight());

		for (Player p : this.players) {
			Player cp = (Player) p.clone();
			clone.addPlayer(cp);
		}

		return clone;
	}
	
	public GameBoard clone(GameBoard bo) {
		GameBoard clone = new GameBoard(bo.getWidth(), bo.getHeight());

		for (Player p : bo.players) {
			Player cp = (Player) p.clone();
			clone.addPlayer(cp);
		}

		return clone;
	}

	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Adds the player to the gameboard, adds the kangaroos to the boardsquares
	 * 
	 * @param p
	 */
	public void addPlayer(Player p) {
		this.players.add(p);
		for (Kangaroo k : p.getKangaroos()) {
			addKangaroo(k);
		}
	}

	public void move(Kangaroo k, int tx, int ty) {

		// Otherwise just move him/ her
		this.bs[k.getX()][k.getY()].setOccupant(null);
		this.bs[tx][ty].setOccupant(k);
		k.setX(tx);
		k.setY(ty);
	}

	public void removeKangarooFromPlay(Kangaroo k) {
		this.bs[k.getX()][k.getY()].setOccupant(null);
		this.kangaroos.remove(k);
		k.getTeam().getKangaroos().remove(k);
	}
	
	public int checkStatus(){
		
		int one = 0;
    	int two = 0;
    	
    	
    	for(int i = 0; i < 16; i++)
    	{
    		for(int j = 0; j < 14; j++)
    		{
    			if(bs[i][j].isOccupied())
    			{
    				if(bs[i][j].getOccupant().getTeam().getTeamId() == 1)
    				{
    					one++;
    				}
    				if(bs[i][j].getOccupant().getTeam().getTeamId() == 2)
    				{
    					two++;
    				}
    			}
    		}
    	}
    	if(one == 0){
    		return 1 ;
    	}
    	if(two == 0){
    		return 2 ;
    	}
    	else{
    		return -1 ; 
    	}
		
	}
	
	public void doMove(LegalMove move){
		Kangaroo k = move.kangaroo;
		int tx = move.to.x;
		int ty = move.to.y;
	}
	
	public boolean isOver() {
    	int one = 0;
    	int two = 0;
    	
    	
    	for(int i = 0; i < 16; i++)
    	{
    		for(int j = 0; j < 14; j++)
    		{
    			if(bs[i][j].isOccupied())
    			{
    				if(bs[i][j].getOccupant().getTeam().getTeamId() == 1)
    				{
    					one++;
    				}
    				if(bs[i][j].getOccupant().getTeam().getTeamId() == 2)
    				{
    					two++;
    				}
    			}
    		}
    	}
    	if(one < 1 || two < 1) return true;
    	else return false;
    }
}
