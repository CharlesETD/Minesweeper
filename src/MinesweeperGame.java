// Author:		Charles Duncan (CharlesETD@gmail.com)
// Compiler:	Javac 1.7.0_02 (Java 1.7.0_60-b19)
// Created:		2/13/15
// Assignment:	1.6
// © Copyright 2015 Charles Duncan

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	public enum Difficulty { EASY, INTERMEDIATE, EXPERT }
	private GameState gameState;
	
	// Enums with specific values are a pain in Java 
	public static final int MINE = -1; // This could be useful for outside classes to know
	public static final int FLAGGED = -2; // This could be useful for outside classes to know
	public static final int UNEXPLORED = -3; // This could be useful for outside classes to know
	
	private static final int EASY_NUMBER_OF_TILES = 9;
	private static final double EASY_MINE_PROBABILITY = 0.123456789;
	
	private static final int INTERMEDIATE_NUMBER_OF_TILES = 16;
	private static final double INTERMEDIATE_MINE_PROBABILITY = 0.15625;
	
	private static final int EXPERT_NUMBER_OF_TILES = 22;
	private static final double EXPERT_MINE_PROBABILITY = 0.20661157;
	
	private static final String FILE_EXTENSION = "msg";
	
	// Methods *************************************************************************
	
	/**
	 * Ctor
	 * @param Number of tiles or 0 for default
	 * @param Probability a tile is a mine or -1.0 for default
	 * @param Random seed to use or -1 for variable seed
	 */
	public MinesweeperGame (int numberOfTiles, double mineProbability, long debugSeed) {
	
		reset (numberOfTiles, mineProbability, debugSeed);
	
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

		return (System.currentTimeMillis () - startTime) / 1000.0f;

	}	
	
	/**
	 * Gets the time elapsed since the game started to when it ended.
	 * @return Returns the time elapsed since the game started to when it ended in seconds.
	 */
	public float getFinalTime () {

		return (stopTime - startTime) / 1000.0f;

	}	
	
	/**
	 * Gets the file extension used to save games.
	 * @return Returns FILE_EXTENSION.
	 */
	public static String getFileExtension () {
	
		return FILE_EXTENSION;
	
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
	 * Creates a new game with the given difficulty.
	 * @param difficulty of the game
	 */
	public void newGame (Difficulty difficulty) {
	
		switch (difficulty) {
		
		case EASY:
			reset (EASY_NUMBER_OF_TILES, EASY_MINE_PROBABILITY, -1);
			break;
			
		case INTERMEDIATE:
			reset (INTERMEDIATE_NUMBER_OF_TILES, INTERMEDIATE_MINE_PROBABILITY, -1);
			break;
			
		case EXPERT:
			reset (EXPERT_NUMBER_OF_TILES, EXPERT_MINE_PROBABILITY, -1);
			break;
		
		}
	
	}
	
	/**
	 * Loads a game from the given file.
	 * @param saveFile to load from.
	 * @return false if failed to load.
	 */
	public boolean load (File saveFile) {
	
		String filename = saveFile.getName ();
		FileInputStream inStream = null;
		BufferedInputStream bufferedInStream = null;
		ObjectInputStream objectStream = null;
		
		if (filename.indexOf (".") == -1) {
		
			saveFile = new File (filename + "." + FILE_EXTENSION);
			filename = saveFile.getName ();
		
		}
		
		if (saveFile.exists () && saveFile.canRead () && (filename.substring (filename.indexOf ("."))).equals ("." + FILE_EXTENSION)) {
		
			try {
			
				inStream = new FileInputStream (saveFile);
				bufferedInStream = new BufferedInputStream (inStream);
				objectStream = new ObjectInputStream (bufferedInStream);
			
				mineProbability = (Double) (objectStream.readObject ());
				randomSeed = (Long) (objectStream.readObject ());
				numberOfMines = (Integer) (objectStream.readObject ());
				squareLength = (Integer) (objectStream.readObject ());
				numberOfTilesExplored = (Integer) (objectStream.readObject ());
				startTime = System.currentTimeMillis () - (Long) (objectStream.readObject ());
				stopTime = (Long) (objectStream.readObject ());
				gameState = (GameState) (objectStream.readObject ());
				flags = castObjectToArrayList (objectStream.readObject ());
				gameGrid = (int []) (objectStream.readObject ());
				explored = (boolean []) (objectStream.readObject ());
			
			} catch (Exception e) {
			
				return false;
			
			}
		
			return true;
		
		}
		
		return false;
	
	}
	
	/**
	 * Saves a game to the given file.
	 * @param saveFile to save to.
	 * @return false if failed to save.
	 */
	public boolean save (File saveFile) {
	
		String filename = saveFile.getName ();
		FileOutputStream outStream = null;
		BufferedOutputStream bufferedOutStream = null;
		ObjectOutputStream objectStream = null;
	
		/*
		mineProbability
		randomSeed
		numberOfMines
		squareLength
		numberOfTilesExplored
		startTime
		stopTime
		gameState
		flags
		gameGrid
		explored
		*/
	
		if (filename.indexOf (".") == -1) {
		
			saveFile = new File (filename + "." + FILE_EXTENSION);
			filename = saveFile.getName ();
		
		}
	
		if (filename.substring (filename.indexOf (".")).equals ("." + FILE_EXTENSION)) {
		
			try {
		
				outStream = new FileOutputStream (saveFile);
				bufferedOutStream = new BufferedOutputStream (outStream);
				objectStream = new ObjectOutputStream (bufferedOutStream);
			
				objectStream.writeObject (new Double (mineProbability));
				objectStream.writeObject (new Long (randomSeed));
				objectStream.writeObject (new Integer (numberOfMines));
				objectStream.writeObject (new Integer (squareLength));
				objectStream.writeObject (new Integer (numberOfTilesExplored));
				objectStream.writeObject (new Long (System.currentTimeMillis () - startTime));
				objectStream.writeObject (new Long (stopTime));
				objectStream.writeObject (gameState);
				objectStream.writeObject (flags);
				objectStream.writeObject (gameGrid);
				objectStream.writeObject (explored);
				
				bufferedOutStream.flush ();
			
			} catch (Exception e) {
			
				return false;
			
			}
		
			return true;
		
		}
		
		return false;
	
	}
	
	/**
	 * newGame
	 * @param Number of tiles or 0 for default
	 * @param Probability a tile is a mine or -1.0 for default
	 * @param Random seed to use or -1 for variable seed
	 */
	public void reset (int numberOfTiles, double mineProbability, long debugSeed) {
	
		numberOfMines = 0;
		numberOfTilesExplored = 0;
		gameState = GameState.PLAYING;
		stopTime = -1;
		startTime = 0;
	
		if (numberOfTiles < 1) {
		
			numberOfTiles = INTERMEDIATE_NUMBER_OF_TILES * INTERMEDIATE_NUMBER_OF_TILES;
		
		} else {
		
			numberOfTiles = numberOfTiles * numberOfTiles;
		
		}
		
		if (mineProbability < 0.0) {
		
			this.mineProbability = INTERMEDIATE_MINE_PROBABILITY;
		
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
	
	@SuppressWarnings("unchecked")
	private ArrayList<Integer> castObjectToArrayList (Object obj) {
	
		if (obj instanceof ArrayList) {

			return (ArrayList<Integer>)(obj);
			
		}
		
		return null;
	
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