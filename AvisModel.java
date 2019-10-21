//@author Avi Gupta
import java.util.Random;
import java.util.ArrayList;

public class AvisModel implements MineModel {
	private int numRows;
	private int numCols;
	private int numMines;
	private int numFlags;
	private long initialTime; 
	private boolean gameStarted;
	private boolean gameOver;
	private boolean isPlayerDead;
	private boolean isGameWon;
	private AvisCell[][] cells; //indexed by row, col
	private boolean firstClick;
	
	public AvisModel() {
		gameStarted = false;
	}
	
	@Override
	public void newGame(int numRows, int numCols, int numMines) {
		this.numRows = numRows;
		this.numCols = numCols;
		this.numMines = numMines;
		numFlags = 0;
		gameOver = false;
		isPlayerDead = false;
		isGameWon = false;
		firstClick = true;
		//initializing cell 2D array
		cells = new AvisCell [numRows][numCols];
		for (int row = 0; row<numRows; row++ ) {
			for (int col = 0; col<numCols; col++ ) {
				cells[row][col] = new AvisCell(row,col);
			}
		}
		gameStarted = true;
		initialTime = System.currentTimeMillis();
	}
	//this places all the mines on the board 
	//subject to the constraint that they can't be next to or on the first click 
	public void minePlace() {		
			Random randgen = new Random();
			for (int minesPlaced = 0; minesPlaced < numMines; minesPlaced ++) {
				int mineRow, mineCol;
				boolean neighborvisible;
				AvisCell active;
				do  {
					mineRow = randgen.nextInt(numRows);
					mineCol = randgen.nextInt(numCols);
					neighborvisible = false;
					active = cells[mineRow][mineCol];
					ArrayList <AvisCell> neighbors = getAdjacent(mineRow, mineCol);
					for (AvisCell neighbor: neighbors) {
						if (neighbor.isVisible()) {
							neighborvisible = true;
						}
					}
				} while (active.isMine() || active.isVisible() || neighborvisible);
				
				cells[mineRow][mineCol].makeMine();
				
				ArrayList <AvisCell> neighbors = getAdjacent(mineRow, mineCol);
				
				for (AvisCell a: neighbors) {
					a.incrementNeighborMines();
				}
			}
	}

	@Override
	public int getNumRows() {
		return numRows;
	}

	@Override
	public int getNumCols() {
		return numCols;
	}

	@Override
	public int getNumMines() {
		return numMines;
	}

	@Override
	public int getNumFlags() {
		return numFlags;
	}

	@Override
	public int getElapsedSeconds() {
		long currentTime = System.currentTimeMillis();
		int timeElapsed = (int) ((currentTime - initialTime)/1000);
		return timeElapsed;
	}

	@Override
	public Cell getCell(int row, int col) {
		return cells[row][col];
	}

	//handles flooding
	public void flood(int row, int col) {
		AvisCell activeCell = cells[row][col];
		ArrayList <AvisCell> neighbors = getAdjacent(row,col);
		if (activeCell.getNeighborMines()==0) {
			for (AvisCell a: neighbors) {
				if (!a.isVisible() && !a.isMine()) {
					stepOnCell(a.getRow(), a.getCol());
				}
			}
		}
	}
	@Override
	public void stepOnCell(int row, int col) {
		AvisCell activeCell = cells[row][col];
		if (activeCell.isMine()) {
			isPlayerDead = true;
			exposeMines(); //exposes the mines when you lose
			gameOver = true;
		}
		
		if (firstClick) {
			activeCell.step(); 
			minePlace(); //places mines AFTER the first click is done so we can make
			//sure the first click is flooded
			firstClick = false;
		}
		else {
			activeCell.step();
		}
		if (activeCell.getNeighborMines()==0 && !activeCell.isMine()) {
			flood(row, col);
		}	
	}
			
	@Override
	public void placeOrRemoveFlagOnCell(int row, int col) {
		AvisCell activeCell = cells[row][col];
		activeCell.changeFlag();
		if (activeCell.isFlagged()==true) {
			numFlags++;
		}
		else if (activeCell.isFlagged()==false) {
			numFlags--;
		}
	}

	@Override
	public boolean isGameStarted() {
		return gameStarted;
	}

	@Override
	public boolean isGameOver() {
		if (isGameWon || isPlayerDead ) {
			gameOver = true;
		}
		return gameOver;
	}

	@Override
	public boolean isPlayerDead() {
		return isPlayerDead;
	}
	
//current win condition: all cells that aren't mines visible
	@Override
	public boolean isGameWon() {
		isGameWon = true;
		for (int row = 0; row <numRows; row++ ) {
			for (int col = 0; col < numCols; col++) {
				if (cells[row][col].isVisible() ==false &&   
						cells[row][col].isMine()==false) {
					isGameWon = false;
					break;
				} 
			}
		}
		if (isGameWon) {
			for (AvisCell [] row: cells) {
				for (AvisCell a: row) {
					if (a.isMine()){
						a.makeVisible();
					}
				}
			}
		}
		return isGameWon;
	}
	//exposes all the mines at the end of the game
	public void exposeMines() {
		if (isPlayerDead) {
			for (AvisCell[] cellRow: cells) {
				for (AvisCell cell: cellRow) {
					if (cell.isMine()) {
						cell.makeVisible();
				}
			}
			}
		}
	}
	//tried to write a helper, never finished
	/**public void helper(int row, int col) {
		int flagCounter = 0;
		AvisCell activeCell = cells[row][col];
		ArrayList <AvisCell> neighbors = getAdjacent(row,col);
		for (AvisCell a: neighbors) {
			if (a.isFlagged()) {
				flagCounter ++;
			}
		}
		if (activeCell.getNeighborMines() == flagCounter) {
			for (AvisCell b: neighbors) {
				if (!b.isVisible() && !b.isFlagged()) {
					stepOnCell(b.getRow(), b.getCol());
				}
			}
		}
		}**/
	//returns arraylist of all adjacent cells to simplify code
	public ArrayList<AvisCell> getAdjacent(int row, int col) {
		boolean rowsUpper = (row + 1 < numRows);
		boolean rowsLower = (row - 1 >= 0);
		boolean colsUpper = (col + 1 < numCols);
		boolean colsLower = (col - 1 >= 0);
		
		ArrayList<AvisCell> neighbors = new ArrayList<AvisCell>();
		
			if (rowsUpper) {				
				neighbors.add(cells[row+1][col]);
				if (colsUpper) {
					neighbors.add(cells[row+1][col+1]);				
				}
				if (colsLower) {					
					neighbors.add(cells[row+1][col-1]);			
				}
			}
			if (rowsLower) {			
				neighbors.add(cells[row-1][col]);			
			
				if (colsUpper) {				
					neighbors.add(cells[row-1][col+1]);
					}
				
				if (colsLower) {					
					neighbors.add(cells[row-1][col-1]);
				}
			}
			if (colsUpper) {				
				neighbors.add(cells[row][col+1]);
			}
			
			if (colsLower) {				
				neighbors.add(cells[row][col-1]);
			} 
	return neighbors;
		}
	}