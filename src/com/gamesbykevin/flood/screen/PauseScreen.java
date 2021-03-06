package com.gamesbykevin.flood.screen;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.gamesbykevin.androidframework.resources.Disposable;
import com.gamesbykevin.androidframework.screen.Screen;
import com.gamesbykevin.flood.panel.GamePanel;

/**
 * The pause screen
 * @author ABRAHAM
 */
public class PauseScreen implements Screen, Disposable
{
    /**
     * Custom message displayed on screen
     */
    private static final String MESSAGE = "Paused";
    
    //the dimensions of the text message above
    private final int pixelW, pixelH;
    
    //our main screen reference
    private final ScreenManager screen;
    
    //object to paint background
    private Paint paint;
    
    //store the previous state
    private ScreenManager.State previous;
    
    public PauseScreen(final ScreenManager screen)
    {
        //store our parent reference
        this.screen = screen;
        
        //create paint text object
        this.paint = new Paint();
        this.paint.setColor(Color.WHITE);
        this.paint.setTextSize(96f);
        
        //create temporary rectangle
        Rect tmp = new Rect();
        
        //get the rectangle around the message
        paint.getTextBounds(MESSAGE, 0, MESSAGE.length(), tmp);
        
        //store the dimensions
        pixelW = tmp.width();
        pixelH = tmp.height();
    }
    
    /**
     * Set the previous state.<br>
     * We need this, so when un-pause we know where to go back
     * @param previous The previous state, can't be set to paused
     */
    public void setStatePrevious(final ScreenManager.State previous)
    {
        //only store if not paused
        if (previous != ScreenManager.State.Paused)
            this.previous = previous;
    }
    
    /**
     * Get the previous state
     * @return The previous state before the game was paused
     */
    public ScreenManager.State getStatePrevious()
    {
        return this.previous;
    }
    
    /**
     * Reset any necessary screen elements here
     */
    @Override
    public void reset()
    {
        //do we need anything here
    }
    
    @Override
    public boolean update(final int action, final float x, final float y) throws Exception
    {
        if (action == MotionEvent.ACTION_UP)
        {
            //return to the previous state
            screen.setState(previous);
            
            //no need to return additional events
            return false;
        }
        
        //return additional motion events
        return true;
    }
    
    @Override
    public void update() throws Exception
    {
        //nothing needed to update here
    }
    
    @Override
    public void render(final Canvas canvas) throws Exception
    {
        if (paint != null)
        {
            //calculate middle
            final int x = (GamePanel.WIDTH / 2) - (pixelW / 2);
            final int y = (GamePanel.HEIGHT / 2) - (pixelH / 2);
             
            //draw text
            canvas.drawText(MESSAGE, x, y, paint);
        }
    }
    
    @Override
    public void dispose()
    {
        if (paint != null)
            paint = null;
    }
}