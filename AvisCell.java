//@author Avi Gupta
public class AvisCell implements Cell {
	private int row;
	private int col;
	private boolean isVisible;
	private boolean isMine;
	private boolean isFlagged;
	private int neighborMines;
	
	public AvisCell(int r, int c) {
		row = r;
		col = c;
		neighborMines = 0;
		isVisible = false;
		isMine = false;
		isFlagged = false;
	}
	
	@Override
	public int getRow() {
		return row;
	}

	@Override
	public int getCol() {
		return col;
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}

	@Override
	public boolean isMine() {
		return isMine;
	}

	//sets cell to a mine
	public void makeMine() {
		isMine = true;
	}
	
	//makes cell visible if it isn't
	public void step() {
		if (isVisible==false) {
			isVisible = true;
		}
	}

	@Override
	public boolean isFlagged() {
		return isFlagged;
	}
	
	
	//changes status of whether cell is flagged
	public void changeFlag() {
		if (isFlagged) {
			isFlagged = false;
		}
		else {
			isFlagged = true;
		}
	}

	@Override
	public int getNeighborMines() {
		return neighborMines;
	}
	
	public void setNeighborMines(int n) {
		neighborMines = n;
	}
	
	public void incrementNeighborMines() {
		neighborMines++;
	}		
	public void makeVisible() {
		isVisible = true;
	}
}