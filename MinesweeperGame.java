// Author:		Charles Duncan
// Compiler:	Javac 1.7.0_02 (Java 1.7.0_60-b19)
// Created:		2/13/15
// Assignment:	1.6

import java.util.ArrayList;
import java.util.Random;

public class MinesweeperGame {

	// Variables ***********************************************************************
	private double mineProbability;
	private long randomSeed;
	private int [] gameGrid;
	private ArrayList<Integer> flags;
	private boolean [] explored;
	private int numberOfMines;
	private int squareLength;
	private int numberOfTilesExplored;
	private long startTime;
	private long stopTime;
	
	public enum GameState { PLAYING, GAME_OVER, WON }
	private GameState gameState;
	
	// Enums with specific values are a pain in Java 
	public static final int MINE = -1; // This could be useful for outside classes to know
	public static final int FLAGGED = -2; // This could be useful for outside classes to know
	public static final int UNEXPLORED = -3; // This could be useful for outside classes to know
	private static final int DEFAULT_NUMBER_OF_TILES = 144;
	private static final double DEFAULT_MINE_PROBABILITY = 0.3;
	
	// Methods *************************************************************************
	
	/**
	 * Ctor
	 * @param Number of tiles per column and row or 0 for default
	 * @param Probability a tile is a mine or -1.0 for default
	 * @param Random seed to use or -1 for variable seed
	 */
	public MinesweeperGame (int numberOfTiles, double mineProbability, long debugSeed) {
	
		numberOfMines = 0;
		numberOfTilesExplored = 0;
		gameState = GameState.PLAYING;
	
		if (numberOfTiles < 1) {
		
			numberOfTiles = DEFAULT_NUMBER_OF_TILES;
		
		} else {
		
			numberOfTiles = numberOfTiles * numberOfTiles;
		
		}
		
		if (mineProbability < 0.0) {
		
			this.mineProbability = DEFAULT_MINE_PROBABILITY;
		
		} else {
		
			this.mineProbability = mineProbability;
		
		}
		
		if (debugSeed == -1) {
		
			this.randomSeed = System.currentTimeMillis ();
		
		} else {
		
			this.randomSeed = debugSeed;
		
		}
		
		squareLength = (int)Math.sqrt (numberOfTiles);
		
		gameGrid = new int [numberOfTiles];
		explored = new boolean [numberOfTiles];
		propogateGameGrid ();
		
		flags = new ArrayList<Integer> (numberOfMines);
		
		startTime = System.currentTimeMillis ();
	
	}
	
	/**
	 * Gets the current gameState
	 * @return Returns the gameState
	 */
	public GameState getGameState () {
	
		return gameState;
	
	}
	
	/**
	 * Gets the number of mines on the field
	 * @return Returns the number of mines on the field
	 */
	public int getNumberOfMines () {
	
		return numberOfMines;
	
	}
	
	/**
	 * Gets the seed used to generate the field
	 * @return Returns the randomSeed
	 */
	public long getRandomSeed () {
	
		return randomSeed;
	
	}
	
	/**
	 * Gets the square length of the field
	 * @return Returns squareLength
	 */
	public int getSquareLength () {
	
		return squareLength;
	
	}
	
	/**
	 * Gets the number of flags currently out.
	 * @return Returns the length of flags.
	 */
	public int getNumberOfFlags () {
	
		return flags.size ();
	
	}
	
	/**
	 * Gets the number of mines - number of flags.
	 * @return Returns the number of mines - number of flags.
	 */
	public int getEstimatedNumberOfMines () {
	
		return numberOfMines - flags.size ();
	
	}
	
	/**
	 * Gets the number of mines adjacent to a tile or MINE if the tile is a mine or FLAGGED if the tile is flagged
	 * or UNEXPLROED if the tile has not been explored yet.
	 * @param Index of the tile to check.
	 * @return Returns the number of mines adjacent to a tile or MINE, FLAGGED, or UNEXPLORED.
	 */
	public int getStateOf (int position) {
	
		// Is the position given invalid?
		if (position < 0 || position >= gameGrid.length) {
		
			System.out.println ("Tile Invalid");
			assert (false);
		
		}
	
		if (explored[position]) {
		
			return gameGrid[position];
		
		}
	
		for (int i = 0; i < flags.size (); i++) {
			
			if (flags.get (i) == position) {
				
				return FLAGGED;
				
			}
			
		}
	
		return UNEXPLORED;
	
	}
	
	/**
	 * Gets the time elapsed since the game started.
	 * @return Returns the time elapsed since the game started in seconds.
	 */
	public float getGameTime () {

		return (System.currentTimeMillis () -startTime) / 1000.0f;

	}	
	
	/**
	 * Gets the time elapsed since the game started to when it ended.
	 * @return Returns the time elapsed since the game started to when it ended in seconds.
	 */
	public float getFinalTime () {

		return (stopTime -startTime) / 1000.0f;

	}	
	
