package com.gamesbykevin.flood.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.MotionEvent;

import com.gamesbykevin.androidframework.awt.Button;
import com.gamesbykevin.androidframework.level.Select;
import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.flood.assets.Assets;
import com.gamesbykevin.flood.board.Board;
import com.gamesbykevin.flood.game.controller.Controller;
import com.gamesbykevin.flood.number.Number;
import com.gamesbykevin.flood.panel.GamePanel;
import com.gamesbykevin.flood.screen.GameoverScreen;
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
    
    //has the player been notified (has the user seen the loading screen)
    private boolean notify = false;
    
    //our object reference for the number of remaining attempts
    private Number number;
    
    /**
     * Default starting size
     */
    public static final int DEFAULT_DIMENSION = 5;
    
    /**
     * The x-coordinate where we display the attempts
     */
    public static final int ATTEMPT_X = 320;
    
    /**
     * The y-coordinate where we display the attempts
     */
    public static final int ATTEMPT_Y = 20;
    
    /**
     * The length to vibrate the phone when you beat a level
     */
    private static final long VIBRATION_DURATION = 500;
    
    //our level select object
    private Select levelSelect;
    
    //the game score card
    private ScoreCard scoreCard;
    
    //level select information
    private static final int LEVEL_SELECT_COLS = 4;
    private static final int LEVEL_SELECT_ROWS = 5;
    private static final int LEVEL_SELECT_DIMENSION = 96;
    private static final int LEVEL_SELECT_PADDING = 25;
    private static final int LEVEL_SELECT_START_X = (GamePanel.WIDTH / 2) - (((LEVEL_SELECT_COLS * LEVEL_SELECT_DIMENSION) + ((LEVEL_SELECT_COLS - 1) * LEVEL_SELECT_PADDING)) / 2);
    private static final int LEVEL_SELECT_START_Y = 25;
    private static final int LEVEL_SELECT_TOTAL = 100;
    
    //are we starting for the first time?
    private boolean start = true;
    
    /**
     * How much we darken the background when we render the hint text
     */
    private static final int HINT_TEXT_ALPHA_BACKGROUND = 210;
    
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
        this.levelSelect.setButtonLocked(new Button(Images.getImage(Assets.ImageGameKey.LevelLocked)));
        this.levelSelect.setButtonPrevious(new Button(Images.getImage(Assets.ImageGameKey.PagePrevious)));
        this.levelSelect.setButtonSolved(new Button(Images.getImage(Assets.ImageGameKey.LevelComplete)));
        this.levelSelect.setCols(LEVEL_SELECT_COLS);
        this.levelSelect.setRows(LEVEL_SELECT_ROWS);
        this.levelSelect.setDimension(LEVEL_SELECT_DIMENSION);
        this.levelSelect.setPadding(LEVEL_SELECT_PADDING);
        this.levelSelect.setStartX(LEVEL_SELECT_START_X);
        this.levelSelect.setStartY(LEVEL_SELECT_START_Y);
        this.levelSelect.setTotal(LEVEL_SELECT_TOTAL);

        //create our score card
        this.scoreCard = new ScoreCard(this, screen.getPanel().getActivity());
        
        //the object to render the remaining attempts
        this.number = new Number();
    }
    
    /**
     * Get the number
     * @return The number object used to render numbers
     */
    public Number getNumber()
    {
    	return this.number;
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
    	
    	//update the level select to mark the completed level
    	updateLevelSelect();
        
        //flag board generated false
        getBoard().setGenerated(false);
    }
    
    /**
     * Update the level select object to flag completed levels and locked levels
     */
    private void updateLevelSelect()
    {
    	//flag start true to start
    	this.start = true;
    	
        //load the saved data
        for (int levelIndex = getLevelSelect().getTotal() - 1; levelIndex >= 0; levelIndex--)
        {
        	//get the score for the specified level and colors
        	Score score = getScorecard().getScore(levelIndex, screen.getScreenOptions().getIndex(OptionsScreen.INDEX_BUTTON_COLORS));
        	
        	//if a score exists
        	if (score != null)
        	{
        		//we have started previous
        		this.start = false;
        		
        		//mark this level as completed
        		getLevelSelect().setCompleted(levelIndex, true);
        		
        		//mark this level as not locked
        		getLevelSelect().setLocked(levelIndex, false);
        		
        		//also make sure the next level is not locked as well
        		if (levelIndex < getLevelSelect().getTotal() - 1)
        			getLevelSelect().setLocked(levelIndex + 1, false);
        	}
        	else
        	{
        		//mark this level as locked
        		getLevelSelect().setLocked(levelIndex, true);
        		
        		//mark this level as not completed
        		getLevelSelect().setCompleted(levelIndex, false);
        	}
        }
        
    	//the first level can never be locked
    	getLevelSelect().setLocked(0, false);
    }
    
    /**
     * Flag reset, we also will flag notify to false if reset is true
     * @param reset true to reset the game, false otherwise
     */
    private void setReset(final boolean reset)
    {
    	this.reset = reset;
    	
    	//flag that the user has not been notified, since we are resetting
    	if (hasReset())
    		this.notify = false;
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
    public void update(final int action, final float x, final float y) throws Exception
    {
    	//if we don't have a selection
    	if (!getLevelSelect().hasSelection())
    	{
    		//if action up, check the location
    		if (action == MotionEvent.ACTION_UP)
    			getLevelSelect().setCheck((int)x, (int)y);
    		
    		//don't continue
    		return;
    	}
    	
    	//if reset we can't continue
    	if (hasReset())
    		return;
    	
    	//make sure the board is generated before interacting
    	if (getBoard().isGenerated())
    	{
    		//make sure we aren't starting
    		if (!this.start)
    		{
		        //update the following
		        if (getController() != null)
		        	getController().update(action, x, y);
		        if (getBoard() != null)
		        	getBoard().update(action, x, y);
    		}
	        
	        //flag start false
    		if (action == MotionEvent.ACTION_UP)
    			this.start = false;
    	}
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
    		{
    			//make sure the level is not locked, if it is locked play sound effect
    			if (getLevelSelect().isLocked(getLevelSelect().getLevelIndex()))
    			{
    				//flag selection as false
    				getLevelSelect().setSelection(false);
    				
    				//play sound effect
    				Audio.play(Assets.AudioGameKey.InvalidLevelSelect);
    			}
    			else
    			{
    				//reset the board for the next level
    				reset();
    			}
    		}
    		
    		//no need to continue
    		return;
    	}
    	
        //if we are to reset the game
        if (hasReset())
        {
        	//make sure we have notified first
        	if (notify)
        	{
	        	//flag reset false
	        	setReset(false);
	        	
	        	//reset controller
	        	if (getController() != null)
	        		getController().reset();
	        	
	        	//the number of different colors
	        	final int colors;
	        	
	        	//determine the number of colors used
	        	switch (getScreen().getScreenOptions().getIndex(OptionsScreen.INDEX_BUTTON_COLORS))
	        	{
		        	case 0:
		        	default:
		        		colors = 6;
		        		break;
		        		
		        	case 1:
		        		colors = 3;
		        		break;
		        		
		        	case 2:
		        		colors = 4;
		        		break;
		        		
		        	case 3:
		        		colors = 5;
		        		break;
	        	}
	        	
	        	//reset with the specified size and colors
	    		getBoard().reset(
					getLevelSelect().getLevelIndex() + DEFAULT_DIMENSION, 
	    			colors
	    		);
	    		
	    		//update the number to be displayed here
				getNumber().setNumber(
					getBoard().getMax() - getBoard().getAttempts(), 
					ATTEMPT_X,
					ATTEMPT_Y
				);
        	}
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
        			getScreen().getScreenGameover().setMessage("You Lose!", GameoverScreen.BUTTON_TEXT_REPLAY);
        			
            		//go to game over state
            		getScreen().setState(State.GameOver);
            		
            		//play sound effect
            		Audio.play(Assets.AudioGameKey.Lose);
        		}
        		else
        		{
		    		//store the # of attempts
		    		final int attempts = getBoard().getAttempts();
		    		
		        	//update the game elements
		        	if (getController() != null)
		        		getController().update();
		        	if (getBoard() != null)
		        		getBoard().update();
		    		
			        //if the number of attempts has changed
			        if (attempts != getBoard().getAttempts())
			        {
						//assign the appropriate number to render
						getNumber().setNumber(
							getBoard().getMax() - getBoard().getAttempts(), 
							ATTEMPT_X,
							ATTEMPT_Y
						);
			        }
        		}
        	}
        	else
        	{
        		//assign win message
    			getScreen().getScreenGameover().setMessage("Congratulations", GameoverScreen.BUTTON_TEXT_NEW_GAME);
    			
    			//save the result
    			getScorecard().update(
    				getLevelSelect().getLevelIndex(),
    				getScreen().getScreenOptions().getIndex(OptionsScreen.INDEX_BUTTON_COLORS)
    			);
    			
        		//go to game over state
        		getScreen().setState(State.GameOver);
        		
        		//play sound effect
        		Audio.play(Assets.AudioGameKey.Win);
        		
        		//make sure vibrate is enabled
        		if (getScreen().getScreenOptions().getIndex(OptionsScreen.INDEX_BUTTON_VIBRATE) == 0)
        		{
	        		//get our vibrate object
	        		Vibrator v = (Vibrator) getScreen().getPanel().getActivity().getSystemService(Context.VIBRATOR_SERVICE);
	        		 
					//vibrate for a specified amount of milliseconds
					v.vibrate(VIBRATION_DURATION);
        		}
        		
        		//update level select screen
        		updateLevelSelect();
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
    		//only render the info and controller if the board has been created
    		if (getBoard().isGenerated())
    		{
    			//render the board
    			getBoard().render(canvas);
    			
		    	//render the controller
		    	if (getController() != null)
		    		getController().render(canvas);
		    	
		    	//if we are just starting, render our message
		    	if (this.start)
				{
					//darken the background
					ScreenManager.darkenBackground(canvas, HINT_TEXT_ALPHA_BACKGROUND);
					
					//render the helper text instructions
					canvas.drawBitmap(Images.getImage(Assets.ImageGameKey.Message), 0, 0, null);
				}
		    	
    			//render the switches if possible
    			if (getBoard().canRenderSwitches())
    				getBoard().getSwitches().render(canvas);
    			
				//render the assigned number
				getNumber().render(canvas);
    		}
    		else
    		{
    			//render loading screen
    			canvas.drawBitmap(Images.getImage(Assets.ImageMenuKey.Splash), 0, 0, null);
    			
    			//flag that the user has been notified
    			notify = true;
    		}
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
        
        if (number != null)
        {
        	number.dispose();
        	number = null;
        }
    }
}