// Author:		Charles Duncan (CharlesETD@gmail.com)
// Compiler:	Javac 1.7.0_02 (Java 1.7.0_60-b19)
// Created:		2/13/15
// Assignment:	1.6
// © Copyright 2015 Charles Duncan

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MinesweeperPanel extends JPanel {
	
	// Variables ***********************************************************************
	private MinesweeperFrame frame;
	private JPanel gridPanel;
	private JLabel mineLable;
	private JLabel statusLabel;
	private int mineCount;
	private String status;
	private Tile [] tiles;
	
	// Methods *************************************************************************
	
	/**
	 * Ctor
	 * @param Number of tiles per column and row or 0 for default
	 * @param Probability a tile is a mine or -1.0 for default
	 * @param Random seed to use or -1 for variable seed
	 */
	public MinesweeperPanel (MinesweeperFrame parent) {
	
		frame = parent;
		tiles = null;
		gridPanel = null;
		
		mineLable = new JLabel ("Mines: "+ Integer.toString (mineCount));
		statusLabel = new JLabel ("Careful!", SwingConstants.CENTER);
		
		setLayout (new BorderLayout ());
		
		add (mineLable, BorderLayout.SOUTH);
		add (statusLabel, BorderLayout.NORTH);
		
		reset ();
	
	}
	
	/**
	 * Resets the panel.
	 */
	public void reset () {
	
		int numberOfTiles = 0;
	
		if (tiles != null) {
		
			for (int i = 0; i < numberOfTiles; i++) {
		
				gridPanel.remove (tiles[i]);
		
			}
		
		}
		
		if (gridPanel != null) {
		
			remove (gridPanel);
		
		}
		
		numberOfTiles = (int)Math.pow (frame.getGame ().getSquareLength (), 2.0);
		gridPanel = new JPanel (new GridLayout (frame.getGame ().getSquareLength (), frame.getGame ().getSquareLength ()));
		
		tiles = new Tile [numberOfTiles];
		
		for (int i = 0; i < numberOfTiles; i++) {
		
			tiles[i] = new Tile (i, this);
			tiles[i].addMouseListener (new MouseHandler ());
			gridPanel.add (tiles[i]);
		
		}
		
		mineCount = getGame ().getEstimatedNumberOfMines ();
		
		status = "Careful!";
		
		add (gridPanel, BorderLayout.CENTER);
		
		repaint ();
	
	}
	
	/**
	 * Draws a game of minesweeper
	 * @param Graphics object to draw to
	 */
	public void paintComponent (Graphics g) {
	
		super.paintComponent (g);
		
		mineLable.setText ("Mines: "+ Integer.toString (mineCount));
		statusLabel.setText (status);

	}
	
	/**
	 * Returns the instance of the MinesweeperGame.
	 * @return Returns game.
	 */
	public MinesweeperGame getGame () {
	
		return frame.getGame ();
	
	}
	
	/**
	 * Handles button presses for the game.
	 */
	private class MouseHandler extends MouseAdapter {
	
		/**
		* Handles button presses for the game.
		* @param e the MouseEvent to be handled.
		*/
		public void mousePressed (MouseEvent e) {
		
			if (e.getButton () == MouseEvent.BUTTON3) {
			
				getGame ().flagTile (((Tile)(e.getSource ())).getTileIndex ());
			
			} else if (e.getButton () == MouseEvent.BUTTON1) {
			
				getGame ().exploreTile (((Tile)(e.getSource ())).getTileIndex ());
			
			}
			
			switch (getGame ().getGameState ()) {
		
			case PLAYING:
			
				mineCount = getGame ().getEstimatedNumberOfMines ();
				break;
			
			case GAME_OVER:
			
				mineCount = getGame ().getNumberOfMines ();
				status = "You Lost In A Mere " + Float.toString (getGame ().getFinalTime ()) + " Seconds!";
				break;
			
			case WON:
		
				mineCount = getGame ().getNumberOfMines ();
				status = "You Won In A Mere " + Float.toString (getGame ().getFinalTime ()) + " Seconds!";
				break;
				
			}
			
			repaint ();
		
		}
	
	}
	
}