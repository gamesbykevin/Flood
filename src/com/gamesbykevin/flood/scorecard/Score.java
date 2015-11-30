package com.gamesbykevin.flood.scorecard;

/**
 * The score for a level
 * @author GOD
 */
public final class Score 
{
    //the level this score is for
    private final int level;
    
    //the number of colors in the level
    private final int colors;
    
    protected Score(final int level, final int colors)
    {
    	//assign default values
    	this.level = level;
    	this.colors = colors;
    }
    
    /**
     * Get the level index
     * @return The level this score is for
     */
    public int getLevel()
    {
        return this.level;
    }
    
    /**
     * Get the colors
     * @return The number of colors for this score
     */
    public int getColors()
    {
        return this.colors;
    }
}