package com.left.shap.model;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.left.shap.util.Utils;

public class ResetPositionAction extends MoveToAction {
	
	@Override
	public void restart() {
		super.restart();
		super.setX(Utils.random.nextFloat() * Gdx.graphics.getWidth() - 32);
	}
	
	public static MoveToAction resetAction(float y) {
		MoveToAction action = action(ResetPositionAction.class);
		action.setPosition(Utils.random.nextFloat() * Gdx.graphics.getWidth() - 32, y);
		action.setDuration(0);
		action.setInterpolation(null);
		return action;
	}
}
