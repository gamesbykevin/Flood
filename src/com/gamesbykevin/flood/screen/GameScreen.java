package com.gamesbykevin.flood.screen;

import android.graphics.Canvas;
import com.gamesbykevin.androidframework.resources.Disposable;
import com.gamesbykevin.androidframework.screen.Screen;
import com.gamesbykevin.flood.game.Game;
import com.gamesbykevin.flood.panel.GamePanel;

/**
 * The game screen that contains the game
 * @author GOD
 */
public class GameScreen implements Screen, Disposable
{
    //our object containing the main game functionality
    private Game game;
    
    //our main screen reference
    private final ScreenManager screen;
    
    public GameScreen(final ScreenManager screen)
    {
        this.screen = screen;
    }
    
    protected Game getGame()
    {
        return this.game;
    }
    
    /**
     * Create game object
     * @throws Exception
     */
    public void createGame() throws Exception
    {
        if (getGame() == null)
            this.game = new Game(screen);
        
        //reset the game
        getGame().reset();
        
        //reset the level select
        getGame().getLevelSelect().reset();
        
        //the description for the level select
        final String description;
        
        //determine the description shown
        switch (screen.getScreenOptions().getIndex(OptionsScreen.INDEX_BUTTON_COLORS))
        {
        	case 0:
        	default:
        		description = "6 Colors - ";
        		break;
        		
        	case 1:
        		description = "3 Colors - ";
        		break;
        		
        	case 2:
        		description = "4 Colors - ";
        		break;
        		
        	case 3:
        		description = "5 Colors - ";
        		break;
        }
        
        //set the level description
        getGame().getLevelSelect().setDescription(description, (GamePanel.WIDTH / 2) - 120, GamePanel.HEIGHT - 25);
    }
    
    /**
     * Reset any necessary screen elements here
     */
    @Override
    public void reset()
    {
        //anything need to be reset here
    }
    
    @Override
    public boolean update(final int action, final float x, final float y) throws Exception
    {
        if (getGame() != null)
            getGame().update(action, x, y);
        
        return true;
    }
    
    @Override
    public void update() throws Exception
    {
        if (getGame() != null)
            getGame().update();
    }
    
    @Override
    public void render(final Canvas canvas) throws Exception
    {
        //render game if exists
        if (getGame() != null)
            getGame().render(canvas);
    }
    
    @Override
    public void dispose()
    {
        if (game != null)
        {
            game.dispose();
            game = null;
        }
    }
}