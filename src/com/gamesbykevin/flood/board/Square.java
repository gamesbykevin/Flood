package com.gamesbykevin.flood.board;

import java.util.UUID;

import com.gamesbykevin.flood.board.Board.Colors;;

/**
 * A square is a small part of a board
 * @author GOD
 *
 */
public final class Square 
{
	//the color reference of a single square
	private Colors color;
	
	//is this square flooded
	private boolean flooded = false;
	
	//the id of the square
	private UUID id = UUID.randomUUID();
	
	protected Square(Colors color)
	{
		setColor(color);
	}
	
	/**
	 * Assign the color
	 * @param color The desired color of the square
	 */
	protected final void setColor(final Colors color)
	{
		this.color = color;
	}
	
	/**
	 * Do we have the specified color?
	 * @param square The square containing the color we want to check
	 * @return true if the color matches, false otherwise
	 */
	protected boolean hasColor(final Square square)
	{
		return hasColor(square.getColor());
	}
	
	/**
	 * Do we have the specified color?
	 * @param color The color we want to check
	 * @return true if the color matches, false otherwise
	 */
	protected boolean hasColor(final Colors color)
	{
		return (this.color == color);
	}
	
	/**
	 * Get the current color
	 * @return The current assigned color
	 */
	protected Colors getColor()
	{
		return this.color;
	}
	
	/**
	 * Get the id
	 * @return Squares that are part of the same group have the same id
	 */
	protected UUID getId()
	{
		return this.id;
	}
	
	/**
	 * Assign the id
	 * @param id Value we want to assign
	 */
	protected void setId(final UUID id)
	{
		this.id = id;
	}
	
	/**
	 * Assign the id
	 * @param square Square containing id we want to assign
	 */
	protected void setId(final Square square)
	{
		setId(square.getId());
	}
	
	/**
	 * Do we have the matching id?
	 * @param square The square containing the id we want to check
	 * @return true if the id matches, false otherwise
	 */
	protected boolean hasId(final Square square)
	{
		return (hasId(square.getId()));
	}
	
	/**
	 * Do we have the matching id?
	 * @param id The id we want to check against this square
	 * @return true if the id matches, false otherwise
	 */
	protected boolean hasId(final UUID id)
	{
		return (getId().equals(id));
	}
	
	/**
	 * Is this square flooded?
	 * @return true = yes, false = no
	 */
	protected boolean isFlooded()
	{
		return this.flooded;
	}
	
	/**
	 * Mark the square flooded
	 * @param flooded true = yes, false = no
	 */
	protected void setFlooded(final boolean flooded)
	{
		this.flooded = flooded;
	}
}