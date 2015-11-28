package com.gamesbykevin.flood.game.controller;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.gamesbykevin.androidframework.resources.Disposable;

/**
 * Each controller needs to have these methods
 * @author GOD
 */
public interface IController extends Disposable
{
    /**
     * Update logic when motion event occurs
     * @param event Motion Event
     * @param x x-coordinate
     * @param y y-coordinate
     * @throws Exception
     */
    public void update(final MotionEvent event, final float x, final float y) throws Exception;
    
    /**
     * Logic to update at runtime, separate from the motion event
     * @throws Exception
     */
    public void update() throws Exception;
    
    /**
     * Render our controller
     * @param canvas Object to write pixels to
     * @throws Exception
     */
    public void render(final Canvas canvas) throws Exception;
    
    /**
     * Reset the controller
     */
    public void reset();
}