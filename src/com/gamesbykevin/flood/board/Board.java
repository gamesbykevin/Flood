package com.gamesbykevin.flood.board;

import com.gamesbykevin.androidframework.base.Entity;

import android.graphics.Canvas;

public class Board extends Entity implements IBoard
{
	public Board()
	{
		//default constructor
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
	}
	
	/**
	 * Reset the board with the specified
	 * @param cols Total columns
	 * @param rows Total rows
	 * @param colors Number of colors
	 */
	@Override
	public void reset(final int cols, final int rows, final int colors)
	{
		
	}
	
	/**
	 * Render the board
	 * @param canvas
	 * @throws Exception
	 */
	@Override
	public void render(final Canvas canvas) throws Exception
	{
		
	}
}