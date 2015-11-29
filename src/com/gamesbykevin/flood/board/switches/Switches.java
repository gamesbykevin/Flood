package com.gamesbykevin.flood.board.switches;

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
	
	public Switches()
	{
		//default constructor
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

	public void reset(final int size, final int colors, final int dimension, final Colors current) 
	{
		//create new array for the board switches
		this.switches = new Switch[size];
		
		//calculate the start coordinates
		int x = (GamePanel.WIDTH / 2) - (((size * SWITCH_DIMENSION) + ((size - 1) * SWITCH_PADDING)) / 2);
		final int y = Board.BOUNDS.bottom + (int)(dimension * 1.25);

		//render the buttons
		for (int index = 0; index < colors; index++)
		{
			//create a new switch of the specified color
			this.switches[index] = new Switch(
				Colors.values()[index],
				Images.getImage(Assets.ImageGameKey.Colors)
			);
			
			//set location
			this.switches[index].setX(x);
			this.switches[index].setY(y);
			
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