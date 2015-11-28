package com.gamesbykevin.flood.board;

import com.gamesbykevin.androidframework.resources.Disposable;

import android.graphics.Canvas;

public interface IBoard extends Disposable
{
	/**
	 * Reset the board with the specified
	 * @param cols Total columns
	 * @param rows Total rows
	 * @param colors Number of colors
	 */
	public void reset(final int cols, final int rows, final int colors);
	
	/**
	 * Render the board
	 * @param canvas
	 * @throws Exception
	 */
	public void render(final Canvas canvas) throws Exception;
}
