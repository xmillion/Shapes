package com.left.shap.util;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;

/**
 * Source: Stack Overflow
 * http://stackoverflow.com/questions/16059578/libgdx-is-there-an-actor-that-is-animated
 */
public class AnimatedImage extends Image {
	private final AnimationDrawable drawable;

	public AnimatedImage(AnimationDrawable drawable) {
		super(drawable);
		this.drawable = drawable;
	}

	public AnimatedImage(AnimationDrawable drawable, Scaling scaling, int align) {
		super(drawable, scaling, align);
		this.drawable = drawable;
	}
	
	@Override
	public void act(float delta) {
		drawable.act(delta);
		super.act(delta);
	}
}