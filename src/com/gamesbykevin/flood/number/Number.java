package com.gamesbykevin.flood.number;

import com.gamesbykevin.androidframework.base.Entity;
import com.gamesbykevin.androidframework.anim.Animation;
import com.gamesbykevin.androidframework.resources.Images;
import com.gamesbykevin.flood.assets.Assets;
import com.gamesbykevin.flood.game.Game;

import android.graphics.Canvas;

/**
 * This class will render a number using a specific sprite sheet
 */
public class Number extends Entity 
{
	//the current number
	private int number = 0;
	
	//the dimensions of each number animation
	private static final int NUMBER_ANIMATION_WIDTH = 55;
	private static final int NUMBER_ANIMATION_HEIGHT = 78;
	
	//the dimensions of each number render
	private static final int NUMBER_RENDER_WIDTH = 45;
	private static final int NUMBER_RENDER_HEIGHT = 65;
	
	private enum Key
	{
		Zero(0), One(1), Two(2), Three(3), Four(4), 
		Five(5), Six(6), Seven(7), Eight(8), Nine(9);
		
		private int x, y = 0;
		
		private Key(final int col)
		{
			x = col * NUMBER_ANIMATION_WIDTH;
		}
	}
	
	//our array object for each digit in our score
	private Digit[] numbers;

	/**
	 * Default constructor
	 */
	public Number()
	{
		//set the dimensions
		setWidth(NUMBER_ANIMATION_WIDTH);
		setHeight(NUMBER_ANIMATION_HEIGHT);
		
		//add all number animations
		for (Key key : Key.values())
		{
			getSpritesheet().add(
				key, 
				new Animation(
					Images.getImage(Assets.ImageGameKey.Numbers), 
					key.x, 
					key.y, 
					NUMBER_ANIMATION_WIDTH, 
					NUMBER_ANIMATION_HEIGHT
				)
			);
		}
		
		//set default animation
		getSpritesheet().setKey(Key.Zero);
		
		//set a default
		setNumber(0, Game.ATTEMPT_X, Game.ATTEMPT_Y);
	}
	
	/**
	 * Assign the number
	 * @param number The desired number
	 * @param x The starting x-coordinate
	 * @param y The y-coordinate
	 */
	public void setNumber(final int number, int x, final int y)
	{
		//set the dimensions
		super.setWidth(NUMBER_RENDER_WIDTH);
		super.setHeight(NUMBER_RENDER_HEIGHT);
		
		//assign the score
		this.number = number;
		
    	//get the score and convert to string
    	String tmpNumber = String.valueOf(number);
    	
		//update the digits
    	if (this.numbers == null || this.numbers != null && this.numbers.length != tmpNumber.length())
    		this.numbers = new Digit[tmpNumber.length()];
    	
    	//assign the y-coordinate
    	setY(y);
    	
    	//index
    	int index = 0;
    	
    	//assign the animations for each character
    	for (char test : tmpNumber.toCharArray())
    	{
    		//store the number key
    		Key tmp = null;
    		
    		//identify which animation
    		switch (test)
    		{
	    		case '0':
    			default:
    				tmp = Key.Zero;
	    			break;
	    			
	    		case '1':
	    			tmp = Key.One;
	    			break;
	    			
	    		case '2':
	    			tmp = Key.Two;
	    			break;
	    			
	    		case '3':
	    			tmp = Key.Three;
	    			break;
	    			
	    		case '4':
	    			tmp = Key.Four;
	    			break;
	    			
	    		case '5':
	    			tmp = Key.Five;
	    			break;
	    			
	    		case '6':
	    			tmp = Key.Six;
	    			break;
	    			
	    		case '7':
	    			tmp = Key.Seven;
	    			break;
	    			
	    		case '8':
	    			tmp = Key.Eight;
	    			break;
	    			
	    		case '9':
	    			tmp = Key.Nine;
	    			break;
    		}
    		
    		if (this.numbers[index] == null)
    		{
    			this.numbers[index] = new Digit(x, tmp);
    		}
    		else
    		{
    			//update
    			this.numbers[index].x = x;
    			this.numbers[index].key = tmp;
    		}
    		
    		//change index
    		index++;
    		
    		//adjust x-coordinate
    		x += super.getWidth();
    	}
	}
	
	/**
	 * Get the current number
	 * @return the current number
	 */
	public int getNumber()
	{
		return this.number;
	}
	
    /**
     * Render the assigned number
     * @param canvas
     */
    public void render(final Canvas canvas) throws Exception
    {
		//set the dimensions
		super.setWidth(NUMBER_RENDER_WIDTH);
		super.setHeight(NUMBER_RENDER_HEIGHT);
		
    	for (Digit digit : numbers)
    	{
    		//assign x-coordinate location
    		setX(digit.x);
    		
    		//assign animation
    		getSpritesheet().setKey(digit.key);
    		
    		//render animation
    		super.render(canvas);
    	}
    }
    
    /**
     * This class will keep track of each digit in our score
     */
    private class Digit
    {
    	protected int x;
    	protected Key key;
    	
    	private Digit(final int x, final Key key)
    	{
    		this.x = x;
    		this.key = key;
    	}
    }
}