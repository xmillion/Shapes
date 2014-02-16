package com.left.shap.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Basic implementation of button listeners.
 * If the user touched the button and dragged it away before releasing touch, it won't count as pressed.
 */
public abstract class DefaultButtonListener extends ClickListener {
	private boolean wasTouchedDown = false;
	// Feature request: hotkey. Check if hotkey is pressed, then call pressed()
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		wasTouchedDown = true;
		return true;
	}
	
	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		if(wasTouchedDown) {
			pressed(event, x, y, pointer, button);
		}
	}
	
	@Override
	public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		wasTouchedDown = false;
	}
	
	public abstract void pressed(InputEvent event, float x, float y, int pointer, int button);
}
