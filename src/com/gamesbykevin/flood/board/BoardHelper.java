package com.gamesbykevin.flood.board;

import java.util.UUID;

import com.gamesbykevin.flood.board.Board.Colors;

public final class BoardHelper 
{
	/**
	 * Do we have a win?
	 * @param squares The array for all the squares
	 * @return true if every square has been flooded, false otherwise
	 */
	public static boolean hasWin(final Square[][] squares)
	{
		//check every square in our array
		for (int row = 0; row < squares.length; row++)
		{
			for (int col = 0; col < squares[0].length; col++)
			{
				//if this square is not flooded, we can't have a win
				if (!squares[row][col].isFlooded())
					return false;
			}
		}
		
		//we have a winner as all are flooded
		return true;
	}
	
	/**
	 * Flood the array of squares
	 * @param squares The array for all the squares
	 * @param color The desired flood color we are changing to
	 */
	public static void floodSquares(final Square[][] squares, final Colors color)
	{
		//assign all of the flooded squares the same color
		for (int row = 0; row < squares.length; row++)
		{
			for (int col = 0; col < squares[0].length; col++)
			{
				if (squares[row][col].isFlooded())
					squares[row][col].setColor(color);
			}
		}
		
		//check each flooded square and attempt to flood the neighbor
		for (int row = 0; row < squares.length; row++)
		{
			for (int col = 0; col < squares[0].length; col++)
			{
				//we have to start at a flooded square
				if (squares[row][col].isFlooded())
				{
					//flood the neighbors
					floodNeighbor(squares, squares[row][col], col + 1, row);
					floodNeighbor(squares, squares[row][col], col - 1, row);
					floodNeighbor(squares, squares[row][col], col, row + 1);
					floodNeighbor(squares, squares[row][col], col, row - 1);
				}
			}
		}
	}
	
	/**
	 * Floor the neighbor.<br>
	 * The neighbor will be flooded if it has not been previously
	 * @param squares The array for all the squares
	 * @param current The current square
	 * @param col The desired neighbor location (column)
	 * @param row The desired neighbor location (row)
	 */
	private static void floodNeighbor(final Square[][] squares, final Square current, final int col, final int row)
	{
		//if out of bounds we can't continue
		if (col < 0 || col >= squares[0].length)
			return;
		if (row < 0 || row >= squares.length)
			return;
		
		//get the neighbor
		Square tmp = squares[row][col];
		
		//if the neighbor has not yet been flooded and the color matches
		if (!tmp.isFlooded() && tmp.hasColor(current))
		{
			//flood the current square and all others with the same id
			floodGroup(squares, tmp.getId(), current);
		}
	}
	
	/**
	 * Flood the group of squares
	 * @param squares The array for all the squares
	 * @param id The id of all the squares we want to change
	 * @param current The square we want to change the matching id squares to
	 */
	private static void floodGroup(final Square[][] squares, final UUID id, final Square current)
	{
		for (int row = 0; row < squares.length; row++)
		{
			for (int col = 0; col < squares[0].length; col++)
			{
				//get the current square
				Square square = squares[row][col]; 
				
				//if the square has the same id
				if (square.hasId(id))
				{
					//flag as flooded
					square.setFlooded(true);
					
					//change the color
					square.setColor(current.getColor());
					
					//it will belong to the new group
					square.setId(current);
				}
			}
		}
	}
	
	/**
	 * Group the matching color squares.<br>
	 * Here we will check each square and every neighbor (N, S, E, W) with a matching color will be assigned the same id
	 * This will help when flooding similar colors that are grouped together
	 * @param squares The desired array of squares to check
	 */
	protected static void groupSquares(final Square[][] squares)
	{
		//keep track when we are finished
		boolean complete = false;
		
		//continue to loop until finished
		while (!complete)
		{
			//flag true until we find otherwise
			complete = true;
			
			for (int row = 0; row < squares.length; row++)
			{
				for (int col = 0; col < squares[0].length; col++)
				{
					//get the current square
					Square square = squares[row][col];
					
					//if we were able to join any neighbor, flag complete false
					if (join(squares, square, col - 1, row))
						complete = false;
					if (join(squares, square, col + 1, row))
						complete = false;
					if (join(squares, square, col, row - 1))
						complete = false;
					if (join(squares, square, col, row + 1))
						complete = false;
				}
			}
		}
	}
	
	/**
	 * Join the square with the specified neighbor
	 * @param squares The array of all squares
	 * @param square The square we want to join
	 * @param col Column of the neighboring square we want to join
	 * @param row Row of the neighboring square we want to join
	 * @param cols Total # of columns
	 * @param rows Total # of rows
	 * @return true if the square and neighbor are joined, false otherwise
	 */
	private static boolean join(final Square[][] squares, final Square square, final int col, final int row)
	{
		//if out of bounds we can't join, so return false
		if (col < 0 || col >= squares[0].length)
			return false;
		if (row < 0 || row >= squares.length)
			return false;
		
		//get the neighbor
		Square tmp = squares[row][col];
		
		//if the color matches, but the id's do not we will need to join
		if (tmp.hasColor(square) && !tmp.hasId(square))
		{
			//get the count for each
			final int count1 = getCount(squares, tmp);
			final int count2 = getCount(squares, square);
			
			//we want to merge with the greater count
			if (count2 > count1)
			{
				tmp.setId(square);
			}
			else
			{
				square.setId(tmp);
			}
			
			//the squares have been joined, return true
			return true;
		}
		else
		{
			//either the color matches or already has a matching id so no join has happened
			return false;
		}
	}
	
	/**
	 * Get the count.
	 * @param squares Array of squares to check
	 * @param square The square containing the id we want to count
	 * @return The total number of squares with the same id
	 */
	private static int getCount(final Square[][] squares, final Square square)
	{
		//keep track of the count
		int count = 0;
		
		//check each square
		for (int row = 0; row < squares.length; row++)
		{
			for (int col = 0; col < squares[0].length; col++)
			{
				//if the squares have the same id, increase our count
				if (square.hasId(squares[row][col]))
					count++;
			}
		}
		
		//return our result
		return count;
	}
}