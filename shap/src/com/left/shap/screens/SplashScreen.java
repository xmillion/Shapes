package com.left.shap.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.left.shap.ShapeGame;
import com.left.shap.ShapeGame.Screens;
import com.left.shap.screens.AbstractMenuScreen;
import com.left.shap.util.AnimatedImage;
import com.left.shap.util.AnimationDrawable;
import com.left.shap.util.Res;

/**
 * Screen showing logos and stuff
 */
public class SplashScreen extends AbstractMenuScreen {
	private Texture texture;
	private Image logo;
	private AnimatedImage arrows;

	public SplashScreen(ShapeGame game) {
		super(game);
	}
	
	@Override
	protected boolean hasDynamicBackground() {
		return false;
	}

	@Override
	public void show() {
		super.show();
		int centerX = Gdx.graphics.getWidth() / 2;
		int centerY = Gdx.graphics.getHeight() / 2;

		texture = new Texture(Res.SPLASH);
		logo = new Image(
				new TextureRegionDrawable(new TextureRegion(texture, 0, 31, 116, 22)),
				Scaling.none, Align.center);
		logo.setPosition(centerX - logo.getWidth() / 2, centerY);

		TextureRegion[] arrowFrames = new TextureRegion[96];
		for(int i = 0; i < 96; i++) {
			arrowFrames[i] = new TextureRegion(texture, i, 0, 128, 31);
		}
		Animation anim = new Animation(0.025f, arrowFrames);
		anim.setPlayMode(Animation.LOOP);
		arrows = new AnimatedImage(new AnimationDrawable(anim), Scaling.none, Align.center);
		arrows.setPosition(centerX - arrows.getWidth() / 2, centerY - arrows.getHeight());
		
		stage.addActor(logo);
		stage.addActor(arrows);
		stage.getRoot().getColor().a = 0f;
		stage.getRoot().addAction(sequence(delay(0.25f), fadeIn(0.25f), delay(1.0f), fadeOut(0.25f),
				run(new Runnable() {
					public void run() {
						game.setNextScreen(Screens.MAINMENU);
					}
				})));
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		int centerX = (int) (width * ShapeGame.getUIScaling() / 2);
		int centerY = (int) (height * ShapeGame.getUIScaling() / 2);
		logo.setPosition(centerX - logo.getWidth() / 2, centerY);
		arrows.setPosition(centerX - arrows.getWidth() / 2, centerY - arrows.getHeight());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		texture.dispose();
	}
}
