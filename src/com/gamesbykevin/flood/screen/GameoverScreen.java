package com.gamesbykevin.flood.screen;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.gamesbykevin.androidframework.awt.Button;
import com.gamesbykevin.androidframework.resources.Audio;
import com.gamesbykevin.androidframework.resources.Disposable;
import com.gamesbykevin.androidframework.resources.Font;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.androidframework.screen.Screen;
import com.gamesbykevin.flood.MainActivity;
import com.gamesbykevin.flood.assets.Assets;
import com.gamesbykevin.flood.panel.GamePanel;

/**
 * The game over screen
 * @author GOD
 */
public class GameoverScreen implements Screen, Disposable
{
    //our main screen reference
    private final ScreenManager screen;
    
    //object to paint message
    private Paint paint;
    
    //the message to display
    private String message = "";
    
    //where we draw the image
    private int messageX = 0, messageY = 0;
    
    //time we have displayed text
    private long time;
    
    /**
     * The amount of time to wait until we render the game over menu
     */
    private static final long DELAY_MENU_DISPLAY = 1250L;
    
    //do we display the menu
    private boolean display = false;
    
    /**
     * The text to display for the new game
     */
    public static final String BUTTON_TEXT_NEW_GAME = "Next";
    
    /**
     * The text to display to retry
     */
    public static final String BUTTON_TEXT_REPLAY = "Retry";
    
    /**
     * The text to display for the menu
     */
    private static final String BUTTON_TEXT_MENU = "Menu";
    
    /**
     * The text to display for the menu
     */
    private static final String BUTTON_TEXT_LEVEL_SELECT = "Level";
    
    //list of buttons
    private SparseArray<Button> buttons;
    
    //buttons to access each button list
    public static final int INDEX_BUTTON_NEXT = 0;
    public static final int INDEX_BUTTON_MENU = 1;
    public static final int INDEX_BUTTON_RATE = 2;
    public static final int INDEX_BUTTON_LEVEL_SELECT = 3;
    
    public GameoverScreen(final ScreenManager screen)
    {
        //store our parent reference
        this.screen = screen;
        
        //create buttons hash map
        this.buttons = new SparseArray<Button>();
        
        //the start location of the button
        int y = ScreenManager.BUTTON_Y;
        int x = ScreenManager.BUTTON_X;

        //create our buttons
        addButton(x, y, INDEX_BUTTON_NEXT, BUTTON_TEXT_NEW_GAME);
        
        y += ScreenManager.BUTTON_Y_INCREMENT;
        addButton(x, y, INDEX_BUTTON_LEVEL_SELECT, BUTTON_TEXT_LEVEL_SELECT);
        
        y += ScreenManager.BUTTON_Y_INCREMENT;
        addButton(x, y, INDEX_BUTTON_MENU, BUTTON_TEXT_MENU);
        
        y += ScreenManager.BUTTON_Y_INCREMENT;
        addButton(x, y, INDEX_BUTTON_RATE, MenuScreen.BUTTON_TEXT_RATE_APP);
    }
    
    /**
     * Add button to our list
     * @param x desired x-coordinate
     * @param y desired y-coordinate
     * @param index Position to place in our array list
     * @param description The text description to add
     */
    private void addButton(final int x, final int y, final int index, final String description)
    {
    	//create new button
    	Button button = new Button(Images.getImage(Assets.ImageMenuKey.Button));
    	
    	//position the button
    	button.setX(x);
    	button.setY(y);
    	
    	//assign the dimensions
    	button.setWidth(MenuScreen.BUTTON_WIDTH);
    	button.setHeight(MenuScreen.BUTTON_HEIGHT);
    	button.updateBounds();
    	
    	//add the text description
    	button.addDescription(description);
    	button.positionText(screen.getPaint());
    	
    	//add button to the list
    	this.buttons.put(index, button);
    }
    
    /**
     * Reset any necessary screen elements here
     */
    @Override
    public void reset()
    {
        //reset timer
        time = System.currentTimeMillis();
        
        //do we display the menu
        setDisplay(false);
    }
    
    /**
     * Assign the message
     * @param message The message we want displayed
     * @param buttonText the button text to display
     */
    public void setMessage(final String message, final String buttonText)
    {
        //assign the message
        this.message = message;
        
        //update button text
        this.buttons.get(INDEX_BUTTON_NEXT).setDescription(0, buttonText);
        
        //align button text
        this.buttons.get(INDEX_BUTTON_NEXT).positionText(screen.getPaint());
        
        //create temporary rectangle
        Rect tmp = new Rect();
        
        //create paint text object for the message
        if (paint == null)
        {
	        //assign metrics
        	paint = new Paint();
        	paint.setColor(Color.WHITE);
        	paint.setTextSize(64f);
	        paint.setTypeface(Font.getFont(Assets.FontGameKey.Default));
        }
        
        //get the rectangle around the message
        paint.getTextBounds(message, 0, message.length(), tmp);
        
        //calculate the position of the message
        messageX = (GamePanel.WIDTH / 2) - (tmp.width() / 2);
        messageY = (int)(GamePanel.HEIGHT * .12);
    }
    
