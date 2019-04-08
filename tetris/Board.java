// Board.java
package tetris;

import java.util.Arrays;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = true;
	boolean committed;
	
	private int maxHeight;
	private int[] widths;
	private int[] heights;
	private boolean[][] backUpGrid;
	private int[] backUpWidths;
	private int[] backUpHeights;
	private int backUpMaxHeight;
	
	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;
		
		backUpGrid = new boolean[width][height];
		heights = new int[width];
		backUpHeights = new int[width];
		widths = new int[height];
		backUpWidths = new int[height];
	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {	 
		return maxHeight; 
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			int realMaxHeight = 0;
			int[] realHeights = new int[width];
			int[] realWidths = new int[height];
			
			for(int i = 0; i < getWidth(); i++){
				for(int j = 0; j < getHeight(); j++){
					if(grid[i][j]){
						realWidths[j] += 1;
						if(j + 1 > realHeights[i]){
							realHeights[i] = j + 1;
							if(realHeights[i] > realMaxHeight){
								realMaxHeight = realHeights[i];
							}
						}
					}
				}
			}
			
			if(!Arrays.equals(realWidths, widths)){
				 throw new RuntimeException("Array of widths is not correct");
			}
			if(!Arrays.equals(realHeights, heights)){
				 throw new RuntimeException("Array of heights is not correct");
			}
			if(getMaxHeight() != realMaxHeight){
				 throw new RuntimeException("Maximum Height of Grid is not correct");
			}
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int h = 0;	
		for(int i = 0; i < piece.getSkirt().length; i++){
			if(getColumnHeight(x + i) - piece.getSkirt()[i] > 0 
					&& getColumnHeight(x + i) - piece.getSkirt()[i] > h){
				h = getColumnHeight(x + i) - piece.getSkirt()[i];
			}
		}
		return h;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heights[x];
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		 return widths[y]; 
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		boolean validArea = false;
		if(x >= getWidth() || x < 0 || y < 0 || y > getHeight()){
			validArea = true;
		}
		return grid[x][y] || validArea;
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
			
		int result = PLACE_OK;
		committed = false;
		keepBoard();
		
		for(int i = 0; i < piece.getBody().length; i++){
			int curPtX = piece.getBody()[i].x;
			int curPtY = piece.getBody()[i].y;
			
			if((curPtX + x >= getWidth()) || (curPtX + x < 0) 
					|| (curPtY + y >= getHeight()) || (curPtY + y < 0)){
				return PLACE_OUT_BOUNDS;
			}
			if(getGrid(curPtX + x, curPtY + y)){
				return PLACE_BAD;
			}
			
			grid[curPtX + x][curPtY + y] = true;
			
			widths[curPtY + y] += 1;
			if(widths[curPtY + y] == getWidth()){
				result = PLACE_ROW_FILLED;
			}
			
			if(curPtY + y + 1 > heights[curPtX + x]){
				heights[curPtX + x] = curPtY + y + 1;
				if(heights[curPtX + x] > getMaxHeight()){
					maxHeight = heights[curPtX + x];
				}
			}
		}
		
		sanityCheck();
		return result;
	}
	
	
	private void keepBoard() {
		System.arraycopy(widths, 0, backUpWidths, 0, getHeight());
		System.arraycopy(heights, 0, backUpHeights, 0, getWidth());
		
		backUpMaxHeight = getMaxHeight();
		
		for(int i = 0; i < grid.length; i++){
			System.arraycopy(grid[i], 0, backUpGrid[i], 0, grid[i].length);
		}
	}


	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		int rowsCleared = 0;
		committed = false;
		
		int to = 0;
		for(int from = 0; from < getHeight(); from++){
			if(widths[from] == getWidth()){
				rowsCleared += 1;
			}else{
				if(rowsCleared > 0){
					for(int i = 0; i < getWidth(); i++){
						grid[i][to] = grid[i][from];	
					}
					widths[to] = widths[from];
				}
				to += 1; 
			}
		}
		
		for(int i = to; i < getHeight(); i++){
			widths[i] = 0;
			for(int j = 0; j < getWidth(); j++){
				grid[j][i] = false;
				heights[j] -= 1;
			}
		}
		
		
		maxHeight -= rowsCleared;
		sanityCheck();
		return rowsCleared;
	}



	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if(!committed){
			commit();
			
			boolean[][] tmp = grid;
			grid = backUpGrid;
			backUpGrid = tmp;
			
			int[] tmpW = widths;
			widths = backUpWidths;
			backUpWidths = tmpW;
			
			int[] tmpH = heights;
			heights = backUpHeights;
			backUpHeights = tmpH;
			
			maxHeight = backUpMaxHeight;	
		}
		sanityCheck();
	}
	
	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
	}


	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


