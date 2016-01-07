// Author:		Charles Duncan
// Compiler:	Javac 1.7.0_02 (Java 1.7.0_60-b19)
// Created:		2/13/15
// Assignment:	1.6

import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;

public class MinesweeperFrame extends JFrame {

	// Variables ***********************************************************************
	private MinesweeperGame game;
	private MinesweeperPanel panel;

	// Methods *************************************************************************

	/**
	 * Ctor
	 * @param Number of tiles per column and row or 0 for default
	 * @param Probability a tile is a mine or -1.0 for default
	 * @param Random seed to use or -1 for variable seed
	 */
	public MinesweeperFrame (int numberOfTiles, double mineProbability, long debugSeed) {
	
		setTitle ("Minesweeper");
		setSize (800, 800);
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		
		game = new MinesweeperGame (numberOfTiles, mineProbability, debugSeed);
		
		panel = new MinesweeperPanel (this);
		add (panel);
		setVisible (true);
	
	}
	
	/**
	 * Returns the instance of the MinesweeperGame.
	 * @return Returns game.
	 */
	public MinesweeperGame getGame () {
	
		return game;
	
	}

}