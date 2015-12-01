package com.gamesbykevin.flood.board;

import com.gamesbykevin.androidframework.base.Entity;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.flood.assets.Assets;
import com.gamesbykevin.flood.board.switches.Switch;
import com.gamesbykevin.flood.board.switches.Switches;
import com.gamesbykevin.flood.panel.GamePanel;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

public class Board extends Entity implements IBoard
{
	//the dimension of a single square on the board
	private int dimension = 0;
	
	//the start location
	private static final int START_X = 0;
	private static final int START_Y = 100;
	
	/**
	 * Create square bounds where the colors are displayed
	 */
	public static final Rect BOUNDS = new Rect(
		START_X, 
		START_Y, 
		START_X + GamePanel.WIDTH, 
		START_Y + GamePanel.WIDTH
	);
	
	/**
	 * The available colors to choose from
	 * @author GOD
	 */
	public enum Colors
	{
		Orange, Red, Blue,
		Green, Purple, Yellow
	}
	
	//the board containing our colors
	private Square[][] key;
	
	//the total number of colors
	private int total;
	
	//the current color
	private Colors current;
	
	//did we solve
	private boolean win = false;
	
	//the button switches we can click
	private Switches switches;
	
	//the maximum number of allowed attempts to resolve
	private int max = 1;
	
	//has the board been generated
	private boolean generated = false;
	
	/**
	 * Create a new board
	 */
	public Board()
	{
		//setup default animations
		Switch.setupAnimation(this, null);
		
		//create the switches container
		this.switches = new Switches();
	}
	
	/**
	 * Assign the current color
	 * @param current The current color we have flooded
	 */
	public void setCurrent(final Colors current)
	{
		this.current = current;
	}
	
	/**
	 * Get the current color
	 * @return The current color that is flooding the board
	 */
	public Colors getCurrent()
	{
		return this.current;
	}
	
	/**
	 * Get the key of the board
	 * @return The key containing all the squares on our board
	 */
	public Square[][] getKey()
	{
		return this.key;
	}
	
	@Override
	public void update(final MotionEvent event, final float x, final float y)
	{
		//don't continue if we already have the win
		if (hasWin())
			return;
		
		if (event.getAction() == MotionEvent.ACTION_UP)
			getSwitches().update(x, y);
	}
	
	@Override
	public void update()
	{
		if (hasWin())
			return;
		
		//update the switches
		getSwitches().update(this);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		if (key != null)
			key = null;
		
		if (switches != null)
		{
			switches.dispose();
			switches = null;
		}
	}
	
	/**
	 * Do we have a win?
	 * @return true if all squares have been flooded, false otherwise
	 */
	public boolean hasWin()
	{
		return this.win;
	}
	
	/**
	 * Flag win.<br>
	 * A win means all squares have been flooded
	 * @param win true if all squares have been flooded, false otherwise
	 */
	public void setWin(final boolean win)
	{
		this.win = win;
	}
	
	/**
	 * Reset the board with the specified
	 * @param size The size of the board (column, row)
	 * @param total Number of colors, if the total exceeds the # of colors the max will be assigned
	 */
	@Override
	public void reset(final int size, int total)
	{
		//flag generated false
		setGenerated(false);
		
		//flag win false
		setWin(false);
		
		//make sure we don't exceed the number of colors available
		if (total > Colors.values().length)
			total = Colors.values().length;
		
		//store the total number of colors
		this.total = total;
		
		//assign the dimension of a single square
		setDimension(BOUNDS.width() / size);
		
		//the dimensions for each square will be the same
		setWidth(getDimension());
		setHeight(getDimension());
		
		//create new key first
		this.key = new Square[size][size];
		
		//make sure we have a board that contains each of the available colors
		while (true)
		{
			//pick random color
			for (int row = 0; row < key.length; row++)
			{
				for (int col = 0; col < key[0].length; col++)
				{
					//pick random color
					final Colors color = Colors.values()[GamePanel.RANDOM.nextInt(getTotal())];
					
					//create a new square with the color
					getKey()[row][col] = new Square(color);
				}
			}
			
			//if the board has each of the unique colors for this game, we can exit loop
			if (BoardHelper.getUniqueColorCount(getKey()) >= getTotal())
				break;
		}
		
		//assign the max number of allowed attempts depending on the size and # of different colors
		setMax((int)Math.ceil(25 * ((size + size) * getTotal()) / 168) + 2);
		
		//assign the current color as the start location
		setCurrent(getKey()[0][0].getColor());
		
		//mark the start location as flooded
		getKey()[0][0].setFlooded(true);
		
		//give the neighbor squares that have a matching color the same id
		BoardHelper.groupSquares(getKey());
		
		//flood the squares
		BoardHelper.floodSquares(getKey(), getCurrent());
		
		//reset the switches
		getSwitches().reset(getTotal(), getDimension(), getCurrent());
		
		//flag generated true
		setGenerated(true);
	}
	
	/**
	 * Set the maximum amount of moves to solve the puzzle
	 * @param max The number of allowed attempts to solve
	 */
	protected void setMax(final int max)
	{
		this.max = max;
	}
	
	/**
	 * Get the max.
	 * @return The maximum amount of attempts to solve the puzzle
	 */
	public int getMax()
	{
		return this.max;
	}
	
	/**
	 * Get the number of attempts
	 * @return The current number of attempts to solve the game
	 */
	public int getAttempts()
	{
		return getSwitches().getAttempts();
	}
	
	/**
	 * Get our switches
	 * @return The switch buttons used to play the game
	 */
	private Switches getSwitches()
	{
		return this.switches;
	}
	
	/**
	 * Get the total
	 * @return The total number of colors in the current board
	 */
	private int getTotal()
	{
		return this.total;
	}
	
	/**
	 * Get the dimension of a single square on the board
	 * @return The pixel size (width/height) of a single square
	 */
	private int getDimension()
	{
		return this.dimension;
	}
	
	/**
	 * Assign the dimension of a single square on the board
	 * @param dimension The pixel size (width/height) of a single square
	 */
	private void setDimension(final int dimension)
	{
		this.dimension = dimension;
	}
	
	/**
	 * Flag the board s generated
	 * @param generated true if the board has been generated, false otherwise
	 */
	public void setGenerated(final boolean generated)
	{
		this.generated = generated;
	}
	
	/**
	 * Render the board
	 * @param canvas
	 * @throws Exception
	 */
	@Override
	public void render(final Canvas canvas) throws Exception
	{
		//if the game has not been generated render loading screen
		if (!generated)
		{
			//render loading screen
			canvas.drawBitmap(Images.getImage(Assets.ImageMenuKey.Splash), 0, 0, null);
		}
		else
		{
			//check every square
			for (int row = 0; row < getKey().length; row++)
			{
				for (int col = 0; col < getKey()[0].length; col++)
				{
					//assign coordinates
					setX(BOUNDS.left + (col * getDimension()));
					setY(BOUNDS.top + (row * getDimension()));
					
					//assign animation
					getSpritesheet().setKey(getKey()[row][col].getColor());
					
					//render the current animation
					super.render(canvas);
				}
			}
			
			//render the switches
			getSwitches().render(canvas);
		}
	}
}