package com.gamesbykevin.flood.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.gamesbykevin.androidframework.awt.Button;
import com.gamesbykevin.androidframework.level.Select;
import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.flood.assets.Assets;
import com.gamesbykevin.flood.board.Board;
import com.gamesbykevin.flood.game.controller.Controller;
import com.gamesbykevin.flood.screen.OptionsScreen;
import com.gamesbykevin.flood.screen.ScreenManager;
import com.gamesbykevin.flood.screen.ScreenManager.State;
import com.gamesbykevin.flood.scorecard.ScoreCard;
import com.gamesbykevin.flood.scorecard.Score;

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
     * Default starting size
     */
    public static final int DEFAULT_DIMENSION = 5;
    
    //the location where we display the attempts
    private static final int ATTEMPT_X = 325;
    private static final int ATTEMPT_Y = 75;
    
    //our level select object
    private Select levelSelect;
    
    //the game score card
    private ScoreCard scoreCard;
    
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
        this.paint.setTextSize(24f);
        this.paint.setColor(Color.WHITE);
        this.paint.setLinearText(false);
        
        //create new controller
        this.controller = new Controller(this);
        
        //create a new board
        this.board = new Board();
        
        //create the level select screen
        this.levelSelect = new Select();
        this.levelSelect.setButtonNext(new Button(Images.getImage(Assets.ImageGameKey.PageNext)));
        this.levelSelect.setButtonOpen(new Button(Images.getImage(Assets.ImageGameKey.LevelOpen)));
        this.levelSelect.setButtonPrevious(new Button(Images.getImage(Assets.ImageGameKey.PagePrevious)));
        this.levelSelect.setButtonSolved(new Button(Images.getImage(Assets.ImageGameKey.LevelComplete)));
        this.levelSelect.setCols(3);
        this.levelSelect.setRows(4);
        this.levelSelect.setDimension(120);
        this.levelSelect.setPadding(30);
        this.levelSelect.setStartX(30);
        this.levelSelect.setStartY(25);
        this.levelSelect.setTotal(26);
        
        //create our score card
        this.scoreCard = new ScoreCard(this, screen.getPanel().getActivity());
    }
    
    /**
     * Get the score card
     * @return Our list of completed levels for each color setting
     */
    public ScoreCard getScorecard()
    {
    	return this.scoreCard;
    }
    
    /**
     * Get the level select
     * @return The level select object
     */
    public Select getLevelSelect()
    {
    	return this.levelSelect;
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
    	
        //load the saved data
        for (int levelIndex = 0; levelIndex < getLevelSelect().getTotal(); levelIndex++)
        {
        	//get the score for the specified level and colors
        	Score score = getScorecard().getScore(levelIndex, screen.getScreenOptions().getIndex(OptionsScreen.INDEX_BUTTON_COLORS));
        	
        	//mark completed if the score object exists
        	getLevelSelect().setCompleted(levelIndex, (score != null));
        }
        
        //flag board generated false
        getBoard().setGenerated(false);
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
    	//if we don't have a selection
    	if (!getLevelSelect().hasSelection())
    	{
    		//if action up, check the location
    		if (event.getAction() == MotionEvent.ACTION_UP)
    			getLevelSelect().setCheck((int)x, (int)y);
    		
    		//don't continue
    		return;
    	}
    	
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
    	if (!getLevelSelect().hasSelection())
    	{
    		//update the object
    		getLevelSelect().update();
    		
    		//if we have a selection now, reset the board
    		if (getLevelSelect().hasSelection())
    			reset();
    		
    		//no need to continue
    		return;
    	}
    	
        //if we are to reset the game
        if (hasReset())
        {
        	//flag reset false
        	setReset(false);
        	
        	//reset controller
        	if (getController() != null)
        		getController().reset();
        	
        	//reset with the specified size and colors
    		getBoard().reset(
				getLevelSelect().getLevelIndex() + DEFAULT_DIMENSION, 
    			getScreen().getScreenOptions().getIndex(OptionsScreen.INDEX_BUTTON_COLORS) + 3
    		);
        }
        else
        {
        	//don't update if we don't have the win
        	if (!getBoard().hasWin())
        	{
        		//if we reached the number of allowed attempts
        		if (getBoard().getAttempts() >= getBoard().getMax())
        		{
        			//set losing message
        			getScreen().getScreenGameover().setMessage("No more attempts");
        			
            		//go to game over state
            		getScreen().setState(State.GameOver);
            		
            		//play sound effect
            		Audio.play(Assets.AudioGameKey.Lose);
        		}
        		else
        		{
		        	//update the game elements
		        	if (getController() != null)
		        		getController().update();
		        	if (getBoard() != null)
		        		getBoard().update();
        		}
        	}
        	else
        	{
        		//assign win message
    			getScreen().getScreenGameover().setMessage("Congratulations");
    			
    			//save the result
    			getScorecard().update(
    				getLevelSelect().getLevelIndex(),
    				getScreen().getScreenOptions().getIndex(OptionsScreen.INDEX_BUTTON_COLORS)
    			);
    			
        		//go to game over state
        		getScreen().setState(State.GameOver);
        		
        		//play sound effect
        		Audio.play(Assets.AudioGameKey.Win);
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
    	
    	if (!getLevelSelect().hasSelection())
    	{
    		//render level select screen
    		getLevelSelect().render(canvas, this.paint);
    		
    		//no need to continue
    		return;
    	}
    	
    	//render the board and switches
    	if (getBoard() != null)
    	{
    		getBoard().render(canvas);
    	
	    	//render the controller
	    	if (getController() != null)
	    		getController().render(canvas);
	    	
	    	//render the number of remaining attempts
	    	canvas.drawText(
	    		"Remaining - " + (getBoard().getMax() - getBoard().getAttempts()), 
	    		ATTEMPT_X, 
	    		ATTEMPT_Y, 
	    		getScreen().getPaint()
	    	);
    	}
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
        
        if (board != null)
        {
        	board.dispose();
        	board = null;
        }
        
        if (levelSelect != null)
        {
        	levelSelect.dispose();
        	levelSelect = null;
        }
        
        if (scoreCard != null)
        {
        	scoreCard.dispose();
        	scoreCard = null;
        }
    }
}