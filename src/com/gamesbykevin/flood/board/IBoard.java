package com.gamesbykevin.flood.board;

import com.gamesbykevin.androidframework.resources.Disposable;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface IBoard extends Disposable
{
	/**
	 * Update the board based on the motion event
	 * @param event The motion event
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void update(final MotionEvent event, final float x, final float y);
	
	/**
	 * Update common elements
	 */
	public void update();
	
	/**
	 * Reset the board with the specified
	 * @param size The size of the board (column, row)
	 * @param colors Number of colors
	 */
	public void reset(final int size, final int colors);
	
	/**
	 * Render the board
	 * @param canvas
	 * @throws Exception
	 */
	public void render(final Canvas canvas) throws Exception;
}