    /**
     * Flag display
     * @param display true if we want to display the buttons, false otherwise
     */
    private void setDisplay(final boolean display)
    {
    	this.display = display;
    }
    
    /**
     * Do we display the buttons?
     * @return true = yes, false = no
     */
    private boolean hasDisplay()
    {
    	return this.display;
    }
    
    @Override
    public boolean update(final int action, final float x, final float y) throws Exception
    {
        //if we aren't displaying the menu, return false
        if (!hasDisplay())
            return false;
        
        if (action == MotionEvent.ACTION_UP)
        {
        	for (int index = 0; index < buttons.size(); index++)
        	{
        		//get the current button
        		Button button = buttons.get(index);
        		
        		//if we did not click this button skip to the next
        		if (!button.contains(x, y))
        			continue;
        		
                //remove message
                setMessage("", "");
                
        		//handle each button different
        		switch (index)
        		{
	        		case INDEX_BUTTON_NEXT:
	                    
	        			//if we have the win this button will behave differently
	        			if (screen.getScreenGame().getGame().getBoard().hasWin())
	        			{
		                    //move to the next level
		                    screen.getScreenGame().getGame().getLevelSelect().setLevelIndex(
		                    	screen.getScreenGame().getGame().getLevelSelect().getLevelIndex() + 1
		                    );
		                    
		                    //reset with the same settings
		                    screen.getScreenGame().getGame().reset();
		                    
		                    //move back to the game
		                    screen.setState(ScreenManager.State.Running);
		                    
		                    //play sound effect
		                    Audio.play(Assets.AudioMenuKey.Selection);
	        			}
	        			else
	        			{
		                    //reset with the same settings
		                    screen.getScreenGame().getGame().reset();
		                    
		                    //move back to the game
		                    screen.setState(ScreenManager.State.Running);
		                    
		                    //play sound effect
		                    Audio.play(Assets.AudioMenuKey.Selection);
	        			}
	                    
	                    //we don't request additional motion events
	                    return false;

	        		case INDEX_BUTTON_LEVEL_SELECT:
	                    
	                    //flag no level selection
	                    screen.getScreenGame().getGame().getLevelSelect().setSelection(false);
	                    
	                    //move back to the game
	                    screen.setState(ScreenManager.State.Running);
	                    
	                    //play sound effect
	                    Audio.play(Assets.AudioMenuKey.Selection);
	                    
	                    //we don't request additional motion events
	                    return false;
	                    
	        		case INDEX_BUTTON_MENU:
	                    
	                    //move to the main menu
	                    screen.setState(ScreenManager.State.Ready);
	                    
	                    //play sound effect
	                    Audio.play(Assets.AudioMenuKey.Selection);
	                    
	                    //we don't request additional motion events
	                    return false;
	        			
	        		case INDEX_BUTTON_RATE:
	                    
	                    //play sound effect
	                    Audio.play(Assets.AudioMenuKey.Selection);
	                    
	                    //go to rate game page
	                    screen.getPanel().getActivity().openWebpage(MainActivity.WEBPAGE_RATE_URL);
	                    
	                    //we don't request additional motion events
	                    return false;
	        			
        			default:
        				throw new Exception("Index not setup here: " + index);
        		}
        	}
        }
        
        //no action was taken here and we may need additional events
        return true;
    }
    
    @Override
    public void update() throws Exception
    {
        //if not displaying the menu, track timer
        if (!hasDisplay())
        {
            //if time has passed display menu
            if (System.currentTimeMillis() - time >= DELAY_MENU_DISPLAY)
            {
            	//display the menu
            	setDisplay(true);

                //anything else to do here
            }
        }
    }
    
    @Override
    public void render(final Canvas canvas) throws Exception
    {
        if (hasDisplay())
        {
            //only darken the background when the menu is displayed
            ScreenManager.darkenBackground(canvas);
            
            //if message exists, draw the text
            if (paint != null)
                canvas.drawText(this.message, messageX, messageY, paint);
        
            //render the buttons
            for (int index = 0; index < buttons.size(); index++)
            {
            	buttons.get(index).render(canvas, screen.getPaint());
            }
        }
    }
    
    @Override
    public void dispose()
    {
        if (paint != null)
        	paint = null;
        
        if (buttons != null)
        {
	        for (int index = 0; index < buttons.size(); index++)
	        {
	        	if (buttons.get(index) != null)
	        	{
	        		buttons.get(index).dispose();
	        		buttons.setValueAt(index, null);
	        	}
	        }
	        
	        buttons.clear();
	        buttons = null;
        }
    }
}