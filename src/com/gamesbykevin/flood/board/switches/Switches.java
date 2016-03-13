package com.gamesbykevin.flood.board.switches;

import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Disposable;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.flood.assets.Assets;
import com.gamesbykevin.flood.board.BoardHelper;
import com.gamesbykevin.flood.board.Board;
import com.gamesbykevin.flood.board.Board.Colors;
import com.gamesbykevin.flood.panel.GamePanel;

import android.graphics.Canvas;

public class Switches implements Disposable
{
	//list of switches to change the color
	private Switch[] switches;

	/**
	 * The default size of a switch
	 */
	private static final int SWITCH_DIMENSION = 64;
	
	/**
	 * The pixel space between each switch
	 */
	private static final int SWITCH_PADDING = 15;
	
	/**
	 * The y-coordinate where the switches are rendered
	 */
	private static final int SWITCH_Y = 676;
	
	//the number of attempts
	private int attempts = 0;
	
	public Switches()
	{
		//default constructor
	}
	
	/**
	 * Get the attempts
	 * @return The number of attempts used to solve the game
	 */
	public int getAttempts()
	{
		return this.attempts;
	}
	
	/**
	 * Set the attempts
	 * @param attempts The number of attempts used to solve the game
	 */
	public void setAttempts(final int attempts)
	{
		this.attempts = attempts;
	}
	
	@Override
	public void dispose() 
	{
		if (switches != null)
		{
			for (int i = 0; i < switches.length; i++)
			{
				if (switches[i] != null)
				{
					switches[i].dispose();
					switches[i] = null;
				}
			}
			
			switches = null;
		}
	}

	/**
	 * Check if any switches can be clicked with the specified coordinates
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void update(final float x, final float y) 
	{
		//make sure we don't press multiple buttons
		for (Switch tmp : switches)
		{
			//if we already clicked a button we can't continue
			if (tmp.isClicked())
				return;
		}
		
		//check if we pressed any of the switches
		for (Switch tmp : switches)
		{
			//if we clicked this button and it is displayed
			if (tmp.contains(x, y) && tmp.isVisible())
			{
				//flag clicked
				tmp.setClicked(true);
				
				//exit loop
				break;
			}
		}
	}

	/**
	 * Check if any switches have been clicked and if so update the board
	 * @param board The board in play
	 */
	public void update(final Board board) 
	{
		//did we click anything
		boolean clicked = false;
		
		//check if we pressed any of the switches
		for (Switch tmp : switches)
		{
			//if we clicked this switch
			if (tmp.isClicked())
			{
				//play sound effect
				switch (tmp.getColor())
				{
					case Green:
						Audio.play(Assets.AudioGameKey.Switch1);
						break;
						
					case Red:
						Audio.play(Assets.AudioGameKey.Switch2);
						break;
						
					case Yellow:
						Audio.play(Assets.AudioGameKey.Switch3);
						break;
						
					case Blue:
						Audio.play(Assets.AudioGameKey.Switch4);
						break;
						
					case Purple:
						Audio.play(Assets.AudioGameKey.Switch5);
						break;
						
					case White:
					default:
						Audio.play(Assets.AudioGameKey.Switch6);
						break;
				}
				
				//increase our attempt count
				setAttempts(getAttempts() + 1);
				
				//flood the squares on the board
				BoardHelper.floodSquares(board.getKey(), tmp.getColor());
				
				//do we have win
				board.setWin(BoardHelper.hasWin(board.getKey()));
				
				//hide button switch
				tmp.setVisible(false);
				
				//flag that something was clicked
				clicked = true;
				
				//set the new flood color
				board.setCurrent(tmp.getColor());
			
				//flag false
				tmp.setClicked(false);
				
				//exit loop
				break;
			}
		}
		
		//update the button visibility
		if (clicked)
		{
			for (Switch tmp : switches)
			{
				tmp.setVisible(tmp.getColor() != board.getCurrent());
			}
		}
	}

	/**
	 * Reset the switches with the specified parameters
	 * @param total The total number of colors
	 * @param dimension The default dimension size of a square on the board
	 * @param current The current color for the flood
	 */
	public void reset(final int total, final int dimension, final Colors current) 
	{
		//reset attempts
		setAttempts(0);
		
		//create new array for the button switches
		this.switches = new Switch[total];
		
		//calculate the start coordinate
		int x = (GamePanel.WIDTH / 2) - (((total * SWITCH_DIMENSION) + ((total - 1) * SWITCH_PADDING)) / 2);

		//render the buttons
		for (int index = 0; index < total; index++)
		{
			//create a new switch of the specified color
			this.switches[index] = new Switch(
				Colors.values()[index],
				Images.getImage(Assets.ImageGameKey.Colors)
			);
			
			//set location
			this.switches[index].setX(x);
			this.switches[index].setY(SWITCH_Y);
			
			//set the dimensions
			this.switches[index].setWidth(SWITCH_DIMENSION);
			this.switches[index].setHeight(SWITCH_DIMENSION);
			
			//update the boundary to detect clicks
			this.switches[index].updateBounds();
			
			//change the coordinate for the next switch
			x += (SWITCH_DIMENSION + SWITCH_PADDING);
		}
		
		//assign the current color and hide the current switch
		for (Switch tmp : switches)
		{
			//if this switches color equals the current color
			if (tmp.getColor() == current)
			{
				//hide the button
				tmp.setVisible(false);
				
				//exit loop
				break;
			}
		}
	}

	public void render(Canvas canvas) throws Exception 
	{
		//render the switches
		for (Switch tmp : switches)
		{
			tmp.render(canvas);
		}
	}
}