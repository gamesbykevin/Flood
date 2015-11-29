package com.gamesbykevin.flood.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.gamesbykevin.flood.board.Board;
import com.gamesbykevin.flood.game.controller.Controller;
import com.gamesbykevin.flood.screen.ScreenManager;
import com.gamesbykevin.flood.screen.ScreenManager.State;

/**
 * The main game logic will happen here
 * @author ABRAHAM
 */
public final class Game implements IGame
{
    //our main screen object reference
    private final ScreenManager screen;
    
    //paint object to draw text
    private Paint paint;
    
    //our controller object
    private Controller controller;
    
    //the board of play
    private Board board;
    
    //is the game being reset
    private boolean reset = false;
    
    /**
     * Create our game object
     * @param screen The main screen
     * @throws Exception
     */
    public Game(final ScreenManager screen) throws Exception
    {
        //our main screen object reference
        this.screen = screen;
        
        //create new paint object
        this.paint = new Paint();
        this.paint.setTextSize(16f);
        this.paint.setColor(Color.WHITE);
        this.paint.setLinearText(false);
        
        //create new controller
        this.controller = new Controller(this);
        
        //create a new board
        this.board = new Board();
    }
    
    /**
     * Get the main screen object reference
     * @return The main screen object reference
     */
    public ScreenManager getScreen()
    {
        return this.screen;
    }
    
    /**
     * Get the board
     * @return The board reference object
     */
    public Board getBoard()
    {
    	return this.board;
    }
    
    /**
     * Get the controller object
     * @return The controller object reference
     */
    public Controller getController()
    {
    	return this.controller;
    }
    
    @Override
    public void reset() throws Exception
    {
        //flag reset
    	setReset(true);
    }
    
    /**
     * Flag reset
     * @param reset true to reset the game, false otherwise
     */
    private void setReset(final boolean reset)
    {
    	this.reset = reset;
    }
    
    /**
     * Do we have reset flagged?
     * @return true = yes, false = no
     */
    protected boolean hasReset()
    {
    	return this.reset;
    }
    
    /**
     * Get the paint object
     * @return The paint object used to draw text in the game
     */
    public Paint getPaint()
    {
        return this.paint;
    }
    
    /**
     * Update the game based on a motion event
     * @param event Motion Event
     * @param x (x-coordinate)
     * @param y (y-coordinate)
     * @throws Exception
     */
    public void update(final MotionEvent event, final float x, final float y) throws Exception
    {
    	//if reset we can't continue
    	if (hasReset())
    		return;
    	
        //update the following
        if (getController() != null)
        	getController().update(event, x, y);
        if (getBoard() != null)
        	getBoard().update(event, x, y);
    }
    
    /**
     * Update game
     * @throws Exception 
     */
    public void update() throws Exception
    {
        //if we are to reset the game
        if (hasReset())
        {
        	//flag reset false
        	setReset(false);
        	
        	//reset controller
        	if (getController() != null)
        		getController().reset();
        	
        	//reset board
        	if (getBoard() != null)
        		getBoard().reset(10, 3);
        }
        else
        {
        	//don't update if we don't have the win
        	if (!getBoard().hasWin())
        	{
	        	//update the game elements
	        	if (getController() != null)
	        		getController().update();
	        	if (getBoard() != null)
	        		getBoard().update();
        	}
        	else
        	{
        		//go to game over state
        		getScreen().setState(State.GameOver);
        	}
        }
    }
    
    /**
     * Render game elements
     * @param canvas Where to write the pixel data
     * @throws Exception 
     */
    @Override
    public void render(final Canvas canvas) throws Exception
    {
    	//darken background
    	ScreenManager.darkenBackground(canvas);
    	
    	if (getBoard() != null)
    		getBoard().render(canvas);
    	if (getController() != null)
    		getController().render(canvas);
    }
    
    @Override
    public void dispose()
    {
        paint = null;
        
        if (controller != null)
        {
            controller.dispose();
            controller = null;
        }
    }
}