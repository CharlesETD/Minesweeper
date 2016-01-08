// Author:		Charles Duncan (CharlesETD@gmail.com)
// Compiler:	Javac 1.7.0_02 (Java 1.7.0_60-b19)
// Created:		2/13/15
// Assignment:	1.6
// © Copyright 2015 Charles Duncan

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Tile extends JButton {

	// Variables ***********************************************************************
	private int tileIndex;
	private int state;
	private MinesweeperPanel panel;
	private ImageIcon mineImage;
	private ImageIcon flagImage;
	
	private static final String MINE_IMG_PATH = "../assets/mine.png";
	private static final String FLAG_IMG_PATH = "../assets/flag.png";
	
	// Methods *************************************************************************
	
	/**
	 * Ctor
	 * @param Tile index of corresponding tile in gameState
	 * @param The panel that is running the game.
	 */
	public Tile (int tileIndex, MinesweeperPanel p) {
		
		super ("");
		this.tileIndex = tileIndex;
		this.panel = p;
		state = p.getGame ().getStateOf (this.tileIndex);
		setEnabled (true);
		mineImage = new ImageIcon (Toolkit.getDefaultToolkit ().getImage (MINE_IMG_PATH));
		flagImage = new ImageIcon (Toolkit.getDefaultToolkit ().getImage (FLAG_IMG_PATH));
		
	}
	
	/**
	 * Gets this tile's index.
	 * @return Returns the tile index.
	 */
	public int getTileIndex () {
	
		return tileIndex;
	
	}
	
	/**
	 * Draws the graphics of the tile.
	 * @param Graphics object to draw to
	 */
	public void paintComponent (Graphics g) {
	
		super.paintComponent (g);
		
		state = panel.getGame ().getStateOf (this.tileIndex);
		
		switch (state) {
		
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
			
			// This would be much cleaner if Java supported case statements with range
			// Also setting the colour of the number based on how many mines are near was my plan,
			// but disabling the button greys out the contents and I don't know how to stop that.
			setEnabled (false);
			setIcon (null);
			setDisabledIcon (null);
			if (state == 0) {
			
				setText (" ");
			
			} else {
			
				setText (Integer.toString (state));
			
			}
			break;
		
		case MinesweeperGame.MINE:
		
			setEnabled (false);
			setText ("");
			setIcon (mineImage);
			setDisabledIcon (mineImage);
			break;
		
		case MinesweeperGame.FLAGGED:
			
			setText ("");
			setIcon (flagImage);
			break;
		
		case MinesweeperGame.UNEXPLORED:
			
			setText ("?");
			setIcon (null);
			setDisabledIcon (null);
			break;
		
		}

	}

}