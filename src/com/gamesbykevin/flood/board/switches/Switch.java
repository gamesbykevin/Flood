package com.gamesbykevin.flood.board.switches;

import com.gamesbykevin.androidframework.anim.Animation;
import com.gamesbykevin.androidframework.awt.Button;
import com.gamesbykevin.androidframework.base.Entity;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.flood.assets.Assets;
import com.gamesbykevin.flood.board.Board.Colors;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * A switch is a button displayed that can change the colors in the board
 * @author GOD
 */
public final class Switch extends Button 
{
	//the color of the button
	private final Colors color;
	
	//did we click this button
	private boolean clicked = false;
	
	/**
	 * The dimension of each animation on our sprite sheet
	 */
	private static final int ANIMATION_DIMENSION = 88;
	
	protected Switch(final Colors color, final Bitmap image)
	{
		super(image);
		
		//assign the color of the button
		this.color = color;
		
		//setup animations
		setupAnimation(this, getColor());
	}
	
	/**
	 * Flag the switch clicked
	 * @param clicked true = yes, false = no
	 */
	protected void setClicked(final boolean clicked)
	{
		this.clicked = clicked;
	}
	
	/**
	 * Is the button clicked?
	 * @return true = yes, false = no
	 */
	protected boolean isClicked()
	{
		return this.clicked;
	}
	
	/**
	 * Get the color
	 * @return The color assigned to the button
	 */
	protected Colors getColor()
	{
		return this.color;
	}
	
	/**
	 * Setup the animations for the specified entity
	 * @param entity The object we want to setup animations for
	 * @param color The default color animation
	 */
	public static void setupAnimation(final Entity entity, final Colors color)
	{
		final int rows = 2;
		final int cols = 3;
		
		int index = 0;
		
		//setup animation
		for (int row = 0; row < rows; row++)
		{
			for (int col = 0; col < cols; col++)
			{
				int x = col * ANIMATION_DIMENSION;
				int y = row * ANIMATION_DIMENSION;
				int d = ANIMATION_DIMENSION;
				
				//create new animation
				Animation animation = new Animation(Images.getImage(Assets.ImageGameKey.Colors), x, y, d, d);
				
				//add to sprite sheet
				entity.getSpritesheet().add(Colors.values()[index], animation);
				
				//increase the index
				index++;
			}
		}
		
		//setup animations for the flooded
		for (int row = 0; row < rows; row++)
		{
			for (int col = 0; col < cols; col++)
			{
				int x = col * ANIMATION_DIMENSION;
				int y = row * ANIMATION_DIMENSION;
				int d = ANIMATION_DIMENSION;
				
				//create new animation
				Animation animation = new Animation(Images.getImage(Assets.ImageGameKey.ColorsFlooded), x, y, d, d);
				
				//add to sprite sheet
				entity.getSpritesheet().add(Colors.values()[index], animation);
				
				//increase the index
				index++;
			}
		}
		
		//set the default animation if possible
		if (color != null)
			entity.getSpritesheet().setKey(color);
	}
	
	@Override
	public void render(final Canvas canvas) throws Exception
	{
		//don't continue if not visible
		if (!super.isVisible())
			return;
		
		super.render(canvas, getSpritesheet().get().getImage());
	}
}