	/**
	 * Prints the game board to the console
	 */
	public void print () {
	
		for (int i = 0; i < gameGrid.length; i++) {
		
			if (i != 0 && i % squareLength == 0) {
			
				System.out.println ();
			
			}
		
			if (gameGrid[i] == MINE) {
			
				System.out.print ('*');
			
			} else {
			
				System.out.print (gameGrid[i]);
			
			}
		
		}
	
	}
	
	/**
	 * Checks to see if the selected tile is a mine or not, alters gameState
	 * @param Index of tile to check
	 * @return Returns false if not a mine, returns true otherwise
	 */
	public boolean exploreTile (int position) {
	
		// Is the position given invalid?
		if (position < 0 || position >= gameGrid.length) {
		
			System.out.println ("Tile Invalid");
			assert (false);
		
		}
		
		if (explored[position]) {
		
			return false;
		
		}
		
		explored[position] = true;
		
		if (gameGrid[position] != MINE) {
		
			numberOfTilesExplored++;
		
			if (numberOfMines + numberOfTilesExplored == gameGrid.length) {
			
				gameState = GameState.WON;
				stopTime = System.currentTimeMillis ();
				revealAll ();
			
			} else if (gameGrid[position] == 0) {
			
				exploreAdjacent (position);
			
			}
		
			return false;
		
		}

		gameState = GameState.GAME_OVER;
		stopTime = System.currentTimeMillis ();
		revealAll ();
		return true;
	
	}
	
	/**
	 * Toggles a tile to be flagged or unflagged.
	 * @param Index of tile to flagged
	 */
	public void flagTile (int position) {
	
		// Is the position given invalid?
		if (position < 0 || position >= gameGrid.length) {
		
			System.out.println ("Tile Invalid");
			assert (false);
		
		}

		if (explored[position]) {
		
			return;
		
		}
		
		for (int i = 0; i < flags.size (); i++) {
			
			if (flags.get (i) == position) {
				
				flags.remove (i);
				return;
				
			}
			
		}
		
		if (flags.size () < numberOfMines) {
		
			flags.add (position);
		
		}
	
	}
	
	/**
	 * Fills the game grid with a random number of mines and calculates the adjacent mines for each tile
	 */
	private void propogateGameGrid () {
	
		Random rand = new Random (randomSeed);
	
		for (int i = 0; i < gameGrid.length; i++) {
		
			if (rand.nextDouble () <= mineProbability) {
			
				gameGrid[i] = MINE;
				numberOfMines++;
				
				updateAdjacent (i);
			
			}
		
		}
	
	}
	
	/**
	 * Increases the adjacent mine count of nearby tiles
	 * @param Index of mine to update around
	 */
	private void updateAdjacent (int minePosition) {
	
		int adjacentIndex = 0;
	
		// Is the position given invalid?
		if (minePosition < 0 || minePosition >= gameGrid.length || gameGrid[minePosition] != MINE) {
		
			return;
		
		}
	
		// Adjust adjacent mine count of nearby tiles
		for (int j = -1; j <= 1; j++) {
			
			for (int k = -1; k <= 1; k++) {
				
				adjacentIndex = minePosition + (j * squareLength);
				
				// Avoid counting first and last tiles as adjacent when mine is a first or last element
				if ((adjacentIndex % squareLength == 0 && k == -1) || ((adjacentIndex + 1) % squareLength == 0 && k == 1)) {
					
					continue;
					
				}
				
				adjacentIndex += k;
				
				// These conditions must be checked twice to avoid catching the end of a line when k = 1
				if (adjacentIndex >= 0 && adjacentIndex < gameGrid.length && gameGrid[adjacentIndex] != MINE) {
					
					gameGrid[adjacentIndex]++;
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Explores all adjacent tiles.
	 * @param Index of tile to update around
	 */
	private void exploreAdjacent (int position) {
	
		int adjacentIndex = 0;
	
		// Is the position given invalid?
		if (position < 0 || position >= gameGrid.length) {
		
			return;
		
		}
	
		// Adjust adjacent mine count of nearby tiles
		for (int j = -1; j <= 1; j++) {
			
			for (int k = -1; k <= 1; k++) {
				
				adjacentIndex = position + (j * squareLength);
				
				// Avoid counting first and last tiles as adjacent when mine is a first or last element
				if ((adjacentIndex % squareLength == 0 && k == -1) || ((adjacentIndex + 1) % squareLength == 0 && k == 1)) {
					
					continue;
					
				}
				
				adjacentIndex += k;
				
				// These conditions must be checked twice to avoid catching the end of a line when k = 1
				if (adjacentIndex >= 0 && adjacentIndex < gameGrid.length) {
					
					exploreTile (adjacentIndex);
					
				}
				
			}
			
		}
	
	}
	
	/**
	 * Explores all tiles when the game is over or won.
	 */
	private void revealAll () {
	
		for (int i = 0; i < explored.length; i++) {
		
			explored[i] = true;
		
		}
	
	}
	
}