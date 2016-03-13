package com.gamesbykevin.flood.board;

import java.util.ArrayList;
import java.util.List;
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
	 * The neighbor will be flooded if it has not been previously and the colors match
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
				
				//make sure the squre has the matching group
				if (square.hasId(id))
				{
					//flood the single square
					floodSquare(current, square);
				}
			}
		}
	}
	
	/**
	 * Flood a single square
	 * @param parent The square we are to copy from
	 * @param child The desired square we want to flood
	 */
	public static void floodSquare(final Square parent, final Square child)
	{
		//flag child as flooded
		child.setFlooded(true);
		
		//change the color of the child to match the parent
		child.setColor(parent.getColor());
		
		//the child will belong to the parent group
		child.setId(parent);
		
	}
	
	/**
	 * Group the matching color squares.<br>
	 * Here we will check each square and every neighbor (N, S, E, W) with a matching color will be assigned the same id
	 * This will help when flooding similar colors as they will be grouped together
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
					
					//if we were able to join a neighbor, flag complete as false
					if (join(squares, square, col - 1, row))
						complete = false;
					if (join(squares, square, col, row - 1))
						complete = false;
				}
			}
		}
	}
	
	/**
	 * Join the square with the specified neighbor.<br>
	 * Here we will make the color matching neighbor have the same id
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
	 * Get the count.<br>
	 * We will count the number of squares that have a matching id.
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
	
	/**
	 * Count the number of unique colors.
	 * @param squares Array of squares to check
	 * @return The total number of different colors found
	 */
	protected static int getUniqueColorCount(final Square[][] squares)
	{
		//list of colors we found
		List<Colors> colors = new ArrayList<Colors>();
		
		for (int row = 0; row < squares.length; row++)
		{
			for (int col = 0; col < squares[0].length; col++)
			{
				Square square = squares[row][col];
				
				if (colors.isEmpty())
				{
					colors.add(square.getColor());
				}
				else
				{
					//does the color already exist in our list
					boolean match = false;
					
					for (int index = 0; index < colors.size(); index++)
					{
						if (colors.get(index) == square.getColor())
						{
							//flag exists
							match = true;
							
							//exit loop
							break;
						}
					}
					
					//if no match add it to the list
					if (!match)
						colors.add(square.getColor());
				}
			}
		}
		
		//return the number of colors we found
		return colors.size();
	}
}